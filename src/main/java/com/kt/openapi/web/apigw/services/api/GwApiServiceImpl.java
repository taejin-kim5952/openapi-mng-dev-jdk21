package com.kt.openapi.web.apigw.services.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kt.openapi.web.apigw.config.GwProperties;
import com.kt.openapi.web.apigw.constants.GwConstants;
import com.kt.openapi.web.apigw.converter.GwApiEntityConverter;
import com.kt.openapi.web.apigw.converter.GwKosCommonHeaderConverter;
import com.kt.openapi.web.apigw.entity.ResultEntity;
import com.kt.openapi.web.apigw.entity.api.GwApiEntity;
import com.kt.openapi.web.apigw.entity.api.GwApiRoute;
import com.kt.openapi.web.apigw.entity.api.GwKosCommonHeader;
import com.kt.openapi.web.apigw.entity.api.manager.ApiEntity;
import com.kt.openapi.web.apigw.entity.deploy.DeployResult;
import com.kt.openapi.web.apigw.entity.deploy.DeployServer;
import com.kt.openapi.web.apigw.entity.endpoint.Endpoint;
import com.kt.openapi.web.apigw.exception.DeploymentException;
import com.kt.openapi.web.apigw.services.endpoint.EndpointService;
import com.kt.openapi.web.apigw.type.*;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.kt.openapi.web.apigw.type.GwProfile.TB;

@Service
public class GwApiServiceImpl implements GwApiService {
    private static final Logger log = LoggerFactory.getLogger(GwApiServiceImpl.class);

    private static final long EXPIRED_JOB = 600000;

    private final Validator validator;
    private final EndpointService endpointService;
    private final APIService apiService;
    private final RestTemplate gwRestTemplate;
    private final GwProperties gwProperties;
    private final GwApiEntityConverter apiEntityConverter;
    private final ObjectMapper objectMapper;

    private ConcurrentHashMap<String, DeployResult> deployJobMap = new ConcurrentHashMap<>();

    @Value("${gateway.ml.prodBUrl}")
    private String mlProdBUrl;
    @Value("${gateway.ml.prodDUrl}")
    private String mlProdDUrl;

    @Autowired
    public GwApiServiceImpl(Validator validator, EndpointService endpointService, APIService apiService, RestTemplate gwRestTemplate, GwProperties gwProperties) {
        this.validator = validator;
        this.endpointService = endpointService;
        this.apiService = apiService;
        this.gwRestTemplate = gwRestTemplate;
        this.gwProperties = gwProperties;
        this.apiEntityConverter = new GwApiEntityConverter();
        objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Override
    public synchronized DeployResult deploy(GwProfile profile, ApiActionType actionType, ApiEntity apiEntity) throws DeploymentException {
        DeployResult deployResult = new DeployResult();

        List<String> plList = new ArrayList<>();
        List<String> alList = new ArrayList<>();
        makeDeployServers(profile, plList, alList);

        plList.forEach(pl -> deployResult.getServers().add(new DeployServer(pl)));
        alList.forEach(al -> deployResult.getAlServers().add(new DeployServer(al)));

        deployJobMap.put(apiEntity.getApiNo(), deployResult);

        this.deploy(profile, actionType, apiEntity, deployResult);
        deployJobMap.put(apiEntity.getApiNo(), deployResult);
        return deployResult;
    }

    @Async
    @Override
    public void deployAsync(GwProfile profile, ApiActionType actionType, ApiEntity apiEntity) throws DeploymentException {
        if (deployJobMap.containsKey(apiEntity.getApiNo())) {
            DeployResult jobResult = deployJobMap.get(apiEntity.getApiNo());
            if (jobResult.getStatus() != DeployJobStatus.DONE && jobResult.getStatus() != DeployJobStatus.FAIL) {
                throw new DeploymentException("Exception, 배포가 진행중입니다.");
            }
            deployJobMap.remove(apiEntity.getApiNo());
        }

        log.info("Start deploy async, apiId={}, apiNo={}", apiEntity.getId(), apiEntity.getApiNo());
        try {
            log.info("data={}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(apiEntity));
        } catch (JsonProcessingException e) {
            log.error("Exception, during converting json string, message={}", e.getMessage(), e);
        }
        DeployResult deployResult = new DeployResult();

        List<String> plList = new ArrayList<>();
        List<String> alList = new ArrayList<>();
        makeDeployServers(profile, plList, alList);
        plList.forEach(pl -> deployResult.getServers().add(new DeployServer(pl)));
        alList.forEach(al -> deployResult.getAlServers().add(new DeployServer(al)));

        deployJobMap.put(apiEntity.getApiNo(), deployResult);
        this.deploy(profile, actionType, apiEntity, deployResult);
    }

    @Override
    public boolean checkExists(GwProfile profile, String apiId, String version) {
        log.info("Request check exist API, profile={}, id={}, version={}", profile, apiId, version);
        ResultEntity<Boolean> result = apiService.checkExists(profile, apiId, version);
        log.info("==> Response check exist API, id={}, version={}, result={}", apiId, version, result);
        return result.getResponse();
    }

    private void makeDeployServers(GwProfile profile, List<String> plList, List<String> alList) {
        if (profile == TB) {
            plList.addAll(gwProperties.getTbPlList());
            alList.addAll(gwProperties.getTbAlList());
        } else {
            plList.addAll(gwProperties.getProdBPlList());
            plList.addAll(gwProperties.getProdDPlList());

            alList.addAll(gwProperties.getProdBAlList());
            alList.addAll(gwProperties.getProdDAlList());
        }
    }

    @Scheduled(cron = "${scheduler.cron.cleaning}")
    public void cleaning() {
        long now = System.currentTimeMillis();
        deployJobMap.entrySet()
                .removeIf(map -> (now - map.getValue().getLastAccessTime()) > EXPIRED_JOB);
    }


    @Override
    public DeployResult deployStatus(String apiId) throws DeploymentException {
        if (!deployJobMap.containsKey(apiId)) {
            log.info("Not Exist Deploy JOB, apiId={}", apiId);
            return null;
        }

        return deployJobMap.get(apiId);
    }

    private void checkValidate(ApiEntity apiEntity) throws Exception {
        Validate.notNull(apiEntity, "Required to set ApiEntity");
        Set<ConstraintViolation<ApiEntity>> constraintViolations = validator.validate(apiEntity);

        this.checkConstraintViolation(constraintViolations);

        if (apiEntity.getHandler() == HandlerType.KOS_SOAP_COMMON) {
            // check commonHeader
            GwKosCommonHeader gwKosCommonHeader = new GwKosCommonHeaderConverter().convert(apiEntity.getRequest().getHeaders());
            if (gwKosCommonHeader == null) {
                throw new DeploymentException("[%s] field is required for KOS Soap".formatted("Kos Header"));
            }
            if (StringUtils.isEmpty(gwKosCommonHeader.getAppName())) {
                throw new DeploymentException("[%s] field is required for KOS Soap".formatted("appName"));
            }

            if (StringUtils.isEmpty(gwKosCommonHeader.getSvcName())) {
                throw new DeploymentException("[%s] field is required for KOS Soap".formatted("svcName"));
            }
            if (StringUtils.isEmpty(gwKosCommonHeader.getFnName())) {
                throw new DeploymentException("[%s] field is required for KOS Soap".formatted("fnName"));
            }
            Set<ConstraintViolation<GwKosCommonHeader>> commonHeaderViolations = validator.validate(gwKosCommonHeader);
            this.checkConstraintViolation(commonHeaderViolations);
        }

        if (apiEntity.getRequest() == null) {
            throw new DeploymentException("apiEntity.getRequest is required");
        }
        if (apiEntity.getResponse() == null) {
            throw new DeploymentException("apiEntity.getResponse is required");
        }
    }

    private <T, V extends ConstraintViolation<T>> void checkConstraintViolation(Set<V> constraintViolations) throws DeploymentException {
        if (constraintViolations.size() > 0) {
            List<String> errorMessage = constraintViolations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.toList());
            throw new DeploymentException(StringUtils.join(errorMessage, "\n"));
        }
    }

    private String getGwAPI(String mlUrl, String apiId) throws DeploymentException {
        String url = "%s%s/{apiName}".formatted(mlUrl, GwConstants.API_BASE_URI);
        try {
            log.info("Get G/W API, id={}", apiId);
            ResponseEntity<String> response = gwRestTemplate.getForEntity(url, String.class, apiId);
            if (response.getStatusCode() != HttpStatus.OK) {
                return null;
            }
            return response.getBody();
        } catch (Exception e) {
            log.error("Exception, during call creating endpoint, message={}", e.getMessage(), e);
            throw new DeploymentException("Exception, during call G/W API, message=" + e.getMessage());
        }
    }

    public synchronized void deploy(GwProfile profile, ApiActionType actionType, ApiEntity apiEntity, DeployResult deployResult) {

        if (actionType == ApiActionType.DELETE) {
            this.delete(profile, apiEntity, deployResult);
        }

        try {
            // 1. Check Validate
            this.checkValidate(apiEntity);

            deployResult.setStatus(DeployJobStatus.INIT);
            deployJobMap.put(apiEntity.getApiNo(), deployResult);
            // 2. API Backup
            log.info("1. Start Backup (API & Endpoint)");
            Endpoint endpointBackup = null;

            /* 2019-07-08 versioning 관련 수정 (허수영) */
            String endpointId = "%s_%s".formatted(apiEntity.getId(), apiEntity.getVersion());
            ResultEntity<Endpoint> endpointResult = endpointService.getById(profile, endpointId);
            if (endpointResult.getHttpStatus() == HttpStatus.OK) {
                endpointBackup = endpointResult.getResponse();
            }

            GwApiEntity gwApiBackup = null;

            /* 2019-07-08 versioning 관련 수정 (허수영) */
            ResultEntity<GwApiEntity> resultEntity = apiService.getById(profile, apiEntity.getId(), apiEntity.getVersion());

            if (resultEntity.getHttpStatus() == HttpStatus.OK) {
                gwApiBackup = resultEntity.getResponse();
            }

            // 3. Endpoint 등록
            deployResult.setStatus(DeployJobStatus.DEPLOYING);

            deployJobMap.put(apiEntity.getApiNo(), deployResult);

            Endpoint endpoint = new Endpoint(endpointId, apiEntity.getEndpoint());
            if (profile == TB) {
                ResultEntity createEndpointResult = endpointService.create(profile, endpoint);
                if (checkCreateEndpointResult(apiEntity, deployResult, createEndpointResult)) return;
            } else {
                // prod
                // 1. 분당
                ResultEntity createEndpointResultB = endpointService.create(mlProdBUrl, endpoint);
                if (checkCreateEndpointResult(apiEntity, deployResult, createEndpointResultB)) return;

                // 2. 대전
                ResultEntity createEndpointResultD = endpointService.create(mlProdDUrl, endpoint);
                if (checkCreateEndpointResult(apiEntity, deployResult, createEndpointResultD)) return;
            }

            // 4. API 등록
            GwApiEntity gwApiEntity = apiEntityConverter.convert(apiEntity);
            if (profile == TB) {
                List<String> routeList = gwProperties.getTbRouteList();
                gwApiEntity.setRoute(new GwApiRoute(routeList));
                ResultEntity createApiResult = apiService.create(profile, gwApiEntity);

                if (checkCreateApiResult(apiEntity, deployResult, createApiResult)) return;
            } else {
                // prod
                // 1. 분당
                List<String> routeList1 = gwProperties.getProdBRouteList();
                gwApiEntity.setRoute(new GwApiRoute(routeList1));

                ResultEntity createApiResult1 = apiService.create(mlProdBUrl, gwApiEntity);
                if (checkCreateApiResult(apiEntity, deployResult, createApiResult1)) return;

                // 2. 대전
                List<String> routeList2 = gwProperties.getProdDRouteList();
                gwApiEntity.setRoute(new GwApiRoute(routeList2));
                ResultEntity createApiResult2 = apiService.create(mlProdDUrl, gwApiEntity);
                if (checkCreateApiResult(apiEntity, deployResult, createApiResult2)) return;
            }


            // 5. Endpoint 배포 (예외 발생 시 Rollback)
            // 6. API 배포 (예외 발생 시 Endpoint & API Rollback)


            for (DeployServer server : deployResult.getServers()) {
                try {
                    server.setStatus(JobStatus.DOING);
                    log.info("Deploy Endpoint & API, id={}, version={}, PL target={}", apiEntity.getId(), apiEntity.getVersion(), server.getServerName());
                    /* 2019-07-08 versioning 관련 수정 (허수영) */
                    ResultEntity plEndpointResult = endpointService.deploy(profile, server.getServerName(), endpointId);
                    if (plEndpointResult.getResult() == GWResultType.FAILURE) {
                        throw new DeploymentException(plEndpointResult.getErrorMessage());
                    }
                    /* 2019-07-08 versioning 관련 수정 (허수영) */
                    ResultEntity plDeployResult = apiService.deploy(profile, server.getServerName(), apiEntity.getId(), apiEntity.getVersion());
                    if (plDeployResult.getResult() == GWResultType.FAILURE) {
                        throw new DeploymentException(plDeployResult.getErrorMessage());
                    }
                    server.setStatus(JobStatus.DONE);
                } catch (Exception e) {
                    log.error("Exception, during Deploying, api id={}, version={}, PL={}, message={}", apiEntity.getId(),apiEntity.getVersion(), server.getServerName(), e.getMessage(), e);
                    server.setStatus(JobStatus.FAILURE);
                    deployResult.setStatus(DeployJobStatus.FAIL);
                    deployResult.setMessage("Exception, during deploying, apiId=%s, PL=%s, message=%s".formatted(
							apiEntity.getId(), server.getServerName(), e.getMessage()));
                    break;
                }
                deployJobMap.put(apiEntity.getApiNo(), deployResult);
            }

            if (deployResult.getStatus() != DeployJobStatus.FAIL) {
                for (DeployServer server : deployResult.getAlServers()) {
                    try {
                        server.setStatus(JobStatus.DOING);
                        log.info("Deploy API, id={}, AL target={}", apiEntity.getId(), server.getServerName());
                        ResultEntity alDeployResult = apiService.deployAL(profile, server.getServerName(), apiEntity.getId());
                        if (alDeployResult.getResult() == GWResultType.FAILURE) {
                            throw new DeploymentException(alDeployResult.getErrorMessage());
                        }
                        server.setStatus(JobStatus.DONE);
                    } catch (Exception e) {
                        log.error("Exception, during Deploying, api id={}. AL={}, message={}", apiEntity.getId(), server.getServerName(), e.getMessage(), e);
                        server.setStatus(JobStatus.FAILURE);
                        deployResult.setStatus(DeployJobStatus.FAIL);
                        deployResult.setMessage("Exception, during deploying, apiId=%s, PL=%s, message=%s".formatted(
								apiEntity.getId(), server.getServerName(), e.getMessage()));
                        break;
                    }
                    deployJobMap.put(apiEntity.getApiNo(), deployResult);
                }
            }

            if (deployResult.getStatus() != DeployJobStatus.FAIL) {
                deployResult.setStatus(DeployJobStatus.DONE);
                deployResult.setMessage("Success");
                deployJobMap.put(apiEntity.getApiNo(), deployResult);
                return;
            }

            log.info("Failure deploy, Start Rollback...");
            // rollback endpoint
            if (endpointBackup == null) {
                try {
                    if (profile == TB) {
                        /* 2019-07-08 versioning 관련 수정 (허수영) */
                        endpointService.delete(profile, endpointId);
                    } else {
                        // prod
                        // 1. 분당
                        /* 2019-07-08 versioning 관련 수정 (허수영) */
                        endpointService.delete(mlProdBUrl, endpointId);

                        // 2. 대전
                        /* 2019-07-08 versioning 관련 수정 (허수영) */
                        endpointService.delete(mlProdDUrl, endpointId);
                    }
                } catch (Exception e) {
                    log.error("Exception, during Rollback Endpoint(deleting endpoint), apiId={}, message={}", apiEntity.getId(), e.getMessage(), e);
                }
            } else {
                try {
                    if (profile == TB) {
                        endpointService.create(profile, endpointBackup);
                    } else {
                        // prod
                        // 1. 분당
                        endpointService.create(mlProdBUrl, endpointBackup);

                        // 2. 대전
                        endpointService.create(mlProdDUrl, endpointBackup);
                    }
                } catch (Exception e) {
                    log.error("Exception, during Rollback Endpoint, apiId={}, message={}", apiEntity.getId(), e.getMessage(), e);
                }
            }
            // rollback api
            if (gwApiBackup == null) {
                try {

                    if (profile == TB) {
                        apiService.delete(profile, apiEntity.getId(), apiEntity.getVersion());
                    } else {
                        // prod
                        // 1. 분당
                        apiService.delete(mlProdBUrl, apiEntity.getId(), apiEntity.getVersion());

                        // 2. 대전
                        apiService.delete(mlProdDUrl, apiEntity.getId(), apiEntity.getVersion());
                    }
                } catch (Exception e) {
                    log.error("Exception, during Rollback API(deleting API), apiId={}, message={}", apiEntity.getId(), e.getMessage(), e);
                }
            } else {
                try {
                    if (profile == TB) {
                        apiService.create(profile, gwApiBackup);
                    } else {
                        // prod
                        // 1. 분당
                        apiService.create(mlProdBUrl, gwApiBackup);

                        // 2. 대전
                        apiService.create(mlProdDUrl, gwApiBackup);
                    }
                    apiService.create(profile, gwApiBackup);
                } catch (Exception e) {
                    log.error("Exception, during Rollback API, apiId={}, message={}", apiEntity.getId(), e.getMessage(), e);
                }
            }

            for (DeployServer server : deployResult.getServers()) {
                if (server.getStatus() == JobStatus.DONE || server.getStatus() == JobStatus.FAILURE) {
                    try {
                        log.info("Rollback Endpoint & API, id={}, PL target={}", apiEntity.getId(), server.getServerName());
                        /* 2019-07-08 versioning 관련 수정 (허수영) */
                        endpointService.deploy(profile, server.getServerName(), endpointId);

                        /* 2019-07-08 versioning 관련 수정 (허수영) */
                        apiService.deploy(profile, server.getServerName(), apiEntity.getId(), apiEntity.getVersion());
                    } catch (Exception e) {
                        log.error("Exception, during Rollback Deployed PL, api id={}. PL={}, message={}", e.getMessage(), e);
                    }
                }
            }

            for (DeployServer server : deployResult.getAlServers()) {
                if (server.getStatus() == JobStatus.DONE || server.getStatus() == JobStatus.FAILURE) {
                    try {
                        log.info("Rollback API, id={}, AL target={}", apiEntity.getId(), server.getServerName());
                        apiService.deployAL(profile, server.getServerName(), apiEntity.getId());
                    } catch (Exception e) {
                        log.error("Exception, during Rollback Deployed AL, api id={}. AL={}, message={}", e.getMessage(), e);
                    }
                }
            }

        } catch (Exception e) {
            log.error("Exception, during check validation, message={}", e.getMessage(), e);
            deployResult.setStatus(DeployJobStatus.FAIL);
            deployResult.setMessage("Exception, during check validation, message=" + e.getMessage());
            deployJobMap.put(apiEntity.getApiNo(), deployResult);
        }
        deployResult.setLastAccessTime(System.currentTimeMillis());
        deployJobMap.put(apiEntity.getApiNo(), deployResult);
    }

    private boolean checkCreateApiResult(ApiEntity apiEntity, DeployResult deployResult, ResultEntity createApiResult) {
    	HttpStatusCode status = createApiResult.getHttpStatus();
        if (status != HttpStatus.OK && status != HttpStatus.CREATED) {
            deployResult.setStatus(DeployJobStatus.FAIL);
            deployResult.setMessage("Fail, during creating API, status=" + ((status == null) ? "null" : status.toString()));
            deployJobMap.put(apiEntity.getApiNo(), deployResult);
            return true;
        }
        return false;
    }

    private boolean checkCreateEndpointResult(ApiEntity apiEntity, DeployResult deployResult, ResultEntity createEndpointResultB) {
    	HttpStatusCode status = createEndpointResultB.getHttpStatus();
        if (status != HttpStatus.OK && status != HttpStatus.CREATED) {
            deployResult.setStatus(DeployJobStatus.FAIL);
            deployResult.setMessage("Fail, during creating endpoint, status=" + ((status == null) ? "null" : status.toString()));
            deployJobMap.put(apiEntity.getApiNo(), deployResult);
            return true;
        }
        return false;
    }

    public synchronized void delete(GwProfile profile, ApiEntity apiEntity, DeployResult deployResult) {

        try {
            // 1. Check Validate
            if (StringUtils.isEmpty(apiEntity.getId())) {
                throw new DeploymentException("[%s] field is required ".formatted("API ID"));
            }
            if (StringUtils.isEmpty(apiEntity.getVersion())) {
                throw new DeploymentException("[%s] field is required ".formatted("API Version"));
            }

            deployResult.setStatus(DeployJobStatus.INIT);
            deployJobMap.put(apiEntity.getApiNo(), deployResult);
            // 2. API Backup
            log.info("1. Start Backup (API & Endpoint)");
            Endpoint endpointBackup = null;

            /* 2019-07-08 versioning 관련 수정 (허수영) */
            String endpointId = "%s_%s".formatted(apiEntity.getId(), apiEntity.getVersion());
            ResultEntity<Endpoint> endpointResult = endpointService.getById(profile, endpointId);
            if (endpointResult.getHttpStatus() == HttpStatus.OK) {
                endpointBackup = endpointResult.getResponse();
            }

            GwApiEntity gwApiBackup = null;

            /* 2019-07-08 versioning 관련 수정 (허수영) */
            ResultEntity<GwApiEntity> resultEntity = apiService.getById(profile, apiEntity.getId(), apiEntity.getVersion());

            if (resultEntity.getHttpStatus() == HttpStatus.OK) {
                gwApiBackup = resultEntity.getResponse();
            }

            if (endpointBackup == null && gwApiBackup == null) {
                throw new DeploymentException("Not found API");
            }

            // 3. Endpoint 삭제
            deployResult.setStatus(DeployJobStatus.DEPLOYING);

            deployJobMap.put(apiEntity.getApiNo(), deployResult);

            if (profile == TB) {
                ResultEntity deleteEndpointResult = endpointService.delete(profile, endpointId);
                if (checkCreateEndpointResult(apiEntity, deployResult, deleteEndpointResult)) return;
            } else {
                // prod
                // 1. 분당
                ResultEntity deleteEndpointResultB = endpointService.delete(mlProdBUrl, endpointId);
                if (checkCreateEndpointResult(apiEntity, deployResult, deleteEndpointResultB)) return;

                // 2. 대전
                ResultEntity deleteEndpointResultD = endpointService.delete(mlProdDUrl, endpointId);
                if (checkCreateEndpointResult(apiEntity, deployResult, deleteEndpointResultD)) return;
            }


            // 4. API 삭제
            GwApiEntity gwApiEntity = apiEntityConverter.convert(apiEntity);

            if (profile == TB) {
                ResultEntity deleteApiResult = apiService.delete(profile, apiEntity.getId(), apiEntity.getVersion());
                if (checkCreateApiResult(apiEntity, deployResult, deleteApiResult)) return;
            } else {
                // prod
                // 1. 분당
                ResultEntity deleteApiResultB = apiService.delete(mlProdBUrl, apiEntity.getId(), apiEntity.getVersion());
                if (checkCreateApiResult(apiEntity, deployResult, deleteApiResultB)) return;

                // 2. 대전
                ResultEntity deleteApiResultD = apiService.delete(mlProdDUrl, apiEntity.getId(), apiEntity.getVersion());
                if (checkCreateApiResult(apiEntity, deployResult, deleteApiResultD)) return;
            }


            // 5. Endpoint 배포 (예외 발생 시 Rollback)
            // 6. API 배포 (예외 발생 시 Endpoint & API Rollback)


            for (DeployServer server : deployResult.getServers()) {
                try {
                    server.setStatus(JobStatus.DOING);
                    log.info("Deploy Endpoint & API, id={}, version={}, PL target={}", apiEntity.getId(), apiEntity.getVersion(), server.getServerName());
                    /* 2019-07-08 versioning 관련 수정 (허수영) */
                    ResultEntity plEndpointResult = endpointService.deploy(profile, server.getServerName(), endpointId);
                    if (plEndpointResult.getResult() == GWResultType.FAILURE) {
                        throw new DeploymentException(plEndpointResult.getErrorMessage());
                    }
                    /* 2019-07-08 versioning 관련 수정 (허수영) */
                    ResultEntity plDeployResult = apiService.deploy(profile, server.getServerName(), apiEntity.getId(), apiEntity.getVersion());
                    if (plDeployResult.getResult() == GWResultType.FAILURE) {
                        throw new DeploymentException(plDeployResult.getErrorMessage());
                    }
                    server.setStatus(JobStatus.DONE);
                } catch (Exception e) {
                    log.error("Exception, during Deploying, api id={}, version={}, PL={}, message={}", apiEntity.getId(),apiEntity.getVersion(), server.getServerName(), e.getMessage(), e);
                    server.setStatus(JobStatus.FAILURE);
                    deployResult.setStatus(DeployJobStatus.FAIL);
                    deployResult.setMessage("Exception, during deploying, apiId=%s, PL=%s, message=%s".formatted(
							apiEntity.getId(), server.getServerName(), e.getMessage()));
                    break;
                }
                deployJobMap.put(apiEntity.getApiNo(), deployResult);
            }

            if (deployResult.getStatus() != DeployJobStatus.FAIL) {
                for (DeployServer server : deployResult.getAlServers()) {
                    try {
                        server.setStatus(JobStatus.DOING);
                        log.info("Deploy API, id={}, AL target={}", apiEntity.getId(), server.getServerName());
                        ResultEntity alDeployResult = apiService.deployAL(profile, server.getServerName(), apiEntity.getId());
                        if (alDeployResult.getResult() == GWResultType.FAILURE) {
                            throw new DeploymentException(alDeployResult.getErrorMessage());
                        }
                        server.setStatus(JobStatus.DONE);
                    } catch (Exception e) {
                        log.error("Exception, during Deploying, api id={}. AL={}, message={}", apiEntity.getId(), server.getServerName(), e.getMessage(), e);
                        server.setStatus(JobStatus.FAILURE);
                        deployResult.setStatus(DeployJobStatus.FAIL);
                        deployResult.setMessage("Exception, during deploying, apiId=%s, PL=%s, message=%s".formatted(
								apiEntity.getId(), server.getServerName(), e.getMessage()));
                        break;
                    }
                    deployJobMap.put(apiEntity.getApiNo(), deployResult);
                }
            }

            if (deployResult.getStatus() != DeployJobStatus.FAIL) {
                deployResult.setStatus(DeployJobStatus.DONE);
                deployResult.setMessage("Success");
                deployJobMap.put(apiEntity.getApiNo(), deployResult);
                return;
            }

            log.info("Failure deploy, Start Rollback...");
            // rollback endpoint
            try {
                if (profile == TB) {
                    endpointService.create(profile, endpointBackup);
                } else {
                    // prod
                    // 1. 분당
                    endpointService.create(mlProdBUrl, endpointBackup);

                    // 2. 대전
                    endpointService.create(mlProdDUrl, endpointBackup);
                }
            } catch (Exception e) {
                log.error("Exception, during Rollback Endpoint, apiId={}, message={}", apiEntity.getId(), e.getMessage(), e);
            }
            // rollback api
            try {
                if (profile == TB) {
                    apiService.create(profile, gwApiBackup);
                } else {
                    // prod
                    // 1. 분당
                    apiService.create(mlProdBUrl, gwApiBackup);

                    // 2. 대전
                    apiService.create(mlProdDUrl, gwApiBackup);
                }
                apiService.create(profile, gwApiBackup);
            } catch (Exception e) {
                log.error("Exception, during Rollback API, apiId={}, message={}", apiEntity.getId(), e.getMessage(), e);
            }

            for (DeployServer server : deployResult.getServers()) {
                if (server.getStatus() == JobStatus.DONE || server.getStatus() == JobStatus.FAILURE) {
                    try {
                        log.info("Rollback Endpoint & API, id={}, PL target={}", apiEntity.getId(), server.getServerName());
                        /* 2019-07-08 versioning 관련 수정 (허수영) */
                        endpointService.deploy(profile, server.getServerName(), endpointId);

                        /* 2019-07-08 versioning 관련 수정 (허수영) */
                        apiService.deploy(profile, server.getServerName(), apiEntity.getId(), apiEntity.getVersion());
                    } catch (Exception e) {
                        log.error("Exception, during Rollback Deployed PL, api id={}. PL={}, message={}", e.getMessage(), e);
                    }
                }
            }

            for (DeployServer server : deployResult.getAlServers()) {
                if (server.getStatus() == JobStatus.DONE || server.getStatus() == JobStatus.FAILURE) {
                    try {
                        log.info("Rollback API, id={}, AL target={}", apiEntity.getId(), server.getServerName());
                        apiService.deployAL(profile, server.getServerName(), apiEntity.getId());
                    } catch (Exception e) {
                        log.error("Exception, during Rollback Deployed AL, api id={}. AL={}, message={}", e.getMessage(), e);
                    }
                }
            }

        } catch (Exception e) {
            log.error("Exception, during check validation, message={}", e.getMessage(), e);
            deployResult.setStatus(DeployJobStatus.FAIL);
            deployResult.setMessage("Exception, during check validation, message=" + e.getMessage());
            deployJobMap.put(apiEntity.getApiNo(), deployResult);
        }
        deployResult.setLastAccessTime(System.currentTimeMillis());
        deployJobMap.put(apiEntity.getApiNo(), deployResult);
    }

}
