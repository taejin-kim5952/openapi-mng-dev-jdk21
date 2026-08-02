package com.kt.openapi.web.apigw.services.api;

import com.kt.openapi.web.apigw.entity.ResultEntity;
import com.kt.openapi.web.apigw.entity.api.GwApi;
import com.kt.openapi.web.apigw.entity.api.GwApiEntity;
import com.kt.openapi.web.apigw.type.GwProfile;

import java.util.List;

public interface APIService {
    ResultEntity<List<GwApi>> listAll(GwProfile profile);
    ResultEntity<GwApiEntity> getById(GwProfile profile, String id);
    ResultEntity<GwApiEntity> create(GwProfile profile, GwApiEntity apiEntity);
    ResultEntity<GwApiEntity> create(String url, GwApiEntity apiEntity);
    ResultEntity deploy(GwProfile profile, String pl, String id);
    ResultEntity deploy(String url, String pl, String id);

    ResultEntity delete(GwProfile profile, String id);
    ResultEntity delete(String url, String id);

    ResultEntity deployAL(GwProfile profile, String al, String id);
    ResultEntity deployAL(String url, String al, String id);

    /* versioning 관련 추가 */
    ResultEntity<GwApiEntity> getById(GwProfile profile, String id, String version);

    ResultEntity deploy(GwProfile profile, String pl, String id, String version);
    ResultEntity deploy(String url, String pl, String id, String version);

    ResultEntity delete(GwProfile profile, String id, String version);
    ResultEntity delete(String url, String id, String version);

    ResultEntity<Boolean> checkExists(GwProfile profile, String id, String version);
    ResultEntity<Boolean> checkExists(String url, String id, String version);
}
