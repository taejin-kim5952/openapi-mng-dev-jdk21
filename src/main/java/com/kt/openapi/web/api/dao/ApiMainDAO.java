package com.kt.openapi.web.api.dao;

import com.kt.openapi.web.api.vo.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <pre>
 * 1. 패키지명 : com.kt.openapi.web.api.dao
 * 2. 타입명   : ApiMainDAO.java
 * 3. 작성일   : 2017. 11. 10. 오후 2:27:12
 * 4. 작성자   : JeonGeun Kang
 * 5. 설명     : API 메인 페이지 MyBatis Mapper 인터페이스
 * </pre>
 */
@Mapper
public interface ApiMainDAO {

	/**
	* 개발요청/검토요청의 답변 갯수
	*/
	int selMainRequestCnt(ApiMainVo vo);

	/**
	* 작업중 목록 조회 - [마이그레이션] EgovMap -> VO 전환
	*/
	List<ApiSpcVO> selDevelopeApiList(ApiMainVo vo);

	/**
	* 등록완료 목록 조회 - [마이그레이션] EgovMap -> VO 전환
	*/
	List<ApiSpcVO> selDevelopeCompleteApiList(ApiMainVo vo);

	/**
	* 작업중 목록 조회 의 전체 갯수
	*/
	int selDevelopeApiTotalCnt(ApiMainVo vo);

	/**
	* 등록완료 목록 조회 의 전체 갯수
	*/
	int selDevelopeCompleteApitotalCnt(ApiMainVo vo);

	/**
	* 개발요청/검토요청 의 전체 갯수
	*/
	int selMainListTotalCnt(ApiMainVo vo);

	/**
	* 개발요청/검토요청의 목록 조회 - [마이그레이션] EgovMap -> VO 전환
	*/
	List<ApiReviewVO> selRequestList(ApiMainVo vo);

	/**
	* 하위 버전 목록 조회 - [마이그레이션] EgovMap -> VO 전환
	*/
	List<ApiSpcVO> selMainApiSubVerList(ApiMainVo vo);

	/**
	* 개발요청 저장
	*/
	int savDevReqReg(ApiMainVo vo);

	/**
	* 개발요청/검토의견 상세조회 - [마이그레이션] EgovMap -> VO 전환
	*/
	ApiReviewVO selRegistView(ApiMainVo vo);

	/**
	* 개발요청/검토의견 의 답글 목록 - [마이그레이션] EgovMap -> VO 전환
	*/
	List<ApiReviewOpinVO> selReqReplyView(ApiMainVo vo);

	/**
	* 개발요청/검토의견 의 답글 저장
	*/
	int savDevReplyReg(ApiMainVo vo);

	/**
	* 전체 시스템 조회 - [마이그레이션] EgovMap -> VO 전환
	*/
	List<ApiSystemVO> selSysList(ApiMainVo vo);

	/**
	* 파일등록
	*/
	int savReqFile(ApiMainVo vo);

	/**
	* 파일 조회 - [마이그레이션] EgovMap -> VO 전환
	*/
	List<ApiReviewAtcVO> selFileList(ApiMainVo vo);

	/**
	* API 등록 : 버전업으로 기존데이터를 사용 : KOA_TB_API_SPC
	*/
	String savApiVerUpApiSpc(ApiMainVo vo);

	/**
	* API 등록 : 버전업으로 기존데이터를 사용 : KOA_TB_API_CTGRY
	*/
	String savApiVerUpApiCtgry(ApiMainVo vo);

	/**
	* API 등록 : 버전업으로 기존데이터를 사용 : KOA_TB_API_DEF
	*/
	String savApiVerUpApiDef(ApiMainVo vo);

	/**
	* API 등록 : 버전업으로 기존데이터를 사용 : KOA_TB_API_PARAM
	*/
	String savApiVerUpApiParam(ApiMainVo vo);

	/**
	* 작업중인 API 삭제
	*/
	int delDevApi(ApiMainVo vo);

	/**
	* YAML 파일 정보 수정
	*/
	int updApiSpcFileInfo(ApiMainVo vo);

	/**
	* API 명세 YAML 파일 정보 조회 - [마이그레이션] EgovMap -> VO 전환
	*/
	ApiSpcVO selApiSpcInfo(ApiMainVo vo);

	/**
	* 버전업 등록을 위한 기존에 저장된 API 내역 조회 : 카테고리 테이블 기준으로 조회 - [마이그레이션] EgovMap -> VO 전환
	*/
	List<ApiSpcVO> selApiInfoList1(ApiMainVo vo);

	/**
	* 버전업 등록을 위한 기존에 저장된 API 내역 조회 : 카테고리 데이터 없는 기준으로 조회 - [마이그레이션] EgovMap -> VO 전환
	*/
	List<ApiSpcVO> selApiInfoList2(ApiMainVo vo);

	/**
	* 버전업 등록을 위한 기존에 저장된 API 내역 조회 : Param 테이블 조회 - [마이그레이션] EgovMap -> VO 전환
	*/
	List<ApiSpcVO> selApiInfoParamList(ApiMainVo vo);

	/**
	* 버전업 등록을 위한 기존에 저장된 API 내역 조회 : 카테고리 데이터만 있는 것으로 조회 - [마이그레이션] EgovMap -> VO 전환
	*/
	List<ApiSpcVO> selApiInfoOnlyCateList(ApiMainVo vo);

	/** [마이그레이션] EgovMap -> VO 전환 */
	ApiSpcVO selReqInfo1(ApiMainVo vo);

	/** [마이그레이션] EgovMap -> VO 전환 */
	ApiSpcVO selReqInfo2(ApiMainVo vo);

	/**
	* API 버전업시, 동일한 버전 있는 지 체크
	*/
	int salApiVerDupCheck(ApiMainVo vo);

	/**
	* API 등록 : Private전환으로 기존데이터를 사용 : KOA_TB_API_SPC
	*/
	int savApiPrivateApiSpc(ApiMainVo vo);
}
