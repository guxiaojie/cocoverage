
local ccDeploy = import "cc-deployment.jsonnet";
local ccSvc = import "cc-svc.jsonnet";

function(
    namespace = "backend",
    version = "latest",
    mysql_host,
    mysql_password,
    port) {
  "cc-deployment.json": ccDeploy.create(namespace, version, mysql_host, mysql_password),
  "cc-svc.json": ccSvc.create(namespace, port)
}
