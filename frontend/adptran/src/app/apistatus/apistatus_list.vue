<comment>
  API상황판 목록보기
  /adptran/apistatus/list
</comment>
<script>
import { CommonMixin, Vuetable, CusVuetablePagination } from '@/common/adptran.js';
import { ApistatusCommonMixin } from '@/common/apistatus_common.js';
import Moment from 'moment';
//-- popup-[API상태점검이력조회]
import apistatusCheckHistListDialog from '@approot/dialog/apistatusCheckHistListDialog.vue';

export default {
  name: 'apistatus_list',
  components: {
    'cus-vuetable': Vuetable,
    'cus-vuetable-pagination': CusVuetablePagination,
    'apistatus-check-hist-list-dialog': apistatusCheckHistListDialog,
},
  mixins: [CommonMixin, ApistatusCommonMixin],
  props: {
    prop_tab_idx: { type: Number, default: 0, },
    prop_sys_id: { type: String, default: '', },
    prop_status_group_no: { type: String, default: '', },
    prop_api_spc_no: { type: String, default: '', },
    prop_api_nm: { type: String, default: '', },
    prop_status_code: { type: String, default: '', },
  },
  data() {
    return {
      m_vue_id: 'apistatus_list',
      //-- @constant {
      //--[drm][test] {
      m_con_dev_is_longtext_test: false,
      m_con_dev_start_status_code_ymd: '',  //-- e.g. 2019-09-19
      //--[drm][test] }
      //-- @constant }
      //-- @property {
      m_prop_apistatusCheckHistListData: {},  //-- modalapistatusCheckHistListDialog prop_data
      //-- @property }
      //-- @biz-data {
      m_tab_queried: [],
      m_current_tab_idx: 0,
      m_status_code_ymd_list: [],
      //-- @biz-data }
      //-- @ui-data {
      m_ui_api_system_list: [],  //-- system목록 [{'sys_id': '', 'sys_nm': ''}]
      m_ui_api_spc_list: {}, //-- service목록 {'sys_id': '', [{'api_spc_no': '', 'api_spc_nm': '', 'api_spc_id': '', 'api_spc_ver': ''}]}
      m_ui_api_status_spc_group_list: [],  //-- group목록 [{'status_group_no': status_group_no, 'status_group_nm': status_group_nm}]

      m_ui_ok_count: 0,  //-- 정상건수
      m_ui_dl_count: 0,  //-- 지연건수
      m_ui_nk_count: 0,  //-- 오류건수
      //-- @ui-data }
      //-- @table-#1 {
      table_apiStatusInfoList: {
        fields: [
          { name: 'PK', visible: false },
          { name: 'no', title: 'NO', width: '5%', titleClass: 'center-aligned', dataClass: 'center-aligned', },
          { name: 'sys_nm', title: '시스템', width: '15%', titleClass: 'center-aligned', dataClass: 'left-aligned', },
          { name: 'api_spc_nm', title: '서비스', width: '25%', titleClass: 'center-aligned', dataClass: 'left-aligned', },
          { name: 'slot_api_nm', title: 'API', width: '25%', titleClass: 'center-aligned', dataClass: 'left-aligned', },
          { name: 'slot_status_code', title: '상태', width: '7%', titleClass: 'center-aligned', dataClass: 'center-aligned', },
          { name: 'status_res_msec', title: '응답시간', width: '10%', titleClass: 'center-aligned', dataClass: 'center-aligned',
            formatter: ((value) => { let n_msec = parseInt(value, 10); return ((n_msec >= 0) ? (value + ' ms') : '-'); }), },
          { name: 'status_check_dt', title: '점검일시', width: '13%', titleClass: 'center-aligned', dataClass: 'center-aligned',
            formatter: ((value) => this.$fmt_data(value, 'fmt_date_01')), },
        ],
        css: { table: { tableClass: 'cus-vuetable table-list popup_table popup_table2', }, pagination: {}, },
        pageRow: 10,
      }, // table_apiStatusInfoList{}
      //-- @table-#1 }
      //-- @table-#2 {
      table_apiStatusInfoDailyList: {
        fields: [
          { name: 'PK', visible: false },
          { name: 'no', title: 'NO', width: '5%', titleClass: 'center-aligned', dataClass: 'center-aligned', },
          { name: 'sys_nm', title: '시스템', width: '13%', titleClass: 'center-aligned', dataClass: 'left-aligned', },
          { name: 'api_spc_nm', title: '서비스', width: '20%', titleClass: 'center-aligned', dataClass: 'left-aligned', },
          { name: 'slot_api_nm', title: 'API', width: '20%', titleClass: 'center-aligned', dataClass: 'left-aligned', },
          { name: 'slot_daily_status_code_0', title: '', width: '6%', titleClass: 'center-aligned', dataClass: 'center-aligned', },
          { name: 'slot_daily_status_code_1', title: '', width: '6%', titleClass: 'center-aligned', dataClass: 'center-aligned', },
          { name: 'slot_daily_status_code_2', title: '', width: '6%', titleClass: 'center-aligned', dataClass: 'center-aligned', },
          { name: 'slot_daily_status_code_3', title: '', width: '6%', titleClass: 'center-aligned', dataClass: 'center-aligned', },
          { name: 'slot_daily_status_code_4', title: '', width: '6%', titleClass: 'center-aligned', dataClass: 'center-aligned', },
          { name: 'slot_daily_status_code_5', title: '', width: '6%', titleClass: 'center-aligned', dataClass: 'center-aligned', },
          { name: 'slot_daily_status_code_6', title: '', width: '6%', titleClass: 'center-aligned', dataClass: 'center-aligned', },
        ],
        css: { table: { tableClass: 'cus-vuetable table-list popup_table popup_table2', }, pagination: {}, },
        pageRow: 10,
      }, // table_apiStatusInfoDailyList{}
      //-- @table-#2 }
      //-- @table-common {
      // -- vuetable-2 tr height(px)
      m_table_height_tr: 41,
      // -- vuetable-2 noDataTemplate
      m_noDataTemplate: '입력하신 검색어에 해당하는 결과가 없습니다.',
      //-- @table-common }

      //-- @table-query {
      m_tc_apiStatusInfoList: 0,
      m_tc_apiStatusInfoDailyList: 0,
      m_prop_appendParams_apiStatusInfoList: {},
      m_prop_appendParams_apiStatusInfoDailyList: {},

      //-- vue-binding
      m_model_sel_sysId: '',
      m_model_sel_statusGroupNo: '',
      m_model_sel_apiSpcNo: '',
      m_model_txt_apiNm: '',
      m_model_sel_statusCode: '',

      m_model_sel_sysId_daily: '',
      m_model_sel_statusGroupNo_daily: '',
      m_model_sel_apiSpcNo_daily: '',
      m_model_txt_apiNm_daily: '',
      //-- @table-query }
    } // return{}
  }, // data ()
  computed: {
    computed_api_url_apiStatusInfoList () {
      return this.$getApistatusApiUrl() + this.m_con_apistatus_list_api_status_info_list_url;
    },
    computed_api_url_apiStatusInfoDailyList () {
      return this.$getApistatusApiUrl() + this.m_con_apistatus_list_api_status_info_daily_list_url;
    },
    computed_css_tab_selected(tab_idx) {
      return ((tab_idx) => {
        return ((this.m_current_tab_idx == tab_idx) ? 'current' : '');
      });
    },
    computed_css_status_code(status_code) {
      return ((status_code) => {
        return (({ 'OK': 'ico_confirm', 'DL': 'ico_delay', 'NK': 'ico_error', }[status_code])||'');
      });
    },
  },
  created: function() {
    this.$console_log('trace', 'created()');

    this.m_current_tab_idx = (((this.prop_tab_idx >= 0) && (this.prop_tab_idx <= 1)) ? this.prop_tab_idx : this.m_current_tab_idx);
    this.m_tab_queried = [false, false];

    if (0 == this.m_current_tab_idx) {
      this.m_model_sel_sysId = this.prop_sys_id;
      this.m_model_sel_statusGroupNo = this.prop_status_group_no;
      this.m_model_sel_apiSpcNo = this.prop_api_spc_no;
      this.m_model_txt_apiNm = this.prop_api_nm;
      this.m_model_sel_statusCode = this.prop_status_code;
    }
    else if (1 == this.m_current_tab_idx) {
      this.m_model_sel_sysId_daily = this.prop_sys_id;
      this.m_model_sel_statusGroupNo_daily = this.prop_status_group_no;
      this.m_model_sel_apiSpcNo_daily = this.prop_api_spc_no;
      this.m_model_txt_apiNm_daily = this.prop_api_nm;
    }

    let n_days = 7;
    let mt_base = Moment().subtract(n_days, 'days');

    //--[drm][test]
    if (this.m_con_dev_start_status_code_ymd.length > 0) {
      mt_base = Moment(this.m_con_dev_start_status_code_ymd);
    }

    this.m_status_code_ymd_list = [];
    for (let n_ii = 0; n_ii < 7; n_ii++) {
      this.m_status_code_ymd_list.push(mt_base.format('YYYY-MM-DD')); //-- 'daily_list': [] status_check_dt
      this.table_apiStatusInfoDailyList.fields[5 + n_ii]['title'] = mt_base.format('M/D');  //-- table-#2 title
      mt_base = mt_base.add(1, 'days');
    }

    this.fn_apicall_api_system_spc_list();
    this.fn_apicall_api_status_spc_group_list();
  },
  mounted: function() {
    this.$console_log('trace', 'mounted()');
    this.onclick_tab(this.m_current_tab_idx);
  },
  updated: function() {
    this.$console_log('trace', 'updated()');
  },
  methods: {
    //-- @table-#1 {
    onPaginationData_apiStatusInfoList(paginationData) {
      this.$console_log('watch', 'onPaginationData_apiStatusInfoList.', 'paginationData: ', paginationData);
      this.$refs.refPagination_apiStatusInfoList.setPaginationData(paginationData)
    },
    onChangePage_apiStatusInfoList(page) {
      this.$refs.refTable_apiStatusInfoList.changePage(page)
    },
    prop_transform_apiStatusInfoList(data) {
      this.$console_log('watch', 'prop_transform_apiStatusInfoList.', 'data: ', data);
      try {
        let isSuccess = (200 == data['resultCode']);  //-- success
        if (true == isSuccess) {
          let table = this.$refs.refTable_apiStatusInfoList;

          let total = this.$sf_int(data['totalCount'], 0);
          let perPage = this.$sf_int(data['pageSize'], 0);
          let currentPage = Math.max(this.$sf_int(data['currentPage'], 0), 1);
          let baseIndex = ((currentPage - 1) * perPage);

          data = (data['data']||{});

          if (0 == this.m_tc_apiStatusInfoList) {
            this.m_ui_ok_count = this.$sf_int(data['ok_count'], 0);
            this.m_ui_dl_count = this.$sf_int(data['dl_count'], 0);
            this.m_ui_nk_count = this.$sf_int(data['nk_count'], 0);
          }
          this.m_tc_apiStatusInfoList = total;

          let mydata = (data['api_status_info_list']||[]);
          mydata.forEach((element, index) => {
            element.PK = baseIndex + index;
            element.no = (baseIndex + index + 1);
            //--[drm][test]
            if (this.m_con_dev_is_longtext_test) {
              let s_info = '';
              s_info = element['sys_nm']; element['sys_nm'] = (s_info + s_info + s_info + s_info);
              s_info = element['api_spc_nm']; element['api_spc_nm'] = (s_info + s_info + s_info + s_info);
              s_info = element['api_nm']; element['api_nm'] = (s_info + s_info + s_info + s_info);
            }
          });

          let mypagination = table.makePagination(total, perPage, currentPage);
          return { 'mydata': mydata, 'mypagination': mypagination };
        }
        this.$proc_api_returnCd_Fail(data);
      }
      catch (e) {
        this.$console_log('error', 'prop_transform_apiStatusInfoList.', 'e: ', e);
      }
      return { 'mydata': [], 'mypagination': {} };
    },
    //-- override vuetable default queryParam
    prop_queryParams_apiStatusInfoList(sortOrder, currentPage, perPage) {
      let param = { 'pz': this.table_apiStatusInfoList.pageRow, 'pg': currentPage, 'tc': this.m_tc_apiStatusInfoList };
      param = Object.assign(this.m_con_def_api_param, param)
      //--[drm][test]
      //--##param = Object.assign(param, {'dev_opt': 'use_static_data'})
      return param;
    },
    //-- @table-#1 }
    //-- @table-#2 {
    onPaginationData_apiStatusInfoDailyList(paginationData) {
      this.$console_log('watch', 'onPaginationData_apiStatusInfoDailyList.', 'paginationData: ', paginationData);
      this.$refs.refPagination_apiStatusInfoDailyList.setPaginationData(paginationData)
    },
    onChangePage_apiStatusInfoDailyList(page) {
      this.$refs.refTable_apiStatusInfoDailyList.changePage(page)
    },
    prop_transform_apiStatusInfoDailyList(data) {
      this.$console_log('watch', 'prop_transform_apiStatusInfoDailyList.', 'data: ', data);
      try {
        let isSuccess = (200 == data['resultCode']);  //-- success
        if (true == isSuccess) {
          let table = this.$refs.refTable_apiStatusInfoDailyList;

          let total = this.$sf_int(data['totalCount'], 0);
          let perPage = this.$sf_int(data['pageSize'], 0);
          let currentPage = Math.max(this.$sf_int(data['currentPage'], 0), 1);
          let baseIndex = ((currentPage - 1) * perPage);

          data = (data['data']||{});

          let mydata = (data||[]);
          mydata.forEach((element, index) => {
            element.PK = baseIndex + index;
            element.no = (baseIndex + index + 1);

            let daily_list = element['daily_list'];

            element.daily_status_code = new Array(7);

            if (Array.isArray(daily_list) == true) {
              daily_list.forEach((daily_item) => {
                let ymd = (daily_item['status_check_dt']||'').substr(0, 10);  //-- yyyy-mm-dd
                for (let n_ii = 0; n_ii < this.m_status_code_ymd_list.length; n_ii++) {
                  if (ymd == this.m_status_code_ymd_list[n_ii] ) {
                    element.daily_status_code[n_ii] = daily_item['status_code'];
                    break;
                  }
                }
              });
            }
            //--[drm][test]
            if (this.m_con_dev_is_longtext_test) {
              let s_info = '';
              s_info = element['sys_nm']; element['sys_nm'] = (s_info + s_info + s_info + s_info);
              s_info = element['api_spc_nm']; element['api_spc_nm'] = (s_info + s_info + s_info + s_info);
              s_info = element['api_nm']; element['api_nm'] = (s_info + s_info + s_info + s_info);
            }
          });

          let mypagination = table.makePagination(total, perPage, currentPage);
          return { 'mydata': mydata, 'mypagination': mypagination };
        }
        this.$proc_api_returnCd_Fail(data);
      }
      catch (e) {
        this.$console_log('error', 'prop_transform_apiStatusInfoDailyList.', 'e: ', e);
      }
      return { 'mydata': [], 'mypagination': {} };
    },
    //-- override vuetable default queryParam
    prop_queryParams_apiStatusInfoDailyList(sortOrder, currentPage, perPage) {
      let param = { 'pz': this.table_apiStatusInfoDailyList.pageRow, 'pg': currentPage, 'tc': this.m_tc_apiStatusInfoDailyList };
      param = Object.assign(this.m_con_def_api_param, param)
      //--[drm][test]
      //--##param = Object.assign(param, {'dev_opt': 'use_static_data'})
      return param;
    },
    //-- @table-#2 }
    //-- @handler {
    //-- tab click
    onclick_tab(tab_idx) {
      this.m_current_tab_idx = tab_idx;
      if (false == this.m_tab_queried[this.m_current_tab_idx]) {
        if (0 == this.m_current_tab_idx) {
          this.onclick_search_apiStatusInfoList();
        }
        else if (1 == this.m_current_tab_idx) {
          this.onclick_search_apiStatusInfoDailyList();
        }
        this.m_tab_queried[this.m_current_tab_idx] = true;
       }
    },
    //-- 검색click - 최신현황
    onclick_search_apiStatusInfoList() {
      this.fn_search_apiStatusInfoList(this.m_model_sel_sysId, this.m_model_sel_statusGroupNo, this.m_model_sel_apiSpcNo, this.m_model_txt_apiNm, this.m_model_sel_statusCode);
    },
    //-- 검색click - 상태히스토리
    onclick_search_apiStatusInfoDailyList() {
      let start_status_check_ymd = this.$sf_str(this.m_status_code_ymd_list[0]).replace(/-/g, '');
      let end_status_check_ymd = this.$sf_str(this.m_status_code_ymd_list[this.m_status_code_ymd_list.length - 1]).replace(/-/g, '');
      this.fn_search_apiStatusInfoDailyList(this.m_model_sel_sysId_daily, this.m_model_sel_statusGroupNo_daily, this.m_model_sel_apiSpcNo_daily, this.m_model_txt_apiNm_daily, start_status_check_ymd, end_status_check_ymd);
    },
    onclick_row_action(cmd, rowData) {
      this.$console_log('trace', 'onclick_row_action()', 'cmd: ', cmd, 'rowData: ', rowData);
      if ('popup-apistatus_check_hist_dialog' == cmd) {
        this.m_prop_apistatusCheckHistListData = {
          'sys_nm': rowData.sys_nm,
          'api_spc_nm': rowData.api_spc_nm,
          'api_nm': rowData.api_nm,
          'api_no': rowData.api_no,
        };
        this.$modal.show('modalApistatusCheckHistListDialog');
      }
    },
    //-- @handler }
    //-- @api function {
    //-- 시스템, 서비스 query
    fn_apicall_api_system_spc_list(param) {
      this.$console_log('trace', 'fn_apicall_api_system_spc_list()', 'param: ', param);

      //-- 초기화
      this.m_ui_api_system_list = [];
      this.m_ui_api_spc_list = {};

      let url = this.m_con_apistatus_common_api_system_spc_list_url;
      this.$fn_call_apistatus_api(url, Object.assign(this.m_con_def_api_param, param), 'post', this.fn_apicb_common_api_system_spc_list);
    },
    //-- api callback common_api_system_spc_list
    fn_apicb_common_api_system_spc_list(ret, response, request, payload) {
      this.$console_log('trace', 'fn_apicb_common_api_system_spc_list()', 'ret: ', ret, 'response: ', response, 'request: ', request, 'payload: ', payload);
      //-- ret: 'ok', 'nk', 'failed', 'catched'
      if ('ok' == ret) {
        let data_list = payload;
        if (Array.isArray(data_list) == false) {
          //--throw {'message': 'common_api_system_spc_list: payload is not array'};
          return;
        }

        let sys_id_sv = '';
        data_list.forEach((data_item) => {
          let sys_id = data_item['sys_id'];
          let sys_nm = data_item['sys_nm'];
          let api_spc_no = data_item['api_spc_no'];
          let api_spc_nm = data_item['api_spc_nm'];
          let api_spc_id = data_item['api_spc_id'];
          let api_spc_ver = data_item['api_spc_ver'];

          if (sys_id != sys_id_sv) {
            //-- system 목록저장
            this.m_ui_api_system_list.push({'sys_id': sys_id, 'sys_nm': sys_nm});
            sys_id_sv = sys_id;
          }
          let api_spc_list = this.m_ui_api_spc_list[sys_id];
          api_spc_list = ((Array.isArray(api_spc_list) == false) ? [] : api_spc_list);
          //-- service 목록저장
          api_spc_list.push({'api_spc_no': api_spc_no, 'api_spc_nm': api_spc_nm, 'api_spc_id': api_spc_id, 'api_spc_ver': api_spc_ver});
          this.m_ui_api_spc_list[sys_id] = api_spc_list;
        });
      }
      else {
        this.$proc_api_resultCode_Fail(ret, response);
      }
    },

    //-- 그룹 query
    fn_apicall_api_status_spc_group_list(param) {
      this.$console_log('trace', 'fn_apicall_api_status_spc_group_list()', 'param: ', param);

      //-- 초기화
      this.m_ui_api_status_spc_group_list = [];

      let url = this.m_con_apistatus_common_api_status_spc_group_list_url;
      this.$fn_call_apistatus_api(url, Object.assign(this.m_con_def_api_param, param), 'post', this.fn_apicb_common_api_status_spc_group);
    },
    //-- api callback common_api_status_spc_group_list
    fn_apicb_common_api_status_spc_group(ret, response, request, payload) {
      this.$console_log('trace', 'common_api_status_spc_group_list()', 'ret: ', ret, 'response: ', response, 'request: ', request, 'payload: ', payload);
      //-- ret: 'ok', 'nk', 'failed', 'catched'
      if ('ok' == ret) {
        let data_list = payload;
        if (Array.isArray(data_list) == false) {
          //throw {'message': 'common_api_status_spc_group_list: payload is not array'};
          return;
        }

        data_list.forEach((data_item) => {
          this.m_ui_api_status_spc_group_list.push(data_item);
        });
      }
      else {
        this.$proc_api_resultCode_Fail(ret, response);
      }
    },

    //-- @api function }
    //-- @function {
    fn_search_apiStatusInfoList(sysId, statusGroupNo, apiSpcNo, apiNm, statusCode) {
      //-- 초기화
      this.m_tc_apiStatusInfoList = 0;

      this.m_ui_ok_count = 0;
      this.m_ui_dl_count = 0;
      this.m_ui_nk_count = 0;

      //-- prepare query parameter
      this.m_prop_appendParams_apiStatusInfoList = { 'sys_id': sysId, 'status_group_no': statusGroupNo, 'api_spc_no': apiSpcNo, 'api_nm': apiNm, 'status_code': statusCode, };
      this.$nextTick(() => { this.$refs.refTable_apiStatusInfoList.refresh(); });
    },
    fn_search_apiStatusInfoDailyList(sysId, statusGroupNo, apiSpcNo, apiNm, startStatusCheckYmd, toStatusCheckYmd) {
      //-- 초기화
      this.m_tc_apiStatusInfoDailyList = 0;

      //-- prepare query parameter
      this.m_prop_appendParams_apiStatusInfoDailyList = { 'sys_id': sysId, 'status_group_no': statusGroupNo, 'api_spc_no': apiSpcNo, 'api_nm': apiNm, 'start_status_check_ymd': startStatusCheckYmd, 'end_status_check_ymd': toStatusCheckYmd, };
      this.$nextTick(() => { this.$refs.refTable_apiStatusInfoDailyList.refresh(); });
    },
    //-- @function }
  } // methods:
};
</script>

<template>
  <section>

    <!--// 0920 작업 -->
    <div class="regiApi_wrap">
      <div class="pkg_board">
        <section>
          <div class="regi_list regi_list_box">
            <ul class="tab_list">
              <li data-tab="tab1" :class="computed_css_tab_selected(0)"><a href="javascript:void(0)" @click.stop="onclick_tab(0)"><span>최신 현황</span></a></li>
              <li data-tab="tab2" :class="computed_css_tab_selected(1)"><a href="javascript:void(0)" @click.stop="onclick_tab(1)"><span>상태 히스토리</span></a></li>
            </ul>

            <div :class="['tabcontent', computed_css_tab_selected(0)]" v-show="(0 == m_current_tab_idx)">
              <h6>최신현황 Tab</h6>

              <ul class="accordion">
                <!-- top table -->
                <table class="table-list popup_table_col">
                  <caption>최신현황 검색조건 Table</caption>
                  <colgroup>
                    <col style="width:10%;">
                    <col style="width:30%;">
                    <col style="width:10%;">
                    <col style="width:30%;">
                    <col style="width:10%;">
                    <col style="width:10%;">
                  </colgroup>
                  <tbody>
                    <tr>
                      <th>
                        <div>시스템</div>
                      </th>
                      <td>
                        <div>
                          <select class="inquiry_select" v-model="m_model_sel_sysId" @change="(m_model_sel_apiSpcNo = '')">
                            <option value="">전체</option>
                            <option v-for="option in m_ui_api_system_list" :value="(option.sys_id)">{{option.sys_nm}}</option>
                          </select>
                        </div>
                      </td>
                      <th>
                        <div>그룹</div>
                      </th>
                      <td>
                        <div class="dum-col_td_width">
                          <select class="inquiry_select" v-model="m_model_sel_statusGroupNo">
                            <option value="">전체</option>
                            <option v-for="option in m_ui_api_status_spc_group_list" :value="(option.status_group_no)">{{option.status_group_nm}}</option>
                          </select>
                        </div>
                      </td>
                      <th>
                        <div>상태</div>
                      </th>
                      <td>
                        <div>
                          <select class="inquiry_select" v-model="m_model_sel_statusCode">
                            <option value="">전체</option>
                            <option value="OK">정상</option>
                            <option value="DL">지연</option>
                            <option value="NK">오류</option>
                          </select>
                        </div>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <div>서비스</div>
                      </th>
                      <td>
                        <div>
                          <select class="inquiry_select" v-model="m_model_sel_apiSpcNo">
                            <option value="">전체</option>
                            <option v-for="option in m_ui_api_spc_list[m_model_sel_sysId]" :value="(option.api_spc_no)">{{option.api_spc_nm}}({{option.api_spc_ver}})</option>
                          </select>
                        </div>
                      </td>
                      <th>
                        <div>API</div>
                      </th>
                      <td colspan="3">
                        <div>
                          <input type="text" placeholder="검색어를 입력하세요" v-model="m_model_txt_apiNm">
                        </div>
                      </td>
                    </tr>
                  </tbody>
                </table>
                <div class="btn_set">
                  <button type="button" title="조회" class="btn btn_black" @click.stop="onclick_search_apiStatusInfoList()"><span>조회</span></button>
                </div>

                <!-- bottom table -->
                <div class="table_box_wrap mt50">
                  <div class="table_head">
                    <div class="top_left dashboard_list">
                      <div class="listBox popup_table">
                        <h5 class="dashboard_list_title">전체 : {{m_tc_apiStatusInfoList}} 건
                          <ol class="thislegend">
                            <li class="state_icons ico_confirm"><span>{{m_ui_ok_count}}</span></li>
                            <li class="state_icons ico_delay"><span>{{m_ui_dl_count}}</span></li>
                            <li class="state_icons ico_error"><span>{{m_ui_nk_count}}</span></li>
                          </ol>
                        </h5>
                      </div>
                    </div>
                    <div class="top_right dashboard_list">
                      <div class="listBox popup_table">
                        <div class="dashboard_list_title">
                          <ol class="thislegend">
                            <li class="state_icons ico_confirm"><span>정상 동작 중입니다.</span></li>
                            <li class="state_icons ico_delay"><span>응답이 지연되고 있습니다.</span></li>
                            <li class="state_icons ico_error"><span>API가 응답하지 않습니다.</span></li>
                          </ol>
                        </div>
                      </div>
                    </div>
                  </div>

                  <template>
                    <cus-vuetable ref="refTable_apiStatusInfoList" :fields="table_apiStatusInfoList.fields" :css="table_apiStatusInfoList.css.table"
                      :noDataTemplate="m_noDataTemplate" data-path="mydata" pagination-path="mypagination" track-by="PK"
                      :dum-table-height="((m_table_height_tr * table_apiStatusInfoList.pageRow) + 'px')"
                      :api-mode="true" :load-on-start="false" :api-url="computed_api_url_apiStatusInfoList" http-method="post" :http-options="m_axios_options"
                      :transform="prop_transform_apiStatusInfoList" :queryParams="prop_queryParams_apiStatusInfoList" :appendParams="m_prop_appendParams_apiStatusInfoList"
                      @vuetable:pagination-data="onPaginationData_apiStatusInfoList"
                    >
                      <template v-slot:slot_api_nm="props">
                        <a href="javascript:void(0)" @click.stop.prevent="onclick_row_action('popup-apistatus_check_hist_dialog', props.rowData)"><div class="lightBlue">{{props.rowData.api_nm}}</div></a>
                      </template>
                      <template v-slot:slot_status_code="props">
                        <div :class="['state_icons', computed_css_status_code(props.rowData.status_code)]"><span>{{props.rowData.status_code}}</span></div>
                      </template>
                    </cus-vuetable>
                    <cus-vuetable-pagination ref="refPagination_apiStatusInfoList" :css="table_apiStatusInfoList.css.pagination" :on-each-side="4" @vuetable-pagination:change-page="onChangePage_apiStatusInfoList" />
                  </template>
                </div><!-- .table_box_wrap -->
              </ul><!-- .accordion -->
            </div><!-- .tabcontent tab1 -->

            <div :class="['tabcontent', computed_css_tab_selected(1)]" v-show="(1 == m_current_tab_idx)">
              <h6>상태히스토리 Tab</h6>

              <ul class="accordion">
                <!-- top table -->
                <table class="table-list popup_table_col">
                  <caption>상태히스토리 검색조건 Table</caption>
                  <colgroup>
                    <col style="width:10%;">
                    <col style="width:30%">
                    <col style="width:10%;">
                    <col style="width:50%">
                  </colgroup>
                  <tbody>
                    <tr>
                      <th>
                        <div>시스템</div>
                      </th>
                      <td>
                        <div>
                          <select class="inquiry_select" v-model="m_model_sel_sysId_daily" @change="(m_model_sel_apiSpcNo_daily = '')">
                            <option value="">전체</option>
                            <option v-for="option in m_ui_api_system_list" :value="(option.sys_id)">{{option.sys_nm}}</option>
                          </select>
                        </div>
                      </td>
                      <th>
                        <div>그룹</div>
                      </th>
                      <td>
                        <div class="col_td_width">
                          <select class="inquiry_select" v-model="m_model_sel_statusGroupNo_daily">
                            <option value="">전체</option>
                            <option v-for="option in m_ui_api_status_spc_group_list" :value="(option.status_group_no)">{{option.status_group_nm}}</option>
                          </select>
                        </div>
                      </td>
                    </tr>
                    <tr>
                      <th>
                        <div>서비스</div>
                      </th>
                      <td>
                        <div>
                          <select class="inquiry_select" v-model="m_model_sel_apiSpcNo_daily">
                            <option value="">전체</option>
                            <option v-for="option in m_ui_api_spc_list[m_model_sel_sysId_daily]" :value="(option.api_spc_no)">{{option.api_spc_nm}}({{option.api_spc_ver}})</option>
                          </select>
                        </div>
                      </td>
                      <th>
                        <div>API</div>
                      </th>
                      <td>
                        <div>
                          <input type="text" placeholder="검색어를 입력하세요" v-model="m_model_txt_apiNm_daily">
                        </div>
                      </td>
                    </tr>
                  </tbody>
                </table>
                <div class="btn_set">
                  <button type="button" title="조회" class="btn btn_black" @click.stop="onclick_search_apiStatusInfoDailyList()"><span>조회</span></button>
                </div>

                <!-- bottom table -->
                <div class="table_box_wrap mt50">
                  <div class="table_head">
                    <div class="top_right dashboard_list">
                      <div class="listBox popup_table">
                        <div class="dashboard_list_title">
                          <ol class="thislegend">
                            <li class="state_icons ico_confirm"><span>정상 동작 중입니다.</span></li>
                            <li class="state_icons ico_delay"><span>응답이 지연되고 있습니다.</span></li>
                            <li class="state_icons ico_error"><span>API가 응답하지 않습니다.</span></li>
                          </ol>
                        </div>
                      </div>
                    </div>
                  </div>

                  <template>
                    <cus-vuetable ref="refTable_apiStatusInfoDailyList" :fields="table_apiStatusInfoDailyList.fields" :css="table_apiStatusInfoDailyList.css.table"
                      :noDataTemplate="m_noDataTemplate" data-path="mydata" pagination-path="mypagination" track-by="PK"
                      :dum-table-height="((m_table_height_tr * table_apiStatusInfoDailyList.pageRow) + 'px')"
                      :api-mode="true" :load-on-start="false" :api-url="computed_api_url_apiStatusInfoDailyList" http-method="post" :http-options="m_axios_options"
                      :transform="prop_transform_apiStatusInfoDailyList" :queryParams="prop_queryParams_apiStatusInfoDailyList" :appendParams="m_prop_appendParams_apiStatusInfoDailyList"
                      @vuetable:pagination-data="onPaginationData_apiStatusInfoDailyList"
                    >
                      <template v-slot:slot_api_nm="props">
                        <a href="javascript:void(0)" @click.stop.prevent="onclick_row_action('popup-apistatus_check_hist_dialog', props.rowData)"><div class="lightBlue">{{props.rowData.api_nm}}</div></a>
                      </template>
                      <template v-slot:slot_daily_status_code_0="props">
                        <div :class="['state_icons', computed_css_status_code(props.rowData.daily_status_code[0])]"><span>{{props.rowData.daily_status_code[0]}}</span></div>
                      </template>
                      <template v-slot:slot_daily_status_code_1="props">
                        <div :class="['state_icons', computed_css_status_code(props.rowData.daily_status_code[1])]"><span>{{props.rowData.daily_status_code[1]}}</span></div>
                      </template>
                      <template v-slot:slot_daily_status_code_2="props">
                        <div :class="['state_icons', computed_css_status_code(props.rowData.daily_status_code[2])]"><span>{{props.rowData.daily_status_code[2]}}</span></div>
                      </template>
                      <template v-slot:slot_daily_status_code_3="props">
                        <div :class="['state_icons', computed_css_status_code(props.rowData.daily_status_code[3])]"><span>{{props.rowData.daily_status_code[3]}}</span></div>
                      </template>
                      <template v-slot:slot_daily_status_code_4="props">
                        <div :class="['state_icons', computed_css_status_code(props.rowData.daily_status_code[4])]"><span>{{props.rowData.daily_status_code[4]}}</span></div>
                      </template>
                      <template v-slot:slot_daily_status_code_5="props">
                        <div :class="['state_icons', computed_css_status_code(props.rowData.daily_status_code[5])]"><span>{{props.rowData.daily_status_code[5]}}</span></div>
                      </template>
                      <template v-slot:slot_daily_status_code_6="props">
                        <div :class="['state_icons', computed_css_status_code(props.rowData.daily_status_code[6])]"><span>{{props.rowData.daily_status_code[6]}}</span></div>
                      </template>
                    </cus-vuetable>
                    <cus-vuetable-pagination ref="refPagination_apiStatusInfoDailyList" :css="table_apiStatusInfoDailyList.css.pagination" :on-each-side="4" @vuetable-pagination:change-page="onChangePage_apiStatusInfoDailyList" />
                  </template>
                </div><!-- .table_box_wrap -->
              </ul><!-- .accordion -->
            </div><!-- .tabcontent tab2 -->

          </div><!-- .regi_list regi_list_box -->
        </section>

      </div><!-- .pkg_board -->

    </div><!-- .regiApi_wrap -->
    <!-- 0920 작업 //-->

    <!-- popup-[API상태점검이력조회] -->
    <sc-vuemodal
      ref="modalApistatusCheckHistListDialog"
      name="modalApistatusCheckHistListDialog"
      width="580"
      height="620"
      :clickToClose="false"
      :draggable="false"
      :resizable="false"
      :scrollable="false"
      :adaptive="false"
      :minWidth="290"
      :minHeight="320"
    >
      <apistatus-check-hist-list-dialog
        ref="apistatusCheckHistListDialog"
        prop_name="modalApistatusCheckHistListDialog"
        :prop_Data="m_prop_apistatusCheckHistListData"
      >
      </apistatus-check-hist-list-dialog>
    </sc-vuemodal>

    <modal-dialog/>
    <!--// <div>{{m_vue_id}}</div> //-->
  </section>
</template>

<style scoped>
@import './../../css/cus-vuetable.css';
</style>
<style scoped>
.dum_white {color: #fff;}
</style>
