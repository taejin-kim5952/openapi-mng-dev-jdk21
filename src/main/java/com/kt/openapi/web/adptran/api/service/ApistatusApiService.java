package com.kt.openapi.web.adptran.api.service;

import com.kt.openapi.web.adptran.api.AdptranApiResultCode;
import com.kt.openapi.web.adptran.api.common.domain.ResultMessage;
import com.kt.openapi.web.adptran.api.common.message.GenericMessage;
import com.kt.openapi.web.adptran.dao.ApistatusDAO;
import com.kt.openapi.web.adptran.util.KsmUtil;
import com.kt.openapi.web.adptran.vo.ApiStatusGroupVO;
import com.kt.openapi.web.adptran.vo.ApiStatusVO;
import com.kt.openapi.web.auth.vo.AuthVO;
import com.kt.openapi.web.userJoin.vo.UserJoinVO;
import jakarta.servlet.http.HttpServletRequest;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author senasa
 *
 */
@Service
public class ApistatusApiService {
	private static final Logger logger = LoggerFactory.getLogger(ApistatusApiService.class);

    @Autowired
    private ApistatusDAO apistatusDAO;
    
	public GenericMessage get_apistatus_api_data(String group, String ifname, Map<String, Object> param, HttpServletRequest request) {
		logger.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

		GenericMessage message = new GenericMessage();
		
		Object data = null;

		String req_dev_opt = (";" + KsmUtil.fnSafeStr(param.get("dev_opt")) + ";");

		if (req_dev_opt.indexOf(";use_static_data;") != -1) {
			return get_apistatus_api_data_demo(group, ifname, param);
		}

		if ((true == "group".equalsIgnoreCase(group)) && (true == "api_status_spc_group_list".equalsIgnoreCase(ifname))) {
			data = this.get_data_api_status_spc_group_list(param, request);
		}
		else if ((true == "group".equalsIgnoreCase(group)) && (true == "group_summary_list".equalsIgnoreCase(ifname))) {
			data = this.get_data_group_summary_list(param, request);
		}
		else if ((true == "group".equalsIgnoreCase(group)) && (true == "group_api_status_info".equalsIgnoreCase(ifname))) {
			data = this.get_data_group_api_status_info(param, request);
		}
		else if ((true == "group".equalsIgnoreCase(group)) && (true == "tran_status_spc_group_user_link".equalsIgnoreCase(ifname))) {
			return this.tran_status_spc_group_user_link(param, request);
		}
		else if ((true == "list".equalsIgnoreCase(group)) && (true == "api_status_info_list".equalsIgnoreCase(ifname))) {
			//-- param: tc, pz, pg + else
			return this.get_data_api_status_info_list(param, request);
		}
		else if ((true == "list".equalsIgnoreCase(group)) && (true == "api_status_info_daily_list".equalsIgnoreCase(ifname))) {
			//-- param: tc, pz, pg + else
			return this.get_data_api_status_info_daily_list(param, request);
		}
		else if ((true == "list".equalsIgnoreCase(group)) && (true == "api_status_check_hist_list".equalsIgnoreCase(ifname))) {
			data = this.get_data_api_status_check_hist_list(param, request);
		}
		else if ((true == "view".equalsIgnoreCase(group)) && (true == "api_status_check_hist".equalsIgnoreCase(ifname))) {
			data = this.get_data_api_status_check_hist(param, request);
		}
		else if ((true == "common".equalsIgnoreCase(group)) && (true == "api_system_spc_list".equalsIgnoreCase(ifname))) {
			data = this.get_data_common_api_system_spc_list(param, request);
		}
		else if ((true == "common".equalsIgnoreCase(group)) && (true == "api_status_spc_group_list".equalsIgnoreCase(ifname))) {
			data = this.get_data_common_api_status_spc_group_list(param, request);
		}

		message.setResultCode(AdptranApiResultCode.RC_200_SUCESS.getCode());
		message.setData(data);

		return message; 
	}

	private Object get_data_api_status_spc_group_list(Map<String, Object> param, HttpServletRequest request) {
		logger.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());
		//-- output
		List<Map<String, Object>> data_list = new ArrayList<>();

		//-- query-input
		Map<String, Object> map_in = this.get_common_query_map_in(request);

		//-- param
		String req_user_id = KsmUtil.fnSafeStr(param.get("user_id"));
		if (req_user_id.length() > 0) {
			map_in.put("userId", req_user_id);
		}

		//-- query-output
		List<ApiStatusGroupVO> listOut = apistatusDAO.select_API_STATUS_SPC_GROUP_LIST(map_in);
		//-- build-output
		for (ApiStatusGroupVO vo : listOut) {
			Map<String, Object> map_rec = new HashMap<>();
			map_rec.put("status_group_nm", vo.getStatusGroupNm()); 
			map_rec.put("status_group_no", vo.getStatusGroupNo());
			map_rec.put("sort_odrg", vo.getSortOdrg());
			
			int user_link_SEQ = KsmUtil.parseInt(vo.getUserLinkSeq(), -1);
			int user_link_SORT_ODRG = KsmUtil.parseInt(vo.getUserLinkSortOdrg(), 999);

			map_rec.put("user_link_yn", ((user_link_SEQ > 0) ? "Y" : "N"));
			map_rec.put("user_link_order", ((user_link_SEQ > 0) ? user_link_SORT_ODRG : null));

			data_list.add(map_rec);
		}

		return data_list;
	}

	private Object get_data_group_summary_list(Map<String, Object> param, HttpServletRequest request) {
		logger.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());
		//-- output
		List<Map<String, Object>> data_list = new ArrayList<>();
		//-- param
		String req_status_group_no_list = KsmUtil.fnSafeStr(param.get("status_group_no_list"));
		//-- query-input
		Map<String, Object> map_in = this.get_common_query_map_in(request);
		if (req_status_group_no_list.length() > 0) {
			String a_status_group_no_list[] = req_status_group_no_list.split(",");
			map_in.put("statusGroupNoList", a_status_group_no_list);
		}
		//-- query-output
		List<ApiStatusGroupVO> listOut = apistatusDAO.select_GROUP_SUMMARY_LIST(map_in);
		//-- build-output
		for (ApiStatusGroupVO vo : listOut) {
			int as_ok_count = KsmUtil.parseInt(vo.getAsOkCount(), 0);
			int as_status_count = KsmUtil.parseInt(vo.getAsStatusCount(), 0);
			int ok_rate = 0;
			if (as_status_count > 0) {
				ok_rate = (int)Math.round(((double)as_ok_count / (double)as_status_count) * 100);
			} 

			Map<String, Object> map_rec = new HashMap<>();
			map_rec.put("status_group_nm", vo.getStatusGroupNm()); 
			map_rec.put("status_group_no", vo.getStatusGroupNo()); 
			map_rec.put("status_ok_rate", "%d".formatted(ok_rate)); 

			data_list.add(map_rec);
		}

		return data_list;
	}

	private Object get_data_group_api_status_info(Map<String, Object> param, HttpServletRequest request) {
		logger.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());
		//-- output
		Map<String, Object> data_map = new HashMap<>();
		//-- param
		String req_status_group_no = KsmUtil.fnSafeStr(param.get("status_group_no"));
		//-- query-input
		Map<String, Object> map_in = this.get_common_query_map_in(request);
		map_in.put("statusGroupNo", req_status_group_no);
		//-- query-output
		ApiStatusGroupVO groupInfo = apistatusDAO.select_GROUP_SUMMARY_INFO(map_in);
		//-- build-output
		String STATUS_GROUP_NM = groupInfo.getStatusGroupNm();
		int as_ok_count = KsmUtil.parseInt(groupInfo.getAsOkCount(), 0);
		int as_dl_count = KsmUtil.parseInt(groupInfo.getAsDlCount(), 0);
		int as_nk_count = KsmUtil.parseInt(groupInfo.getAsNkCount(), 0);

		List<Map<String, Object>> list_api_status_info_list = new ArrayList<>();
		List<ApiStatusVO> listOut = apistatusDAO.select_GROUP_API_STATUS_LIST(map_in);
		for (ApiStatusVO vo : listOut) {
			Map<String, Object> map_rec = new HashMap<>();
			map_rec.put("sys_nm", vo.getSysNm()); 
			map_rec.put("api_spc_nm", vo.getAsApiSpcNm()); 
			map_rec.put("api_nm", vo.getApiNm()); 
			map_rec.put("api_no", vo.getApiNo()); 
			map_rec.put("status_check_dt", vo.getStatusCheckDt()); 
			map_rec.put("status_code", vo.getStatusCode()); 
			map_rec.put("status_res_msec", vo.getStatusResMsec()); 

			list_api_status_info_list.add(map_rec);
		}

		data_map.put("status_group_nm", STATUS_GROUP_NM); 
		data_map.put("ok_count", "%d".formatted(as_ok_count)); 
		data_map.put("dl_count", "%d".formatted(as_dl_count)); 
		data_map.put("nk_count", "%d".formatted(as_nk_count)); 
		data_map.put("api_status_info_list", list_api_status_info_list); 

		return data_map;
	}

	private GenericMessage tran_status_spc_group_user_link(Map<String, Object> param, HttpServletRequest request) {
		logger.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

		//-- output
		GenericMessage message = new GenericMessage();
		ResultMessage resultMessage = new ResultMessage(AdptranApiResultCode.RC_200_SUCESS.getCode(), "정보가 처리 되었습니다.");

		//-- param
		List<Map<String, Object>> list_api_status_spc_group_user_link_list = (ArrayList<Map<String, Object>>)param.get("api_status_spc_group_user_link_list");
		//-- query-input
		Map<String, Object> map_in = this.get_common_query_map_in(request);

		apistatusDAO.delete_API_STATUS_SPC_GROUP_USER_LINK(map_in);

		if (list_api_status_spc_group_user_link_list.size() > 0) {
			for (Map<String, Object> mapItem : list_api_status_spc_group_user_link_list) {
				mapItem.put("statusGroupNo", mapItem.get("status_group_no"));
				mapItem.put("sortOdrg", mapItem.get("sort_odrg"));

				mapItem.remove("status_group_no");
				mapItem.remove("sort_odrg");
			}
			map_in.put("apiStatusSpcGroupUserLinkList", list_api_status_spc_group_user_link_list); 

			int ret = apistatusDAO.insert_API_STATUS_SPC_GROUP_USER_LINK(map_in);
			if (ret <= 0){
				resultMessage = new ResultMessage(AdptranApiResultCode.BIZ_EXCEPTION.getCode(), "등록이 실패하였습니다.");
			}
		}
		message.setResultCode(AdptranApiResultCode.RC_200_SUCESS.getCode());
		message.setData(resultMessage);

		return message;
	}

	private GenericMessage get_data_api_status_info_list(Map<String, Object> param, HttpServletRequest request) {
		logger.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

		//-- output
		GenericMessage message = new GenericMessage();
		Map<String, Object> data_map = new HashMap<>();
		Integer totalCount = null;
		Integer pageSize = null;
		Integer currentPage = null;
		//-- param
		//-- pagination {
		int req_tc = Math.max(KsmUtil.parseInt(param.get("tc"), 0), 0);
		int req_pz = Math.max(KsmUtil.parseInt(param.get("pz"), 10), 1);
		int req_pg = Math.max(KsmUtil.parseInt(param.get("pg"), 1), 1);
		//-- pagination }
		String req_sys_id = KsmUtil.fnSafeStr(param.get("sys_id"));
		String req_api_spc_no = KsmUtil.fnSafeStr(param.get("api_spc_no"));
		String req_status_code = KsmUtil.fnSafeStr(param.get("status_code"));
		String req_api_nm = KsmUtil.fnSafeStr(param.get("api_nm"));
		String req_status_group_no = KsmUtil.fnSafeStr(param.get("status_group_no"));
		//-- query-input
		Map<String, Object> map_in = this.get_common_query_map_in(request);
		map_in.put("sysId", req_sys_id);
		map_in.put("apiSpcNo", req_api_spc_no);
		map_in.put("statusCode", req_status_code);
		map_in.put("apiNm", req_api_nm);
		map_in.put("statusGroupNo", req_status_group_no);

		String ok_count = "";
		String dl_count = "";
		String nk_count = "";

		//-- get count
		if (req_tc == 0) {
			int as_tot_count = 0;
			int as_ok_count = 0;
			int as_dl_count = 0;
			int as_nk_count = 0;
			List<ApiStatusVO> listOut = apistatusDAO.select_API_STATUS_INFO_LIST_STATUS_CODE_COUNT(map_in);
			for (ApiStatusVO vo : listOut) {
				String STATUS_CODE = vo.getStatusCode();
				int count = KsmUtil.parseInt(vo.getCount(), 0);
				as_tot_count += count;
				as_ok_count = ((true == "OK".equalsIgnoreCase(STATUS_CODE)) ? count : as_ok_count);
				as_dl_count = ((true == "DL".equalsIgnoreCase(STATUS_CODE)) ? count : as_dl_count);
				as_nk_count = ((true == "NK".equalsIgnoreCase(STATUS_CODE)) ? count : as_nk_count);
			}
			//-- status count를 검색한 경우에만 값을 설정
			ok_count = "%d".formatted(as_ok_count); 
			dl_count = "%d".formatted(as_dl_count); 
			nk_count = "%d".formatted(as_nk_count); 
			req_tc = as_tot_count;
		}
		List<Map<String, Object>> list_api_status_info_list = new ArrayList<>();
		if (req_tc > 0) {
			//-- query-output
			//-- pagination {
			int last_pg = (int)Math.max(Math.ceil((double)req_tc / (double)req_pz), 1);
			req_pg = Math.min(req_pg, last_pg);
			int first_index = ((req_pg - 1) * req_pz);
			int last_index = Math.min((req_pg * req_pz), ((req_tc > 0) ? req_tc : (req_pg * req_pz)));
			map_in.put("firstIndex", first_index);
			map_in.put("lastIndex", last_index);
			//-- pagination }
			List<ApiStatusVO> listOut = apistatusDAO.select_API_STATUS_INFO_LIST(map_in);
			//-- build-output
			for (ApiStatusVO vo : listOut) {
				Map<String, Object> map_rec = new HashMap<>();
				map_rec.put("sys_nm", vo.getSysNm()); 
				map_rec.put("api_spc_nm", vo.getAsApiSpcNm()); 
				map_rec.put("api_nm", vo.getApiNm()); 
				map_rec.put("api_no", vo.getApiNo()); 
				map_rec.put("status_check_dt", vo.getStatusCheckDt()); 
				map_rec.put("status_code", vo.getStatusCode()); 
				map_rec.put("status_res_msec", vo.getStatusResMsec()); 
	
				list_api_status_info_list.add(map_rec);
			}
		}
		//-- status count를 검색한 경우에만 값을 설정
		data_map.put("ok_count", ok_count); 
		data_map.put("dl_count", dl_count); 
		data_map.put("nk_count", nk_count); 
		data_map.put("api_status_info_list", list_api_status_info_list);
		
		totalCount = req_tc;
		pageSize = req_pz;
		currentPage = req_pg;

		message.setResultCode(AdptranApiResultCode.RC_200_SUCESS.getCode());
		message.setData(data_map);
		message.setTotalCount(totalCount);
		message.setPageSize(pageSize);
		message.setCurrentPage(currentPage);

		return message;		
	}

	private GenericMessage get_data_api_status_info_daily_list(Map<String, Object> param, HttpServletRequest request) {
		logger.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

		//-- output
		GenericMessage message = new GenericMessage();
		List<Map<String, Object>> data_list = new ArrayList<>();
		Integer totalCount = null;
		Integer pageSize = null;
		Integer currentPage = null;
		//-- param
		//-- pagination {
		int req_tc = Math.max(KsmUtil.parseInt(param.get("tc"), 0), 0);
		int req_pz = Math.max(KsmUtil.parseInt(param.get("pz"), 10), 1);
		int req_pg = Math.max(KsmUtil.parseInt(param.get("pg"), 1), 1);
		//-- pagination }
		String req_sys_id = KsmUtil.fnSafeStr(param.get("sys_id"));
		String req_api_spc_no = KsmUtil.fnSafeStr(param.get("api_spc_no"));
		String req_api_nm = KsmUtil.fnSafeStr(param.get("api_nm"));
		String req_status_group_no = KsmUtil.fnSafeStr(param.get("status_group_no"));
		String req_start_status_check_ymd = KsmUtil.fnSafeStr(param.get("start_status_check_ymd"));
		String req_end_status_check_ymd = KsmUtil.fnSafeStr(param.get("end_status_check_ymd"));
		//-- query-input
		Map<String, Object> map_in = this.get_common_query_map_in(request);
		map_in.put("sysId", req_sys_id);
		map_in.put("apiSpcNo", req_api_spc_no);
		map_in.put("apiNm", req_api_nm);
		map_in.put("statusGroupNo", req_status_group_no);
		map_in.put("startStatusCheckYmd", req_start_status_check_ymd);
		map_in.put("endStatusCheckYmd", req_end_status_check_ymd);

		//-- get count
		if (req_tc == 0) {
			req_tc = apistatusDAO.count_select_API_STATUS_CHECK_HIST_DAILY_LIST(map_in);
		}
		if (req_tc > 0) {
			//-- query-output
			//-- pagination {
			int last_pg = (int)Math.max(Math.ceil((double)req_tc / (double)req_pz), 1);
			req_pg = Math.min(req_pg, last_pg);
			int first_index = ((req_pg - 1) * req_pz);
			int last_index = Math.min((req_pg * req_pz), ((req_tc > 0) ? req_tc : (req_pg * req_pz)));
			map_in.put("firstIndex", first_index);
			map_in.put("lastIndex", last_index);
			//-- pagination }
			List<ApiStatusVO> list_rec = apistatusDAO.select_API_STATUS_CHECK_HIST_DAILY_LIST(map_in);
			//-- build-output
			for (ApiStatusVO vo : list_rec) {
				String daily_list = KsmUtil.fnSafeStr(vo.getDailyList());

				List<Map<String, Object>> list_daily_list = new ArrayList<>();

				//--[i] 각 record를 ';' 구분으로 concate
				String[] arr_daily_list = daily_list.split(";");
				for (int n_ii = 0; n_ii < arr_daily_list.length; n_ii++) {
					//--[i] 각 field를 '::' 구분으로 concate
					String[] arr_fdata = arr_daily_list[n_ii].split("::");
					if (arr_fdata.length == 3) {
						String STATUS_CHECK_DT = arr_fdata[0];
						String STATUS_CODE = arr_fdata[1];
						String STATUS_RES_MSEC = arr_fdata[2];

						Map<String, Object> map_rec = new HashMap<>();
						map_rec.put("status_check_dt", STATUS_CHECK_DT); 
						map_rec.put("status_code", STATUS_CODE); 
						map_rec.put("status_res_msec", STATUS_RES_MSEC);
						
						list_daily_list.add(map_rec);
					} 
				}
	
				Map<String, Object> map_rec = new HashMap<>();
				map_rec.put("sys_nm", vo.getSysNm()); 
				map_rec.put("api_spc_nm", vo.getAsApiSpcNm()); 
				map_rec.put("api_nm", vo.getApiNm()); 
				map_rec.put("api_no", vo.getApiNo()); 
				map_rec.put("daily_list", list_daily_list); 
	
				data_list.add(map_rec);
			}
			
		}
		
		totalCount = req_tc;
		pageSize = req_pz;
		currentPage = req_pg;

		message.setResultCode(AdptranApiResultCode.RC_200_SUCESS.getCode());
		message.setData(data_list);
		message.setTotalCount(totalCount);
		message.setPageSize(pageSize);
		message.setCurrentPage(currentPage);

		return message;		
	}

	private Object get_data_api_status_check_hist_list(Map<String, Object> param, HttpServletRequest request) {
		logger.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());
		//-- output
		List<Map<String, Object>> data_list = new ArrayList<>();
		//-- param
		String req_api_no = KsmUtil.fnSafeStr(param.get("api_no"));
		int req_status_check_dt_days = KsmUtil.parseInt(param.get("status_check_dt_days"), 0);
		//-- query-input
		Map<String, Object> map_in = this.get_common_query_map_in(request);
		map_in.put("apiNo", req_api_no);
		if (req_status_check_dt_days > 0) {
			map_in.put("statusCheckDtDays", req_status_check_dt_days);
		}
		//-- query-output
		List<ApiStatusVO> listOut = apistatusDAO.select_API_STATUS_CHECK_HIST_LIST(map_in);
		//-- build-output
		for (ApiStatusVO vo : listOut) {
			Map<String, Object> map_rec = new HashMap<>();
			map_rec.put("seq", vo.getSeq()); 
			map_rec.put("sys_nm", vo.getSysNm()); 
			map_rec.put("api_spc_nm", vo.getAsApiSpcNm()); 
			map_rec.put("api_nm", vo.getApiNm()); 
			map_rec.put("api_no", vo.getApiNo()); 
			map_rec.put("status_check_dt", vo.getStatusCheckDt()); 
			map_rec.put("status_code", vo.getStatusCode()); 
			map_rec.put("status_res_msec", vo.getStatusResMsec()); 

			data_list.add(map_rec);
		}

		return data_list;
	}

	private Object get_data_api_status_check_hist(Map<String, Object> param, HttpServletRequest request) {
		logger.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());
		//-- output
		Map<String, Object> data_map = new HashMap<>();
		//-- param
		String req_seq = KsmUtil.fnSafeStr(param.get("seq"));
		//-- query-input
		Map<String, Object> map_in = this.get_common_query_map_in(request);
		map_in.put("seq", req_seq);
		//-- query-output
		ApiStatusVO vo = apistatusDAO.select_API_STATUS_CHECK_HIST(map_in);
		//-- build-output
		data_map.put("seq", vo.getSeq()); 

		data_map.put("sys_nm", vo.getSysNm()); 
		data_map.put("api_spc_nm", vo.getAsApiSpcNm()); 
		data_map.put("api_nm", vo.getApiNm()); 
		data_map.put("api_no", vo.getApiNo()); 

		data_map.put("status_check_dt", vo.getStatusCheckDt()); 
		data_map.put("status_code", vo.getStatusCode()); 
		data_map.put("status_res_msec", vo.getStatusResMsec()); 

		data_map.put("st_time", vo.getStTime()); 
		data_map.put("end_time", vo.getEndTime()); 
		data_map.put("proc_result_cd", vo.getProcResultCd()); 
		data_map.put("proc_result_msg", vo.getProcResultMsg()); 

		data_map.put("req_api_url", vo.getReqApiUrl()); 
		data_map.put("req_headers", vo.getReqHeaders()); 
		data_map.put("req_body", vo.getReqBody()); 
		data_map.put("req_transaction_id", vo.getReqTransactionId()); 
		data_map.put("req_sequence_no", vo.getReqSequenceNo()); 

		data_map.put("res_transaction_id", vo.getResTransactionId()); 
		data_map.put("res_sequence_no", vo.getResSequenceNo()); 
		data_map.put("res_return_code", vo.getResReturnCode()); 
		data_map.put("res_return_description", vo.getResReturnDescription()); 
		data_map.put("res_error_code", vo.getResErrorCode()); 
		data_map.put("res_error_description", vo.getResErrorDescription()); 
		data_map.put("res_response", vo.getResResponse()); 

		return data_map;
	}

	private Object get_data_common_api_system_spc_list(Map<String, Object> param, HttpServletRequest request) {
		logger.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());
		//-- output
		List<Map<String, Object>> data_list = new ArrayList<>();
		//-- query-input
		Map<String, Object> map_in = this.get_common_query_map_in(request);
		//-- query-output
		List<ApiStatusVO> listOut = apistatusDAO.select_API_SYSTEM_SPC_LIST(map_in);
		//-- build-output
		for (ApiStatusVO vo : listOut) {
			Map<String, Object> map_rec = new HashMap<>();
			map_rec.put("sys_id", vo.getSysId()); 
			map_rec.put("sys_nm", vo.getSysNm()); 
			map_rec.put("api_spc_no", vo.getApiSpcNo()); 
			map_rec.put("api_spc_nm", vo.getApiNm()); 
			map_rec.put("api_spc_id", vo.getApiSpcId()); 
			map_rec.put("api_spc_ver", vo.getVer()); 

			data_list.add(map_rec);
		}

		return data_list;
	}

	private Object get_data_common_api_status_spc_group_list(Map<String, Object> param, HttpServletRequest request) {
		logger.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());
		//-- output
		List<Map<String, Object>> data_list = new ArrayList<>();
		//-- query-input
		Map<String, Object> map_in = this.get_common_query_map_in(request);
		//-- query-output
		List<ApiStatusGroupVO> listOut = apistatusDAO.select_API_STATUS_SPC_GROUP_LIST(map_in);
		//-- build-output
		for (ApiStatusGroupVO vo : listOut) {
			Map<String, Object> map_rec = new HashMap<>();
			map_rec.put("status_group_no", vo.getStatusGroupNo()); 
			map_rec.put("status_group_nm", vo.getStatusGroupNm()); 

			data_list.add(map_rec);
		}

		return data_list;
	}
	
	/**
	 * query-input을 위한 map을 생성
	 * (전역적 parameter설정을 위함)
	 */
	private Map<String, Object> get_common_query_map_in(HttpServletRequest request) {
		Map<String, Object> map_in = new HashMap<>();

		//-- 사용자권한설정 {		
		UserJoinVO userJVo = (UserJoinVO)request.getSession().getAttribute("ssUserVo");

		String enCmbrId = "";
		ArrayList<String> sysList = new ArrayList<String>();
		ArrayList<String> authList 	= new ArrayList<String>();
		if (userJVo != null) {
			enCmbrId = userJVo.getEnCmbrId();
			if (userJVo.getAuthList() != null) {
				for (AuthVO authVo : userJVo.getAuthList()) {
					sysList.add(authVo.getSysId());
					authList.add(authVo.getAutId());
				}
			}
		}
		
		map_in.put("userId", enCmbrId);
		map_in.put("userSysIdList", sysList);
		map_in.put("userAutIdList", authList);
		//-- 사용자권한설정 }

		return map_in;
	}

	//--[drm][dev] {
	private GenericMessage get_apistatus_api_data_demo(String group, String ifname, Map<String, Object> param) {
		GenericMessage message = new GenericMessage();
		
		Object data = null;
		Integer totalCount = null;
		Integer pageSize = null;
		Integer currentPage = null;

		//-- group/ifname별 처리 {
		String s_json = "";
		if (true == "group".equalsIgnoreCase(group)) {
			if (true == "api_status_spc_group_list".equalsIgnoreCase(ifname)) {
				s_json = String.join("", "["
					, "{'status_group_nm': 'KT ID 인증',            'status_group_no': '1', 'user_link_yn':'Y', 'user_link_order': 1 },"
					, "{'status_group_nm': '서비스 계약 조회',          'status_group_no': '2', 'user_link_yn':'N', 'user_link_order': null },"
					, "{'status_group_nm': '과금 서비스',             'status_group_no': '3', 'user_link_yn':'Y', 'user_link_order': 2 },"
					, "{'status_group_nm': '메세징 서비스',            'status_group_no': '4', 'user_link_yn':'N', 'user_link_order': null },"
					, "{'status_group_nm': '부가서비스 가입 모니터링',    'status_group_no': '5', 'user_link_yn':'Y', 'user_link_order': 3 },"
					, "{'status_group_nm': '#2-KT ID 인증',         'status_group_no': '6', 'user_link_yn':'N', 'user_link_order': null },"
					, "{'status_group_nm': '#2-서비스 계약 조회',       'status_group_no': '7', 'user_link_yn':'Y', 'user_link_order': 4 },"
					, "{'status_group_nm': '#2-과금 서비스',          'status_group_no': '8', 'user_link_yn':'N', 'user_link_order': null },"
					, "{'status_group_nm': '#2-메세징 서비스',         'status_group_no': '9', 'user_link_yn':'Y', 'user_link_order': 5 },"
					, "{'status_group_nm': '#2-부가서비스 가입 모니터링', 'status_group_no': '10', 'user_link_yn':'N', 'user_link_order': null },"
					, "]");
				data = JSONArray.fromObject(s_json);
			}
			else if (true == "group_summary_list".equalsIgnoreCase(ifname)) {
				s_json = String.join("", "["
					, "{'status_group_nm': 'KT ID 인증',         'status_group_no': '1', 'status_ok_rate':'100'},"
					, "{'status_group_nm': '서비스 계약 조회',       'status_group_no': '2', 'status_ok_rate':'99'},"
					, "{'status_group_nm': '과금 서비스',          'status_group_no': '3', 'status_ok_rate':'88'},"
					, "{'status_group_nm': '메세징 서비스',         'status_group_no': '4', 'status_ok_rate':'77'},"
					, "{'status_group_nm': '부가서비스 가입 모니터링', 'status_group_no': '5', 'status_ok_rate':'66'},"
					, "]");
				data = JSONArray.fromObject(s_json);
			}
			else if (true == "group_api_status_info".equalsIgnoreCase(ifname)) {
				String req_status_group_no = KsmUtil.fnSafeStr(param.get("status_group_no"));
				
				if (true == "1".equalsIgnoreCase(req_status_group_no)) {
					s_json = String.join("", "{"
						, "'status_group_nm': 'KT ID 인증', 'ok_count': '100', 'dl_count': '0', 'nk_count': '0',"
						, "'api_status_info_list': ["
						, "{'sys_nm': 'User Authentication', 'api_spc_nm': 'SHUB 인증API', 'api_nm': 'SHUB_GetUserListAPI_1', 'api_no': '11', 'status_check_dt': '2019-01-01 12:13:14', 'status_code': 'OK', 'status_res_msec': '11',},"
						, "{'sys_nm': 'User Authentication', 'api_spc_nm': 'SHUB 인증API', 'api_nm': 'SHUB_GetUserListAPI_2', 'api_no': '12', 'status_check_dt': '2019-01-01 12:13:14', 'status_code': 'OK', 'status_res_msec': '12',},"
						, "{'sys_nm': 'User Authentication', 'api_spc_nm': 'SHUB 인증API', 'api_nm': 'SHUB_GetUserListAPI_3', 'api_no': '13', 'status_check_dt': '2019-01-01 12:13:14', 'status_code': 'OK', 'status_res_msec': '13',},"
						, "{'sys_nm': 'User Authentication', 'api_spc_nm': 'SHUB 인증API', 'api_nm': 'SHUB_GetUserListAPI_4', 'api_no': '14', 'status_check_dt': '2019-01-01 12:13:14', 'status_code': 'OK', 'status_res_msec': '14',},"
						, "{'sys_nm': 'User Authentication', 'api_spc_nm': 'SHUB 인증API', 'api_nm': 'SHUB_GetUserListAPI_5', 'api_no': '15', 'status_check_dt': '2019-01-01 12:13:14', 'status_code': 'OK', 'status_res_msec': '15',},"
						, "],"
					, "}");
				}
				else if (true == "2".equalsIgnoreCase(req_status_group_no)) {
					s_json = String.join("", "{"
						, "'status_group_nm': '과금 서비스', 'ok_count': '1234', 'dl_count': '1234', 'nk_count': '1234',"
						, "'api_status_info_list': ["
						, "{'sys_nm': 'IoTMakers', 'api_spc_nm': 'IoT Makers 인증API', 'api_nm': 'IoT_GetUserListAPI_1', 'api_no': '21', 'status_check_dt': '2019-01-02 12:13:14', 'status_code': 'NK', 'status_res_msec': '',},"
						, "{'sys_nm': 'IoTMakers', 'api_spc_nm': 'IoT Makers 인증API', 'api_nm': 'IoT_GetUserListAPI_2', 'api_no': '22', 'status_check_dt': '2019-01-03 12:13:14', 'status_code': 'NK', 'status_res_msec': '',},"
						, "{'sys_nm': 'IoTMakers', 'api_spc_nm': 'IoT Makers 인증API', 'api_nm': 'IoT_GetUserListAPI_3', 'api_no': '23', 'status_check_dt': '2019-01-04 12:13:14', 'status_code': 'DL', 'status_res_msec': '1234',},"
						, "{'sys_nm': 'IoTMakers', 'api_spc_nm': 'IoT Makers 인증API', 'api_nm': 'IoT_GetUserListAPI_4', 'api_no': '24', 'status_check_dt': '2019-01-05 12:13:14', 'status_code': 'OK', 'status_res_msec': '123',},"
						, "{'sys_nm': 'IoTMakers', 'api_spc_nm': 'IoT Makers 인증API', 'api_nm': 'IoT_GetUserListAPI_5', 'api_no': '25', 'status_check_dt': '2019-01-06 12:13:14', 'status_code': 'OK', 'status_res_msec': '234',},"
						, "],"
					, "}");
				}
				else if (true == "3".equalsIgnoreCase(req_status_group_no)) {
					s_json = String.join("", "{"
						, "'status_group_nm': '메세징 서비스', 'ok_count': '43210', 'dl_count': '4321', 'nk_count': '432',"
						, "'api_status_info_list': ["
						, "{'sys_nm': 'GiGA Genie', 'api_spc_nm': 'Giga Genie 인증API', 'api_nm': 'GG_GetUserListAPI_1', 'api_no': '31', 'status_check_dt': '2019-02-01 12:13:14', 'status_code': 'DL', 'status_res_msec': '12345',},"
						, "{'sys_nm': 'GiGA Genie', 'api_spc_nm': 'Giga Genie 인증API', 'api_nm': 'GG_GetUserListAPI_2', 'api_no': '32', 'status_check_dt': '2019-02-01 12:13:14', 'status_code': 'DL', 'status_res_msec': '1234',},"
						, "{'sys_nm': 'GiGA Genie', 'api_spc_nm': 'Giga Genie 인증API', 'api_nm': 'GG_GetUserListAPI_3', 'api_no': '33', 'status_check_dt': '2019-02-01 12:13:14', 'status_code': 'DL', 'status_res_msec': '123',},"
						, "{'sys_nm': 'GiGA Genie', 'api_spc_nm': 'Giga Genie 인증API', 'api_nm': 'GG_GetUserListAPI_4', 'api_no': '34', 'status_check_dt': '2019-02-01 12:13:14', 'status_code': 'DL', 'status_res_msec': '12',},"
						, "{'sys_nm': 'GiGA Genie', 'api_spc_nm': 'Giga Genie 인증API', 'api_nm': 'GG_GetUserListAPI_5', 'api_no': '35', 'status_check_dt': '2019-02-01 12:13:14', 'status_code': 'DL', 'status_res_msec': '1',},"
						, "],"
					, "}");
				}
				else if (true == "4".equalsIgnoreCase(req_status_group_no)) {
					s_json = String.join("", "{"
						, "'status_group_nm': '부가서비스 가입 모니터링', 'ok_count': '2468', 'dl_count': '1', 'nk_count': '2',"
						, "'api_status_info_list': ["
						, "{'sys_nm': 'KOS', 'api_spc_nm': 'KOS-MOS 인증API', 'api_nm': 'KOSMOS_GetUserListAPI_1', 'api_no': '41', 'status_check_dt': '2019-03-01 12:13:14', 'status_code': 'NK', 'status_res_msec': '',},"
						, "{'sys_nm': 'KOS', 'api_spc_nm': 'KOS-MOS 인증API', 'api_nm': 'KOSMOS_GetUserListAPI_2', 'api_no': '42', 'status_check_dt': '2019-03-01 12:13:14', 'status_code': 'NK', 'status_res_msec': '',},"
						, "{'sys_nm': 'KOS', 'api_spc_nm': 'KOS-MOS 인증API', 'api_nm': 'KOSMOS_GetUserListAPI_3', 'api_no': '43', 'status_check_dt': '2019-03-01 12:13:14', 'status_code': 'NK', 'status_res_msec': '',},"
						, "{'sys_nm': 'KOS', 'api_spc_nm': 'KOS-MOS 인증API', 'api_nm': 'KOSMOS_GetUserListAPI_4', 'api_no': '44', 'status_check_dt': '2019-03-01 12:13:14', 'status_code': 'NK', 'status_res_msec': '',},"
						, "{'sys_nm': 'KOS', 'api_spc_nm': 'KOS-MOS 인증API', 'api_nm': 'KOSMOS_GetUserListAPI_5', 'api_no': '45', 'status_check_dt': '2019-03-01 12:13:14', 'status_code': 'NK', 'status_res_msec': '',},"
						, "],"
					, "}");
				}
				else if (true == "5".equalsIgnoreCase(req_status_group_no)) {
					s_json = String.join("", "{"
						, "'status_group_nm': '서비스 계약 조회', 'ok_count': '100', 'dl_count': '10', 'nk_count': '20',"
						, "'api_status_info_list': ["
						, "{'sys_nm': 'User Authentication', 'api_spc_nm': 'SHUB 인증API',       'api_nm': 'SHUB_GetUserListAPI',   'api_no': '11', 'status_check_dt': '2019-09-19 12:13:14', 'status_code': 'NK', 'status_res_msec': '',},"
						, "{'sys_nm': 'IoTMakers',           'api_spc_nm': 'IoT makers 인증API', 'api_nm': 'IoT_GetUserListAPI',    'api_no': '21', 'status_check_dt': '2019-09-19 12:13:14', 'status_code': 'DL', 'status_res_msec': '54321',},"
						, "{'sys_nm': 'GiGA Genie',          'api_spc_nm': 'Giga Genie 인증API', 'api_nm': 'GG_BC_GetUserListAPI',  'api_no': '31', 'status_check_dt': '2019-09-19 12:13:14', 'status_code': 'OK', 'status_res_msec': '34',},"
						, "{'sys_nm': 'KOS',                 'api_spc_nm': 'KOS-MOS 인증API',    'api_nm': 'KOSMOS_GetUserListAPI', 'api_no': '41', 'status_check_dt': '2019-09-19 12:13:14', 'status_code': 'OK', 'status_res_msec': '56',},"
						, "{'sys_nm': 'KT BaaS',             'api_spc_nm': 'BlockChain 인증API', 'api_nm': 'BC_GetUserListAPI',     'api_no': '51', 'status_check_dt': '2019-09-19 12:13:14', 'status_code': 'OK', 'status_res_msec': '78',},"
						, "],"
					, "}");
				}
				data = JSONObject.fromObject(s_json);
			}
		}
		else if (true == "list".equalsIgnoreCase(group)) {
			if (true == "api_status_info_list".equalsIgnoreCase(ifname)) {
				s_json = String.join("", "["
					, "{'sys_nm': 'KOS',                    'api_spc_nm': 'KOS-RDS[A-H]',        'api_nm': 'authenticateByDN',                         'api_no': '80001', 'status_check_dt': '2019-09-20 12:34:56', 'status_code': 'OK', 'status_res_msec': '123'  },"
					, "{'sys_nm': 'KOS',                    'api_spc_nm': 'KOS-RDS[A-H]',        'api_nm': 'CondByHndsetOrrgRetvSO',                   'api_no': '80002', 'status_check_dt': '2019-09-21 12:34:56', 'status_code': 'OK', 'status_res_msec': '231'  },"
					, "{'sys_nm': 'KOS',                    'api_spc_nm': 'KOS-RDS[A-H]',        'api_nm': 'SpecRetvSO',                               'api_no': '80003', 'status_check_dt': '2019-09-22 12:34:56', 'status_code': 'DL', 'status_res_msec': '12345'},"
					, "{'sys_nm': 'KOS',                    'api_spc_nm': 'KOS-RDS[A-H]',        'api_nm': 'KtsIcgBfacGdncSO',                         'api_no': '80004', 'status_check_dt': '2019-09-23 12:34:56', 'status_code': 'OK', 'status_res_msec': '12'   },"
					, "{'sys_nm': 'KOS',                    'api_spc_nm': 'KOS-RDS[A-H]',        'api_nm': 'getCcomDevBas',                            'api_no': '80005', 'status_check_dt': '2019-09-24 12:34:56', 'status_code': 'NK', 'status_res_msec': '0'    },"
					, "{'sys_nm': 'Application Service',    'api_spc_nm': '위치 서비스',         'api_nm': 'LocationFindPosition',                     'api_no': '80006', 'status_check_dt': '2019-09-20 12:34:56', 'status_code': 'NK', 'status_res_msec': '0'    },"
					, "{'sys_nm': 'Application Service',    'api_spc_nm': '위치 서비스',         'api_nm': 'LocationListTrigger',                      'api_no': '80007', 'status_check_dt': '2019-09-21 12:34:56', 'status_code': 'OK', 'status_res_msec': '413'  },"
					, "{'sys_nm': 'Application Service',    'api_spc_nm': '위치 서비스',         'api_nm': 'convertCoord',                             'api_no': '80008', 'status_check_dt': '2019-09-22 12:34:56', 'status_code': 'OK', 'status_res_msec': '324'  },"
					, "{'sys_nm': 'Application Service',    'api_spc_nm': '위치 서비스',         'api_nm': 'LocationJoinInfoLostCp',                   'api_no': '80009', 'status_check_dt': '2019-09-23 12:34:56', 'status_code': 'NK', 'status_res_msec': '0'    },"
					, "{'sys_nm': 'Application Service',    'api_spc_nm': '위치 서비스',         'api_nm': 'LocationJoinInfoMNGCp',                    'api_no': '80010', 'status_check_dt': '2019-09-24 12:34:56', 'status_code': 'OK', 'status_res_msec': '435'  },"
					, "{'sys_nm': 'User Authentication',    'api_spc_nm': 'KT ID 인증',          'api_nm': 'authenticateByCredentialId',               'api_no': '80011', 'status_check_dt': '2019-09-20 12:34:56', 'status_code': 'OK', 'status_res_msec': '234'  },"
					, "{'sys_nm': 'User Authentication',    'api_spc_nm': 'KT ID 인증',          'api_nm': 'getAgreementListForUser',                  'api_no': '80012', 'status_check_dt': '2019-09-21 12:34:56', 'status_code': 'OK', 'status_res_msec': '512'  },"
					, "{'sys_nm': 'User Authentication',    'api_spc_nm': 'KT ID 인증',          'api_nm': 'authenticateUserByIdPwdForOlleh',          'api_no': '80013', 'status_check_dt': '2019-09-22 12:34:56', 'status_code': 'DL', 'status_res_msec': '10234'},"
					, "{'sys_nm': 'User Authentication',    'api_spc_nm': 'KT ID 인증',          'api_nm': 'getCustomerIdentificationInfo',            'api_no': '80014', 'status_check_dt': '2019-09-23 12:34:56', 'status_code': 'OK', 'status_res_msec': '34'   },"
					, "{'sys_nm': 'User Authentication',    'api_spc_nm': 'KT ID 인증',          'api_nm': 'validateToken',                            'api_no': '80015', 'status_check_dt': '2019-09-24 12:34:56', 'status_code': 'NK', 'status_res_msec': '0'    },"
					, "{'sys_nm': 'KT BaaS',                'api_spc_nm': 'KT Smart Contract',   'api_nm': 'MessageMTOTSMSNetChargeOpen',              'api_no': '80016', 'status_check_dt': '2019-09-20 12:34:56', 'status_code': 'NK', 'status_res_msec': '0'    },"
					, "{'sys_nm': 'KT BaaS',                'api_spc_nm': 'KT Smart Contract',   'api_nm': 'MessageSendSMSReportNoNetCharge',          'api_no': '80017', 'status_check_dt': '2019-09-21 12:34:56', 'status_code': 'OK', 'status_res_msec': '231'  },"
					, "{'sys_nm': 'KT BaaS',                'api_spc_nm': 'KT Smart Contract',   'api_nm': 'MessageConfirm',                           'api_no': '80018', 'status_check_dt': '2019-09-22 12:34:56', 'status_code': 'OK', 'status_res_msec': '523'  },"
					, "{'sys_nm': 'KT BaaS',                'api_spc_nm': 'KT Smart Contract',   'api_nm': 'LocationFindPosition',                     'api_no': '80019', 'status_check_dt': '2019-09-23 12:34:56', 'status_code': 'NK', 'status_res_msec': '0'    },"
					, "{'sys_nm': 'KT BaaS',                'api_spc_nm': 'KT Smart Contract',   'api_nm': 'LocationListTrigger',                      'api_no': '80020', 'status_check_dt': '2019-09-24 12:34:56', 'status_code': 'OK', 'status_res_msec': '413'  },"
					, "{'sys_nm': 'GiGA Genie',             'api_spc_nm': 'AI MAKERS KIT',       'api_nm': 'getGeocodeByAddr',                         'api_no': '80021', 'status_check_dt': '2019-09-20 12:34:56', 'status_code': 'OK', 'status_res_msec': '123'  },"
					, "{'sys_nm': 'GiGA Genie',             'api_spc_nm': 'AI MAKERS KIT',       'api_nm': 'addrStepSearch',                           'api_no': '80022', 'status_check_dt': '2019-09-21 12:34:56', 'status_code': 'OK', 'status_res_msec': '231'  },"
					, "{'sys_nm': 'GiGA Genie',             'api_spc_nm': 'AI MAKERS KIT',       'api_nm': 'localSearch',                              'api_no': '80023', 'status_check_dt': '2019-09-22 12:34:56', 'status_code': 'DL', 'status_res_msec': '12345'},"
					, "{'sys_nm': 'GiGA Genie',             'api_spc_nm': 'AI MAKERS KIT',       'api_nm': 'getPoiWithName',                           'api_no': '80024', 'status_check_dt': '2019-09-23 12:34:56', 'status_code': 'OK', 'status_res_msec': '12'   },"
					, "{'sys_nm': 'GiGA Genie',             'api_spc_nm': 'AI MAKERS KIT',       'api_nm': 'geocode ',                                 'api_no': '80025', 'status_check_dt': '2019-09-24 12:34:56', 'status_code': 'NK', 'status_res_msec': '0'    },"
					, "{'sys_nm': 'Service Authentication', 'api_spc_nm': '부가서비스 가입',     'api_nm': 'getSubscriptionStatusByIdPwd',             'api_no': '80026', 'status_check_dt': '2019-09-20 12:34:56', 'status_code': 'NK', 'status_res_msec': '0'    },"
					, "{'sys_nm': 'Service Authentication', 'api_spc_nm': '부가서비스 가입',     'api_nm': 'checkUserInfoAndDeviceInfo',               'api_no': '80027', 'status_check_dt': '2019-09-21 12:34:56', 'status_code': 'OK', 'status_res_msec': '413'  },"
					, "{'sys_nm': 'Service Authentication', 'api_spc_nm': '부가서비스 가입',     'api_nm': 'getPartyAndMarketInfoByPhoneNumber',       'api_no': '80028', 'status_check_dt': '2019-09-22 12:34:56', 'status_code': 'OK', 'status_res_msec': '324'  },"
					, "{'sys_nm': 'Service Authentication', 'api_spc_nm': '부가서비스 가입',     'api_nm': 'getWiredInfoByMacId',                      'api_no': '80029', 'status_check_dt': '2019-09-23 12:34:56', 'status_code': 'NK', 'status_res_msec': '0'    },"
					, "{'sys_nm': 'Service Authentication', 'api_spc_nm': '부가서비스 가입',     'api_nm': 'getUnpaindInfoAndMarketInfoByPhoneNumber', 'api_no': '80030', 'status_check_dt': '2019-09-24 12:34:56', 'status_code': 'OK', 'status_res_msec': '435'  },"
					, "{'sys_nm': 'IoTMakers',              'api_spc_nm': 'Device Model API',    'api_nm': 'authenticationByCTN',                      'api_no': '80031', 'status_check_dt': '2019-09-20 12:34:56', 'status_code': 'OK', 'status_res_msec': '234'  },"
					, "{'sys_nm': 'IoTMakers',              'api_spc_nm': 'Device Model API',    'api_nm': 'subscriberOllehByCTN',                     'api_no': '80032', 'status_check_dt': '2019-09-21 12:34:56', 'status_code': 'OK', 'status_res_msec': '512'  },"
					, "{'sys_nm': 'IoTMakers',              'api_spc_nm': 'Device Model API',    'api_nm': 'authenticateByCredentialIdWithActionType', 'api_no': '80033', 'status_check_dt': '2019-09-22 12:34:56', 'status_code': 'DL', 'status_res_msec': '10234'},"
					, "{'sys_nm': 'IoTMakers',              'api_spc_nm': 'Device Model API',    'api_nm': 'updateNickNameInShowMenu',                 'api_no': '80034', 'status_check_dt': '2019-09-23 12:34:56', 'status_code': 'OK', 'status_res_msec': '34'   },"
					, "{'sys_nm': 'IoTMakers',              'api_spc_nm': 'Device Model API',    'api_nm': 'authenticateByDN',                         'api_no': '80035', 'status_check_dt': '2019-09-24 12:34:56', 'status_code': 'NK', 'status_res_msec': '0'    },"
					, "{'sys_nm': 'Geo master',             'api_spc_nm': 'MAP API',             'api_nm': 'getOldQookShowId',                         'api_no': '80036', 'status_check_dt': '2019-09-20 12:34:56', 'status_code': 'NK', 'status_res_msec': '0'    },"
					, "{'sys_nm': 'Geo master',             'api_spc_nm': 'MAP API',             'api_nm': 'authenticateBySMSWithTablet',              'api_no': '80037', 'status_check_dt': '2019-09-21 12:34:56', 'status_code': 'OK', 'status_res_msec': '231'  },"
					, "{'sys_nm': 'Geo master',             'api_spc_nm': 'MAP API',             'api_nm': 'invalidateToken',                          'api_no': '80038', 'status_check_dt': '2019-09-22 12:34:56', 'status_code': 'OK', 'status_res_msec': '523'  },"
					, "{'sys_nm': 'Geo master',             'api_spc_nm': 'MAP API',             'api_nm': 'getUserProfileByAccessLevel',              'api_no': '80039', 'status_check_dt': '2019-09-23 12:34:56', 'status_code': 'NK', 'status_res_msec': '0'    },"
					, "{'sys_nm': 'Geo master',             'api_spc_nm': 'MAP API',             'api_nm': 'manageUserServiceAgreementStatus',         'api_no': '80040', 'status_check_dt': '2019-09-24 12:34:56', 'status_code': 'OK', 'status_res_msec': '231'  },"
					, "{'sys_nm': 'ucloud biz',             'api_spc_nm': '스토리지/CDN/인코딩', 'api_nm': 'getGroupMemberList',                       'api_no': '80041', 'status_check_dt': '2019-09-20 12:34:56', 'status_code': 'OK', 'status_res_msec': '52'   },"
					, "{'sys_nm': 'ucloud biz',             'api_spc_nm': '스토리지/CDN/인코딩', 'api_nm': 'getDeviceInfoForUser',                     'api_no': '80042', 'status_check_dt': '2019-09-21 12:34:56', 'status_code': 'OK', 'status_res_msec': '24'   },"
					, "{'sys_nm': 'ucloud biz',             'api_spc_nm': '스토리지/CDN/인코딩', 'api_nm': 'getUserProfileForOllehPortal',             'api_no': '80043', 'status_check_dt': '2019-09-22 12:34:56', 'status_code': 'OK', 'status_res_msec': '3461' },"
					, "{'sys_nm': 'ucloud biz',             'api_spc_nm': '스토리지/CDN/인코딩', 'api_nm': 'getNameCheckValidation',                   'api_no': '80044', 'status_check_dt': '2019-09-23 12:34:56', 'status_code': 'OK', 'status_res_msec': '234'  },"
					, "{'sys_nm': 'ucloud biz',             'api_spc_nm': '스토리지/CDN/인코딩', 'api_nm': 'updateShowPassword',                       'api_no': '80045', 'status_check_dt': '2019-09-24 12:34:56', 'status_code': 'OK', 'status_res_msec': '12'   },"
					, "]");
				JSONArray jsa = JSONArray.fromObject(s_json);

				//-- pagination {
				int req_tc = Math.max(KsmUtil.parseInt(param.get("tc"), 0), 0);
				int req_pz = Math.max(KsmUtil.parseInt(param.get("pz"), 10), 1);
				int req_pg = Math.max(KsmUtil.parseInt(param.get("pg"), 1), 1);

				Object arr_jsa[] = jsa.toArray();
				req_tc = arr_jsa.length;

				int last_pg = (int)Math.max(Math.ceil((double)req_tc / (double)req_pz), 1);
				req_pg = Math.min(req_pg, last_pg);
				int first_index = ((req_pg - 1) * req_pz);
				int last_index = Math.min((req_pg * req_pz), ((req_tc > 0) ? req_tc : (req_pg * req_pz)));
				arr_jsa = Arrays.copyOfRange(arr_jsa, first_index, last_index);
				//-- pagination }

				s_json = String.join("", "{"
					, "'ok_count': '29', 'dl_count': '4', 'nk_count': '12', 'api_status_info_list': [],"
				, "}");

				JSONObject jso = JSONObject.fromObject(s_json);
				jso.put("api_status_info_list", JSONArray.fromObject(arr_jsa));

				totalCount = ((req_tc != -1) ? req_tc : jsa.size());
				pageSize = req_pz;
				currentPage = req_pg;
				data = jso;
			}
			else if (true == "api_status_info_daily_list".equalsIgnoreCase(ifname)) {
				//-- build example 'daily_list': [] array { 
				s_json = String.join("", "["
					, "{'status_check_dt': '2019-09-24 12:34:56', 'status_code': 'OK', 'status_res_msec': '123'  },"
					, "{'status_check_dt': '2019-09-23 12:34:56', 'status_code': 'OK', 'status_res_msec': '231'  },"
					, "{'status_check_dt': '2019-09-22 12:34:56', 'status_code': 'OK', 'status_res_msec': '12345'},"
					, "{'status_check_dt': '2019-09-21 12:34:56', 'status_code': 'OK', 'status_res_msec': '12'   },"
					, "{'status_check_dt': '2019-09-20 12:34:56', 'status_code': 'OK', 'status_res_msec': '1'    },"
					, "{'status_check_dt': '2019-09-19 12:34:56', 'status_code': 'OK', 'status_res_msec': '23'   },"
					, "{'status_check_dt': '2019-09-18 12:34:56', 'status_code': 'OK', 'status_res_msec': '123'  },"
					, "]");
				JSONArray jsa_daily_all_ok = JSONArray.fromObject(s_json);
				JSONArray jsa_daily_all_dl = JSONArray.fromObject(s_json);
				for (int n_ii = 0 ; n_ii < jsa_daily_all_dl.size(); n_ii++) {
					jsa_daily_all_dl.getJSONObject(n_ii).put("status_code", "DL");
				}
				JSONArray jsa_daily_all_nk = JSONArray.fromObject(s_json);
				for (int n_ii = 0 ; n_ii < jsa_daily_all_nk.size(); n_ii++) {
					jsa_daily_all_nk.getJSONObject(n_ii).put("status_code", "NK");
				}
				//-- random으로 status_code를 작성한 example
				JSONArray jsa_daily_all_rnd_1 = JSONArray.fromObject(s_json);
				JSONArray jsa_daily_all_rnd_2 = JSONArray.fromObject(s_json);
				JSONArray jsa_daily_all_rnd_3 = JSONArray.fromObject(s_json);
				String[] arr_status_code;
				int n_rnd_idx;
				for (int n_ii = 0 ; n_ii < jsa_daily_all_rnd_1.size(); n_ii++) {
					arr_status_code = "OK,OK,OK,DL,NK".split(",");
					n_rnd_idx = (int)(ThreadLocalRandom.current().nextDouble() * (arr_status_code.length - 1)) + 0;
					jsa_daily_all_rnd_1.getJSONObject(n_ii).put("status_code", arr_status_code[n_rnd_idx]);
					arr_status_code = "OK,DL,DL,DL,NK".split(",");
					n_rnd_idx = (int)(ThreadLocalRandom.current().nextDouble() * (arr_status_code.length - 1)) + 0;
					jsa_daily_all_rnd_2.getJSONObject(n_ii).put("status_code", arr_status_code[n_rnd_idx]);
					arr_status_code = "OK,DL,NK,NK,NK".split(",");
					n_rnd_idx = (int)(ThreadLocalRandom.current().nextDouble() * (arr_status_code.length  - 1)) + 0;
					jsa_daily_all_rnd_3.getJSONObject(n_ii).put("status_code", arr_status_code[n_rnd_idx]);
				}
				//-- example 'daily_list': [] array
				JSONArray[] arr_jsa_daily = { jsa_daily_all_ok, jsa_daily_all_nk, jsa_daily_all_rnd_1, jsa_daily_all_rnd_2, jsa_daily_all_rnd_3 }; 
				//-- build example 'daily_list': [] array } 

				s_json = String.join("", "["
					, "{'sys_nm': 'KOS',                    'api_spc_nm': 'KOS-RDS[A-H]',        'api_nm': 'authenticateByDN',                         'api_no': '80001', 'daily_list': []},"
					, "{'sys_nm': 'KOS',                    'api_spc_nm': 'KOS-RDS[A-H]',        'api_nm': 'CondByHndsetOrrgRetvSO',                   'api_no': '80002', 'daily_list': []},"
					, "{'sys_nm': 'KOS',                    'api_spc_nm': 'KOS-RDS[A-H]',        'api_nm': 'SpecRetvSO',                               'api_no': '80003', 'daily_list': []},"
					, "{'sys_nm': 'KOS',                    'api_spc_nm': 'KOS-RDS[A-H]',        'api_nm': 'KtsIcgBfacGdncSO',                         'api_no': '80004', 'daily_list': []},"
					, "{'sys_nm': 'KOS',                    'api_spc_nm': 'KOS-RDS[A-H]',        'api_nm': 'getCcomDevBas',                            'api_no': '80005', 'daily_list': []},"
					, "{'sys_nm': 'Application Service',    'api_spc_nm': '위치 서비스',         'api_nm': 'LocationFindPosition',                     'api_no': '80006', 'daily_list': []},"
					, "{'sys_nm': 'Application Service',    'api_spc_nm': '위치 서비스',         'api_nm': 'LocationListTrigger',                      'api_no': '80007', 'daily_list': []},"
					, "{'sys_nm': 'Application Service',    'api_spc_nm': '위치 서비스',         'api_nm': 'convertCoord',                             'api_no': '80008', 'daily_list': []},"
					, "{'sys_nm': 'Application Service',    'api_spc_nm': '위치 서비스',         'api_nm': 'LocationJoinInfoLostCp',                   'api_no': '80009', 'daily_list': []},"
					, "{'sys_nm': 'Application Service',    'api_spc_nm': '위치 서비스',         'api_nm': 'LocationJoinInfoMNGCp',                    'api_no': '80010', 'daily_list': []},"
					, "{'sys_nm': 'User Authentication',    'api_spc_nm': 'KT ID 인증',          'api_nm': 'authenticateByCredentialId',               'api_no': '80011', 'daily_list': []},"
					, "{'sys_nm': 'User Authentication',    'api_spc_nm': 'KT ID 인증',          'api_nm': 'getAgreementListForUser',                  'api_no': '80012', 'daily_list': []},"
					, "{'sys_nm': 'User Authentication',    'api_spc_nm': 'KT ID 인증',          'api_nm': 'authenticateUserByIdPwdForOlleh',          'api_no': '80013', 'daily_list': []},"
					, "{'sys_nm': 'User Authentication',    'api_spc_nm': 'KT ID 인증',          'api_nm': 'getCustomerIdentificationInfo',            'api_no': '80014', 'daily_list': []},"
					, "{'sys_nm': 'User Authentication',    'api_spc_nm': 'KT ID 인증',          'api_nm': 'validateToken',                            'api_no': '80015', 'daily_list': []},"
					, "{'sys_nm': 'KT BaaS',                'api_spc_nm': 'KT Smart Contract',   'api_nm': 'MessageMTOTSMSNetChargeOpen',              'api_no': '80016', 'daily_list': []},"
					, "{'sys_nm': 'KT BaaS',                'api_spc_nm': 'KT Smart Contract',   'api_nm': 'MessageSendSMSReportNoNetCharge',          'api_no': '80017', 'daily_list': []},"
					, "{'sys_nm': 'KT BaaS',                'api_spc_nm': 'KT Smart Contract',   'api_nm': 'MessageConfirm',                           'api_no': '80018', 'daily_list': []},"
					, "{'sys_nm': 'KT BaaS',                'api_spc_nm': 'KT Smart Contract',   'api_nm': 'LocationFindPosition',                     'api_no': '80019', 'daily_list': []},"
					, "{'sys_nm': 'KT BaaS',                'api_spc_nm': 'KT Smart Contract',   'api_nm': 'LocationListTrigger',                      'api_no': '80020', 'daily_list': []},"
					, "{'sys_nm': 'GiGA Genie',             'api_spc_nm': 'AI MAKERS KIT',       'api_nm': 'getGeocodeByAddr',                         'api_no': '80021', 'daily_list': []},"
					, "{'sys_nm': 'GiGA Genie',             'api_spc_nm': 'AI MAKERS KIT',       'api_nm': 'addrStepSearch',                           'api_no': '80022', 'daily_list': []},"
					, "{'sys_nm': 'GiGA Genie',             'api_spc_nm': 'AI MAKERS KIT',       'api_nm': 'localSearch',                              'api_no': '80023', 'daily_list': []},"
					, "{'sys_nm': 'GiGA Genie',             'api_spc_nm': 'AI MAKERS KIT',       'api_nm': 'getPoiWithName',                           'api_no': '80024', 'daily_list': []},"
					, "{'sys_nm': 'GiGA Genie',             'api_spc_nm': 'AI MAKERS KIT',       'api_nm': 'geocode ',                                 'api_no': '80025', 'daily_list': []},"
					, "{'sys_nm': 'Service Authentication', 'api_spc_nm': '부가서비스 가입',     'api_nm': 'getSubscriptionStatusByIdPwd',             'api_no': '80026', 'daily_list': []},"
					, "{'sys_nm': 'Service Authentication', 'api_spc_nm': '부가서비스 가입',     'api_nm': 'checkUserInfoAndDeviceInfo',               'api_no': '80027', 'daily_list': []},"
					, "{'sys_nm': 'Service Authentication', 'api_spc_nm': '부가서비스 가입',     'api_nm': 'getPartyAndMarketInfoByPhoneNumber',       'api_no': '80028', 'daily_list': []},"
					, "{'sys_nm': 'Service Authentication', 'api_spc_nm': '부가서비스 가입',     'api_nm': 'getWiredInfoByMacId',                      'api_no': '80029', 'daily_list': []},"
					, "{'sys_nm': 'Service Authentication', 'api_spc_nm': '부가서비스 가입',     'api_nm': 'getUnpaindInfoAndMarketInfoByPhoneNumber', 'api_no': '80030', 'daily_list': []},"
					, "{'sys_nm': 'IoTMakers',              'api_spc_nm': 'Device Model API',    'api_nm': 'authenticationByCTN',                      'api_no': '80031', 'daily_list': []},"
					, "{'sys_nm': 'IoTMakers',              'api_spc_nm': 'Device Model API',    'api_nm': 'subscriberOllehByCTN',                     'api_no': '80032', 'daily_list': []},"
					, "{'sys_nm': 'IoTMakers',              'api_spc_nm': 'Device Model API',    'api_nm': 'authenticateByCredentialIdWithActionType', 'api_no': '80033', 'daily_list': []},"
					, "{'sys_nm': 'IoTMakers',              'api_spc_nm': 'Device Model API',    'api_nm': 'updateNickNameInShowMenu',                 'api_no': '80034', 'daily_list': []},"
					, "{'sys_nm': 'IoTMakers',              'api_spc_nm': 'Device Model API',    'api_nm': 'authenticateByDN',                         'api_no': '80035', 'daily_list': []},"
					, "{'sys_nm': 'Geo master',             'api_spc_nm': 'MAP API',             'api_nm': 'getOldQookShowId',                         'api_no': '80036', 'daily_list': []},"
					, "{'sys_nm': 'Geo master',             'api_spc_nm': 'MAP API',             'api_nm': 'authenticateBySMSWithTablet',              'api_no': '80037', 'daily_list': []},"
					, "{'sys_nm': 'Geo master',             'api_spc_nm': 'MAP API',             'api_nm': 'invalidateToken',                          'api_no': '80038', 'daily_list': []},"
					, "{'sys_nm': 'Geo master',             'api_spc_nm': 'MAP API',             'api_nm': 'getUserProfileByAccessLevel',              'api_no': '80039', 'daily_list': []},"
					, "{'sys_nm': 'Geo master',             'api_spc_nm': 'MAP API',             'api_nm': 'manageUserServiceAgreementStatus',         'api_no': '80040', 'daily_list': []},"
					, "{'sys_nm': 'ucloud biz',             'api_spc_nm': '스토리지/CDN/인코딩', 'api_nm': 'getGroupMemberList',                       'api_no': '80041', 'daily_list': []},"
					, "{'sys_nm': 'ucloud biz',             'api_spc_nm': '스토리지/CDN/인코딩', 'api_nm': 'getDeviceInfoForUser',                     'api_no': '80042', 'daily_list': []},"
					, "{'sys_nm': 'ucloud biz',             'api_spc_nm': '스토리지/CDN/인코딩', 'api_nm': 'getUserProfileForOllehPortal',             'api_no': '80043', 'daily_list': []},"
					, "{'sys_nm': 'ucloud biz',             'api_spc_nm': '스토리지/CDN/인코딩', 'api_nm': 'getNameCheckValidation',                   'api_no': '80044', 'daily_list': []},"
					, "{'sys_nm': 'ucloud biz',             'api_spc_nm': '스토리지/CDN/인코딩', 'api_nm': 'updateShowPassword',                       'api_no': '80045', 'daily_list': []},"
					, "]");
				JSONArray jsa = JSONArray.fromObject(s_json);

				//-- pagination {
				int req_tc = Math.max(KsmUtil.parseInt(param.get("tc"), 0), 0);
				int req_pz = Math.max(KsmUtil.parseInt(param.get("pz"), 10), 1);
				int req_pg = Math.max(KsmUtil.parseInt(param.get("pg"), 1), 1);

				Object arr_jsa[] = jsa.toArray();
				req_tc = arr_jsa.length;

				int last_pg = (int)Math.max(Math.ceil((double)req_tc / (double)req_pz), 1);
				req_pg = Math.min(req_pg, last_pg);
				int first_index = ((req_pg - 1) * req_pz);
				int last_index = Math.min((req_pg * req_pz), ((req_tc > 0) ? req_tc : (req_pg * req_pz)));
				arr_jsa = Arrays.copyOfRange(arr_jsa, first_index, last_index);
				//-- pagination }

				//-- set example 'daily_list': [] array { 
				for (int n_ii = 0 ; n_ii < arr_jsa.length; n_ii++) {
					n_rnd_idx = (int)(ThreadLocalRandom.current().nextDouble() * (arr_jsa_daily.length - 1)) + 0;
					if (arr_jsa[n_ii] instanceof JSONObject jso) {
						jso.put("daily_list", arr_jsa_daily[n_rnd_idx]);
					}
				}
				//-- set example 'daily_list': [] array } 

				jsa = JSONArray.fromObject(arr_jsa);

				totalCount = ((req_tc != -1) ? req_tc : jsa.size());
				pageSize = req_pz;
				currentPage = req_pg;
				data = jsa;
			}
		}
		else if (true == "list".equalsIgnoreCase(group)) {
			if (true == "api_status_check_hist_list".equalsIgnoreCase(ifname)) {
				String req_api_no = KsmUtil.fnSafeStr(param.get("api_no"));

				s_json = String.join("", "["
					, "]");
				data = JSONArray.fromObject(s_json);
			}
		}
		else if (true == "common".equalsIgnoreCase(group)) {
			if (true == "api_system_spc_list".equalsIgnoreCase(ifname)) {
				s_json = String.join("", "["
							, "{'sys_id': 'KOS',        'sys_nm': 'KOS',                    'api_spc_no': '12738', 'api_spc_nm': 'prectice',            'api_spc_id': '12738', 'api_spc_ver': '1.0'  },"
							, "{'sys_id': '5G',         'sys_nm': '5G as a Platform',       'api_spc_no': '11717', 'api_spc_nm': 'Networked VR',        'api_spc_id': '11717', 'api_spc_ver': '0.0.1'},"
							, "{'sys_id': '5G',         'sys_nm': '5G as a Platform',       'api_spc_no': '11719', 'api_spc_nm': 'Precise Positioning', 'api_spc_id': '11719', 'api_spc_ver': '0.0.1'},"
							, "{'sys_id': '5G',         'sys_nm': '5G as a Platform',       'api_spc_no': '11718', 'api_spc_nm': 'Video Analytics',     'api_spc_id': '11718', 'api_spc_ver': '0.0.1'},"
							, "{'sys_id': 'BLOCKCHAIN', 'sys_nm': 'KT BaaS',                'api_spc_no': '11728', 'api_spc_nm': 'KT Smart Contract',   'api_spc_id': '11728', 'api_spc_ver': '0.0.1'},"
							, "{'sys_id': 'BLOCKCHAIN', 'sys_nm': 'KT BaaS',                'api_spc_no': '11729', 'api_spc_nm': 'Smart Contract Lite', 'api_spc_id': '11729', 'api_spc_ver': '0.0.1'},"
							, "{'sys_id': 'BLOCKCHAIN', 'sys_nm': 'KT BaaS',                'api_spc_no': '11730', 'api_spc_nm': 'Smart Contract pro',  'api_spc_id': '11730', 'api_spc_ver': '0.0.1'},"
							, "{'sys_id': 'GIGAGENIE',  'sys_nm': 'GiGA Genie',             'api_spc_no': '11687', 'api_spc_nm': '대화 API',              'api_spc_id': '11687', 'api_spc_ver': '0.0.1'},"
							, "{'sys_id': 'GIGAGENIE',  'sys_nm': 'GiGA Genie',             'api_spc_no': '11688', 'api_spc_nm': '번역 API',              'api_spc_id': '11688', 'api_spc_ver': '0.0.1'},"
							, "{'sys_id': 'GIGAGENIE',  'sys_nm': 'GiGA Genie',             'api_spc_no': '11686', 'api_spc_nm': '음성인식 API',           'api_spc_id': '11686', 'api_spc_ver': '0.0.1'},"
							, "{'sys_id': 'GIGAGENIE',  'sys_nm': 'GiGA Genie',             'api_spc_no': '11716', 'api_spc_nm': 'AI MAKERS KIT',       'api_spc_id': '11716', 'api_spc_ver': '0.0.1'},"
							, "{'sys_id': 'GIGAGENIE',  'sys_nm': 'GiGA Genie',             'api_spc_no': '11674', 'api_spc_nm': 'AI MAKERS KIT2',      'api_spc_id': '11674', 'api_spc_ver': '0.0.1'},"
							, "{'sys_id': 'GIGAGENIE',  'sys_nm': 'GiGA Genie',             'api_spc_no': '11673', 'api_spc_nm': 'Dialog Kit',          'api_spc_id': '11673', 'api_spc_ver': '0.0.1'},"
							, "{'sys_id': 'GIGAGENIE',  'sys_nm': 'GiGA Genie',             'api_spc_no': '11671', 'api_spc_nm': 'Service SDK',         'api_spc_id': '11671', 'api_spc_ver': '0.0.1'},"
							, "{'sys_id': 'GIGAGENIE',  'sys_nm': 'GiGA Genie',             'api_spc_no': '11676', 'api_spc_nm': 'TTS API',             'api_spc_id': '11676', 'api_spc_ver': '0.0.1'},"
							, "{'sys_id': 'GIGAGENIE',  'sys_nm': 'GiGA Genie',             'api_spc_no': '11720', 'api_spc_nm': 'TTS API',             'api_spc_id': '11720', 'api_spc_ver': '0.0.1'},"
							, "{'sys_id': 'GIGAGENIE',  'sys_nm': 'GiGA Genie',             'api_spc_no': '11677', 'api_spc_nm': 'Vision API',          'api_spc_id': '11677', 'api_spc_ver': '0.0.1'},"
							, "{'sys_id': 'GIGAGENIE',  'sys_nm': 'GiGA Genie',             'api_spc_no': '11672', 'api_spc_nm': 'Voice Kit',           'api_spc_id': '11672', 'api_spc_ver': '0.0.1'},"
							, "{'sys_id': 'AUTHENTI',   'sys_nm': 'User Authentication',    'api_spc_no': '11658', 'api_spc_nm': '앱 간편 인증',            'api_spc_id': '11658', 'api_spc_ver': '0.0.1'},"
							, "{'sys_id': 'AUTHENTI',   'sys_nm': 'User Authentication',    'api_spc_no': '11654', 'api_spc_nm': 'KT ID 인증',           'api_spc_id': '11654', 'api_spc_ver': '0.0.1'},"
							, "{'sys_id': 'AUTHENTI',   'sys_nm': 'User Authentication',    'api_spc_no': '11657', 'api_spc_nm': 'Open 인증',            'api_spc_id': '11657', 'api_spc_ver': '0.0.1'},"
							, "{'sys_id': 'INFRA',      'sys_nm': 'Service Authentication', 'api_spc_no': '11656', 'api_spc_nm': '과금 서비스',            'api_spc_id': '11656', 'api_spc_ver': '0.0.1'},"
							, "{'sys_id': 'INFRA',      'sys_nm': 'Service Authentication', 'api_spc_no': '11659', 'api_spc_nm': '부가서비스 가입',         'api_spc_id': '11659', 'api_spc_ver': '0.0.1'},"
							, "{'sys_id': 'INFRA',      'sys_nm': 'Service Authentication', 'api_spc_no': '11653', 'api_spc_nm': '서비스 계약 조회',         'api_spc_id': '11653', 'api_spc_ver': '0.0.1'},"
							, "{'sys_id': 'OLLEHMAP',   'sys_nm': 'Geo master',             'api_spc_no': '11695', 'api_spc_nm': '경로안내',              'api_spc_id': '11695', 'api_spc_ver': '0.0.1'},"
							, "{'sys_id': 'OLLEHMAP',   'sys_nm': 'Geo master',             'api_spc_no': '11696', 'api_spc_nm': '실시간 교통정보',         'api_spc_id': '11696', 'api_spc_ver': '0.0.1'},"
							, "{'sys_id': 'OLLEHMAP',   'sys_nm': 'Geo master',             'api_spc_no': '11692', 'api_spc_nm': '주소검색(geocoding)',   'api_spc_id': '11692', 'api_spc_ver': '0.0.1'},"
							, "{'sys_id': 'OLLEHMAP',   'sys_nm': 'Geo master',             'api_spc_no': '11691', 'api_spc_nm': '지도',                 'api_spc_id': '11691', 'api_spc_ver': '0.0.1'},"
							, "{'sys_id': 'OLLEHMAP',   'sys_nm': 'Geo master',             'api_spc_no': '11697', 'api_spc_nm': '특화 API',             'api_spc_id': '11697', 'api_spc_ver': '0.0.1'},"
							, "{'sys_id': 'OLLEHMAP',   'sys_nm': 'Geo master',             'api_spc_no': '11722', 'api_spc_nm': '화면테스트',            'api_spc_id': '11721', 'api_spc_ver': '0.0.2'},"
							, "{'sys_id': 'OLLEHMAP',   'sys_nm': 'Geo master',             'api_spc_no': '11723', 'api_spc_nm': '화면테스트2222',        'api_spc_id': '11723', 'api_spc_ver': '0.0.1'},"
							, "{'sys_id': 'OLLEHMAP',   'sys_nm': 'Geo master',             'api_spc_no': '5314',  'api_spc_nm': 'LBS API',            'api_spc_id': '5314',  'api_spc_ver': '0.1'  },"
							, "{'sys_id': 'OLLEHMAP',   'sys_nm': 'Geo master',             'api_spc_no': '4294',  'api_spc_nm': 'MAP API',            'api_spc_id': '4294',  'api_spc_ver': '0.0.1'},"
							, "{'sys_id': 'OLLEHMAP',   'sys_nm': 'Geo master',             'api_spc_no': '5315',  'api_spc_nm': 'MVNO API',           'api_spc_id': '5315',  'api_spc_ver': '0.1'  },"
							, "{'sys_id': 'OLLEHMAP',   'sys_nm': 'Geo master',             'api_spc_no': '11694', 'api_spc_nm': 'POI/공간검색',          'api_spc_id': '11694', 'api_spc_ver': '0.0.1'},"
							, "{'sys_id': 'UCLOUDBIZ',  'sys_nm': 'ucloud biz',             'api_spc_no': '11668', 'api_spc_nm': '네트워크',              'api_spc_id': '11668', 'api_spc_ver': '0.0.1'},"
							, "{'sys_id': 'UCLOUDBIZ',  'sys_nm': 'ucloud biz',             'api_spc_no': '11665', 'api_spc_nm': '데스크탑',              'api_spc_id': '11665', 'api_spc_ver': '0.0.1'},"
							, "{'sys_id': 'UCLOUDBIZ',  'sys_nm': 'ucloud biz',             'api_spc_no': '11663', 'api_spc_nm': '데이터 베이스',          'api_spc_id': '11663', 'api_spc_ver': '0.0.1'},"
							, "{'sys_id': 'UCLOUDBIZ',  'sys_nm': 'ucloud biz',             'api_spc_no': '11670', 'api_spc_nm': '매니지먼트',            'api_spc_id': '11670', 'api_spc_ver': '0.0.1'},"
							, "{'sys_id': 'UCLOUDBIZ',  'sys_nm': 'ucloud biz',             'api_spc_no': '11667', 'api_spc_nm': '보안',                'api_spc_id': '11667', 'api_spc_ver': '0.0.1'},"
							, "{'sys_id': 'UCLOUDBIZ',  'sys_nm': 'ucloud biz',             'api_spc_no': '11664', 'api_spc_nm': '스토리지/CDN/인코딩',    'api_spc_id': '11664', 'api_spc_ver': '0.0.1'},"
							, "{'sys_id': 'UCLOUDBIZ',  'sys_nm': 'ucloud biz',             'api_spc_no': '11666', 'api_spc_nm': '엔터프라이즈',           'api_spc_id': '11666', 'api_spc_ver': '0.0.1'},"
							, "{'sys_id': 'UCLOUDBIZ',  'sys_nm': 'ucloud biz',             'api_spc_no': '11662', 'api_spc_nm': '컴퓨팅',               'api_spc_id': '11662', 'api_spc_ver': '0.0.1'},"
							, "{'sys_id': 'UCLOUDBIZ',  'sys_nm': 'ucloud biz',             'api_spc_no': '11669', 'api_spc_nm': '플랫폼',               'api_spc_id': '11669', 'api_spc_ver': '0.0.1'},"
							, "{'sys_id': 'IOTMAKERS',  'sys_nm': 'IoTMakers',              'api_spc_no': '11679', 'api_spc_nm': 'Device API',         'api_spc_id': '11679', 'api_spc_ver': '0.0.1'},"
							, "{'sys_id': 'IOTMAKERS',  'sys_nm': 'IoTMakers',              'api_spc_no': '11683', 'api_spc_nm': 'Device Group API',   'api_spc_id': '11683', 'api_spc_ver': '0.0.1'},"
							, "{'sys_id': 'IOTMAKERS',  'sys_nm': 'IoTMakers',              'api_spc_no': '11681', 'api_spc_nm': 'Device Log API',     'api_spc_id': '11681', 'api_spc_ver': '0.0.1'},"
							, "{'sys_id': 'IOTMAKERS',  'sys_nm': 'IoTMakers',              'api_spc_no': '11678', 'api_spc_nm': 'Device Model API',   'api_spc_id': '11678', 'api_spc_ver': '0.0.1'},"
							, "{'sys_id': 'IOTMAKERS',  'sys_nm': 'IoTMakers',              'api_spc_no': '11682', 'api_spc_nm': 'Event API',          'api_spc_id': '11682', 'api_spc_ver': '0.0.1'},"
							, "{'sys_id': 'IOTMAKERS',  'sys_nm': 'IoTMakers',              'api_spc_no': '11727', 'api_spc_nm': 'IotTest01',          'api_spc_id': '11727', 'api_spc_ver': '0.0.1'},"
							, "{'sys_id': 'IOTMAKERS',  'sys_nm': 'IoTMakers',              'api_spc_no': '11684', 'api_spc_nm': 'Public Device API',  'api_spc_id': '11684', 'api_spc_ver': '0.0.1'},"
							, "{'sys_id': 'IOTMAKERS',  'sys_nm': 'IoTMakers',              'api_spc_no': '11680', 'api_spc_nm': 'Tag Stream API',     'api_spc_id': '11680', 'api_spc_ver': '0.0.1'},"
							, "{'sys_id': 'APPLICATI',  'sys_nm': 'Application Service',    'api_spc_no': '11655', 'api_spc_nm': '메세징 서비스',          'api_spc_id': '11655', 'api_spc_ver': '0.0.1'},"
							, "{'sys_id': 'APPLICATI',  'sys_nm': 'Application Service',    'api_spc_no': '11660', 'api_spc_nm': '위치 서비스',           'api_spc_id': '11660', 'api_spc_ver': '0.0.1'},"
							, "{'sys_id': 'APPLICATI',  'sys_nm': 'Application Service',    'api_spc_no': '11685', 'api_spc_nm': '쿠폰 서비스',           'api_spc_id': '11685', 'api_spc_ver': '0.0.1'},"
							, "{'sys_id': 'OTHER',      'sys_nm': 'GiGA energy',            'api_spc_no': '11698', 'api_spc_nm': '기준정보 API',          'api_spc_id': '11698', 'api_spc_ver': '0.0.1'},"
							, "{'sys_id': 'OTHER',      'sys_nm': 'GiGA energy',            'api_spc_no': '11701', 'api_spc_nm': '예측 피크 전력 API',     'api_spc_id': '11701', 'api_spc_ver': '0.0.1'},"
							, "{'sys_id': 'OTHER',      'sys_nm': 'GiGA energy',            'api_spc_no': '11699', 'api_spc_nm': '전력계약 정보 API',      'api_spc_id': '11699', 'api_spc_ver': '0.0.1'},"
							, "{'sys_id': 'OTHER',      'sys_nm': 'GiGA energy',            'api_spc_no': '11700', 'api_spc_nm': '피크 전력 API',         'api_spc_id': '11700', 'api_spc_ver': '0.0.1'},"
					, "]");
				data = JSONArray.fromObject(s_json);
			}
			else if (true == "api_status_spc_group_list".equalsIgnoreCase(ifname)) {
				s_json = String.join("", "["
					, "{'status_group_nm': 'KT ID 인증',         'status_group_no': '1'},"
					, "{'status_group_nm': '서비스 계약 조회',       'status_group_no': '2'},"
					, "{'status_group_nm': '과금 서비스',          'status_group_no': '3'},"
					, "{'status_group_nm': '메세징 서비스',         'status_group_no': '4'},"
					, "{'status_group_nm': '부가서비스 가입 모니터링', 'status_group_no': '5'},"
					, "]");
				data = JSONArray.fromObject(s_json);
			}
		}
		//-- group/ifname별 처리 }

		message.setResultCode(AdptranApiResultCode.RC_200_SUCESS.getCode());
		if (null == data) {
			message.setResultCode(AdptranApiResultCode.RC_404_NOT_FOUND.getCode());
		}
		message.setData(data);
		message.setTotalCount(totalCount);
		message.setPageSize(pageSize);
		message.setCurrentPage(currentPage);

		return message; 
	}
	//--[drm][dev] }
}
