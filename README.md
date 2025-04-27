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
./gradlew :consumer:app:bootRun --args='--spring.profiles.active=local'
./gradlew :consumer:integration-test:test
```

## Publish Pacts

```shell
docker run --rm -v $(pwd):/tmp/app pactfoundation/pact-cli:latest publish \
/tmp/app/pacts \
--broker-base-url=http://host.docker.internal:9292 \
--consumer-app-version=1.0.0-SNAPSHOT
```

## Verify Pacts

```shell
docker run --rm \
    -p 8083:8083 \
    -e PORT="8083" \
    mccutchen/go-httpbin:latest
./gradlew :producer:integration-test:test
```

## Run pacts

```shell
docker run --rm -p 8082:8082 pactfoundation/pact-cli:latest stub-service \
http://host.docker.internal:9292/pacts/provider/Bar/consumer/Foo/version/1.0.0-SNAPSHOT \
--host 0.0.0.0 \
--port 8082
```

## Can I Deploy?

https://docs.pact.io/pact_broker/recording_deployments_and_releases

### Create Environments

```shell
docker run --rm pactfoundation/pact-cli:latest pact-broker \
create-environment --name uat --display-name UAT --no-production \
--broker-base-url=http://host.docker.internal:9292
```

### Deploy Consumer as Baseline

```shell
docker run --rm pactfoundation/pact-cli:latest pact-broker \
record-deployment --pacticipant Foo --version 1.0.0-SNAPSHOT --environment uat \
--broker-base-url=http://host.docker.internal:9292
```

### Can I deploy a new Consumer?

```shell
docker run --rm -v $(pwd):/tmp/app pactfoundation/pact-cli:latest publish \
/tmp/app/pacts \
--broker-base-url=http://host.docker.internal:9292 \
--consumer-app-version=1.0.0-SNAPSHOT
docker run --rm pactfoundation/pact-cli:latest pact-broker \
can-i-deploy --pacticipant Foo --version 1.1.0-SNAPSHOT --to-environment uat \
--broker-base-url=http://host.docker.internal:9292
```

Failed!

### Deploy Producer to satisfy contract in Environment

```shell
docker run --rm pactfoundation/pact-cli:latest pact-broker \
record-deployment --pacticipant Bar --version 0.0.1-SNAPSHOT --environment uat \
--broker-base-url=http://host.docker.internal:9292
```

### Can I deploy a new Consumer now?

```shell
docker run --rm pactfoundation/pact-cli:latest pact-broker \
can-i-deploy --pacticipant Foo --version 1.1.0-SNAPSHOT --to-environment uat \
--broker-base-url=http://host.docker.internal:9292
```

Success!
