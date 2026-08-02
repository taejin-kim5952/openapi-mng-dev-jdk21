<comment>
  popup-[API상태점검이력상세조회]
</comment>
<script>
import { CommonMixin } from '@/common/adptran.js';
import { ApistatusCommonMixin } from '@/common/apistatus_common.js';
//-- popup-[연동로그조회]
import lamplogViewDialog from '@approot/dialog/lamplogViewDialog.vue';

export default {
  name: 'apistatusCheckHistViewDialog',
  props: {
    prop_name: {
      type: String,
      required: true,
    },
   prop_Data: {
      type: Object,
      required: true,
      default: (() => ({ 'seq': '', gw_profile: '', })),
      validator: function(value) {
        if ((parseInt(value['seq'], 10) > 0) == false) { return false; }
        return true;
      }
    },
  },
  components: {
    'lamplog-view-dialog': lamplogViewDialog,
  },
  mixins: [CommonMixin, ApistatusCommonMixin],
  data() {
    return {
      m_vue_id: 'apistatusCheckHistViewDialog',
      //-- @property {
      m_prop_lamplogViewData: {},
      //-- @property }
      //-- @biz-data {
      m_seq: this.$sf_str(this.prop_Data['seq']),
      m_gw_profile: this.$sf_str(this.prop_Data['gw_profile']),
      //-- @biz-data }
      //-- @ui-data {
      m_data_item: {},
      //-- @ui-data }
    } // return{}
  }, // data()
  computed: {
    computed_fmt_data() {
      return ((cmd) => {
        let api_status_check_hist = this.m_data_item;
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
            s_info = (n_msec + ' ms');
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
    if ((this.m_gw_profile != 'TB') && (this.m_gw_profile != 'PROD')) {
      this.m_gw_profile = ''; //-- view_lamplog hidden 
    }
  },
  mounted: function () {
    this.$console_log('trace', 'mounted()');
    let param = {'seq': this.m_seq};
    this.fn_apicall_api_status_check_hist(param);
  },
  updated: function () {
    this.$console_log('trace', 'updated()');
  },
  methods: {
    //-- @handler {
    fn_view_lamplog() {
      let req_gw_profile = this.m_gw_profile;  //-- 'PROD', 'TB'
      let req_search_date = this.$fmt_data(this.m_data_item['status_check_dt'], 'fmt_date_02'); //-- yyyymmdd
      let req_transaction_id = this.$sf_str(this.m_data_item['res_transaction_id']);
      let req_api_id = '';  //-- optional // not exsit
      
      if (req_gw_profile.length == 0) {
        this.$adpt_alert('로그검색 정보가 없습니다. - gw_profile', '연동 로그 조회', false); return;
      }
      if (req_search_date.length == 0) {
        this.$adpt_alert('로그검색 정보가 없습니다. - verification_dt', '연동 로그 조회', false); return;
      }
      if (req_transaction_id.length == 0) {
        this.$adpt_alert('로그검색 정보가 없습니다. - transaction_id', '연동 로그 조회', false); return;
        //--[drm][ing]
        //-- e.g.#1
        //--req_transaction_id = 'f851bc65-4963-42a2-b8fa-b4804df50d15';
        //--req_search_date = '20190603';
        //-- e.g.#2        
        //--req_transaction_id = '837d85d1-be27-4b80-a86e-7b220763860d';
        //--req_search_date = '20190531';
       }
      //--@@if (req_api_id.length == 0) { this.$adpt_alert('로그검색 정보가 없습니다. - api_id', '연동 로그 조회', false); return; }

      this.m_prop_lamplogViewData = { 'gw_profile': req_gw_profile, 'search_date': req_search_date, 'transaction_id': req_transaction_id, 'api_id': req_api_id };
      this.$modal.show('modalLamplogViewDialog');
    },
    //-- @handler }

    //-- @api function {
    //-- API상태점검이력 query
    fn_apicall_api_status_check_hist(param) {
      this.$console_log('trace', 'fn_apicall_api_status_check_hist()', 'param: ', param);
      
      //-- 초기화
      this.m_data_item = {};

      let url = this.m_con_apistatus_view_api_status_check_hist_url;
      this.$fn_call_apistatus_api(url, Object.assign(this.m_con_def_api_param, param), 'post', this.fn_apicb_api_status_check_hist);
    },
    //-- api callback api_status_check_hist
    fn_apicb_api_status_check_hist(ret, response, request, payload) {
      this.$console_log('trace', 'fn_apicb_api_status_check_hist()', 'ret: ', ret, 'response: ', response, 'request: ', request, 'payload: ', payload);
      //-- ret: 'ok', 'nk', 'failed', 'catched'
      if ('ok' == ret) {
        this.m_data_item = payload;
        //--[todo]
      }
      else {
        this.$proc_api_resultCode_Fail(ret, response);
      }
    },
    //-- @api function }
    //-- @function {
    fn_dialog_close() {
      this.$modal.hide(this.prop_name);
      let ret_data = { 'verify_seq': this.m_verify_seq };
      this.$emit('emit_proc_finished', ret_data);
    },
    //-- @function }
  } // methods:
};
</script>

<template>
  <div class="popup_wrap">
    <div class="popup_header">
      <span class="popup_header_title">API상태점검이력상세조회</span>
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
                  <col style="width:15%;">
                  <col style="width:auto;">
                </colgroup>
                <tbody> 
                  <tr>
                    <th><div>시스템</div></th>
                    <td><div>{{m_data_item['sys_nm']}}</div></td>
                  </tr>
                  <tr>
                    <th><div>서비스</div></th>
                    <td><div>{{m_data_item['api_spc_nm']}}</div></td>
                  </tr>
                  <tr>
                    <th><div>API명</div></th>
                    <td><div>{{m_data_item['api_nm']}}</div></td>
                  </tr>
                </tbody>
              </table><!-- .table-list popup_table_col -->

              <h3>상태점검결과</h3>
              <table class="table-list popup_table_col">
                <caption>API상태점검이력정보 Table</caption>
                <colgroup>
                  <col style="width:15%;">
                  <col style="width:auto;">
                  <col style="width:15%;">
                  <col style="width:auto;">
                </colgroup>
                <tbody> 
                  <tr>
                    <th><div>점검일시</div></th>
                    <td><div>{{computed_fmt_data('status_check_dt')}}</div></td>
                    <td colspan="2" class="view_lamplog">
                      <template v-if="(m_gw_profile.length > 0)">
                        <a class="btn7 btn_gray" href="javascript:void(0)" @click.stop="fn_view_lamplog()">연동 로그 조회</a>
  
                        <!-- popup-[연동로그조회] -->
                        <sc-vuemodal
                          ref="modalLamplogViewDialog"
                          name="modalLamplogViewDialog"
                          width="800"
                          height="600"
                          :clickToClose="false"
                          :draggable="false"
                          :resizable="false"
                          :scrollable="false"
                          :adaptive="false"
                          :minWidth="400"
                          :minHeight="300"
                        >
                          <lamplog-view-dialog
                            ref="lamplogViewDialog"
                            prop_name="modalLamplogViewDialog"
                            :prop_Data="m_prop_lamplogViewData"
                          >
                          </lamplog-view-dialog>
                        </sc-vuemodal>
                      </template>
                      
                    </td>
                  </tr>
                  <tr>
                    <th><div>상태</div></th>
                    <td><div :class="['state_icons', computed_fmt_data('status_code_css')]"></span></div><span class="txt_state_code">{{computed_fmt_data('status_code')}}</span></td>
                    <th><div>응답시간(ms)</div></th>
                    <td><div>{{computed_fmt_data('status_res_msec')}}</div></td>
                  </tr>
                  <tr>
                    <th><div>호출시작일시</div></th>
                    <td><div>{{m_data_item['st_time']}}</div></td>
                    <th><div>호출종료일시</div></th>
                    <td><div>{{m_data_item['end_time']}}</div></td>
                  </tr>
                  <tr>
                    <th><div>결과코드</div></th>
                    <td><div>{{m_data_item['proc_result_cd']}}</div></td>
                    <th><div>결과메시지</div></th>
                    <td><div>{{m_data_item['proc_result_msg']}}</div></td>
                  </tr>
                </tbody>
              </table><!-- .table-list popup_table_col -->

              <h3>Request</h3>
              <table class="table-list popup_table_col">
                <caption>API상태점검요청정보 Table</caption>
                <colgroup>
                  <col style="width:15%;">
                  <col style="width:auto;">
                  <col style="width:15%;">
                  <col style="width:auto;">
                </colgroup>
                <tbody> 
                  <tr>
                    <th><div>요청URL</div></th>
                    <td colspan="3"><div>{{m_data_item['req_api_url']}}</div></td>
                  </tr>
                  <tr>
                    <th><div>요청Header</div></th>
                    <td colspan="3"><div>{{m_data_item['req_headers']}}</div></td>
                  </tr>
                  <tr>
                    <th><div>요청Body</div></th>
                    <td colspan="3"><div>{{m_data_item['req_body']}}</div></td>
                  </tr>
                  <tr>
                    <th><div>TransactionId</div></th>
                    <td><div>{{m_data_item['req_transaction_id']}}</div></td>
                    <th><div>SequenceNo</div></th>
                    <td><div>{{m_data_item['req_sequence_no']}}</div></td>
                  </tr>
                </tbody>
              </table><!-- .table-list popup_table_col -->

              <h3>Response</h3>
              <table class="table-list popup_table_col">
                <caption>API상태점검응답정보 Table</caption>
                <colgroup>
                  <col style="width:15%;">
                  <col style="width:auto;">
                  <col style="width:15%;">
                  <col style="width:auto;">
                </colgroup>
                <tbody> 
                  <tr>
                    <th><div>TransactionId</div></th>
                    <td><div>{{m_data_item['res_transaction_id']}}</div></td>
                    <th><div>SequenceNo</div></th>
                    <td><div>{{m_data_item['res_sequence_no']}}</div></td>
                  </tr>
                  <tr>
                    <th><div>결과코드</div></th>
                    <td><div>{{m_data_item['res_return_cd']}}</div></td>
                    <th><div>결과메시지</div></th>
                    <td><div>{{m_data_item['res_return_description']}}</div></td>
                  </tr>
                  <tr>
                    <th><div>오류코드</div></th>
                    <td><div>{{m_data_item['res_error_cd']}}</div></td>
                    <th><div>오류메시지</div></th>
                    <td><div>{{m_data_item['res_error_description']}}</div></td>
                  </tr>
                  <tr>
                    <th><div>응답전문</div></th>
                    <td colspan="3"><div>{{m_data_item['res_response']}}</div></td>
                  </tr>
                </tbody>
              </table><!-- .table-list popup_table_col -->
            </section>
          </div><!-- .pkg_board -->
        </div><!-- .scroll_wrap -->
        
        <div class="brd_tp process_btn">
          <button type="button" class="btn btn_sml" @click.stop="fn_dialog_close()" title="닫기">닫기</button>
        </div>
        
      </div><!-- .popup_content -->
    </div><!--.pop_ver pop_ver2 -->
  </div><!-- .popup_wrap -->
</template>

<style scoped>
  /*add for local*/
  .distribution_cont {height: calc(100% - 35px - 35px);}/*100% - padding - 버튼area*/
  .table-list span.txt_state_code {position:relative; top:-7px; margin-left:10px;}
  .table-list .view_lamplog {text-align: center;}
  .table-list .view_lamplog a {display: inline-block; text-decoration:none; text-align: center; float:center;}
  .table-list .view_lamplog a:hover {text-decoration:none;}
  
</style>
