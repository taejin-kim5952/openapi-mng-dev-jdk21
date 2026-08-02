//-- KOS excel파일 parsing
//
const parse_kos_excel = {
  //-- alert창 처리 
  fn_alert: function(message, title) {
    //-- module 외부 handler설정확인 
    if (typeof(this.fn_alert_message) != 'undefined') {
      this.fn_alert_message(message, title);
    }
    else {
      //-- @apiGlobalScript.js
      ((typeof(alert_message) != 'undefined') ? $sf_func_call(alert_message, message, title) : void(0));
    }
  },

  //-- KOS file to form 처리
  fn_proc_data_KOS: function(json_data, sheetname, filename) {
    //--@@console.log('[o-o]', '[proc_data_KOS()]', 'json_data: ', json_data, 'sheetname: ', sheetname, 'filename: ', filename);

    //-- { path, method, oas }
    var o_ret = this.fn_KOS_data_to_OAS2(json_data, sheetname);
    if (!o_ret) { return; }

    var s_path = $sf_obj_val(o_ret, 'path', '');
    var s_method = $sf_obj_val(o_ret, 'method', '');
    var o_oas = $sf_obj_val(o_ret, 'oas', {});

    var json_oas = $.trim($sf_json_stringify(o_oas));

    //-- @pathRegFormPrivate.jsp
    ((typeof(fn_oas_to_form) != 'undefined') ? $sf_func_call(fn_oas_to_form, s_path, s_method, json_oas, filename) : void(0)); //-- load form
  },  //-- fn_proc_data_KOS()

  //-- KOS file load 오류메시지
  fn_get_KOS_error_message: function(error_code) {
    var s_msg = '정의되지 않은 오류 - [err: ' + error_code + ']';
    switch (error_code) {
      case -101: s_msg = 'sheet가 4개 이상이 아님'; break;
      case -102: s_msg = '#4 sheet에 이름이 없음';  break;

      case -103: s_msg = 'sheet 이름이 지정되지 않음';  break;
      case -104: s_msg = '지정된 cell의 field name이 불일치'; break;
      case -105: s_msg = 'API NAME항목과 #4 sheet명이 불일치';  break;
      case -106: s_msg = 'Service Url(PROD) 값 없음'; break;
      case -107: s_msg = 'Service Url(PROD) 형식오류'; break;
      case -108: s_msg = 'Service Url(PROD) 에서 path parsing 값 없음'; break;
      case -109: s_msg = 'protocol 입력 형식오류';  break;
      case -110: s_msg = '정의되지 않은 protocol';  break;
      case -111: s_msg = 'test url 값 없음';  break;
      case -112: s_msg = 'test url 형식오류'; break;
      case -113: s_msg = 'parameter row: datatype 오류';  break;
      case -114: s_msg = 'parameter row: not in section'; break;
      case -115: s_msg = 'parameter row: parent not found 오류';  break;
      default: break; //-- [2023:codeeyes][swtich_default issue]
    }
    return s_msg;
  },  //-- fn_get_KOS_error_message()

  //-- KOS file load 오류처리
  fn_proc_error_KOS: function(error_code, error_data) {
    if (typeof(this.fn_handler_on_proc_error_KOS) != 'undefined') {
      this.fn_handler_on_proc_error_KOS(error_code, error_data);
    }
    //-- @pathRegFormPrivate.jsp
    ((typeof(fn_handler_on_proc_error_KOS) != 'undefined') ? $sf_func_call(fn_handler_on_proc_error_KOS) : void(0)); //-- 오류시 처리내용 handler

    error_data = $sf_str(error_data);
    console.warn('[o-o]', '[fn_proc_error_KOS()]', 'error_code: ', error_code, 'error_data: ', error_data);
    var message = 'parsing error - [err: '  + error_code + ']\n\n' + this.fn_get_KOS_error_message(error_code) + ((error_data.length > 0) ? ('\n\n' + error_data) : '');
    var title = 'KOS spec file load';

    this.fn_alert(message, title);
  },  //-- fn_proc_error_KOS()

  //-- 2d array data to api json object
  fn_KOS_data_to_OAS2: function(arr_data, sheetname) {
    var m_fn_fmt_cell = (function(p_val) { return $.trim($sf_str(p_val)); });
    //-- get url path
    var m_fn_cmn_get_url_token = (function(p_cmd, p_url) {
      var o_url = $fn_parseUrl(p_url);
      var s_ret = '';
      if (p_cmd == 'url_only') { s_ret = o_url.origin + o_url.pathname; }
      else if (p_cmd == 'pathname') { s_ret = o_url.pathname; }
      return s_ret;
    });

    //-- const key name
    var con_SUMMARY = 'summary';
    var con_DESCRIPTION = 'description';
    var con_PROTOCOL = 'protocol';
    var con_ENDPNT_PRD_URL = 'endpnt_prd_url';
    var con_ENDPNT_TB_URL = 'endpnt_tb_url';

    //-- const row type {
    var con_ROW_INIT = -1;
    var con_ROW_EMPTY = 0;
    var con_ROW_REQUEST = 1;
    var con_ROW_RESPONSE = 2;
    var con_ROW_HEADER = 3;
    var con_ROW_BODY = 4;
    var con_ROW_PARAM = 5;
    var con_ROW_IGNORE = 6;
    //-- const row type }

    //-- excel파일의 정보위치
    //-- [0]:row, [1]:col, [2]:field name, [3]:api key name
    var o_api_info_fldnm = [
      [1, 3, 'API NAME', con_SUMMARY],
      [2, 3, 'API Desc', con_DESCRIPTION],
      [3, 1, 'protocol', con_PROTOCOL],
      [5, 1, 'Test Url', con_ENDPNT_TB_URL],
      [5, 3, 'Service Url', con_ENDPNT_PRD_URL],
    ];
    var n_parameter_start_row_idx = 6;

    var b_is_err = false;
    var error_data = '';

    //-- ### API기본정보 처리 {
    //-- sheet name check
    sheetname = m_fn_fmt_cell(sheetname);
    if (sheetname.length == 0) {
      this.fn_proc_error_KOS(-103); return;  //-- sheet 이름이 지정되지 않음
    }

    var o_api_info = {};
    //-- API항목값 position title check, value assign
    $.each(o_api_info_fldnm, function(idx, elem) {
      var row_idx = elem[0], col_idx = elem[1], field_name = elem[2], key_name = elem[3];
      var s_src = $sf_str(arr_data[row_idx][col_idx]);
      if (s_src.indexOf(field_name) != 0) {  //-- 시작점  포함여부 확인
        error_data = '[row#: ' + (row_idx + 1) + '][col#: ' + (col_idx + 1) + '][field_name: ' + field_name + ']';
        console.warn('[o-o][## field not match][row_idx: %d][col_idx: %d][field_name: %s][s_src: %s]', row_idx, col_idx, field_name, s_src);
        b_is_err = true;
        return false;
      }
      else {
        o_api_info[key_name] = $.trim(arr_data[row_idx][col_idx + 1]);  //-- 항목명+1 = 정보
      }
    });
    if (b_is_err) {
      this.fn_proc_error_KOS(-104, error_data);  return; //-- 지정된 cell의 field name이 불포함
    }
    //--[drm][chg]sheetname길이제한으로 indexOf로 변경
    //--if (o_api_info[con_SUMMARY] != sheetname) {
    /*-- [drm][cmt][sheet명 validation off]
    if ((o_api_info[con_SUMMARY]).indexOf(sheetname) == -1) {
      error_data = '[sheetname: ' + sheetname + '][API_NAME: ' + o_api_info[con_SUMMARY] + ']';
      this.fn_proc_error_KOS(-105, error_data);  return; //-- API NAME항목과 #4 sheet명이 불일치
    }
    --*/

    var s_endpnt_prd_url = $sf_str(o_api_info[con_ENDPNT_PRD_URL]);
    if (s_endpnt_prd_url.length == 0) {
      this.fn_proc_error_KOS(-106);  return; //-- Service Url(PROD) 값 없음
    }

    var s_url_only = m_fn_cmn_get_url_token('url_only', s_endpnt_prd_url);  //-- url 유효성확인
    if (s_url_only != s_endpnt_prd_url) {
      this.fn_proc_error_KOS(-107, error_data);  return false; //-- Service Url 형식오류
    }

    //-- [drm][chg][api path는 api명을 사용
    var s_path = ('/' + $sf_str(o_api_info[con_SUMMARY]));
    /*
    var s_path = m_fn_cmn_get_url_token('pathname', s_endpnt_prd_url);
    if (s_path.length == 0) {
      this.fn_proc_error_KOS(-108);  return; //-- Service Url(PROD) 에서 path parsing 값 없음
    }
    */

    var s_method = $sf_str(o_api_info[con_PROTOCOL]);
    var a_tmp = s_method.split('/');
    error_data = '[protocol: ' + s_method + ']';
    if (a_tmp.length != 2) {
      this.fn_proc_error_KOS(-109, error_data);  return; //-- protocol 입력 형식오류
    }

    var s_protocols = ';get;post;put;delete;head;options;patch';
    s_method = m_fn_fmt_cell(a_tmp[1]).toLowerCase();
    if (s_protocols.indexOf(';' + s_method + ';') == -1) {
      this.fn_proc_error_KOS(-110, error_data);  return; //-- 정의되지 않은 protocol
    }

    var s_endpnt_tb_url = $sf_str(o_api_info[con_ENDPNT_TB_URL]);

    //-- Test Url(DEV/TB/SIT) 항목 parsing
    var a_list = s_endpnt_tb_url.split('\n');
    if (a_list.length > 0) {  //-- [deprecated] 이전규칙(DEV/TB/SIT)분리
      a_list.some(function(val) {
        var a_tmp = val.split(':');
        error_data = '[url: ' + val + ']';
        if (a_tmp.length > 1) {
          //-- 첫번째 ':'를 기준으로 나눔
          var s_gub = $.trim(a_tmp[0]);
          a_tmp.splice(0, 1);
          var s_url = $.trim(a_tmp.join(':'));
          error_data = '[gub: ' + s_gub + ']';
          if (s_gub == 'TB') {  //-- TB: 구분 url 설정
            s_endpnt_tb_url = s_url; return true;
          }
        }
        return false;
      });
    }

    if (s_endpnt_tb_url.length == 0) {
      this.fn_proc_error_KOS(-111);  return; //-- test url 값 없음
    }

    var url_only = m_fn_cmn_get_url_token('url_only', s_endpnt_tb_url);  //-- url 유효성확인
    if (url_only != s_endpnt_tb_url) {
      this.fn_proc_error_KOS(-112, error_data);  return false; //-- test url 형식오류
    }

    //-- 기본 OAS 구조 for KOS 구성
    var o_OAS = {
      'summary': $sf_str(o_api_info[con_SUMMARY]),
      'description': $sf_str(o_api_info[con_DESCRIPTION]),
      //--'operationId': '',
      //--'x-apiNo': '',
      //--'x-category': '',
      //--'x-visiblity': '',
      //--'x-display': '',
      'consumes': [ 'application/json' ],
      'produces': [ 'application/json' ],
      'x-x-endpnt_prd_url': $sf_str(o_api_info[con_ENDPNT_PRD_URL]),
      'x-x-endpnt_tb_url': $sf_str(o_api_info[con_ENDPNT_TB_URL]),

      'parameters': [],
      'responses': {
        '200': {
          'description': 'OK',
          'headers': {},
          'schema': {},
        },
      },
    };

    console.info('[o-o][fn_KOS_data_to_OAS2()][o_OAS: %o]', o_OAS);
    //-- ### API기본정보 처리 }

    //-- ### parameter 처리 {
    //-- arr_data 정의
    //-- [0]: N/A, [1]: 이름/parameter, [2]: 설명, [3]: 포맷, [4]: Parent, [5]: 크기(Byte), [6]: 필수, [7]: 고정값(상수), [8]: 비고, [9]: 개인정보 항목, [10]: CP 노출여부
    var m_fn_make_node = (function(a_data) {
      var a_data = $sf_arr(a_data);
      a_data[3] = (('List' == a_data[3]) ? 'Array' : a_data[3]);
      return {
        'id':-99, 'pid':-99,
        'parent': m_fn_fmt_cell(a_data[4]),
        'name': m_fn_fmt_cell(a_data[1]),
        'datatype': m_fn_fmt_cell(a_data[3]),
        'desc': m_fn_fmt_cell(a_data[2]),
        'required': m_fn_fmt_cell(a_data[6]),
        'size': m_fn_fmt_cell(a_data[5]),
        'default': m_fn_fmt_cell(a_data[7]),
        'etc': m_fn_fmt_cell(a_data[8]),
        'pv_data': m_fn_fmt_cell(a_data[9]),
        'cp': m_fn_fmt_cell(a_data[10]),
      };
    });

    var m_fn_wrap_root_node = (function(a_param, name, desc) {
      if (name.length == 0) { return; }

      var n_root_num = a_param.filter(function(o_param) {
        return (o_param['parent'].length == 0);
      }).length;
      //--@@console.log('[o-o][m_fn_wrap_root_node()][root_num: %d]', n_root_num);
      
      //--[i]always wrap root node
      //--[drm][190709][chg] (n_root_num > 1) -> (n_root_num > 0) 
      var b_is_rwap_root_node = (n_root_num > 0); 

      if (b_is_rwap_root_node == true) {
        var a_data = [];
        a_data[1] = name;
        a_data[2] = desc;
        a_data[3] = 'Object';
        var o_param = m_fn_make_node(a_data);
        a_param.forEach(function(o_param) {
          o_param['parent'] = (name + ((o_param['parent'].length > 0) ? '.' : '') + o_param['parent']);
        });
        a_param.unshift(o_param);
        //--@@console.log('[o-o][m_fn_wrap_root_node()][parameter wrapped][name: %s]', name);
      }
    });

    var m_fn_get_data_rowtype = (function(a_data) {
      var s_row = $.trim(a_data.join(''));
      if (s_row.length == 0) { return con_ROW_EMPTY; }
      if ('Request' == s_row) { return con_ROW_REQUEST; }
      if ('Response' == s_row) { return con_ROW_RESPONSE; }
      if (('SOAP Header' == s_row) || ('HTTP Header' == s_row)){ return con_ROW_HEADER; }
      if (('SOAP Body' == s_row) || ('HTTP Body' == s_row)) { return con_ROW_BODY; }

      var col_name = m_fn_fmt_cell(a_data[1]);
      var col_datatype = m_fn_fmt_cell(a_data[3]);
      if ((col_name.length > 0) && (col_datatype.length > 0)) { return con_ROW_PARAM; }

      return con_ROW_IGNORE;
    });

    var m_fn_fmt_datatype = (function(p_datatype) {
      var ret_datatype = m_fn_fmt_cell(p_datatype);
      ret_datatype = ((ret_datatype.indexOf('#') == -1) ? ret_datatype : ''); //-- datatype에 '#' 문자포함시 설정무시

      ret_datatype = ret_datatype.toLowerCase();
      if (['string', 'str'].indexOf(ret_datatype) != -1) { ret_datatype = 'String'; }
      else if (['integer', 'int', 'long'].indexOf(ret_datatype) != -1) { ret_datatype = 'Integer'; }
      else if (['number', 'num', 'decimal', 'bigdecimal' ].indexOf(ret_datatype) != -1) { ret_datatype = 'Number'; }
      else if (['boolean', 'bool'].indexOf(ret_datatype) != -1) { ret_datatype = 'Boolean'; }
      else if (['object', 'obj'].indexOf(ret_datatype) != -1) { ret_datatype = 'Object'; }
      else if (['array', 'arr', 'list'].indexOf(ret_datatype) != -1) { ret_datatype = 'Array'; }

      return ret_datatype;
    });

    var m_fn_fmt_parent = (function(p_parent) {
      var ret_parent = m_fn_fmt_cell(p_parent);
      ret_parent = ((ret_parent.indexOf('#') == -1) ? ret_parent : ''); //-- parent에 '#' 문자포함시 설정무시
      return ret_parent;
    });
    
    var m_fn_fmt_cp = (function(p_cp) {
      var ret_cp = m_fn_fmt_cell(p_cp);
      ret_cp = ((ret_cp == 'N') ? 'Y' : 'N');
      return ret_cp; 
    });

    var m_fn_is_valid_datatype = (function(p_section, p_param, p_datatype) {
      var con_DATATYPE_LIST = ';String;Number;Integer;Boolean;Object;Array;';
      if ('header' == p_param) {
        con_DATATYPE_LIST = ';String;Number;Integer;Boolean;Array;';
      }
      return (con_DATATYPE_LIST.indexOf(';' + p_datatype + ';') != -1);
    });

    //-- parameter
    var o_parameter = { req_header: [], req_body: [], res_header: [], res_body: [] };

    //-- parameter를 특성에 따라 분류
    var s_current_sec = ''; //-- request, response
    var s_current_param = ''; //-- header, body

    var n_empty_line_cnt = 0;
    var n_ignore_line_cnt = 0;
    var n_title_line_cnt = 0;
    var n_skip_line_cnt = 0;
    for (var n_ii = n_parameter_start_row_idx; n_ii < arr_data.length; n_ii++) {
      var a_data = arr_data[n_ii];

      var row_type = m_fn_get_data_rowtype(a_data);
      if (row_type == con_ROW_REQUEST) {
        s_current_sec = 'request';
        s_current_param = '';
        n_title_line_cnt++;
      }
      else if (row_type == con_ROW_RESPONSE) {
        s_current_sec = 'response';
        s_current_param = '';
        n_title_line_cnt++;
      }
      else if (row_type == con_ROW_HEADER) {
        n_title_line_cnt++;
        if (s_current_sec.length > 0) {
          s_current_param = 'header';
          n_ii++; //-- skip title row
          n_skip_line_cnt++;
        }
      }
      else if (row_type == con_ROW_BODY) {
        n_title_line_cnt++;
        if (s_current_sec.length > 0) {
          s_current_param = 'body';
          n_ii++; //-- skip title row
          n_skip_line_cnt++;
        }
      }
      else if (row_type == con_ROW_EMPTY) {
        s_current_param = '';
        n_empty_line_cnt++;
      }
      else if (row_type == con_ROW_IGNORE) {
        //-- ##s_current_param = '';
        n_ignore_line_cnt++;
      }
      else if (row_type == con_ROW_PARAM) {
        //-- 항목 format 변환
        a_data[3] = m_fn_fmt_datatype(a_data[3]);
        a_data[4] = m_fn_fmt_parent(a_data[4]);
        a_data[10] = m_fn_fmt_cp(a_data[10]);

        error_data = '[row#: ' + (n_ii + 1) + ']';
        var col_datatype = m_fn_fmt_cell(a_data[3]);
        var b_is_param_push = true;

        //-- except proc {
        if ('header' == s_current_param) {
          a_data[4] = ''; //-- header일시 parent 무시
        }
        //-- except proc }
        if (m_fn_is_valid_datatype(s_current_sec, s_current_param, col_datatype) == false) {
          b_is_err = true;
          //-- except proc {
          if ('header' == s_current_param) {
            a_data[4] = ''; //-- header일시 parent 무시
          }
          if (('header' == s_current_param) && ('Object' == col_datatype)) {
            var n_param_cnt = -1;
            if ('request' == s_current_sec) { n_param_cnt = o_parameter.req_header.length; }
            else if ('response' == s_current_sec) { n_param_cnt = o_parameter.res_header.length; }
            if (n_param_cnt == 0) { //-- header의 첫번째일시 Object type허용
              b_is_err = false;
              b_is_param_push = false;
            }
          }
          //-- except proc }

          if (b_is_err == true) {
            this.fn_proc_error_KOS(-113, error_data);  return; //-- datatype 오류
          }
        }

        if (b_is_param_push == true) {
          var o_param = m_fn_make_node(a_data);
          if (('request' == s_current_sec) && ('header' == s_current_param)) { o_parameter.req_header.push(o_param); }
          else if (('request' == s_current_sec) && ('body' == s_current_param)) { o_parameter.req_body.push(o_param); }
          else if (('response' == s_current_sec) && ('header' == s_current_param)) { o_parameter.res_header.push(o_param); }
          else if (('response' == s_current_sec) && ('body' == s_current_param)) { o_parameter.res_body.push(o_param); }
          else {
            this.fn_proc_error_KOS(-114, error_data);  return; //-- parameter row not in section
          }
        }
      }
    }
    console.warn('[o-o][fn_KOS_data_to_OAS2()][rows: %d][req_header: %d][req_body: %d][res_header: %d][res_body: %d]', arr_data.length, o_parameter.req_header.length, o_parameter.req_body.length, o_parameter.res_header.length, o_parameter.res_body.length);
    
    //-- tree구조를 위한 element의 id, pid 설정
    var m_fn_set_elem_id = (function(a_param) {
      //-- parent id를 구한다
      var m_fn_get_pid = (function(a_param, parent) {
        var pid = -1;
        if (parent.length == 0) { return pid; }
        pid = -2; //-- parent지정이 잘못됨
        a_param.every(function(o_param, idx) {
          var param_parent = ((o_param['parent'].length > 0) ? (o_param['parent'] + '.') : '') + o_param['name']; // parent + name
          if (param_parent == parent) { pid = idx; return false; }
          return true;
        });
        return pid;
      });
      a_param.forEach(function(o_param, idx) {
        o_param['id'] = idx; o_param['pid'] = m_fn_get_pid(a_param, o_param['parent']);
        //--##console.log('[o-o][o_param][#%d][%o]', idx, o_param);
      });
    });

    m_fn_wrap_root_node(o_parameter.req_body, 'request', 'Request Model');
    m_fn_wrap_root_node(o_parameter.res_body, 'response', 'Response Model');

    b_is_err = false;
    error_data = '';
    var idx_a_param = 0;
    $.each(o_parameter, function(key_param, a_param) {
      var a_param_name = ['Request Header', 'Request Body', 'Response Header', 'Response Body'];
      m_fn_set_elem_id(a_param);
      //-- check pid 오류
      a_param.every(function(o_param, idx) {
        if (o_param['pid'] == -2) { //-- parent지정 오류
          error_data = '[' + (a_param_name[idx_a_param]||'Unknown') + ' param row#: ' + (idx + 1 ) + ']';
          b_is_err = true;
          return false;
        }
        return true;
      });
      if (b_is_err) { return false; }
      idx_a_param++;
    });
    if (b_is_err) {
      this.fn_proc_error_KOS(-115, error_data);  return; //-- parameter parent not found 오류
    }

    //-- request header
    var a_req_param = fn_oas2.fn_get_req_header_parameters(o_parameter.req_header);
    o_OAS['parameters'] = o_OAS['parameters'].concat(a_req_param);
    //-- request body
    var o_res_param = fn_oas2.fn_get_req_body_parameter(o_parameter.req_body);
    o_OAS['parameters'].push(o_res_param);
    //-- response header
    o_OAS['responses']['200']['headers'] = fn_oas2.fn_get_res_header_headers(o_parameter.res_header);
    //-- response body
    o_OAS['responses']['200']['schema'] = fn_oas2.fn_get_res_body_schema(o_parameter.res_body);
    //-- ### parameter 처리 }
    //--@@console.log('[o-o][o_OAS][%o]', o_OAS);

    return {
      'path': s_path,
      'method': s_method,
      'oas': o_OAS
    };
  } //-- fn_KOS_data_to_OAS2()
}; //-- const parse_kos_excel

//--### build swagger object function (ref: pathReqForm.jsp) {
const fn_oas2 = {
  exampleOb: {},
  exampleArrayStr: '',

  //-- request header
  fn_get_req_header_parameters: (function(a_data) {
    var arr_root_node = this.convertToHierarchy(a_data);
    //--@@console.log('[o-o][req header hierarchy][%o]', arr_root_node);

    var paramArray = [];

    for (var n_ii = 0; n_ii < arr_root_node.length; n_ii++) {
      var data = arr_root_node[n_ii];
      var name = data.node['name'];
      var datatype = data.node['datatype'];
      var desc = data.node['desc'];
      var required = data.node['required'];
      var example = ' ';
      //-- ext {
      var x_required     = data.node['required'];
      var x_personalData = data.node['pv_data'];
      var x_fixedValue   = data.node['default'];
      var x_hidden       = data.node['cp'];
      var x_bigo         = data.node['etc'];
      //-- ext }

      var paramOb = {};
      paramOb['in'] = 'header';
      paramOb['name'] = name;
      paramOb['description'] = desc;
      paramOb['required'] = (required == 'Y');
      paramOb['x-example'] = example;
      paramOb['x-dataTypeCd'] = 'PRMTYP1010';   // PRMTYP1010(요청 파라미터), PRMTYP1020(응답 파라미터)
      //-- ext {
      paramOb['x-required']    = x_required;
      paramOb['x-personalData'] = this.fmt_oas2_data('personalData', x_personalData);
      paramOb['x-fixedValue']  = x_fixedValue;
      paramOb['x-hidden']      = x_hidden;
      paramOb['x-bigo']        = x_bigo;
      //-- ext }

      if ('Array' == datatype) {
        var emptyOb = {};
        this.jsp_typeArrayFn(data, emptyOb);
        paramOb['type']  = emptyOb[name]['type'];
        paramOb['items'] = emptyOb[name]['items'];
      }
      else {
        paramOb['type'] = datatype.toLowerCase();
      }
      paramArray.push(paramOb);
    }
    //--@@console.log('[o-o][req header parameters][%o]', paramArray);

    return paramArray;
  }),

  //-- request body
  fn_get_req_body_parameter: (function(a_data) {
    var arr_root_node = this.convertToHierarchy(a_data);
    //--@@console.log('[o-o][req body hierarchy][%o]', arr_root_node);

    var paramOb = {};
    if (arr_root_node.length > 0) {
      var dataOb = {};

      this.exampleOb = {};
      var description = '';

      paramOb['in'] = 'body';
      paramOb['name'] = 'body';  //--@[초기값설정][overwrite됨?]
      paramOb['description'] = description;
      paramOb['schema'] = {};
      paramOb['x-dataTypeCd'] = 'PRMTYP1010'; // PRMTYP1010(요청 파라미터), PRMTYP1020(응답 파라미터)

      var data = arr_root_node[0];
      var name = data.node['name'];
      var datatype = data.node['datatype'];
      var desc = data.node['desc'];
      var required = data.node['required'];
      var example = ' ';
      //-- ext {
      var x_required     = data.node['required'];
      var x_personalData = data.node['pv_data'];
      var x_fixedValue   = data.node['default'];
      var x_hidden       = data.node['cp'];
      var x_bigo         = data.node['etc'];
      //-- ext }

      //-- ext {
      paramOb['x-required']    = x_required;
      paramOb['x-personalData'] = this.fmt_oas2_data('personalData', x_personalData);
      paramOb['x-fixedValue']  = x_fixedValue;
      paramOb['x-hidden']      = x_hidden;
      paramOb['x-bigo']        = x_bigo;
      //-- ext }

      if ('Array' == datatype) {
        paramOb['name'] = name;
        paramOb['required'] = (required == 'Y');
        paramOb['schema']['type'] = datatype.toLowerCase();
        paramOb['x-example'] = example;

        dataOb[0] = data
        this.jsp_typeArrayFn(dataOb[0], emptyOb);
        paramOb['schema']['items']  = emptyOb[name]['items'];
        paramOb['schema']['description']  = desc;
      }
      else if ('Object' == datatype) {
        dataOb[0] = data;
        if (data.children.length > 0) {   //-- ?? maybe always
          var emptyOb = {};
          this.jsp_typeObject(dataOb[0], emptyOb);
          paramOb['schema']['properties'] = emptyOb['properties'][name]['properties'];
          paramOb['schema']['required']   = emptyOb['properties'][name]['required'];
        }
        paramOb['name'] = name;
        paramOb['required'] = (required == 'Y');
        paramOb['schema']['type'] = datatype.toLowerCase();
        paramOb['schema']['description'] = desc;
        paramOb['x-example'] = ('' + JSON.stringify(this.exampleOb) + '');
      }
      else {
        paramOb['name'] = name;
        paramOb['required'] = (required == 'Y');
        paramOb['x-example'] = example;
        if (datatype.indexOf('(data type)') != -1) {  //-- not in case
          paramOb['schema']['$ref'] = '#/definitions/' + datatype;
          paramOb['x-dataTypeCd'] = 'PRMTYP1040';
        }
        else {
          paramOb['schema']['type'] = datatype.toLowerCase();
          paramOb['schema']['description'] = desc;
          //-- ext {
          paramOb['schema']['x_required']     = x_required;
          paramOb['schema']['x_personalData'] = x_personalData;
          paramOb['schema']['x_fixedValue']   = x_fixedValue;
          paramOb['schema']['x_hidden']       = x_hidden;
          paramOb['schema']['x_bigo']         = x_bigo;
          //-- ext }
        }
      }
    }
    //--@@console.log('[o-o][req body parameter][%o]', paramOb);

    return paramOb;
  }),

  //-- response header
  fn_get_res_header_headers: (function(a_data) {
    var arr_root_node = this.convertToHierarchy(a_data);
    //--@@console.log('[o-o][res header hierarchy][%o]', arr_root_node);

    var o_headers = {};

    for (var n_ii = 0; n_ii < arr_root_node.length; n_ii++) {
      var data = arr_root_node[n_ii];
      var name = data.node['name'];
      var datatype = data.node['datatype'];
      var desc = data.node['desc'];
      var required = data.node['required'];
      var example = ' ';

      //-- ext {
      var x_required     = data.node['required'];
      var x_personalData = data.node['pv_data'];
      var x_fixedValue   = data.node['default'];
      var x_hidden       = data.node['cp'];
      var x_bigo         = data.node['etc'];
      //-- ext }

      var paramOb = {};
      paramOb = {};
      paramOb['description'] = desc;
      paramOb['x-example'] = example;
      paramOb['x-dataTypeCd'] = 'PRMTYP1020'; // PRMTYP1010(요청 파라미터), PRMTYP1020(응답 파라미터)
      //-- ext {
      paramOb['x-required']    = x_required;
      paramOb['x-personalData'] = this.fmt_oas2_data('personalData', x_personalData);
      paramOb['x-fixedValue']  = x_fixedValue;
      paramOb['x-hidden']      = x_hidden;
      paramOb['x-bigo']        = x_bigo;
      //-- ext }

      if ('Array' == datatype) {
        var emptyOb = {};
        this.jsp_typeArrayFn(data, emptyOb);
        paramOb['type']  = emptyOb[name]['type'];
        paramOb['items'] = emptyOb[name]['items'];
      }
      else {
        paramOb['type'] = datatype.toLowerCase();
      }
      o_headers[name] = paramOb;
    }
    //--@@console.log('[o-o][res header headers][%o]', o_headers);

    return o_headers;
  }),

  //-- response body
  fn_get_res_body_schema: (function(a_data) {
    var arr_root_node = this.convertToHierarchy(a_data);
    //--@@console.log('[o-o][res body hierarchy][%o]', arr_root_node);

    var dataOb = {};

    if (arr_root_node.length > 0) {
      //-- just 1 parameter
      //--##for (var n_ii = 0; n_ii < arr_root_node.length; n_ii++) {
      this.exampleOb = {};
      var description = '';

      var paramOb = {};
      paramOb['x-description'] = description;
      paramOb['x-dataTypeCd'] = 'PRMTYP1020'; // PRMTYP1010(요청 파라미터), PRMTYP1020(응답 파라미터)

      var data = arr_root_node[0];
      var name = data.node['name'];
      var datatype = data.node['datatype'];
      var desc = data.node['desc'];
      var required = data.node['required'];
      var example = ' ';
      //-- ext {
      var x_required     = data.node['required'];
      var x_personalData = data.node['pv_data'];
      var x_fixedValue   = data.node['default'];
      var x_hidden       = data.node['cp'];
      var x_bigo         = data.node['etc'];
      //-- ext }

      //-- ext {
      paramOb['x-required']    = x_required;
      paramOb['x-personalData'] = this.fmt_oas2_data('personalData', x_personalData);
      paramOb['x-fixedValue']  = x_fixedValue;
      paramOb['x-hidden']      = x_hidden;
      paramOb['x-bigo']        = x_bigo;
      //-- ext }

      if ('Array' == datatype) {
        dataOb[0] = data;
        var emptyOb = {};
        this.jsp_typeArrayFn(dataOb[0], emptyOb);

        paramOb['type'] = datatype.toLowerCase();
        paramOb['items'] = emptyOb[name]['items'];
        paramOb['description']  = desc;
        paramOb['x-name'] = name;
        paramOb['example'] = ('' + example + '');
      }
      else if ('Object' == datatype) {
        dataOb[0] = data;
        if (data.children.length > 0) {   //-- ?? maybe always
          var emptyOb = {};
          this.jsp_typeObject(dataOb[0], emptyOb);
          paramOb = emptyOb;
        }
        paramOb['type'] = datatype.toLowerCase();
        paramOb['description'] = desc;
        paramOb['x-name'] = name;
        paramOb['example'] = ('' + JSON.stringify(this.exampleOb) + '');
      }
      else {
        paramOb['example'] = example;
        paramOb['description'] = desc;
        paramOb['x-name'] = name;
        if (datatype.indexOf('(data type)') != -1) {  //-- not in case
          paramOb['schema']['$ref'] = '#/definitions/' + datatype;
          paramOb['x-dataTypeCd'] = 'PRMTYP1040';
        }
        else {
          paramOb['type'] = datatype.toLowerCase();
        }
      }
      //--##}
    } //-- if (arr_root_node.length > 0) {
    //--@@console.log('[o-o][res body schema][%o]', paramOb);

    return paramOb;
  }),

  jsp_typeObject: (function(data, object) {
    //-- custom logic {
    var name = 'dummy_root';
    var desc = 'dummy root';
    var datatype = 'Object';

    data = this.getNodeObject(this.makeNode(name, datatype), [data], null); //-- 가상 root를 작성
    //-- custom logic }

    object['properties'] = {};
    object['required'] = [];

    //-- org코드의 구조는 실상은 1번만 호출되는 내용이 되는듯

    this.exampleOb = {};
    //--##for (var n_ii = 0; n_ii < data.children.length; n_ii++) {
    for (var n_ii = data.children.length - 1; n_ii >= 0; n_ii--) {
      this.jsp_typeObjectTwo(
        data.children[n_ii],
        object['properties'],
        this.exampleOb,
        object['required']
      );
    }
  }),

  /*--
    {
      node: { id, pid, name, datatype, desc, required, size, default, etc, pv_data, cp },
      children:[],
      parent
    }
  --*/
  jsp_typeObjectTwo: (function(data, object, exOb, requriedArray) {
    var name = data.node['name'];
    var datatype = data.node['datatype'];
    var desc = data.node['desc'];
    var required = data.node['required'];
    var example = ' ';
    //-- ext {
    var x_required     = data.node['required'];
    var x_personalData = data.node['pv_data'];
    var x_fixedValue   = data.node['default'];
    var x_hidden       = data.node['cp'];
    var x_bigo         = data.node['etc'];
    //-- ext }

    object[name] = (object[name]||{});

    if ('Object' == datatype) {
      object[name] = {};
      object[name]['type'] = datatype.toLowerCase();
      object[name]['description'] = desc;
      object[name]['properties'] = {};
      if (example != undefined) {
        object[name]['x-example'] = example;
      }
      if (required == 'Y') {
        requriedArray.push(name);
      }
      exOb[name] = {};

      object[name]['required'] = [];
      //-- ext {
      object[name]['x-required']     = x_required;
      object[name]['x-personalData'] = this.fmt_oas2_data('personalData', x_personalData);
      object[name]['x-fixedValue']   = x_fixedValue;
      object[name]['x-hidden']       = x_hidden;
      object[name]['x-bigo']         = x_bigo;
      //-- ext }
      
      //--###for (var n_ii = 0; n_ii < data.children.length; n_ii++) {
      for (var n_ii = data.children.length - 1; n_ii >= 0; n_ii--) {
        this.jsp_typeObjectTwo(
          data.children[n_ii],
          object[name]['properties'],
          exOb[name],
          object[name]['required']
        );
      }
    }
    else if ('Array' == datatype) {
      this.exampleArrayStr = '';
      this.jsp_typeArrayFn(data, object);
      object[name]['description'] = desc;
      if (example != undefined) {
        object[name]['x-example'] = example;
      }
      if (required == 'Y') {
        requriedArray.push(name);
      }
      exOb[name] = this.exampleArrayStr;
      //-- ext {
      object[name]['x-required']     = x_required;
      object[name]['x-personalData'] = this.fmt_oas2_data('personalData', x_personalData);
      object[name]['x-fixedValue']   = x_fixedValue;
      object[name]['x-hidden']       = x_hidden;
      object[name]['x-bigo']         = x_bigo;
      //-- ext }
    }
    else {
      object[name] = {};

      exOb[name] = example;
      if (datatype.indexOf('(data type)') != -1) {  //-- not in case
        object[name]['$ref'] = '#/definitions/' + datatype;
        paramOb['x-dataTypeCd'] = 'PRMTYP1040';
      }
      else {
        if (required == 'Y') {
          requriedArray.push(name);
        }
        object[name]['type'] = datatype.toLowerCase();
        object[name]['description'] = desc;
        if (example != undefined) {
          object[name]['x-example'] = example;
        }
        //-- ext {
        object[name]['x-required']     = x_required;
        object[name]['x-personalData'] = this.fmt_oas2_data('personalData', x_personalData);
        object[name]['x-fixedValue']   = x_fixedValue;
        object[name]['x-hidden']       = x_hidden;
        object[name]['x-bigo']         = x_bigo;
        //-- ext }
      }
    }
    return object;
  }),

  jsp_typeArrayFn: (function(data, object){
    var typeArray = {};
    var name = data.node['name'];
    var datatype = data.node['datatype'];
    var desc = data.node['desc'];
    var required = data.node['required'];
    var example = ' ';

    if (example != '') {
      this.exampleArrayStr = example;
    }

    //--##var is_parent_array_root = ((data.parent != null) ? (('Array' == data.parent.node['datatype']) && (data.parent.children.length == 1)) : false);
    //--##if (is_parent_array_root == false) {  //-- case가 없어보임
    if (name == '') { //-- (name == '') case가 없어보임
      object['items'] = {};
      typeArray = object['items'];
    }
    else {
      object[name] = {};
      typeArray = object[name];
    }

    let is_loop = (data.children.length > 0);
    while (true == is_loop) {
    //--@@while (data.children.length > 0) {
      name = data.node['name'];
      datatype = data.node['datatype'];
      desc = data.node['desc'];
      required = data.node['required'];
      //-- ext {
      var x_required     = data.node['required'];
      var x_personalData = data.node['pv_data'];
      var x_fixedValue   = data.node['default'];
      var x_hidden       = data.node['cp'];
      var x_bigo         = data.node['etc'];
      //-- ext }
      //-- ext {
      typeArray['x-required']    = x_required;
      typeArray['x-personalData'] = this.fmt_oas2_data('personalData', x_personalData);
      typeArray['x-fixedValue']  = x_fixedValue;
      typeArray['x-hidden']      = x_hidden;
      typeArray['x-bigo']        = x_bigo;
      //-- ext }
      
      if ('Array' == datatype) {
        //--[ref]type = datatype.toLowerCase();
        //--[ref]example = ' ';
        //--[ref]typeArray = this.jsp_typeArrayMake(type, example, typeArray);
        typeArray['type'] = datatype.toLowerCase(); //--@@
        typeArray['items'] = {};
        typeArray = typeArray['items'];

        //-- custom logic {
        if (data.children.length > 1) {
          data.node['datatype'] = 'Object'; //-- change to object node  아래의 data = data.child[0]에 의
        }
        else {
          data = data.children[0];
        }
        //-- custom logic }
        //-- ext {
        typeArray['x-required']    = x_required;
        typeArray['x-personalData'] = this.fmt_oas2_data('personalData', x_personalData);
        typeArray['x-fixedValue']  = x_fixedValue;
        typeArray['x-hidden']      = x_hidden;
        typeArray['x-bigo']        = x_bigo;
        //-- ext }
      }
      else if ('Object' == datatype) {
        typeArray['type'] = datatype.toLowerCase();
        if (example != undefined) {
          typeArray['x-example'] = example;
        }
        typeArray['properties'] = {};
        typeArray['required'] = [];
        //--##for (var n_ii = 0; n_ii < data.children.length; n_ii++) {
        for (var n_ii = data.children.length - 1; n_ii >= 0; n_ii--) {
          this.jsp_typeObjectTwo(
            data.children[n_ii],
            typeArray['properties'],
            this.exampleOb,
            typeArray['required']
          );
        }
        break;
      }
      else {
        typeArray['type'] = datatype.toLowerCase();
        typeArray['x-example'] = example;
        break;
      }
    }
  }),

  jsp_typeArrayMake: (function(type, example, typeArray) {
    typeArray['type'] = type.toLowerCase();
    typeArray['items'] = {};
    return typeArray['items'];
  }),
  //--### build swagger object function (ref: pathReqForm.jsp) {

  //-- node function {
  makeNode: (function(name, datatype) {
    return { 'id':-99, 'pid':-99, 'name': name, 'datatype': datatype, };
  }),

  getNodeObject: (function(node, children, parent) {
    return { 'node': node, 'children': children, 'parent': parent };
  }),
  //-- node function }

  //-- tree function {
  convertToHierarchy: (function(arry) {
    //-- method {
    var createStructure = (function(nodes) {
      var objects = [];
      for (var n_ii = 0; n_ii < nodes.length; n_ii++) { objects.push({ 'node': nodes[n_ii], 'children': [], 'parent': null }); }
      return objects;
    });
    var getParent = (function(child, nodes) {
      for (var n_ii = 0; n_ii < nodes.length; n_ii++) { if (nodes[n_ii].node.id == child.node.pid) { return nodes[n_ii]; } }
      return null;
    });
    //-- method }

    var nodeObjects = createStructure(arry);
    for (var i = nodeObjects.length - 1; i >= 0; i--) {
      var currentNode = nodeObjects[i];
      //-- skip over root node.
      if (currentNode.node.pid == -1) {
        continue;
      }
      var parent = getParent(currentNode, nodeObjects);
      if (parent == null) {
        continue;
      }
      currentNode.parent = parent;
      parent.children.push(currentNode);
      nodeObjects.splice(i, 1);
    }
    //--What remains in nodeObjects will be the root nodes.
    return nodeObjects;
  }),
  //-- tree function }
  
  fmt_oas2_data: (function(cmd, value) {
    var s_ret = '';
    value = value.trim();
    if ('personalData' == cmd) {
      var a_val = ['성명','생년월일','주소','유선전화번호','휴대폰번호','이메일주소','주민번호','운전면허','외국인등록번호','여권번호','계좌번호','신용카드번호','멤버십카드번호','단말정보','위치정보','바이오정보','SA_ID'];
      var a_code = ['PRMPDT1010','PRMPDT1020','PRMPDT1030','PRMPDT1040','PRMPDT1050','PRMPDT1060','PRMPDT1070','PRMPDT1080','PRMPDT1090','PRMPDT1100','PRMPDT1110','PRMPDT1120','PRMPDT1130','PRMPDT1140','PRMPDT1150','PRMPDT1160','PRMPDT1170'];
      var idx = a_val.indexOf(value);
      if (idx != -1) {
        s_ret = a_code[idx];
      }
    }
    return s_ret;
  }),
};  //-- var fn_oas2 = {

export default parse_kos_excel;
