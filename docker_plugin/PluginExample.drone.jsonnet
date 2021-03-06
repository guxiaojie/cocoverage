


local coverage(name, tag, when) = {
    name: name,
    image: "ihealthlabs/coverage_collector_docker_plugin:v1.0.49",
    settings:{
        repo: "<YOUR REPO>",
        tags:[
            tag
          ],
        username:{
          from_secret: "DOCKER_USERNAME",
        },
        password:{
          from_secret: "DOCKER_PASSWORD",
        }, 
    },
    environment:{
      COVERAGE_COLLECTOR_UPLOAD_URL: {
        from_secret: "COVERAGE_COLLECTOR_UPLOAD_URL",
      },
      PROJECT_NAME: "${DRONE_REPO}",
      BASE_BRANCH: "${DRONE_SOURCE_BRANCH}",
      COMPARING_BRANCH: "${DRONE_TARGET_BRANCH}",
      BASE_COMMIT_ID: "${DRONE_COMMIT}",
      ACTION: "${DRONE_BUILD_EVENT} + ${DRONE_BUILD_ACTION}",
      COVERAGE_XML_PATH: <COVERAGE_XML_PATH>,
      SOURCE_CODE:<SOURCE_CODE>,
      REPORT_PATH: <REPORT_PATH>
    }, 
    when: when
};

local comments(name, message, when) = {
    name: name,
    image: "ihealthlabs/test_image:drone-github-comment-1.0",
    pull: "always",
    environment:{
        PLUGIN_API_KEY: 
        {
            from_secret: "APIKEY"
        },
        REPORT_PATH: "report.txt",
        PLUGIN_MESSAGE: "/drone/src/$REPORT_PATH",//or a text message
    },
    when: when
};

local pipeline(branch, namespace, tag, instance) = {
    kind: 'pipeline',
    type: 'kubernetes',
    name: branch,
    steps: [
        coverage(branch+"-coverage", tag, {instance: instance, event: ["push"]}),
    ],
    trigger:{
        branch: branch
    },
    image_pull_secrets: ["dockerconfigjson"]
};

local pipelineComments(branch, namespace, tag, instance) = {
    kind: 'pipeline',
    type: 'kubernetes',
    name: branch,
    steps: [
        comments(branch+"-comment", tag, {instance: instance, event: ["pull_request"]})
    ],
    trigger:{
        branch: branch
    },
    image_pull_secrets: ["dockerconfigjson"]
};

local dev_drone = [<dev-drone instance>];

[
    // define dev pipeline
    pipeline(branch="dev",
             namespace="<NAMESPACE>",
             tag="${DRONE_BRANCH}-${DRONE_COMMIT:0:4}",
             instance=dev_drone),
]


