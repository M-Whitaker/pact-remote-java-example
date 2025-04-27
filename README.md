# Out of process contract testing

## Run Broker

```shell
docker run --rm \
    -p 9292:9292 \
    -e PACT_BROKER_DATABASE_URL="sqlite:////tmp/pact_broker.sqlte3" \
    pactfoundation/pact-broker
```

## Run contract server

```shell
docker run --rm -p 8081:8081 -v $(pwd):/tmp/app pactfoundation/pact-cli:latest mock-service service \
  --host 0.0.0.0 \
  --port 8081 \
  --consumer Foo \
  --provider Bar \
  --pact-specification-version 2 \
  --pact-dir /tmp/app/pacts \
  --log /tmp/app/bar_mock_service.log \
  --log-level DEBUG
```

## Run out of process tests

```shell
./gradlew bootRun --args='--spring.profiles.active=local'
./gradlew test
```

## Publish Pacts

```shell
docker run --rm -v $(pwd):/tmp/app pactfoundation/pact-cli:latest publish \
/tmp/app/pacts \
--broker-base-url=http://host.docker.internal:9292 \
--consumer-app-version=1.0.0-SNAPSHOT
```

## Run pacts

```shell
docker run --rm -p 8082:8082 pactfoundation/pact-cli:latest stub-service \
http://host.docker.internal:9292/pacts/provider/Bar/consumer/Foo/version/1.0.0-SNAPSHOT \
--host 0.0.0.0 \
--port 8082
```
