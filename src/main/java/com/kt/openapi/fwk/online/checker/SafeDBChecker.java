package com.kt.openapi.fwk.online.checker;

import com.kt.openapi.web.util.CommonFunc;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * 앱 기동 시 인프라 정상 동작 여부를 체크하는 컴포넌트
 * [mngOnm 표준 이식]
 * [JDK21/Boot4 마이그레이션] local 프로파일은 SafeDB Agent(포트 19001)가 없는 환경이라
 * 이 시작 검사만 건너뜀 - safeDbEncrypt/Decrypt 로직 자체는 수정하지 않음.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Profile("!local")
public class SafeDBChecker implements ApplicationRunner {

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 배포 폴더 존재 여부 체크
        checkDeployFolder();

        String testOriginal = "1234";
        String encText = CommonFunc.safeDbEncrypt("1234");

        try {
            if (StringUtils.isEmpty(encText)) {
                log.error("SafeDB 가 정상 동작 중인지 확인하세요. 암호화에 실패하였습니다.");
                log.error("1. SafeDb Agent 실행 여부 (netstat -ano | find str 19001) 확인");
                log.error("2. SafeDBMap_config.xml에서 패스워드 확인");
                System.exit(1);
            }

            String decText = CommonFunc.safeDbDecrypt(encText);

            if (!testOriginal.equals(decText)) {
                log.error("SafeDB 가 정상 동작 중인지 확인하세요. 암호화 값과 복호화 값이 다릅니다.");
                log.error("1. SafeDb Agent 실행 여부 (netstat -ano | find str 19001) 확인");
                log.error("2. SafeDBMap_config.xml에서 패스워드 확인");
                System.exit(1);
            }
        }catch (Exception e) {
            log.error("SafeDB 체크 중 오류 발생: {}", e.getMessage());
            log.error("1. SafeDb Agent 실행 여부 (netstat -ano | find str 19001) 확인");
            log.error("2. SafeDBMap_config.xml에서 패스워드 확인");
        }
    }

    /**
     * 배포 설정 폴더(deploy) 존재 여부를 체크합니다.
     */
    private void checkDeployFolder() {
        String deployPath = "deploy";
        File deployFolder = new File(deployPath);
        if (!deployFolder.exists() || !deployFolder.isDirectory()) {
            log.error("배포 설정 폴더가 존재하지 않습니다: {} (절대경로: {})", deployPath, deployFolder.getAbsolutePath());
            log.error("SafeDBMap_config.xml 파일이 프로젝트 루트의 deploy/ 폴더에 위치해야 합니다.");
        }
    }
}
