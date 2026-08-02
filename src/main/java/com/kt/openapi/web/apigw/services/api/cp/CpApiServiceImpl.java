package com.kt.openapi.web.apigw.services.api.cp;

import com.kt.openapi.web.apigw.constants.GwConstants;
import com.kt.openapi.web.apigw.entity.api.cp.CpApiRequest;
import com.kt.openapi.web.apigw.entity.api.cp.CpApiResponse;
import com.kt.openapi.web.apigw.type.GwProfile;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class CpApiServiceImpl implements CpApiService {
    private static final Logger log = LoggerFactory.getLogger(CpApiService.class);

    private final RestTemplate gwRestTemplate;

    //--[drm][chg]@Value("${gateway.al.tbUrl}")
    @Value("${gateway.al.tbUrl}")
    private String alTbUrl;
    //--[drm][chg]@Value("${gateway.al.prodUrl}")
    @Value("${gateway.al.prodUrl}")
    private String alProdUrl;

    @Autowired
    public CpApiServiceImpl(RestTemplate gwRestTemplate) {
        this.gwRestTemplate = gwRestTemplate;
    }

    @Override
    public CpApiResponse get(GwProfile profile, CpApiRequest request) {
        String url = (profile == GwProfile.TB) ? alTbUrl : alProdUrl;
        log.info("Call CP API, profile={}", profile);
        return this.get(url, request);
    }

	//-- [tag:PRJ-20220901]
	//-- [i][url parameter를 사용하는 기본형 함수]
	@Override
	public CpApiResponse get(String apiVeriBaseurl, CpApiRequest request) {
        String url = "%s%s".formatted(apiVeriBaseurl, request.getApiUrl());
        HttpHeaders headers = new HttpHeaders();
        for (Map.Entry<String, String> entry : request.getHeaders().entrySet()) {
            headers.add(entry.getKey(), entry.getValue());
        }

        Map<String, Object> body = new HashMap<>();
        if (!StringUtils.isBlank(request.getTransactionId())) {
            body.put(GwConstants.CP_API.TRANSACTION_ID, request.getTransactionId());
        }
        if (!StringUtils.isBlank(request.getSequenceNo())) {
            body.put(GwConstants.CP_API.SEQUENCE_NO, request.getSequenceNo());
        }
        if (request.getRequest() != null) {
            body.put(GwConstants.CP_API.REQUEST, request.getRequest());
        }
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            log.info("Call CP API, url={}", url);
            ResponseEntity<CpApiResponse> response = gwRestTemplate.exchange(url, HttpMethod.POST, entity, new ParameterizedTypeReference<CpApiResponse>() {});
            return response.getBody();
        } catch (Exception e) {
            log.error("Exception, during call CP API, message={}", e.getMessage(), e);
        }
        return null;
	}
}
