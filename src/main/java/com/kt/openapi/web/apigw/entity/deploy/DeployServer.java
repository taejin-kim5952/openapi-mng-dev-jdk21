package com.kt.openapi.web.apigw.entity.deploy;

import com.kt.openapi.web.apigw.type.JobStatus;

import java.io.Serial;
import java.io.Serializable;

public class DeployServer implements Serializable {
	@Serial
	private static final long serialVersionUID = 588429405701085889L;

    private String serverName;
    private JobStatus status = JobStatus.STANDBY;
    
    public DeployServer() {
    }

    public DeployServer(String serverName, JobStatus status) {
        this.serverName = serverName;
        this.status = status;
    }

    public DeployServer(String serverName) {
        this.serverName = serverName;
    }


    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }
}
