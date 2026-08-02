<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:set var="b_use_highchars" value="true" />
<c:set var="b_use_highchars_xls_down" value="false" />

<script src="<c:url value="/resources/js/cdn/moment.min.js" />"></script>
<c:if test="${b_use_highchars}">
  <script src="<c:url value="/resources/js/cdn/highcharts/highcharts.js" />"></script>
</c:if>
<c:if test="${b_use_highchars_xls_down}">
  <script src="<c:url value="/resources/js/cdn/highcharts/modules/exporting.js" />"></script>
  <script src="<c:url value="/resources/js/cdn/highcharts/modules/export-data.js" />"></script>
</c:if>


<%-- [tag:PRJ-20220901] --%>
<!--// apiTrafficGraph_inc.jsp -->

<div class="cid_apitrafficgraph_wrap">
  <div class="sub_tit_area">
    <div class="left_area">
      <h2 class="stit">API Traffic</h2> 
    </div>
    <div class="right_area">
      <div class="rdo_area">
        <span class="rdo_btn">
          <input type="radio" class="cit_rdo_topn" id="id_rdo_1" name="rdo_topn" disabled>
          <label for="id_rdo_1">TOP 5</label>
        </span>
      </div>
    </div>

    <!--  [ref]
    <div class="right_area">
      <div class="rdo_area">
        <span class="rdo_btn"><input type="radio" id="rdo01" name="rdo" checked=""><label for="rdo01">최근 일주일</label></span>
        <span class="rdo_btn"><input type="radio" id="rdo02" name="rdo"><label for="rdo02">최근 한달</label></span>
      </div>
    </div>
   //-->

  </div><!-- .sub_tit_area -->

  <div class="graph_area">
    <div class="graph_top">
      <div class="left"></div>
      <div class="right">
        <select class="select_design cid_sel_category">
          <option value="">카테고리 선택</option>
        </select>
        <select class="select_design cid_sel_api">
          <option value="">API 선택</option>
        </select>
      </div>
    </div>
    <div class="graph_box">
      <div id="id_graph" class="graph"></div>
    </div>
  </div><!-- .graph_area -->
</div><!--  .cid_apitrafficgraph_wrap -->

<script>
  $(document).ready(function() {
    apitrfgrp_fn_init_handler();
    apitrfgrp_fn_init_dialog();
    apitrfgrp_fn_init_page();
  });
  function apitrfgrp_fn_init_handler() {
    $('.cit_rdo_topn').on('click', function(p_evt) {
      var a_topn_series = $sf_arr(apitrfgrp_g_data['topn_series']);
      apitrfgrp_fn_proc_graph('draw_topn', a_topn_series);
    });

    $('.cid_sel_category').on('change', function(p_evt) {
      var api_spc_no = $(this).val();
      if ($is_positive_number(api_spc_no)) {
        apitrfgrp_fn_query_api_list(api_spc_no);
      }
    });

    $('.cid_sel_api').on('change', function(p_evt) {
      var api_no = $(this).val();
      var api_nm = $(this).find('option:selected').text();
      if ($is_positive_number(api_no) && !$is_empty(api_nm)) {
        apitrfgrp_fn_query_api_traffic(api_nm);
      }
    });
  }

  function apitrfgrp_fn_init_dialog() {
    //-- [2023:codeeyes][empty_block issue]
  }

  function apitrfgrp_fn_init_page() {
    apitrfgrp_fn_query_category_list();
    
    apitrfgrp_fn_build_graph();
    
    apitrfgrp_fn_query_api_traffic_topn();
  }
</script>

<script>
  var apitrfgrp_g_data = {
    'graph_day_cnt': 7
    , 'topn_series': []
  };

  function _dev_apitrfgrp_fn_on_sel_api(api_nm) {
    var lfn_dev_apitrfgrp_fn_get_series = function(p_name) {
      var a_data = [];
      //-- [i][random number]
      var day_cnt = apitrfgrp_g_data['graph_day_cnt'];
      var a_day = apitrfgrp_fn_get_nday_data(day_cnt);
      for (var n_ii = 0; n_ii < a_day.length; n_ii++) {
        var s_day = a_day[n_ii];
        var randNum = Math.round(Math.random() * 10000);
        a_data.push([moment.utc(s_day).valueOf(), randNum]);
      }
      /*-- [ref][random date] 
      var lfn_randomDate = (function(start, end) { return new Date(start.getTime() + Math.random() * (end.getTime() - start.getTime())); });
      for (var n_ii = 0; n_ii < 100; n_ii++) { a_data.push([(lfn_randomDate(new Date(2022, 0, 1), new Date())).getTime(), Math.round(Math.random() * 100)]); }
      a_data.sort(function (a,b) { if (a[0] < b[0]) return -1; if (a[0] > b[0]) return 1; return 0; })
      --*/
      return { 'name': p_name, 'data': a_data };
    };
    
    var a_graph_series = [ lfn_dev_apitrfgrp_fn_get_series('성공'), lfn_dev_apitrfgrp_fn_get_series('실패') ];
    apitrfgrp_fn_proc_graph('draw_2color', a_graph_series);
  }
  
  function apitrfgrp_fn_get_nday_data(p_nday) {
    var a_date = [];
    var mmt = moment().subtract((p_nday + 1), 'd');
    for (var n_ii = 0; n_ii < p_nday; n_ii++) {
      a_date.push(mmt.add(1, 'd').format('YYYYMMDD'));
    }
    return a_date;
  }

  var apitrfgrp_m_chart = null;

  function apitrfgrp_fn_proc_graph(p_direct, p_a_graph_series, p_options) {
    var apitrfgrp_line_col_pre = {
      'default': ['#009687', '#8bc6cd', '#0f58a3', '#97a6cf','#55c0ce','#ccced0']
      , '2color': ['#1da987', '#eb4f4f']
    };

    var opt_colors = { 'colors': (('draw_2color' == p_direct) ? apitrfgrp_line_col_pre['2color'] : apitrfgrp_line_col_pre['default']) }; 
    var chart_options = Object.assign({}, opt_colors, $sf_obj(p_options));
    apitrfgrp_fn_build_graph(p_a_graph_series, chart_options);
  }
  
  function apitrfgrp_fn_build_graph(p_a_graph_series, p_chart_options) {
    var apitrfgrp_m_chart_options = {
      credits: { enabled: false },
      colors: ['#009687', '#8bc6cd', '#0f58a3', '#97a6cf','#55c0ce','#ccced0'],
      chart: { spacingBottom: 100 },
      legend: {
        align: 'left', verticalAlign: 'bottom', x: 10, y: 40, marker: { enabled: false, symbolHeight: 6 },
        itemStyle: { color: '#333a4b', fontSize:'14', letterSpacing:'-1' },
        itemDistance: 20
      },
      title: { text: '', x: -20 }, subtitle: { text: '', x: -20 },
      xAxis: { title: { text: '' },  maxPadding: 0, labels: { y: 40, align: 'center', autoRotationLimit: 90 }, crosshair: { width: 1, color: '#cdd6dd' },
        type: 'datetime', dateTimeLabelFormats: { millisecond: '%H:%M', second: '%H:%M', minute: '%H:%M', hour: '%H:%M', day: '%m월%d일', week: '%m월%d일', month: '%y년%m월', year: '%y년%m월' },
      },
      yAxis: { title: { text: '' }, maxPadding: 0.2 },
      plotOptions: {
        series: {
          label: {  enabled: false, connectorAllowed: false },
          marker: { enabled: false, fillColor: '#FFFFFF', lineWidth: 2, lineColor: null, symbol: 'round', width: 16, height: 16, radius: 2 }, 
        },
      },
      tooltip: {
        headerFormat: '',
        <%-- 
          //--[i][chg][pointFormat => formatter][',' not display]
          //--@@pointFormat : '<span style="color:#fff;font-size:13px">{point.y}건</span>',
        --%>
        formatter: (function() { return ('<b>' + this.series.name + '</b><br/><span style="color:#fff;font-size:13px">' + Highcharts.numberFormat(this.y, 0, '', ',') + '건</span>'); }),
        backgroundColor: '#454b54', borderWidth: 0, borderRadius: 3, style: { color: '#fff', fontsize:13 }
      },
      responsive: { rules: [{ condition: { maxWidth: 500 }, chartOptions: { legend: { layout: 'horizontal', align: 'center', verticalAlign: 'bottom' } } }] },
      series: {},
    };

    var graph_id = 'id_graph';
    var chart_options = Object.assign(apitrfgrp_m_chart_options, { series: $sf_arr(p_a_graph_series) }, $sf_obj(p_chart_options));
    apitrfgrp_m_chart = new Highcharts.chart(graph_id, chart_options);
  }
  
  function apitrfgrp_fn_build_empty_data() {
    var a_day = apitrfgrp_fn_get_nday_data(apitrfgrp_g_data['graph_day_cnt']);
    var o_cnt = {};
    for (var n_ii = 0; n_ii < a_day.length; n_ii++) {
      o_cnt[a_day[n_ii]] = 0;
    }
    return o_cnt;
  }

  function apitrfgrp_fn_build_data(p_direct, p_nlist) {
    var nlist = $sf_arr(p_nlist);

    var a_graph_series = [];
    if ('api' == p_direct) {
      var o_suces_data = apitrfgrp_fn_build_empty_data();
      var o_fail_data = apitrfgrp_fn_build_empty_data();
      $.each(nlist, function (idx, item) {
        var item = nlist[idx];
        var yyyymmdd = item['STAT_DT'];  //-- yyyymmdd
        if ($has_own(o_suces_data, yyyymmdd) || $has_own(o_fail_data, yyyymmdd)) {
          var rqt_cnt = item['RQT_CNT'];
          var suces_cnt = item['SUCES_CNT'];
          var fail_cnt = item['FAIL_CNT'];
          o_suces_data[yyyymmdd] = suces_cnt;
          o_fail_data[yyyymmdd] = fail_cnt;
        }
      });
      var a_name = ['성공', '실패'];
      var a_data_list = [o_suces_data, o_fail_data];
      for (var n_ii = 0; n_ii < a_data_list.length; n_ii++) {
        var a_data = [];
        var o_data = a_data_list[n_ii];
        //-- [i][change 'yyyymmdd' -> value]
        $.each(o_data, function(key, val) {
          a_data.push([moment.utc(key).valueOf(), val]);
        });
        a_graph_series.push({'name': a_name[n_ii], 'data': a_data});
      }

<%--
      //-- [i][ing_dev][fill random value]
      if (nlist.length == 0) {
        for (var n_ii = 0; n_ii < a_data_list.length; n_ii++) {
          var a_data = (a_graph_series[n_ii])['data'];
          for (var n_jj = 0; n_jj < a_data.length; n_jj++) {
            var a_point = a_data[n_jj];
            a_point[1] = Math.round(Math.random() * 10000);
          }
        }
      }
--%>
    }
    else if ('topn' == p_direct) {
      var a_name = [];
      var a_data_list = [];
      $.each(nlist, function (idx, item) {
        var item = nlist[idx];
        var yyyymmdd = item['STAT_DT'];
        var api_nm = item['def_API_NM'];
        var rqt_cnt = item['RQT_CNT'];
        var n_idx = a_name.indexOf(api_nm);
        //--##console.log("idx:"+idx+", yyyymmdd:"+yyyymmdd+", rqt_cnt:"+rqt_cnt+" , n_idx:"+n_idx);
        if (n_idx == -1) {
          if (!$is_empty(api_nm)) {
            a_name.push(api_nm);
            a_data_list.push(apitrfgrp_fn_build_empty_data());
            n_idx = a_data_list.length - 1;
          }
        }
        if (n_idx != -1) {
          var o_line = a_data_list[n_idx]; 
          var cnt = rqt_cnt;
          if ($has_own(o_line, yyyymmdd)) {
            o_line[yyyymmdd] = cnt;
            //--##console.log("n_idx:"+n_idx+" , yyyymmdd:"+yyyymmdd + " , cnt:"+cnt);  
          }
        }
      });

<%--
      //-- [i][ing_dev][dummy api]
      if (nlist.length == 0) {
        var a_test_name = 'api_01;api_02;api_03;api_04;api_05'.split(';');
        for (var n_ii = 0; n_ii < a_test_name.length; n_ii++) {
          a_name.push(a_test_name[n_ii]);
          a_data_list.push(apitrfgrp_fn_build_empty_data());
        }
      }
--%>

      for (var n_ii = 0; n_ii < a_data_list.length; n_ii++) {
        var a_data = [];
        var o_data = a_data_list[n_ii];
        //-- [i][change 'yyyymmdd' -> value]
        $.each(o_data, function(key, val) {
          a_data.push([moment.utc(key).valueOf(), val]);
        });
        a_graph_series.push({'name': a_name[n_ii], 'data': a_data});
      }

<%--
      //-- [i][ing_dev][fill random value]
      if (nlist.length == 0) {
        for (var n_ii = 0; n_ii < a_data_list.length; n_ii++) {
          var a_data = (a_graph_series[n_ii])['data'];
          for (var n_jj = 0; n_jj < a_data.length; n_jj++) {
            var a_point = a_data[n_jj];
            a_point[1] = Math.round(Math.random() * 10000);
          }
        }
      }
--%>
    }
    return a_graph_series;
  }
</script>

<script>
  function apitrfgrp_fn_query_api_traffic_topn() {
    var fn_cb = function(data, textStatus, request, call_param) {
      var s_msg = '';
      var b_is_valid_data = $has_own(data, 'nlist');
      if (b_is_valid_data) {
        var nlist = $sf_obj_val(data, 'nlist');
        apitrfgrp_g_data['topn_series'] = apitrfgrp_fn_build_data('topn', nlist);
        $('.cit_rdo_topn').prop('disabled', false).show().trigger('click');
      }
      else { s_msg = '유효하지 않은 검색 결과 입니다.'; }
      if (s_msg.length > 0) {
        alert_message(s_msg);
        return;
      }
    }
    
    //-- [i][uncheck][disable][hide]
    $('.cit_rdo_topn').prop('checked', false).prop('disabled', true).hide();
  
    //-- [i]검색기간get
    var day_cnt = apitrfgrp_g_data['graph_day_cnt'];
    var a_day = apitrfgrp_fn_get_nday_data(day_cnt);
    
    <%--
    //-- [i][for_test]
    //--a_day[0] = '00000000'; a_day[a_day.length - 1] = '99999999';
    --%>
    var param = {'fdate': a_day[0], 'tdate': a_day[a_day.length - 1], 'top': 5};  //-- optional: 'defApiNm'
    <%-- //-- [i][try] --%>
    var cmd = 'cmd_api_traffic_topn_v1'; //-- 'cmd_api_traffic_topn'
    apitrfgrp_fn_query(cmd, param, fn_cb);
  }
  
  function apitrfgrp_fn_query_api_traffic(p_api_nm) {
    var fn_cb = function(data, textStatus, request, call_param) {
      var s_msg = '';
      var b_is_valid_data = $has_own(data, 'nlist');
      if (b_is_valid_data) {
        var nlist = $sf_obj_val(data, 'nlist');
        apitrfgrp_fn_proc_graph('draw_2color', apitrfgrp_fn_build_data('api', nlist));
      }
      else { s_msg = '유효하지 않은 검색 결과 입니다.'; }
      if (s_msg.length > 0) {
        alert_message(s_msg);
        return;
      }
    }
    
    //-- [i]검색기간get
    var day_cnt = apitrfgrp_g_data['graph_day_cnt'];
    var a_day = apitrfgrp_fn_get_nday_data(day_cnt);

    //-- [i][for_test]
    //--a_day[0] = '00000000'; a_day[a_day.length - 1] = '99999999';
    var param = {'fdate': a_day[0], 'tdate': a_day[a_day.length - 1], 'apiNm': p_api_nm};
    apitrfgrp_fn_query('cmd_sel_api_traffic', param, fn_cb);
  }
  
  function apitrfgrp_fn_query_category_list() {
    var fn_cb = function(data, textStatus, request, call_param) {
      var s_msg = '';
      var b_is_valid_data = $has_own(data, 'nlist');
      if (b_is_valid_data) {
        var nlist = $sf_obj_val(data, 'nlist');
        $.each(nlist, function (idx, item) {
          var item = nlist[idx];
          var jq_option = $('<option value="' + item['API_SPC_NO'] + '">' + item['spc_API_NM'] + '</option>');
          $('.cid_sel_category').append(jq_option);
        });
      }
      else { s_msg = '유효하지 않은 검색 결과 입니다.'; }
      if (s_msg.length > 0) {
        alert_message(s_msg);
        return;
      }
    }

    //-- [i][clear select]
    $('.cid_sel_category, .cid_sel_api').find('option[value!=""]').remove();
  
    var param = {};
    apitrfgrp_fn_query('cmd_sel_api_category_list', param, fn_cb);
  }

  function apitrfgrp_fn_query_api_list(p_api_spc_no) {
    var fn_cb = function(data, textStatus, request, call_param) {
      var s_msg = '';
      var b_is_valid_data = $has_own(data, 'nlist');
      if (b_is_valid_data) {
        var nlist = $sf_obj_val(data, 'nlist');
        $.each(nlist, function (idx, item) {
          var item = nlist[idx];
          var jq_option = $('<option value="' + item['API_NO'] + '">' + item['API_NM'] + '</option>');
          $('.cid_sel_api').append(jq_option);
        });
      }
      else { s_msg = '유효하지 않은 검색 결과 입니다.'; }
      if (s_msg.length > 0) {
        alert_message(s_msg);
        return;
      }
    }
    
    //-- [i][clear select]
    $('.cid_sel_api').find('option[value!=""]').remove();

    var param = {'apiSpcNo': p_api_spc_no};
    apitrfgrp_fn_query('cmd_sel_api_list', param, fn_cb);
  }

  //-- [i][apitrfgrp/ajax_query.do 공통
  function apitrfgrp_fn_query(p_cmd, p_param, p_fn_cb) {
    var fn_beforeSend = (function(xhr) { (('function' == typeof($.ajaxSetup()['beforeSend'])) && ($.ajaxSetup()['beforeSend'])(xhr)); });
    var fn_error = (function(request, status, error) { alert('status: ' + request.status + '\n' + 'error: ' + error); });
    var fn_success = (function(data, textStatus, request) {
      if ('function' == typeof(p_fn_cb)) {
        p_fn_cb(data, textStatus, request, {'cmd': p_cmd, 'param': p_param});
      }
    });
    var param = $sf_obj(p_param);
    $.ajax({
      url: '<c:url value="/api/reg/apitrfgrp/ajax_query.do"/>' + '?cmd=' + p_cmd,
      type : 'POST',
      data: param, cache: false,
      async: true,
      beforeSend: fn_beforeSend,
      success: fn_success, 
      error: fn_error,
    });
  }
</script>

<!-- apiTrafficGraph_inc.jsp //-->
