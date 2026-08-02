<comment>
  @lamplog view
    popup-[연동로그조회]
</comment>
<script>
import { CommonMixin } from '@/common/adptran.js';

export default {
  name: 'lamplogViewDialog',
  props: {
    prop_name: {
      type: String,
      required: true,
    },
    prop_Data: {
      type: Object,
      required: true,
      default: (() => ({ 'gw_profile': '', 'search_date': '', 'transaction_id': '', 'api_id': '', })),
      validator: function(value) {
        if ((value['gw_profile'] != 'TB') && (value['gw_profile'] != 'PROD')) { return false; }
        if (((value['search_date']||'').length != 8)) { return false; }
        return true;
      }
    },
  },
  mixins: [CommonMixin],
  data() {
    return {
      m_vue_id: 'lamplogViewDialog',

      //-- @constant {
      m_con_KEY_RESPONSE: 'Response',
      //-- @constant }
      //-- @biz-data {
      m_gw_profile: this.$sf_str(this.prop_Data['gw_profile']),
      m_search_date: this.$sf_str(this.prop_Data['search_date']),
      m_transaction_id: this.$sf_str(this.prop_Data['transaction_id']),
      m_api_id: this.$sf_str(this.prop_Data['api_id']),
      m_a_key_list: 'timestamp,serviceCode,operation,bizTransactionId,transactionId,logType,payload,caller,response,user,device,destination,url'.split(','),
      m_a_tit_list: '로그생성시간,서비스코드,오퍼레이션,비즈니스 거래번호,거래번호,로그유형,log payload,요청정보(channel/channelIp),응답정보,사용자 정보,단말정보,목적지정보,사용자접속 URL'.split(','),
      //-- @biz-data }

      //-- @ui-data {
      m_model_lamplog_data: [],
      //-- @ui-data }
      //-- @option {
      m_opt_is_disp_response: true,
      //-- @option }
    } // return{}
  }, // data()
  computed: {
  },
  watch: {
  },
  created: function () {
    this.$console_log('trace', 'created()');
    if (true == this.m_opt_is_disp_response) {
      this.m_a_key_list.push(this.m_con_KEY_RESPONSE);
      this.m_a_tit_list.push(this.m_con_KEY_RESPONSE);
    }
    this.fn_clear_lamplog_data();
  },
  mounted: function () {
    this.$console_log('trace', 'mounted()');
    this.fn_query_lamplogView();
  },
  updated: function () {
    this.$console_log('trace', 'updated()');
  },
  methods: {
    //-- @handler {
    //-- @handler }

    //-- @api function {
    //-- apigw_LampLog query
    fn_call_api_apigw_LampLog(req_gw_profile, req_search_date, req_transaction_id, req_api_id) {
      //- query callback
      let fn_callback = ((call_ret, response, request, result_data) => {
        this.$console_log('trace', 'fn_call_api_apigw_LampLog()', 'call_ret: ', call_ret, 'response :', response, 'request: ', request, 'result_data: ', result_data);

        //-- clear model
        this.fn_clear_lamplog_data();

        //-- ret: 'ok', 'nk', 'failed', 'catched'
        if ('ok' == call_ret) { //-- 호출성공(not 처리함수성공)
          /*--[ref]
                   호출 : this.$fn_call_api_common(): local script
                   처리함수: apigw_LampLog(): Controller
                   호출서비스: lampLogService.getByTransaction(): apigw Service
          --*/
          let response_fn_data = result_data['data']; //-- 호출-처리함수반환data
          let response_fn_resultCd = result_data['resultCd'];
          let response_fn_resultMsg = result_data['resultMsg'];
  
          let lampresponsebody_list = response_fn_data; //-- 호출-처리함수-apigw호출서비스반환data(LampResponseBody)
          //-- [i]n개가 아닌경우 단일object로 오는경우가 있음
          if (Array.isArray(lampresponsebody_list) == false) {
            lampresponsebody_list = [ lampresponsebody_list ];
          }
          let b_is_no_data = true;
  
          if (lampresponsebody_list.length > 0) {
            //-- IN_RES 선별
            //--[i]timestamp DESC정렬
            let response_IN_RES = lampresponsebody_list.find((item) => {
              return (this.$sf_obj_val(item, 'logType', '') == 'IN_RES');
            });
            if (response_IN_RES != null) {
              /*--[ref]
              List<LampResponseBody>
              response_IN_RES['timestamp']  // 로그생성시간(yyyy-MM-dd HH:mm:ss.SSS
              response_IN_RES['serviceCode']  // 서비스코드
              response_IN_RES['operation']  // 오퍼레이션
              response_IN_RES['bizTransactionId'] // 비즈니스 거래번호
              response_IN_RES['transactionId']  // 거래번호
              response_IN_RES['logType']  // 로그유형(IN_REQ, IN_RES, OUT_REQ, OUT_RES
              response_IN_RES['payload']  // log payload
              response_IN_RES['caller'] // Map<String, String> 요청정보(channel / channelIp)
              response_IN_RES['response'] // Map<String, String> 응답정보
              response_IN_RES['user']  // Map<String, String> 사용자 정보
              response_IN_RES['device']  // Map<String, String> 단말정보
              response_IN_RES['destination']  // Map<String, String> 목적지정보
              response_IN_RES['url']  // 사용자접속 URL
              --*/
              if (true == this.m_opt_is_disp_response) {
                //-- insert response json
                response_IN_RES[this.m_con_KEY_RESPONSE] = this.$sf_json_stringify(lampresponsebody_list, null, 2);
              }
              this.m_model_lamplog_data = [];
              this.m_a_key_list.forEach((key, idx) => {
                let value = ((this.$has_own(response_IN_RES, key) == true) ? response_IN_RES[key] : '');
                this.m_model_lamplog_data.push({ 'title': this.m_a_tit_list[idx], 'value' : value });
              });
              b_is_no_data = false;
            }
          }
          if (true == b_is_no_data) {
            let resultMessage = '입력하신 정보로 수신된 이력이 없습니다.';
            resultMessage += '\n\n[resultCd: ' + response_fn_resultCd + '][resultMsg: ' + response_fn_resultMsg + ']';
            this.$adpt_alert(resultMessage, '연동 로그 조회', false);
          }
        }
        else {
          this.$proc_api_resultCode_Fail(call_ret, response);
        }
      });
      let api_param = { 'gw_profile': req_gw_profile, 'search_date': req_search_date, 'transaction_id': req_transaction_id, 'api_id': req_api_id, };
      this.$fn_call_api_common(this.m_con_apigw_LampLog_url, api_param, 'post', fn_callback);
    },
    //-- @api function }

    //-- @function {
    //-- lamplog정보초기화
    fn_clear_lamplog_data() {
      this.m_model_lamplog_data = [];
      this.m_a_key_list.forEach((key, idx) => {
        this.m_model_lamplog_data.push({ 'title': this.m_a_tit_list[idx], 'value' : '' });
      });
    },
    //-- lamplog 검색
    fn_query_lamplogView() {
      this.fn_call_api_apigw_LampLog(this.m_gw_profile, this.m_search_date, this.m_transaction_id, this.m_api_id);
    },
    
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
      <span class="popup_header_title">연동 로그 조회</span>
      <button type="button" class="popup_header_button" role="button" @click.stop="fn_dialog_close()" title="Close">
        <span class="button_close">Close</span>
      </button>
    </div>
    <div class="pop_ver pop_ver2">
      <div class="popup_content distribution_cont">
        <div class="scroll_wrap">

          <div class="pkg_board">

            <table class="table-vw">
              <caption>연동 로그 조회 Table</caption>
              <colgroup>
                <col style="width:25%;">
                <col style="width:auto;">
              </colgroup>
              <thead>
                <tr>
                  <th>항목</th>
                  <th>내용</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(log_item, index) in m_model_lamplog_data" :key="index">
                  <th>{{log_item['title']}}</th>
                  <td><pre>{{log_item['value']}}</pre></td>
                </tr>
              </tbody>
            </table>
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
  /*add for modal dialog*/
  .pkg_board .table-vw {text-align:center}

  /*add for local*/
  .distribution_cont {height: calc(100% - 35px - 35px);}/*100% - padding - 버튼area*/
</style>
