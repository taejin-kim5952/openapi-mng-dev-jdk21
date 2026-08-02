package com.kt.openapi.web.mypage.service.impl;

import com.kt.openapi.web.mypage.dao.MypageDAO;
import com.kt.openapi.web.mypage.service.MypageService;
import com.kt.openapi.web.mypage.vo.MypageVO;
import com.kt.openapi.web.userJoin.vo.UserHistVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 
* <pre>
* 1. 패키지명 : com.kt.openapi.web.mypage.service.impl
* 2. 타입명 : MypageServiceImpl.java
* 3. 작성일 : 2017. 11. 30. 오후 2:37:59
* 4. 작성자 : ANEUNTAEK
* 5. 설명 : MYPAGE
* </pre>
 */
@Service("mypageService")
public class MypageServiceImpl implements MypageService {

	private static final Logger log = LoggerFactory.getLogger(MypageServiceImpl.class);
	
	@Autowired
	private MypageDAO mypageDAO;
	
	/**
	 * 
	* <pre>
	* 1. 메소드명 : getApiRevCnt
	* 2. 작성일 : 2017. 11. 30. 오후 2:31:52
	* 3. 작성자 : ANEUNTAEK
	* 4. 설명 : API 검토 요청 건수 및 API 개발 요청 건수
	* </pre>
	* @param param
	* @return
	* @throws Exception
	 */
	@Override
	public int getApiRevCnt(MypageVO param) throws Exception {
		// TODO Auto-generated method stub
		return mypageDAO.getApiRevCnt(param);
	}

	/**
	 * 
	* <pre>
	* 1. 메소드명 : selWriteApi
	* 2. 작성일 : 2017. 11. 30. 오후 2:32:10
	* 3. 작성자 : ANEUNTAEK
	* 4. 설명 : 작성중 API 상세보기
	* </pre>
	* @param param
	* @return
	* @throws Exception
	 */
	@Override
	public Map<String, Object> selWriteApi(MypageVO param) throws Exception {
		// TODO Auto-generated method stub
		return mypageDAO.selWriteApi(param);
	}

	/**
	 * 
	* <pre>
	* 1. 메소드명 : selDevFView
	* 2. 작성일 : 2017. 11. 30. 오후 2:32:16
	* 3. 작성자 : ANEUNTAEK
	* 4. 설명 : 나의 개발자 포럼 최신글 1개 
	* </pre>
	* @param param
	* @return
	* @throws Exception
	 */
	@Override
	public Map<String, Object> selDevFView(MypageVO param) throws Exception {
		// TODO Auto-generated method stub
		return mypageDAO.selDevFView(param);
	}

	/**
	 * 
	* <pre>
	* 1. 메소드명 : selDevFCnt
	* 2. 작성일 : 2017. 11. 30. 오후 2:32:23
	* 3. 작성자 : ANEUNTAEK
	* 4. 설명 : 나의 개발자 포럼 댓글 개수
	* </pre>
	* @param param
	* @return
	* @throws Exception
	 */
	@Override
	public int selDevFCnt(MypageVO param) throws Exception {
		// TODO Auto-generated method stub
		return mypageDAO.selDevFCnt(param);
	}

	/**
	 * 
	* <pre>
	* 1. 메소드명 : selQAView
	* 2. 작성일 : 2017. 11. 30. 오후 2:32:29
	* 3. 작성자 : ANEUNTAEK
	* 4. 설명 : 나의 Q&A 상세글
	* </pre>
	* @param param
	* @return
	* @throws Exception
	 */
	@Override
	public Map<String, Object> selQAView(MypageVO param) throws Exception {
		// TODO Auto-generated method stub
		return mypageDAO.selQAView(param);
	}

	/**
	 * 
	* <pre>
	* 1. 메소드명 : selAutList
	* 2. 작성일 : 2017. 11. 30. 오후 2:32:35
	* 3. 작성자 : ANEUNTAEK
	* 4. 설명 : 나의 보유 권한 최대 5개
	* </pre>
	* @param svo
	* @return
	* @throws Exception
	 */
	@Override
	public List<Map<String, Object>> selAutList(MypageVO param) throws Exception {
		// TODO Auto-generated method stub
		return mypageDAO.selAutList(param);
	}

	/**
	 * 
	* <pre>
	* 1. 메소드명 : selAutReq
	* 2. 작성일 : 2017. 11. 30. 오후 2:32:42
	* 3. 작성자 : ANEUNTAEK
	* 4. 설명 : 권한요청 상태 최대 5개
	* </pre>
	* @param svo
	* @return
	* @throws Exception
	 */
	@Override
	public List<Map<String, Object>> selAutReq(MypageVO param) throws Exception {
		// TODO Auto-generated method stub
		return mypageDAO.selAutReq(param);
	}

	/**
	 * 
	* <pre>
	* 1. 메소드명 : newAutReq
	* 2. 작성일 : 2017. 11. 30. 오후 2:32:48
	* 3. 작성자 : ANEUNTAEK
	* 4. 설명 : 권한 요청 등록
	* </pre>
	* @param param
	* @throws Exception
	 */
	@Override
	@Transactional(rollbackFor={Exception.class})
	public void newAutReq(MypageVO param) throws Exception {
		// TODO Auto-generated method stub
		mypageDAO.newAutReq(param);
	}

	/**
     * 
    * <pre>
    * 1. 메소드명 : selboxSysNm
    * 2. 작성일 : 2017. 11. 30. 오후 2:32:54
    * 3. 작성자 : ANEUNTAEK
    * 4. 설명 : 시스템 목록
    * </pre>
    * @param param
    * @return
    * @throws Exception
     */
	@Override
	public List<Map<String, Object>> selboxSysNm(MypageVO param) throws Exception {
		// TODO Auto-generated method stub
		return mypageDAO.selboxSysNm(param);
	}

	/**
     * 
    * <pre>
    * 1. 메소드명 : selboxAGroup
    * 2. 작성일 : 2017. 11. 30. 오후 2:33:00
    * 3. 작성자 : ANEUNTAEK
    * 4. 설명 : 권한 그룹 목록
    * </pre>
    * @param param
    * @return
    * @throws Exception
     */
	@Override
	public List<Map<String, Object>> selboxAGroup(MypageVO param) throws Exception {
		// TODO Auto-generated method stub
		return mypageDAO.selboxAGroup(param);
	}

	/**
     * 
    * <pre>
    * 1. 메소드명 : selBack
    * 2. 작성일 : 2017. 11. 30. 오후 2:33:09
    * 3. 작성자 : ANEUNTAEK
    * 4. 설명 : 반려 사유
    * </pre>
    * @param param
    * @return
    * @throws Exception
     */
	@Override
	public Map<String, Object> selBack(MypageVO param) throws Exception {
		// TODO Auto-generated method stub
		return mypageDAO.selBack(param);
	}
	
	/**
     * 
    * <pre>
    * 1. 메소드명 : autHist
    * 2. 작성일 : 2017. 11. 30. 오후 2:33:14
    * 3. 작성자 : ANEUNTAEK
    * 4. 설명 : 회원 관리 이력
    * </pre> 
    * @param param
    * @throws Exception
     */
	@Override
	@Transactional(rollbackFor={Exception.class})
	public void autHist(UserHistVO param) throws Exception {
		// TODO Auto-generated method stub
		mypageDAO.autHist(param);
	}

	/**
	 * 
	* <pre>
	* 1. 메소드명 : updateMbr
	* 2. 작성일 : 2017. 12. 5. 오후 4:05:10
	* 3. 작성자 : ANEUNTAEK
	* 4. 설명 : 회원 상태 수정
	* </pre>
	* @param param
	* @throws Exception
	 */
	@Override
	@Transactional(rollbackFor={Exception.class})
	public void updateMbr(MypageVO param) throws Exception {
		
		mypageDAO.updateMbr(param);
	}
	
	/* (non-Javadoc)
	 * @see com.kt.openapi.web.mypage.service.MypageService#chkInsCnt(com.kt.openapi.web.mypage.vo.MypageVO)
	 */
	@Override
	public int chkInsCnt(MypageVO param) throws Exception {
		// TODO Auto-generated method stub
		return mypageDAO.chkInsCnt(param);
	}

	/* (non-Javadoc)
	 * @see com.kt.openapi.web.mypage.service.MypageService#chkDupCnt(com.kt.openapi.web.mypage.vo.MypageVO)
	 */
	@Override
	public int chkDupCnt(MypageVO mypageVo) throws Exception {
		return mypageDAO.chkDupCnt(mypageVo);
	}
}
