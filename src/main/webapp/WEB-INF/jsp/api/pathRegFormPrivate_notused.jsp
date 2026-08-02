<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<t:layout type="apiInfo">
<!-- ##[!]not used { -->

<script>
  //-- ##[?]not used
  // 타입 선택 (body 일 경우)
  function typeBodyClick_notused(data) {
    if (data.value == "Object") {
      // 기존 object가 아니였을때 div 삭제
      if ($(data).parent().parent().parent().next().length > 0) {
        $(data).parent().parent().parent().next().remove();
      }

      var sectionHtml = '';
      if ($(data).parent().parent().parent().parent().parent().parent().parent().parent().parent().parent().find(".div_draging").length == 0) {
        sectionHtml = '<div class="div_draging ui-sortable">'+
                        '<button type="button" class="btn btn_addParabox" onclick="objectElAdd(this, \'not_used\')" title="속성 추가"><span>속성 추가</span></button>'+
                        '<button type="button" class="btn btn_sml btn_gray" onclick="objectElAdd(this, \'not_used\')" title="속성 추가"><span>속성 추가</span></button>'+
                      '</div>';
        $(data).parent().parent().parent().parent().parent().parent().parent().parent().parent().parent().append(sectionHtml);
      }
    }
    else {
      $(data).parent().parent().parent().parent().parent().parent().parent().parent().parent().parent().find(".div_draging").remove();

      if ($(data).parent().parent().parent().next().length == 0) {
        if (data.value == "Array") {
          var arrayHtml = '';
          arrayHtml = '<tr>'+
                        '<th scope="row">'+
                          '<div class="essential">of</div>'+
                        '</th>'+
                        '<td><div>'+
                          '<select class="w100" onclick="typeBodyClick(this, \'not_used\')" name="type">'+
                            '<option value="">타입을 선택하여 주세요</option>'+
                           <c:forEach var="list" items="${dataTypeList}" varStatus="status">
                            '<option value="${list.cdNm}">${list.cdNm}</option>'+
                           </c:forEach>
                          '</select>'+
                        '</div></td>'+
                      '</tr>';
          $(data).parent().parent().parent().parent().append(arrayHtml);
        }
      }
      else {
        if (data.value != "Array") {
          for (var i = $(data).parent().parent().parent().parent().find("tr").length - 1; i > 0; i--) {
            if (i > $(data).parent().parent().parent().index()) {
              $(data).parent().parent().parent().parent().find("tr").eq(i).remove();
            }
          }
        }
      }
    }
    dragDrop(); // 드롭앤 드롭 실행 매소드를 호출 안해줄 경우 기능 실행이 안됨
  }

  //-- ##[?]not used
  // 요청 파라미터 파라미터 추가 (body 부분)
  function paramBodyAdd_notused(data) {
    requiredNum = requiredNum + 1;
    $("#paramBodyForm").find("input[name='required']").attr("id","required"+requiredNum);
    $("#paramBodyForm").find("input[name='required']").next().attr("for","required"+requiredNum);

    if ($(data).parent().parent().parent().find("section").length == 1) {
      paramBtnHtml =  '<div class="paraDiv_drag">'+
                        '<div class="div_draging">'+
                           $("#paramBodyForm").html() +
                        '</div>'+
                      '</div>';
      $(data).parent().parent().append(paramBtnHtml);
    }
  }
  
  //-- ##[?]not used
  // 요청 파라미터 파라미터 추가 (body 부분)
  function paramReqBodyAddBtn(data) {
    requiredNum = requiredNum + 1;
    $("#paramReqBodyDataTypeForm").find("input[name='required']").attr("id","required"+requiredNum);
    $("#paramReqBodyDataTypeForm").find("input[name='required']").next().attr("for","required"+requiredNum);

    if ($(data).parent().parent().parent().find("section").length == 1) {
      paramBtnHtml =  '<div class="paraDiv_drag">'+
                        '<div class="div_draging">'+
                         $("#paramReqBodyDataTypeForm").html() +
                        '</div>'+
                      '</div>';
      $(data).parent().parent().append(paramBtnHtml);
    }
  }

  //-- ##[?]not used
  // object 일 경우 속성 추가 버튼
  function objectElAdd_notused(data) {
    requiredNum = requiredNum + 1;
    $("#paramBodyForm").find("input[name='required']").attr("id","required"+requiredNum);
    $("#paramBodyForm").find("input[name='required']").next().attr("for","required"+requiredNum);

    $(data).parent().find("button").last().before($("#paramBodyForm").html());
  }

  //-- ##[?]not used
  // dataType Object 시에 div 세팅
  function dataInfoObjectDivSet_notused(data, appendTag) {
    if (appendTag.children(".bodyForm").length == 0) {
      var html = '<div class="div_draging paramBodyDataDiv bodyForm ui-sortable">'+
                   '<button type="button" class="btn btn_addParabox" onclick="objectElAdd(this, \'not_used\')" title="속성 추가"><span>속성 추가</span></button>'+
                   '<button type="button" class="btn btn_sml btn_gray" onclick="objectElAdd(this, \'not_used\')" title="속성 추가"><span>속성 추가</span></button>'+
                 '</div>';
      appendTag.append(html);
    }
    $.each(data, function(index, item) {
      requiredNum = requiredNum + 1;
      $("#paramBodyForm").find("input[name='required']").attr("id","required"+requiredNum);
      $("#paramBodyForm").find("input[name='required']").next().attr("for","required"+requiredNum);
      $("#paramBodyForm").find("section").attr("id", "section_"+requiredNum)
      appendTag.children(".paramBodyDataDiv").find("button").last().before($("#paramBodyForm").html());
      appendTag.children(".paramBodyDataDiv").find("#section_"+requiredNum).children(".inner").find("input[name='name']").val(index)
      appendTag.children(".paramBodyDataDiv").find("#section_"+requiredNum).children(".inner").find("select[name='type']").val(lowString(item.type));
      appendTag.children(".paramBodyDataDiv").find("#section_"+requiredNum).children(".inner").find("input[name='account']").val(item.description);

      if (lowString(item.type) == "Object") {
        dataInfoObjectDivSet(item.properties, appendTag.children(".paramBodyDataDiv").find("#section_"+requiredNum));
      }
      else if (lowString(item.type) == "Array") {
        dataInfoArrayDivSet(item, appendTag.children(".paramBodyDataDiv").find("#section_"+requiredNum), 1);
      }
    });
  }
</script>
<!-- ##[!]not used } -->

<!--##[?]not used -->
<!-- parameter form (body) 시작 -->
<div id="paramBodyForm_notused" style="display:none;">
  <!-- parameter dep 1-1 #paramBodyForm -->
  <section class="cid_template_param_root">
    <div class="inner">
      <p class="handler_bar">handler</p>
      <div class="para_content">
        <div class="pkg_board">
          <section>
            <table class="table-noBrd">
              <caption>table Table</caption>
              <colgroup><col style="width:10%;"><col style="width:40%;"><col style="width:10%;"><col style="width:40%;"></colgroup>
              <tbody>
                <tr>
                  <th scope="row"><div class="essential">이름</div></th>
                  <td><div><input type="text" name="name" title="이름 입력"></div></td>
                  <td colspan="2">
                    <div>
                      <!-- <span class="red_txt">중복된 이름이 있습니다.</span> -->
                      <span class="fr">
                        <span class="cid_opt_required"><a href="javascript:void(0)"><input type="checkbox" id="required" name="required" title="필수"><label for="required"><span></span>필수</label></a></span>
                        <button type="button" title="삭제" class="btn btn_garbage" onclick="paramBodyDel(this)"><span>삭제</span></button>
                      </span>
                    </div>
                  </td>
                </tr>
                <tr>
                  <th scope="row"><div class="essential">타입</div></th>
                  <td>
                    <div>
                      <select class="w100" onchange="typeBodyClick(this, 'not_used')" name="type">
                        <option value="">타입을 선택하여 주세요</option>
                      <c:forEach var="list" items="${dataTypeList}" varStatus="status">
                        <option value="${list.cdNm}">${list.cdNm}</option>
                      </c:forEach>
                      </select>
                    </div>
                  </td>
                  <th scope="row"><div>설명</div></th>
                  <td><div><input type="text" name="account" title="설명 입력"></div></td>
                </tr>
              </tbody>
            </table>
          </section>
        </div><!-- .pkg_board -->
      </div><!-- .para_content -->
    </div><!-- .inner -->
  </section>
  <!-- // parameter dep 1-1 ##paramBodyForm -->
</div><!-- #paramBodyForm -->
<!-- parameter form (body) 끝 -->

<!--##[?]not used -->
<!-- parameter form (body) datatype 적용 안안 form 시작 -->
<div id="paramReqBodyDataTypeForm_notused" style="display:none;">
  <!-- parameter dep 1-1 -->
  <section>
    <div class="inner">
      <p class="handler_bar">handler</p>
      <div class="para_content">
        <div class="pkg_board">
          <section>
            <table class="table-noBrd">
              <caption>table Table</caption>
              <colgroup><col style="width:10%;"><col style="width:40%;"><col style="width:10%;"><col style="width:40%;"></colgroup>
              <tbody>
                <tr>
                  <th scope="row"><div class="essential">이름</div></th>
                  <td><div><input type="text" name="name" title="이름 입력"></div></td>
                  <td colspan="2">
                    <div>
                      <!-- <span class="red_txt">중복된 이름이 있습니다.</span> -->
                      <span class="fr">
                        <span class="cid_opt_required"><a href="javascript:void(0)"><input type="checkbox" id="required" name="required" title="필수"><label for="required"><span></span>필수</label></a></span>
                        <button type="button" title="삭제" class="btn btn_garbage" onclick="paramBodyDel(this)"><span>삭제</span></button>
                      </span>
                    </div>
                  </td>
                </tr>
                <tr>
                  <th scope="row"><div class="essential">타입</div></th>
                  <td>
                    <div>
                      <select class="w100" onchange="typeBodyClick(this, 'not_used')" name="type">
                        <option value="">타입을 선택하여 주세요</option>
                      <c:forEach var="list" items="${dataTypeList}" varStatus="status">
                        <option value="${list.cdNm}">${list.cdNm}</option>
                      </c:forEach>
                      <c:forEach var="list" items="${definitionsList}" varStatus="status">
                        <option value="${list.typeNm}" class="dataType">(data type) ${list.typeNm}</option>
                      </c:forEach>
                      </select>
                    </div>
                  </td>
                  <th scope="row"><div>설명</div></th>
                  <td><div><input type="text" name="account" title="설명 입력"></div></td>
                </tr>
              </tbody>
            </table>
          </section>
        </div><!-- .pkg_board -->
      </div><!-- .para_content -->
    </div><!-- .inner -->
  </section>
  <!-- // parameter dep 1-1 -->
</div><!-- #paramReqBodyDataTypeForm -->
<!-- parameter form (body) 끝 -->

<!-- Layer Pop Start - 얼럿 제목 -->
<div id="popupRegConfirm" class="pop_alert_top" title="API 등록" style="display:none;">
  <div class="popup_content">
    <div class="alert_txt">
      <span id="alertTxt"></span>
    </div><!-- .alert_txt -->
    <div class="lPop_bottom brd_tp">
      <button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm successOpenPopup" id="cBtton" >확인</button>
      <!-- <button type="button" title="취소" class="btn btn_sml3 btn_popup_close" id="cCbtn">취소</button> -->
    </div><!-- .lPop_bottom -->
  </div><!-- .popup_content -->
</div><!-- #popupRegConfirm -->
<script>
  $(document).ready(function() {
    $("#popupRegConfirm .successOpenPopup").click(function(event) {
      $("#popupRegConfirm").dialog("close");
      if (getCookie('apiPopDel') != "Y") {
        $(".pop_testRequest").dialog( "open" ); //-- 테스트/등록요청 안내
      }
    });
  });
</script>

<!-- ##[!]not used } -->


</t:layout>