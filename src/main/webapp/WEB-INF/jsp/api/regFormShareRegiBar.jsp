
<!--// regFormShareRegiBar.jsp -->
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<%@ page import="com.kt.openapi.web.util.CommonFunc"%>
<%
  //-- [tag:PRJ-20220901]
  boolean bIs2022PrjMode = CommonFunc.isRunmodeTag("2022_prj_mode");
%>
  <div class="left_menu">
    <p>
      <button type="button" title="메뉴열기" class="btn btn_lnm_open act"><span>메뉴열기</span></button>
      <button type="button" title="메뉴닫기" class="btn btn_lnm_close" onclick="toggleLmenu(this, '.api_left');return false;"><span>메뉴닫기</span></button>
    </p>
    <ol class="left_ext_menu_bar disp_none cid_arsenal_hide">
      <li><button type="button" title="API 불러오기" class="btn" onClick="javascript:preOpenPopup(1);"><span>API 불러오기</span></button></li>
      <li><button type="button" title="외부파일 불러오기" class="btn" onClick="javascript:openPopupEtc();"><span>외부파일 불러오기</span></button></li>
      <li><button type="button" title="템플릿 불러오기" class="btn" onClick="javascript:preOpenPopup(2);"><span>템플릿 불러오기</span></button></li>
    </ol>
    <span class="disp_none cid_arsenal_show">
      <span class="disp_none cid_spc_loaded_show">
        <button type="button" title="ARSENAL로 내보내기" class="btn btn_export_arsenal" onclick="fn_export_ARSENAL('${fn:escapeXml(param.apiSpcNo)}');">
          <span>ARSENAL로 내보내기</span>
        </button>
      </span>
    </span>
  </div><!-- .left_menu -->
  <div class="right_menu">
    <p class="err_count" onclick="$('.err_tooltip').show();">
      <span>오류 <em>0</em>건
      </span>
    </p>
    <p>
      <span class="disp_none cid_spc_loaded_show">
<%--
    //-- [tag:20200617]
    //-- [i] private일시 button hidden 
    //-- Public: APIGUB1010, Private: APIGUB1020, Internal: APIGUB1030'
--%>
        <c:if test="${info.apiClass != 'APIGUB1020'}">
        <span class="disp_none cid_arsenal_hidden">
          <button type="button" title="테스트/등록요청" class="btn btn_re-test" onclick="yamlEditorOpen('${ssUserVo.enCmbrId}','${fn:escapeXml(param.apiSpcNo)}', 'sessionkey', {'apiClass':'${info.apiClass}'});">
            <span>테스트/등록요청</span>
          </button>
        </span>
        </c:if>
        
        <span class="disp_none cid_arsenal_show">
          <button type="button" title="SWAGGER 에디터 수정" class="btn btn_swagger_editor" onclick="yamlEditorOpen('${ssUserVo.enCmbrId}','${fn:escapeXml(param.apiSpcNo)}', 'sessionkey', {'apiClass':'${info.apiClass}'});">
            <span>SWAGGER 에디터 수정</span>
          </button>
        </span>
      </span>
<%
  //-- [tag:PRJ-20220901]
  if (bIs2022PrjMode) {
%>
      <button type="button" title="간편등록" class="btn btn_orange dp_none cid_btn_simple_reg"><span>간편등록</span></button>
<%
  }
%>
      <button type="button" title="목록" onclick="location.href='<c:url value="/api/main/mvMainList.do" />'" class="btn btn_list">
        <span>목록</span>
      </button>
    </p>
  </div><!-- .right_menu -->
<!-- regFormShareRegiBar.jsp //-->