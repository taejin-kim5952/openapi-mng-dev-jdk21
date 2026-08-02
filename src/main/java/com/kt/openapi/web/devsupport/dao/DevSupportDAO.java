package com.kt.openapi.web.devsupport.dao;

import com.kt.openapi.web.devsupport.vo.DevSupportFileVo;
import com.kt.openapi.web.devsupport.vo.DevSupportManagerVo;
import com.kt.openapi.web.devsupport.vo.DevSupportSaveVo;
import com.kt.openapi.web.devsupport.vo.DevSupportVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* <pre>
* 1. 패키지명 : com.kt.openapi.web.devsupport.dao
* 2. 타입명   : DevSupportDAO.java
* 3. 작성일   : 2017. 12. 12.
* 4. 작성자   : user
* 5. 설명     : 기술지원 관련 MyBatis Mapper 인터페이스
* </pre>
*/
@Mapper
public interface DevSupportDAO {
    
    /** 기술지원 목록 조회 */
    List<DevSupportVo> selDevSupportList(DevSupportVo dvo) throws Exception;
    
    /** 개발자 포럼 글 등록 */
    int saveForum(DevSupportSaveVo dvo) throws Exception;
    
    /** 기술지원 목록 개수 조회 */
    int selDevSupportListCnt(DevSupportVo param) throws Exception;
    
    /** 기술지원 상세보기 */
    DevSupportVo selDevSupportVoView(DevSupportVo param) throws Exception;

    /** 기술지원 첨부파일 목록 조회 */
    List<DevSupportFileVo> selDevSupportFileList(DevSupportVo param) throws Exception;
    
    /** 기술지원 첨부파일 등록 */
    int devSupportFileReg(DevSupportFileVo fvo) throws Exception;
    
    /** 서비스명 자동 입력을 위한 이전 신청 내역 조회 */
    List<DevSupportVo> selDevSupportMyServiceList(DevSupportVo param) throws Exception;
    
    /** 사업담당자 목록 조회 */
    List<DevSupportManagerVo> selDevSupportManagerList(DevSupportManagerVo param) throws Exception;

    /** 사업담당자 개수 조회 */
    int selDevSupportManagerCnt(DevSupportManagerVo param) throws Exception;
    
    /** 기술지원 수정 */
    int updateDevSupport(DevSupportSaveVo para) throws Exception;
    
    /** 기술지원 첨부파일 삭제 */
    int delDevSupportFile(DevSupportSaveVo vo) throws Exception;

    /** 최신 SDK 조회 */
    List<DevSupportFileVo> selDevSupportSdk() throws Exception;
    
}
