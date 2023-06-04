#!/bin/bash
# Generate the zip file to be copied over to gams

rm -f migrator.zip

cd target
zip ../migrator.zip -r migrator.jar  lib
cd ..
zip migrator.zip ingest-projects.sh

scp migrator.zip vasold@gams.uni-graz.at:/data/www/cirilo

