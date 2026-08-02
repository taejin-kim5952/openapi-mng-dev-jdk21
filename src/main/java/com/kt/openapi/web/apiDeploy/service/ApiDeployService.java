package com.kt.openapi.web.apiDeploy.service;

import com.kt.openapi.web.api.vo.ApiDefVO;
import com.kt.openapi.web.api.vo.ApiSpcVO;
import com.kt.openapi.web.apiDeploy.vo.*;
import com.kt.openapi.web.cmmn.ApiException;

import java.util.List;
import java.util.Map;

public interface ApiDeployService {

	List<ApiDeployVO> selApiDeployList(ApiDeploySearchVo param) throws ApiException;

	int selApiDeployListCnt(ApiDeploySearchVo param) throws ApiException;

	List<ApiCommCodeVO> selCommCodeList(CommCodeVo param) throws ApiException;

	ApiDeployVO selDeployView(ApiDeploySearchVo param) throws ApiException;

	List<ApiDeploySystemVO> selSystemList(ApiDeploySearchVo param) throws ApiException;

	List<ApiSpcVO> selSpcList(ApiDeploySearchVo param) throws ApiException;


	//배포 프로세스 기본 정보 등록
	int insertDeployInfo(ApiDeployInsertVo param) throws ApiException;

	//배포 프로세스 자동 진행
	int updateDeployProc(ApiDeployInsertVo param)  throws ApiException;

	//상용 배포 요청
	int deployApply(DeployApplyVo param) throws  ApiException;

	//배포 신청 리스트
	List<ApiDeployApplyVO> selApiDeployApplyList(ApiDeploySearchVo param) throws ApiException;

	//배포 신청 개수
	int selApiDeployApplyListCnt(ApiDeploySearchVo param) throws ApiException;

	//배포 요청 내역
	ApiDeployApplyVO selDeployApplyView(DeployApplyVo vo) throws ApiException;

	//검증 결과 리스트
	List<ApiVerifiResultVO> selVerifiList(VerifiResultVo vo)  throws ApiException;

	//배포 결과 리스트
	List<ApiDeployHstVO> selDeployHstList(DeployHstVo vo)  throws ApiException;

	//배포 결과 개수
	int selDeployHstCnt(DeployHstVo vo)  throws ApiException;


	//배포 실패
	String updateDeployExcuteProc(ApiDeployInsertVo apiDeployInsertVo,  DeployApplyVo deployApplyVo, DeployHstVo deployHstVo)  throws ApiException;

	//TB배포 성공
	String updateDeployTbExcuteProc(ApiDeployInsertVo apiDeployInsertVo , DeployHstVo deployHstVo)  throws ApiException;

	//검증 성공
	int updateVerifiExcuteProc( VerifiResultVo verifiResultVo)  throws ApiException;

	//검증중
	int updateVerifiStartProc(ApiDeployInsertVo apiDeployInsertVo)  throws ApiException;
	// -- 1 -- 서비스 단위로 비즈니스 로직추가
	//프로세스 기본 값 입력
	String callPrivateProcessBaseInsert(ApiDeployInsertVo vo)  throws ApiException;

	//TB배포 (성공 or 실패)
	String callPrivateTbDeploy(ApiDeployInsertVo apiDeployInsertVo,  DeployHstVo deployHstVo) throws ApiException;

	//검증시작
	String callPrivateVerifiProc(ApiDeploySearchVo apiDeploySearchVo ) throws Exception;

	//검증 결과 이력 저장
	String callPrivateVerifiHst(VerifiResultVo verifiResultVo)    throws ApiException;

	//상용배포 완료
	String callPrivateCbDeploySuccess(DeployApplyVo deployApplyVo, ApiDeploySearchVo apiDeploySearchVo, DeployHstVo deployHstVo)   throws ApiException;

	//상용 배포 결과 상세 보기
	List<ApiDeployHstVO> selApiDeployView(DeployHstVo deployHstVo) throws ApiException;

	//tb배포 이력 보기
	List<ApiDeployHstVO> selApiTbDeployHst(DeployHstVo deployHstVo) throws ApiException;

	//TEST CASE 입력하기
	String insertTestCaseList(TestCaseListVo testCaseListVo) throws ApiException;

	//TEST CASE 조회
	List<ApiTestCaseVO> selTescCaseResult(TestCaseListVo testCaseListVo) throws ApiException;

	//API LIST
	List<ApiDefVO> selApiList(TestCaseListVo testCaseListVo) throws ApiException;

	//API TREE
	List<ApiTreeVO> selApiTreeList(ApiDeploySearchVo param) throws ApiException;

	//배포 반려
	String updateDeployReject(ApiDeployInsertVo apiDeployInsertVo, DeployApplyVo deployApplyVo) throws ApiException;

	//상용 재배포 요청
	int updateReDeployApply(ApiDeployInsertVo apiDeployInsertVo)  throws ApiException;

	//배일 발송
	List<ApiEmailVO> mailSend(MailVo mailVo, Map<String, String> mailMap )  ;

	//배포 프로세스 삭제
	int delDeployProc(DeployApplyVo deployApplyVo)  throws ApiException;

	//API NM으로 API NO조회
	List<ApiDefVO> selDelApiSearch(ApiDeploySearchVo apiDeploySearchVo ) throws ApiException;


	//잘못 등록한 API삭제
	int delDeployApi(DeployApplyVo vo)  throws ApiException;

	//배포 신청  현황
	List<ApiDeployApplyVO> selDeployApplySearch(ApiDeploySearchVo apiDeploySearchVo ) throws ApiException;

	//배포 신청 삭제
	int delDeployApply(DeployApplyVo vo)  throws ApiException;

	//배포 전체 현황
	List<ApiDeployStateVO> selDeployStateTotal(ApiDeploySearchVo apiDeploySearchVo ) throws ApiException;

	//API등록자 및 수정자 이메일 정보
	ApiEmailVO selMailByApiRegrNAmdr(ApiDeploySearchVo apiDeploySearchVo ) throws ApiException;

	//이메일 정보 출력(by userid)
	ApiEmailVO selMailByUserid(ApiDeploySearchVo apiDeploySearchVo ) throws ApiException;

	int apiVerNoUpdate(Map<String, String> apiVerNoUpdateInfo) throws ApiException;

}
