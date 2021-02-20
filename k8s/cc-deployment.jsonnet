{
  create(namespace, version, mysql_host, mysql_password): {
  "apiVersion": "apps/v1",
  "kind": "Deployment",
  "metadata": {
    "name": "coverage-controller",
    "namespace": namespace,
    "labels": {
      "app": "coverage-controller"
    }
  },
  "spec": {
    "replicas": 1,
    "selector": {
      "matchLabels": {
        "app": "coverage-controller"
      }
    },
    "template": {
      "metadata": {
        "labels": {
          "app": "coverage-controller"
        }
      },
      "spec": {
        "containers": [
          {
            "name": "coverage-controller",
            "image": "ihealthlabs/coverage_collector:"+version,
            "imagePullPolicy": "Always",
            "securityContext": {
              "runAsUser": 0
            },
            "env": [
              {
                "name": "MYSQL_HOST",
                "value": mysql_host
              },
              {
               "name": "MYSQL_PASSWORD",
               "value": mysql_password
              }
            ],
            "resources": {
              "limits": {
                "cpu": "0.2",
                "memory": "0.2Gi"
              },
              "requests": {
                "cpu": "0.2",
                "memory": "0.2Gi"
              }
            }
          }
        ]
      }
    }
  }
}
}
