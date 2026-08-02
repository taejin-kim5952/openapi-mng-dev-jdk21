<comment>
  API상황판 그룹보기
  /adptran/apistatus/group
</comment>
<script>
import { CommonMixin } from '@/common/adptran.js';
import { ApistatusCommonMixin } from '@/common/apistatus_common.js';
//-- popup-[API상태그룹사용자설정]
import apistatusGroupUserLinkDialog from '@approot/dialog/apistatusGroupUserLinkDialog.vue';
//-- popup-[API상태점검이력조회]
import apistatusCheckHistListDialog from '@approot/dialog/apistatusCheckHistListDialog.vue';

//-- group_api_status_info component {
import Vue from 'vue';

//--[drm][test] {
const con_dev_is_longtext_test = false; 
//--[drm][test] }

const groupApistatusInfo = Vue.component('group-apistatus-info', {
  props: {
    prop_status_group_no: { type: String, required: true, default: '', },
  },
  mixins: [CommonMixin, ApistatusCommonMixin],
  data() {
    return {
      m_vue_id: 'group_apistatus_info',
      //-- @ui-data {
      m_ui_status_group_nm: '',  //-- 그룹명
      m_ui_ok_count: '',  //-- 작동갯수
      m_ui_dl_count: '',  //-- 지연갯수
      m_ui_nk_count: '',  //-- 오류갯수
      m_ui_api_status_info_list: [], //-- API최근상태목록
      //-- @ui-data }
    } // return{}
  }, // data()
  computed: {
    computed_group_api_status_info() {
      return ((value, cmd) => {
        let s_info = '';
        if ('status_group_nm' == cmd) {
          s_info = value;
          //--[drm][test]
          if (con_dev_is_longtext_test) { s_info += s_info + s_info + s_info; }
        }
        else if (('ok_count' == cmd) || ('dl_count' == cmd) || ('nk_count' == cmd)) {
          let n_count = parseInt(value, 10);
          s_info = ((n_count >= 0) ? n_count : '-');
        }
        return s_info;
      });
    },
    computed_api_status_info() {
      return ((api_status_info, cmd) => {
        let s_info = '';
        if ('api_spc_nm' == cmd) {
          s_info = api_status_info['api_spc_nm'];
          //--[drm][test]
          if (con_dev_is_longtext_test) { s_info += s_info + s_info + s_info; }
        }
        else if ('api_nm' == cmd) {
          s_info = api_status_info['api_nm'];
          //--[drm][test]
          if (con_dev_is_longtext_test) { s_info += s_info + s_info + s_info; }
        }
        else if ('status_code' == cmd) {
          s_info = api_status_info['status_code'];
          if ('OK' == s_info) { s_info = '정상'; }
          else if ('DL' == s_info) { s_info = '지연'; }
          else if ('NK' == s_info) { s_info = '오류'; }
          else { s_info = '-'; }
        }
        else if ('status_code_css' == cmd) {
          s_info = api_status_info['status_code'];
          if ('OK' == s_info) { s_info = 'ico_confirm'; }
          else if ('DL' == s_info) { s_info = 'ico_delay'; }
          else if ('NK' == s_info) { s_info = 'ico_error'; }
          else { s_info = ''; }
        }
        else if ('status_res_msec' == cmd) {
          s_info = api_status_info['status_res_msec'];
          let n_msec = parseInt(s_info, 10);
          s_info = api_status_info['status_code'];
          if ((n_msec >= 0) && (('OK' == s_info) || ('DL' == s_info))) {
            s_info = ('<em>' + n_msec + '</em> ms');
          }
          else {
            s_info = '<em>-</em>';
          }
        }
        else if ('status_check_dt' == cmd) {
          s_info = api_status_info['status_check_dt'];
          s_info = s_info.substr(0, 10);
        }
        return s_info;
      });
    },
  },
  created: function () {
    this.$console_log('trace', 'created()');
  },
  mounted: function() {
    this.$console_log('trace', 'mounted()');
    let param = {'status_group_no': this.prop_status_group_no};
    this.fn_apicall_group_api_status_info(param);
  },
  updated: function() {
    this.$console_log('trace', 'updated()');
  },
  methods: {
    //-- @handler {
    //-- 일자별보기
    onclick_btn_daily_history(api_status_info, status_check_dt) {
      this.$emit('emit_popup_apistatus_check_hist_dialog', {
        'sys_nm': api_status_info['sys_nm'],
        'api_spc_nm': api_status_info['api_spc_nm'],
        'api_nm': api_status_info['api_nm'],
        'api_no': api_status_info['api_no'],
      })
    },
    //-- @handler }
    //-- @api function {
    //-- 상단그룹별요약정보 query
    fn_apicall_group_api_status_info(param) {
      this.$console_log('trace', 'fn_apicall_group_api_status_info()', 'param: ', param);

      //-- 초기화
      this.m_ui_status_group_nm = '';
      this.m_ui_ok_count = '';
      this.m_ui_dl_count = '';
      this.m_ui_nk_count = '';
      this.m_ui_api_status_info_list = [];

      let url = this.m_con_apistatus_group_group_api_status_info_url;
      this.$fn_call_apistatus_api(url, Object.assign(this.m_con_def_api_param, param), 'post', this.fn_apicb_group_api_status_info);
    },
    //-- api callback group_api_status_info
    fn_apicb_group_api_status_info(ret, response, request, payload) {
      this.$console_log('trace', 'fn_apicb_group_api_status_info()', 'ret: ', ret, 'response: ', response, 'request: ', request, 'payload: ', payload);
      //-- ret: 'ok', 'nk', 'failed', 'catched'
      if ('ok' == ret) {
        this.m_ui_status_group_nm = payload['status_group_nm'];
        this.m_ui_ok_count = payload['ok_count'];
        this.m_ui_dl_count = payload['dl_count'];
        this.m_ui_nk_count = payload['nk_count'];

        let data_list = payload['api_status_info_list'];
        if (Array.isArray(data_list) == false) {
          throw {'message': 'group_api_status_info: payload.api_status_info_list is not array'};
        }
        data_list.forEach((api_status_info) => {
          this.m_ui_api_status_info_list.push(api_status_info);
        });
      }
      else {
        this.$proc_api_resultCode_Fail(ret, response);
      }
    },
    //-- @api function }
    //-- @function {
    //-- @function }
  }, // methods:
  template: 
    `
    <section>
      <h5 class="dashboard_list_title">
        {{computed_group_api_status_info(m_ui_status_group_nm, 'status_group_nm')}}<!-- {{그룹명}}-->
        <ol class="thislegend">
          <li class="ico_confirm"><span>{{computed_group_api_status_info(m_ui_ok_count, 'ok_count')}}<!-- {{정상갯수}}--></span></li>
          <li class="ico_delay"><span>{{computed_group_api_status_info(m_ui_dl_count, 'dl_count')}}<!-- {{지연갯수}}--></span></li>
          <li class="ico_error"><span>{{computed_group_api_status_info(m_ui_nk_count, 'nk_count')}}<!-- {{오류갯수}}--></span></li>
        </ol>
      </h5>
      <div class="dashboard_list_inner">
        <div v-for="(api_status_info, index) in m_ui_api_status_info_list" :key="index">
          <div class="column01">
            <span :class="computed_api_status_info(api_status_info, 'status_code_css')"><!-- 정상/지연/오류 --></span><!-- [D]ico_confirm, ico_delay, ico_error-->
          </div><!-- .column01 -->
          <div class="column02">
            <div class="lst_title">
              <a href="javascript:void(0)" title="">{{computed_api_status_info(api_status_info, 'api_spc_nm')}}<!--{{API_SPC명}}--></a>
            </div>
            <div class="lst_subdata">
              <p class="name_api">{{computed_api_status_info(api_status_info, 'api_nm')}}<!--{{API명}}--></p>
              <p class="use_data">
                <span class="ms_data" v-html="computed_api_status_info(api_status_info, 'status_res_msec')"><!--{{응답시간}}--><!--[D]<em>32</em> ms--></span>
                <span class="date_data">{{computed_api_status_info(api_status_info, 'status_check_dt')}}<!--{{점검일자}}--></span>
              </p>
            </div>
          </div><!-- .column02 -->
          <div class="column03">
            <button type="button" title="일자별 보기" class="btn btn_sml btn_gray" @click.stop="onclick_btn_daily_history(api_status_info, computed_api_status_info(api_status_info, 'status_check_dt'))"><span>일자별 보기</span></button>
          </div><!-- .column03 -->
        </div>
      </div><!-- .dashboard_list_inner -->
    </section>
    `
});
//-- group_api_status_info component }

export default {
  name: 'apistatus_group',
  components: {
    'group-apistatus-info': groupApistatusInfo,
    'apistatus-group-user-link-dialog': apistatusGroupUserLinkDialog,
    'apistatus-check-hist-list-dialog': apistatusCheckHistListDialog,
  },
  mixins: [CommonMixin, ApistatusCommonMixin],
  props: { },
  data() {
    return {
      m_vue_id: 'apistatus_group',
      //-- @property {
      m_prop_apistatusGroupUserLinkData: {},  //-- modalApistatusGroupUserLinkDialog prop_data
      m_prop_apistatusCheckHistListData: {},  //-- modalApistatusCheckHistListDialog prop_data
      //-- @property }
      //-- @constant {
      m_con_show_user_link_only: true,  //-- user_link지정정보만 출력여부
      m_con_group_summary_count: 5,  //-- 출력API상태갯수
      //-- @constant }
      //-- @biz-data {
      m_api_status_spc_group_list: [], //-- API상태그룹
      m_status_group_no_list: [], //-- 상단그룹번호(정렬)
      //-- @biz-data }
      //-- @ui-data {
      m_ui_group_summary_list: [], //-- 상단그룹요약정보
      //-- @ui-data }
    } // return{}
  }, // data ()
  computed: {
    computed_group_summary_info() {
      return ((group_summary_info, cmd) => {
        let s_info = '';
        if ('status_group_nm' == cmd) {
          s_info = group_summary_info['status_group_nm'];
          //--[drm][test]
          if (con_dev_is_longtext_test) { s_info += s_info + s_info + s_info; }
        }
        else if ('status_ok_rate' == cmd) {
          s_info = group_summary_info['status_ok_rate'];
          let n_rate = parseInt(s_info, 10);
          s_info = (((n_rate >= 0) && (n_rate <= 100)) ? (n_rate + '<em>%</em>') : '<em>-</em>');
        }
        else if ('status_group_color_css' == cmd) {
          //-- redColor skyColor orangeColor greenColor puppleColor blueColor
          s_info = group_summary_info['status_ok_rate'];
          let n_rate = parseInt(s_info, 10);
          if (n_rate >= 100) {
            s_info = 'greenColor';
          }
          else if ((n_rate >= 90) && (n_rate < 100)) {
            s_info = 'orangeColor';
          }
          else {
            s_info = 'redColor';
          }
        }
        return s_info;
      });
    },
  },
  created: function() {
    this.$console_log('trace', 'created()');
  },
  mounted: function() {
    this.$console_log('trace', 'mounted()');
    let param = {};
    //--[drm][test]
    //--##param = { 'user_id': 'demo_user' };
    this.fn_apicall_api_status_spc_group_list(param)
  },
  updated: function() {
    this.$console_log('trace', 'updated()');
  },
  methods: {
    //-- @handler {
    //-- API상황판 목록보기
    onclick_link_apistatus_list() {
      this.$fn_go_location('/apidev/adptran/apistatus/list');
    },
    //-- group사용자설정
    onclick_btn_group_user_link() {
      this.m_prop_apistatusGroupUserLinkData = { 'group_link_count': this.m_con_group_summary_count, 'api_status_spc_group_list': this.m_api_status_spc_group_list };
      this.$modal.show('modalApistatusGroupUserLinkDialog');
    },
    //-- @handler }
    //-- @api function {
    //-- API상태그룹 query
    fn_apicall_api_status_spc_group_list(param) {
      this.$console_log('trace', 'fn_apicall_api_status_spc_group_list()', 'param: ', param);

      //-- 초기화
      this.m_api_status_spc_group_list = [];
      this.m_status_group_no_list = [];
      this.m_ui_group_summary_list = [];

      let url = this.m_con_apistatus_group_api_status_spc_group_list_url;
      this.$fn_call_apistatus_api(url, Object.assign(this.m_con_def_api_param, param), 'post', this.fn_apicb_api_status_spc_group_list);
    },
    //-- api callback api_status_spc_group_list
    fn_apicb_api_status_spc_group_list(ret, response, request, payload) {
      this.$console_log('trace', 'fn_apicb_api_status_spc_group_list()', 'ret: ', ret, 'response: ', response, 'request: ', request, 'payload: ', payload);
      //-- ret: 'ok', 'nk', 'failed', 'catched'
      if ('ok' == ret) {
        let data_list = payload;
        if (Array.isArray(data_list) == false) {
          throw {'message': 'api_status_spc_group_list: payload is not array'};
        }
        let arr_api_status_spc_group_list = [];
        data_list.forEach((api_status_spc_group) => {
          //-- user_link_only모드 설정에 따라 해당정보 filter
          let b_is_push = ((false == this.m_con_show_user_link_only) ? true : (('Y' == api_status_spc_group['user_link_yn']) ? true : false));
          if (true == b_is_push) {
            arr_api_status_spc_group_list.push(api_status_spc_group);
          }
          this.m_api_status_spc_group_list.push(api_status_spc_group);
        });

        //-- 출력대상 filter
        for (let n_ii = 0; n_ii < arr_api_status_spc_group_list.length; n_ii++) {
          if (this.m_status_group_no_list.length >= this.m_con_group_summary_count) {
            break;
          }
          this.m_status_group_no_list.push(arr_api_status_spc_group_list[n_ii]['status_group_no']);
        }

        //-- 상단그룹별요약정보 query
        if (this.m_status_group_no_list.length > 0) {
          let param = { 'status_group_no_list': this.m_status_group_no_list.join(',') };
          this.fn_apicall_group_summary_list(param);
        }
        else {
          this.onclick_btn_group_user_link();
        }
      }
      else {
        this.$proc_api_resultCode_Fail(ret, response);
      }
    },
    
    //-- 상단그룹별요약정보 query
    fn_apicall_group_summary_list(param) {
      this.$console_log('trace', 'fn_apicall_group_summary_list()', 'param: ', param);

      //-- 초기화
      this.m_ui_group_summary_list = [];
      
      let url = this.m_con_apistatus_group_group_summary_list_url;
      this.$fn_call_apistatus_api(url, Object.assign(this.m_con_def_api_param, param), 'post', this.fn_apicb_group_summary_list);
    },
    //-- api callback group_summary_list
    fn_apicb_group_summary_list(ret, response, request, payload) {
      this.$console_log('trace', 'fn_apicb_group_summary_list()', 'ret: ', ret, 'response: ', response, 'request: ', request, 'payload: ', payload);
      //-- ret: 'ok', 'nk', 'failed', 'catched'
      if ('ok' == ret) {
        let data_list = payload;
        if (Array.isArray(data_list) == false) {
          throw {'message': 'group_summary_list: payload is not array'};
        }
        //-- group정렬 순서대로 추가
        data_list.forEach((group_summary_info) => {
          let status_group_no = group_summary_info['status_group_no'];
          let n_idx = this.m_status_group_no_list.indexOf(status_group_no);
          n_idx = ((n_idx == -1) ? 999 : n_idx);  //-- 없을시 맨뒤(999)로
          group_summary_info['order_idx'] = n_idx; 
          this.m_ui_group_summary_list.push(group_summary_info);
        });
        //-- group sorting
        this.m_ui_group_summary_list.sort(function(item_a, item_b) {
          let order_a = item_a['order_idx'];
          let order_b = item_b['order_idx'];
          return ((order_a < order_b) ? -1 : ((order_a > order_b) ? 1: 0));
        });
      }
      else {
        this.$proc_api_resultCode_Fail(ret, response);
      }
    },
    //-- @api function }
    //-- @function {
    emit_popup_apistatus_check_hist_dialog(arg) {
      this.$console_log('trace', 'emit_popup_apistatus_check_hist_dialog.', 'arg: ', arg);
      this.m_prop_apistatusCheckHistListData = arg;
      this.$modal.show('modalApistatusCheckHistListDialog');
    },
    emit_proc_finished_save(arg) {
      this.$console_log('trace', 'emit_proc_finished_save.', 'arg: ', arg);
      //--[ref] let ret_data = { 'api_status_spc_group_user_link_list': this.m_ui_api_status_spc_group_user_link_list };
      let param = {};
      this.fn_apicall_api_status_spc_group_list(param)
    }
    //-- @function }
  } // methods:
};
</script>

<template>
  <section>

    <!--// 0920 작업 -->
    <div class="regiApi_wrap">
      <div class="pkg_board">
        <section><!--// 개발 요청 내용 -->
          <h4 class="brd_title underline">그룹별 API 모니터링
            <button type="button" title="전체조회" class="btn_fr btn btn_black" @click.stop="onclick_link_apistatus_list()"><span>전체조회</span></button>
            <button type="button" title="그룹설정" class="btn_fr btn btn_white" @click.stop="onclick_btn_group_user_link()"><span>그룹설정</span></button>
          </h4>
          <div class="dashboard_graph">
            <ol id="id_ol_group_sortable">
              <li v-for="(group_summary_item, index) in m_ui_group_summary_list" :key="index"><!--// loop //-->
                <div :class="['donutGraph', computed_group_summary_info(group_summary_item, 'status_group_color_css')]"><!-- [D]redColor skyColor orangeColor greenColor puppleColor blueColor -->
                  <div>
                    <span class="dp_none">{{group_summary_item['status_group_no']}}</span>
                    <p>{{computed_group_summary_info(group_summary_item, 'status_group_nm')}}<!-- {{그룹명}}--></p>
                    <strong v-html="computed_group_summary_info(group_summary_item, 'status_ok_rate')"><!-- {{정상비율}}--><!--[D]100<em>%</em>--></strong>
                  </div>
                </div>
              </li>
            </ol>
          </div><!-- .dashboard_graph -->

          <div class="dashboard_list">
            <div class="listBox" v-for="(group_summary_item, index) in m_ui_group_summary_list" :key="index"><!--// loop //-->
              <group-apistatus-info :prop_status_group_no="group_summary_item['status_group_no']" @emit_popup_apistatus_check_hist_dialog="emit_popup_apistatus_check_hist_dialog"></group-apistatus-info>
            </div><!-- .listBox -->
          </div><!-- .dashboard_list -->
        </section><!-- 개발 요청 내용 //-->
      </div><!-- .pkg_board -->
    </div><!-- .regiApi_wrap -->
    <!-- 0920 작업 //-->

    <!-- popup-[API상태그룹사용자설정] -->
    <sc-vuemodal
      ref="modalApistatusGroupUserLinkDialog"
      name="modalApistatusGroupUserLinkDialog"
      width="840"
      height="620"
      :clickToClose="false"
      :draggable="false"
      :resizable="false"
      :scrollable="false"
      :adaptive="false"
      :minWidth="420"
      :minHeight="300"
    >
      <apistatus-group-user-link-dialog
        ref="apistatusGroupUserLinkDialog"
        prop_name="modalApistatusGroupUserLinkDialog"
        :prop_Data="m_prop_apistatusGroupUserLinkData"
        @emit_proc_finished_save="emit_proc_finished_save"
      >
      </apistatus-group-user-link-dialog>
    </sc-vuemodal>

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
</style>
