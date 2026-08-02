package com.kt.openapi.web.bbs.cmn.vo;

import com.kt.openapi.web.cmm.vo.DefaultVo;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@Getter
@Setter
public class BbsVo extends DefaultVo {

    @Serial
    private static final long serialVersionUID = 1L;

    private String pstingId;
    private String title;
    private String sbst;
    private String pstingNo;
    private String bbsTypeCd;
    private String imptYn;
    private String showYn;
    private String regDt;
    private String regr;
    private String amdDt;
    private String amdr;
    
    // Result fields
    private int rownum;
    private int cnt;       // Attachment count
    private int reqCnt;    // Comment count
    private String retvNum; // View count
    
    // Navigation fields (Prev/Next)
    private String prevPstingId;
    private String nextPstingId;
    private String prevTitle;
    private String nextTitle;
    
    // UI fields
    private String regrMasking;
}
