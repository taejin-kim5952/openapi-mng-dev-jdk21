package com.kt.openapi.web.cmmn.logutil;

import com.kt.openapi.web.adptran.util.KsmUtil;
import jakarta.servlet.http.HttpServletRequest;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public abstract class KsmLocalLogUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(KsmLocalLogUtil.class);

	//--properties {
	protected String prop_logFilePath = "";
	protected String prop_logServiceName = "";
	//--properties }
	
	/* @formatter:off */
	public static final int DEF_LOG_REQ = 1;  //-- request
	public static final int DEF_LOG_RES = 2;  //-- response
	public static final int DEF_LOG_ERR = 3;  //-- error
	//-- logType
	protected static final String DEF_LOGTYPE_IN_REQ  = "IN_REQ";  //-- 현재 오퍼레이션에 요청이 들어온 경우 (호출 상단)
	protected static final String DEF_LOGTYPE_IN_RES  = "IN_RES";  //-- 들어온 요청에 대해서 응답을 보내는 경우 (응답전)
	protected static final String DEF_LOGTYPE_OUT_REQ = "OUT_REQ"; //-- 외부 서비스로 리퀘스트를 요청하는 경우
	protected static final String DEF_LOGTYPE_OUT_RES = "OUT_RES"; //-- 외부 서비스에서 리스폰스를 받은 경우
	protected static final String DEF_LOGTYPE_NOTIFY  = "NOTIFY";  //-- REQ,RES 쌍 없이, 에러나 알림용 로그를 발생시켜야 하는 경우 사용 별도의 트랜잭션 그룹핑 과정 없이 1개의 로그를 한 개의 트랜잭션으로 판단하여 처리하게 된다.
	protected static final String DEF_LOGTYPE_ASYNC   = "ASYNC";   //-- REQ,RES 쌍 없이, 비동기 요청, 혹은 배치작업하는 경우에 사용 별도의 트랜잭션 그룹핑 과정 없이 1개의 로그를 한 개의 트랜잭션으로 판단하여 처리하게 된다.
	protected static final String DEF_LOGTYPE_IN_MSG  = "IN_MSG";  //-- REQ, RES의 쌍을 한 개의 로그로 처리하는 경우
	protected static final String DEF_LOGTYPE_OUT_MSG = "OUT_MSG";
	//-- response-type
	protected static final String DEF_RESPONSE_TYPE_S = "S";	//-- System Error
	protected static final String DEF_RESPONSE_TYPE_E = "E";	//-- Business Error
	protected static final String DEF_RESPONSE_TYPE_I = "I";	//-- Information
	protected static final String DEF_RESPONSE_TYPE_W = "W";	//-- "NOTIFY" 의 경우 warning
	//-- security-type
	protected static final String DEF_SECURITY_TYPE_ACCESS = "ACCESS";  //-- 사용자접속로그
	protected static final String DEF_SECURITY_TYPE_PRCS   = "PRCS";    //-- 개인정보처리로그
	protected static final String DEF_SECURITY_TYPE_MNGT   = "MNGT";    //-- 개인정보취급자 관리로그
	//-- security-event
	protected static final String DEF_SECURITY_EVENT_LOGIN  = "LOGIN";  //-- 로그인
	protected static final String DEF_SECURITY_EVENT_LOGOUT = "LOGOUT"; //-- 로그아웃
	protected static final String DEF_SECURITY_EVENT_CREATE = "CREATE"; //-- 권한부여
	protected static final String DEF_SECURITY_EVENT_READ   = "READ";   //-- 개인정보조회
	protected static final String DEF_SECURITY_EVENT_UPDATE = "UPDATE"; //-- 개인정보변경/권한변경
	protected static final String DEF_SECURITY_EVENT_DELETE = "DELETE"; //-- 권한말소
	protected static final String DEF_SECURITY_EVENT_EXPORT = "EXPORT"; //-- 개인정보출력
	/* @formatter:on */

	//-- constructor {
	protected String group;
	protected String ifname;
	protected boolean is_secutiry_log;
	protected LogStandard logStandard;	
	protected String url;
	
	protected KsmLocalLogUtil() {
		
	}
	
	protected KsmLocalLogUtil(HttpServletRequest request, String group, String ifname, String userId, String transactionId, String callerChannel) {
		super();
		this.group = group;
		this.ifname = ifname;
		this.is_secutiry_log = false;

		this.logStandard = new LogStandard();
		this.logStandard.setTimestamp(KsmLocalLogUtil.getTimestampString());
		this.logStandard.setService(this.prop_logServiceName);
		this.logStandard.setTransactionId(transactionId);

		if (null != callerChannel) {
			logStandard.setCaller(new LOGSTANDARDCaller());
			logStandard.getCaller().setChannel(callerChannel);
			logStandard.getCaller().setChannelIp(KsmLocalLogUtil.getHostIp());
		}

		logStandard.setHost(new LOGSTANDARDHost());
		logStandard.getHost().setIp(KsmLocalLogUtil.getHostIp());
		logStandard.getHost().setName(KsmLocalLogUtil.getHostName("Unknown"));

		logStandard.setResponse(new LOGSTANDARDResponse());
		logStandard.getResponse().setType(KsmLocalLogUtil.DEF_RESPONSE_TYPE_I);

		logStandard.setUser(new LOGSTANDARDUser());
		logStandard.getUser().setId(userId);
		logStandard.getUser().setIp(KsmLocalLogUtil.getClientIp(request));
		logStandard.getUser().setType("user");	//-- user|admin
		logStandard.getUser().setAgent(KsmLocalLogUtil.getUserAgent(request));
	}
	//-- constructor }
	
	protected KsmLocalLogUtil(HttpServletRequest request, String group, String ifname, String userId, String target, String transactionId, String event, String type, String userInfo) {
		super();
		this.group = group;
		this.ifname = ifname;
		this.is_secutiry_log = false;

		this.logStandard = new LogStandard();
		this.logStandard.setTimestamp(KsmLocalLogUtil.getTimestampString());
		this.logStandard.setService(KsmUtil.fnSafeStr(ApilinkLogUtil.logServiceName));
		this.logStandard.setTransactionId(transactionId);
		this.logStandard.setUrl(request.getRequestURL().toString());
		this.logStandard.setServiceDomain(ApilinkLogUtil.logServiceDomain);
		logStandard.setHost(new LOGSTANDARDHost());
		logStandard.getHost().setIp(KsmLocalLogUtil.getHostIp());
		logStandard.getHost().setName(KsmLocalLogUtil.getHostName("Unknown"));

		logStandard.setResponse(new LOGSTANDARDResponse());
		logStandard.getResponse().setType(KsmLocalLogUtil.DEF_RESPONSE_TYPE_I);

		logStandard.setUser(new LOGSTANDARDUser());
		logStandard.getUser().setId(userId);
		logStandard.getUser().setIp(KsmLocalLogUtil.getClientIp(request));
		logStandard.getUser().setType("user");	//-- user|admin
		logStandard.getUser().setAgent(KsmLocalLogUtil.getUserAgent(request));
		
		
		logStandard.setCaller(new LOGSTANDARDCaller());
		logStandard.getCaller().setChannel(ApilinkLogUtil.logcenterChannel);
		logStandard.getCaller().setChannelIp(KsmLocalLogUtil.getHostIp());

		logStandard.setSecurity(new LOGSTANDARDSecurity());
		logStandard.getSecurity().setType(type); //"ACCESS"
		logStandard.getSecurity().setEvent(event); //처리 이벤트 "LOGIN"
		logStandard.getSecurity().setTarget(target); //식별 ID
		logStandard.getSecurity().setPersonalInfoList(userInfo);
		if("AUTH".equals(type)) { //핸드폰 또는 메일 인증 시
			logStandard.getSecurity().setDetail("문자발송");
		}

	}	

	//-- abstract method {
	public abstract String procLogstandard(int logGub, String payload, String destinationName, String destinationIp);
	public abstract String getLogfileName();
	//-- abstract method }

	//-- public method {
	public String procLogstandard(int logGub, String payload) {
		return this.procLogstandard(logGub, payload, "", "");
	}

	public void setUserType(String userType) {
		this.logStandard.getUser().setType(userType);
	}

	public void setBiztransactionId(String biztransactionId) {
		this.logStandard.setBizTransactionId(biztransactionId);
	}

	public void setCaller(String channel, String channelIp) {
		this.logStandard.setCaller(new LOGSTANDARDCaller());
		this.logStandard.getCaller().setChannel(channel);
		this.logStandard.getCaller().setChannelIp(channelIp);
	}

	//-- public method }
	
	public void setResponseCd(String errCd, String errDesc, String type) {
		LOGGER.info("setResponseCd errCd : {}, errDesc : {} , type : {}",errCd, errDesc, type);
		this.logStandard.getResponse().setCode(errCd);
		this.logStandard.getResponse().setDesc(errDesc);
		this.logStandard.getResponse().setType(type);
	}

	//-- protected method {
	protected String procLogstandard(int logGub, String payload, HashMap<String, String> mapLogData) {
		
		String logType = KsmLocalLogUtil.DEF_LOGTYPE_OUT_MSG;
		String response_type = KsmLocalLogUtil.DEF_RESPONSE_TYPE_I;
		String seq = "1";
		
		if (logGub == KsmLocalLogUtil.DEF_LOG_REQ) {
			//logType = ((true == "in".equalsIgnoreCase(inout)) ? KsmLocalLogUtil.DEF_LOGTYPE_IN_REQ : KsmLocalLogUtil.DEF_LOGTYPE_OUT_REQ);
			logType = KsmLocalLogUtil.DEF_LOGTYPE_IN_REQ ;
			seq = "1";
		}
		else if (logGub == KsmLocalLogUtil.DEF_LOG_RES) {
			//logType = ((true == "in".equalsIgnoreCase(inout)) ? KsmLocalLogUtil.DEF_LOGTYPE_IN_RES : KsmLocalLogUtil.DEF_LOGTYPE_OUT_RES);
			logType =  KsmLocalLogUtil.DEF_LOGTYPE_IN_RES ;
			seq = "9999";
			
		}
		else if (logGub == KsmLocalLogUtil.DEF_LOG_ERR) {
			logType = KsmLocalLogUtil.DEF_LOGTYPE_NOTIFY;
			response_type = KsmLocalLogUtil.DEF_RESPONSE_TYPE_S;
		}
		
		if(payload.equals("[login-err]")) {
			response_type = KsmLocalLogUtil.DEF_RESPONSE_TYPE_E;
		}
			
		this.logStandard.setLogType(logType);
		
		if (seq.length() > 0) {
			this.logStandard.setSeq(seq);
		}
		
		this.logStandard.getResponse().setType(response_type);
		this.logStandard.setPayload(payload);
		

		String operation = mapLogData.getOrDefault("operation", "");
		if (operation.length() > 0) {
			this.logStandard.setOperation(operation);
		}

		String security_type = mapLogData.getOrDefault("security_type", "");
		String security_event = mapLogData.getOrDefault("security_event", "");
		this.is_secutiry_log = ((security_type.length() > 0) || (security_event.length() > 0));
		if (true == this.is_secutiry_log) {
			this.is_secutiry_log = true;
			this.logStandard.setSecurity(new LOGSTANDARDSecurity());
			this.logStandard.getSecurity().setType(security_type);
			this.logStandard.getSecurity().setEvent(security_event);
		}

		JSONObject jso_log = JSONObject.fromObject(this.logStandard);
		String s_log = jso_log.toString();
		boolean bRet = this.writeLogFile(s_log);
		if (bRet == false) {
			LOGGER.error("[error: {}.{}()][ret: false][log: {}]", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), s_log);
		}

		return s_log;
	}
	//-- protected method }

	private boolean writeLogFile(String outStr) {
		boolean bRet = false;
	  FileWriter fw = null;
	  BufferedWriter bw = null;
	  PrintWriter out = null;
		try {
			String logFilePath = this.prop_logFilePath;
			String s_div = "";
			if (logFilePath.length() > 0) {
				logFilePath = logFilePath.replace("\\", File.separator);
				logFilePath = logFilePath.replace("/", File.separator);
				s_div = ((true ==  File.separator.equals(logFilePath.substring(logFilePath.length() - 1))) ? "" :  File.separator);
				File f = new File(logFilePath);
				if (!f.exists()) {
					LOGGER.error("[error: {}.{}()][logFilePath is not exists: {}]", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName(), logFilePath);
					return bRet;
				}
			}
			String filePath = "%s%s%s".formatted(logFilePath, s_div, this.getLogfileName());
			fw = new FileWriter(filePath, true); // true가 append 모드
			bw = new BufferedWriter(fw);
			//PrintWriter out = new PrintWriter(bw);
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath, true), "UTF-8")), true);
			out.println(outStr); // 파일에 출력
			bRet = true;
		} catch (IOException e) {
			LOGGER.error("[IOException: LogUtil.WriteFile()][e: {}]", e.getMessage());
	  } finally {
	    try {
	      //-- [2023:codeeyes][File 자원 해제 검사 필수 issue]
	      if (fw != null) { fw.close(); }
	      if (bw != null) { bw.close(); }
	      if (out != null) { out.close(); }
	    } catch (IOException e) {
	      LOGGER.error("[IOException while closing BufferedWriter: {}]", e.getMessage());
	    }
	  }
		return bRet;
	}

	public static String getTimestampString() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		return dateFormat.format(new Date());
	}

	public static String getClientIp(HttpServletRequest request) {
		String remoteAddr = "";
		if (request != null) {
			remoteAddr = request.getHeader("X-FORWARDED-FOR");
			if (remoteAddr == null || "".equals(remoteAddr)) {
				remoteAddr = request.getRemoteAddr();
			}
		}
		return remoteAddr;
	}

	public static String getHostIp() {
		String result = "";
		try {
			result = InetAddress.getLocalHost().getHostAddress();
			if (StringUtils.isNotEmpty(result)) {
				return result;
			}
		} catch (UnknownHostException e) {
			LOGGER.warn("[UnknownHostException: ApiLinkLogUtil.getHostIp()][e: {}]", e.getMessage());
		}
		return result;
	}

	public static String getHostName(String defHostName) {
		// try InetAddress.LocalHost first;
		// NOTE -- InetAddress.getLocalHost().getHostName() will not work in certain environments.
		try {
			String result = InetAddress.getLocalHost().getHostName();
			if (StringUtils.isNotEmpty(result)) {
				return result;
			}
		} catch (UnknownHostException e) {
			LOGGER.warn("[UnknownHostException: ApiLinkLogUtil.getHostName()][e: {}]", e.getMessage());
			// failed; try alternate means.
		}

		// try environment properties.
		//
		String host = System.getenv("COMPUTERNAME");
		if (host != null) {
			return host;
		}
		host = System.getenv("HOSTNAME");
		if (host != null) {
			return host;
		}

		// undetermined.
		return defHostName;
	}

	public static String getUserAgent(HttpServletRequest request) {
		return ((request != null) ? request.getHeader("User-Agent") : "");
	}
	
	
}
