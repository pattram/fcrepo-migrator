#!/bin/bash

# Ingest one or more projects
# Usage:
#    ./ingest_projects.sh project1 [project2 [project3]]]
#
#    eg:
#    ./ingest_projects.sh rehi 
#    ./ingest_projects.sh rehi polos mws srbas


DEFAULT_TARGET="localhost"
TARGET_USER="padawan"
DEFAULT_SOURCE="gams.uni-graz.at/archive"
SOURCE_USER="yoda"

mkdir -p logs

default_prompt() {
	local question="${1}"
	local default_answer="${2}"
	read -p "${question} [${default_answer}] "
	if [[ -z "${REPLY}" ]]; then
		echo $default_answer
	else
		echo ${REPLY}
	fi
}

prompt_password() {
	echo $1 > /dev/stderr
	stty -echo
	read PASSWORD 
	stty echo
	printf "\n"
	echo ${PASSWORD}
}


if [ "$#" -lt 1 ]; then
    echo "Give at least one project name as argument."
    exit 1
fi


#read -p "Target host (e.g. localhost): "
TARGET_HOST=$(default_prompt "Target host (where to migrate to)" ${DEFAULT_TARGET})
TARGET_USER=$(default_prompt "User name on target host" ${TARGET_USER})
TARGET_PASSWORD=$(prompt_password "Enter password for user '${TARGET_USER}' on host '${TARGET_HOST}'")
echo ""
SOURCE_HOST=$(default_prompt "Source host (where to migrate from) incl. context" ${DEFAULT_SOURCE})
SOURCE_USER=$(default_prompt "User name on source host" ${SOURCE_USER})
SOURCE_PASSWORD=$(prompt_password "Enter password for user '${SOURCE_USER}' on host '${SOURCE_HOST}'")

for project in "$@"; do
	TIMESTAMP=`date +"%F-%H-%M"`
	LOG_FILE=./logs/ingest-${OWNER}-${TARGET_HOST}_${TIMESTAMP}.log

	date | tee -a ${LOG_FILE}

	cmd="java -jar migrator.jar \
	    -o ${project} \
	    -f3 ${SOURCE_HOST} \
	    -u3 ${SOURCE_USER} -p3 ${SOURCE_PASSWORD} \
	    -f4 ${TARGET_HOST} -u4 ${TARGET_USER} -p4 ${TARGET_PASSWORD}"
#	echo $cmd
	$cmd | tee -a ${LOG_FILE}
done

date | tee -a ${LOG_FILE}

