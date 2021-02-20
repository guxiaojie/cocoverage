

local test(branch, name, image, when) = {
    name: name,
    image: image,
    pull: "always",
    commands: [
       "export MYSQL_HOST=mysql:3306",
       "export MYSQL_DB=cc",
       "export MYSQL_USERNAME=root",
       "export MYSQL_PASSWORD=123456",
       "./mvnw clean clover:setup test clover:aggregate clover:clover site",
       "head -20 target/site/clover/clover.xml",
    ],
    depends_on: [
       "mysql-check",
    ],
    when: when
};

local coverage(name, image, when) = {
    name: name,
    image: image,
    environment:{
      COVERAGE_COLLECTOR_SVC_ADDR: {
        from_secret: "COVERAGE_COLLECTOR_SVC_ADDR",
      },
      GITHUB_ACCESS_TOKEN: {
        from_secret: "GITHUB_ACCESS_TOKEN",
      },
      COVERAGE_XML_PATH: "target/site/clover/clover.xml",
      CUSTOMIZEDFOLDER: "target/site"
    },
    depends_on: [
      "test",
    ],
    when: when
};

local build(branch, name, image, when) = {
    name: name,
    image: image,
    pull: "always",
    commands: [
        "./mvnw package -DskipTests",
    ],
    // depends_on: [
    //   "test",
    //   "coverage",
    //   "comments",
    // ],
    when: when
};

local publish_plugin(branch, name, image, tag, when) = {
    name: name,
    image: image,
    pull: "if-not-exists",
    settings:{
        username:{
          from_secret: "IHEALTH_DOCKER_USERNAME",
        },
        password:{
          from_secret: "IHEALTH_DOCKER_PASSWORD",
        },
        repo: "ihealthlabs/coverage_plugin",
        tags: tag,
        dockerfile: "./docker_plugin/Dockerfile"
    },
    when: when
};

local publish(branch, name, image, tag, when) = {
    name: name,
    image: image,
    pull: "if-not-exists",
    settings:{
        username:{
            from_secret: "IHEALTH_DOCKER_USERNAME",
        },
        password:{
            from_secret: "IHEALTH_DOCKER_PASSWORD",
        },
        repo: "ihealthlabs/coverage_collector",
        tags: tag,
        dockerfile: "./Dockerfile"
    },
    depends_on: [
      "build",
    ],
    when: when
};

local deploy(branch, name, image, tag, when) = {
    name: name,
    image: image,
    pull: "always",
    commands:[
        "aws eks update-kubeconfig --name $CLUSTER_NAME --role-arn $AWS_ROLE",
        "kubectl get all --namespace=$NAMESPACE",
        "sh -x -v ./k8s/deploy.sh " + tag,
        "kubectl get all --namespace=$NAMESPACE"
    ],
    environment:{
        CLUSTER_NAME:{
            from_secret: "CLUSTER_NAME",
        },
        NAMESPACE:{
            from_secret: "NAMESPACE",
        },
        AWS_ROLE:{
            from_secret: "AWS_ROLE",
        },
        IHEALTH_AWS_ACCESS_KEY_ID:{
            from_secret: "IHEALTH_AWS_ACCESS_KEY_ID",
        },
        IHEALTH_AWS_ACCESS_SECRET:{
            from_secret: "IHEALTH_AWS_ACCESS_SECRET",
        },
        AWS_DEFAULT_REGION:{
            from_secret: "AWS_DEFAULT_REGION",
        },
        IHEALTH_DOCKER_USERNAME:{
            from_secret: "IHEALTH_DOCKER_USERNAME",
        },
        IHEALTH_DOCKER_PASSWORD:{
            from_secret: "IHEALTH_DOCKER_PASSWORD",
        },
        MYSQL_HOST:{
            from_secret: "MYSQL_HOST",
        },
        MYSQL_DB:{
            from_secret: "MYSQL_DB",
        },
        MYSQL_USERNAME:{
            from_secret: "MYSQL_USERNAME",
        },
        MYSQL_PASSWORD:{
            from_secret: "MYSQL_PASSWORD",
        },
        AWS_ENDPOINT_URL:{
            from_secret: "AWS_ENDPOINT_URL",
        },
        AWS_BUCKET:{
            from_secret: "AWS_BUCKET",
        }
    },
    depends_on: [
      "publish",
    ],
    when: when
};

local mysql(name, when) = {
    name: name,
    image: 'mysql:5',
    commands: [
      "sleep 15 ",
      "mysql -u root -h mysql -p123456 --execute='SELECT VERSION();'"
    ],
    when: when
};

local pipeline(branch, instance) = {
    kind: 'pipeline',
    type: 'kubernetes',
    name: branch,
    steps: [
        mysql("mysql-check",
              {instance: instance, event: ["push", "pull_request"]}),
        test(branch, "test", "adoptopenjdk/openjdk8",
             {instance: instance, event: ["push", "pull_request"]}),
        coverage("coverage", "ihealthlabs/coverage_plugin:v1.0.200",
                {instance: instance, event: ["push", "pull_request"]}),
   ],
    services:[
    {
        name: 'mysql',
        pull: 'if-not-exists',
        image: 'mysql:5',
        environment: {
            MYSQL_DATABASE: 'cc',
            MYSQL_ROOT_PASSWORD: '123456',
        },
    }
    ],
    trigger:{
        branch: branch
    },
    image_pull_secrets: ["dockerconfigjson"]
};

local dev_drone = ["dev-drone.ihealth-eng.com"];
local test_drone = ["test-drone.ihealth-eng.com"];
local prod_drone = ["prod-drone.ihealth-eng.com"];

[
    pipeline(branch="main", instance=dev_drone),
    //pipeline(branch="test", instance=test_drone),
    //pipeline(branch="prod", instance=prod_drone)
]
