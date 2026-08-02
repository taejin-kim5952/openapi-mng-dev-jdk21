package com.kt.openapi.web.qna.vo;

import com.kt.openapi.web.cmm.vo.DefaultVo;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@Getter
@Setter
public class QnAVO extends DefaultVo {

    @Serial
    private static final long serialVersionUID = 1L;

    private String qnaId;
    private String title;
    private String qstn;
    private String ans;
    private String qstnr;
    private String qstnDt;
    private String ansr;
    private String ansDt;
    private String qnaSttusCd;
    private String showYn;
    private String regDt;
    private String regr;
    private String amdDt;
    private String amdr;

    // Result fields
    private int rownum;
    private String amdrNm;
    private String qnaSttusNm;

    // Navigation fields
    private String prevQnaId;
    private String nextQnaId;
    private String prevTitle;
    private String nextTitle;
    // UI fields
    private String regrMasking;
    private String ansrMasking;
}
