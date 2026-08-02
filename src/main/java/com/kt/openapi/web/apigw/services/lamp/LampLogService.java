package com.kt.openapi.web.apigw.services.lamp;

import com.kt.openapi.web.apigw.entity.lamp.LampResponse;
import com.kt.openapi.web.apigw.exception.ServiceException;
import com.kt.openapi.web.apigw.type.GwProfile;

public interface LampLogService {
    /**
     * LAMP 로그조회
     * @param searchDate 조회일자(yyyyMMdd)
     * @param transactionId G/W 응답 transactionId
     * @param apiId API ID
     * @return
     */
    LampResponse getByTransaction(GwProfile profile, String searchDate, String transactionId, String apiId) throws ServiceException;
    String getRawByTransaction(GwProfile profile, String searchDate, String transactionId, String apiId) throws ServiceException;
}
