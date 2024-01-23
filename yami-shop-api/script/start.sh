#!/bin/bash
MAIN_CLASS=yami-shop-api-1.24.0115.jar

_CAD=$(cd $(dirname $0); pwd)
JAVA_HOME="$_CAD/jdk/linux64"
# 设置JAVA_HOME
        if [ ! -z $java_home ];then
                JAVA_HOME=$java_home
        fi
        export JAVA_HOME
        #设置java启动程序
        export _RUNJAVA=$JAVA_HOME/bin/java

        export LANG="zh_CN.UTF-8"

start(){
     procnum=$(ps -ef|grep ${MAIN_CLASS} |grep -v grep|awk '{print $2}'|wc -l)  
  if [ $procnum -eq 1 ];then 
     echo "${MAIN_CLASS} is alreay started!!! "
  else
     cd $_CAD
     nohup java -Xmx128m -Xms128m -jar -Dserver.port=8112 -Dspring.profiles.active=prod -Dserver.servlet.context-path=/ ./bin/${MAIN_CLASS}  > ./logs/out.txt 2>&1 &
     #nohup ./${MAIN_CLASS} > ./log/ot.log &
     sleep 2 
    procnum=$(ps -ef|grep ${MAIN_CLASS} |grep -v grep|awk '{print $2}'|wc -l)
  if [ $procnum -eq 1 ];then
     echo "${MAIN_CLASS} is starting...!"
  else echo "${MAIN_CLASS} is error!!!"
  fi fi 
}
stop(){
   procnum=$(ps -ef|grep ${MAIN_CLASS} |grep -v grep|awk '{print $2}'|wc -l)
   if [ $procnum -eq 1 ];then
      ps -ef | grep ${MAIN_CLASS} | grep -v grep |awk '{print $2}' | xargs kill -9 
      echo "kill ${MAIN_CLASS} success!"
   else
      echo "${MAIN_CLASS} is not started! "
   fi
}

info(){
    tail -fn 200 $_CAD/logs/out.txt
}

status(){
    ps -ef |grep ${MAIN_CLASS} |grep -v grep 
}

case $1 in
  start)
   start
  ;;
  stop)
   stop
  ;;
  info)
    info
  ;;
  status)
    status
  ;; 
  restart)
   $0 stop
   sleep 2
   $0 start
  ;;
  *)
   echo "Usage: {start|stop|restarti|info|status}"
  ;;
esac
exit 0

