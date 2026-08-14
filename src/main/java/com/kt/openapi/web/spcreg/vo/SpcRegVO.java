package com.kt.openapi.web.spcreg.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * <pre>
 * 1. 패키지명 : com.kt.openapi.web.spcreg.vo
 * 2. 타입명   : SpcRegVO.java
 * 5. 설명     : "SPC 등록" 화면 전용 VO. KOA_TB_API_SPC 1건을 만드는 데 필요한 필드만 담는다.
 *              API(Method+Path, KOA_TB_API_DEF) 등록은 이 화면 범위 밖(quickApiReg가 담당).
 * </pre>
 */
@Getter
@Setter
@ToString
public class SpcRegVO {

    // KOA_TB_API_SPC
    private String apiSpcNo;      // 생성 후 selectKey로 채워짐
    private String sysId;
    private String autId;
    private String apiNm;         // API 그룹 이름
    private String apiDesc;
    private String host;
    private String basPath;
    private String ver;
    private String apiSchema;     // http/https
    private String apiClass;      // APIGUB1000 comn_cd (Public/Private/Internal)
    private String apiVeriBaseurl;
    private String bstgwYn;
    // "YAML 등록"으로 그룹을 만들 때 붙여넣은/불러온 YAML 원문. 파일로 저장하지 않고 DB(YAML_SBST)에만
    // 보관한다(파일 기반 저장은 폐기 - 물리 파일에 의존하는 소비자가 없음을 확인했고, 컨테이너에 그
    // 경로에 대한 볼륨 마운트조차 없어 원래도 재배포 시 소실되는 상태였음). 직접입력으로 그룹을 만든
    // 경우엔 비워둔다(수정 화면에서 다시 덮어쓰지 않음 - updApiSpc는 이 컬럼을 건드리지 않는다).
    private String yamlSbst;

    // 등록자 (세션)
    private String regr;
    private String amdr;
}
