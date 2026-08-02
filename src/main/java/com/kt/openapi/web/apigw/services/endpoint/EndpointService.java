package com.kt.openapi.web.apigw.services.endpoint;

import com.kt.openapi.web.apigw.entity.ResultEntity;
import com.kt.openapi.web.apigw.entity.endpoint.Endpoint;
import com.kt.openapi.web.apigw.type.GwProfile;

import java.util.List;

public interface EndpointService {

    /**
     *
     * @return Endpoint 목록 조회
     */
    ResultEntity<List<Endpoint>> listAll(GwProfile profile);

    /**
     *
     * @param id
     * @return Endpoint 단일 조회
     */
    ResultEntity<Endpoint> getById(GwProfile profile, String id);

    /**
     *
     * @param endpoint
     * @return 생성 결과
     */
    ResultEntity<Endpoint> create(GwProfile profile, Endpoint endpoint);
    ResultEntity<Endpoint> create(String url, Endpoint endpoint);

    /**
     *
     * @param id
     * @return 배포 결과
     */
    ResultEntity deploy(GwProfile profile, String pl, String id);
    ResultEntity deploy(String url, String pl, String id);

    ResultEntity delete(GwProfile profile, String id);
    ResultEntity delete(String url, String id);
}
