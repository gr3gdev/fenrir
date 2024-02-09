tasks.register("report") {
    group = "benchmark"
    dependsOn(
        "tests:clean",
        "tests:test"
    )
}
