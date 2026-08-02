<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:layout type="error" title="Kt Open API - Error">
<script>
function fnGoMain(){
	location.href= '<c:url value="/main/index.do" />';
}
</script>
	<div id="container">
		<div class="contents pt50">
			<div class="conBox">
				<div id="content">
                    <form>
                        <div class="err_wrap">
                            <div class="err_msg">
                                <p><span>서비스 이용</span>에 <br> 불편을 드려서 죄송합니다.</p>
                                <p>일시적으로 오류가 발생하여 페이지를 표시 할 수 없습니다. <br>빠른 시간 안에 조치하도록 하겠습니다.</p>
                            </div>

                            <div class="btn_set">
                                <button type="button" title="메인으로" class="btn btn_black" onclick="fnGoMain();"> <span>메인으로</span></button>
                            </div>
                        </div>
                    </form>
                </div>
			</div>
		</div>
	</div>
</t:layout>
