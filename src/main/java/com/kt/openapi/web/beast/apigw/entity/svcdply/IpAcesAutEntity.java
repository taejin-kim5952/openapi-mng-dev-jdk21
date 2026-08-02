package com.kt.openapi.web.beast.apigw.entity.svcdply;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class IpAcesAutEntity implements Serializable {
	@Serial
	private static final long serialVersionUID = -8099970071646129211L;

	//-- [i]IP 접근 권한
	//-- payload {
    private List<String> alwdIp; //-- 허용 IP
    private List<String> blckIp; //-- 서비스 ID
	//-- payload }

	public List<String> getAlwdIp() { return alwdIp; }
	public void setAlwdIp(List<String> alwdIp) { this.alwdIp = alwdIp; }
	public List<String> getBlckIp() { return blckIp; }
	public void setBlckIp(List<String> blckIp) { this.blckIp = blckIp; }
}
