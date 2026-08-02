package com.kt.openapi.web.apigw.utils;

import com.kt.openapi.web.apigw.entity.deploy.DeployResult;

public interface DeployingJob extends Runnable {
    DeployResult getResult();

    String jobName();
}
