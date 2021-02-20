# CoverageCollector

Springboot project to get code coverage according to a coverage report file(*.xml) 

## Build

Build the project with the following command, or run with IntelliJ IDEA

```Shell
./mvnw package -DskipTests
java -jar target/cc-0.0.3-SNAPSHOT.jar
```

Build the Docker image with the following command:

```Shell
docker build -t ihealthlabs/coverage_controller:0.6 .
docker push ihealthlabs/coverage_controller:0.6
```

Using Drone, check jsonnet
```Shell
commands: [
        "./mvnw package -DskipTests",
    ],
```


## Usage

```Shell
docker run -p 8080:8080 -t coverage_controller_container
```

or

```Shell
docker-compose up
```

## Deploy
ask for yml files from Sage

## API
#### get current coverage, return percentage
```Shell
curl -v -X POST \
  -F "projectName=${PROJECT_NAME}" \
  -F "baseBranch=<BASE_BRANCH>" \
  -F "comparingBranch=<COMPARING_BRANCH> " \
  -F "baseCommitId=<BASE_COMMIT_ID>" \
  -F "action=<ACTION>" \
  -F "file=<COVERAGE_RESULT_PATH>" \
  COVERAGE_COLLECTOR_UPLOAD_URL/cc/upload
```
#### retrieve all projects, return list of projects
```Shell
curl COVERAGE_COLLECTOR_UPLOAD_URL/cc/allprojects
```
## How to contribute
1. change code of Spring Boot, submit a PR;
2. once PR is approved, Drone will run by .drone.jsonnet, 
which will build a new jar, publish a new image to Docker Hub, named ihealthlabs/coverage_collector:v1.0.123
and publish a docker plugin
ihealthlabs/coverage_collector_docker_plugin:v1.0.123(as a plugin, we can only rebuild it when API changed);
the last step is deploy to k8s
3. add ihealthlabs/coverage_collector_docker_plugin:v1.0.123 to your project, have a look at
PluginExample.drone.jsonnet

## DB
MySQL Data Source: cc@localhost Schema: cc 
Tables
```Shell
-- auto-generated definition
create table coverage
(
    id               int auto_increment      primary key,
    percentage       float                   not null,
    action           varchar(255) default '' not null,
    base_branch      varchar(255)            not null,
    base_commit_id   varchar(255)            not null,
    comparing_branch varchar(255)            null,
    created_at       datetime(6)             null,
    project_name     varchar(255)            null,
    branch           varchar(255)            null,
    commit_id        varchar(255)            not null,
    report_file      varchar(255)            null
);

-- auto-generated definition
create table project
(
    id         int auto_increment primary key,
    name       varchar(255) not null,
    created_at time         null
);
```


## Contributors
iHealth
