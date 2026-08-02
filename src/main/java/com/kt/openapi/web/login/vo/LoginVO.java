package com.kt.openapi.web.login.vo;

import java.io.Serial;
import java.util.List;

import com.kt.openapi.web.auth.vo.AuthVO;
import com.kt.openapi.web.cmm.vo.DefaultVo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
public class LoginVO extends DefaultVo {

	@Serial
	private static final long serialVersionUID = 1L;

	private String userId;
	private String userName;
	private String email;
	private String telNo;
	private String phoneNo;
	private String company;
	private String nickname;
	private String ceoNm;
	private String businessRegNo;
	private String homepage;
	private String dept;
	private String allienceType;
	private String chkMe;
	private String delYn;
	private String regDt;
	private String regr;
	private String amdDt;
	private String amdr;
	private String captchaCode;
	private String certifyCode;

	private String pssoPw;

	// 2018-07-25 추가
	private String passInitYn;

	// 중복로그인 여부
	private String dupChkYn;

	// 권한
	private String authId;
	private List<AuthVO> authList;

	// 2018-08-02 추가
	private String ktEmpNum;	// kt 사번
	private String ktEmpDept;	// kt 부서

	// 2018-08-07 추가 :: SHUB 관련
	private int userSeq;
	private String authSeq;
	private String depNm;
	private String companyTel;
	private String companyEmail;

	private boolean personalAgreeYn;
	private String developerId;
	private String crteDate;

	// 2018-08-14 추가 :: KT ADMIN || Console 관리자 권한 있을 경우 true
	private boolean adminAuth;

	// 2018-08-19 기가지니 엑세스 토근 추가
	private String accessToken;
	private String maskUserId;
	private String maskUserName;
	private String maskUserTel;
	private String userTel;
	private String userPost;
	private String userDivCd;
	private String userDivCdNm;
	private String roles;
	private String emailAuthYn;
	private boolean usageAgreeYn;

	// 2018-08-22 제휴법인 정보 추가
	private String category;
	private String title;
	private String sbst;
	private int fileSeq;

	// 2018-08-27 리셀러 추가
	private String position;
	private String sDate;
	private String eDate;
	// 2018-08-28 기가지니 사용 추가
	private int sendSeq;
	private String restMsg;

	// 2018-09-12 기가지니 기가지니 관련 추가
	private String saltData;
	private String FlagEncPW;  //비밀번호 암호화 여부(N: 양방향 암호화 후 전송 , Y : PSSO에 정송 그대로입력)
	private String gipd;
	private String pnpPwYN; // PSSO 초기화 대상자

	private String corpType;

}
