import groovy.json.JsonSlurper
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.time.Duration
import java.time.Instant
import java.util.regex.Pattern

class Framework(
    val service: String,
    val port: Int,
    val startedRegex: String,
    val times: List<Float> = mutableListOf(),
    val requests: Map<String, List<String>> = mutableMapOf(
        "CREATE" to mutableListOf(),
        "UPDATE" to mutableListOf(),
        "FIND_ALL" to mutableListOf(),
        "FIND_BY_ID" to mutableListOf(),
        "DELETE_BY_ID" to mutableListOf(),
    )
) {
    lateinit var imageSize: String
}

class Report {
    val frameworks = listOf(
        Framework("benchmark-spring", 9001, "Started SpringApp in (.*) seconds"),
        Framework("benchmark-quarkus", 9002, "started in (.*)s\\."),
        Framework("benchmark-fenrir", 9003, "started on port [0-9]+ in (.*) seconds")
    )

    init {
        Files.writeString(
            File(projectDir, "curl-format.txt").toPath(),
            ", \"report\": {\"response_code\": \"%{response_code}\", \"time_namelookup\": \"%{time_namelookup}s\", \"time_connect\": \"%{time_connect}s\", \"time_appconnect\": \"%{time_appconnect}s\", \"time_pretransfer\": \"%{time_pretransfer}s\", \"time_redirect\": \"%{time_redirect}s\", \"time_starttransfer\": \"%{time_starttransfer}s\", \"time_total\": \"%{time_total}s\"}"
        )
    }
}

fun sleep(seconds: Double) {
    exec {
        executable("sleep")
        args(seconds)
    }
}

fun startDocker() {
    logger.info("[benchmark-report] start docker images")
    exec {
        executable("docker-compose")
        args("up", "-d")
    }
}

fun stopDocker() {
    logger.info("[benchmark-report] stop and remove docker images")
    exec {
        executable("docker-compose")
        args("rm", "-fs")
    }
}

fun measureDockerImagesSize(report: Report) {
    logger.info("[benchmark-report] measure docker images size")
    report.frameworks.forEach {
        ByteArrayOutputStream().use { out ->
            exec {
                executable("docker")
                args("image", "ls", "--filter", "reference=gr3gdev/${it.service}", "--format", "json")
                standardOutput = out
            }
            val json = JsonSlurper().parseText(out.toString(StandardCharsets.UTF_8)) as Map<*, *>
            it.imageSize = json["Size"].toString()
        }
    }
}

fun measureStartedTimes(index: Int, report: Report) {
    logger.info("[benchmark-report] ($index) measure started times")
    report.frameworks.forEach {
        val service = it.service
        val regex = Pattern.compile(it.startedRegex)
        var timeFound = false
        val start = Instant.now()
        while (!timeFound) {
            if (Duration.between(start, Instant.now()).toMillis() > 30000) {
                throw GradleException("Unable to find started time in logs of $service")
            }
            ByteArrayOutputStream().use { out ->
                exec {
                    executable("docker-compose")
                    args("logs", service)
                    standardOutput = out
                }
                val logs = out.toString(StandardCharsets.UTF_8)
                val matcher = regex.matcher(logs)
                timeFound = matcher.find()
                if (timeFound) {
                    val time = matcher.group(1)
                    logger.info("[benchmark-report] ($index) [$service] started time : $time seconds")
                    it.times.addLast(time.toFloat())
                }
                sleep(0.2)
            }
        }
    }
}

fun curl(args: List<String>): String {
    logger.lifecycle("[benchmark-report] execute : curl ${args.joinToString(" ")}")
    ByteArrayOutputStream().use { out ->
        exec {
            executable("curl")
            args(args)
            standardOutput = out
        }
        return "{\"response\": ${out.toString(StandardCharsets.UTF_8)}}"
    }
}

fun executeRequests(report: Report) {
    sleep(1.0)
    logger.info("[benchmark-report] execute requests")
    report.frameworks.forEach {
        val port = it.port
        val paths = mapOf(
            "country" to mapOf(
                "POST" to "{\"id\":1,\"name\":\"England_\"}",
                "PUT" to "{\"id\":1,\"name\":\"England\"}"
            ),
            "city" to mapOf(
                "POST" to "{\"id\":1,\"name\":\"London\"}",
                "PUT" to "{\"id\":1,\"name\":\"London\",\"country\":{\"id\":1,\"name\":\"England\"}}"
            ),
            "address" to mapOf(
                "POST" to "{\"id\":1,\"name\":\"Baker Street\"}",
                "PUT" to "{\"id\":1,\"name\":\"Baker Street\",\"city\":{\"id\":1,\"name\":\"London\",\"country\":{\"id\":1,\"name\":\"England\"}}}"
            ),
            "person" to mapOf(
                "POST" to "{\"id\":1,\"firstName\":\"Tim\",\"lastName\":\"Shoes\"}",
                "PUT" to "{\"id\":1,\"firstName\":\"Tim\",\"lastName\":\"Shoes\",\"addresses\":[{\"id\":1,\"name\":\"Baker Street\",\"city\":{\"id\":1,\"name\":\"London\",\"country\":{\"id\":1,\"name\":\"England\"}}}]}"
            )
        )
        // Call CRUD requests
        paths.forEach { data ->
            val path = data.key
            val url = "http://127.0.0.1:$port/$path/"
            it.requests["CREATE"]?.addLast(
                curl(
                    listOf(
                        "-w",
                        "@curl-format.txt",
                        "-s",
                        "-X",
                        "POST",
                        url,
                        "-H",
                        "\"Content-type: application/json\"",
                        "--data",
                        "'${data.value["POST"]}'"
                    )
                )
            )
            it.requests["UPDATE"]?.addLast(
                curl(
                    listOf(
                        "-w",
                        "@curl-format.txt",
                        "-s",
                        "-X",
                        "PUT",
                        url,
                        "-H",
                        "\"Content-type: application/json\"",
                        "--data",
                        "'${data.value["PUT"]}'"
                    )
                )
            )
            it.requests["FIND_ALL"]?.addLast(
                curl(
                    listOf(
                        "-w",
                        "@curl-format.txt",
                        url
                    )
                )
            )
            it.requests["FIND_BY_ID"]?.addLast(
                curl(
                    listOf(
                        "-w",
                        "@curl-format.txt",
                        "${url}1"
                    )
                )
            )
        }
        paths.forEach { data ->
            val path = data.key
            val url = "http://127.0.0.1:$port/$path/"
            it.requests["DELETE_BY_ID"]?.addLast(
                curl(
                    listOf(
                        "-w",
                        "@curl-format.txt",
                        "-X",
                        "DELETE",
                        "${url}1"
                    )
                )
            )
        }
    }
}

tasks.register("executeRequest") {
    group = "benchmark"
    val report = Report()
    doLast {
        executeRequests(report)
        report.frameworks.forEach {
            it.requests.forEach { (method, log) ->
                logger.lifecycle("[benchmark-report] ${it.service} request $method : $log")
            }
        }
    }
}

tasks.register("report") {
    group = "benchmark"
    dependsOn(
        "benchmark-spring:bootBuildImage",
        "benchmark-quarkus:imageBuild",
        "benchmark-fenrir:buildDockerImage"
    )
    val report = Report()
    doLast {
        measureDockerImagesSize(report)
        for (i in 1..10) {
            logger.lifecycle("[benchmark-report] BENCH $i")
            startDocker()
            measureStartedTimes(i, report)
            executeRequests(report)
            stopDocker()
        }
        report.frameworks.forEach {
            logger.lifecycle("[benchmark-report] ${it.service} docker image size : ${it.imageSize}")
            logger.lifecycle("[benchmark-report] ${it.service} started time : ${it.times.min()} - ${it.times.max()} seconds")
            logger.lifecycle("[benchmark-report] ${it.service} average started time : ${it.times.average()} seconds")
            it.requests.forEach { (method, log) ->
                logger.lifecycle("[benchmark-report] ${it.service} request $method : $log")
            }
        }
    }
}
