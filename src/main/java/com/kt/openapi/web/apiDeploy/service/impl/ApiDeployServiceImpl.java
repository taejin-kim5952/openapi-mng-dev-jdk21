package com.kt.openapi.web.apiDeploy.service.impl;

import com.kt.openapi.web.adptran.util.KsmUtil;
import com.kt.openapi.web.api.dao.ApiRegDAO;
import com.kt.openapi.web.api.vo.ApiDefVO;
import com.kt.openapi.web.api.vo.ApiRegVO;
import com.kt.openapi.web.apiDeploy.dao.ApiDeployDAO;
import com.kt.openapi.web.apiDeploy.service.ApiDeployService;
import com.kt.openapi.web.apiDeploy.util.ApiDeployResultCode;
import com.kt.openapi.web.apiDeploy.vo.*;
import com.kt.openapi.web.api.vo.ApiSpcVO;
import com.kt.openapi.web.cmmn.ApiException;
import com.kt.openapi.web.util.CommonFunc;
import com.kt.openapi.web.util.SendMailUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ApiDeployServiceImpl implements ApiDeployService {
	private static final Logger LOG = LoggerFactory.getLogger(ApiDeployServiceImpl.class);

	//-- [KTJ][20220302--][add]
	@Value("${security.verify.mail:false}")
	private String securityVerifyMail;

	@Autowired
	private SendMailUtil sendMailUtil;

	@Autowired
	private ApiDeployDAO apiDeployDao;

	@Autowired
	private ApiRegDAO apiRegDAO;

	@Override
	public List<ApiDeployVO> selApiDeployList(ApiDeploySearchVo vo) throws ApiException{
		return apiDeployDao.selApiDeployList(vo);
	}

	@Override
	public int selApiDeployListCnt(ApiDeploySearchVo vo) throws ApiException{
		return apiDeployDao.selApiDeployListCnt(vo);
	}

	@Override
	public List<ApiCommCodeVO> selCommCodeList(CommCodeVo vo) throws ApiException{
		return apiDeployDao.selCommCodeList(vo);
	}

	//프로세스 상세 정보 보기
	@Override
	public ApiDeployVO selDeployView(ApiDeploySearchVo vo) throws ApiException{
		return apiDeployDao.selDeployView(vo);
	}

	//검색을 위한 SYSTEM리스트
	@Override
	public List<ApiDeploySystemVO> selSystemList(ApiDeploySearchVo vo) throws ApiException{
		return apiDeployDao.selSystemList(vo);
	}

	//검색을 위한 SPC 리스트
	@Override
	public List<ApiSpcVO> selSpcList(ApiDeploySearchVo vo) throws ApiException{
		return  apiDeployDao.selSpcList(vo);
	}

	//프로세스 기본 정보 입력
	@Override
	@Transactional(isolation = Isolation.READ_UNCOMMITTED, propagation = Propagation.REQUIRES_NEW)
	public int insertDeployInfo(ApiDeployInsertVo vo) throws ApiException{
		return apiDeployDao.insertDeployProc(vo);
	}

	//프로세스 단계 진행
	@Override
	@Transactional(isolation = Isolation.READ_UNCOMMITTED, propagation = Propagation.REQUIRES_NEW)
	public int updateDeployProc(ApiDeployInsertVo vo)  throws ApiException{
		return apiDeployDao.updateDeployProc(vo);
	}

	//상용 배포 요청
	@Override
	@Transactional(isolation = Isolation.READ_UNCOMMITTED)
	public int deployApply(DeployApplyVo vo) throws  ApiException{
		return apiDeployDao.insertDeployPropose(vo);
	}

	//상용 배포 신청 리스트
	@Override
	public List<ApiDeployApplyVO> selApiDeployApplyList(ApiDeploySearchVo vo) throws ApiException{
		return apiDeployDao.selApiDeployApplyList(vo);
	}

	//사용 배포 신청 개수
	@Override
	public int selApiDeployApplyListCnt(ApiDeploySearchVo vo) throws ApiException{
		return apiDeployDao.selApiDeployApplyListCnt(vo);
	}

	//배포 신청 상세 내역 보기
	@Override
	public ApiDeployApplyVO selDeployApplyView(DeployApplyVo vo) throws ApiException{
		return apiDeployDao.selDeployApplyView(vo);
	}

	//검증 결과 리스트
	//배포 프로세스 기본 업데이트 (이력 저장 포함)
	@Transactional(isolation = Isolation.READ_UNCOMMITTED, propagation = Propagation.REQUIRES_NEW)
	public int updateDeployProcDefault(ApiDeployInsertVo vo) throws ApiException {
		LOG.debug("########## updateDeployProcDefault start ###############");
		// 1. 배포 히스토리 등록 (기존 DAO 로직 이관)
		int procSeq = vo.getSeq();
		int rtnHstSeq = apiDeployDao.insertProcHst(vo);

		// insertProcHst 호출 후 vo의 seq가 자동 채번 등으로 변경되었을 수 있으므로 원복 (기존 DAO 로직 유지)
		vo.setSeq(procSeq);

		LOG.debug("<<< rtnHstSeq , procSEq >>> {} , {} ", rtnHstSeq, vo.getSeq());

		// 2. 히스토리 등록 성공 시에만 기본 정보 업데이트
		if (rtnHstSeq > 0) {
			return apiDeployDao.updateDeployProcDefault(vo);
		}
		return -1;
	}

	@Override
	public List<ApiVerifiResultVO> selVerifiList(VerifiResultVo vo)  throws ApiException{
		return apiDeployDao.selVerifiResult(vo);
	}

	//배포 결과 리스트
	@Override
	public List<ApiDeployHstVO> selDeployHstList(DeployHstVo vo)  throws ApiException{
		return apiDeployDao.selDeployHstList(vo);
	}

	//배포 결과 개수
	@Override
	public int selDeployHstCnt(DeployHstVo vo)  throws ApiException{
		return apiDeployDao.selDeployHstCnt(vo);
	}

	//배포 실패
	@Override
	@Transactional(isolation = Isolation.READ_UNCOMMITTED, propagation = Propagation.REQUIRES_NEW)
	public String updateDeployExcuteProc(ApiDeployInsertVo apiDeployInsertVo,  DeployApplyVo deployApplyVo, DeployHstVo deployHstVo)  throws ApiException{
		//KOA_TB_DEPLOY_PROC UPDATE
		apiDeployDao.updateDeployProcDefault(apiDeployInsertVo);
		//KOA_DEPLOY_APPLY UPDATE
		apiDeployDao.updateDeployApplyDefault(deployApplyVo);
		//KOA_DEPLOY_HST INSERT
		//-- [tag:PRJ-20220901]
		deployHstVo.setApiNo(KsmUtil.parseInt(apiDeployInsertVo.getApiNo(), 0));
		apiDeployDao.insertDeployHst(deployHstVo);

		return "200";
	}


	//TB배포 성공
	@Override
	@Transactional(isolation = Isolation.READ_UNCOMMITTED, propagation = Propagation.REQUIRES_NEW)
	public String updateDeployTbExcuteProc(ApiDeployInsertVo apiDeployInsertVo, DeployHstVo deployHstVo)  throws ApiException{
		//KOA_TB_DEPLOY_PROC UPDATE
		apiDeployDao.updateDeployProcDefault(apiDeployInsertVo);
		//KOA_DEPLOY_HST INSERT
		//-- [tag:PRJ-20220901]
		deployHstVo.setApiNo(KsmUtil.parseInt(apiDeployInsertVo.getApiNo(), 0));
		apiDeployDao.insertDeployHst(deployHstVo);

		return "200";
	}


	//검증 성공
	@Override
	@Transactional(isolation = Isolation.READ_UNCOMMITTED, propagation = Propagation.REQUIRES_NEW)
	public int updateVerifiExcuteProc(VerifiResultVo verifiResultVo)  throws ApiException{
		return apiDeployDao.insertVerifiDefault(verifiResultVo);
	}

	//검증중
	@Override
	@Transactional(isolation = Isolation.READ_UNCOMMITTED, propagation = Propagation.REQUIRES_NEW)
	public int updateVerifiStartProc(ApiDeployInsertVo apiDeployInsertVo)  throws ApiException{
		return apiDeployDao.updateDeployProcDefault(apiDeployInsertVo);
	}


	//상용 배포 요청
	@Override
	@Transactional(isolation = Isolation.READ_UNCOMMITTED, propagation = Propagation.REQUIRES_NEW)
	public int updateReDeployApply(ApiDeployInsertVo apiDeployInsertVo)  throws ApiException{
		int rtn = apiDeployDao.updateDeployProcDefault(apiDeployInsertVo);
		//-- [tab:job-20200714] {
		if (rtn == 1) {
			//-- [i]상용배포요청처리후 KOA_TB_API_DEF.EDIT_FLAG = 'N' 설정처리
			ApiRegVO apiRegVO = new ApiRegVO();
			apiRegVO.setApiNo(apiDeployInsertVo.getApiNo());
			apiRegDAO.updApiDefEditFlag(apiRegVO);
		}
		//-- [tab:job-20200714] }
		return rtn;
	}

	//상용 배포 결과 상세 보기
	@Override
	public List<ApiDeployHstVO> selApiDeployView(DeployHstVo deployHstVo) throws ApiException {
		return apiDeployDao.selDeployDetailList(deployHstVo);
	}

	//--{ private service

	//프로세스 기본 값 입력
	//Request parameter vo ApiDeployInsertVo
	// ex) vo.setApiNo("15896")  -- (String)
	//     vo.setProcessGubun("insert")   -- (String)  "insert" , "update"
	//	   vo.setRegr("0001M7HGVS7AwV401M4R/0xqmg=="); -- (String)  SafeDb 암호화 한 data
	@Override
	@Transactional(isolation = Isolation.READ_UNCOMMITTED, propagation = Propagation.REQUIRES_NEW)
	public String callPrivateProcessBaseInsert(ApiDeployInsertVo vo)  throws ApiException{
		int rtnSeq;
		if("insert".equals(vo.getProcessGubun())){
			vo.setDeployCd(ApiDeployResultCode.CD_1010_DEPLOY_APPLY_CODE.getCode());
			vo.setVerifiCd(ApiDeployResultCode.CD_1010_VERIFI_BASE_CODE.getCode());
			rtnSeq = insertDeployInfo(vo);
		}else if("update".equals(vo.getProcessGubun())){
			rtnSeq = updateDeployProc(vo);
		}else {
			return ApiDeployResultCode.CD_RETURN_FAIL.getCode();
		}
		return rtnSeq > 0 ? ApiDeployResultCode.CD_RETURN_SUCCESS.getCode()
				: ApiDeployResultCode.CD_RETURN_FAIL.getCode();
	}

	//TB 배포
	//Request parameter vo ApiDeployInsertVo
	// ex) vo.setDeployProcSeq(11)  -- (int)
	//     vo.setRegr("0001M7HGVS7AwV401M4R/0xqmg=="); -- (String)  SafeDb 암호화 한 data
	//     vo.setDeployAdm("0001M7HGVS7AwV401M4R/0xqmg=="); -- 로그인한 아이디
	//
	//     deployHstVo.setDeployAdm("0001M7HGVS7AwV401M4R/0xqmg=="); -- 로그인한 아이디'
	//     deployHstVo.setResultCd  -- 연동 하여 전달 받은 결과 코드 값 (String)
	//     deployHstVo.setResultMsg -- 연동 하여 전달 받은 결과 메세지 값 (String)
	//     deployHstVo.setDeployProcSeq(1212); -- tb일경우 Proc seq를 입력해 준다
	@Override
	@Transactional(isolation = Isolation.READ_UNCOMMITTED, propagation = Propagation.REQUIRES_NEW)
	public String callPrivateTbDeploy(ApiDeployInsertVo apiDeployInsertVo, DeployHstVo deployHstVo) throws ApiException{
		//비즈니스 로직
		// tbProSuccessYn : Y TB배포가 성공인 경우
		if("Y".equals(apiDeployInsertVo.getTbProSuccessYn().toString())) {
			apiDeployInsertVo.setDeployCd(ApiDeployResultCode.CD_1020_DEPLOY_APPLY_CODE.getCode());	//-- DEPLOY1020-TB배포완료
			apiDeployInsertVo.setVerifiCd(ApiDeployResultCode.CD_1010_VERIFI_BASE_CODE.getCode());
			apiDeployInsertVo.setUseYn("Y");
			apiDeployInsertVo.setBigo("");  //최초 단계에서는 비고값을 입력하지 않음

			deployHstVo.setSuccessYn("Y");
			deployHstVo.setDeployApplySeq(0); //TB일경우 배포 SEQ를 기본 0으로 입력
			deployHstVo.setDeployGb(ApiDeployResultCode.CD_1020_DEPLOY_APPLY_CODE.getCode());
			deployHstVo.setDeployGb("T"); //TB일 경우 T세팅
		}else {
			apiDeployInsertVo.setDeployCd(ApiDeployResultCode.CD_1013_DEPLOY_APPLY_CODE.getCode());	//-- DEPLOY1013-TB배포실패
			apiDeployInsertVo.setVerifiCd(ApiDeployResultCode.CD_1010_VERIFI_BASE_CODE.getCode());
			apiDeployInsertVo.setUseYn("Y");
			apiDeployInsertVo.setBigo("");  //최초 단계에서는 비고값을 입력하지 않음

			deployHstVo.setSuccessYn("N");
			deployHstVo.setDeployApplySeq(0); //TB일경우 배포 SEQ를 기본 0으로 입력
			deployHstVo.setDeployGb(ApiDeployResultCode.CD_1013_DEPLOY_APPLY_CODE.getCode());
			deployHstVo.setDeployGb("T"); //TB일 경우 T세팅
		}

		String rtnSeq = updateDeployTbExcuteProc(apiDeployInsertVo, deployHstVo);

		return "200".equals(rtnSeq) ? ApiDeployResultCode.CD_RETURN_SUCCESS.getCode()
				: ApiDeployResultCode.CD_RETURN_FAIL.getCode();
	}

	//검증 시작
	//Request parameter vo ApiDeployInsertVo
	// ex) apiDeploySearchVo.setVerifiCd("VERIFI1020")  검증중 VERIFI1020 검증 완료 VERIFI1030
	//     apiDeploySearchVo.setSeq(23) 프로세스 테이블으 ㅣseq
	//     apiDeploySearchVo.setRegr("0001M7HGVS7AwV401M4R/0xqmg==");
	//     apiDeploySearchVo.setDeployCd("DEPLOY1030");
	@Override
	@Transactional(isolation = Isolation.READ_UNCOMMITTED, propagation = Propagation.REQUIRES_NEW)
	public String callPrivateVerifiProc(ApiDeploySearchVo apiDeploySearchVo) throws Exception {
		ApiDeployInsertVo apiDeployInsertVo = new ApiDeployInsertVo();

		ApiDeployVO deploySearchMap = selDeployView(apiDeploySearchVo);

		//넘어온 검증 코드가 1030이면 DEPLOY CODE도 1030
		apiDeployInsertVo.setVerifiCd((ApiDeployResultCode.CD_1030_DEPLOY_APPLY_CODE.getCode().equals(apiDeploySearchVo.getDeployCd())
				? ApiDeployResultCode.CD_1020_VERIFI_BASE_CODE.getCode() : ApiDeployResultCode.CD_1030_VERIFI_BASE_CODE.getCode()));

		apiDeployInsertVo.setUseYn("Y");
		apiDeployInsertVo.setBigo((deploySearchMap.getBigo() == null) ? "" : deploySearchMap.getBigo());
		apiDeployInsertVo.setDeployCd(apiDeploySearchVo.getDeployCd());
		apiDeployInsertVo.setSeq(apiDeploySearchVo.getSeq());
		apiDeployInsertVo.setRegr(apiDeploySearchVo.getRegr());
		apiDeployInsertVo.setDeployAdm(apiDeploySearchVo.getRegr());

		int rtnSeq = updateVerifiStartProc(apiDeployInsertVo);

		if (rtnSeq > 0) {
			if(ApiDeployResultCode.CD_1040_DEPLOY_APPLY_CODE.getCode().equals(apiDeploySearchVo.getDeployCd()) && "Y".equals(securityVerifyMail)) {
				Map<String, String> mailMap = new HashMap<>();
				MailVo mailVo = new MailVo();

				mailMap.put("tempId", ApiDeployResultCode.MAIL_TEMPLATE_SEC_CODE.getCode());
				mailMap.put("title", ApiDeployResultCode.MAIL_TITLE_1030.getMessage());

				ApiDefVO apiNmVo = this.selApiNmNSysNSpcByprocseq(apiDeploySearchVo);
				String contents = "· API명 : apiNm(시스템: sysNm, 서비스: spcNm)";
				contents = contents.replace("sysNm", apiNmVo.getSysNm() != null ? apiNmVo.getSysNm() : "")
				                   .replace("spcNm", apiNmVo.getSpcNm() != null ? apiNmVo.getSpcNm() : "")
				                   .replace("apiNm", apiNmVo.getApiNm() != null ? apiNmVo.getApiNm() : "");

				LOG.debug("contents ===> {}", contents);
				mailMap.put("content", contents);

				mailVo.setAutNm(ApiDeployResultCode.MAIL_MBR_SEC_ADMIN.getMessage());
				mailVo.setMailSendCode(ApiDeployResultCode.CD_1045_DEPLOY_APPLY_CODE.getCode());

				ApiEmailVO emailVo = this.selMailByUserid(apiDeploySearchVo);
				mailMap.put("sessionToMail", CommonFunc.safeDbDecrypt(emailVo.getEmail() != null ? emailVo.getEmail() : ""));

				this.mailSend(mailVo, mailMap);
			}
		}

		return rtnSeq > 0 ? ApiDeployResultCode.CD_RETURN_SUCCESS.getCode()
				: ApiDeployResultCode.CD_RETURN_FAIL.getCode();
	}

	//검증이력 저장
	//  VerifiResultVo
	// ex) verifiResultVo.getDeployProcSeq(23)  검증중 VERIFI1020 검증 완료 VERIFI1030
	//     verifiResultVo.setSeq(23) 프로세스 테이블으 ㅣseq
	//     verifiResultVo.setResultCd("000");
	//     verifiResultVo.setResultMsg("SUCCESS")
	//     verifiResultVo.setSuccessYn("Y")
	@Override
	@Transactional(isolation = Isolation.READ_UNCOMMITTED, propagation = Propagation.REQUIRES_NEW)
	public String callPrivateVerifiHst(VerifiResultVo verifiResultVo)    throws ApiException{
		int rtnSeq = updateVerifiExcuteProc(verifiResultVo);
		return rtnSeq > 0 ? ApiDeployResultCode.CD_RETURN_SUCCESS.getCode()
				: ApiDeployResultCode.CD_RETURN_FAIL.getCode();
	}


	//상용 배포
	// apiDeploySearchVo.setSeq(23)
	// apiDeploySearchVo.setDeployCd("DEPLOY1070") //상용 배포 성공
	// apiDeploySearchVo.setRegr("0001M7HGVS7AwV401M4R/0xqmg==");


	// deployApplyVo.setCbProSuccessYn("Y")
	// deployApplyVo.setSeq(9)
	// deployHstVo.setResultCd("000") GW로 부터 전달 받은 코드값
	// deployHstVo.setResultMsg("SUCCESS") GW로 부터 전달 받은 코드값
	@Override
	@Transactional(isolation = Isolation.READ_UNCOMMITTED, propagation = Propagation.REQUIRES_NEW)
	public String callPrivateCbDeploySuccess(DeployApplyVo deployApplyVo, ApiDeploySearchVo apiDeploySearchVo, DeployHstVo deployHstVo)   throws ApiException{
		ApiDeployInsertVo apiDeployInsertVo = new ApiDeployInsertVo();

		LOG.debug(" applySeq >>> {}", deployApplyVo.getSeq());
		deployApplyVo.setDeployApplySeq(deployApplyVo.getSeq());

		ApiDeployApplyVO deployApplyMap = this.selDeployApplyView(deployApplyVo);
		ApiDeployVO deploySearchMap = this.selDeployView(apiDeploySearchVo);

		// STEP 1 -- Deploy PROC UPDATE
		apiDeployInsertVo.setSeq(apiDeploySearchVo.getSeq());
		apiDeployInsertVo.setDeployCd(apiDeploySearchVo.getDeployCd());
		apiDeployInsertVo.setVerifiCd(deploySearchMap.getVerifiCd());
		apiDeployInsertVo.setRegr(apiDeploySearchVo.getRegr());
		apiDeployInsertVo.setDeployAdm(apiDeploySearchVo.getRegr());
		apiDeployInsertVo.setUseYn("Y");
		apiDeployInsertVo.setBigo((deploySearchMap.getBigo() == null) ? "" : deploySearchMap.getBigo());

		// STEP 2 -- KOA_DEPLOY_APPLY UPDATE
		deployApplyVo.setDeployCd(apiDeploySearchVo.getDeployCd());
		deployApplyVo.setDeployDesc("");
		deployApplyVo.setDeployAdm(apiDeploySearchVo.getRegr());
		deployApplyVo.setBigo((deployApplyMap.getBigo() == null) ? "" : deployApplyMap.getBigo());
		deployApplyVo.setComment("");

		// STEP 3 -- Deploy HISTORY INSERT
		deployHstVo.setDeployAdm(apiDeploySearchVo.getRegr());
		deployHstVo.setSuccessYn("Y".equals(deployApplyVo.getCbProSuccessYn()) ? "Y" : "N");
		deployHstVo.setDeployApplySeq(deployApplyVo.getSeq());
		deployHstVo.setDeployGb(ApiDeployResultCode.CD_DEPLOY_CB_GUBUN.getCode());
		deployHstVo.setDeployProcSeq(apiDeploySearchVo.getSeq());

		String rtnCode = updateDeployExcuteProc(apiDeployInsertVo, deployApplyVo, deployHstVo);

		return "200".equals(rtnCode) ? ApiDeployResultCode.CD_RETURN_SUCCESS.getCode()
				: ApiDeployResultCode.CD_RETURN_FAIL.getCode();
	}

	//tb배포 이력 보기
	@Override
	public List<ApiDeployHstVO> selApiTbDeployHst(DeployHstVo deployHstVo) throws ApiException{
		return apiDeployDao.selTbDeployHst(deployHstVo);
	}

	//test case list저장
	@Override
	@Transactional(isolation = Isolation.READ_UNCOMMITTED, propagation = Propagation.REQUIRES_NEW)
	public String insertTestCaseList(TestCaseListVo testCaseListVo) throws ApiException{
		int rtnSeq = apiDeployDao.insertTestCaseList(testCaseListVo);
		return rtnSeq > 0 ? ApiDeployResultCode.CD_RETURN_SUCCESS.getCode()
				: ApiDeployResultCode.CD_RETURN_FAIL.getCode();
	}

	//TEST CASE 조회
	@Override
	public List<ApiTestCaseVO> selTescCaseResult(TestCaseListVo testCaseListVo) throws ApiException{
		return apiDeployDao.selTescCaseResult(testCaseListVo);
	}

	//최신 등록 API리스트
	@Override
	public List<ApiDefVO> selApiList(TestCaseListVo testCaseListVo) throws ApiException{
		return apiDeployDao.selApiList(testCaseListVo);
	}

	//API TREE 리스트
	@Override
	public List<ApiTreeVO> selApiTreeList(ApiDeploySearchVo apiDeploySearchVo) throws ApiException{
		return apiDeployDao.selApiTreeList(apiDeploySearchVo);
	}

	//배포 반려
	@Override
	@Transactional(isolation = Isolation.READ_UNCOMMITTED, propagation = Propagation.REQUIRES_NEW)
	public String updateDeployReject(ApiDeployInsertVo apiDeployInsertVo, DeployApplyVo deployApplyVo)  throws ApiException{
		apiDeployDao.updateDeployProcDefault(apiDeployInsertVo);
		apiDeployDao.updateDeployApplyReject(deployApplyVo);
		return ApiDeployResultCode.CD_RETURN_SUCCESS.getCode();
	}

	//메일 발송
	@Override
	public List<ApiEmailVO> mailSend(MailVo mailVo, Map<String, String> mailMap ) {
		List<ApiEmailVO> mailSendList = null;
		mailSendList =  apiDeployDao.selMailList(mailVo);
		int size = mailSendList.size();

		if(mailMap.get("sessionToMail") != null  ) {
			LOG.error("[20200923][!@@!] 로그인 사용자 메일 주소 => {}", mailMap.get("sessionToMail"));
			mailMap.put("toMail", mailMap.get("sessionToMail"));
			sendMailUtil.sendMailcall(mailMap);
		}

		for( int i = 0; i < size; i++ ) {
			try {
				String email = mailSendList.get(i).getEmail();
				mailMap.put("toMail", CommonFunc.safeDbDecrypt(email != null ? email : ""));
				sendMailUtil.sendMailcall(mailMap);
			} catch (Exception e) {
				LOG.error("<<< 메일 발송 실패  >>>");
			}
		}

		return mailSendList;
	}

	//배포 프로세스 삭제
	@Override
	public int delDeployProc(DeployApplyVo deployApplyVo)  throws ApiException{
		return apiDeployDao.delDeployProc(deployApplyVo);
	}

	//API NM으로 API NO조회
	@Override
	public List<ApiDefVO> selDelApiSearch(ApiDeploySearchVo apiDeploySearchVo ) throws ApiException{
		List<ApiDefVO> map = apiDeployDao.selDelApiSearch(apiDeploySearchVo);
		return map;
	}

	//잘못 등록한 API삭제
	@Override
	public int delDeployApi(DeployApplyVo vo)  throws ApiException{
		return apiDeployDao.delDeployApi(vo);
	}

	//배포 신청  현황
	@Override
	public List<ApiDeployApplyVO> selDeployApplySearch(ApiDeploySearchVo apiDeploySearchVo ) throws ApiException{
		List<ApiDeployApplyVO> map = apiDeployDao.selDeployApplySearch(apiDeploySearchVo);
		return map;
	}

	//배포 신청 삭제
	@Override
	public int delDeployApply(DeployApplyVo vo)  throws ApiException{
		return apiDeployDao.delDeployApply(vo);
	}

	//배포 전체 현황
	@Override
	public List<ApiDeployStateVO> selDeployStateTotal(ApiDeploySearchVo apiDeploySearchVo ) throws ApiException{
		List<ApiDeployStateVO> map = apiDeployDao.selDeployStateTotal(apiDeploySearchVo);
		return map;
	}

	/*--[dep]
	//auth id로 sysid뽑기
	@Override
	public Map<String, Object> selSysIdByAutId(String str) throws ApiException{
		Map<String, Object> rtnMap = apiDeployDao.selSysIdByAutId(str);
		return rtnMap;
	}
	--*/
	
	//API등록자 및 수정자 메일 정보
	public ApiEmailVO selMailByApiRegrNAmdr(ApiDeploySearchVo vo) throws ApiException{
		return apiDeployDao.selMailByApiRegrNAmdr(vo);
	}

	//search email by userid
	public ApiEmailVO selMailByUserid(ApiDeploySearchVo vo ) throws ApiException{
		return apiDeployDao.selMailByUserid(vo);
	}

	//search APINM by PROCSEQ
	public String selApiNmByprocseq(ApiDeploySearchVo vo ) throws ApiException{
		ApiDefVO rtnMap = apiDeployDao.selApiNmByprocseq(vo);
		return rtnMap != null ? rtnMap.getApiNm() : null;
	}

	//search APINM & system & spc by PROCSEQ
	public ApiDefVO selApiNmNSysNSpcByprocseq(ApiDeploySearchVo vo ) throws ApiException{
		return apiDeployDao.selApiNmByprocseq(vo);
	}

	@Override
	public int apiVerNoUpdate(Map<String, String> apiVerNoUpdateInfo) throws ApiException {
		return apiDeployDao.apiVerNoUpdate(apiVerNoUpdateInfo);
	}
	
	
}