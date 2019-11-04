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
# Holer Shutdown
# -----------------------------------------------------------------------------
cd `dirname $0`/..

HOLER_OK=0
HOLER_ERR=1
HOLER_PID=""

HOLER_HOME=`pwd`
HOLER_LOG_DIR=$HOLER_HOME/logs
HOLER_LOG=$HOLER_LOG_DIR/holer-client.log
HOLER_APP=$HOLER_HOME/holer-client.jar

pid() 
{
    HOLER_PID=`ps -ef | grep -v grep | grep "$HOLER_APP" |awk '{print $2}'`
}

status() 
{
    pid
    if [ -n "$HOLER_PID" ]; then
        echo "Holer client PID <$HOLER_PID> is running."
    else
        echo "Holer client is stopped."
    fi
}

stop() 
{
    if [ ! -d $HOLER_LOG_DIR ]; then
        mkdir -p $HOLER_LOG_DIR
    fi

    pid
    if [ -z "$HOLER_PID" ]; then
        status
        return $HOLER_OK
    fi

    echo -e "Stopping the holer client PID <$HOLER_PID> ...\c"
    kill -9 $HOLER_PID >> $HOLER_LOG 2>&1
    status
}

stop
