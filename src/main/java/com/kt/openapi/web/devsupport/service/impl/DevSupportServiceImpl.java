package com.kt.openapi.web.devsupport.service.impl;

import com.kt.openapi.web.cmm.upload.FileUploadInfo;
import com.kt.openapi.web.cmm.upload.UploadFileUtils;
import com.kt.openapi.web.cmm.upload.WebFileHelper;
import com.kt.openapi.web.cmm.vo.CmnFileVo;
import com.kt.openapi.web.devsupport.dao.DevSupportDAO;
import com.kt.openapi.web.devsupport.service.DevSupportService;
import com.kt.openapi.web.devsupport.vo.DevSupportFileVo;
import com.kt.openapi.web.devsupport.vo.DevSupportManagerVo;
import com.kt.openapi.web.devsupport.vo.DevSupportSaveVo;
import com.kt.openapi.web.devsupport.vo.DevSupportVo;
import com.kt.openapi.web.util.CommonFunc;
import com.kt.openapi.web.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service("devSupportService")
public class DevSupportServiceImpl implements DevSupportService {
	
	private static final Logger LOG = LoggerFactory.getLogger(DevSupportServiceImpl.class);
	
	@Autowired
	private DevSupportDAO devSupportDAO;
	
	@Autowired
    private UploadFileUtils uploadFileUtiles;
	
	/* (non-Javadoc)
	 * @see com.kt.openapi.web.bbs.notice.service.BbsNotiService#selNoticeList(com.kt.openapi.web.bbs.notice.vo.BbsSearchVo)
	 */
	@Override
	public List<DevSupportVo> selDevSupportList(DevSupportVo dvo) throws Exception {
		return devSupportDAO.selDevSupportList(dvo);
		
	}
	

	/* (non-Javadoc)
	 * @see com.kt.openapi.web.devSupport.cmn.service.devSupportService#saveForum(com.kt.openapi.web.bbs.cmn.vo.devSupport)
	 */
	@Transactional
	public String saveForum(DevSupportSaveVo param, MultipartFile uploadFile) throws Exception {
		devSupportDAO.saveForum(param);
		String devSupportId = param.getPstingId();
		
		if (uploadFile != null && !uploadFile.isEmpty()) {
            LOG.debug("################ addDevSupportFile call ###################" );
            CmnFileVo fileVo = addDevSupportFile(uploadFile);
            LOG.debug("################ addDevSupportFile call2 ###################" + fileVo);
            DevSupportFileVo fvo = new DevSupportFileVo();
            fvo.setRegr(param.getRegr());
            fvo.setFilePath(fileVo.getFilePath());
            fvo.setFileSize(fileVo.getFileSize());
            fvo.setFileTypeCd("FILTYP1040");
            fvo.setPstingId(devSupportId);
            fvo.setOriginFileNm(fileVo.getOrgFileName());
            fvo.setSaveFileNm(fileVo.getSaveFileName());
            
            devSupportDAO.devSupportFileReg(fvo);
            String fileSeq = fvo.getAtcFileNo();
        }
		
		return devSupportId;
	}
	
	
	/* (non-Javadoc)
	 * @see com.kt.openapi.web.qna.service.QnAService#updQna(com.kt.openapi.web.qna.vo.QnASaveVO, org.springframework.web.multipart.MultipartFile)
	 */
	@Override
	@Transactional
	public int updDevSupport(DevSupportSaveVo vo, MultipartFile uploadFile)  throws Exception {
		
		LOG.debug("################ Start UpSupport #############");
		
		int cnt = devSupportDAO.updateDevSupport(vo);
		LOG.debug("vo.getDelAtcFileNo()   :: {}" , vo.getDelAtcFileNo());
		
		if(  !"".equals(  StringUtil.isNullToString( vo.getDelAtcFileNo())   ) ) {
			LOG.debug("################ UpSupport 파일 삭제 시작 ################");
			int fcnt = devSupportDAO.delDevSupportFile(vo);
			
		}
		
		if( uploadFile != null && !uploadFile.isEmpty() ) {
			LOG.debug("FILE ADD START##");
			LOG.debug("################ addDevSupportFile call ###################" );
			CmnFileVo fileVo = addDevSupportFile(uploadFile);
			LOG.debug("################ addDevSupportFile call2 ###################" + fileVo);
			DevSupportFileVo fvo = new DevSupportFileVo();
			fvo.setRegr(vo.getRegr());
			fvo.setFilePath(fileVo.getFilePath());
			fvo.setFileSize(fileVo.getFileSize());
			fvo.setFileTypeCd("FILTYP1040");
			LOG.debug("################ Delfile bbs id ###################" + vo.getPstingId());
			fvo.setPstingId(vo.getPstingId());
			fvo.setOriginFileNm(fileVo.getOrgFileName());
			fvo.setSaveFileNm(fileVo.getSaveFileName());
			
			devSupportDAO.devSupportFileReg(fvo);
			String fileSeq = fvo.getAtcFileNo();
		}
		return cnt;
	}
	
	/* (non-Javadoc)
	 * @see com.kt.openapi.web.devSupport.cmn.service.devSupportService#selDevSupportListCnt(com.kt.openapi.web.bbs.cmn.vo.devSupport)
	 */
	@Override
	public int selDevSupportListCnt(DevSupportVo param) throws Exception {
		return devSupportDAO.selDevSupportListCnt(param);
		
	}                        


	/* (non-Javadoc)
	 * @see com.kt.openapi.web.bbs.notice.service.DevSupportService#selNoticeView(com.kt.openapi.web.bbs.devSupportVo.vo.DevSupportVo)
	 */
	@Override
	public DevSupportVo selDevSupportView(DevSupportVo param) throws Exception {
		
		DevSupportVo vo = devSupportDAO.selDevSupportVoView(param);
		vo.setBusern(CommonFunc.safeDbDecrypt(vo.getBusern())); //사업담당자 명 복호화
		vo.setDusern(CommonFunc.safeDbDecrypt(vo.getDusern())); //개발담당자 명 복호화
		
		return vo;
	}
	
	/* (non-Javadoc)
	 * @see com.kt.openapi.web.bbs.notice.service.DevSupportService#selDevSupportFileList(com.kt.openapi.web.bbs.devSupportVo.vo.DevSupportVo)
	 */
	@Override
	public List<DevSupportFileVo> selDevSupportFileList(DevSupportVo param) throws Exception {
		return devSupportDAO.selDevSupportFileList(param);
	}
	
	/* (non-Javadoc)
	 * @see com.kt.openapi.web.bbs.notice.service.DevSupportService#addDevSupportFile(com.kt.openapi.web.bbs.devSupportVo.vo.CmnFileVo)
	 */
	public  CmnFileVo addDevSupportFile(MultipartFile uploadFile)  throws Exception {
		File tempFile = WebFileHelper.transferToTempFile(uploadFile);
		FileUploadInfo file = new FileUploadInfo(uploadFile.getOriginalFilename(),tempFile);
		
		LOG.debug("################ uploadFileUpload call #1 ###################"+ file);
		
		CmnFileVo fileVo = uploadFileUtiles.uploadFileUpload(file);
		
		LOG.debug("################ uploadFileUpload call #2 ###################"+ fileVo);
		return fileVo;
	}
	
	/* (non-Javadoc)
	 * @see com.kt.openapi.web.bbs.notice.service.DevSupportService#selDevSupportMyServiceList(com.kt.openapi.web.bbs.devSupportVo.vo.DevSupportVo)
	 */
	public  List<DevSupportVo> selDevSupportMyServiceList(DevSupportVo param)  throws Exception{
		return devSupportDAO.selDevSupportMyServiceList(param);
	}
	
	
	/* (non-Javadoc)
	 * @see com.kt.openapi.web.bbs.notice.service.DevSupportService#selDevSupportMyServiceList(com.kt.openapi.web.bbs.DevSupportManagerVo.vo.DevSupportVo)
	 */
	public List<DevSupportManagerVo> selDevSupportManagerList (DevSupportManagerVo param)  throws Exception{
		
		List<DevSupportManagerVo> list = devSupportDAO.selDevSupportManagerList(param);
		
		List<DevSupportManagerVo> listn = new ArrayList<>();
		
		LOG.debug("################ Manager list Reput #2 ###################"+ list.size());
		
		int size = list.size();
 
		//복호화 후 재 구성
		for(int i=0; i<size; i++){
			
			DevSupportManagerVo vo = new DevSupportManagerVo();
			
			LOG.debug("========================================================================================================================");
			LOG.debug("################ Manager list Reput #3 ###################"+ i + "++++" + (String)list.get(i).toString());
			LOG.debug("################ Manager list userSeq #3 ###################"+ i + "++++" + (Integer)list.get(i).getUserSeq());
			
			vo.setUserIdC(CommonFunc.safeDbDecrypt(list.get(i).getUserIdC()));
			vo.setUserNmC(CommonFunc.safeDbDecrypt(list.get(i).getUserNmC()));
			vo.setDepNm(list.get(i).getDepNm());
			vo.setUserSeq(list.get(i).getUserSeq());

			listn.add(vo);
			
			LOG.debug("################ listn.add(vo); ###################"+ i + "++++" + (String)listn.get(i).toString());
			
			LOG.debug("========================================================================================================================");
		
		}
		
		return listn;
	}
	
	
	/* (non-Javadoc)
	 * @see com.kt.openapi.web.devSupport.cmn.service.devSupportService#selDevSupportListCnt(com.kt.openapi.web.bbs.cmn.vo.devSupport)
	 */
	@Override
	public int selDevSupportManagerCnt(DevSupportManagerVo param) throws Exception {
		return devSupportDAO.selDevSupportManagerCnt(param);
		
	}  
	
	/* (non-Javadoc)
	 * @see com.kt.openapi.web.devsupport#addDevSupportSdk(com.kt.openapi.web.devsupport)
	 */
	public List<DevSupportFileVo>  selDevSupportSdk()  throws Exception{
		return devSupportDAO.selDevSupportSdk();
	}
}