package com.kt.openapi.web.adptran.api.controller;

import com.kt.openapi.web.adptran.api.AdptranApiConst;
import com.kt.openapi.web.adptran.api.AdptranApiResultCode;
import com.kt.openapi.web.adptran.api.common.CommonUtil;
import com.kt.openapi.web.adptran.api.common.message.GenericMessage;
import com.kt.openapi.web.adptran.api.common.message.RestMessage;
import com.kt.openapi.web.adptran.api.service.RefCommonService;
import com.kt.openapi.web.adptran.util.KsmUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@RequestMapping(value = AdptranApiConst.REF_API_PATH + AdptranApiConst.REF_API_VERSION)
@RestController
public class RefCommonController {

	private static final Logger logger = LoggerFactory.getLogger(RefCommonController.class);

	@Autowired
	private RefCommonService refCommonService;

	/**
	 * 정보 목록 조회
	 */
	@RequestMapping(value = "/ref/tabdata_list", method = RequestMethod.GET)
	public RestMessage select_tabdata_list(GenericMessage message, HttpServletRequest request) {
		logger.info(request.getClass().toString());

		Map<String, Object> param = new HashMap<>();
		CommonUtil.convertQueryStringToMap(request.getQueryString(), param);
		
		message.setResultCode(AdptranApiResultCode.RC_200_SUCESS.getCode());
		message.setTotalCount(refCommonService.select_tabdata_count(param));
		message.setData(refCommonService.select_tabdata_list(param));
		
		return message;
	}

	/**
	 * 정보 상세 조회
	 */
	@RequestMapping(value = "/ref/tabdata/{tabdata_seq}", method = RequestMethod.GET)
	public RestMessage select_tabdata(GenericMessage message, HttpServletRequest request, @PathVariable String tabdata_seq) {
		logger.info(request.getClass().toString());

		message.setResultCode(AdptranApiResultCode.RC_200_SUCESS.getCode());
		message.setData(refCommonService.select_tabdata(tabdata_seq));

		return message;
	}

	/**
	 * 정보 등록
	 */
	@RequestMapping(value = "/ref/tabdata", method = RequestMethod.POST)
	public RestMessage insert_tabdata(GenericMessage message, HttpServletRequest request, @RequestBody Map<String, Object> param) {
		logger.info(request.getClass().toString());

		message.setResultCode(AdptranApiResultCode.RC_200_SUCESS.getCode());
		message.setData(refCommonService.insert_tabdata(param));

		return message;
	}

	/**
	 * 정보 수정
	 */
	@RequestMapping(value = "/ref/tabdata/{tabdata_seq}", method = RequestMethod.PUT)
	public RestMessage update_tabdata(GenericMessage message, HttpServletRequest request, @PathVariable String tabdata_seq, @RequestBody Map<String, Object> param) {
		logger.info(request.getClass().toString());

		message.setResultCode(AdptranApiResultCode.RC_200_SUCESS.getCode());
		message.setData(refCommonService.update_tabdata(param));

		return message;
	}

	/**
	 * 정보 삭제
	 */
	@RequestMapping(value = "/ref/tabdata/{tabdata_seq}", method = RequestMethod.DELETE)
	public RestMessage delete_tabdata(GenericMessage message, HttpServletRequest request, @PathVariable String tabdata_seq) {
		logger.info(request.getClass().toString());

		message.setResultCode(AdptranApiResultCode.RC_200_SUCESS.getCode());
		message.setData(refCommonService.delete_tabdata(tabdata_seq));

		return message;
	}

	/**
	 * dynamic query
	 */
	@RequestMapping(value = "/ref/select_dynamic", method = RequestMethod.POST)
	public RestMessage select_api_by_get(GenericMessage message, HttpServletRequest request, @RequestBody Map<String, Object> param) throws Exception  {
		logger.debug("\n\n### {}.{}() ###\n", getClass().getSimpleName(), Thread.currentThread().getStackTrace()[1].getMethodName());

		/*--  [ref]
		HashMap<String, Object> map_in = new HashMap<>();
		map_in.put("select", "TABLE_NAME, ORDINAL_POSITION, COLUMN_NAME, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH, IS_NULLABLE, COLUMN_DEFAULT");
		map_in.put("from", "INFORMATION_SCHEMA.COLUMNS");
		map_in.put("where", "(TABLE_NAME IS NOT NULL)");
		map_in.put("orderby", "TABLE_NAME, ORDINAL_POSITION");
		--*/

		//-- [pagination][ing]
		Integer totalCount = null;
		Integer pageSize = null;
		Integer currentPage = null;
		//-- param
		//-- pagination {
		int req_tc = Math.max(KsmUtil.parseInt(param.get("tc"), 0), 0);
		int req_pz = Math.max(KsmUtil.parseInt(param.get("pz"), 10), 1);
		int req_pg = Math.max(KsmUtil.parseInt(param.get("pg"), 1), 1);
		//-- pagination }

		ArrayList<Map<String, Object>> list_out = (ArrayList<Map<String, Object>>)refCommonService.select_dynamic(param);

		message.setResultCode(AdptranApiResultCode.RC_200_SUCESS.getCode());
		message.setData(list_out);
		message.setTotalCount(list_out.size());
		message.setPageSize(req_pz);
		message.setCurrentPage(req_pg);
		return message;
	}
}
