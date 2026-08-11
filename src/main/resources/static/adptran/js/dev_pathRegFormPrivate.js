	//-- pathRegFormPrivate.jsp에 포함 테스트 관련

	$(document).ready(function() {
    fn_init_test_01.m_init();
		//-- fn_init_test_02.m_init();
		//-- fn_init_test_03.m_init();
	});


  //-- [drm]개발용script {
  var fn_dev = {
    fn_alert: (function(msg) {
      alert(msg);
    }),

    //-- 입력값을 console에 출력
    fn_console_disp_param_value: (function (elem, p_cmd) {
      var jq_param_root = fn_ui_get_param_root(p_elem);
      var a_field = [ 'name', 'account', 'type', 'example', 'fixedValue', 'bigo', 'mappingKey', 'personalData', 'doNotSend', 'hidden', 'required', 'resultMapping' ];
      a_field.forEach(function(p_field) {
        var s_value = fn_get_input_value(p_field, jq_param_root);
        if ('yaml' == p_cmd) {
          s_value = fn_fmt_yaml_value(p_field, jq_param_root);
        }
        console.info('[o-o][p_cmd: ' + p_cmd + ']', '[field: ' + p_field + '][value: ' + s_value + ']');
      });
    }),

    //-- hidden field를 console에 출력
    fn_console_disp_hidden: (function() {
      console.info('[o-o]\n\t[#pApiSpcNo=%s]\n\t[#pApiNo=%s]\n\t[#pApiCtgryNo=%s]\n\t[#pApiCtgryNm=%s]\n\t[#pApiDataTypeNm=%s]\n\t[#pApiPath=%s]\n\t[#pApiMethod=%s]\n\t[#pApiCopyYn=%s]\n\t[#insertYn=%s]'
        , $('#pApiSpcNo').val()
        , $('#pApiNo').val()
        , $('#pApiCtgryNo').val()
        , $('#pApiCtgryNm').val()
        , $('#pApiDataTypeNm').val()
        , $('#pApiPath').val()
        , $('#pApiMethod').val()
        , $('#pApiCopyYn').val()
        , $('#insertYn').val()
      );
    }),
    
    //-- api_parameters_object display
    fn_disp_api_parameters_object: (function() {
      var s_api_parameter_object = '';
      var o_api_info = this.fn_ui_get_api_info();
      if (o_api_info != null) {
        var a_parameters = fn_cpapiget_data.fn_get_api_parameters_array(o_api_info);
        s_api_parameter_object = $sf_json_stringify(a_parameters, null, 2);
      }
      $('#id_drm_request_object').val(s_api_parameter_object);
    }),

    //-- api_request_object display
    fn_disp_api_request_object: (function() {
      var s_api_request_object = '';
      var o_api_info = this.fn_ui_get_api_info();
      if (o_api_info != null) {
        var o_api_request_object = fn_cpapiget_data.fn_get_api_request_object(o_api_info);
        s_api_request_object = $sf_json_stringify(o_api_request_object, null, 2);
      }
      $('#id_drm_request_object').val(s_api_request_object);
    }),

    //-- api_cpapirequest_object display
    fn_disp_api_cpapirequest_object: (function() {
      var s_api_cpapirequest_object = '';
      var o_api_info = this.fn_ui_get_api_info();
      if (o_api_info != null) {
        var o_api_cpapirequest_object = fn_cpapiget_data.fn_get_api_cpapirequest_object(o_api_info);
        s_api_cpapirequest_object = $sf_json_stringify(o_api_cpapirequest_object, null, 2);
      }
      $('#id_drm_request_object').val(s_api_cpapirequest_object);
    }),

    //-- get api info from UI
    fn_ui_get_api_info: (function() {
      var o_ret_api_info = null;
      
      var s_yamlSbst = $sf_str($('#yamlSbst').val());
      var s_apiPath = $sf_str($('#pApiPath').val());
      var s_apiMethod = $sf_str($('#pApiMethod').val()).toLowerCase();

      var o_yamlSbst = YAML.parse(s_yamlSbst);

      var o_target;
      if ($has_own(yamlOb, 'paths') == false) { fn_dev.fn_alert('paths is not exist'); return o_ret_api_info; }
      o_target = o_yamlSbst['paths'];
      if ($has_own(o_target, s_apiPath) == false) { fn_dev.fn_alert('paths.' + s_apiPath + ' is not exist'); return o_ret_api_info; }
      o_target = o_yamlSbst['paths'][s_apiPath];
      if ($has_own(o_target, s_apiMethod) == false) { fn_dev.fn_alert('paths.' + s_apiPath + '.' + s_apiMethod + ' is not exist'); return o_ret_api_info; }
      o_target = o_yamlSbst['paths'][s_apiPath][s_apiMethod];
      if ($has_own(o_target, 'parameters') == false) { this.fn_alert('paths.' + s_apiPath + '.' + s_apiMethod + '.parameters is not exist'); return o_ret_api_info; }
      
      o_ret_api_info = { 'yaml': s_yamlSbst, 'path': s_apiPath, 'method': s_apiMethod };
      return o_ret_api_info;
    }),
  };
  //-- [drm]개발용script }

  var fn_init_test_01 = {
    m_init: (function() {
      //-- build test area
      var jq_base = $('#yamlSbst');
      jq_base.before('<div id="id_drm_test_area"></div>');
  
      jq_base = $('#id_drm_test_area');
  
      var jq_btn, jq_elem;
      //-- toggle yaml {
      jq_btn = $('<input type="button" class="cid_test_btn" onclick="$(\'#yamlSbst\').toggle();" value="toggle yaml">');
      $('#yamlSbst').css({height: '300px'});
      jq_base.before(jq_btn);
      //-- }
      //-- console display hidden {
      jq_btn = $('<input type="button" class="cid_test_btn" onclick="fn_dev.fn_console_disp_hidden();" value="show hidden">');
      jq_base.before(jq_btn);
      //-- }
    }),
  };

  var fn_init_test_02 = {
  	m_init: (function() {
  		//-- build test area
  		var jq_base = $('#yamlSbst');
  		jq_base.before('<div id="id_drm_test_area"></div>');
  
  		jq_base = $('#id_drm_test_area');
  
  		var jq_btn, jq_elem;

  		//-- clear form {
  		jq_btn = $('<input type="button" class="cid_test_btn" onclick="fn_adpt_kosload.fn_init_form()" value="clear form">');
  		jq_base.before(jq_btn);
  		//-- }
  
  		//-- reload form {
  		jq_btn = $('<input type="button" class="cid_test_btn" onclick="fn_init_test_02.m_fn_reload_form()" value="reload form">');
  		jq_base.before(jq_btn);
  		var def_path = '';
  		var def_method = '';
  		//-- input path, method
  		jq_elem = $('<input type="text" class="cid_test_txt" id="id_drm_test_path" size="30" value="' + def_path + '"><input type="text" class="cid_test_txt" id="id_drm_test_method" size="4" value="' + def_method + '">');
  		jq_base.before(jq_elem);
  		$('#id_drm_test_path, #id_drm_test_method').css({'height':'auto', 'width':'auto'});
  		//-- }
  
  		//-- input test data textarea {
  		jq_btn = $('<input type="button" class="cid_test_btn" onclick="$(\'#id_drm_test_data\').toggle();" value="input test data">');
  		jq_base.before(jq_btn);
  
  		jq_elem = $('<textarea id="id_drm_test_data" style="display:none; width:100%; height:300px; background-color:lightgrey"></textarea>');
  		jq_base.before(jq_elem);
  		//-- }
  
  		//-- move file input for test {
  		//--##jq_elem = $('#id_comp_file_load').detach();
  		//--##jq_base.before(jq_elem);
  		//-- }
  
  		//-- toggle grid
  		jq_btn = $('<input type="button" class="cid_test_btn" onclick="$(\'#id_canvas_datagrid\').toggle();" value="toggle grid">');
  		jq_base.before(jq_btn);

  		//-- request object {
  		jq_btn = $('<input type="button" class="cid_test_btn" onclick="fn_dev.fn_disp_api_parameters_object()" value="api parameters object">');
  		jq_base.before(jq_btn);

  		jq_btn = $('<input type="button" class="cid_test_btn" onclick="fn_dev.fn_disp_api_request_object()" value="api request object">');
  		jq_base.before(jq_btn);

      jq_btn = $('<input type="button" class="cid_test_btn" onclick="fn_dev.fn_disp_api_cpapirequest_object()" value="api cpapirequest object">');
      jq_base.before(jq_btn);

  		jq_btn = $('<input type="button" class="cid_test_btn" onclick="$(\'#id_drm_request_object\').toggle();" value="toggle req object">');
  		jq_base.before(jq_btn);

  		jq_elem = $('<textarea id="id_drm_request_object" style="display:none; width:100%; height:300px; background-color:lightyellow;"></textarea>');
  		jq_base.before(jq_elem);
  		//-- }

  		//-- append ui element {
  		jq_elem = $('<div id="id_canvas_datagrid" style="display:none; overflow:scroll; padding:20px; width:100%; height:320px;"></div>');
  		jq_base.before(jq_elem);
  		//-- append ui element }

  		$('.cid_test_btn').css({'padding':'2px 4px', 'margin':'2px 4px 0 4px', 'cursor':'pointer', });
  		$('.cid_test_txt').css({'padding':'2px 4px', 'margin':'2px 4px 0 4px', 'text-indent': '0', });
  	}),
    m_fn_reload_form: (function() {
  		var s_path = $('#id_drm_test_path').val();
  		var s_method = $('#id_drm_test_method').val();
  		if (s_path.length == 0) {
  			alert('[o-o] path is not set'); return;
  		}
  		if (s_method.length == 0) {
  			alert('[o-o] method is not set'); return;
  		}
  
  		fn_adpt_kosload.fn_init_form();
  		apiPathInfoSet(s_path, s_method, $.trim($('#id_drm_test_data').val()));
  	}),
  };
  
  var fn_init_test_03 = {
		m_init: (function() {
	    //-- for param input display
      var jq_handle = $('input[name=name]').closest('tr').find('th:first div');   //-- parameter 이름title
      jq_handle.on('dblclick', function(p_evt) {
        fn_dev.fn_console_disp_param_value(this);
        fn_dev.fn_console_disp_param_value(this, 'yaml');
      });
  
      //-- 개발시 기능테스트를 위한 trigger handler
      //-- for common dev #test#
      var jq_handle = $('input[name=summary]').closest('tr').find('th:first div');   //-- api정보 이름title
      jq_handle.on('dblclick', function(p_evt) {
        m_fn_test();
      });
  
      var m_fn_test_01 = (function() {  // alert with click handler 테스트
        var alert_option = { ok_button_onclick: (function() { alert('click ok handler'); }), };
        alert_message('message', 'title', alert_option);
      });
    
      var m_fn_test_02 = (function() { // claer_form테스트
        fn_ui_clear_form(); alert('clear form');
      });
  
      var m_fn_test_03 = (function() { // init_form테스트
        fn_ui_init_form(); alert('init form');
      });
  
      var m_fn_test_04 = (function() { // init_form(kosload)테스트
        fn_ui_init_form('kosload'); alert('init form(kosload)');
      });
      
      var m_fn_test_05 = (function() {
      	fn_api_loding_ui('show', 'API Loding...');
        window.setTimeout((function () {
          fn_api_loding_ui('hide');
        }), 5000);
      });
  
      var m_fn_test = (function() {
        var a_choice = [];
        a_choice.push('alert');
        a_choice.push('clearform');
        a_choice.push('initform');
        a_choice.push('initformkosload');
        a_choice.push('loadingdialog');
  
        var choice = window.prompt(a_choice.join(', '));
        if ('alert' == choice) { m_fn_test_01(); }
        else if ('clearform' == choice) { m_fn_test_02(); }
        else if ('initform' == choice) { m_fn_test_03(); }
        else if ('initformkosload' == choice) { m_fn_test_04(); }
        else if ('loadingdialog' == choice) { m_fn_test_05(); }
      });
		}),
	}
