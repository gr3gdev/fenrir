tasks.register("report") {
    group = "benchmark"
    dependsOn(
        "tests:test"
    )
}
