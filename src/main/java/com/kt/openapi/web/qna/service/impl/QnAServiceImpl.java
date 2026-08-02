package com.kt.openapi.web.qna.service.impl;

import com.kt.openapi.web.cmm.upload.FileUploadInfo;
import com.kt.openapi.web.cmm.upload.UploadFileUtils;
import com.kt.openapi.web.cmm.upload.WebFileHelper;
import com.kt.openapi.web.cmm.vo.CmnFileVo;
import com.kt.openapi.web.qna.dao.QnADAO;
import com.kt.openapi.web.qna.service.QnAService;
import com.kt.openapi.web.qna.vo.QnAFileVO;
import com.kt.openapi.web.qna.vo.QnASaveVO;
import com.kt.openapi.web.qna.vo.QnASearchVO;
import com.kt.openapi.web.qna.vo.QnAVO;
import com.kt.openapi.web.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

/**
* <pre>
* 1. 패키지명 : com.kt.openapi.web.qna.service.impl
* 2. 타입명 : QnAServiceImpl.java
* 3. 작성일 : 2017. 11. 30. 오후 2:14:29
* 4. 작성자 : user
* 5. 설명 : QNA SERVERIMPL INFO
* </pre>
*/
@Service("qnaService")
public class QnAServiceImpl implements QnAService{

	private static final Logger LOG = LoggerFactory.getLogger(QnAServiceImpl.class);
	
	@Autowired
    private UploadFileUtils uploadFileUtiles;

	@Autowired
	private QnADAO qnaDAO;
	
	/* (non-Javadoc)
	 * @see com.kt.openapi.web.qna.service.QnAService#selQnaList(com.kt.openapi.web.qna.vo.QnASearchVO)
	 */
	@Override
	public List<QnAVO> selQnaList(QnASearchVO param) throws Exception {
		return qnaDAO.selQnAList(param);
	}

	/* (non-Javadoc)
	 * @see com.kt.openapi.web.qna.service.QnAService#selQnaListCnt(com.kt.openapi.web.qna.vo.QnASearchVO)
	 */
	@Override
	public int selQnaListCnt(QnASearchVO param) throws Exception {
		return qnaDAO.selQnaListCnt(param);
	}

	/* (non-Javadoc)
	 * @see com.kt.openapi.web.qna.service.QnAService#selQnaView(com.kt.openapi.web.qna.vo.QnASearchVO)
	 */
	@Override
	public QnAVO selQnaView(QnASearchVO param) throws Exception {
		return qnaDAO.selQnaView(param);
	}

	/* (non-Javadoc)
	 * @see com.kt.openapi.web.qna.service.QnAService#delForumAjax(com.kt.openapi.web.qna.vo.QnASearchVO)
	 */
	@Override
	public int delForumAjax(QnASearchVO param) throws Exception {
		return qnaDAO.delForumAjax(param);
	}
	
	@Override
	public int checkOwnQna(QnASearchVO param) throws Exception {
		return qnaDAO.checkOwnQna(param);
	}

	/* (non-Javadoc)
	 * @see com.kt.openapi.web.qna.service.QnAService#selQnaFileList(com.kt.openapi.web.qna.vo.QnASearchVO)
	 */
	@Override
	public List<QnAFileVO> selQnaFileList(QnASearchVO param) throws Exception {
		return qnaDAO.selQnaFileList(param);
	}

	/* (non-Javadoc)
	 * @see com.kt.openapi.web.qna.service.QnAService#saveQna(com.kt.openapi.web.qna.vo.QnASaveVO)
	 */
	@Transactional(rollbackFor={Exception.class})
	@Override
	public String saveQna(QnASaveVO param , MultipartFile uploadFile) throws Exception {
		qnaDAO.saveQna(param);
		String qnaId = param.getQnaId();
		if( uploadFile != null  && !uploadFile.isEmpty()  ) {
			CmnFileVo fileVo = addQnaFile(uploadFile);
			QnAFileVO fvo = new QnAFileVO();
			fvo.setRegr(param.getRegr());
			fvo.setFilePath(fileVo.getFilePath());
			fvo.setFileSize(fileVo.getFileSize());
			fvo.setFileTypeCd("FILTYP1040"); // 기타
			fvo.setQnaId(qnaId);
			fvo.setOriginFileNm(fileVo.getOrgFileName());
			fvo.setSaveFileNm(fileVo.getSaveFileName());
			
			qnaDAO.qnaFileReg(fvo);
			String fileSeq = fvo.getAtcFileNo();
		}
		return qnaId;
	}

	/* (non-Javadoc)
	 * @see com.kt.openapi.web.qna.service.QnAService#updQna(com.kt.openapi.web.qna.vo.QnASaveVO, org.springframework.web.multipart.MultipartFile)
	 */
	@Override
	@Transactional(rollbackFor={Exception.class})
	public int updQna(QnASaveVO qnASaveVO, MultipartFile uploadFile) throws Exception {
		int cnt = qnaDAO.updQna(qnASaveVO);//qna 글 수정
		if(  !"".equals(  StringUtil.isNullToString( qnASaveVO.getDelAtcFileNo())   ) ) {
			LOG.debug("qnASaveVO.getDelAtcFileNo()   :: {}" , qnASaveVO.getDelAtcFileNo());
			int fcnt = qnaDAO.delQnaFile(qnASaveVO);//기존 첨부파일 삭제
			//신규로 추가한 첨부파일이 존재 할 경우
			if( uploadFile != null && !uploadFile.isEmpty() ) {
				LOG.debug("FILE ADD START##");
				//파일 등록
				CmnFileVo fileVo = addQnaFile(uploadFile);
				QnAFileVO fvo = new QnAFileVO();
				fvo.setRegr(qnASaveVO.getRegr());
				fvo.setFilePath(fileVo.getFilePath());
				fvo.setFileSize(fileVo.getFileSize());
				fvo.setFileTypeCd("FILTYP1040"); // 기타
				fvo.setQnaId(qnASaveVO.getQnaId());
				fvo.setOriginFileNm(fileVo.getOrgFileName());
				fvo.setSaveFileNm(fileVo.getSaveFileName());
				
				qnaDAO.qnaFileReg(fvo);
				String fileSeq = fvo.getAtcFileNo();
			}
		}
		return cnt;
	}
	
	/**
	* <pre>
	* 1. 메소드명 : addQnaFile
	* 2. 작성일 : 2017. 12. 1. 오후 4:07:24
	* 3. 작성자 : user
	* 4. 설명 : 첨부파일 처리 
	* </pre>
	* @param uploadFile
	* @return
	* @throws Exception
	*/
	public  CmnFileVo addQnaFile(MultipartFile uploadFile)  throws Exception {
		File tempFile = WebFileHelper.transferToTempFile(uploadFile);
		FileUploadInfo file = new FileUploadInfo(uploadFile.getOriginalFilename(),tempFile);
		CmnFileVo fileVo = uploadFileUtiles.uploadFileUpload(file);
		return fileVo;
	}

}
