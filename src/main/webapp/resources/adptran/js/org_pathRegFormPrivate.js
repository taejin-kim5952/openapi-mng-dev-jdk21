	//-- pathRegFormPrivate.jsp의 원본 함수

  // path 저장
  function pathSave_org(){
    if(sttusCd == "APIREV1020" || sttusCd == "APIREG1020" || sttusCd == "APIREG1030"){ // 작성중, 검토요청, 등록 검토일 경우에만 저장
      $("#popupConfirm").parent().find("div").eq(0).children("span").text("API");
      $("#popupConfirm").find('#alertTxt').text(sttusCdNm + '에서는 수정하실 수 없습니다.');
      $("#popupConfirm").parent().find("div").eq(1).find("button").parent().html('<button type="button" title="확인" class="btn btn_sml3 btn_black btn_confirm" id="cBtton" onclick="window.location.reload()">확인</button>');
      $("#popupConfirm").dialog( "open" );

      return false;
    }

    // body데이터 형식 체크박스 가 체크되어 있을 경우 파라미터 존재유무 체크
    if($("input[name='reqContentType']").is(":checked") == true){
      if($(".reqBody").find(".inner").length == 0){
        $("#popupConfirm").parent().find("div").eq(0).children("span").text("API");
        $("#popupConfirm").find('#alertTxt').html('요청 body파라미터를 추가하세요.');
        $("#popupConfirm").dialog( "open" );
        return false;
      }
    }

    /**** 데이터 검사 시작******///
    dataValidation();

    // 에러 건수가 1개라도 있으면 저장 되지 않음
    if(errorNum > 0){
      err_on();
      var offset = $("#container").offset();
          $('html, body').animate({scrollTop : offset.top}, 500);
      return false;
    } else {
      $('.err_tooltip').css("display", "none");
      $('.err_count').css("display", "none");
    }
    /**** 데이터 검사 끝 ******///

    //yaml 값 초기화 후 저장 시작
    yamlOb = YAML.parse($("#yamlSbst").val());

    /** PATH URI 저장 시작   ==========>   ***/
    // pathParam이 있을경우에는 path 뒤에 pathParam 붙여서 저장
    var yamlObPaths = "";
    var dataOb = new Object();
    if(yamlOb['paths'] == undefined){
      yamlOb['paths'] = new Object();
    }
    if($("#pApiPath").val() != "" && $("#pApiMethod").val() != "" && copyYn != "Y" && copyYn != "A"){

      delete yamlOb['paths'][$("#pApiPath").val()][$("#pApiMethod").val().toLowerCase()]; // 기존 경로의 매소드 삭제
    }

    if(0 < $(".reqPath").find(".inner").length){
      // path param이 있을 경우에는 yaml path에 변수로 추가 해주어야 한다
      var pathAdd = "";
      for(var i=0;i < $(".reqPath").find(".inner").length;i++){
        var pathParamStr = "/{"+$($(".reqPath").find(".inner")[i]).find("input[name='name']").val()+"}";
        if($("input[name='path']").val().indexOf(pathParamStr) == -1){
          pathAdd = pathAdd + pathParamStr;
        }
      }
      $("input[name='path']").val($("input[name='path']").val() + pathAdd);

      pathAdd =  $("input[name='path']").val();

      if(jQuery.isEmptyObject(yamlOb.paths[pathAdd])){
        yamlOb.paths[pathAdd] = {};
      }

      yamlObPaths = yamlOb.paths[pathAdd];
    } else {
      if(jQuery.isEmptyObject(yamlOb.paths[$("input[name='path']").val()])){
        yamlOb.paths[$("input[name='path']").val()] = {};
      }

      yamlObPaths = yamlOb.paths[$("input[name='path']").val()];
    }
    //메소드 변수명으로 담아준다. (대문자로 들어온 값 소문자로 변환)
    var methodVar = $("select[name='method'] option:checked").text().toLowerCase();
    // 패스 초기화
    yamlObPaths[methodVar] = {}
    // 패스에 대한 이름
    yamlObPaths[methodVar].summary     = $("input[name='summary']").val();
    // 패스에 대한API ID
    yamlObPaths[methodVar].operationId = $("input[name='apiId']").val();
    // 패스에 대한 설명
    yamlObPaths[methodVar].description = $("textarea[name='account']").val();

    // pApiNo 저장
    // 신규 api 번호 조회
    if($("#pApiNo").val() == ""){
      $.ajax({
        url    : '<c:url value="/api/reg/selApiPathApiNoAjax.do"/>',
        type   : 'POST',
        cache  : false,
        async  : false,
        success: function(data){
          $("#pApiNo").val(data.apiNo);
          yamlObPaths[methodVar]['x-apiNo'] =  {};
          yamlObPaths[methodVar]['x-apiNo'] =  data.apiNo;
          },
        error:function(request,status,error){
          err_message(status, error);
          }
      });
    } else {
      yamlObPaths[methodVar]['x-apiNo'] =  {};
      yamlObPaths[methodVar]['x-apiNo'] =  $("#pApiNo").val();
    }


    // 카테고리 명 저장
    yamlObPaths[methodVar]['x-category'] =  {};
    yamlObPaths[methodVar]['x-category'] =  $("#pApiCtgryNm").val();

    var XCateOb = new Object;
    XCateOb['apiNm'] = $("input[name='summary']").val();
    XCateOb['apiNo'] = yamlObPaths[methodVar]['x-apiNo'];

    if($("#pApiPath").val() != "" && $("#pApiMethod").val() != "" && copyYn != "Y" && copyYn != "A"){
      if(Object.keys(yamlOb['x-category'][$("#pApiCtgryNm").val()][$("#pApiPath").val()]).length == 1){
        delete yamlOb['x-category'][$("#pApiCtgryNm").val()][$("#pApiPath").val()]; // 기존 경로의 매소드 삭제
      } else {
        delete yamlOb['x-category'][$("#pApiCtgryNm").val()][$("#pApiPath").val()][($("#pApiMethod").val().toLowerCase())]; // 기존 경로의 매소드 삭제
      }


      if(yamlOb['x-category'][$("#pApiCtgryNm").val()][$("input[name='path']").val()] == undefined){
        yamlOb['x-category'][$("#pApiCtgryNm").val()][$("input[name='path']").val()] = new Object();
      }
      yamlOb['x-category'][$("#pApiCtgryNm").val()][$("input[name='path']").val()][methodVar] = XCateOb;

    } else {
      if(yamlOb['x-category'][$("#pApiCtgryNm").val()][$("input[name='path']").val()] == undefined){
        yamlOb['x-category'][$("#pApiCtgryNm").val()][$("input[name='path']").val()] = new Object();
      }
      yamlOb['x-category'][$("#pApiCtgryNm").val()][$("input[name='path']").val()][methodVar] = XCateOb;
    }
    /*20190308 apiGubun(visiblity),use_yn 추가
    */
    // apiGubun 저장
      yamlObPaths[methodVar]['x-visiblity'] =  {};
      yamlObPaths[methodVar]['x-visiblity'] =  $("select[name='apiGubun']").val();

    // display 저장
      yamlObPaths[methodVar]['x-display'] =  {};
      yamlObPaths[methodVar]['x-display'] =  $("#apiUseYn").val();
    /** PATH URI 저장 끝   ==========>   ***/

    /** 보안 스키마 시작   ==========>   ***/
    if($("#securityType input[type='checkbox']:checked").length > 0 && $("input[name='setyrityType']:checked").val() == "custom"){
      var securityArray = new Array();
      var security = new Object();
      var arryList = {};
      // 보안 No authentication 선택시에 저장 안함
      if($($("#securityType input[type='checkbox']:checked")[0]).val() != "no"){
        for(var i=0;i < $("#securityType input[type='checkbox']:checked").length;i++){
          var array = new Array(); //초기화
          if($($("#securityType input[type='checkbox']:checked")[i]).parent().parent().find("li").length > 0){
            for(var k=0;k < $($("#securityType input[type='checkbox']:checked")[i]).parent().parent().find("li").length; k++){
              array.push($($($("#securityType input[type='checkbox']:checked")[i]).parent().parent().find("li")[k]).find("span")[0].innerText);
            }
          }
          arryList = {};
          arryList[$($("#securityType input[type='checkbox']:checked")[i]).val()] = array;
          securityArray.push(arryList);
        }
        yamlObPaths[methodVar].security = securityArray;
      }
    }
    /** 보안 스키마 끝   ==========>   ***/

    /** 요청 파라미터 세팅 시작   ==========>   ***/

    var paramArray = new Array();
    var paramOb = {};
    /*********************** Query 파라미터 세팅 시작   ==========>   ***/
    // 각각의 파라미터 하단에 inner 밑에 기본정보 폼이 존재 하므로 inner기준으로 값 세팅
    if(0 < $(".reqQuery").find(".inner").length){
      for(var i=0;i < $(".reqQuery").find(".inner").length;i++){
        paramOb = {};
        paramOb['in']        = 'query';
        paramOb['name']      = $($(".reqQuery").find(".inner")[i]).find("input[name='name']").val();
        paramOb['description'] = $($(".reqQuery").find(".inner")[i]).find("input[name='account']").val();
        paramOb['required']    = $($(".reqQuery").find(".inner")[i]).find("input[name='required']").is(":checked");
        paramOb['x-example']   = $($(".reqQuery").find(".inner")[i]).find("input[name='example']").val();
        paramOb['x-dataTypeCd']    = "PRMTYP1010"; // PRMTYP1010(요청 파라미터), PRMTYP1020(응답 파라미터)


        // 타입이 배열일때
        if($($(".reqQuery").find(".inner")[i]).find("select").val() == 'Array'){

          var emptyOb = new Object();

          dataOb[0] = $(".reqQuery").find(".inner")[i];

          typeArrayFn($(dataOb[0]), emptyOb);

          paramOb['type']  = emptyOb[$($(".reqQuery").find(".inner")[i]).find("input[name='name']").val()]['type'];
          paramOb['items'] = emptyOb[$($(".reqQuery").find(".inner")[i]).find("input[name='name']").val()]['items'];
        } else {
          paramOb['type'] = $($(".reqQuery").find(".inner")[i]).find("select").val().toLowerCase();
        }
        // Query 파라미터 배열로 저장
        paramArray.push(paramOb);
      }
    }
    /*********************** Query 파라미터 세팅 끝   ==========>   ***/
    /*********************** Header 파라미터 세팅 시작   ==========>   ***/
    if(0 < $(".reqHeaders").find(".inner").length){
      for(var i=0;i < $(".reqHeaders").find(".inner").length;i++){
        paramOb = {};
        paramOb['in']        = 'header';
        paramOb['name']      = $($(".reqHeaders").find(".inner")[i]).find("input[name='name']").val();
        paramOb['description'] = $($(".reqHeaders").find(".inner")[i]).find("input[name='account']").val();
        paramOb['required']    = $($(".reqHeaders").find(".inner")[i]).find("input[name='required']").is(":checked");
        paramOb['x-example']   = $($(".reqHeaders").find(".inner")[i]).find("input[name='example']").val();
        paramOb['x-dataTypeCd']    = "PRMTYP1010"; // PRMTYP1010(요청 파라미터), PRMTYP1020(응답 파라미터)

        // 타입이 배열일때
        if($($(".reqHeaders").find(".inner")[i]).find("select").val() == 'Array'){

          var emptyOb = new Object();

          dataOb[0] = $(".reqHeaders").find(".inner")[i];

          typeArrayFn($(dataOb[0]), emptyOb);

          paramOb['type']  = emptyOb[$($(".reqHeaders").find(".inner")[i]).find("input[name='name']").val()]['type'];
          paramOb['items'] = emptyOb[$($(".reqHeaders").find(".inner")[i]).find("input[name='name']").val()]['items'];
        } else {
          paramOb['type'] = $($(".reqHeaders").find(".inner")[i]).find("select").val().toLowerCase();
        }
        // Header 파라미터 배열로 저장
        paramArray.push(paramOb);
      }
    }
    /*********************** Header 파라미터 세팅 끝   ==========>   ***/
    /*********************** Path 파라미터 세팅 시작   ==========>   ***/
    if(0 < $(".reqPath").find(".inner").length){
      for(var i=0;i < $(".reqPath").find(".inner").length;i++){
        paramOb = {};
        paramOb['in']        = 'path';
        paramOb['name']      = $($(".reqPath").find(".inner")[i]).find("input[name='name']").val();
        paramOb['description'] = $($(".reqPath").find(".inner")[i]).find("input[name='account']").val();
        paramOb['x-example']   = $($(".reqPath").find(".inner")[i]).find("input[name='example']").val();
        paramOb['required']    = true // 패스 일 경우 required는 항상 트루 이다 아닐시에 swagger 에러
        paramOb['x-dataTypeCd']    = "PRMTYP1010"; // PRMTYP1010(요청 파라미터), PRMTYP1020(응답 파라미터)

        // 타입이 배열일때
        if($($(".reqPath").find(".inner")[i]).find("select").val() == 'Array'){

          var emptyOb = new Object();

          dataOb[0] = $(".reqPath").find(".inner")[i];

          typeArrayFn($(dataOb[0]), emptyOb);

          paramOb['type']  = emptyOb[$($(".reqPath").find(".inner")[i]).find("input[name='name']").val()]['type'];
          paramOb['items'] = emptyOb[$($(".reqPath").find(".inner")[i]).find("input[name='name']").val()]['items'];
        } else {
          paramOb['type'] = $($(".reqPath").find(".inner")[i]).find("select").val().toLowerCase();
        }
        // Path 파라미터 배열로 저장
        paramArray.push(paramOb);
      }
    }
    /*********************** Path 파라미터 세팅 끝   ==========>   ***/
    /*********************** formData 파라미터 세팅 시작   ==========>   ***/
    if(0 < $(".reqFormData").find(".inner").length){
      for(var i=0;i < $(".reqFormData").find(".inner").length;i++){
        paramOb = {};
        paramOb['in']        = 'formData';
        paramOb['name']      = $($(".reqFormData").find(".inner")[i]).find("input[name='name']").val();
        paramOb['description'] = $($(".reqFormData").find(".inner")[i]).find("input[name='account']").val();
        paramOb['required']    = $($(".reqFormData").find(".inner")[i]).find("input[name='required']").is(":checked");
        paramOb['x-example']   = $($(".reqFormData").find(".inner")[i]).find("input[name='example']").val();
        paramOb['x-dataTypeCd']    = "PRMTYP1010"; // PRMTYP1010(요청 파라미터), PRMTYP1020(응답 파라미터)

        // 타입이 배열일때
        if($($(".reqFormData").find(".inner")[i]).find("select").val() == 'Array'){

          var emptyOb = new Object();

          dataOb[0] = $(".reqFormData").find(".inner")[i];

          typeArrayFn($(dataOb[0]), emptyOb);

          paramOb['type']  = emptyOb[$($(".reqFormData").find(".inner")[i]).find("input[name='name']").val()]['type'];
          paramOb['items'] = emptyOb[$($(".reqFormData").find(".inner")[i]).find("input[name='name']").val()]['items'];
        } else {
          paramOb['type'] = $($(".reqFormData").find(".inner")[i]).find("select").val().toLowerCase();
        }
        // formData 파라미터 배열로 저장
        paramArray.push(paramOb);
      }
    }
    /*********************** formData 파라미터 세팅 끝   ==========>   ***/

    /*********************** body 파라미터 세팅 시작   ==========>     ***/
    if(0 < $("input[name='reqContentType']:checked").length){ //--@[Content-Type선택이 있으면]
      var consumesArray = new Array();
      exampleOb = new Object();
      //consumes 저장
      for(var i=0;i < $("input[name='reqContentType']:checked").length;i++){
        consumesArray.push($($("input[name='reqContentType']:checked")[i]).val());
      }
      yamlObPaths[methodVar].consumes = consumesArray;  //--@[Content-Type저장]

      paramOb = {};
      paramOb['in']        = 'body';
      paramOb['name']        = 'body';  //--@[초기값설정][overwrite됨?]
      paramOb['description'] = $(".reqBody").find("textarea[name='reqBodyAccount']").val();
      paramOb['schema']      = {};
      paramOb['x-dataTypeCd']    = "PRMTYP1010"; // PRMTYP1010(요청 파라미터), PRMTYP1020(응답 파라미터)

      if($($($(".reqBody").children().children(".paraDiv_drag").children(".div_draging").find("select")[0])).val() == 'Array'){
        paramOb['name']           = $($(".reqBody").children().children(".paraDiv_drag").children(".div_draging").find("input[name='name']")[0]).val();
        paramOb['required']         = $($(".reqBody").children().children(".paraDiv_drag").children(".div_draging").find("input[name='required']")[0]).is(":checked");
        paramOb['schema']['type']       = $($(".reqBody").children().children(".paraDiv_drag").children(".div_draging").find("select")[0]).val().toLowerCase();
        paramOb['x-example']          = $($(".reqBody").children().children(".paraDiv_drag").children(".div_draging").find("input[name='example']")[0]).val();

        var emptyOb = new Object();
        dataOb[0] = $(".reqBody").children().children(".paraDiv_drag").children(".div_draging").find(".inner")[0];
        typeArrayFn($(dataOb[0]), emptyOb);

        paramOb['schema']['items']  = emptyOb[$($(".reqBody").children().children(".paraDiv_drag").children(".div_draging").find("input[name='name']")[0]).val()]['items'];
        paramOb['schema']['description']  = $($(".reqBody").children().children(".paraDiv_drag").children(".div_draging").find("input[name='account']")[0]).val();


      } else if($($($(".reqBody").children().children(".paraDiv_drag").children(".div_draging").find("select")[0])).val() == 'Object'){
        dataOb[0] = $($(".reqBody").children().children(".paraDiv_drag").children(".div_draging"));
        //--[drm][cmt][??][baybe dataOb[0]]
        if(!jQuery.isEmptyObject(dataOb)){
          var emptyOb = new Object();
          typeObject(dataOb[0], emptyOb);
          paramOb['schema']['properties'] = emptyOb['properties'][$($(".reqBody").children().children(".paraDiv_drag").children(".div_draging").find("input[name='name']")[0]).val()]['properties'];
          paramOb['schema']['required']   = emptyOb['properties'][$($(".reqBody").children().children(".paraDiv_drag").children(".div_draging").find("input[name='name']")[0]).val()]['required'];
        }

        paramOb['name']           = $($(".reqBody").children().children(".paraDiv_drag").children(".div_draging").find("input[name='name']")[0]).val();
        paramOb['required']         = $($(".reqBody").children().children(".paraDiv_drag").children(".div_draging").find("input[name='required']")[0]).is(":checked");
        paramOb['schema']['type']       = $($(".reqBody").children().children(".paraDiv_drag").children(".div_draging").find("select")[0]).val().toLowerCase();
        paramOb['schema']['description']  = $($(".reqBody").children().children(".paraDiv_drag").children(".div_draging").find("input[name='account']")[0]).val();
        paramOb['x-example']        = "Ob_Small_Com_Del"+JSON.stringify(exampleOb)+"Ob_Small_Com_Del";
      } else {
        paramOb['name']           = $($(".reqBody").children().children(".paraDiv_drag").children(".div_draging").find("input[name='name']")[0]).val();
        paramOb['required']         = $($(".reqBody").children().children(".paraDiv_drag").children(".div_draging").find("input[name='required']")[0]).is(":checked");
        paramOb['x-example']          = $($(".reqBody").children().children(".paraDiv_drag").children(".div_draging").find("input[name='example']")[0]).val();

        if($($(".reqBody").children().children(".paraDiv_drag").children(".div_draging").find("select option:selected")).text().indexOf("(data type)") > -1 ){
          paramOb['schema']['$ref']       = "#/definitions/"+  $($(".reqBody").children().children(".paraDiv_drag").children(".div_draging").find("select")[0]).val();
          paramOb['x-dataTypeCd']    = "PRMTYP1040";
        } else {
          paramOb['schema']['type']       = $($(".reqBody").children().children(".paraDiv_drag").children(".div_draging").find("select")[0]).val().toLowerCase();
          paramOb['schema']['description']  = $($(".reqBody").children().children(".paraDiv_drag").children(".div_draging").find("input[name='account']")[0]).val();
        }
      }
      // body 파라미터 배열로 저장
      paramArray.push(paramOb);
    }
    /*********************** body 파라미터 세팅 끝   ==========>     ***/
    yamlObPaths[methodVar].parameters = paramArray;
    /** 요청 파라미터 세팅 끝     ==========>   ***/
    /** 응답 파라미터 세팅 시작   ==========>   ***/

    if(0 < tabNum){
      yamlObPaths[methodVar].responses = {};
      for(var i=0;i < $("#responseTab").children("div").length; i++){
        var resTabId = "#tabForm"+ (i+1);
        var statusCdVar = $(resTabId).find("select[name='resStatus']").val(); // 상태 코드

        yamlObPaths[methodVar].responses[statusCdVar] = {};
        yamlObPaths[methodVar].responses[statusCdVar].description = $(resTabId).find("input[name='resAccont']").val();

        /*********************** response header 파라미터 세팅 시작   ==========>     ***/
        var resHeaderId = "#headerForm"+ (i+1);

        if(0 < $(resHeaderId).find(".inner").length){
          yamlObPaths[methodVar].responses[statusCdVar].headers = {};

          for(var k=0;k < $(resHeaderId).find(".inner").length;k++){
            var resStatusHeaderVar = $($(resHeaderId).find(".inner")[k]).find("input[name='name']").val();

            yamlObPaths[methodVar].responses[statusCdVar].headers[resStatusHeaderVar] = {};

            paramOb = {};
            paramOb['description']    = $($(resHeaderId).find(".inner")[k]).find("input[name='account']").val();
            paramOb['x-example']    = $($(resHeaderId).find(".inner")[k]).find("input[name='example']").val();
            paramOb['x-dataTypeCd']     = "PRMTYP1020"; // PRMTYP1010(요청 파라미터), PRMTYP1020(응답 파라미터)
            /* paramOb['required']        = $($(resHeaderId).find(".inner")[i]).find("input[name='required']").is(":checked");  // 응답 파라미터는 헤더 값이 없음*/

            // 타입이 배열일때
            if($($(resHeaderId).find(".inner")[k]).find("select").val() == 'Array'){

              var emptyOb = new Object();

              dataOb[0] = $(resHeaderId).find(".inner")[k];

              typeArrayFn($(dataOb[0]), emptyOb);

              paramOb['type']  = emptyOb[resStatusHeaderVar]['type'];
              paramOb['items'] = emptyOb[resStatusHeaderVar]['items'];
            } else {
              paramOb['type'] = $($(resHeaderId).find(".inner")[k]).find("select").val().toLowerCase();
            }
            // response headers 저장
            yamlObPaths[methodVar].responses[statusCdVar].headers[resStatusHeaderVar] = paramOb;
          }

        }
        /*********************** response header 파라미터 세팅 끝     ==========>     ***/

        /*********************** response Body 파라미터 세팅 시작   ==========>         ***/
        var resBodyId = "#bodyForm"+ (i+1);
        var resProducesArray = new Array();
        yamlObPaths[methodVar].responses[statusCdVar].schema = {};
        // Produces 저장 시작
        for(var k=0;k < $("input[name='resContentType']:checked").length;k++){
          var producesCk = resProducesArray.contains($($("input[name='resContentType']:checked")[k]).val());
          if(producesCk == false){
            resProducesArray.push($($("input[name='resContentType']:checked")[k]).val());
          }

        }
        yamlObPaths[methodVar].produces = resProducesArray;
        // Produces 저장 끝
        if(0 < $(resBodyId).find(".inner").length){
          for(var k=0;k < $(resBodyId).find(".inner").length;k++){

            paramOb = {};
            paramOb['x-description']    = $(resBodyId).find("textarea").val();
            paramOb['x-dataTypeCd']     = "PRMTYP1020"; // PRMTYP1010(요청 파라미터), PRMTYP1020(응답 파라미터)
            // 예제 배열 초기화
            exampleOb = new Object();
            // 타입이 배열일때
            if($($($(resBodyId).children().children(".paraDiv_drag").children(".div_draging").find("select")[0])).val() == 'Array'){
              paramOb['type']       = $($(resBodyId).children().children(".paraDiv_drag").children(".div_draging").find("select")[0]).val().toLowerCase();

              var emptyOb = new Object();
              dataOb[0] = $(resBodyId).children().children(".paraDiv_drag").children(".div_draging").find(".inner")[0];
              typeArrayFn($(dataOb[0]), emptyOb);

              paramOb['items']        = emptyOb[$($(resBodyId).children().children(".paraDiv_drag").children(".div_draging").find("input[name='name']")[0]).val()]['items'];
              paramOb['description']    = $($(resBodyId).children().children(".paraDiv_drag").children(".div_draging").find("input[name='account']")[0]).val();
              paramOb['x-name']       = $($(resBodyId).children().children(".paraDiv_drag").children(".div_draging").find("input[name='name']")[0]).val();
              paramOb['example']      = "Ob_Small_Com_Del"+$($(resBodyId).children().children(".paraDiv_drag").children(".div_draging").find("input[name='example']")[0]).val()+"Ob_Small_Com_Del";


            } else if($($($(resBodyId).children().children(".paraDiv_drag").children(".div_draging").find("select")[0])).val() == 'Object'){
              dataOb[0] = $($(resBodyId).children().children(".paraDiv_drag").children(".div_draging"));
              //--[drm][cmt][??][baybe dataOb[0]]
              if(!jQuery.isEmptyObject(dataOb)){
                var emptyOb = new Object();
                typeObject(dataOb[0], emptyOb);
                paramOb = emptyOb;
              }

              paramOb['type']       = $($(resBodyId).children().children(".paraDiv_drag").children(".div_draging").find("select")[0]).val().toLowerCase();
              paramOb['description']    = $($(resBodyId).children().children(".paraDiv_drag").children(".div_draging").find("input[name='account']")[0]).val();
              paramOb['x-name']       = $($(resBodyId).children().children(".paraDiv_drag").children(".div_draging").find("input[name='name']")[0]).val();
              paramOb['example']      = "Ob_Small_Com_Del"+JSON.stringify(exampleOb)+"Ob_Small_Com_Del"; // example 를 넣을때 yaml에 문자열변환하는 순간 '를 추가 하여서 문자열로 변환 후 '를 삭제하기 위해 임의의 문자 추가
            } else {
              paramOb['example']      = $($(resBodyId).children().children(".paraDiv_drag").children(".div_draging").find("input[name='example']")[0]).val();
              paramOb['description']    = $($(resBodyId).children().children(".paraDiv_drag").children(".div_draging").find("input[name='account']")[0]).val();
              paramOb['x-name']       = $($(resBodyId).children().children(".paraDiv_drag").children(".div_draging").find("input[name='name']")[0]).val();
              /* paramOb['required']    = $($(resBodyId).children().children(".paraDiv_drag").children(".div_draging").find("input[name='required']")[0]).is(":checked"); */

              if($($(resBodyId).children().children(".paraDiv_drag").children(".div_draging").find("select")[0]).children("option:selected").text().indexOf("(data type)") > -1 ){
                paramOb['$ref']       = "#/definitions/"+   $($(resBodyId).children().children(".paraDiv_drag").children(".div_draging").find("select")[0]).val();
                paramOb['x-dataTypeCd']    = "PRMTYP1040";
              } else {
                paramOb['type']       = $($(resBodyId).children().children(".paraDiv_drag").children(".div_draging").find("select")[0]).val().toLowerCase();
              }
            }
          }
          yamlObPaths[methodVar].responses[statusCdVar].schema = paramOb;
        }
        /*********************** response Body 파라미터 세팅 끝   ==========>       ***/
      }
    }
    /** 응답 파라미터 세팅 끝     ==========>   ***/

    // 패스가 2개 이상일 경우에 처음 등록한 / 경로 삭제
    if(Object.keys(yamlOb['paths']).length > 1){
      delete yamlOb['paths']['/']; // json / 경로 삭제
    }

    var yamlStr = YAML.stringify(yamlOb);
    // 임의 문자열 및 ' 삭제
    yamlStr = yamlStr.replace(/\'Ob_Small_Com_Del/gi,"");
    yamlStr = yamlStr.replace(/example: >-/g,"example:");
    yamlStr = yamlStr.replace(/Ob_Small_Com_Del\'/gi,"");
    yamlStr = yamlStr.replace(/Ob_Small_Com_Del/gi,"");
    yamlStr = yamlStr.replace(/required: \[\]/gi,"");
    // console.log(yamlStr);
    // 필수값이 없는것들 삭제


    var param = {
        apiSpcNo:   $("#pApiSpcNo").val(), // 무조건 존재
        apiNo:    $("#pApiNo").val(),     // 존재(수정) , 부재(등록)
          yamlStr:  yamlStr,          // yaml 데이터 : 필수,
          apiNm:    $("#apiNm").val(),
          apiDesc:  $("#apiDesc").val(),
          apiId:    $("#apiId").val(),
          apiPath:  $("#apiPath").val(),
          apiCtgryNo: $("#pApiCtgryNo").val(),
          apiCtgryNm: $("#pApiCtgryNm").val(),
          methodCd:   $("select[name='method']").val(),
          methodCdNm: $("#methodBox option:checked").text(),
          insertYn:   $("#insertYn").val(),
          apiGubun:   $("select[name='apiGubun']").val(),
          useYn:   $("#apiUseYn").val()
      };

    $.ajax({
      url    : '<c:url value="/api/reg/savApiRegPathAjax.do"/>',
      type   : 'POST',
      data   : param,
      cache  : false,
      async  : false,
      success: function(data){
        // console.log("성공 data", data);
          //레이어 메세지 적용
        $("#popupRegConfirm").parent().find("div").eq(0).children("span").text("API");
        $("#popupRegConfirm").find('#alertTxt').text('<spring:message code="api.req.save.msg" />');
        $("#popupRegConfirm").dialog( "open" );
        // 값 세팅
        $("#pApiNo").val(data.apiRegVO.apiNo);
        $("#pApiSpcNo").val(data.apiRegVO.apiSpcNo);
        $("#pApiPath").val(data.apiRegVO.apiPath);
        $("#pApiMethod").val(data.apiRegVO.methodCdNm.toLowerCase());
        $("#apiPath").val(data.apiRegVO.apiPath);
        $("#pApiCtgryNo").val(data.apiRegVO.apiCtgryNo);
        $("#insertYn").val("N");
        // yaml값 셋팅
        $("#yamlSbst").val(yamlStr);

        //LEFT 메뉴 다시 셋팅
        XLeftMenuSet(yamlOb['x-category']);
        isChange = false; // 페이지 이동 체크 여부
        // DATY TYPE 다시 세팅
        // dataInfoOb = JSON.stringify(data.dataTypeInfo);
        },
      error:function(request,status,error){
        err_message(status, error);
        }
    });
  }

  function paramAdd_org(data) {
    var paramBtnHtml = '';
    requiredNum = requiredNum + 1;
    if ($(data).parent().parent().parent().find("span")[0].innerText == "path") {
      $("#paramForm").find("input[name='required']").prop("checked", true);
      $("#paramForm").find("input[name='required']").prop("disabled", true);
    }
    else {
      $("#paramForm").find("input[name='required']").prop("checked", false);
      $("#paramForm").find("input[name='required']").prop("disabled", false);
    }
    // body파라미터와 formdata파라미터가 같이 등록될 경우 swagger 구문 에러가 발생
    if ($(data).parent().parent().parent().find("span")[0].innerText == "formData") {
      if ($("input[name='reqContentType']").is(":checked") == true) {
        $("#popupConfirm").parent().find("div").eq(0).children("span").text("API");
        $("#popupConfirm").find('#alertTxt').html('body파라미터와 formData파라미터는<br /> 같이 등록할 수 없습니다.');
        $("#popupConfirm").dialog( "open" );
        return false;
      }
    }
    // 응답 파라미터의 헤더는 필수 값이 없다.
    if ($(data).parent().parent().parent().parent().find(".responseForm").length > 0) {
      $("#paramForm").find("a").css("display", "none");
    }
    else {
      $("#paramForm").find("a").css("display", "");
    }

    $("#paramForm").find("input[name='required']").attr("id", "required" + requiredNum);
    $("#paramForm").find("input[name='required']").next().attr("for", "required" + requiredNum);

    if ($(data).parent().parent().parent().find(".div_draging").length > 0) {
      $(data).parent().parent().find(".div_draging").find("button").last().before($("#paramForm").html());
    }
    else {
      var paramBtnHtml = '<div class="paraDiv_drag">'+
                           '<div class="div_draging">'+
                             '<button type="button" class="btn btn_addParabox"   onclick="paramAdd(this)" title="파라미터 추가"><span>파라미터 추가</span></button>'+
                              $("#paramForm").html() +
                             '<button type="button" class="btn btn_sml btn_gray" onclick="paramAdd(this)" title="파라미터 추가"><span>파라미터 추가</span></button>'+
                           '</div>'+
                         '</div>';
      $(data).parent().parent().append(paramBtnHtml);
    }

    dragDrop();
  }
  

  // 응답 파라미터 파라미터 추가 (body 부분)
  function paramResBodyAddBtn_org(data) {
    var aHtml = "";
    if ($(data).parent().parent().parent().attr("class").indexOf("responseForm") > -1) {
      aHtml = $("#paramResBodyDataTypeForm").find(".fr").html();
      $("#paramResBodyDataTypeForm").find(".fr").find("a").remove();
    }
    requiredNum = requiredNum + 1;

    if ($("#paramResBodyDataTypeForm").find("tbody").find(".example").length == 0) {
      $("#paramResBodyDataTypeForm").find("tbody").find("tr").eq(2).append('<th scope="row"><div class="essential">예제</div></th>');
      $("#paramResBodyDataTypeForm").find("tbody").find("tr").eq(2).append('<td class="example"><div><input type="text" name="example" title="예제 입력"></div></td>');
    }

    $("#paramResBodyDataTypeForm").find("input[name='required']").attr("id","required"+requiredNum);
    $("#paramResBodyDataTypeForm").find("input[name='required']").next().attr("for","required"+requiredNum);

    if ($(data).parent().parent().parent().find("section").length == 1) {
      paramBtnHtml =  '<div class="paraDiv_drag">'+
                        '<div class="div_draging">'+
                         $("#paramResBodyDataTypeForm").html() +
                        '</div>'+
                      '</div>';
      $(data).parent().parent().append(paramBtnHtml);
    }
    else {
      $("#popupConfirm").parent().find("div").eq(0).children("span").text("API");
      $("#popupConfirm").find('#alertTxt').html('body파라미터는 1개만 추가 할수 있습니다.');
      $("#popupConfirm").dialog( "open" );
    }
    if ($(data).parent().parent().parent().attr("class").indexOf("responseForm") > -1) {
      aHtml = $("#paramResBodyDataTypeForm").find(".fr").html(aHtml);
    }
  }

    // 불러온 api정보 세팅
  function apiPathInfoSet_org() {
    var paramApiMethod = "${param.apiMethod}";
    var paramApiPath   = "${param.apiPath}";

    //--[drm][add] {
    //-- check object exist
    if (typeof(yamlOb['paths']) == 'undefined') {
      alert_message('[o-o] yaml.paths object is not exist'); return;
    }
    if (typeof(yamlOb['paths'][paramApiPath]) == 'undefined') {
      alert_message('[o-o] yaml.paths.' + paramApiPath + ' object is not exist'); return;
    }
    if (typeof(yamlOb['paths'][paramApiPath][paramApiMethod.toLowerCase()]) == 'undefined') {
      alert_message('[o-o] yaml.paths.' + paramApiPath + '.' + paramApiMethod.toLowerCase() + ' object is not exist'); return;
    }
    //--[drm][add] }

    var pathInfoOb = yamlOb['paths'][paramApiPath][paramApiMethod.toLowerCase()];

    // 이름 세팅
    $("#apiNm").val(pathInfoOb['summary']);
    // path 세팅
    $("#apiPath").val(paramApiPath);
    // api id 세팅
    $("#apiId").val(pathInfoOb.operationId);
    // 설명 세팅
    $("#apiDesc").val(pathInfoOb.description);

    /********** 보안 탭 세팅 시작 ********************************/
    if(pathInfoOb.security != undefined){
      var noSelectHtml = '' +
        '<div>'+
          '<a href="javascript:;">'+
            '<input type="checkbox" id="public_schema0" name="noGlobalSchema" title="No authentication" value="no" onclick="noGlobalSchema(this)">'+
            '<label for="public_schema0"><span></span>No authentication</label>'+
          '</a>'+
        '</div>';
      $("#securityType").html("");
      $("#securityType").append(noSelectHtml);

      var securityHmlt = '';
      var securityNum = 0;
      var securityChecked = "";
      $("input[name='setyrityType'][id='inherit']").prop('checked', true);
      $.each(yamlOb.securityDefinitions , function (index, info) {
        securityNum = securityNum + 1;
        securityHmlt = "";
        securityChecked = "";
        securityScopeList = "";
        securityDisabled = "disabled";
        securityScopeArray = new Array();
        if(pathInfoOb.security != undefined){
          $.each(pathInfoOb.security , function (num, item) {
            if(item != undefined){
              $.each(item , function (num2, item2) {
                if(index == num2){
                  securityChecked = "checked";
                  securityDisabled        = "";
                  if(item2 != undefined){
                    $.each(item2 , function (num3, item3) {
                      securityScopeArray.push(item3);
                    });
                  }
                }
              });
            }
          });
        }
        else {
          $("input[name='noGlobalSchema']").prop("checked", true);
        }
        if (info.type == "oauth2") {
          var scopesOptionHtml = '';
          if (info.scopes != undefined) {
            $.each(info.scopes , function (scopeNum, scopeItem) {
              scopesOptionHtml = scopesOptionHtml + '<option value="'+scopeNum+'">'+scopeNum+'</option>';
            });
          }
          if (securityScopeArray != undefined) {
            $.each(securityScopeArray , function (scopeNum, scopeItem) {
              securityScopeList = securityScopeList + '<li><span>'+scopeItem+'</span>';
              if (pathInfoOb.security != undefined) {
                securityScopeList = securityScopeList + '<button type="button" title="삭제" class="btn btn_garbage" onclick="scopesRemove(this);"><span>삭제</span></button>';
              }
              securityScopeList = securityScopeList + '</li>';
            });
          }
          securityHmlt = '' +
            '<div>'+
              '<a href="javascript:;">'+
                '<input type="checkbox" id="public_schema'+securityNum+'" name="securityType" title="'+index+'"  onclick="oauthClik(this)" value="'+index+'" ' + securityChecked + '>'+
                '<label for="public_schema'+securityNum+'"><span></span>'+index+'</label>'+
              '</a>'+
              '<dl class="range_wrap">'+
                '<dt>'+
                  '<label>범위</label>'+
                  '<select class="wx140" name="scopesBox'+securityNum+'" onclick="scopesSelect('+securityNum+')"   '+securityDisabled+'   >'+
                    '<option value="">범위를 선택하여 주세요</option>'+ scopesOptionHtml +
                  '</select>'+
                '</dt>'+
                '<dd>'+
                  '<ol class="scopes' + securityNum + ' oauthScope">'+ securityScopeList + '</ol>'+
                '</dd>'+
              '</dl>'+
            '</div>';
        }
        else {
          securityHmlt = '' +
            '<div>'+
              '<a href="javascript:;">'+
                '<input type="checkbox" id="public_schema' + securityNum + '" name="securityType" title="' + index + '" value="' + index + '" ' + securityChecked + ' onclick="onGlobalSchema(this);" >'+
                '<label for="public_schema' + securityNum + '"><span></span>' + index + '</label>'+
               '</a>'+
            '</div>';
        }
        $("#securityType").append(securityHmlt);
        if (pathInfoOb.security != undefined) {
          $("input:radio[name='setyrityType'][value='custom']").prop("checked", true);
        }
      });
    };
    /********** 보안 탭 세팅 끝 ********************************/
    /********** 파라미터 타입 탭 세팅 시작 ********************************/
    var reqQueryArray = new Array();
    var reqHeadersArray = new Array();
    var reqPathArray = new Array();
    var reqFormDataArray = new Array();
    var reqBodyArray = new Array();
    // 타입이 Object 또는 배열일 경우 div 세팅

    //--##[drm][chg]if(pathInfoOb.parameters.length > 0){
    if (pathInfoOb.parameters != undefined) {
      for(var i=0; i < pathInfoOb.parameters.length; i++){
        var paramVar = pathInfoOb.parameters[i];
        if (paramVar['in'] == "query")   { reqQueryArray.push(paramVar);    }
        else if (paramVar['in'] == "header")  { reqHeadersArray.push(paramVar);  }
        else if (paramVar['in'] == "path")    { reqPathArray.push(paramVar);     }
        else if (paramVar['in'] == "formData"){ reqFormDataArray.push(paramVar); }
        else if (paramVar['in'] == "body")    { reqBodyArray.push(paramVar);     }
      }
    }
    // query 파라미터 셋팅
    if(reqQueryArray.length > 0){
      $.each(reqQueryArray, function(index, item){
        requiredNum = requiredNum + 1;
        $("#paramForm").find("a").css("display", "");
        $("#paramForm").find("input[name='required']").attr("id","required"+requiredNum);
        $("#paramForm").find("input[name='required']").next().attr("for","required"+requiredNum);

        if($(".reqQuery").children(".parameter_add").children(".paraDiv_drag").length == 0){
          var queryHtml =   '<div class="paraDiv_drag">'+
                        '<div class="div_draging">'+
                            '<button type="button" title="파라미터 추가" class="btn btn_addParabox"   onclick="paramAdd(this)"><span>파라미터 추가</span></button>'+
                              $("#paramForm").html() +
                        '<button type="button" title="파라미터 추가" class="btn btn_sml btn_gray" onclick="paramAdd(this)"><span>파라미터 추가</span></button>'+
                        '</div>'+
                    '</div>';
          $(".reqQuery").children(".parameter_add").append(queryHtml);
        } else {
          $(".reqQuery").children(".parameter_add").children(".paraDiv_drag").children(".div_draging").find("button").last().before($("#paramForm").html());
        }
        $($(".reqQuery").children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[index]).find("input[name='name']").val(item.name);
        $($(".reqQuery").children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[index]).find("input[name='required']").prop("checked", item.required);
        $($(".reqQuery").children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[index]).find("input[name='account']").val(item.description);
        $($(".reqQuery").children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[index]).find("select[name='type']").val(lowString(item.type));
        $($(".reqQuery").children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[index]).find("input[name='example']").val(item['x-example']);
        if(item.type == "array"){
          dataInfoArrayDivSet(item.items, $($(".reqQuery").children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[index]), 3);
        }
      });
    }
    // headers 파라미터 셋팅
    if(reqHeadersArray.length > 0){
      $.each(reqHeadersArray, function(index, item){
        requiredNum = requiredNum + 1;
        $("#paramForm").find("a").css("display", "");
        $("#paramForm").find("input[name='required']").attr("id","required"+requiredNum);
        $("#paramForm").find("input[name='required']").next().attr("for","required"+requiredNum);

        if($(".reqHeaders").children(".parameter_add").children(".paraDiv_drag").length == 0){
          var headersHtml =   '<div class="paraDiv_drag">'+
                        '<div class="div_draging">'+
                            '<button type="button" title="파라미터 추가" class="btn btn_addParabox"   onclick="paramAdd(this)"><span>파라미터 추가</span></button>'+
                              $("#paramForm").html() +
                        '<button type="button" title="파라미터 추가" class="btn btn_sml btn_gray" onclick="paramAdd(this)"><span>파라미터 추가</span></button>'+
                        '</div>'+
                    '</div>';
          $(".reqHeaders").children(".parameter_add").append(headersHtml);
        } else {
          $(".reqHeaders").children(".parameter_add").children(".paraDiv_drag").children(".div_draging").find("button").last().before($("#paramForm").html());
        }
        $($(".reqHeaders").children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[index]).find("input[name='name']").val(item.name);
        $($(".reqHeaders").children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[index]).find("input[name='required']").prop("checked", item.required);
        $($(".reqHeaders").children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[index]).find("input[name='account']").val(item.description);
        $($(".reqHeaders").children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[index]).find("select[name='type']").val(lowString(item.type));
        $($(".reqHeaders").children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[index]).find("input[name='example']").val(item['x-example']);
        if(item.type == "array"){
          dataInfoArrayDivSet(item.items, $($(".reqHeaders").children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[index]), 3);
        }
      });
    }
    // 패스 파라미터 셋팅
    if(reqPathArray.length > 0){
      $.each(reqPathArray, function(index, item){
        requiredNum = requiredNum + 1;
        $("#paramForm").find("a").css("display", "");
        $("#paramForm").find("input[name='required']").prop("disabled", true);
        $("#paramForm").find("input[name='required']").attr("id","required"+requiredNum);
        $("#paramForm").find("input[name='required']").next().attr("for","required"+requiredNum);

        if($(".reqPath").children(".parameter_add").children(".paraDiv_drag").length == 0){
          var pathHtml =    '<div class="paraDiv_drag">'+
                        '<div class="div_draging">'+
                            '<button type="button" title="파라미터 추가" class="btn btn_addParabox"   onclick="paramAdd(this)"><span>파라미터 추가</span></button>'+
                              $("#paramForm").html() +
                        '<button type="button" title="파라미터 추가" class="btn btn_sml btn_gray" onclick="paramAdd(this)"><span>파라미터 추가</span></button>'+
                        '</div>'+
                    '</div>';
          $(".reqPath").children(".parameter_add").append(pathHtml);
        } else {
          $(".reqPath").children(".parameter_add").children(".paraDiv_drag").children(".div_draging").find("button").last().before($("#paramForm").html());
        }
        $($(".reqPath").children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[index]).find("input[name='name']").val(item.name);
        $($(".reqPath").children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[index]).find("input[name='required']").prop("checked", item.required);
        $($(".reqPath").children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[index]).find("input[name='account']").val(item.description);
        $($(".reqPath").children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[index]).find("select[name='type']").val(lowString(item.type));
        $($(".reqPath").children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[index]).find("input[name='example']").val(item['x-example']);
        if(item.type == "array"){
          dataInfoArrayDivSet(item.items, $($(".reqPath").children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[index]), 3);
        }
      });
    }
    $("#paramForm").find("input[name='required']").prop("disabled", false);
    // 폼 데이터 파라미터 셋팅
    if(reqFormDataArray.length > 0){
      $.each(reqFormDataArray, function(index, item){
        requiredNum = requiredNum + 1;
        $("#paramForm").find("a").css("display", "");
        $("#paramForm").find("input[name='required']").attr("id","required"+requiredNum);
        $("#paramForm").find("input[name='required']").next().attr("for","required"+requiredNum);

        if($(".reqFormData").children(".parameter_add").children(".paraDiv_drag").length == 0){
          var formDataHtml =  '<div class="paraDiv_drag">'+
                        '<div class="div_draging">'+
                            '<button type="button" title="파라미터 추가" class="btn btn_addParabox"   onclick="paramAdd(this)"><span>파라미터 추가</span></button>'+
                              $("#paramForm").html() +
                        '<button type="button" title="파라미터 추가" class="btn btn_sml btn_gray" onclick="paramAdd(this)"><span>파라미터 추가</span></button>'+
                        '</div>'+
                    '</div>';
          $(".reqFormData").children(".parameter_add").append(formDataHtml);
        } else {
          $(".reqFormData").children(".parameter_add").children(".paraDiv_drag").children(".div_draging").find("button").last().before($("#paramForm").html());
        }
        $($(".reqFormData").children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[index]).find("input[name='name']").val(item.name);
        $($(".reqFormData").children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[index]).find("input[name='required']").prop("checked", item.required);
        $($(".reqFormData").children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[index]).find("input[name='account']").val(item.description);
        $($(".reqFormData").children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[index]).find("select[name='type']").val(lowString(item.type));
        $($(".reqFormData").children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[index]).find("input[name='example']").val(item['x-example']);
        if(item.type == "array"){
          dataInfoArrayDivSet(item.items, $($(".reqFormData").children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[index]), 3);
        }
      });
    }
    // body데이터
    if(reqBodyArray.length > 0){
      if(pathInfoOb.consumes != undefined){
        for(var i=0;i < pathInfoOb.consumes.length; i++){
          $("input[name='reqContentType']").each(function(index, value){
            if(pathInfoOb.consumes[i] == value.value){
              this.checked = true;
              $(value).prop("checked", true);
            }
          });
        }
      }

      $.each(reqBodyArray, function(index, item){
        requiredNum = requiredNum + 1;
        if(item.schema['type'] != undefined){

          if(item.schema.type == "object"  && $("#paramResBodyDataTypeForm").find("tbody").find(".example").length > 0){
            $("#paramResBodyDataTypeForm").find("tbody").find("tr").eq(2).find("th").eq(1).remove();
            $("#paramResBodyDataTypeForm").find("tbody").find("tr").eq(2).find("td").eq(1).remove();
          } else if( $("#paramResBodyDataTypeForm").find("tbody").find(".example").length == 0){
            $("#paramResBodyDataTypeForm").find("tbody").find("tr").eq(2).append('<th scope="row"><div class="essential">예제</div></th>');
            $("#paramResBodyDataTypeForm").find("tbody").find("tr").eq(2).append('<td class="example"><div><input type="text" name="example" title="예제 입력"></div></td>');
          }

          $("#paramResBodyDataTypeForm").find("input[name='required']").attr("id","required"+requiredNum);
          $("#paramResBodyDataTypeForm").find("input[name='required']").next().attr("for","required"+requiredNum);

          if($(".reqBody").children(".parameter_add").children(".paraDiv_drag").length == 0){
            var bodyHtml =  '<div class="paraDiv_drag">'+
                        '<div class="div_draging">'+
                            $("#paramResBodyDataTypeForm").html() +
                        '</div>'+
                    '</div>';
            $(".reqBody").children(".parameter_add").append(bodyHtml);
          } else {
            $("#paramForm").find("a").css("display", "");
            $(".reqBody").children(".parameter_add").children(".paraDiv_drag").children(".div_draging").find("button").last().before($("#paramForm").html());
          }

          $($(".reqBody").children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[index]).find("input[name='name']").val(item.name);
          $($(".reqBody").children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[index]).find("input[name='required']").prop("checked", item.required);
          //--##[drm][chg][maybe bug]$("textarea[name='reqBodyAccount']").val(item.description);
          $(".reqBody").find("textarea[name='reqBodyAccount']").val(item.description);
          $($(".reqBody").children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[index]).find("input[name='account']").val(item.schema.description);
          $($(".reqBody").children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[index]).find("select[name='type']").val(lowString(item.schema.type));

          $($(".reqBody").children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[index]).find("input[name='example']").val(item['x-example']);
          if(item.schema.type == "array"){
            resDataInfoArrayDivSet(item.schema.items, $($(".reqBody").children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[index]), 3);
          } else if (item.schema.type == "object"){
            resDataInfoObjectDivSet(item.schema.properties, $(".reqBody").children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section"), item.schema);
          }
        } else {
          // datatype 이 있을경우
          $("#paramResBodyDataTypeForm").find("input[name='required']").attr("id","required"+requiredNum);
          $("#paramResBodyDataTypeForm").find("input[name='required']").next().attr("for","required"+requiredNum);

          var bodyHtml =  '<div class="paraDiv_drag">'+
                      '<div class="div_draging">'+
                          $("#paramResBodyDataTypeForm").html() +
                      '</div>'+
                  '</div>';
          $(".reqBody").children(".parameter_add").append(bodyHtml);

          $($(".reqBody").children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[index]).find("input[name='name']").val(item.name);
          $($(".reqBody").children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[index]).find("input[name='required']").prop("checked", item.required);
          //--##[drm][chg][maybe bug]$("textarea[name='reqBodyAccount']").val(item.description);
          $(".reqBody").find("textarea[name='reqBodyAccount']").val(item.description);
          $($(".reqBody").children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[index]).find("input[name='account']").val(item.description);
          $($(".reqBody").children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[index]).find("select[name='type']").val(item.schema['$ref'].replace("#/definitions/",""));
          $($(".reqBody").children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[index]).find("input[name='example']").val(item['x-example']);
        }
      });
    }

    /********** 파라미터 타입 탭 세팅 끝 ********************************/

    /**********응답 파라미터 타입 탭 세팅 시작 ********************************/
    if(pathInfoOb.responses != undefined){
      $.each(pathInfoOb.responses, function(index, item){
        tabNum = tabNum + 1;
        var formHtml = '';
        var tabHtml  = '';
        // 보안 스키마 탭 append 시작
        tabHtml =   '<div id="tab'+tabNum+'" data-tab="tab'+tabNum+'" onclick="onTab('+tabNum+');">'+
                  '<a href="javascript:;" title="basic"><span>'+index+'</span></a><button type="button" title="삭제" class="btn btn_garbage" onclick="responseTabDel('+tabNum+');"><span>삭제</span></button>'+
                  '</div>';
          $("#responseTab").append(tabHtml);
        // 보안 스키마 탭 append 끝

        // 보안 스키마 탭 form append 시작
        formHtml =  '<div id="tabForm'+tabNum+'" class="tab-content" data-tabNum="'+tabNum+'">'+
                $("#responseTabForm").html();
              '</div>';
        $(".tab_wraping").append(formHtml);
        // 보안 스키마 탭 form append 끝
        $("#tabForm"+tabNum).find("select").val(index);
        $("#tabForm"+tabNum).find("input[name='resAccont']").val(item.description);

        // header 추가
        $("#responseHeaderForm").find(".schema_wrap").attr("id","headerForm"+tabNum);
        $("#responseDiv").append($("#responseHeaderForm").html());

        if(pathInfoOb.responses[index].headers != undefined){
          var paramDept = 0;
          $.each(pathInfoOb.responses[index].headers, function(headIndex, headItem){
            requiredNum = requiredNum + 1;
            $("#paramForm").find("a").css("display", "none");
            $("#paramForm").find("input[name='required']").attr("id","required"+requiredNum);
            $("#paramForm").find("input[name='required']").next().attr("for","required"+requiredNum);

            if($("#headerForm"+tabNum).children(".parameter_add").children(".paraDiv_drag").length == 0){
              var responseHeaders =   '<div class="paraDiv_drag">'+
                            '<div class="div_draging">'+
                                '<button type="button" title="파라미터 추가" class="btn btn_addParabox"   onclick="paramAdd(this)"><span>파라미터 추가</span></button>'+
                                  $("#paramForm").html() +
                            '<button type="button" title="파라미터 추가" class="btn btn_sml btn_gray" onclick="paramAdd(this)"><span>파라미터 추가</span></button>'+
                            '</div>'+
                        '</div>';
              $("#headerForm"+tabNum).children(".parameter_add").append(responseHeaders);
            } else {
              $("#headerForm"+tabNum).children(".parameter_add").children(".paraDiv_drag").children(".div_draging").find("button").last().before($("#paramForm").html());
            }
            $($("#headerForm"+tabNum).children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[paramDept]).find("input[name='name']").val(headIndex);
            /* $("#headerForm"+tabNum).children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section").find("input[name='required']").prop("checked", item.required); */
            $($("#headerForm"+tabNum).children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[paramDept]).find("input[name='account']").val(headItem.description);
            $($("#headerForm"+tabNum).children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[paramDept]).find("input[name='example']").val(headItem['x-example']);
            $($("#headerForm"+tabNum).children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[paramDept]).find("select[name='type']").val(lowString(headItem.type));
            $($("#headerForm"+tabNum).children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[paramDept]).find("input[name='required']").prop("checked", headItem.required);

            if(headItem.type == "array"){
              dataInfoArrayDivSet(headItem.items, $($("#headerForm"+tabNum).children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[paramDept]), 2);
            }
            paramDept = paramDept + 1;
          });
        }


        // body 추가
        if(pathInfoOb.produces != undefined){
          for(var i=0;i < pathInfoOb.produces.length; i++){
            $("input[name='resContentType']").each(function(index, value){
              if(pathInfoOb.produces[i] == value.value){
                this.checked = true;
                $(value).prop("checked", true);
              }
            });
          }
        }
        $("#responseBodyForm").find("textarea").parent().html('<textarea title="설명 입력" id="resAccount'+tabNum+'" onchange="apiRegCheckStrLength(4000,\'resAccount'+tabNum+'\')"   onkeyup="apiRegCheckStrLength(4000,\'resAccount'+tabNum+'\')" ></textarea>');
        $("#responseBodyForm").find(".schema_wrap").attr("id","bodyForm"+tabNum);
        $.each($("#responseBodyForm").find("input[name='resContentType']"), function(index, item){
          $(item).attr("id", $(item).attr("id") + "_1");
          $(item).next().attr("for", $(item).attr("id"));
        });

        $("#responseDiv").append($("#responseBodyForm").html());
        // 스키마가 없을경우 바디 파라미터 세팅 안함
        if(!jQuery.isEmptyObject(pathInfoOb.responses[index].schema)){
          var bodyVar = pathInfoOb.responses[index].schema;
          requiredNum = requiredNum + 1;
          $("#bodyForm"+tabNum).find("textarea").val(bodyVar['x-description']);
          // 데이터 타입 이용 안할시에
          var aHtml = "";
          aHtml = $("#paramResBodyDataTypeForm").find(".fr").html();
          $("#paramResBodyDataTypeForm").find(".fr").find("a").remove();
          if(bodyVar['type'] != undefined){
            if(bodyVar.type == "object"){
              $("#paramResBodyDataTypeForm").find("tbody").find("tr").eq(2).find("th").eq(1).remove();
              $("#paramResBodyDataTypeForm").find("tbody").find("tr").eq(2).find("td").eq(1).remove();
            } else if( $("#paramResBodyDataTypeForm").find("tbody").find(".example").length == 0){
              $("#paramResBodyDataTypeForm").find("tbody").find("tr").eq(2).append('<th scope="row"><div class="essential">예제</div></th>');
              $("#paramResBodyDataTypeForm").find("tbody").find("tr").eq(2).append('<td class="example"><div><input type="text" name="example" title="예제 입력"></div></td>');
            }

            $("#paramResBodyDataTypeForm").find("input[name='required']").attr("id","required"+requiredNum);
            $("#paramResBodyDataTypeForm").find("input[name='required']").next().attr("for","required"+requiredNum);

            if($("#bodyForm"+tabNum).children(".parameter_add").children(".paraDiv_drag").length == 0){
              var bodyHtml =  '<div class="paraDiv_drag">'+
                          '<div class="div_draging">'+
                              $("#paramResBodyDataTypeForm").html() +
                          '</div>'+
                      '</div>';
              $("#bodyForm"+tabNum).children(".parameter_add").append(bodyHtml);
            } else {
              $("#bodyForm"+tabNum).children(".parameter_add").children(".paraDiv_drag").children(".div_draging").find("button").last().before($("#paramBodyExampleForm").html());
            }
            $($("#bodyForm"+tabNum).children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")).find("input[name='name']").val(bodyVar['x-name']);
            /* $($("#bodyForm"+tabNum).children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")[index]).find("input[name='required']").prop("checked", item.required); */
            //--##[drm][cmt]$("textarea[name='reqBodyAccount']").val(bodyVar.description);
            $($("#bodyForm"+tabNum).children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")).find("input[name='account']").val(bodyVar.description);
            $($("#bodyForm"+tabNum).children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")).find("select[name='type']").val(lowString(bodyVar.type));

            $($("#bodyForm"+tabNum).children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")).find("input[name='example']").val(bodyVar.example);

            if(bodyVar['required'] != undefined){
              if(jQuery.inArray(bodyVar['x-name'], bodyVar['required']) != -1){
                $($("#bodyForm"+tabNum).children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")).find("input[name='required']").prop("checked", true);
              } else {
                $($("#bodyForm"+tabNum).children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")).find("input[name='required']").prop("checked", false);
              }
            }

            if(bodyVar.type == "array"){
              // 예제 세팅
              resDataInfoArrayDivSet(bodyVar.items, $($("#bodyForm"+tabNum).children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")), 3);
            } else if (bodyVar.type == "object"){
              resDataInfoObjectDivSet(bodyVar.properties[bodyVar['x-name']].properties, $("#bodyForm"+tabNum).children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section"), bodyVar.properties[bodyVar['x-name']]);
            }
          }
          // 데이터 타입 이용 시에
          else {
            $("#paramResBodyDataTypeForm").find("input[name='required']").attr("id","required"+requiredNum);
            $("#paramResBodyDataTypeForm").find("input[name='required']").next().attr("for","required"+requiredNum);

            var bodyHtml =  '<div class="paraDiv_drag">'+
                        '<div class="div_draging">'+
                            $("#paramResBodyDataTypeForm").html() +
                        '</div>'+
                    '</div>';
            $("#bodyForm"+tabNum).children(".parameter_add").append(bodyHtml);
            //--##[drm][cmt]$("textarea[name='reqBodyAccount']").val(bodyVar.description);
            $($("#bodyForm"+tabNum).children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")).find("input[name='required']").prop("checked", bodyVar.required);
            $($("#bodyForm"+tabNum).children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")).find("input[name='example']").val(bodyVar.example);
            $($("#bodyForm"+tabNum).children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")).find("input[name='name']").val(bodyVar['x-name']);
            $($("#bodyForm"+tabNum).children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")).find("input[name='account']").val(bodyVar.description);
            $($("#bodyForm"+tabNum).children(".parameter_add").children(".paraDiv_drag").children(".div_draging").children("section")).find("select[name='type']").val(bodyVar['$ref'].replace("#/definitions/",""));
          }
          $("#paramResBodyDataTypeForm").find(".fr").html(aHtml);
        }
      });
      onTab(1);
    }
    /**********응답 파라미터 타입 탭 세팅 끝 ********************************/

    dragDrop();
  }
