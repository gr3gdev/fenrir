#!/bin/bash
test() {
  dir=$1
  port=$2

  curl -f -s "http://localhost:$port/"
  while [ "$?" != "0" ]; do
    sleep 1
  done

  mkdir -p "build/$dir/country" "build/$dir/city" "build/$dir/address" "build/$dir/person"

  curl -w "@curl-format.txt" -s -X POST "http://localhost:$port/country/" -H 'Content-type: application/json' --data '{"id": 1, "name": "England_"}' > "build/$dir/country/create"
  curl -w "@curl-format.txt" -s -X PUT "http://localhost:$port/country/" -H 'Content-type: application/json' --data '{"id": 1, "name": "England"}' > "build/$dir/country/update"
  curl -w "@curl-format.txt" -s "http://localhost:$port/country/" > "build/$dir/country/findAll"
  curl -w "@curl-format.txt" -s "http://localhost:$port/country/1" > "build/$dir/country/findById"

  curl -w "@curl-format.txt" -s -X POST "http://localhost:$port/city/" -H 'Content-type: application/json' --data '{"id": 1, "name": "London"}' > "build/$dir/city/create"
  curl -w "@curl-format.txt" -s -X PUT "http://localhost:$port/city/" -H 'Content-type: application/json' --data '{"id": 1, "name": "London", "country": {"id": 1, "name": "England"}}' > "build/$dir/city/update"
  curl -w "@curl-format.txt" -s "http://localhost:$port/city/" > "build/$dir/city/findAll"
  curl -w "@curl-format.txt" -s "http://localhost:$port/city/1" > "build/$dir/city/findById"

  curl -w "@curl-format.txt" -s -X POST "http://localhost:$port/address/" -H 'Content-type: application/json' --data '{"id": 1, "name": "Baker Street"}' > "build/$dir/address/create"
  curl -w "@curl-format.txt" -s -X PUT "http://localhost:$port/address/" -H 'Content-type: application/json' --data '{"id": 1, "name": "Baker Street", "city": {"id": 1, "name": "London", "country": {"id": 1, "name": "England"}}}' > "build/$dir/address/update"
  curl -w "@curl-format.txt" -s "http://localhost:$port/address/" > "build/$dir/address/findAll"
  curl -w "@curl-format.txt" -s "http://localhost:$port/address/1" > "build/$dir/address/findById"

  curl -w "@curl-format.txt" -s -X POST "http://localhost:$port/person/" -H 'Content-type: application/json' --data '{"id": 1, "firstName": "Tim", "lastName": "Shoes"}' > "build/$dir/person/create"
  curl -w "@curl-format.txt" -s -X PUT "http://localhost:$port/person/" -H 'Content-type: application/json' --data '{"id": 1, "firstName": "Tim", "lastName": "Shoes", "addresses": [{"id": 1, "name": "Baker Street", "city": {"id": 1, "name": "London", "country": {"id": 1, "name": "England"}}}]}' > "build/$dir/person/update"
  curl -w "@curl-format.txt" -s "http://localhost:$port/person/" > "build/$dir/person/findAll"
  curl -w "@curl-format.txt" -s "http://localhost:$port/person/1" > "build/$dir/person/findById"

  curl -w "@curl-format.txt" -s -X DELETE "http://localhost:$port/person/1" > "build/$dir/person/delete"
  curl -w "@curl-format.txt" -s -X DELETE "http://localhost:$port/address/1" > "build/$dir/address/delete"
  curl -w "@curl-format.txt" -s -X DELETE "http://localhost:$port/city/1" > "build/$dir/city/delete"
  curl -w "@curl-format.txt" -s -X DELETE "http://localhost:$port/country/1" > "build/$dir/country/delete"
}

# Build images
./gradlew benchmark-spring:bootBuildImage
./gradlew benchmark-quarkus:imageBuild
./gradlew benchmark-fenrir:buildDockerImage

# Start docker-compose
docker-compose up -d
sleep 5

# Tests
test "benchmark-spring" 9001
test "benchmark-quarkus" 9002
test "benchmark-fenrir" 9003

# Stop docker-compose
docker-compose down
