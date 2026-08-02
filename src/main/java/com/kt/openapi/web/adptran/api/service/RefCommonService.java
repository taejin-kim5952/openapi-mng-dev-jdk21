package com.kt.openapi.web.adptran.api.service;

import com.kt.openapi.web.adptran.api.AdptranApiResultCode;
import com.kt.openapi.web.adptran.api.common.CommonUtil;
import com.kt.openapi.web.adptran.api.common.domain.ResultMessage;
import com.kt.openapi.web.adptran.dao.RefCommonDAO;
import com.kt.openapi.web.adptran.util.KsmUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RefCommonService {
	private static final Logger logger = LoggerFactory.getLogger(RefCommonService.class);

    @Autowired
    private RefCommonDAO refCommonDAO;
    
	@Autowired
	//--@Qualifier("dataSource")
	private DataSource dataSource;

	public Integer select_tabdata_count(Map<String, Object> param) {
		return refCommonDAO.select_tabdata_count(param);
	}

	public List<Map<String, Object>> select_tabdata_list(Map<String, Object> param) {
		return refCommonDAO.select_tabdata_list_map(param);
	}

	public Map<String, Object> select_tabdata(String tabdata_seq) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("tabdata_seq", tabdata_seq);

		@SuppressWarnings("unused")
		int ret = refCommonDAO.update_tabdata_readstatus(param);

		Map<String, Object> mapOut = refCommonDAO.select_tabdata_map(param);

		return mapOut;
	}

	public ResultMessage insert_tabdata(Map<String, Object> param) {
		try {
			if ((param.containsKey("fd_varchar") && CommonUtil.isNotEmpty(param.get("fd_varchar"))) &&
				(param.containsKey("fd_int") && CommonUtil.isNotEmpty(param.get("fd_int")))) {

				int key = refCommonDAO.insert_tabdata(param);
				if (key <= 0){
					return new ResultMessage(AdptranApiResultCode.BIZ_EXCEPTION.getCode(), "tab정보 등록이 실패하였습니다.");
				}
				return new ResultMessage(AdptranApiResultCode.RC_200_SUCESS.getCode(), "tab정보가 등록 되었습니다.");
			} else {
				return new ResultMessage(AdptranApiResultCode.BIZ_EXCEPTION.getCode(), "요청 변수를 확인하여 주세요.");
			}
		} catch(Exception e) {
			CommonUtil.exLogging("insert_tabdata", e, logger);
			return new ResultMessage(AdptranApiResultCode.BIZ_EXCEPTION.getCode(), "tab정보 등록 예외");
		}
	}

	public ResultMessage update_tabdata(Map<String, Object> param) {
		try {
			int ret = refCommonDAO.update_tabdata(param);
			if (ret != 1) {
				return new ResultMessage(AdptranApiResultCode.BIZ_EXCEPTION.getCode(), "tab정보 수정이 실패하였습니다");
			}
			return new ResultMessage(AdptranApiResultCode.RC_200_SUCESS.getCode(), "tab정보가 정상적으로 수정 되었습니다.");
		} catch(Exception e) {
			CommonUtil.exLogging("update_tabdata", e, logger);
			return new ResultMessage(AdptranApiResultCode.BIZ_EXCEPTION.getCode(), "tab정보 수정 예외");
		}
	}

	public ResultMessage delete_tabdata(String tabdata_seq) {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("tabdata_seq", tabdata_seq);
			int ret = refCommonDAO.delete_tabdata(param);
			if (ret != 1) {
				return new ResultMessage(AdptranApiResultCode.BIZ_EXCEPTION.getCode(), "tab정보 삭제가 실패하였습니다");
			}
			return new ResultMessage(AdptranApiResultCode.RC_200_SUCESS.getCode(), "tab정보가 정상적으로 삭제 되었습니다.");
		} catch(Exception e) {
			CommonUtil.exLogging("delete_tabdata", e, logger);
			return new ResultMessage(AdptranApiResultCode.BIZ_EXCEPTION.getCode(), "tab정보 삭제 예외");
		}
	}
	
	public List<Map<String, Object>> select_dynamic(Map<String, Object> param) {
		//--[tag:SR-20210427]
		//--[i][dep][avoid xml mapper $ binding]
		//--##String queryId = "refCommon.select_DYNAMIC";
		//--##List<Map<String, Object>> listOut = ksmCmnDAO.selectQueryList(queryId, param);
		String req_s = KsmUtil.fnSafeStr(param.get("select"));
		String req_f = KsmUtil.fnSafeStr(param.get("from"));
		String req_w = KsmUtil.fnSafeStr(param.get("where"));
		String req_g = KsmUtil.fnSafeStr(param.get("groupby"));
		String req_o = KsmUtil.fnSafeStr(param.get("orderby"));

		String query = "SELECT " + req_s  + " FROM " + req_f;
		query += ((req_w.length() > 0) ? (" WHERE " + req_w) : "");
		query += ((req_g.length() > 0) ? (" GROUP BY " + req_g) : "");
		query += ((req_o.length() > 0) ? (" ORDER BY " + req_o) : "");

		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		List<Map<String, Object>> listOut = jdbcTemplate.queryForList(query);

		//--[ref]
		/*
		List<Map<String, Object>> listOut = new ArrayList<>();
		Context context = null;
		DataSource dataSource = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			context = new InitialContext();
			dataSource = (DataSource) context.lookup("java:comp/env/jdbc/OPENAPI");
			conn = dataSource.getConnection();
			pstmt = conn.prepareStatement(query);
			rs = pstmt.executeQuery();
			
			ResultSetMetaData md = rs.getMetaData();
			int columns = md.getColumnCount();
			while (rs.next()) {
				Map<String, Object> row = new HashMap<>(columns);
				for (int n_ii = 1; n_ii <= columns; n_ii++) {
					row.put(md.getColumnName(n_ii), rs.getObject(n_ii));
				}
				listOut.add(row);
			}
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		*/

		return listOut;
	}
}
