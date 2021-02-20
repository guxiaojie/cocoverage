
#!/bin/sh


#uncomment below variables to test locally
#git clone https://<GITHUB_USERNAME>:PSW@github.com/<GITHUB_USERNAME>/test.git
#export SOURCE_CODE=test
#export COVERAGE_XML_PATH=test/coverage/clover.xml
#export PROJECT_NAME=SiteTest
#export BASE_BRANCH=testbranch
#export COMPARING_BRANCH=cmpBranch
#export BASE_COMMIT_ID=comtId
#export ACTION=action
#export REPORT_PATH=report.txt
#export COVERAGE_COLLECTOR_UPLOAD_URL=localhost:8080/cc/upload
#export SITE=target/site
#export DRONE_REPO=sage-gu/SiteTest
#export DRONE_SOURCE_BRANCH=baseBranch
#export DRONE_TARGET_BRANCH=target
#export DRONE_COMMIT=123
#export DRONE_BUILD_EVENT=push
#export DRONE_BUILD_ACTION=pr

export SOURCE_CODE=/drone/src
export REPORT_PATH="report.txt"

dotnet /root/.nuget/packages/reportgenerator/4.8.1/tools/net5.0/ReportGenerator.dll \
 "-reports:${COVERAGE_XML_PATH}" \
 "-targetdir:coveragereport" \
 -reporttypes:Html \
 "-sourcedirs:${SOURCE_CODE}"

t=$(date +"%s")
sudo mv -v coveragereport clover$t
reportFolderName=clover$t
aws s3 cp $reportFolderName s3://file.coveragecollector/coveragereport/${DRONE_REPO}/${DRONE_COMMIT} --recursive

customizedFolder=''

if [[ -z "$CUSTOMIZEDFOLDER" ]];then
   echo "GIVEN CUSTOMIZED FOLDER NULL"
else
   echo "CUSTOMIZED FOLDER NOT NULL"
   customizedFolder=/customized/${DRONE_REPO}/${DRONE_COMMIT}/
fi


curl -v \
   -o ${REPORT_PATH} \
   -F "reportFolderName=/coveragereport/${DRONE_REPO}/${DRONE_COMMIT}/" \
   -F "projectName=${DRONE_REPO}" \
   -F "baseBranch=${DRONE_SOURCE_BRANCH}" \
   -F "comparingBranch=${DRONE_TARGET_BRANCH}" \
   -F "baseCommitId=${DRONE_COMMIT}" \
   -F "action=${DRONE_BUILD_EVENT}/${DRONE_BUILD_ACTION}" \
   -F "file=@${COVERAGE_XML_PATH}" \
   -F "customizedFolder=$customizedFolder" \
   ${COVERAGE_COLLECTOR_SVC_ADDR}/cc/upload

# comment
export PLUGIN_API_KEY=${GITHUB_ACCESS_TOKEN}
export PLUGIN_MESSAGE=${REPORT_PATH}

drone-github-comment
