# Benchmark

Compare Fenrir with :

- Spring
- Quarkus

Dependencies :

- JPA
- Lombok
- PostgreSQL database

Database model :

```mermaid
classDiagram
    class Person {
        -Long id
        -String firstName
        -String lastName
        -Set<Address> addresses
    }
    class Address {
        -Long id
        -String name
        -City city
    }
    class City {
        -Long id
        -String name
        -Country country
    }
    class Country {
        -Long id
        -String name
    }
    Person --* Address
    Address *-- City
    City *-- Country
```

## Procedure

Create a project which expose REST API with JPA, build a docker image.

- measure docker image size

Do 10 times with memory limit to 256MB, 512MB :

- Start image, measure start time
- Execute requests, measure response time and compare response (code & body)

```shell
./gradlew report
```

See [report](./report.html)