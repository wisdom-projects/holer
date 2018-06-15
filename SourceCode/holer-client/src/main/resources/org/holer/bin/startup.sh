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
# Start script for the Holer Client
# -----------------------------------------------------------------------------

cd `dirname $0`
cd ..

HOLER_HOME=`pwd`
HOLER_CONF_DIR=$HOLER_HOME/conf
HOLER_LOG_DIR=$HOLER_HOME/logs
HOLER_LIB_DIR=$HOLER_HOME/lib
HOLER_LOG=$HOLER_LOG_DIR/holer-script.log
HOLER_LOG_GC=$HOLER_LOG_DIR/holer-gc.log
HOLER_LIB_JARS=`ls $HOLER_LIB_DIR|grep .jar|awk '{print "'$HOLER_LIB_DIR'/"$0}'| xargs | sed "s/ /:/g"`

HOLER_ERR=1
HOLER_OK=0

HOLER_MAIN=org.holer.client.HolerClientContainer
HOLER_ARGS=" -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true "
HOLER_PID=`ps -ef | grep -v grep | grep "$HOLER_CONF_DIR" |awk '{print $2}'`
if [ -n "$HOLER_PID" ]; then
    echo "ERROR: already started."
    echo "Holer client PID <$HOLER_PID>"
    exit $HOLER_ERR
fi

if [ ! -d $HOLER_LOG_DIR ]; then
    mkdir $HOLER_LOG_DIR
fi

# Check if Java is correctly installed and set
java -version >> $HOLER_LOG 2>&1
if [ $? -ne 0 ]; then
    echo "Please install Java 1.7 or higher and make sure the Java is set correctly."
    echo "You can execute command [ java -version ] to check if Java is correctly installed and set."
    exit $HOLER_ERR
fi

if [ "$1" = "debug" ]; then
    HOLER_ARGS=$HOLER_ARGS" -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=9999,server=y,suspend=n "
fi

if [ "$1" = "jmx" ]; then
    HOLER_ARGS=$HOLER_ARGS" -Dcom.sun.management.jmxremote.port=1099 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false "
fi

#HOLER_ARGS=$HOLER_ARGS" -server -Xms5120M -Xmx5120M -Xmn1024M -Xnoclassgc -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:+CMSClassUnloadingEnabled -XX:+CMSParallelRemarkEnabled -XX:CMSInitiatingOccupancyFraction=80 -XX:SoftRefLRUPolicyMSPerMB=0 -XX:+PrintClassHistogram -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintHeapAtGC -Xloggc:$HOLER_LOG_GC "
echo -e "Starting the holer client ...\c"

nohup java -Dapp.home=$HOLER_HOME $HOLER_ARGS -classpath $HOLER_CONF_DIR:$HOLER_LIB_JARS $HOLER_MAIN >> $HOLER_LOG 2>&1 &
sleep 1
echo "Started"

HOLER_PID=`ps -ef | grep java | grep "$HOLER_HOME" | awk '{print $2}'`
echo "Holer client PID <$HOLER_PID>"
