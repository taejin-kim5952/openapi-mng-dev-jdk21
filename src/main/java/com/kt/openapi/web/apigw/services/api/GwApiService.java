package com.kt.openapi.web.apigw.services.api;

import com.kt.openapi.web.apigw.entity.api.manager.ApiEntity;
import com.kt.openapi.web.apigw.entity.deploy.DeployResult;
import com.kt.openapi.web.apigw.exception.DeploymentException;
import com.kt.openapi.web.apigw.type.ApiActionType;
import com.kt.openapi.web.apigw.type.GwProfile;

public interface GwApiService {
    /**
     * api 배포
     * @param profile : TB / PROD
     * @param actionType : CREATE / UPDATE / DELETE
     * @param apiEntity : deploy target api info
     * @return 배포 결과 및 서버 상태
     */
    DeployResult deploy(GwProfile profile, ApiActionType actionType, ApiEntity apiEntity) throws DeploymentException;

    /**
     * api 배포 (비동기) : 실시간 배포 상태 확인용
     * @param profile : TB / PROD
     * @param actionType : CREATE / UPDATE / DELETE
     * @param apiEntity : deploy target api info
     */
    void deployAsync(GwProfile profile, ApiActionType actionType, ApiEntity apiEntity) throws DeploymentException;

    /**
     * api 비동기 배포에 대한 실시간 상태
     * @param apiId
     * @return API 배포 현황
     */
    DeployResult deployStatus(String apiId) throws DeploymentException;

    void cleaning();

    boolean checkExists(GwProfile profile, String apiId, String version);
}

