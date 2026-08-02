package com.kt.openapi.web.apigw.services.lamp;

import com.kt.openapi.web.apigw.entity.lamp.LampRequest;
import com.kt.openapi.web.apigw.entity.lamp.LampResponse;
import com.kt.openapi.web.apigw.exception.ServiceException;
import com.kt.openapi.web.apigw.type.GwProfile;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class LampLogServiceImpl implements LampLogService {
    private static final Logger log = LoggerFactory.getLogger(LampLogService.class);

    private final RestTemplate gwRestTemplate;

    //@Value("${lamp.tbUrl}")
    @Value("${lamp.tbUrl}")
    private String tbUrl;

    //@Value("${lamp.prodUrl}")
    @Value("${lamp.prodUrl}")
    private String prodUrl;

    //@Value("${lamp.logApi}")
    @Value("${lamp.logApi}")
    private String apiUrl;

    //@Value("${lamp.serviceCode}")
    @Value("${lamp.serviceCode}")
    private String serviceCode;
   // @Value("${lamp.authKey}")
    @Value("${lamp.authKey}")
    private String authKey;
    //@Value("${lamp.gwServiceCode}")
    @Value("${lamp.gwServiceCode}")
    private String gatewayServiceCode;

    @Autowired
    public LampLogServiceImpl(RestTemplate gwRestTemplate) {
        this.gwRestTemplate = gwRestTemplate;
    }

    @Override
    public LampResponse getByTransaction(GwProfile profile, String searchDate, String transactionId, String apiId) throws ServiceException {
        log.info("Request Lamp log, profile={}, searchDate={}, transactionId={}, apiId={}", profile, searchDate, transactionId, apiId);
        if (StringUtils.isBlank(transactionId)) {
            throw new ServiceException("transactionId is required");
        }
//        if (StringUtils.isBlank(apiId)) {
//            throw new ServiceException("apiId is required");
//        }
        String url = (profile == GwProfile.TB) ? tbUrl : prodUrl;
        url += apiUrl;

        LampRequest request = new LampRequest();
        request.setServiceId(serviceCode);
        request.setKey(authKey);
        request.setServiceCode(gatewayServiceCode);

        request.setTransactionId(transactionId);
        request.setSearchDate(searchDate);
        request.setApiId(apiId);

        try {
            log.info("Call Lamp get log api, url={}", url, profile);
            ResponseEntity<LampResponse> response = gwRestTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(request),
                    new ParameterizedTypeReference<LampResponse>() {
                    });
            return response.getBody();
        } catch (Exception e) {
            log.error("Exception, during call Lamp get log API, message={}", e.getMessage(), e);
            throw new ServiceException("Exception, during call Lamp API, message="+ e.getMessage(), e);
        }
    }

    @Override
    public String getRawByTransaction(GwProfile profile, String searchDate, String transactionId, String apiId) throws ServiceException {
        log.info("Request Lamp log, profile={}, searchDate={}, transactionId={}, apiId={}", profile, searchDate, transactionId, apiId);
        if (StringUtils.isBlank(transactionId)) {
            throw new ServiceException("transactionId is required");
        }
//        if (StringUtils.isBlank(apiId)) {
//            throw new ServiceException("apiId is required");
//        }
        String url = (profile == GwProfile.TB) ? tbUrl : prodUrl;
        url += apiUrl;

        LampRequest request = new LampRequest();
        request.setServiceId(serviceCode);
        request.setKey(authKey);
        request.setServiceCode(gatewayServiceCode);

        request.setTransactionId(transactionId);
        request.setSearchDate(searchDate);
        request.setApiId(apiId);

        try {
            log.info("Call Lamp get log api, url={}", url, profile);
            ResponseEntity<String> response = gwRestTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(request),
                    String.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Exception, during call Lamp get log API, message={}", e.getMessage(), e);
            throw new ServiceException("Exception, during call Lamp API, message="+ e.getMessage(), e);
        }
    }


}
