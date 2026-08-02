<comment>
  popup-[API상태점검이력목록조회]
</comment>
<script>
import { CommonMixin } from '@/common/adptran.js';
import { ApistatusCommonMixin } from '@/common/apistatus_common.js';
//-- popup-[API상태점검이력상세조회]
import apistatusCheckHistViewDialog from '@approot/dialog/apistatusCheckHistViewDialog.vue';

export default {
  name: 'apistatusCheckHistListDialog',
  props: {
    prop_name: {
      type: String,
      required: true,
    },
    prop_Data: {
      type: Object,
      required: true,
      default: (() => ({ 'sys_nm': '', 'api_spc_nm': '', 'api_nm': '', 'api_no': '', })),
      validator: function(value) {
        if ((parseInt(value['api_no'], 10) > 0) == false) { return false; }
        return true;
      }
    },
  },
  components: {
    'apistatus-check-hist-view-dialog': apistatusCheckHistViewDialog,
  },
  mixins: [CommonMixin, ApistatusCommonMixin],
  data() {
    return {
      m_vue_id: 'apistatusCheckHistListDialog',
      //-- @property {
      m_prop_apistatusCheckHistViewData: {},  //-- apistatusCheckHistViewDialog prop_data
      //-- @property }
      //-- @constant {
      m_con_status_check_dt_days: 30,  //-- API상태점검이력 검색기간(일)
      //-- @constant }
      //-- @biz-data {
      m_api_no: this.$sf_str(this.prop_Data['api_no']),
      //-- @biz-data }

      //-- @ui-data {
      m_ui_sys_nm: this.$sf_str(this.prop_Data['sys_nm']),
      m_ui_api_spc_nm: this.$sf_str(this.prop_Data['api_spc_nm']),
      m_ui_api_nm: this.$sf_str(this.prop_Data['api_nm']),

      m_ui_api_status_check_hist_list: [],
      //-- @ui-data }
    } // return{}
  }, // data()
  computed: {
    computed_api_status_check_hist() {
      return ((api_status_check_hist, cmd) => {
        let s_info = '';
        if ('status_check_dt' == cmd) {
          s_info = api_status_check_hist['status_check_dt'];
          //--##s_info = s_info.substr(0, 10);
        }
        else if ('status_code' == cmd) {
          s_info = api_status_check_hist['status_code'];
          if ('OK' == s_info) { s_info = '정상'; }
          else if ('DL' == s_info) { s_info = '지연'; }
          else if ('NK' == s_info) { s_info = '오류'; }
          else { s_info = '-'; }
        }
        else if ('status_code_css' == cmd) {
          s_info = api_status_check_hist['status_code'];
          if ('OK' == s_info) { s_info = 'ico_confirm'; }
          else if ('DL' == s_info) { s_info = 'ico_delay'; }
          else if ('NK' == s_info) { s_info = 'ico_error'; }
          else { s_info = ''; }
        }
        else if ('status_res_msec' == cmd) {
          s_info = api_status_check_hist['status_res_msec'];
          let n_msec = parseInt(s_info, 10);
          s_info = api_status_check_hist['status_code'];
          if ((n_msec >= 0) && (('OK' == s_info) || ('DL' == s_info))) {
            s_info = (n_msec + 'ms');
          }
          else {
            s_info = '-';
          }
        }
        return s_info;
      });
    },
  },
  watch: {
  },
  created: function () {
    this.$console_log('trace', 'created()');
  },
  mounted: function () {
    this.$console_log('trace', 'mounted()');
    let param = {'api_no': this.m_api_no, 'status_check_dt_days': this.m_con_status_check_dt_days};
    this.fn_apicall_api_status_check_hist_list(param);
  },
  updated: function () {
    this.$console_log('trace', 'updated()');
  },
  methods: {
    //-- @handler {
    //-- 상세정보보기
    onclick_btn_api_status_check_hist(api_status_check_hist) {
      //--##this.$adpt_alert('service not yet', '알림', true);
      //--##this.$modal.hide(this.prop_name);
      this.m_prop_apistatusCheckHistViewData = Object.assign(api_status_check_hist, {'gw_profile': this.m_con_gw_profile_lamplog}); 
      this.$modal.show('apistatusCheckHistViewDialog');
    },
    //-- @handler }

    //-- @api function {
    //-- API상태점검이력목록 query
    fn_apicall_api_status_check_hist_list(param) {
      this.$console_log('trace', 'fn_apicall_api_status_check_hist_list()', 'param: ', param);

      //--[drm][test]
      //--##param = Object.assign(param, {'dev_opt': 'use_static_data'})

      //-- 초기화
      this.m_ui_api_status_check_hist_list = [];

      let url = this.m_con_apistatus_list_api_status_check_hist_list_url;
      this.$fn_call_apistatus_api(url, Object.assign(this.m_con_def_api_param, param), 'post', this.fn_apicb_api_status_check_hist_list);
    },
    //-- api callback api_status_check_hist_list
    fn_apicb_api_status_check_hist_list(ret, response, request, payload) {
      this.$console_log('trace', 'fn_apicb_api_status_check_hist_list()', 'ret: ', ret, 'response: ', response, 'request: ', request, 'payload: ', payload);
      //-- ret: 'ok', 'nk', 'failed', 'catched'
      if ('ok' == ret) {
        let data_list = payload;
        if (Array.isArray(data_list) == false) {
          throw {'message': 'api_api_status_check_hist_list: payload is not array'};
        }
        data_list.forEach((api_status_check_hist) => {
          this.m_ui_api_status_check_hist_list.push(api_status_check_hist);
        });
      }
      else {
        this.$proc_api_resultCode_Fail(ret, response);
      }
    },
    //-- @api function }

    //-- @function {
    fn_dialog_close() {
      this.$modal.hide(this.prop_name);
    },
    //-- @function }
  } // methods:
};
</script>

<template>
  <div class="popup_wrap">
    <div class="popup_header">
      <span class="popup_header_title">API상태점검 이력 조회</span>
      <button type="button" class="popup_header_button" role="button" @click.stop="fn_dialog_close()" title="Close">
        <span class="button_close">Close</span>
      </button>
    </div>
    <div class="pop_ver pop_ver2">
      <div class="popup_content distribution_cont">
        <div class="scroll_wrap">
          <div class="pkg_board">
            <section>
              <table class="table-list popup_table_col">
                <caption>API정보 Table</caption>
                <colgroup>
                  <col style="width:20%;">
                  <col style="width:auto;">
                </colgroup>
                <tbody> 
                  <tr>
                    <th><div>시스템</div></th>
                    <td><div>{{m_ui_sys_nm}}</div></td>
                  </tr>
                  <tr>
                    <th><div>서비스</div></th>
                    <td><div>{{m_ui_api_spc_nm}}</div></td>
                  </tr>
                  <tr>
                    <th><div>API명</div></th>
                    <td><div>{{m_ui_api_nm}}</div></td>
                  </tr>
                </tbody>
              </table><!-- .table-list popup_table_col -->

              <table class="table-list popup_table">
                <caption>API상태점검이력목록 Table</caption>
                <colgroup>
                  <col style="width:40%;">
                  <col style="width:auto;">
                  <col style="width:auto;">
                  <col style="width:auto;">
                </colgroup>
                <thead>
                  <tr>
                    <th>점검일시</th>
                    <th>상태</th>
                    <th>응답시간</th>
                    <th>상세정보</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(api_status_check_hist, index) in m_ui_api_status_check_hist_list" :key="index">
                    <td><div class="date">{{computed_api_status_check_hist(api_status_check_hist, 'status_check_dt')}}</div></td>
                    <td>
                      <div :class="['state_icons', computed_api_status_check_hist(api_status_check_hist, 'status_code_css')]"></span><span>{{computed_api_status_check_hist(api_status_check_hist, 'status_code')}}</span></div>
                    </td>
                    <td><div class="speed">{{computed_api_status_check_hist(api_status_check_hist, 'status_res_msec')}}</div></td>
                    <td>
                      <div v-if="api_status_check_hist['seq'] > 0"><button type="button" title="상세정보 보기" class="btn btn_sml btn_gray" @click.stop="onclick_btn_api_status_check_hist(api_status_check_hist)"><span>보기</span></button></div>

                    </td>
                  </tr>
                </tbody>
              </table><!-- .table-list popup_table -->
            </section>
          </div><!-- .pkg_board -->
        </div><!-- .scroll_wrap -->
        
        <div class="brd_tp process_btn">
          <button type="button" class="btn btn_sml" @click.stop="fn_dialog_close()" title="닫기">닫기</button>
        </div>
        
      </div><!-- .popup_content -->
    </div><!--.pop_ver pop_ver2 -->

    <!-- popup-[API상태점검이력상세조회] -->
    <sc-vuemodal
      ref="apistatusCheckHistViewDialog"
      name="apistatusCheckHistViewDialog"
      width="860"
      height="780"
      :clickToClose="false"
      :draggable="false"
      :resizable="false"
      :scrollable="false"
      :adaptive="false"
      :minWidth="430"
      :minHeight="390"
    >
      <apistatus-check-hist-view-dialog
        ref="apistatusCheckHistListDialog"
        prop_name="apistatusCheckHistViewDialog"
        :prop_Data="m_prop_apistatusCheckHistViewData"
      >
      </apistatus-check-hist-view-dialog>
    </sc-vuemodal>

  </div><!-- .popup_wrap -->
</template>

<style scoped>
  /*add for local*/
  .distribution_cont {height: calc(100% - 35px - 35px);}/*100% - padding - 버튼area*/
</style>
