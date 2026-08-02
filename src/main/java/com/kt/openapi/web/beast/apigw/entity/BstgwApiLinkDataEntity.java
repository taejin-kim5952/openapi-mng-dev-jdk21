package com.kt.openapi.web.beast.apigw.entity;

import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BstgwApiLinkDataEntity implements Serializable {
	private static final Logger LOGGER = LoggerFactory.getLogger(BstgwApiLinkDataEntity.class);

	@Serial
	private static final long serialVersionUID = -2689715968064504616L;

  public static final String con_PROCTYPE_DPLY = "dply";  //-- query를 통해 확인후 create/update를 수행
  public static final String con_PROCTYPE_CREATE = "create";
  public static final String con_PROCTYPE_UPDATE = "update";
  public static final String con_PROCTYPE_DELETE = "delete";

  public static final String con_TYPE_ROUTE = "ROUTE";
  public static final String con_TYPE_DOMAIN = "DOMAIN";
  public static final String con_TYPE_PARAM = "PARAM";
  public static final String con_TYPE_DATA = "DATA";

  //-- payload {
  @NotEmpty(message = "type should not be empty")
  private String type;  //-- 유형 // [ROUTE, DOMAIN, PARAM, DATA]
  
  @NotEmpty(message = "key should not be empty")
  private String key;   //-- 키
  
  @NotEmpty(message = "value should not be empty")
  private String value;   //-- 값
  
  @NotEmpty(message = "dplyDt should not be empty")
  private String dplyDt;	//-- 배포 일자 // yyyy-MM-ddTHH:mm:ss 
  //-- payload }

    
  public String getType() { return type; }
  public void setType(String type) { this.type = type; }
  public String getKey() { return key; }
  public void setKey(String key) { this.key = key; }
  public String getValue() { return value; }
  public void setValue(String value) { this.value = value; }
  public String getDplyDt() { return dplyDt; }
  public void setDplyDt(String dplyDt) { this.dplyDt = dplyDt; }

  // Object -> map for DB
  public static Map<String, Object> getSyncDbMap(BstgwApiLinkDataEntity bstgwApiLinkDataEntity) {
    Map<String, Object> map_out = new HashMap<String, Object>();
    ObjectMapper objectMapper = new ObjectMapper();

    if (null != bstgwApiLinkDataEntity) {
      map_out.put("aldtType", bstgwApiLinkDataEntity.getType());
      map_out.put("aldtKey", bstgwApiLinkDataEntity.getKey());
      map_out.put("aldtValue", bstgwApiLinkDataEntity.getValue());
      map_out.put("dplyDt", bstgwApiLinkDataEntity.getDplyDt());
    }

    return map_out;
  }

  //-- Object -> JSON converting
  public static String getJson(BstgwApiLinkDataEntity bstgwApiLinkDataEntity, String direct) {
    boolean b_is_exclude_null = (direct.indexOf(";exclude_null;") != -1);
    boolean b_is_pretty_json = (direct.indexOf(";pretty_json;") != -1);

    ObjectMapper objectMapper = new ObjectMapper();
    String json = "";
    try {
      if (b_is_exclude_null) {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
      }
      if (b_is_pretty_json) {
        json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(bstgwApiLinkDataEntity);
      }
      else {
        json = objectMapper.writeValueAsString(bstgwApiLinkDataEntity);
      }
    } catch (JsonProcessingException e) {
      LOGGER.error("[Exception: {}][e: {}][bstgwApiLinkDataEntity.getJson()]", e.getMessage(), e);
      json = null;
    }
    return json;
  }
    
  //-- JSON -> Object converting
  public static BstgwApiLinkDataEntity setFromJson(String json) {
    ObjectMapper objectMapper = new ObjectMapper();
    BstgwApiLinkDataEntity bstgwApiLinkDataEntity = null;
    try {
      bstgwApiLinkDataEntity = objectMapper.readValue(json, BstgwApiLinkDataEntity.class);
    } catch (IOException e) {
      LOGGER.error("[Exception: {}][e: {}][bstgwApiLinkDataEntity.setFromJson()]", e.getMessage(), e);
    }
    return bstgwApiLinkDataEntity;
  }
  
  //-- format DplyDt
  public static String fmtDplyDt() {
    return fmtDplyDt(new Date());
  }

  //-- format DplyDt
  public static String fmtDplyDt(Date dtNow) {
    dtNow = ((null == dtNow) ? new Date() : dtNow);
    return "%sT%s".formatted((new SimpleDateFormat("yyyy-MM-dd")).format(dtNow), (new SimpleDateFormat("HH:mm:ss")).format(dtNow));
  }
}
