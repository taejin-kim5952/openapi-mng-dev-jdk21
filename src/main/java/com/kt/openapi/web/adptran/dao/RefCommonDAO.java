package com.kt.openapi.web.adptran.dao;

import com.kt.openapi.web.adptran.vo.LabTabDataVO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface RefCommonDAO {

    /** sample(검색) - [마이그레이션] EgovMap -> Map 전환 (Dummy) */
    List<Map<String, Object>> select_sample(Map<String, Object> params);

    /** 탭 데이터 카운트 조회 */
    int select_tabdata_count(Map<String, Object> params);

    /** 탭 데이터 목록 조회 - [마이그레이션] EgovMap -> VO 전환 */
    List<LabTabDataVO> select_tabdata_list(Map<String, Object> params);

    /** 탭 데이터 읽음 상태 업데이트 */
    int update_tabdata_readstatus(Map<String, Object> params);

    /** 탭 데이터 상세 조회 - [마이그레이션] EgovMap -> VO 전환 */
    LabTabDataVO select_tabdata(Map<String, Object> params);

    /** 탭 데이터 등록 */
    int insert_tabdata(Map<String, Object> params);

    /** 탭 데이터 수정 */
    int update_tabdata(Map<String, Object> params);

    /** 탭 데이터 삭제 */
    int delete_tabdata(Map<String, Object> params);

    // -- [마이그레이션] KsmCmnDAO.selectQueryList() → RefCommonDAO로 이전
    /** 탭 데이터 목록 조회 (Map 기반) */
    List<Map<String, Object>> select_tabdata_list_map(Map<String, Object> params);

    // -- [마이그레이션] KsmCmnDAO.selectQuery() → RefCommonDAO로 이전
    /** 탭 데이터 상세 조회 (Map 기반) */
    Map<String, Object> select_tabdata_map(Map<String, Object> params);
}
