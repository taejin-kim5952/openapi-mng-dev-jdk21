package com.kt.openapi.web.api.service;

import com.kt.openapi.web.adptran.vo.BstApiDeployVO;
import com.kt.openapi.web.adptran.vo.BstApiTrafficVO;
import com.kt.openapi.web.api.vo.ApiCategoryVO;
import com.kt.openapi.web.api.vo.ApiDefVO;
import com.kt.openapi.web.api.vo.ApiRegVO;
import com.kt.openapi.web.userJoin.vo.UserJoinVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.ModelMap;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * [마이그레이션] EgovMap 제거 및 VO 전환
 */
public interface ApiRegService {
	/**
	* API 저장시 기본정보 등록
	*/
	public HashMap<String,Object> savApiRegBasic(ApiRegVO vo) throws Exception;

	/**
	* API명/ API PATH / API PATH 명 체크
	*/
	public int salApiDupCheck(ApiRegVO vo) throws Exception;

	/**
	 * api operationId 체크
	 */
	public int salApiIdCheck(ApiRegVO vo) throws Exception;

	/**
	 * 카테고리 명 중복 체크
	 */
	public String selApiCateNmDupCheck(ApiRegVO vo) throws Exception;

	/**
	 * api 이름 중복 조회
	 */
	public String selApiNmDupCheck(ApiRegVO vo) throws Exception;

	/**
	 * api 이름 중복 조회 기본정보
	 */
	public String selApiInfoNmDupCheck(ApiRegVO vo) throws Exception;

	/**
	 * 카테고리 안의 동일안 Path 가 존재하는지 체크
	 */
	public String salApijDupPathCheck(ApiRegVO vo) throws Exception;

	/**
	* API 불러오기 / 템플릿 불러오기
	*/
	public List<ApiDefVO> selImportApiList(ApiRegVO vo) throws Exception;

	/**
	* API 불러오기 / 템플릿 불러오기 의 전체 갯수
	*/
	public int selImportApiTotalList(ApiRegVO vo) throws Exception;

	/**
	* 카테고리 등록,수정
	*/
	public Map<String, Object> savApiCateInfo(ApiRegVO vo) throws Exception;

	/**
	* 카테고리 삭제
	*/
	public Map<String, Object> delApiCateInfo(ApiRegVO vo) throws Exception;

	/**
	* 패스 순서 변경
	*/
	public int savApiPathOrderInfo(ApiRegVO vo) throws Exception;

	/**
	* 패스의 파라미터 저장
	*/
	public int savApiPathParamInfo(ApiRegVO vo) throws Exception;

	/**
	* API 상세 조회
	*/
	public ApiDefVO selApiInfo(ApiRegVO vo) throws Exception;

	/**
	* API 패스 API 번호 조회
	*/
	public String selApiPathApiNo(ApiRegVO vo) throws Exception;

	/**
	 * 카테고리 번호 조회
	 */
	public String selApiCategoryNo(ApiRegVO vo) throws Exception;

	/**
	* DATA TYPE 저장
	*/
	public Map<String, Object> savApiDataTypeReg(ApiRegVO vo) throws Exception;

	/**
	* 카테고리 정보 조회
	*/
	public ApiCategoryVO selCateInfo(ApiRegVO vo) throws Exception;

	/**
	* 카테고리 목록 조회
	*/
	public Map<String, Object> selCateList(ApiRegVO vo) throws Exception;

	/**
	* API PATH 등록
	*/
	public Map<String, Object> savApiRegPath(ApiRegVO vo) throws Exception;

	/**
	* 공통코드 MTHTYP1000 의 목록
	*/
	public List<Map<String, Object>> selMethodDupList(ApiRegVO vo) throws Exception ;

	/**
	* YAML 파일 저장
	*/
	public void savYamlFile(String filePath , String fileName , String yaml ) throws Exception;

	/**
	* 한 건의 PATH 삭제
	*/
	public Map<String, Object> delApiPath(ApiRegVO vo) throws Exception;

	/**
	* 동일한 PATH의 삭제
	*/
	public Map<String, Object> delApiAllPath(ApiRegVO vo) throws Exception;

	/**
	* YAML 파일 만들기
	*/
	public Map<String, Object> savYamlToFile(ApiRegVO vo) throws Exception;

	/**
	* API 관련 이력 관리
	*/
	public Map<String, Object> updApiHisInfo(ApiRegVO vo) throws Exception;

	/**
	* REST 에서 API 등록
	*/
	public Map<String, Object> savApiRegRest(ApiRegVO vo) throws Exception;

	/**
	 * yaml 데이터 에서 데이터 타입만 조회
	 */
	public HashMap<String,Object> selYamlDataType(String yamlSbst) throws Exception;

	/**
	* 레스트 기본 정보를 작업중으로 변경
	*/
	public int updApiRegRestBasicToWork(ApiRegVO vo) throws Exception;

	/**
	* URL에서 YAML 파일 받아오기
	*/
	public HashMap<String,Object> selUrlToYamlAjax(ApiRegVO vo) throws Exception;

	/**
	* API에서 사용중인 사용자가 생성한 DATATYPE 목록을 조회
	*/
	public List<Map<String, Object>> selApiDataTypeUseList(ApiRegVO vo) throws Exception ;

	/**
	* apidoc 파일을 업로드 처리
	*/
	public Map<String, Object> regApidocAjax(MultipartFile uploadFile) throws Exception ;

	//--[tag:adpt][add]
	public String selNextApiId(String prefix) throws Exception;

	//-- [tag:SR-20220328]
	public Map<String, Object> selNextApiIdInfo(Map<String, Object> map_in) throws Exception;

	//--[tag:adpt][add]
	public List<Map<String, Object>> selDeployProc(Map<String, Object> map_in) throws Exception;

	/**
	* API명세 수정 권한 체크
	*/
	public int selApiSpcAuthCheck(ApiRegVO vo) throws Exception;

	/**
	* 회원 권한 체크
	*/
	public int selMbrAuthCheck(ApiRegVO vo) throws Exception;

	/**
	 * KOA_TB_API_DEF 조회
	 */
	public ApiDefVO selApiDef(ApiRegVO vo) throws Exception;

	/**
	 * KOA_TB_API_DEF 목록조회
	 */
	public List<ApiDefVO> selApiDefList(ApiRegVO vo) throws Exception;
	
	/**
	* 그룹권한 중복 체크
	*/
	public String selGrpAuthCheck(Map<String, Object> map) throws Exception;
	
	/**
	* 그룹권한 추가
	*/
	public String saveAutGrp(Map<String, Object> map) throws Exception ;

	//-- [tag:SR-20210711]
	//-- ApiProvider목록검색 // KOA_TB_API_PROVIDER
	public ArrayList<HashMap<String, Object>> selApiProviderList() throws Exception;
	
	// API명 검색
	public ApiDefVO selectApiNmNoCheck(ApiRegVO vo)throws Exception;
	
	// 프로세스 등록 된 API인지 확인용도
	public int selectApiNoCount(String apiNo)throws Exception;
	
	// 중복된 API ID가 있는지 확인하는 용도
	public boolean selectApiIdChk(String apiId)throws Exception;

	//-- [tag:PRJ-20220901] {
	//-- API배포(BEAST)-R-목록
	public List<BstApiDeployVO> selApiDeployBeastList(Map<String, Object> map_in) throws Exception;
	//-- API배포(BEAST)-R-목록count
	public int selApiDeployBeastListCnt(Map<String, Object> map_in) throws Exception;

	//-- API상태별갯수-목록
	public Map<String, Object> selBeastApiCountGroupByStatus(UserJoinVO userVO) throws Exception;

	//-- API-traffic조회대상 spc-목록
	public List<BstApiTrafficVO> selectApitrafficSpclist(Map<String, Object> map_in) throws Exception;

	//-- API-traffic조회대상 def-목록
	public List<BstApiTrafficVO> selectApitrafficDeflist(Map<String, Object> map_in) throws Exception;

	//-- API-traffic정보-[api | top5]-목록
	public List<BstApiTrafficVO> selectApitrafficData(Map<String, Object> map_in) throws Exception;

	//-- /api/reg/{pathVal}/ajax_query.do
	public ModelMap ajaxQuery(HttpServletRequest request, String pathVal);
	//-- /api/reg/{pathVal}/ajax_proc.do
	public ModelMap ajaxProc(HttpServletRequest request, String pathVal, String requestBody);
}
