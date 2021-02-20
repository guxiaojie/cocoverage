
#!/bin/sh

uploadCustomizedFolder() {
    aws s3 cp ${CUSTOMIZEDFOLDER} s3://file.coveragecollector/customized/${DRONE_REPO}/${DRONE_COMMIT} --recursive
}

if [[ -z "$CUSTOMIZEDFOLDER" ]];then
   echo "GIVEN CUSTOMIZED FOLDER NULL"
else
   echo "CUSTOMIZED FOLDER NOT NULL"
   uploadCustomizedFolder
fi

dir=`dirname $0`
$dir/coverage.sh
