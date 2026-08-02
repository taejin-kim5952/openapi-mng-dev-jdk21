#!/bin/bash

# context.xml 시크릿 적용
sed -i "s|#{DB_URL}|$DB_URL|g" /usr/local/tomcat/conf/context.xml
sed -i "s|#{DB_USERNAME}|$DB_USERNAME|g" /usr/local/tomcat/conf/context.xml
sed -i "s|#{DB_PASSWORD}|$DB_PASSWORD|g" /usr/local/tomcat/conf/context.xml

# SafeDBMap_config.xml 시크릿 적용
sed -i "s|#{SAFEDB_AUTH_PASSWORD}|$SAFEDB_AUTH_PASSWORD|g" /usr/local/tomcat/webapps/ROOT/WEB-INF/classes/SafeDBMap_config.xml

exec /usr/local/tomcat/bin/catalina.sh run

