package com.kt.openapi.web.apigw.utils;

import com.kt.openapi.web.apigw.entity.api.manager.ApiEntity;
import com.kt.openapi.web.apigw.entity.deploy.DeployResult;
import com.kt.openapi.web.apigw.services.api.GwApiService;
import com.kt.openapi.web.apigw.type.GwProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

public class DeployingApiJob implements DeployingJob {
    public static final Logger log = LoggerFactory.getLogger(DeployingApiJob.class);

    private final String jobName;
    private final GwProfile profile;
    private final ApiEntity apiEntity;
    private final GwApiService gwApiService;
    private final DeployResult deployResult;

    public DeployingApiJob(GwProfile profile, ApiEntity apiEntity, GwApiService gwApiService, DeployResult deployResult) {
        this.profile = profile;
        this.apiEntity = apiEntity;
        this.jobName = apiEntity.getId();
        this.gwApiService = gwApiService;
        this.deployResult = deployResult;
    }
    @Override
    public DeployResult getResult() {
        return deployResult;
    }

    @Override
    public String jobName() {
        return jobName;
    }

    @Override
    public void run() {
      //-- [2023:codeeyes][empty_block issue]
    }
}
