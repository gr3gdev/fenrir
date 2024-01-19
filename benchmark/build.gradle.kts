import groovy.json.JsonSlurper
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets
import java.time.Duration
import java.time.Instant
import java.util.regex.Pattern

class Framework(
    val service: String,
    val port: Int,
    val startedRegex: String,
    val times: List<Float> = mutableListOf()
) {
    lateinit var imageSize: String
}

class Report {
    val frameworks = listOf(
        Framework("benchmark-spring", 9001, "Started SpringApp in (.*) seconds"),
        Framework("benchmark-quarkus", 9002, "started in (.*)s\\."),
        Framework("benchmark-fenrir", 9003, "started on port [0-9]+ in (.*) seconds")
    )
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

tasks.register("report") {
    group = "benchmark"
    dependsOn(
        "tests:test"
    )
}
