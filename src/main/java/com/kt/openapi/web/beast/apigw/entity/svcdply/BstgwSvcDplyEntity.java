package com.kt.openapi.web.beast.apigw.entity.svcdply;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.validation.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BstgwSvcDplyEntity implements Serializable {
  private static final Logger LOGGER = LoggerFactory.getLogger(BstgwSvcDplyEntity.class);

	@Serial
	private static final long serialVersionUID = 5053999887501044342L;

  //-- payload {
  @NotEmpty(message = "dplyDt should not be empty")
  private String dplyDt;  //-- 배포 일자 // yyyy-MM-ddTHH:mm:ss

  @NotEmpty(message = "dplyType should not be empty")
  private String dplyType;  //-- 배포 유형 // [DPLY, DEL]

  @NotEmpty(message = "svcId should not be empty")
  private String svcId;   //-- 서비스 ID

  @NotEmpty(message = "svcNm should not be empty")
  private String svcNm;   //-- 서비스 명

  @NotEmpty(message = "userNm should not be empty")
  private String userNm;    //-- 인증키

  @NotEmpty(message = "pw should not be empty")
  private String pw;  //-- 패스워드

  private SlaEntity sla;   //-- SLA
  private String svcStDt; //-- 서비스 시작 일자
  private String svcEndDt;    //-- 서비스 종료 일자
  private List<String> apiAut;  //-- API 권한
  private IpAcesAutEntity ipAcesAut;   //-- IP 접근권한
  private AtribEntity atrib;  //-- 속성
  //-- [tag:SR-20230113]
  private List<apiSlaEntity> apiSla;  //-- API별 SLA목록
  //-- [i][20240109]
  private Map<String, Object> svcOptn;	//-- 서비스 옵션
  private String apiDomainAcesAut;  //-- API 도메인 접근 권한 (Azure)
  private String allowHttp;  //-- HTTP 허용 여부
   //-- payload }

  public String getDplyDt() { return dplyDt; }
  public void setDplyDt(String dplyDt) { this.dplyDt = dplyDt; }
  public String getDplyType() { return dplyType; }
  public void setDplyType(String dplyType) { this.dplyType = dplyType; }
  public String getSvcId() { return svcId; }
  public void setSvcId(String svcId) { this.svcId = svcId; }
  public String getSvcNm() { return svcNm; }
  public void setSvcNm(String svcNm) { this.svcNm = svcNm; }
  public String getUserNm() { return userNm; }
  public void setUserNm(String userNm) { this.userNm = userNm; }
  public String getPw() { return pw; }
  public void setPw(String pw) { this.pw = pw; }
  public SlaEntity getSla() { return sla; }
  public void setSla(SlaEntity sla) { this.sla = sla; }
  public String getSvcStDt() { return svcStDt; }
  public void setSvcStDt(String svcStDt) { this.svcStDt = svcStDt; }
  public String getSvcEndDt() { return svcEndDt; }
  public void setSvcEndDt(String svcEndDt) { this.svcEndDt = svcEndDt; }
  public List<String> getApiAut() { return apiAut; }
  public void setApiAut(List<String> apiAut) { this.apiAut = apiAut; }
  public IpAcesAutEntity getIpAcesAut() { return ipAcesAut; }
  public void setIpAcesAut(IpAcesAutEntity ipAcesAut) { this.ipAcesAut = ipAcesAut; }
  public AtribEntity getAtrib() { return atrib; }
  public void setAtrib(AtribEntity atrib) { this.atrib = atrib; }
  public List<apiSlaEntity> getApiSla() { return apiSla; }
  public void setApiSla(List<apiSlaEntity> apiSla) { this.apiSla = apiSla; }
  public Map<String, Object> getSvcOptn() { return svcOptn; }
  public void setSvcOptn(Map<String, Object> svcOptn) { this.svcOptn = svcOptn; }
  public String getApiDomainAcesAut() { return apiDomainAcesAut; }
  public void setApiDomainAcesAut(String apiDomainAcesAut) { this.apiDomainAcesAut = apiDomainAcesAut; }
  public String getAllowHttp() { return allowHttp; }
  public void setAllowHttp(String allowHttp) { this.allowHttp = allowHttp; }
  // Object -> map for DB
  public static Map<String, Object> getSyncDbMap(BstgwSvcDplyEntity bstgwSvcDplyEntity) {
    Map<String, Object> map_out = new HashMap<String, Object>();
    ObjectMapper objectMapper = new ObjectMapper();
  
    if (null != bstgwSvcDplyEntity) {
      map_out.put("dplyDt", bstgwSvcDplyEntity.getDplyDt());
      map_out.put("dplyType", bstgwSvcDplyEntity.getDplyType());
      map_out.put("svcId", bstgwSvcDplyEntity.getSvcId());  // PK
      map_out.put("svcNm", bstgwSvcDplyEntity.getSvcNm());
      map_out.put("userNm", bstgwSvcDplyEntity.getUserNm());
      map_out.put("pw", bstgwSvcDplyEntity.getPw());
      SlaEntity slaEntity = bstgwSvcDplyEntity.getSla();  // object
      if (null != slaEntity) {
        map_out.put("slaSec", slaEntity.getSec());
        map_out.put("slaMin", slaEntity.getMin());
        map_out.put("slaHr", slaEntity.getHr());
        map_out.put("slaDay", slaEntity.getDay());
        map_out.put("slaMon", slaEntity.getMon());
      }
      map_out.put("svcStDt", bstgwSvcDplyEntity.getSvcStDt());
      map_out.put("svcEndDt", bstgwSvcDplyEntity.getSvcEndDt());
      map_out.put("apiDomainAcesAut", bstgwSvcDplyEntity.getApiDomainAcesAut());
      map_out.put("allowHttp", bstgwSvcDplyEntity.getAllowHttp());
      if (null != bstgwSvcDplyEntity.getApiAut()) {
        map_out.put("apiAut", String.join(";", bstgwSvcDplyEntity.getApiAut()));  //-- list
      }
      IpAcesAutEntity ipAcesAutEntity = bstgwSvcDplyEntity.getIpAcesAut();  // object
      if (null != ipAcesAutEntity) {
        if (null != ipAcesAutEntity.getAlwdIp()) {
          map_out.put("ipAcesAutAlwdIp", String.join(";", ipAcesAutEntity.getAlwdIp()));  //-- list
        }
        if (null != ipAcesAutEntity.getBlckIp()) {
          map_out.put("ipAcesAutBlckIp", String.join(";", ipAcesAutEntity.getBlckIp()));  //-- list
        }
      }
      AtribEntity atribEntity = bstgwSvcDplyEntity.getAtrib();  // object
      if (null != atribEntity) {
        map_out.put("atribCpId", atribEntity.getCpId());
        map_out.put("atribServiceId", atribEntity.getServiceId());
      }
      //--[i][not_used_yet]
      /*--
      List<apiSlaEntity> apiSla = bstgwSvcDplyEntity.getApiSla(); // object list
      if (null != apiSla) { }
			Map<String, Object> svcOptn = bstgwSvcDplyEntity.getSvcOptn();	// object 
			if (null != svcOptn) { }
			--*/
    }

    return map_out;
  }

  //-- Object -> JSON converting
  public static String getJson(BstgwSvcDplyEntity bstgwSvcDplyEntity, String direct) {
    boolean b_is_exclude_null = (direct.indexOf(";exclude_null;") != -1);
    boolean b_is_pretty_json = (direct.indexOf(";pretty_json;") != -1);

    ObjectMapper objectMapper = new ObjectMapper();
    String json = "";
    try {
      if (b_is_exclude_null) {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
      }
      if (b_is_pretty_json) {
        json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(bstgwSvcDplyEntity);
      }
      else {
        json = objectMapper.writeValueAsString(bstgwSvcDplyEntity);
      }
    } catch (JsonProcessingException e) {
      LOGGER.error("[Exception: {}][e: {}][BstgwSvcDplyEntity.getJson()]", e.getMessage(), e);
      json = null;
    }
    return json;
  }

  //-- JSON -> Object converting
  public static BstgwSvcDplyEntity setFromJson(String json) {
    ObjectMapper objectMapper = new ObjectMapper();
    BstgwSvcDplyEntity bstgwSvcDplyEntity = null;
    try {
      bstgwSvcDplyEntity = objectMapper.readValue(json, BstgwSvcDplyEntity.class);
    } catch (IOException e) {
      LOGGER.error("[Exception: {}][e: {}][BstgwSvcDplyEntity.setFromJson()]", e.getMessage(), e);
    }
    return bstgwSvcDplyEntity;
  }
}
