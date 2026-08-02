package com.kt.openapi.web.cmmn.logutil;

import com.kt.openapi.web.adptran.util.KsmUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

@Component
public class ApilinkLogUtil extends KsmLocalLogUtil {
	//--##private static final Logger LOGGER = LoggerFactory.getLogger(ApiLinkLogUtil.class);

	public static String logFilePath;
	public static String logServiceName;
	public static String logServiceDomain;
	public static String logcenterChannel;
	@Value("${apimeneger.log.filepath}")
	public void setLogFilePath(String logFilePath) {
		ApilinkLogUtil.logFilePath = logFilePath;
	}
	//-- service name
	@Value("${apimeneger.log.service.name}")
	public void setLogServiceName(String logServiceName) {
		ApilinkLogUtil.logServiceName = logServiceName;
	}
	
	@Value("${apimeneger.log.service.domain}")
	public void setLogServiceDomain(String logServiceDomain) {
		ApilinkLogUtil.logServiceDomain = logServiceDomain;
	}
	
	@Value("${apimeneger.logcenter.channel}")
	public void setLogCenterChannel(String logCenterChannel) {
		ApilinkLogUtil.logcenterChannel = logCenterChannel;
	}
	
	//-- constructor {
	public ApilinkLogUtil() {
		super();		
	}

	public ApilinkLogUtil(HttpServletRequest request, String group, String ifname, String userId, String target, String transactionId,String event, String type, String userInfo) {
		super(request, group, ifname, userId, target, transactionId, event, type ,userInfo);

		//--properties {
		this.prop_logFilePath = KsmUtil.fnSafeStr(ApilinkLogUtil.logFilePath);
		this.prop_logServiceName = KsmUtil.fnSafeStr(ApilinkLogUtil.logServiceName);
		//--properties }
	}
	//-- constructor }

	//-- public method {
	@Override
	public String procLogstandard(int logGub, String payload, String destinationName, String destinationIp) {
		return super.procLogstandard(logGub, payload, this.getLogDataMap(destinationName, destinationIp));
	}

	@Override
	public String getLogfileName()  {
		//LAMP로그 파일명 규칙변경
		String hostName = KsmLocalLogUtil.getHostName("unknown");
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		return "%s-%s.log".formatted(hostName, dateFormat.format(new Date()));
		//return String.format("%s_%s.log", (this.is_secutiry_log ? "apilink_secu" : "apilink"), dateFormat.format(new Date()));
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

		b_is_logdata = true;

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
		}
		return mapLogData;
	}
}
