
# coverage_collector_docker_plugin
Drone plugin to add code coverage 

## Build

Build the Docker plugin with the following command:

```Shell
docker build -t <your docker repo> .
docker push <your docker repo>
```

Or Build the Docker plugin in Docker pipeline:

```Shell
settings:{
  dockerfile: "./docker_plugin/Dockerfile"
}
```

## Usage
run docker locally
```Shell
 docker run --rm \
  -e PROJECT_NAME=<PROJECT_NAME> \
  -e BASE_BRANCH=<BASE_BRANCH> \
  -e COMPARING_BRANCH=<COMPARING_BRANCH> \
  -e BASE_COMMIT_ID=<BASE_COMMIT_ID> \
  -e ACTION=<ACTION> \
  -e FILE=<COVERAGE_RESULT_PATH> \
  <your docker repo>
```

or use it in CI provider - Drone, config .drone.jsonnet.In the pipeline, set steps - environment to
```Shell
environment:{
      COVERAGE_COLLECTOR_UPLOAD_URL: <YOUR HOST>,
      PROJECT_NAME: "${DRONE_REPO}",
      BASE_BRANCH: "${DRONE_SOURCE_BRANCH}",
      COMPARING_BRANCH: "${DRONE_TARGET_BRANCH}",
      BASE_COMMIT_ID: "${DRONE_COMMIT}",
      ACTION: "${DRONE_BUILD_EVENT} + ${DRONE_BUILD_ACTION}",
      FILE: <COVERAGE_RESULT_PATH>,
      REPORT_PATH: <REPORT_PATH>
}, 

```

## Contributors
iHealth
