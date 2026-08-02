<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<t:layout type="empty">
<style>
  /* override */
  .pkg_board .table-vw.view_style thead th{padding:15px 10px;border-left:0;}
  .pkg_board .table-vw.view_style tbody th {padding:10px;height:auto;}
  .pkg_board .table-vw.view_style tbody td {padding:10px;}

  /* custom */
  .pkg_board .table-vw.view_style.apiyamltool_table_style {border-top:0; margin-top:0;}
  .pkg_board .table-vw.view_style.apiyamltool_table_style tbody td.align_l {text-align:left;}
  .pkg_board .table-vw.view_style.apiyamltool_table_style [class^="tag_"]{width:50px; height:20px;padding:0;line-height:20px;vertical-align:middle;text-align:center;color:#fff;font-size:11px;font-weight:700;border-radius:4px;display:inline-block;}

  .pkg_board .scroll_box {height: 450px; overflow-y:auto;}
  .pkg_board .yaml_textarea {margin-top:10px; padding:5px; height: 486px; 
    background-color: #1d1f21; color: #A8FF60; dum-color: #96CBFE; font-family: Monaco, Menlo, "Ubuntu Mono", Consolas, source-code-pro, monospace; font-size: 12px; }

  .popup_result_scroll_box {height: 450px; overflow-y:auto;}

  .dp_none {display:none;}
  .cursor_pointer {cursor: pointer;}
  .td_nk { background-color: #ffd700; color:#f00 !important;}
</style>
<div id="popup_apiyamltool" title="ApiYamlTool" style="display:none;">
  <div class="popup_content">
    <div class="pkg_board">

      <table class="table-vw view_style cid_apispc_table">
        <caption>ApiYamlTool Table</caption>
        <colgroup>
          <col style="width: 10%;">
          <col>
          <col style="width: 10%;">
          <col>
          <col style="width: 10%;">
          <col>
          <col style="width: 10%;">
          <col>
        </colgroup>
        <tbody>
          <tr>
            <th scope="row"><div>apiSpcNo</div></th><td></td>
            <th scope="row"><div>apiNm</div></th><td></td>
            <th scope="row"><div>apiSpcId</div></th><td></td>
            <th scope="row"><div>ver</div></th><td></td>
          </tr>
        </tbody>
      </table>

      <div class="dp_none cid_mode cid_mode_table">
	      <table class="table-vw view_style">
	        <caption>ApiYamlTool Table</caption>
	        <colgroup>
	          <col style="width: 15%;">
	          <col style="width: 20%;">
	          <col style="width: auto;">
	          <col style="width: 70px;">
	          <col style="width: 65px;">
	          <col style="width: 15%;">
	        </colgroup>
	        <thead>
	          <tr>
	            <th scope="col"><div class="cursor_pointer" onclick="popymlt_fn_onclick_rebuild_tags()">카테고리</div></th>
	            <th scope="col"><div>API</div></th>
	            <th scope="col"><div>Path</div></th>
	            <th scope="col"><div>ApiNo</div></th>
	            <th scope="col"><div class="cursor_pointer" onclick="popymlt_fn_paths_check(g_o_yaml)">paths</div></th>
	            <th scope="col"><div class="cursor_pointer" onclick="popymlt_fn_onclick_apiNo_check()">ApiNo Check</div></th>
	          </tr>
	        </thead>
	      </table>
	      <div class="scroll_box">
		      <table class="table-vw view_style apiyamltool_table_style cid_xcategory_table">
		        <caption>ApiYamlTool Table</caption>
		        <colgroup>
		          <col style="width: 15%;">
		          <col style="width: 20%;">
		          <col style="width: auto;">
		          <col style="width: 70px;">
		          <col style="width: 65px;">
	            <col style="width: 15%;">
		        </colgroup>
		        <tbody>
		          <!--// [ref] <tr><td>메시징 서비스</td><td>MessageSendSMSReportNoNetCharge</td><td class="align_l"><span class="tag_post">post</span>/MessageSendSMSReportNoNetCharge</td><td>12345</td></tr> //-->
		        </tbody>
		      </table>
        </div><!-- .scroll_box -->
      </div><!-- .cid_mode_table -->
      
      <div class="dp_none cid_mode cid_mode_yaml">
        <textarea class="yaml_textarea cid_yaml_textarea"></textarea>
      </div><!-- .cid_mode_yaml -->

    </div><!-- .pkg_board -->

    <div class="lPop_bottom brd_tp">
      <button type="button" title="확인" class="btn btn_black btn_sml3 cid_btn_close">확인</button>

      <button type="button" title="Yaml" class="btn btn_sml3 dp_none cid_mode cid_mode_table cid_btn_yaml">Yaml보기</button>
      <button type="button" title="Table" class="btn btn_sml3 dp_none cid_mode cid_mode_yaml cid_btn_table">목록보기</button>

      <button type="button" title="Yaml저장" class="btn btn_black btn_sml3 cid_btn_save">Yaml저장</button>

    </div><!-- .lPop_bottom -->
  </div><!-- .popup_content -->
</div><!-- #popup_apiyamltool -->
<script>
  var g_o_yamlInfo;
  var g_o_yaml;
  var g_a_req_api = []; //-- table출력 api info {apiNo, path, method}
  var g_n_res_api_cnt = -1;  //-- api check count

  $(document).ready(function() {
    $('#popup_apiyamltool').dialog({ autoOpen: false, width: 1000, modal: true, resizable: true });
    $('#popup_apiyamltool .cid_btn_close').on('click', function(p_evt) {
      p_evt.preventDefault();
      $('#popup_apiyamltool').dialog('close');
    });
    $('#popup_apiyamltool .cid_btn_yaml, #popup_apiyamltool .cid_btn_table').on('click', function(p_evt) {
      p_evt.preventDefault();
      popymlt_fn_ui_setmode($(this).hasClass('cid_btn_yaml') ? 'yaml' : 'table');
    });
    $('#popup_apiyamltool .cid_btn_save').on('click', function(p_evt) {
      p_evt.preventDefault();
      fn_save_yaml();
    });
  });

  //-- yaml <-> table mode UI 전환
  function popymlt_fn_ui_setmode(p_mode) {
    if ('table' == p_mode) {
      var b_ret = fn_proc_yaml_parse($('.cid_yaml_textarea').val());
    }

    $('.cid_mode').hide();
    $((p_mode == 'yaml') ? '.cid_mode_yaml' : '.cid_mode_table').show();
  }

  //-- apiSpc정보 table에 출력
  function popymlt_fn_disp_apispc_table(apiSpcInfo) {
    apiSpcInfo = (apiSpcInfo||{});

    var jq_td_api_spc = $('.cid_apispc_table tr td');

    jq_td_api_spc.eq(0).html(apiSpcInfo.apiSpcNo);
    jq_td_api_spc.eq(1).html(apiSpcInfo.apiNm);
    jq_td_api_spc.eq(2).html(apiSpcInfo.apiSpcId);
    jq_td_api_spc.eq(3).html(apiSpcInfo.ver);
  }

  //-- yaml x-category 목록을 table에 출력
  function popymlt_fn_disp_xcatagory_table(o_yaml) {
    o_yaml = (o_yaml||{});

    g_a_req_api = [];
    var jq_tbody = $('.cid_xcategory_table tbody');
    jq_tbody.empty();

    if (false == o_yaml.hasOwnProperty('x-category')) { alert_message('x-category not found'); return; }
    if (false == o_yaml.hasOwnProperty('paths')) { alert_message('paths not found'); return; }

    var o_paths = o_yaml['paths'];
    var o_category = o_yaml['x-category'];
    $.each(o_category, function(p_key, p_item) {
      var category_key = p_key;
      var category_item = p_item;
      $.each(category_item, function(p_key, p_item) {
        var api_path_key = p_key;
        var api_path_item = p_item;
        $.each(api_path_item, function(p_key, p_item) {
          var api_method_key = p_key;
          var api_method_item = p_item;

          var apiNm = (api_method_item['apiNm']||'');
          var apiNo = (api_method_item['apiNo']||'');

          var b_is_path_exist = o_paths.hasOwnProperty(api_path_key);
          var b_is_method_exist = false;
          var b_is_xcategory_exist = false;
          var b_is_xapino_exist = false;
          var b_is_tags_exist = false
          var x_category = '';
          var x_apiNo = '';
          var tags = [];

          var dp_path_info = '';
          var a_path_info = [];

          if (b_is_path_exist) {
            b_is_method_exist = o_paths[api_path_key].hasOwnProperty(api_method_key);
          }
          else {
            a_path_info.push('[path: X][' + api_path_key + ']');
          }
          if (b_is_method_exist) {
            b_is_xcategory_exist = o_paths[api_path_key][api_method_key].hasOwnProperty('x-category');
            b_is_xapino_exist = o_paths[api_path_key][api_method_key].hasOwnProperty('x-apiNo');
            b_is_tags_exist = o_paths[api_path_key][api_method_key].hasOwnProperty('tags'); 
          }
          else {
            a_path_info.push('[method: X][' + api_path_key + '][' + api_method_key + ']');
          }

          if (b_is_xcategory_exist) {
            x_category = o_paths[api_path_key][api_method_key]['x-category'];
          }
          else {
            a_path_info.push('[x-category: X][' + api_path_key + '][' + api_method_key + ']');
          }
          if (b_is_xapino_exist) {
            x_apiNo = o_paths[api_path_key][api_method_key]['x-apiNo'];
          }
          else {
            a_path_info.push('[x-apiNo: X][' + api_path_key + '][' + api_method_key + ']');
          }
          if (b_is_tags_exist) {
            tags = (o_paths[api_path_key][api_method_key]['tags']||[]);
          }
          else {
            a_path_info.push('[tags: X][' + api_path_key + '][' + api_method_key + ']');
          }

          if (category_key != x_category) {
            a_path_info.push('[category != x-category][x-category: ' + x_category + ']');
          }
          if (apiNo != x_apiNo) {
            a_path_info.push('[apiNo != x-apiNo][x-apiNo: ' + x_apiNo + ']');
          }
          if (tags.length == 1) {
	          if (category_key != tags[0]) {
	            a_path_info.push('[category != tags[0]][tags[0]: ' + tags[0] + ']');
	          }
          }
          else {
            a_path_info.push('[tags.length: ' + tags.length + ']' );
          }

          if (a_path_info.length == 0) {
            dp_path_info = 'OK';
          }
          else {
            dp_path_info = 'NK';
          }

          /*--
          //-- [for test]
          a_path_info.push('[paths: X]');
          a_path_info.push('[path: X][' + api_path_key + ']');
          a_path_info.push('[method: X][' + api_path_key + '][' + api_method_key + ']');
          a_path_info.push('[x-apiNo: X][' + api_path_key + '][' + api_method_key + ']');
          a_path_info.push('[apiNo != x-apiNo]');
          */

          var s_tr = '<tr>';
          s_tr += '<td>' + category_key + '</td>';
          s_tr += '<td>' + apiNm + '</td>';
          s_tr += '<td class="align_l"><span class="tag_' + api_method_key + '">' + api_method_key + '</span>&nbsp;' + api_path_key + '</td>';
          s_tr += '<td>' + apiNo + '</td>';
          if (dp_path_info == 'OK') {
            s_tr += '<td>' + dp_path_info + '</td>';
          }
          else {
            s_tr += '<td class="td_nk"><span class="cursor_pointer" style="width:100%; height:100%;" onclick="alert_message($(this).closest(\'tr\').data(\'a_path_info\').join(\'\\n\'))">' + dp_path_info + '</span></td>';
          }
          s_tr += '<td></td>';
          s_tr += '</tr>';
          var jq_tr = $(s_tr);
          jq_tr.data('a_path_info', a_path_info);

          var def_api = {
            apiNo: apiNo,
            path: api_path_key,
            method: api_method_key,
            category: category_key
          };
          g_a_req_api.push(def_api);
          jq_tbody.append(jq_tr);
        });
      });
    });
  }
  
  //-- yaml paths목록을 x-category와 check
  function popymlt_fn_paths_check(o_yaml) {
    o_yaml = (o_yaml||{});

    if (false == o_yaml.hasOwnProperty('x-category')) { alert_message('x-category not found'); return; }
    if (false == o_yaml.hasOwnProperty('paths')) { alert_message('paths not found'); return; }

    var a_path_err_info = [];
    
    var o_category = o_yaml['x-category'];
    var o_paths = o_yaml['paths'];
    if (Object.keys(o_paths).length == 0) {
      a_path_err_info.push('[paths length 0]');
    }
    $.each(o_paths, function(p_key, p_item) {
      var api_path_key = p_key;
      var api_path_item = p_item;
      if (Object.keys(api_path_item).length == 0) {
        a_path_err_info.push('[path: ' + api_path_key + '][method length 0]');
      }
      $.each(api_path_item, function(p_key, p_item) {
        var api_method_key = p_key;
        var api_method_item = p_item;
        
        var path_info = '=== [path: ' + api_path_key + '][method: ' + api_method_key + ']';

        var x_category = (api_method_item['x-category']||'');
        var x_apiNo = (api_method_item['x-apiNo']||'');
        var summary = (api_method_item['summary']||'');
        var tags = (api_method_item['tags']||[]);

        var b_is_xcategory_item_exist = (x_category.length > 0);
        var b_is_xcategory_exist = false;
        var b_is_path_exist = false;
        var b_is_method_exist = false;
        var b_is_apino_exist = false;
        var b_is_apinm_exist = false;
        var apiNo = '';
        var apiNm = '';
        
        var a_cate_info = [];

        if (b_is_xcategory_item_exist) {
          b_is_xcategory_exist = o_category.hasOwnProperty(x_category);
        }
        else {
          a_cate_info.push('[x-category item: X]');
        }

        if (b_is_xcategory_exist) {
          b_is_path_exist = o_category[x_category].hasOwnProperty(api_path_key);
        }
        else {
          a_cate_info.push('[x-category: X][x-category: ' + x_category + ']');
        }
        if (b_is_path_exist) {
          b_is_method_exist = o_category[x_category][api_path_key].hasOwnProperty(api_method_key);
        }
        else {
          a_cate_info.push('[path: X][x-category: ' + x_category + ']');
        }
        if (b_is_method_exist) {
          b_is_apino_exist = o_category[x_category][api_path_key][api_method_key].hasOwnProperty('apiNo');
          b_is_apinm_exist = o_category[x_category][api_path_key][api_method_key].hasOwnProperty('apiNm');
        }
        else {
          a_cate_info.push('[method: X][path: ' + api_path_key + ']');
        }
        if (b_is_apino_exist) {
          apiNo = o_category[x_category][api_path_key][api_method_key]['apiNo'];
          if (apiNo != x_apiNo) {
            a_cate_info.push('[apiNo != x-apiNo][x-apiNo: ' + x_apiNo + ']');
          }
        }
        else {
          a_cate_info.push('[apiNo: X]');
        }
        if (b_is_apinm_exist) {
          apiNm = o_category[x_category][api_path_key][api_method_key]['apiNm'];
          if (apiNm != summary) {
            a_cate_info.push('[apiNm != summary][summary: ' + summary + ']');
          }
        }
        else {
          a_cate_info.push('[apiNm: X]');
        }
        if (tags.length == 1) {
          if (tags[0] != x_category) {
            a_cate_info.push('[tags[0] != x-category][tags[0]: ' + tags[0] + ']');
          }
        }
        else {
          a_cate_info.push('[tags.length: ' + tags.length + ']');
        }

        //-- [for test]
        /*--
          a_cate_info.push('[x-category item: X]');
          a_cate_info.push('[x-category: X][x-category: ' + x_category + ']');
          a_cate_info.push('[path: X][x-category: ' + x_category + ']');
          a_cate_info.push('[method: X][path: ' + api_path_key + ']');
          a_cate_info.push('[apiNo != x-apiNo][x-apiNo: ' + x_apiNo + ']');
          a_cate_info.push('[apiNo: X]');
          a_cate_info.push('[apiNm != summary][summary: ' + summary + ']');
          a_cate_info.push('[apiNm: X]');
          apiNo = '';
        --*/
        if (a_cate_info.length > 0) {
          a_path_err_info.push(path_info + '\n' + a_cate_info.join('\n'));
        }
      });
    });

    var s_result = '';
    var s_title = 'paths Check 결과';
    var s_result_option = {};
    if (a_path_err_info.length == 0) {
      s_result = 'no paths error';
    }
    else {
      s_result = '<div class="popup_result_scroll_box">' + a_path_err_info.join('\n---\n\n') + '</div>';
      s_result_option = {width:600};
    }
    
    if (s_result.length > 0) {
      alert_message(s_result, s_title, s_result_option);
    }
  }

	//-- tags 항목 재구성 / x-category순정렬
	//-- same @apiGlobalScript.js
	function popymlt_fn_rebuild_tags(o_yaml) {
	  var category = o_yaml['x-category'];
	  var arr_tags = [];
	  var renew_paths = {};
	
	  if (o_yaml.hasOwnProperty('paths')) {  //-- 'paths'존재시
	    $.each(category, function(p_key, p_item) {
	      var category_key = p_key;
	      var category_item = p_item;
	      $.each(category_item, function(p_key, p_item) {
	        var api_path_key = p_key;
	        var api_path_item = p_item;
	        if (o_yaml['paths'].hasOwnProperty(api_path_key)) {  //-- api path 존재시
	          $.each(api_path_item, function(p_key, p_item) {
	            var api_method_key = p_key;
	            var api_method_item = p_item;
	            if (o_yaml['paths'][api_path_key].hasOwnProperty(api_method_key)) {  //-- api path + method 존재시
	              o_yaml['paths'][api_path_key][api_method_key]['tags'] = [category_key];
	              renew_paths[api_path_key] = (renew_paths[api_path_key]||{});
	              renew_paths[api_path_key][api_method_key] = o_yaml['paths'][api_path_key][api_method_key];
	            }
	          });
	        }
	      });
	      arr_tags.push({'name': category_key});
	    });
	  }
	  o_yaml['paths'] = renew_paths;
	  o_yaml['tags'] = arr_tags;
	}

  function popymlt_fn_onclick_rebuild_tags() {
    if (!confirm('tags를 재구성 하시겠습니까?')) { return; }

    //-- @apiGlobalScript.js
    popymlt_fn_rebuild_tags(g_o_yaml);
    var b_ret = fn_proc_yaml_stringify(g_o_yaml);
    if (true == b_ret) {
      b_ret = fn_proc_yaml_parse($('.cid_yaml_textarea').val());
    }
    if (true == b_ret) {
      alert_message('tags를 재구성 하였습니다.');
    }
  }

  function popymlt_fn_onclick_apiNo_check() {
    window.setTimeout(popymlt_fn_apiNo_check, 100);
  }
  
  //-- table의 apiNo의 유효성을 check
  function popymlt_fn_apiNo_check() {
    if ((g_o_yamlInfo.apiSpcNo||'').length == 0) { alert_message('apiScpNo 정보가 없습니다'); return; }
    if (g_a_req_api.length == 0) { alert_message('대상 정보가 없습니다'); return; }
    if ((g_n_res_api_cnt >= 0) && (g_n_res_api_cnt < g_a_req_api.length)) { alert_message('점검이 진행중 입니다.'); return; }

    if (!confirm(g_a_req_api.length + '개의 api에 대해 점검을 수행 하시겠습니까?')) { return; }

    g_n_res_api_cnt = 0;
    var apiSpcNo = g_o_yamlInfo.apiSpcNo;
    var jq_tr = $('.cid_xcategory_table tbody tr');
    $.each(g_a_req_api, function(p_idx, p_item) {
      var jq_td = jq_tr.eq(p_idx).find('td').last();
      popymlt_fn_proc_apiNo_check(p_idx, p_item, apiSpcNo, jq_td);
    });
  }
  
  //-- apiNo의 유효성을 check
  function popymlt_fn_proc_apiNo_check(p_idx, p_def_api, p_apiSpcNo, p_jq_td) {
    p_jq_td.html('ing...').removeClass('td_nk');

    var fn_cb_succ = function(p_idx, p_jq_td, data) {
      var s_result = '';
      var b_is_err = false;
      
      var path = '';
      var method = '';
      var max_apino = '';

      if (false == b_is_err) {
	      if (false == data.hasOwnProperty('path')) {
	        s_result = ('invalid path');
	        b_is_err = true;
	      }
	      else {
	        path = data['path'];
	      }
      }
      if (false == b_is_err) {
        if (false == data.hasOwnProperty('method')) {
          s_result = ('invalid method');
          b_is_err = true;
        }
        else {
          method = data['method'];
        }
      }
      if (false == b_is_err) {
        if (false == data.hasOwnProperty('max_apino')) {
          s_result = ('invalid max_apino');
          b_is_err = true;
        }
        else {
          max_apino = data['max_apino'];
        }
      }

      if (false == b_is_err) {
	      if ((p_def_api.path == path) && (p_def_api.method == method)) {
	        s_result = 'OK';
	      }
	      else {
	        s_result = 'NK';
          b_is_err = true;
	      }
        s_result = s_result + '<br>[max_apino: ' + '<span class="cursor_pointer" ondblclick="fn_set_apino(' + p_idx + ', ' + max_apino + ')">' + max_apino + '</span>' + ']';
      }
      
      if (true == b_is_err) {
        p_jq_td.addClass('td_nk');
      }
      p_jq_td.html(s_result);

      fn_cb_res();
    };
    var fn_cb_fail = function(p_idx, p_jq_td, status, error) {
      p_jq_td.html(error).addClass('td_nk');

      fn_cb_res();
    };
    var fn_cb_res = function() {
      g_n_res_api_cnt++;
      if (g_n_res_api_cnt >= g_a_req_api.length) {
        alert_message(g_n_res_api_cnt + '건의 정보 점검이 완료 되었습니다.');
      }
    }

    var param = { 
      'apiSpcNo': p_apiSpcNo,
      'apiNo': p_def_api.apiNo,
      'path': p_def_api.path,
      'method': p_def_api.method,
    };
    $.ajax({
      url: '<c:url value="/api/reg/apiNoCheck.do"/>', type: 'POST', data: param, async: true, cache: false,
      success: function(data) {
        //-- data.yamlInfo
        if (typeof(fn_cb_succ) == 'function') { fn_cb_succ(p_idx, p_jq_td, data); }
      },
      error: function(request, status, error) {
        if (typeof(fn_cb_fail) == 'function') { fn_cb_fail(p_idx, p_jq_td, status, error); }
        else { err_message(status, error); }
      }
    });
  }

  //-- dialog 초기화
  function popymlt_fn_init_dialog() {
    g_o_yamlInfo = undefined;
    g_o_yaml = undefined;
    g_a_req_api = [];
    g_n_res_api_cnt = -1;

    $('.cid_apispc_table tr td').each(function() { $(this).html(''); });
    $('.cid_xcategory_table tbody').empty();
    $('.cid_yaml_textarea').val('');
    popymlt_fn_ui_setmode('init');
  }
  
  //-- api spc 조회
  function popymlt_fn_ajax_selApiInfo(apiSpcNo, fn_cb_succ, fn_cb_fail) {
    var param = { 'apiSpcNo': apiSpcNo };
    $.ajax({
      url: '<c:url value="/api/reg/savApiYamlAjax.do"/>', type: 'POST', data: param, async: true, cache: false,
      success: function(data) {
        //-- data.yamlInfo
        if (typeof(fn_cb_succ) == 'function') { fn_cb_succ(data); }
      },
      error: function(request, status, error) {
        if (typeof(fn_cb_fail) == 'function') { fn_cb_fail(status, error); }
        else { err_message(status, error); }
      }
    });
  }
  
  //-- apino설정
  function fn_set_apino(p_idx, p_apino) {
    //-- { apiNo, path, method }
    if (false == ((p_idx >= 0) && (p_idx < g_a_req_api.length))) { alert_message('invalid index - [idx: ' + p_idx + ']'); return; }
    var new_apino = ((parseInt(p_apino) > 0) ? parseInt(p_apino) : -1);
    if (new_apino == -1) { alert_message('invalid apino - [apino: ' + p_apino + ']'); return; }
    
    var def_api = (g_a_req_api[p_idx]||{});
    if (false == def_api.hasOwnProperty('apiNo')) { alert_message('def_api apiNo missing'); return; }
    if (false == def_api.hasOwnProperty('path')) { alert_message('def_api path missing'); return; }
    if (false == def_api.hasOwnProperty('method')) { alert_message('def_api method missing'); return; }
    if (false == def_api.hasOwnProperty('category')) { alert_message('def_api category missing'); return; }

    var apiNo = ((parseInt(def_api['apiNo']) > 0) ? parseInt(def_api['apiNo']) : -1);
    var path = (def_api['path']||'');
    var method = (def_api['method']||'');
    var category = (def_api['category']||'');

    if (apiNo == -1) { alert_message('invalid def_api apino - [apino: ' + def_api['apiNo'] + ']'); return; }
    if (path.length == 0) { alert_message('invalid def_api path - [path: ' + def_api['path'] + ']'); return; }
    if (method.length == 0) { alert_message('invalid def_api method - [method: ' + def_api['method'] + ']'); return; }
    if (category.length == 0) { alert_message('invalid def_api category - [category: ' + def_api['category'] + ']'); return; }

    if (new_apino == apiNo) { alert_message('apino is same'); return; }

    var o_yaml = g_o_yaml;
    if (false == o_yaml.hasOwnProperty('x-category')) { alert_message('x-category not found'); return; }
    if (false == o_yaml.hasOwnProperty('paths')) { alert_message('paths not found'); return; }

    var o_paths = o_yaml['paths'];
    var o_category = o_yaml['x-category'];
    
    o_paths[path][method]['x-apiNo'] = new_apino;
    o_category[category][path][method]['apiNo'] = new_apino; 
    def_api['apiNo'] = new_apino;

    var b_ret = fn_proc_yaml_stringify(o_yaml);
    if (true == b_ret) {
      var jq_tr = $('.cid_xcategory_table tbody tr').eq(p_idx);
      jq_tr.find('td').removeClass('td_nk');
      var jq_td_apino = jq_tr.find('td').eq(3);
      var jq_td_path = jq_tr.find('td').eq(4);
      var jq_td_apino_check = jq_tr.find('td').eq(5);

      jq_td_apino.html(new_apino);
      jq_td_path.html('-');
      jq_td_apino_check.html('SET' + '<br>[max_apino: ' + new_apino + ']');
    }
  }

  //-- yaml string -> object // table출력, textarea출력
  function fn_proc_yaml_parse(p_yaml) {
    var b_is_err = false;
    try {
      g_o_yaml = SwaggerParser.YAML.parse(p_yaml);
    }
    catch (e) {
      b_is_err = true;
      alert_message('YAML parse error\n\n' + e);
    }
    if (false == b_is_err) {
      popymlt_fn_disp_xcatagory_table(g_o_yaml);
      b_is_err = (false == fn_proc_yaml_stringify(g_o_yaml));
    }
    return (false == b_is_err);
  }

  //-- yaml object -> string // textarea출력
  function fn_proc_yaml_stringify(p_o_yaml) {
    var b_is_err = false;
    try {
      var s_yaml = SwaggerParser.YAML.stringify(p_o_yaml);
      $('.cid_yaml_textarea').val(s_yaml);
    }
    catch (e) {
      var b_is_err = true;
      alert_message('YAML stringify error\n\n' + e);
    }
    return (false == b_is_err);
  }
  
  //-- yaml 저장
  function fn_save_yaml() {
    if ((g_o_yamlInfo.apiSpcNo||'').length == 0) { alert_message('apiScpNo 정보가 없습니다'); return; }

    var apiSpcNo = g_o_yamlInfo.apiSpcNo;
    var yamlSbst = $('.cid_yaml_textarea').val();
    var b_ret = fn_proc_yaml_parse(yamlSbst);
    if (false == b_ret) { return; }

    /*--[ref][add key]
    var o_yaml;
    try {
      o_yaml = SwaggerParser.YAML.parse(yamlSbst);
      o_yaml['x-last-modified'] = new Date();
      yamlSbst = SwaggerParser.YAML.stringify(o_yaml);
    }
    catch (e) {
      alert_message('add x-last-modified error\n\n' + e);
      return;
    }
    --*/

    if (!confirm('yaml정보를 저장 하시겠습니까?')) { return; }

    var fn_cb_succ = function(data) {
      //-- data.result, data.apiSpcNo, data.yamlSbst
      alert_message(data.result);
    };
    var fn_cb_fail = function(status, error) {
      alert_message('[status: ' + status + '][error: ' + error + ']');
    };
    
    var param = { 'apiSpcNo': apiSpcNo, 'yamlSbst': yamlSbst };
    $.ajax({
      url: '<c:url value="/api/reg/saveYamlFileAjax.do"/>', type: 'POST', data: param, async: false, cache: false,
      success: function(data) {
        if (typeof(fn_cb_succ) == 'function') { fn_cb_succ(data); }
      },
      error: function(request, status, error) {
        if (typeof(fn_cb_fail) == 'function') { fn_cb_fail(status, error); }
        else { err_message(status, error); }
      }
    });
  }

  //-- public call { 
  function fn_popApiYamlTool(apiSpcNo) {
    var fn_cb_succ = function(data) {
      if (false == data.hasOwnProperty('yamlInfo')) {
        alert_message('invalid selApiInfo');
        return;
      }

      g_o_yamlInfo = data['yamlInfo'];
      popymlt_fn_disp_apispc_table(g_o_yamlInfo);

      var b_ret = fn_proc_yaml_parse(g_o_yamlInfo['yamlSbst']);
      $('#popup_apiyamltool').dialog('open');
    };
    var fn_cb_fail;

    //-- init
    popymlt_fn_init_dialog();
    popymlt_fn_ajax_selApiInfo(apiSpcNo, fn_cb_succ, fn_cb_fail);
  }
  //-- public call } 
</script>
</t:layout>