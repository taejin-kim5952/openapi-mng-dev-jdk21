/**
 *  OPEN API version 1.0
 *
 *  Copyright ⓒ 2017 kt corp. All rights reserved.
 *
 *  This is a proprietary software of kt corp, and you may not use this file except in
 *  compliance with license agreement with kt corp. Any redistribution or use of this
 *  software, with or without modification shall be strictly prohibited without prior written
 *  approval of kt corp, and the copyright notice above does not evidence any actual or
 *  intended publication of such software.
 *
 */
package com.kt.openapi.web.api.service.impl;

import com.kt.openapi.web.api.dao.ApiMainDAO;
import com.kt.openapi.web.api.dao.ApiRegDAO;
import com.kt.openapi.web.api.service.ApiMainService;
import com.kt.openapi.web.api.vo.*;
import com.kt.openapi.web.cmm.upload.UploadFileUtils;
import com.kt.openapi.web.cmm.vo.CmnFileVo;
import com.kt.openapi.web.util.CommonFunc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("apiMainService")
public class ApiMainServiceImpl implements ApiMainService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ApiMainServiceImpl.class);

	// TODO ibatis 사용
	@Autowired
	private ApiMainDAO apiMainDAO;

	@Autowired
	private ApiRegDAO apiRegDAO;

	@Autowired
    private UploadFileUtils uploadFileUtiles;

	/* (non-Javadoc)
	 * @see com.kt.openapi.web.api.service.ApiMainService#selMainRequestCnt(com.kt.openapi.web.api.vo.ApiMainVo)
	 */
	public int selMainRequestCnt(ApiMainVo vo) throws Exception{
		return apiMainDAO.selMainRequestCnt(vo);
	}

	/* (non-Javadoc)
	 * @see com.kt.openapi.web.api.service.ApiMainService#selMainList(com.kt.openapi.web.api.vo.ApiMainVo)
	 */
	public Map<String, Object> selMainList(ApiMainVo vo) throws Exception{
		Map<String, Object> returnMap = new HashMap<String, Object>();
		List<?> mainList = new ArrayList<>();
		List<ApiSpcVO> subList 	= new ArrayList<>();

		if((vo.getTabCode()).equals("1")) {
			vo.setRegSttusCd("APIREG1010");	// 작성중
			mainList 	= apiMainDAO.selDevelopeApiList(vo);
			subList 	= selMainApiSubVerList(mainList);

		}else if((vo.getTabCode()).equals("2")) {
			vo.setRegSttusCd("APIREG1030");	// 등록완료
			mainList 	= apiMainDAO.selDevelopeCompleteApiList(vo);
			subList 	= selMainApiSubVerList(mainList);

		}else if((vo.getTabCode()).equals("3")) {
			vo.setReviewRqtTypeCd("APIRQT1010");	// 검토의견
			mainList 	= apiMainDAO.selRequestList(vo);
			subList 	= selMainApiSubVerList(mainList);

		}else if((vo.getTabCode()).equals("4")) {
			vo.setReviewRqtTypeCd("APIRQT1020");	// 개발요청
			mainList = apiMainDAO.selRequestList(vo);
		}

		for(Object item : mainList) {
			String amdrNm = "";
			if (item instanceof ApiSpcVO) {
				amdrNm = ((ApiSpcVO) item).getAmdrNm();
			} else if (item instanceof ApiReviewVO) {
				amdrNm = ((ApiReviewVO) item).getAmdrNm();
			}
			
			if (amdrNm != null) {
				amdrNm = CommonFunc.safeDbDecrypt(amdrNm);
				if(amdrNm.length() > 1) {
					amdrNm = amdrNm.substring(0, amdrNm.length()-1);
					amdrNm = amdrNm+"*";
				}
				
				if (item instanceof ApiSpcVO) {
					((ApiSpcVO) item).setAmdrNm(amdrNm);
				} else if (item instanceof ApiReviewVO) {
					((ApiReviewVO) item).setAmdrNm(amdrNm);
				}
			}
		}

		for(ApiSpcVO egovVO : subList) {
			String amdrNm = egovVO.getAmdrNm();
			if (amdrNm != null) {
				amdrNm = CommonFunc.safeDbDecrypt(amdrNm);
				if(amdrNm.length() > 1) {
					amdrNm = amdrNm.substring(0, amdrNm.length()-1);
					amdrNm = amdrNm+"*";
				}
				egovVO.setAmdrNm(amdrNm);
			}
		}

		returnMap.put("mainList", mainList);
		returnMap.put("subList", subList);

		return returnMap;

	}

	/* (non-Javadoc)
	 * @see com.kt.openapi.web.api.service.ApiMainService#selMainListTotalCnt(com.kt.openapi.web.api.vo.ApiMainVo)
	 */
	public int selMainListTotalCnt(ApiMainVo vo) throws Exception{
		int returnValue = 0;

		if((vo.getTabCode()).equals("1")) {
			vo.setRegSttusCd("APIREG1010");	// 작성중
			returnValue 	= apiMainDAO.selDevelopeApiTotalCnt(vo);

		}else if((vo.getTabCode()).equals("2")) {
			vo.setRegSttusCd("APIREG1030");	// 등록완료
			returnValue 	= apiMainDAO.selDevelopeCompleteApitotalCnt(vo);

		}else if((vo.getTabCode()).equals("3")) {
			vo.setReviewRqtTypeCd("APIRQT1010");	// 검토의견
			returnValue 	= apiMainDAO.selMainListTotalCnt(vo);

		}else if((vo.getTabCode()).equals("4")) {
			vo.setReviewRqtTypeCd("APIRQT1020");	// 개발요청
			returnValue 	= apiMainDAO.selMainListTotalCnt(vo);
		}
		return returnValue;
	}

	/* (non-Javadoc)
	 * @see com.kt.openapi.web.api.service.ApiMainService#savApiVer(com.kt.openapi.web.api.vo.ApiMainVo)
	 */
	@Transactional(rollbackFor={Exception.class})
	public Map<String, Object> savApiVer(ApiMainVo vo) throws Exception{
		Map<String, Object> returnMap = new HashMap<String, Object>();

		ApiMainVo orgVo = new ApiMainVo();
		orgVo.setApiSpcNo(vo.getApiSpcNo());
		orgVo.setRegr(vo.getRegr());
		orgVo.setAmdr(vo.getRegr());

		LOGGER.debug("==== savApiVer ============================= START ");

		LOGGER.debug("0==== vo.getApiSpcNo()   " + vo.getApiSpcNo());
		LOGGER.debug("0==== orgVo.getApiSpcNo()   " + orgVo.getApiSpcNo());

		ApiSpcVO preInfo = apiMainDAO.selReqInfo1(vo);
		String yamlSbst = preInfo.getYamlSbst();

		LOGGER.debug("0==== vo.getVer()   =" + vo.getVer());
		LOGGER.debug("0==== preInfo.getVer()   =" + preInfo.getVer());

		if(vo.getVer() != null && !vo.getVer().equals("")) {
			String version = preInfo.getVer();
			String replaceStr1 = "version: \'" + version+"\'";
			String replaceStr2 = "version: \'" + vo.getVer()+"\'";

			LOGGER.debug("1==== replaceStr1   " + replaceStr1);
			LOGGER.debug("1==== replaceStr2   " + replaceStr2);

			LOGGER.debug("1==== yamlSbst   " + yamlSbst);
			yamlSbst = yamlSbst.replaceAll(replaceStr1, replaceStr2 );
			LOGGER.debug("2==== yamlSbst   " + yamlSbst);
			vo.setImportType("1");
		}else {
			vo.setVer(preInfo.getVer());
			vo.setVerDesc(preInfo.getVerDesc()+"");
			vo.setImportType("2");
		}

		vo.setYamlSbst(yamlSbst);

		orgVo.setYamlSbst(yamlSbst);// 추가
		// API 등록
		String newApiSpcNo =  apiMainDAO.savApiVerUpApiSpc(vo);

		LOGGER.debug("1==== newApiSpcNo   " + newApiSpcNo);
		LOGGER.debug("1==== vo.getApiSpcNo()   " + vo.getApiSpcNo());
		LOGGER.debug("1==== orgVo.getApiSpcNo()   " + orgVo.getApiSpcNo());

		returnMap.put("apiSpcNo",newApiSpcNo);

		vo.setNewApiSpcNo(newApiSpcNo);
		orgVo.setNewApiSpcNo(newApiSpcNo);

		CmnFileVo fileVo = uploadFileUtiles.transferYamlFile( preInfo.getYamlFilePath(),  preInfo.getYamlFileNm()  , newApiSpcNo , "yaml");

		orgVo.setYamlFilePath(fileVo.getFilePath());
		orgVo.setYamlFileNm(fileVo.getSaveFileName());
		int returnInt =  apiMainDAO.updApiSpcFileInfo(orgVo);

		LOGGER.debug("2==== vo.getApiSpcNo()   " + vo.getApiSpcNo());
		LOGGER.debug("2==== orgVo.getApiSpcNo()   " + orgVo.getApiSpcNo());
		LOGGER.debug("2==== orgVo.getNewApiSpcNo()   " + orgVo.getNewApiSpcNo());

		insertApiVerUpDep(orgVo);

		return returnMap;
	}


	/* (non-Javadoc)
	 * @see com.kt.openapi.web.api.service.ApiMainService#insertApiVerUpDep(com.kt.openapi.web.api.vo.ApiMainVo)
	 */
	@Transactional(rollbackFor={Exception.class})
	public void insertApiVerUpDep(ApiMainVo orgVo) throws Exception{

		LOGGER.debug("==== insertApiVerUpDep ===========================  START ");

		// 1. API카테고리와  API정의에 존재하는 데이터 조회
		List<ApiSpcVO> aList1 = apiMainDAO.selApiInfoList1(orgVo);

		String selApiCtgryNo= "";
		String selApiNo		= "";

		LOGGER.debug("==== aList1 -- {}" , aList1.size());

		for(ApiSpcVO eMap : aList1) {

			String apiCtgryNo 	= eMap.getApiCtgryNo();
			String apiNo 		= eMap.getApiNo();

			LOGGER.debug("==== ==== apiCtgryNo = {} , apiNo = {}" , apiCtgryNo , apiNo);

			String newApiCtgryNo	= "";
			String newApiNo			= "";

			if(!selApiCtgryNo.equals(apiCtgryNo)) {

//				vo.setApiCtgryNo(apiCtgryNo);
				orgVo.setApiCtgryNo(apiCtgryNo);

				newApiCtgryNo	= apiMainDAO.savApiVerUpApiCtgry(orgVo);

			}

			LOGGER.debug("==== ==== newApiCtgryNo = {} " , newApiCtgryNo );

			if(!selApiNo.equals(apiNo)) {

//				vo.setNewApiCtgryNo(newApiCtgryNo);
//				vo.setNewApiSpcNo(newApiSpcNo);
//				vo.setApiNo(apiNo);

				orgVo.setNewApiCtgryNo(newApiCtgryNo);
//				orgVo.setNewApiSpcNo(newApiSpcNo);
				orgVo.setApiNo(apiNo);

				newApiNo	= apiMainDAO.savApiVerUpApiDef(orgVo);

				LOGGER.debug("==== ==== newApiNo = {} " , newApiNo );

//				vo.setApiNo(apiNo);
//				vo.setNewApiNo(newApiNo);

				orgVo.setApiNo(apiNo);
				orgVo.setNewApiNo(newApiNo);

				insertApiParam(orgVo);

				String yamlSbst = orgVo.getYamlSbst();
				String replaceStr1 = "apiNo: \'" + apiNo+"\'";
				String replaceStr2 = "apiNo: \'" + newApiNo+"\'";
				yamlSbst = yamlSbst.replaceAll(replaceStr1, replaceStr2 );
				replaceStr1 = "x-apiNo: \'" + apiNo+"\'";
				replaceStr2 = "x-apiNo: \'" + newApiNo+"\'";
				yamlSbst = yamlSbst.replaceAll(replaceStr1, replaceStr2 );
				ApiRegVO  avo = new ApiRegVO();
				avo.setYamlSbst(yamlSbst);
				avo.setApiSpcNo(orgVo.getNewApiSpcNo());
				avo.setRegr(orgVo.getRegr());

				int apiCnt = apiRegDAO.updApiYamlInfo(avo); // yaml 내용 중 apiNo도 최신 정보로 변경 한다.
			}
			selApiCtgryNo = apiCtgryNo;
			selApiNo	  = apiNo;
		}

		// 1. 카테고리 값이 없는 api테이블 조회
		// API 카테고리 등록
		selApiCtgryNo	= "";
		selApiNo		= "";

		List<ApiSpcVO> aList2 = apiMainDAO.selApiInfoList2(orgVo);

		LOGGER.debug("==== aList2 -- {}" , aList2.size());

		for(ApiSpcVO eMap : aList2) {

			String apiNo 		= eMap.getApiNo();
			String newApiNo		= "";

			if(!selApiNo.equals(apiNo)) {

//				vo.setNewApiCtgryNo("");
//				vo.setNewApiSpcNo(newApiSpcNo);
//				vo.setApiNo(apiNo);


				orgVo.setNewApiCtgryNo("");
//				orgVo.setNewApiSpcNo(newApiSpcNo);
				orgVo.setApiNo(apiNo);

				newApiNo	= apiMainDAO.savApiVerUpApiDef(orgVo);

//				vo.setApiNo(apiNo);
//				vo.setNewApiNo(newApiNo);

				orgVo.setApiNo(apiNo);
				orgVo.setNewApiNo(newApiNo);

				insertApiParam(orgVo);

				String yamlSbst = orgVo.getYamlSbst();
				String replaceStr1 = "apiNo: \'" + apiNo+"\'";
				String replaceStr2 = "apiNo: \'" + newApiNo+"\'";
				yamlSbst = yamlSbst.replaceAll(replaceStr1, replaceStr2 );
				replaceStr1 = "x-apiNo: \'" + apiNo+"\'";
				replaceStr2 = "x-apiNo: \'" + newApiNo+"\'";
				yamlSbst = yamlSbst.replaceAll(replaceStr1, replaceStr2 );
				ApiRegVO  avo = new ApiRegVO();
				avo.setYamlSbst(yamlSbst);
				avo.setApiSpcNo(orgVo.getNewApiSpcNo());
				avo.setRegr(orgVo.getRegr());

				int apiCnt = apiRegDAO.updApiYamlInfo(avo); // yaml 내용 중 apiNo도 최신 정보로 변경 한다.
			}
			selApiNo = apiNo;
		}

		// 3. 카테고리 데이터만 존재

		LOGGER.debug("==== 3. 카테고리 데이터만 존재 ");

		List<ApiSpcVO> aList3 = apiMainDAO.selApiInfoOnlyCateList(orgVo);

		for(ApiSpcVO eMap : aList3) {

			String apiCtgryNo 	= eMap.getApiCtgryNo();

			LOGGER.debug("==== ==== apiCtgryNo = {}  orgVo.getNewApiSpcNo() = {} " , apiCtgryNo  , orgVo.getNewApiSpcNo());

			orgVo.setApiCtgryNo(apiCtgryNo);

			apiMainDAO.savApiVerUpApiCtgry(orgVo);

		}

		LOGGER.debug("==== savApiVer ============================= END ");
	}

	/**
	* <pre>
	* 1. 메소드명 : insertApiParam
	* 2. 작성일 : 2017. 12. 5. 오후 3:30:17
	* 3. 작성자 : JungHwan Hwang
	* 4. 설명 :  버전업/Private전환 일 경우에 , API 파라미터 테이블 저장.
	* </pre>
	* @param vo
	* @throws Exception
	*/
	@Transactional(rollbackFor={Exception.class})
	public void insertApiParam(ApiMainVo vo) throws Exception{
		LOGGER.debug("==== ==== ==== insertApiParam START "  );
		// 본인, 본인의 신규
		List<ApiSpcVO> paramList = apiMainDAO.selApiInfoParamList(vo);

		LOGGER.debug("==== ==== ==== paramList ={} " ,paramList.size() );

		List<String> orgParamNoStrs = new ArrayList<>();
		List<String> newParamNoStrs = new ArrayList<>();

		for(ApiSpcVO eMap1 : paramList) {
			//ApiMainVo paramVo = new ApiMainVo();
			String eMap1ParntsParamNo 	= eMap1.getPrntsParamNo() == null ? "" : eMap1.getPrntsParamNo();
			String eMap1ParamNo 		= eMap1.getParamNo();
			orgParamNoStrs.add(eMap1ParamNo);

			LOGGER.debug("==== ==== ==== eMap1ParntsParamNo ={} " ,eMap1ParntsParamNo );
			LOGGER.debug("==== ==== ==== eMap1ParamNo ={} " ,eMap1ParamNo );

			String newParamNo = "";

			for(int k=0 ;  k < orgParamNoStrs.size() ; k++ ) {
				String orgParamNo = orgParamNoStrs.get(k);
				if(orgParamNo.equals(eMap1ParntsParamNo)) {
					newParamNo = newParamNoStrs.get(k);
				}
			}

			vo.setPrntsParamNo(newParamNo);
			vo.setParamNo(eMap1ParamNo);
			newParamNo	= apiMainDAO.savApiVerUpApiParam(vo);

			newParamNoStrs.add(newParamNo);

		}

		LOGGER.debug("==== ==== ==== insertApiParam END "  );

	}

	@Override
	@Transactional(rollbackFor={Exception.class})
	public String savDevReqReg(ApiMainVo vo) throws Exception{
		apiMainDAO.savDevReqReg(vo);
		return vo.getApiReviewRqtNo();
	}


	/* (non-Javadoc)
	 * @see com.kt.openapi.web.api.service.ApiMainService#selSysList(com.kt.openapi.web.api.vo.ApiMainVo)
	 */
	public List<ApiSystemVO> selSysList(ApiMainVo vo) throws Exception{
		return apiMainDAO.selSysList(vo);
	}

	/* (non-Javadoc)
	 * @see com.kt.openapi.web.api.service.ApiMainService#savReqFile(com.kt.openapi.web.api.vo.ApiMainVo)
	 */
	@Transactional(rollbackFor={Exception.class})
	public int savReqFile(ApiMainVo vo) throws Exception{
		return apiMainDAO.savReqFile(vo);

	}

	/* (non-Javadoc)
	 * @see com.kt.openapi.web.api.service.ApiMainService#selFileList(com.kt.openapi.web.api.vo.ApiMainVo)
	 */
	public List<ApiReviewAtcVO> selFileList(ApiMainVo vo) throws Exception{
		return apiMainDAO.selFileList(vo);
	}

	/* (non-Javadoc)
	 * @see com.kt.openapi.web.api.service.ApiMainService#savDevReplyReg(com.kt.openapi.web.api.vo.ApiMainVo)
	 */
	@Transactional(rollbackFor={Exception.class})
	public int savDevReplyReg(ApiMainVo vo) throws Exception{
		return apiMainDAO.savDevReplyReg(vo);
	}


	/* (non-Javadoc)
	 * @see com.kt.openapi.web.api.service.ApiMainService#selRegistView(com.kt.openapi.web.api.vo.ApiMainVo)
	 */
	public ApiReviewVO selRegistView(ApiMainVo vo) throws Exception{

		ApiReviewVO apiReviewVO = apiMainDAO.selRegistView(vo);

		if (apiReviewVO != null) {
			String amdrNm = CommonFunc.safeDbDecrypt(apiReviewVO.getAmdrNm());
			String cmpnNm = CommonFunc.safeDbDecrypt(apiReviewVO.getCmpnNm());

			if(amdrNm.length() > 1) {
				amdrNm = amdrNm.substring(0, amdrNm.length()-1);
				amdrNm = amdrNm+"*";
			}
			apiReviewVO.setAmdrNm(amdrNm);

			if(cmpnNm != null && cmpnNm.length() > 1) {
				cmpnNm = cmpnNm.substring(0, cmpnNm.length()-1);
				cmpnNm = cmpnNm+"*";
			}
			apiReviewVO.setCmpnNm(cmpnNm);
		}

		return apiReviewVO;
	}

	/* (non-Javadoc)
	 * @see com.kt.openapi.web.api.service.ApiMainService#selReqReplyView(com.kt.openapi.web.api.vo.ApiMainVo)
	 */
	public List<ApiReviewOpinVO> selReqReplyView(ApiMainVo vo) throws Exception{
		List<ApiReviewOpinVO> list = apiMainDAO.selReqReplyView(vo);
		if(list!=null) {
			for(ApiReviewOpinVO voItem : list) {
				String amdrNm = CommonFunc.safeDbDecrypt(voItem.getAmdrNm());

				if(amdrNm.length() > 1) {
					amdrNm = amdrNm.substring(0, amdrNm.length()-1);
					amdrNm = amdrNm+"*";
				}
				voItem.setAmdrNm(amdrNm);

			}
		}
		return list;
	}


	/* (non-Javadoc)
	 * @see com.kt.openapi.web.api.service.ApiMainService#delDevApi(com.kt.openapi.web.api.vo.ApiMainVo)
	 */
	@Transactional(rollbackFor={Exception.class})
	public int delDevApi(ApiMainVo vo) throws Exception{
		ApiRegVO  avo = new ApiRegVO();
		avo.setNewApiSpcNo(vo.getApiSpcNo());
		int cnt =  apiRegDAO.delApiAllPathParamType(avo);
		cnt +=  apiRegDAO.delApiAllPathParam(avo);//api param 삭제
		cnt +=  apiRegDAO.delApiSpcImpact(avo);//api 명세 하위로 붙는 api Impact들 삭제
		cnt +=  apiRegDAO.delApiSpcPath(avo);//api 명세 하위로 붙는 api 들 삭제
		cnt +=  apiMainDAO.delDevApi(vo);//작업중인 api 명세 삭제

		return cnt;
	}


	/**
	* <pre>
	* 1. 메소드명 : selMainApiSubVerList
	* 2. 작성일 : 2017. 11. 21. 오전 11:05:09
	* 3. 작성자 : JungHwan Hwang
	* 4. 설명 : 조회된 목록에 이전버전으로 등록완료된 api목록 조회
	* </pre>
	* @param list
	* @return
	* @throws Exception
	*/
	public List<ApiSpcVO> selMainApiSubVerList(List<?> list) throws Exception{
		List<ApiSpcVO> aList = new ArrayList<>();

		LOGGER.info("selMainApiSubVerList=== START");

		if(list != null && !list.isEmpty()) {
			List<String> apiSpcIds = new ArrayList<>();

			for(Object item : list) {
				String apiSpcId = "";
				if (item instanceof ApiSpcVO) {
					apiSpcId = ((ApiSpcVO) item).getApiSpcId();
				} else if (item instanceof ApiReviewVO) {
					apiSpcId = ((ApiReviewVO) item).getApiSpcId();
				}
				
				if (apiSpcId != null && !apiSpcId.isEmpty()) {
					apiSpcIds.add(apiSpcId);
				}
			}

			if (!apiSpcIds.isEmpty()) {
				ApiMainVo apiMainVo = new ApiMainVo();
				apiMainVo.setApiSpcIdList(apiSpcIds);
				aList = apiMainDAO.selMainApiSubVerList(apiMainVo);
			}
		}
		return aList;

	}


	public ApiSpcVO selReqInfo(ApiMainVo vo) throws Exception{
		if((vo.getTabCode()).equals("3")) {
			return apiMainDAO.selReqInfo1(vo);//
		}else {
			return apiMainDAO.selReqInfo2(vo);//
		}
	}


	/* (non-Javadoc)
	 * @see com.kt.openapi.web.api.service.ApiMainService#salApiVerDupCheck(com.kt.openapi.web.api.vo.ApiMainVo)
	 */
	public int salApiVerDupCheck(ApiMainVo vo) throws Exception{
		return apiMainDAO.salApiVerDupCheck(vo);
	}

	/* (non-Javadoc)
	 * @see com.kt.openapi.web.api.service.ApiMainService#savApiPrivate(com.kt.openapi.web.api.vo.ApiMainVo)
	 * [tag:adpt][drm][add]
	 * API Private전환
	 */
	@Transactional(rollbackFor={Exception.class})
	public Map<String, Object> savApiPrivate(ApiMainVo vo) throws Exception {
		Map<String, Object> returnMap = new HashMap<>();

		ApiMainVo orgVo = new ApiMainVo();
		orgVo.setApiSpcNo(vo.getApiSpcNo());
		orgVo.setRegr(vo.getRegr());
		orgVo.setAmdr(vo.getRegr());

		ApiSpcVO preInfo = apiMainDAO.selReqInfo1(vo);

		// API 등록
		apiMainDAO.savApiPrivateApiSpc(vo);
		String newApiSpcNo = vo.getApiSpcNo();

		returnMap.put("apiSpcNo", newApiSpcNo);
		LOGGER.debug("==== newApiSpcNo   " + newApiSpcNo);

		orgVo.setNewApiSpcNo(newApiSpcNo);

		CmnFileVo fileVo = uploadFileUtiles.transferYamlFile(preInfo.getYamlFilePath(), preInfo.getYamlFileNm(), newApiSpcNo, "yaml");

		orgVo.setYamlFilePath(fileVo.getFilePath());
		orgVo.setYamlFileNm(fileVo.getSaveFileName());
		int returnInt =  apiMainDAO.updApiSpcFileInfo(orgVo);

		String yamlSbst = preInfo.getYamlSbst();
		orgVo.setYamlSbst(yamlSbst);

		insertApiPrivateDep(orgVo);

		return returnMap;
	}

	/* (non-Javadoc)
	 * @see com.kt.openapi.web.api.service.ApiMainService#insertApiPrivateDep(com.kt.openapi.web.api.vo.ApiMainVo)
	 */
	@Transactional(rollbackFor={Exception.class})
	public void insertApiPrivateDep(ApiMainVo orgVo) throws Exception{
		LOGGER.debug("==== insertApiPrivateDep ===========================  START ");

		// 1. API카테고리와  API정의에 존재하는 데이터 조회
		List<ApiSpcVO> aList1 = apiMainDAO.selApiInfoList1(orgVo);

		String selApiCtgryNo= "";
		String selApiNo		= "";

		LOGGER.debug("==== aList1 -- {}" , aList1.size());

		for(ApiSpcVO eMap : aList1) {
			String apiCtgryNo 	= eMap.getApiCtgryNo();
			String apiNo 		= eMap.getApiNo();

			LOGGER.debug("==== ==== apiCtgryNo = {} , apiNo = {}" , apiCtgryNo , apiNo);

			String newApiCtgryNo	= "";
			String newApiNo			= "";

			if(!selApiCtgryNo.equals(apiCtgryNo)) {
				orgVo.setApiCtgryNo(apiCtgryNo);
				newApiCtgryNo	= apiMainDAO.savApiVerUpApiCtgry(orgVo);
			}

			LOGGER.debug("==== ==== newApiCtgryNo = {} " , newApiCtgryNo );

			if(!selApiNo.equals(apiNo)) {
				orgVo.setNewApiCtgryNo(newApiCtgryNo);
				orgVo.setApiNo(apiNo);

				newApiNo	= apiMainDAO.savApiVerUpApiDef(orgVo);
				LOGGER.debug("==== ==== newApiNo = {} " , newApiNo );

				orgVo.setApiNo(apiNo);
				orgVo.setNewApiNo(newApiNo);

				insertApiParam(orgVo);

				String yamlSbst = orgVo.getYamlSbst();
				String replaceStr1 = "apiNo: \'" + apiNo + "\'";
				String replaceStr2 = "apiNo: \'" + newApiNo + "\'";
				yamlSbst = yamlSbst.replaceAll(replaceStr1, replaceStr2);
				replaceStr1 = "x-apiNo: \'" + apiNo + "\'";
				replaceStr2 = "x-apiNo: \'" + newApiNo + "\'";
				yamlSbst = yamlSbst.replaceAll(replaceStr1, replaceStr2);
				ApiRegVO  avo = new ApiRegVO();
				avo.setYamlSbst(yamlSbst);
				avo.setApiSpcNo(orgVo.getNewApiSpcNo());
				avo.setRegr(orgVo.getRegr());

				int apiCnt = apiRegDAO.updApiYamlInfo(avo); // yaml 내용 중 apiNo도 최신 정보로 변경 한다.
			}
			selApiCtgryNo = apiCtgryNo;
			selApiNo	  = apiNo;
		}

		// 1. 카테고리 값이 없는 api테이블 조회
		// API 카테고리 등록
		selApiCtgryNo	= "";
		selApiNo		= "";

		List<ApiSpcVO> aList2 = apiMainDAO.selApiInfoList2(orgVo);

		LOGGER.debug("==== aList2 -- {}" , aList2.size());

		for(ApiSpcVO eMap : aList2) {
			String apiNo 		= eMap.getApiNo();
			String newApiNo		= "";

			if(!selApiNo.equals(apiNo)) {
				orgVo.setNewApiCtgryNo("");
				orgVo.setApiNo(apiNo);

				newApiNo	= apiMainDAO.savApiVerUpApiDef(orgVo);
				LOGGER.debug("==== ==== newApiNo = {} " , newApiNo );

				orgVo.setApiNo(apiNo);
				orgVo.setNewApiNo(newApiNo);

				insertApiParam(orgVo);

				String yamlSbst = orgVo.getYamlSbst();
				String replaceStr1 = "apiNo: \'" + apiNo + "\'";
				String replaceStr2 = "apiNo: \'" + newApiNo + "\'";
				yamlSbst = yamlSbst.replaceAll(replaceStr1, replaceStr2);
				replaceStr1 = "x-apiNo: \'" + apiNo + "\'";
				replaceStr2 = "x-apiNo: \'" + newApiNo + "\'";
				yamlSbst = yamlSbst.replaceAll(replaceStr1, replaceStr2);
				ApiRegVO  avo = new ApiRegVO();
				avo.setYamlSbst(yamlSbst);
				avo.setApiSpcNo(orgVo.getNewApiSpcNo());
				avo.setRegr(orgVo.getRegr());

				int apiCnt = apiRegDAO.updApiYamlInfo(avo); // yaml 내용 중 apiNo도 최신 정보로 변경 한다.
			}
			selApiNo = apiNo;
		}

		// 3. 카테고리 데이터만 존재
		LOGGER.debug("==== 3. 카테고리 데이터만 존재 ");

		List<ApiSpcVO> aList3 = apiMainDAO.selApiInfoOnlyCateList(orgVo);
		for(ApiSpcVO eMap : aList3) {
			String apiCtgryNo 	= eMap.getApiCtgryNo();

			LOGGER.debug("==== ==== apiCtgryNo = {}  orgVo.getNewApiSpcNo() = {} " , apiCtgryNo  , orgVo.getNewApiSpcNo());

			orgVo.setApiCtgryNo(apiCtgryNo);
			apiMainDAO.savApiVerUpApiCtgry(orgVo);
		}

		LOGGER.debug("==== insertApiPrivateDep ============================= END ");
	}
	
	public ApiSpcVO selYamlInfo(ApiMainVo vo) throws Exception{
		return apiMainDAO.selReqInfo1(vo);
	}
}
