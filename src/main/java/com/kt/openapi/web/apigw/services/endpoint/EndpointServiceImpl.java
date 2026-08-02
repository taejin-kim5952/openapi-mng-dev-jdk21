package com.kt.openapi.web.apigw.services.endpoint;

import com.kt.openapi.web.apigw.constants.GwConstants;
import com.kt.openapi.web.apigw.entity.ResultEntity;
import com.kt.openapi.web.apigw.entity.endpoint.Endpoint;
import com.kt.openapi.web.apigw.type.GWResultType;
import com.kt.openapi.web.apigw.type.GwProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static com.kt.openapi.web.apigw.type.GwProfile.TB;

@Service
public class EndpointServiceImpl implements EndpointService {
    private static final Logger log = LoggerFactory.getLogger(EndpointService.class);

    private final RestTemplate gwRestTemplate;

    @Value("${gateway.ml.tbUrl}")
    private String mlTbUrl;
    @Value("${gateway.ml.prodUrl}")
    private String mlProdUrl;
    @Value("${gateway.ml.prodBUrl}")
    private String mlBProdUrl;

    @Autowired
    public EndpointServiceImpl(RestTemplate gwRestTemplate) {
        this.gwRestTemplate = gwRestTemplate;
    }

    @Override
    public ResultEntity<List<Endpoint>> listAll(GwProfile profile) {
        String mlUrl = (profile == TB) ? mlTbUrl : mlBProdUrl;
        String url = mlUrl + GwConstants.ENDPOINT_LIST_URI;

        try {
            log.info("Call get endpoint list from GW");
            ResponseEntity<List<Endpoint>> response = gwRestTemplate.exchange(url, HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<Endpoint>>() {
                    });
            
            ResultEntity<List<Endpoint>> res = new ResultEntity<>();
            res.setHttpStatus(response.getStatusCode());
            res.setResponse(response.getBody());
            res.setResult(response.getStatusCode().is2xxSuccessful() ? GWResultType.OK : GWResultType.FAILURE);
            return res;
        } catch (Exception e) {
            log.error("Exception, during call endpoint list, message={}", e.getMessage(), e);
            return new ResultEntity<List<Endpoint>>(GWResultType.FAILURE, e.getMessage());
        }
    }

    @Override
    public ResultEntity<Endpoint> getById(GwProfile profile, String id) {
        String mlUrl = (profile == TB) ? mlTbUrl : mlBProdUrl;
        String url = mlUrl + "/apilink/v1/endpoint/getEndpointById?endpointId=" + id;

        try {
            log.info("Call get endpoint by id from GW");
            ResponseEntity<Endpoint> response = gwRestTemplate.exchange(url, HttpMethod.GET, null,
                    new ParameterizedTypeReference<Endpoint>() {
                    });
            
            ResultEntity<Endpoint> res = new ResultEntity<>();
            res.setHttpStatus(response.getStatusCode());
            res.setResponse(response.getBody());
            res.setResult(response.getStatusCode().is2xxSuccessful() ? GWResultType.OK : GWResultType.FAILURE);
            return res;
        } catch (Exception e) {
            log.error("Exception, during call endpoint by id, message={}", e.getMessage(), e);
            return new ResultEntity<Endpoint>(GWResultType.FAILURE, e.getMessage());
        }
    }

    @Override
    public ResultEntity<Endpoint> create(GwProfile profile, Endpoint endpoint) {
        String mlUrl = (profile == TB) ? mlTbUrl : mlProdUrl;
        return create(mlUrl, endpoint);
    }

    @Override
    public ResultEntity<Endpoint> create(String url, Endpoint endpoint) {
        String newUrl = url + "/apilink/v1/endpoint/createEndpoint";

        try {
            log.info("Call create endpoint to GW");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Endpoint> request = new HttpEntity<>(endpoint, headers);

            ResponseEntity<Endpoint> response = gwRestTemplate.exchange(newUrl, HttpMethod.POST, request, Endpoint.class);
            
            ResultEntity<Endpoint> res = new ResultEntity<>();
            res.setHttpStatus(response.getStatusCode());
            res.setResponse(response.getBody());
            res.setResult(response.getStatusCode().is2xxSuccessful() ? GWResultType.OK : GWResultType.FAILURE);
            return res;
        } catch (Exception e) {
            log.error("Exception, during call create endpoint, message={}", e.getMessage(), e);
            return new ResultEntity<Endpoint>(GWResultType.FAILURE, e.getMessage());
        }
    }

    @Override
    public ResultEntity deploy(GwProfile profile, String pl, String id) {
        String mlUrl = (profile == TB) ? mlTbUrl : mlBProdUrl;
        return deploy(mlUrl, pl, id);
    }

    @Override
    public ResultEntity deploy(String url, String pl, String id) {
        String newUrl = url + GwConstants.NEW_DEPLOYMENT_URI.formatted(pl);
        ResultEntity<Object> res = new ResultEntity<>();
        res.setHttpStatus(HttpStatus.OK);
        res.setResult(GWResultType.OK);
        return res;
    }

    @Override
    public ResultEntity delete(GwProfile profile, String id) {
        String mlUrl = (profile == TB) ? mlTbUrl : mlBProdUrl;
        return delete(mlUrl, id);
    }

    @Override
    public ResultEntity delete(String url, String id) {
        String newUrl = url + "/apilink/v1/endpoint/endpoint?endpointId=" + id;

        try {
            log.info("Call delete endpoint to GW");
            ResponseEntity<Object> response = gwRestTemplate.exchange(newUrl, HttpMethod.DELETE, null, Object.class);
            
            ResultEntity<Object> res = new ResultEntity<>();
            res.setHttpStatus(response.getStatusCode());
            res.setResult(response.getStatusCode().is2xxSuccessful() ? GWResultType.OK : GWResultType.FAILURE);
            return res;
        } catch (Exception e) {
            log.error("Exception, during call delete endpoint, message={}", e.getMessage(), e);
            return new ResultEntity<Object>(GWResultType.FAILURE, e.getMessage());
        }
    }
}