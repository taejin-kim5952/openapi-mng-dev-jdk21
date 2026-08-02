package com.kt.openapi.web.beast.apigw.entity.apidply;

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

public class BstgwApiDplyEntity implements Serializable {
  private static final Logger LOGGER = LoggerFactory.getLogger(BstgwApiDplyEntity.class);

	@Serial
	private static final long serialVersionUID = -4253656506369251645L;

  //-- payload {
  @NotEmpty(message = "dplyDt should not be empty")
  private String dplyDt;  //-- 배포 일자 // yyyy-MM-ddTHH:mm:ss

  @NotEmpty(message = "dplyType should not be empty")
  private String dplyType;  //-- 배포 유형 // [DPLY, DEL]

  @NotEmpty(message = "sysId should not be empty")
  private String sysId;   //-- 시스템 ID

  @NotEmpty(message = "apiId should not be empty")
  private String apiId;   //-- API ID

  @NotEmpty(message = "ifNo should not be empty")
  private String ifNo;    //-- 인터페이스 번호

  private String ver; //-- 버전
  private List<String> meth;  //-- 메소드
  private String in;  //-- URI IN
  private String out; //-- URI OUT
  private List<String> reqHndlr;  //-- 요청 핸들러
  private List<String> resHndlr;  //-- 응답 핸들러
  private String errHndlr; //-- 에러 핸들러
  private Integer timeOut;    //-- 타임 아웃
  private Boolean prnts;  //-- 부모여부
  //-- [tag:SR-20230113]
  private List<String> prntsApiId;  //-- 부모 API ID
  private HndlrOptnEntity hndlrOptn;   //-- 핸들러 옵션
  private List<String> mask;  //-- 마스킹 대상
  private AtribEntity atrib;  //-- 속성
  //-- payload }

  public String getDplyDt() { return dplyDt; }
  public void setDplyDt(String dplyDt) { this.dplyDt = dplyDt; }
  public String getDplyType() { return dplyType; }
  public void setDplyType(String dplyType) { this.dplyType = dplyType; }
  public String getSysId() { return sysId; }
  public void setSysId(String sysId) { this.sysId = sysId; }
  public String getApiId() { return apiId; }
  public void setApiId(String apiId) { this.apiId = apiId; }
  public String getIfNo() { return ifNo; }
  public void setIfNo(String ifNo) { this.ifNo = ifNo; }
  public String getVer() { return ver; }
  public void setVer(String ver) { this.ver = ver; }
  public List<String> getMeth() { return meth; }
  public void setMeth(List<String> meth) { this.meth = meth; }
  public String getIn() { return in; }
  public void setIn(String in) { this.in = in; }
  public String getOut() { return out; }
  public void setOut(String out) { this.out = out; }
  public List<String> getReqHndlr() { return reqHndlr; }
  public void setReqHndlr(List<String> reqHndlr) { this.reqHndlr = reqHndlr; }
  public List<String> getResHndlr() { return resHndlr; }
  public void setResHndlr(List<String> resHndlr) { this.resHndlr = resHndlr; }
  public String getErrHndlr() { return errHndlr; }
  public void setErrHndlr(String errHndlr) { this.errHndlr = errHndlr; }
  public Integer getTimeOut() { return timeOut; }
  public void setTimeOut(Integer timeOut) { this.timeOut = timeOut; }
  public Boolean getPrnts() { return prnts; }
  public void setPrnts(Boolean prnts) { this.prnts = prnts; }
  public List<String> getPrntsApiId() { return prntsApiId; }
  public void setPrntsApiId(List<String> prntsApiId) { this.prntsApiId = prntsApiId; }
  public HndlrOptnEntity getHndlrOptn() { return hndlrOptn; }
  public void setHndlrOptn(HndlrOptnEntity hndlrOptn) { this.hndlrOptn = hndlrOptn; }
  public List<String> getMask() { return mask; }
  public void setMask(List<String> mask) { this.mask = mask; }
  public AtribEntity getAtrib() { return atrib; }
  public void setAtrib(AtribEntity atrib) { this.atrib = atrib; }

  // Object -> map for DB
  public static Map<String, Object> getSyncDbMap(BstgwApiDplyEntity bstgwApiDplyEntity) {
    Map<String, Object> map_out = new HashMap<String, Object>();
    ObjectMapper objectMapper = new ObjectMapper();

    if (null != bstgwApiDplyEntity) {
      map_out.put("dplyDt", bstgwApiDplyEntity.getDplyDt());
      map_out.put("dplyType", bstgwApiDplyEntity.getDplyType());
      map_out.put("apiId", bstgwApiDplyEntity.getApiId());  // PK
      map_out.put("sysId", bstgwApiDplyEntity.getSysId());
      map_out.put("ifNo", bstgwApiDplyEntity.getIfNo());
      map_out.put("ver", bstgwApiDplyEntity.getVer());
      if (null != bstgwApiDplyEntity.getMeth()) {
        map_out.put("meth", String.join(";", bstgwApiDplyEntity.getMeth()));  //-- list
      }
      map_out.put("uriIn", bstgwApiDplyEntity.getIn());
      map_out.put("uriOut", bstgwApiDplyEntity.getOut());
      if (null != bstgwApiDplyEntity.getReqHndlr()) {
        map_out.put("reqHndlr", String.join(";", bstgwApiDplyEntity.getReqHndlr()));  //-- list
      }
      if (null != bstgwApiDplyEntity.getResHndlr()) {
        map_out.put("resHndlr", String.join(";", bstgwApiDplyEntity.getResHndlr()));  //-- list
      }
      map_out.put("errHndlr", bstgwApiDplyEntity.getErrHndlr());
      map_out.put("timeOut", "%d".formatted(bstgwApiDplyEntity.getTimeOut()));
      if (null != bstgwApiDplyEntity.getPrnts()) {
        map_out.put("prnts", (bstgwApiDplyEntity.getPrnts() ? "true" : "false"));
      }
      //-- [tag:SR-20230113]
      //-- [not_used_yet]
      /*--
      if (null != bstgwApiDplyEntity.getPrntsApiId()) {
        map_out.put("prntsApiId", String.join(";", bstgwApiDplyEntity.getPrntsApiId()));  //-- list
      }
      --*/
      HndlrOptnEntity hndlrOptnEntity = bstgwApiDplyEntity.getHndlrOptn();  // object
      String json_hndlrOptn = null;
      if (null != hndlrOptnEntity) {
        try {
          json_hndlrOptn = objectMapper.writeValueAsString(hndlrOptnEntity);
          //-- [i][encode string]
          //--##JsonStringEncoder encoder = (JsonStringEncoder.getInstance());
          //--##json_hndlrOptn = new String(encoder.quoteAsString(json_hndlrOptn));
        } catch (JsonProcessingException e) {
          LOGGER.error("[Exception: {}][e: {}][BstgwApiDplyEntity.getSyncDbMap()][hndlrOptn]", e.getMessage(), e);
        }
      }
      map_out.put("hndlrOptn", json_hndlrOptn);
      if (null != bstgwApiDplyEntity.getMask()) {
        map_out.put("", String.join(";", bstgwApiDplyEntity.getMask()));    //-- list
      }
      AtribEntity atribEntity = bstgwApiDplyEntity.getAtrib();  // object
      if (null != atribEntity) {
        map_out.put("atribInFmt", atribEntity.getInFmt());
        map_out.put("atribOutFmt", atribEntity.getOutFmt());
        //-- [i][20231212][bugfix][atribInFmt -> atribInComnParam][atribOutFmt -> atribOutComnParam]
        map_out.put("atribInComnParam", atribEntity.getInComnParam());
        map_out.put("atribOutComnParam", atribEntity.getOutComnParam());
      }
    }

    return map_out;
  }

  //-- Object -> JSON converting
  public static String getJson(BstgwApiDplyEntity bstgwApiDplyEntity, String direct) {
    boolean b_is_exclude_null = (direct.indexOf(";exclude_null;") != -1);
    boolean b_is_pretty_json = (direct.indexOf(";pretty_json;") != -1);

    ObjectMapper objectMapper = new ObjectMapper();
    String json = "";
    try {
      if (b_is_exclude_null) {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
      }
      if (b_is_pretty_json) {
        json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(bstgwApiDplyEntity);
      }
      else {
        json = objectMapper.writeValueAsString(bstgwApiDplyEntity);
      }
    } catch (JsonProcessingException e) {
      LOGGER.error("[Exception: {}][e: {}][BstgwApiDplyEntity.getJson()]", e.getMessage(), e);
      json = null;
    }
    return json;
  }

  //-- JSON -> Object converting
  public static BstgwApiDplyEntity setFromJson(String json) {
    ObjectMapper objectMapper = new ObjectMapper();
    BstgwApiDplyEntity bstgwApiDplyEntity = null;
    try {
      bstgwApiDplyEntity = objectMapper.readValue(json, BstgwApiDplyEntity.class);
    } catch (IOException e) {
      LOGGER.error("[Exception: {}][e: {}][BstgwApiDplyEntity.setFromJson()]", e.getMessage(), e);
    }
    return bstgwApiDplyEntity;
  }
}
