
FROM ubuntu:21.04

RUN apt-get update && apt-get install -y wget curl sudo git unzip
RUN wget https://packages.microsoft.com/config/ubuntu/20.10/packages-microsoft-prod.deb -O packages-microsoft-prod.deb
RUN dpkg -i packages-microsoft-prod.deb
RUN apt-get update && apt-get install -y apt-transport-https
RUN apt-get update && apt-get install -y dotnet-sdk-5.0

RUN dotnet new console -o generator1 && cd generator1 && dotnet add package ReportGenerator --version 4.8.1

RUN apt-get update && sudo DEBIAN_FRONTEND=noninteractive  apt-get install -y awscli
RUN aws --version

RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app

ADD docker_plugin/upload.sh /usr/src/app
ADD docker_plugin/coverage.sh /usr/src/app
ADD docker_plugin/drone-github-comment /bin/

ENTRYPOINT /usr/src/app/upload.sh
