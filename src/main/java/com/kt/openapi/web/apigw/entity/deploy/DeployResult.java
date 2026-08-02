package com.kt.openapi.web.apigw.entity.deploy;

import com.kt.openapi.web.apigw.type.DeployJobStatus;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DeployResult implements Serializable {
	@Serial
	private static final long serialVersionUID = -7205163272684525897L;

    private DeployJobStatus status = DeployJobStatus.STANDBY;
    private String message;

    private List<DeployServer> servers = new ArrayList<>();
    private List<DeployServer> alServers = new ArrayList<>();

    private long lastAccessTime = System.currentTimeMillis();

    public DeployResult() {
    }

    public DeployResult(DeployJobStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public DeployJobStatus getStatus() {
        return status;
    }

    public void setStatus(DeployJobStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<DeployServer> getServers() {
        return servers;
    }

    public void setServers(List<DeployServer> servers) {
        this.servers = servers;
    }

    public long getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(long lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    public List<DeployServer> getAlServers() {
        return alServers;
    }

    public void setAlServers(List<DeployServer> alServers) {
        this.alServers = alServers;
    }
}
