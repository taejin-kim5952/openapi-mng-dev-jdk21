package com.kt.openapi.web.util;

import com.kt.openapi.web.cmmn.logutil.KsmLocalLogUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

@Component
public class ApiLinkLogUtil extends KsmLocalLogUtil {
	//--##private static final Logger LOGGER = LoggerFactory.getLogger(ApiLinkLogUtil.class);

	//-- properties {
	//-- logfile path
	public static String logFilePath;
	@Value("${apilink.log.filepath}")
	public void setLogFilePath(String logFilePath) {
		ApiLinkLogUtil.logFilePath = logFilePath;
	}
	//-- service name
	public static String logServiceName;
	@Value("${apilink.log.service.name}")
	public void setLogServiceName(String logServiceName) {
		ApiLinkLogUtil.logServiceName = logServiceName;
	}
	//-- service domain
	public static String logServiceDomain;
	@Value("${apilink.log.service.domain}")
	public void setLogServiceDomain(String logServiceDomain) {
		ApiLinkLogUtil.logServiceDomain = logServiceDomain;
	}
	//-- service domain
	public static String logCallerChannel;
	@Value("${apilink.log.caller.channel}")
	public void setLogCallerChannel(String logCallerChannel) {
		ApiLinkLogUtil.logCallerChannel = logCallerChannel;
	}
	//-- properties }

	//-- constructor {
	public ApiLinkLogUtil() {
		super();
	}

	public ApiLinkLogUtil(HttpServletRequest request, String group, String ifname, String userId, String transactionId) {
		super(request, group, ifname, userId, transactionId, ApiLinkLogUtil.logCallerChannel);

		//--properties {
		this.prop_logFilePath = KsmUtil.fnSafeStr(ApiLinkLogUtil.logFilePath);
		this.prop_logServiceName = KsmUtil.fnSafeStr(ApiLinkLogUtil.logServiceName);
		//--properties }
		this.logStandard.setService(this.prop_logServiceName);
		//--[tag:SR-20221222] {
		this.logStandard.setUrl(request.getRequestURL().toString());
		this.logStandard.setServiceDomain(ApiLinkLogUtil.logServiceDomain);
		//--[tag:SR-20221222] }
	}
	//-- constructor }

	//-- public method {
	@Override
	public String procLogstandard(int logGub, String payload, String destinationName, String destinationIp) {
		return super.procLogstandard(logGub, payload, this.getLogDataMap(destinationName, destinationIp));
	}

	@Override
	public String getLogfileName()  {
		//-- [tag:SR-20230419][i][LAMP로그 파일명 규칙변경]
		String hostName = KsmLocalLogUtil.getHostName("unknown");
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		return "%s-%s.log".formatted(hostName, dateFormat.format(new Date()));
		//--##return String.format("%s-%s.log", (this.is_secutiry_log ? "apilink_secu" : "apilink"), (new SimpleDateFormat("yyyyMMdd_HH")).format(new Date()));
	}
	//-- public method }

	private HashMap<String, String> getLogDataMap(String destinationName, String destinationIp) {
		HashMap<String, String> mapLogData = new HashMap<String, String>();

		boolean b_is_logdata = false;
		String inout = "in";
		String operation = "%s-%s".formatted(group, ifname);
		String destination_name = KsmUtil.fnSafeStr(destinationName);
		String destination_ip = KsmUtil.fnSafeStr(destinationIp);
		String security_type = "";
		String security_event = "";
		String security_target = "";
		String security_personalInfoList = "";

		if (true == "loginout".equals(this.group)) {
			if (true == "login".equals(this.ifname)) {
				b_is_logdata = true;
				security_type = KsmLocalLogUtil.DEF_SECURITY_TYPE_ACCESS;
				security_event = KsmLocalLogUtil.DEF_SECURITY_EVENT_LOGIN;
			}
			else if (true == "logout".equals(this.ifname)) {
				b_is_logdata = true;
				security_type = KsmLocalLogUtil.DEF_SECURITY_TYPE_ACCESS;
				security_event = KsmLocalLogUtil.DEF_SECURITY_EVENT_LOGOUT;
			}
		}
		else if (true == "instauth".equals(this.group)) {
			//-- shub instance등록 로그
			b_is_logdata = true;
			security_type = KsmLocalLogUtil.DEF_SECURITY_TYPE_ACCESS;
			security_event = KsmLocalLogUtil.DEF_SECURITY_EVENT_LOGIN;
		}
		else if (true == "sendmessage".equals(this.group)) {
			b_is_logdata = true;
		}

		if (inout.length() > 0) {
			mapLogData.put("inout", inout);
		}
		if (operation.length() > 0) {
			mapLogData.put("operation", operation);
		}
		if (true == b_is_logdata) {
			if (destination_name.length() > 0) {
				mapLogData.put("destination_name", destination_name);
			}
			if (destination_ip.length() > 0) {
				mapLogData.put("destination_ip", destination_ip);
			}
			if (security_type.length() > 0) {
				mapLogData.put("security_type", security_type);
			}
			if (security_event.length() > 0) {
				mapLogData.put("security_event", security_event);
			}
			if (security_target.length() > 0) {
				mapLogData.put("security_target", security_target);
			}
			if (security_personalInfoList.length() > 0) {
				mapLogData.put("security_personalInfoList", security_personalInfoList);
			}
		}
		return mapLogData;
	}
}
