// 개발서버 배포 파이프라인 (GitHub push -> Jenkins -> 개발서버)
//
// 전제
//   - Jenkins 가 개발서버와 같은 머신에서 동작한다 (openapi-mock 파이프라인과 동일 구조).
//   - Jenkins 에이전트에는 Docker 만 있으면 된다. Maven/JDK21 은 Dockerfile_jar 의
//     빌드 스테이지(maven:3.9-eclipse-temurin-21)가 담당한다.
//   - GitHub Webhook 으로 push 시 자동 기동한다 (Jenkins 잡 설정에서
//     "GitHub hook trigger for GITScm polling" 체크 필요).
//
// 이미지는 Dockerfile_jar 로 만든다. 기존 Dockerfile / Dockerfile_dev 는
// "외부 Tomcat 9(javax) + WAR + JDK17" 전제라 이 프로젝트(jar / Boot 4.1.0 / Java 21)에는 쓸 수 없다.
pipeline {
    agent any

    parameters {
        string(name: 'SPRING_PROFILE', defaultValue: 'tb',
               description: '실행 프로파일 (config/<profile>/ 아래 설정을 읽는다)')
        string(name: 'ENV_FILE', defaultValue: '/opt/openapi-mng-dev/dev.env',
               description: 'DB 비밀번호·PSSO 키 등 런타임 환경변수 파일. tb 프로파일은 기본값 없는 ${TB_DB_PASSWORD} 같은 값을 요구하므로 이 파일이 없으면 기동에 실패한다.')
        string(name: 'HOST_PORT', defaultValue: '8081',
               description: '개발서버에서 노출할 포트')
    }

    environment {
        IMAGE_NAME     = 'openapi-mng-dev-jdk21'
        CONTAINER_NAME = 'openapi-mng-dev-jdk21'
        // application.yml 의 server.port / server.servlet.context-path 와 일치해야 한다.
        APP_PORT       = '8081'
        CONTEXT_PATH   = '/apidev'
    }

    triggers {
        githubPush()
    }

    options {
        disableConcurrentBuilds()
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '20'))
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build image') {
            steps {
                // 멀티스테이지라 이 한 단계에서 mvn package 까지 같이 돌아간다.
                sh """
                    docker build \
                        -f Dockerfile_jar \
                        --build-arg env=${params.SPRING_PROFILE} \
                        -t ${IMAGE_NAME}:${BUILD_NUMBER} \
                        -t ${IMAGE_NAME}:latest \
                        .
                """
            }
        }

        stage('Deploy') {
            steps {
                sh """
                    if [ ! -f "${params.ENV_FILE}" ]; then
                        echo "환경변수 파일이 없습니다: ${params.ENV_FILE}"
                        echo "TB_DB_PASSWORD, PSSO_AES_KEY 등 기본값 없는 값이 있어 이대로면 기동에 실패합니다."
                        exit 1
                    fi

                    docker stop ${CONTAINER_NAME} || true
                    docker rm ${CONTAINER_NAME} || true
                    docker run -d \
                        --name ${CONTAINER_NAME} \
                        --restart unless-stopped \
                        -p ${params.HOST_PORT}:${APP_PORT} \
                        --env-file "${params.ENV_FILE}" \
                        -e SPRING_PROFILES_ACTIVE=${params.SPRING_PROFILE} \
                        ${IMAGE_NAME}:${BUILD_NUMBER}
                """
            }
        }

        stage('Health check') {
            steps {
                // actuator 를 안 쓰므로 컨텍스트 루트가 HTTP 로 응답하는지로 확인한다.
                // 로그인 리다이렉트(302)도 "기동 성공"이다. 5xx 와 무응답(000)만 실패로 본다.
                sh """
                    for i in \$(seq 1 40); do
                        code=\$(curl -s -o /dev/null -w '%{http_code}' \
                                http://localhost:${params.HOST_PORT}${CONTEXT_PATH}/ || echo 000)
                        if [ "\$code" != "000" ] && [ "\$code" -lt 500 ]; then
                            echo "기동 확인 (HTTP \$code)"
                            exit 0
                        fi
                        echo "기동 대기 중... (\$i/40, HTTP \$code)"
                        sleep 5
                    done
                    echo "헬스체크 실패 - 컨테이너 로그를 확인하세요."
                    docker logs --tail 200 ${CONTAINER_NAME}
                    exit 1
                """
            }
        }
    }

    post {
        success {
            sh "docker image prune -f --filter until=24h || true"
        }
        failure {
            sh "docker ps -a --filter name=${CONTAINER_NAME} || true"
            sh "docker logs --tail 200 ${CONTAINER_NAME} || true"
        }
    }
}
