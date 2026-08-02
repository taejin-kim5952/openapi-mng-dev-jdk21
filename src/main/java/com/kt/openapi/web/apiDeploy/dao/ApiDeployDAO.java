package com.kt.openapi.web.apiDeploy.dao;

import com.kt.openapi.web.api.vo.ApiDefVO;
import com.kt.openapi.web.api.vo.ApiSpcVO;
import com.kt.openapi.web.apiDeploy.vo.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
* <pre>
* 1. 패키지명 : com.kt.openapi.web.apiDeploy.dao
* 2. 타입명   : ApiDeployDAO.java
* 3. 작성일   : 2017. 12. 12.
* 4. 작성자   : user
* 5. 설명     : 배포 관련 MyBatis Mapper 인터페이스
* </pre>
*/
@Mapper
public interface ApiDeployDAO {

    /** 배포 프로세스 리스트 */
    List<ApiDeployVO> selApiDeployList(ApiDeploySearchVo vo);

    /** 배포 프로세스 개수 */
    int selApiDeployListCnt(ApiDeploySearchVo vo);

    /** 공통 코드 리스트 조회 */
    List<ApiCommCodeVO> selCommCodeList(CommCodeVo vo);

    /** 배포 상세 조회 */
    ApiDeployVO selDeployView(ApiDeploySearchVo vo);

    /** 시스템 목록 조회 */
    List<ApiDeploySystemVO> selSystemList(ApiDeploySearchVo vo);

    /** SPC 목록 조회 */
    List<ApiSpcVO> selSpcList(ApiDeploySearchVo vo);

    /** 배포 프로세스 등록 */
    int insertDeployProc(ApiDeployInsertVo vo);

    /** 배포 프로세스 상태 업데이트 */
    int updateDeployProc(ApiDeployInsertVo vo);

    /** 배포 프로세스 기본 업데이트 */
    int updateDeployProcDefault(ApiDeployInsertVo vo);

    /** 배포 신청 등록 */
    int insertDeployPropose(DeployApplyVo vo);

    /** 배포 신청 리스트 */
    List<ApiDeployApplyVO> selApiDeployApplyList(ApiDeploySearchVo vo);

    /** 배포 신청 개수 */
    int selApiDeployApplyListCnt(ApiDeploySearchVo vo);

    /** 배포 신청 상세 보기 */
    ApiDeployApplyVO selDeployApplyView(DeployApplyVo vo);

    /** 검증결과 리스트 */
    List<ApiVerifiResultVO> selVerifiResult(VerifiResultVo vo);

    /** 배포결과 리스트 */
    List<ApiDeployHstVO> selDeployHstList(DeployHstVo vo);

    /** 배포결과 개수 */
    int selDeployHstCnt(DeployHstVo vo);

    /** 상용 배포 상세 이력 조회 */
    List<ApiDeployHstVO> selDeployDetailList(DeployHstVo vo);

    /** TB 배포 이력 조회 */
    List<ApiDeployHstVO> selTbDeployHst(DeployHstVo vo);

    /** 테스트 케이스 등록 */
    int insertTestCaseList(TestCaseListVo vo);

    /** 테스트 케이스 결과 조회 */
    List<ApiTestCaseVO> selTescCaseResult(TestCaseListVo vo);

    /** 최근 등록 API 리스트 */
    List<ApiDefVO> selApiList(TestCaseListVo vo);

    /** 배포 히스토리 등록 */
    int insertProcHst(ApiDeployInsertVo vo);

    /** API Tree 목록 조회 */
    List<ApiTreeVO> selApiTreeList(ApiDeploySearchVo vo);

    /** 반려 사유 업데이트 */
    int updateDeployApplyReject(DeployApplyVo vo);

    /** 메일 발송 대상 리스트 조회 */
    List<ApiEmailVO> selMailList(MailVo vo);

    /** 배포 프로세스 삭제 */
    int delDeployProc(DeployApplyVo vo);

    /** 삭제 대상 API 검색 */
    List<ApiDefVO> selDelApiSearch(ApiDeploySearchVo vo);

    /** API 정의 삭제 */
    int delDeployApi(DeployApplyVo vo);

    /** 삭제 대상 배포 신청 검색 */
    List<ApiDeployApplyVO> selDeployApplySearch(ApiDeploySearchVo vo);

    /** 배포 신청 삭제 */
    int delDeployApply(DeployApplyVo vo);

    /** 배포 전체 현황 조회 */
    List<ApiDeployStateVO> selDeployStateTotal(ApiDeploySearchVo vo);

    /** 등록자 및 수정자 이메일 조회 */
    ApiEmailVO selMailByApiRegrNAmdr(ApiDeploySearchVo vo);

    /** 사용자 이메일 조회 */
    ApiEmailVO selMailByUserid(ApiDeploySearchVo vo);

    /** 배포 프로세스 번호로 API명 조회 */
    ApiDefVO selApiNmByprocseq(ApiDeploySearchVo vo);

    /** API 버전번호 업데이트 */
    int apiVerNoUpdate(Map<String, String> apiVerNoUpdateInfo);

    /** 배포 신청 기본 정보 업데이트 */
    int updateDeployApplyDefault(DeployApplyVo vo);

    /** 배포 결과 등록 */
    int insertDeployHst(DeployHstVo vo);

    /** 검증 결과 등록 */
    int insertVerifiDefault(VerifiResultVo vo);
}
