{
  create(namespace, port): {
    "apiVersion": "v1",
    "kind": "Service",
    "metadata": {
      "name": "cc-service",
      "namespace": namespace
    },
    "spec": {
      "type": "NodePort",
      "selector": {
        "app": "coverage-controller"
      },
      "ports": [
        {
          "protocol": "TCP",
          "port": 8080,
          "targetPort": 8080,
          "nodePort": port
        }
      ]
    }
  }
}
