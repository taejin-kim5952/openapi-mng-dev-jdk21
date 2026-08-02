package com.kt.openapi.web.adptran.vo;

import com.kt.openapi.web.cmm.vo.ComBaseVO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

/**
 * Lab TabData 정보를 담는 VO
 * lab_tabdata 테이블 대응
 */
@Getter
@Setter
@ToString(callSuper = true)
public class LabTabDataVO extends ComBaseVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long fdSeq;
    private String fdVarchar;
    private Integer fdInt;
    private String fdDatetime;
}
