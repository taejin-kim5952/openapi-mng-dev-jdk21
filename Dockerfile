FROM ktbase.azurecr.io/edward.kang/tomcat:9.0.115-jdk17-no-openssl

ARG env
ENV ENV_VAR=$env
ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8
ENV TZ=Asia/Seoul
COPY /insights/* /insights/
COPY ./context.xml /usr/local/tomcat/conf/context.xml
COPY ./server.xml /usr/local/tomcat/conf/server.xml
COPY ./public_error.html /usr/local/tomcat/public_error.html

# entrypoint 스크립트 추가
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

ENV JAVA_OPTS="-Dspring.profiles.active=$ENV_VAR -Djdk.tls.client.protocols=TLSv1.3,TLSv1.2 -Djdk.tls.client.cipherSuites=TLS_AES_128_GCM_SHA256,TLS_AES_256_GCM_SHA384,TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384,TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,TLS_ECDHE_RSA_WITH_AES_256_GCM_SHA384,TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256,TLS_ECDHE_ECDSA_WITH_AES_256_CBC_SHA384,TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA256,TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA384 -javaagent:'/insights/applicationinsights-agent-3.6.2.jar'"
COPY target/*.war /usr/local/tomcat/webapps/ROOT.war
RUN mkdir -p /usr/local/tomcat/webapps/ROOT && \
    cd /usr/local/tomcat/webapps/ROOT && \
    $JAVA_HOME/bin/jar xf ../ROOT.war

# entrypoint 사용
ENTRYPOINT ["/entrypoint.sh"]