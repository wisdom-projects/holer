#!/bin/bash

# Copyright 2018-present, Yudong (Dom) Wang
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# -----------------------------------------------------------------------------
# Stop script for the Holer Client
# -----------------------------------------------------------------------------

cd `dirname $0`
HOLER_BIN_DIR=`pwd`
cd ..
HOLER_HOME=`pwd`
HOLER_LOG_DIR=$HOLER_HOME/logs
HOLER_ERR=1
HOLER_OK=0

if [ ! -d $HOLER_LOG_DIR ]; then
    mkdir $HOLER_LOG_DIR
fi

HOLER_LOG=$HOLER_LOG_DIR/holer-script.log
HOLER_PID=`ps -ef | grep -v grep | grep "$HOLER_HOME/conf" | awk '{print $2}'` 
echo "Holer client PID <$HOLER_PID>"
if [ -z "$HOLER_PID" ]; then
    echo "ERROR: The holer client does not start."
    exit $HOLER_ERR
fi

echo -e "Stopping the holer client ...\c"
kill $HOLER_PID > $HOLER_LOG 2>&1

HOLER_COUNTER=0
while [ $HOLER_COUNTER -lt 1 ]; do    
    echo -e ".\c"
    sleep 1
    HOLER_COUNTER=1
    HOLER_PID_EXIST=`ps -f -p $HOLER_PID | grep java`
    if [ -n "$HOLER_PID_EXIST" ]; then
        HOLER_COUNTER=0
    fi
done

echo "Stopped"
echo "Holer client PID <$HOLER_PID>"
