package com.kt.openapi.web.apigw.services.api;

import com.kt.openapi.web.apigw.constants.GwConstants;
import com.kt.openapi.web.apigw.entity.ResultEntity;
import com.kt.openapi.web.apigw.entity.api.GwApi;
import com.kt.openapi.web.apigw.entity.api.GwApiEntity;
import com.kt.openapi.web.apigw.entity.api.GwDeploymentEntity;
import com.kt.openapi.web.apigw.entity.api.PLDeploymentEntity;
import com.kt.openapi.web.apigw.type.GWResultType;
import com.kt.openapi.web.apigw.type.GwProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kt.openapi.web.apigw.type.GwProfile.TB;

@Service
public class APIServiceImpl implements APIService {
    private static final Logger log = LoggerFactory.getLogger(APIService.class);

    private final RestTemplate gwRestTemplate;

    @Value("${gateway.ml.tbUrl}")
    private String mlTbUrl;
    @Value("${gateway.ml.prodUrl}")
    private String mlProdUrl;
    @Value("${gateway.ml.prodBUrl}")
    private String mlBProdUrl;
    @Value("${gateway.ml.prodDUrl}")
    private String mlDProdUrl;


    @Autowired
    public APIServiceImpl(RestTemplate gwRestTemplate) {
        this.gwRestTemplate = gwRestTemplate;
    }

    @Override
    public ResultEntity<List<GwApi>> listAll(GwProfile profile) {
        String mlUrl = (profile == TB) ? mlTbUrl : mlBProdUrl;
        String url = mlUrl + GwConstants.API_BASE_URI;

        try {
            log.info("Call get api list from GW");
            ResponseEntity<List<GwApi>> response = gwRestTemplate.exchange(url, HttpMethod.GET, null,
                    new ParameterizedTypeReference<List<GwApi>>() {
                    });
            
            ResultEntity<List<GwApi>> res = new ResultEntity<>();
            res.setHttpStatus(response.getStatusCode());
            res.setResponse(response.getBody());
            res.setResult(response.getStatusCode().is2xxSuccessful() ? GWResultType.OK : GWResultType.FAILURE);
            return res;
        } catch (Exception e) {
            log.error("Exception, during call api list, message={}", e.getMessage(), e);
            return new ResultEntity<List<GwApi>>(GWResultType.FAILURE, e.getMessage());
        }
    }

    @Override
    public ResultEntity<GwApiEntity> getById(GwProfile profile, String id) {
        String mlUrl = (profile == TB) ? mlTbUrl : mlBProdUrl;
        String url = mlUrl + GwConstants.API_BASE_URI + "/" + id;

        try {
            log.info("Call get api from GW, id={}", id);

            ResponseEntity<GwApiEntity> response = gwRestTemplate.exchange(url, HttpMethod.GET, null,
                    new ParameterizedTypeReference<GwApiEntity>() {
                    });
            
            ResultEntity<GwApiEntity> res = new ResultEntity<>();
            res.setHttpStatus(response.getStatusCode());
            res.setResponse(response.getBody());
            res.setResult(response.getStatusCode().is2xxSuccessful() ? GWResultType.OK : GWResultType.FAILURE);
            return res;
        } catch (Exception e) {
            log.error("Exception, during call api, message={}", e.getMessage(), e);
            return new ResultEntity<GwApiEntity>(GWResultType.FAILURE, e.getMessage());
        }
    }

    @Override
    public ResultEntity<GwApiEntity> getById(GwProfile profile, String id, String version) {
        String mlUrl = (profile == TB) ? mlTbUrl : mlBProdUrl;
        String url = mlUrl + GwConstants.NEW_API_BASE_URI + "/" + id + "/" + version;

        try {
            log.info("Call get api from GW, id={}, version={}", id, version);

            ResponseEntity<GwApiEntity> response = gwRestTemplate.exchange(url, HttpMethod.GET, null,
                    new ParameterizedTypeReference<GwApiEntity>() {
                    });
            
            ResultEntity<GwApiEntity> res = new ResultEntity<>();
            res.setHttpStatus(response.getStatusCode());
            res.setResponse(response.getBody());
            res.setResult(response.getStatusCode().is2xxSuccessful() ? GWResultType.OK : GWResultType.FAILURE);
            return res;
        } catch (Exception e) {
            log.error("Exception, during call api, message={}", e.getMessage(), e);
            return new ResultEntity<GwApiEntity>(GWResultType.FAILURE, e.getMessage());
        }
    }

    @Override
    public ResultEntity<GwApiEntity> create(GwProfile profile, GwApiEntity apiEntity) {
        String mlUrl = (profile == TB) ? mlTbUrl : mlProdUrl;
        return create(mlUrl, apiEntity);
    }

    @Override
    public ResultEntity<GwApiEntity> create(String url, GwApiEntity apiEntity) {
    	String newUrl = url + GwConstants.NEW_API_BASE_URI + "/" + apiEntity.getId() + "/" + apiEntity.getApiVersion();
        try {
            log.info("Call create api, id={}", apiEntity.getId());
            ResponseEntity<Void> response = gwRestTemplate.exchange(newUrl, HttpMethod.PUT,
                    new HttpEntity<>(apiEntity), Void.class);
            
            ResultEntity<GwApiEntity> res = new ResultEntity<>();
            res.setHttpStatus(response.getStatusCode());
            res.setResult(response.getStatusCode().is2xxSuccessful() ? GWResultType.OK : GWResultType.FAILURE);
            return res;
        } catch (Exception e) {
            log.error("Exception, during call creating api, message={}", e.getMessage(), e);
            return new ResultEntity<GwApiEntity>(GWResultType.FAILURE, e.getMessage());
        }
    }


    @Override
    public ResultEntity deploy(GwProfile profile, String pl, String id) {
        String mlUrl = (profile == TB) ? mlTbUrl :
                (pl.startsWith(GwConstants.SERVER_BD_PREFIX)) ? mlBProdUrl : mlDProdUrl;
        return deploy(mlUrl, pl, id);
    }

    @Override
    public ResultEntity deploy(String url, String pl, String id) {
    	String newUrl = url + GwConstants.NEW_DEPLOYMENT_URI.formatted(pl);
        GwDeploymentEntity deploymentEntity = new GwDeploymentEntity(GwConstants.DEPLOYMENT_TARGET_API, id);
        try {
            log.info("Call deploy(PL) api , id={}", id);
            ResponseEntity<Void> response = gwRestTemplate.exchange(newUrl, HttpMethod.POST,
                    new HttpEntity<>(deploymentEntity), Void.class);
            
            ResultEntity<Object> res = new ResultEntity<>();
            res.setHttpStatus(response.getStatusCode());
            res.setResult(response.getStatusCode().is2xxSuccessful() ? GWResultType.OK : GWResultType.FAILURE);
            return res;
        } catch (Exception e) {
            log.error("Exception, during call deploying api, message={}", e.getMessage(), e);
            return new ResultEntity<Object>(GWResultType.FAILURE, e.getMessage());
        }
    }

    @Override
    public ResultEntity deploy(GwProfile profile, String pl, String id, String version) {
        String mlUrl = (profile == TB) ? mlTbUrl :
                (pl.startsWith(GwConstants.SERVER_BD_PREFIX)) ? mlBProdUrl : mlDProdUrl;
        return deploy(mlUrl, pl, id, version);
    }

    @Override
    public ResultEntity deploy(String url, String pl, String id, String version) {
    	String newUrl = url + GwConstants.NEW_DEPLOYMENT_URI.formatted(pl);
        PLDeploymentEntity deploymentEntity = new PLDeploymentEntity(GwConstants.DEPLOYMENT_TARGET_API, id, version);
        try {
            log.info("Call deploy(PL) api , id={}, version={}", id, version);
            ResponseEntity<Void> response = gwRestTemplate.exchange(newUrl, HttpMethod.POST,
                    new HttpEntity<>(deploymentEntity), Void.class);
            
            ResultEntity<Object> res = new ResultEntity<>();
            res.setHttpStatus(response.getStatusCode());
            res.setResult(response.getStatusCode().is2xxSuccessful() ? GWResultType.OK : GWResultType.FAILURE);
            return res;
        } catch (Exception e) {
            log.error("Exception, during call deploying api, message={}", e.getMessage(), e);
            return new ResultEntity<Object>(GWResultType.FAILURE, e.getMessage());
        }
    }

    @Override
    public ResultEntity delete(GwProfile profile, String id) {
        String mlUrl = (profile == TB) ? mlTbUrl : mlProdUrl;
        return delete(mlUrl, id);
    }

    @Override
    public ResultEntity delete(String url, String id) {
    	String newUrl = url + GwConstants.API_BASE_URI + "/" + id;
        try {
            log.info("Call delete api, id={}", id);
            ResponseEntity<Void> response = gwRestTemplate.exchange(newUrl, HttpMethod.DELETE,
                    null, Void.class);
            
            ResultEntity<Object> res = new ResultEntity<>();
            res.setHttpStatus(response.getStatusCode());
            res.setResult(response.getStatusCode().is2xxSuccessful() ? GWResultType.OK : GWResultType.FAILURE);
            return res;
        } catch (Exception e) {
            log.error("Exception, during call deleting api, message={}", e.getMessage(), e);
            return new ResultEntity<Object>(GWResultType.FAILURE, e.getMessage());
        }
    }

    @Override
    public ResultEntity delete(GwProfile profile, String id, String version) {
        String mlUrl = (profile == TB) ? mlTbUrl : mlProdUrl;
        return delete(mlUrl, id, version);
    }

    @Override
    public ResultEntity delete(String url, String id, String version) {
    	String newUrl = url + GwConstants.NEW_API_BASE_URI + "/" + id + "/" + version;
        try {
            log.info("Call delete api, id={}, version={}", id, version);
            ResponseEntity<Void> response = gwRestTemplate.exchange(newUrl, HttpMethod.DELETE,
                    null, Void.class);
            
            ResultEntity<Object> res = new ResultEntity<>();
            res.setHttpStatus(response.getStatusCode());
            res.setResult(response.getStatusCode().is2xxSuccessful() ? GWResultType.OK : GWResultType.FAILURE);
            return res;
        } catch (Exception e) {
            log.error("Exception, during call deleting api, message={}", e.getMessage(), e);
            return new ResultEntity<Object>(GWResultType.FAILURE, e.getMessage());
        }
    }

    @Override
    public ResultEntity deployAL(GwProfile profile, String al, String id) {
        String mlUrl = (profile == TB) ? mlTbUrl :
                (al.startsWith(GwConstants.SERVER_BD_PREFIX)) ? mlBProdUrl : mlDProdUrl;
        return deployAL(mlUrl, al, id);
    }

    @Override
    public ResultEntity deployAL(String url, String al, String id) {
    	String newUrl = url + GwConstants.DEPLOYMENT_URI.formatted(al);

        Map<String, Object> entity = new HashMap<>();
        entity.put("to-list", Arrays.asList("*"));
        entity.put("reload", "on");

        try {
            log.info("Call deploy(AL) api, id={}", id);
            ResponseEntity<Void> response = gwRestTemplate.exchange(newUrl, HttpMethod.POST,
                    new HttpEntity<>(entity), Void.class);
            
            ResultEntity<Object> res = new ResultEntity<>();
            res.setHttpStatus(response.getStatusCode());
            res.setResult(response.getStatusCode().is2xxSuccessful() ? GWResultType.OK : GWResultType.FAILURE);
            return res;
        } catch (Exception e) {
            log.error("Exception, during call deploying(AL) api, message={}", e.getMessage(), e);
            return new ResultEntity<Object>(GWResultType.FAILURE, e.getMessage());
        }
    }

    @Override
    public ResultEntity<Boolean> checkExists(GwProfile profile, String id, String version) {
        String mlUrl = (profile == TB) ? mlTbUrl : mlProdUrl;
        return checkExists(mlUrl, id, version);
    }

    @Override
    public ResultEntity<Boolean> checkExists(String url, String id, String version) {
    	String newUrl = url + "/check/apis/%s?api-version=%s".formatted(id, version);
        try {
            log.info("Call check exists api from GW, id={}, version={}", id, version);
            ResponseEntity<Void> response = gwRestTemplate.exchange(newUrl, HttpMethod.GET, null, Void.class);
            
            ResultEntity<Boolean> res = new ResultEntity<>();
            res.setHttpStatus(response.getStatusCode());
            res.setResponse(response.getStatusCode() != HttpStatus.OK);
            res.setResult(GWResultType.OK);
            return res;
        } catch (Exception e) {
            log.error("Exception, during call api, message={}", e.getMessage(), e);
            return new ResultEntity<Boolean>(GWResultType.FAILURE, e.getMessage());
        }
    }
}