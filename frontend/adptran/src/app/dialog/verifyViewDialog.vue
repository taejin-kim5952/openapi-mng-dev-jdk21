<comment>
  @verify view
    popup-[verify상세보기]
</comment>
<script>
import { CommonMixin } from '@/common/adptran.js';
import { fn_api_paramtest_object } from '@/common/fn_api_paramtest_object.js';
import VueJsonPretty from 'vue-json-pretty'
//-- popup-[연동로그조회]
import lamplogViewDialog from '@approot/dialog/lamplogViewDialog.vue';

//-- param-edit component {
import Vue from 'vue';
// inline component with template string :+1:
const paramEdit = Vue.component('param-edit', {
  props: {
    prop_paramGroup: { type: String, required: true, default: '', },
    prop_paramRs: { type: Array, required: true, default: (() => []), },
    prop_paramLink: { type: Array, required: true, default: (() => []), },
    prop_readonly: { type: Boolean, required: true, default: false, },
  },
  mixins: [CommonMixin],
  data() {
    return {
    } // return{}
  }, // data()
  computed: {
    computed_param_info() {
      return ((link, cmd) => {
        let param = this.prop_paramRs[link['self_idx']];
        if ('input' == cmd) { 
          return param['input'];
        }
        else if ('readonly' == cmd) { 
          return param['readonly'];
        }
        else if ('required' == cmd) {
          return param['required'];
        }
      });
    },
    compute_param_value() {
      return ((link, field_name) => { return this.prop_paramRs[link['self_idx']][field_name]; }); //-- 필드명의 값을구함
    },
    compute_param_id() { //-- not used yet
      return ((link) => { return ('id_' + this.prop_paramGroup + '_param_' + link['self_idx']) });  //-- input의 id를 설정
    },
  },
  created: function () {
    this.$console_log('trace', 'created()');
  },
  methods: {
    onchange_input(e, paramgroup, idx) {
      //-- input수정시 parent로 값을전달
      this.$emit('emit_update', { 'paramgroup': paramgroup, 'idx': idx, 'value': e.target.value })
    },
    emit_update(arg) {
      //-- input수정시 parent로 값을전달
      this.$emit('emit_update', { 'paramgroup': arg.paramgroup, 'idx': arg.idx, 'value': arg.value })
    },
  }, // methods:
  template: 
    `
    <section>

      <template v-for="(link, index) in prop_paramLink">
        <div :class="('inner para_contain-dp' + compute_param_value(link, 'LEVEL'))">
          <div class="para_content">
            <div class="pkg_board">
              <section>
                <table class="table-noBrd">
                  <caption>table Table</caption>
                  <colgroup><col style="width:33%;"><col style="width:25%;"><col style="width:auto;"></colgroup>
                  <tbody>
                    <tr>
                      <td>{{compute_param_value(link, 'PARAM_NM')}} <em v-show="computed_param_info(link, 'required')">*</em></td>
                      <td>{{compute_param_value(link, 'DATA_TYPE_CD_NM')}}</td>
                      <td>
                        <div class="popup_input">
                          <input type="text" class="cid_param_edit_input" :id="compute_param_id(link)" :value="compute_param_value(link, 'value')" @change="onchange_input($event, prop_paramGroup, link['self_idx'])" v-show="computed_param_info(link, 'input')" :readonly="computed_param_info(link, 'readonly')">
                        </div>
                      </td>
                    </tr>
                  </tbody>
                </table>
              </section>
            </div>
          </div>
        </div>
        <div class="div_depth" v-if="(link.children.length > 0)">
          <div class="btn_addParabox"><span>파라미터 추가</span></div>
          <param-edit :prop_paramGroup="prop_paramGroup" :prop_paramRs="prop_paramRs" :prop_paramLink="link.children" :prop_readonly="prop_readonly" @emit_update="emit_update"></param-edit>
        </div>
      </template>

    </section>
    `
});
//-- param-edit component }

export default {
  name: 'verifyViewDialog',
  props: {
    prop_name: {
      type: String,
      required: true,
    },
   prop_Data: {
      type: Object,
      required: true,
      default: (() => ({ 'verify_seq': '' })),
      validator: function(value) {
        if ((parseInt(value['verify_seq'], 10) > 0) == false) { return false; }
        return true;
      }
    },
  },
  components: {
    'param-edit': paramEdit,
    'lamplog-view-dialog': lamplogViewDialog,
    VueJsonPretty,
  },
  mixins: [CommonMixin],
  data() {
    return {
      m_vue_id: 'verifyViewDialog',

      //-- @property {
      m_prop_lamplogViewData: {},
      //-- @property }

      //-- @biz-data {
      m_verify_seq: this.$sf_str(this.prop_Data['verify_seq']),
      //-- options
      m_readonly: true,

      m_rs_API_VERI_CONDITION: {},  //-- query_rs
      m_o_a_API_PARAM_TEST: {},  //-- 'header':[], 'body':[], 'query':[]

      m_link_API_PARAM_TEST_header: [],  //-- query_rs -> link_array(length -> root갯수)
      m_link_API_PARAM_TEST_body: [],
      m_link_API_PARAM_TEST_query: [],
      //-- @biz-data }
    } // return{}
  }, // data()
  computed: {
    computed_verify_info() {
      return ((cmd) => {
        let result = '';
        if ('testcase_nm' == cmd) {
          return this.m_rs_API_VERI_CONDITION['TESTCASE_NM'];
        }
        else if ('api_nm' == cmd) {
          return this.m_rs_API_VERI_CONDITION['API_NM'];
        }
        else if ('verify_result' == cmd) {
          return ((this.m_rs_API_VERI_CONDITION['SUCCESS_YN'] == 'Y') ? '성공' : '실패');
        }
        else if ('verify_result_css' == cmd) {
          return ((this.m_rs_API_VERI_CONDITION['SUCCESS_YN'] == 'Y') ? 'blue_txt' : 'red_txt');
        }
        else if ('verify_dur_msec' == cmd) {
          let n_StTime = this.$sf_int(this.m_rs_API_VERI_CONDITION['ST_TIME'], -1);
          let n_EndTime = this.$sf_int(this.m_rs_API_VERI_CONDITION['END_TIME'], -1);
          return (((n_StTime > 0) && (n_EndTime > 0)) ? (n_EndTime - n_StTime) : '-');
        }
        else if ('error_message' == cmd) {
          return this.m_rs_API_VERI_CONDITION['RES_ERROR_DESCRIPTION'];
        }
        else if ('error_code' == cmd) {
          return this.m_rs_API_VERI_CONDITION['RES_ERROR_CODE'];
        }
        else if ('css_req_1_arrow' == cmd) {  //-- link -> g/w
          let is_success_req_1 = true;  //-- always success
          let is_success = is_success_req_1;
          return (is_success ? 'process_success_next' : 'process_failure_next');
        }
        else if ('css_res_1_arrow' == cmd) {  //-- link <- g/w
          let is_success_req_1 = true;
          //-- // 에러코드(200001: SHUB 자체오류 - 연동/인증/규격 에러, 200002: SHUB 자체오류 - 유효성 체크 에러)
          let is_success_req_2 = ("1" == this.m_rs_API_VERI_CONDITION['PROC_RESULT_CD']);
          let is_success_res_1 = is_success_req_2;
          let is_success_res_2 = true;
          //--@@let is_success = (is_success_req_1 && is_success_req_2 && is_success_res_1 && is_success_res_2);
          let is_success = true;  //-- always success
          return (is_success ? 'process_success_prev' : 'process_failure_prev');
        }
        else if ('css_req_2_arrow' == cmd) {  //-- g/w -> endpoing
          let is_success_req_1 = true;
          let is_success_req_2 = ("1" == this.m_rs_API_VERI_CONDITION['PROC_RESULT_CD']);
          let is_success = (is_success_req_1 && is_success_req_2);
          return (is_success ? 'process_success_next' : 'process_failure_next');
        }
        else if ('css_res_2_arrow' == cmd) {  //-- g/w <- endpoing
          let is_success_req_1 = true;
          let is_success_req_2 = ("1" == this.m_rs_API_VERI_CONDITION['PROC_RESULT_CD']);
          let is_success_res_1 = is_success_req_2;
          let is_success = (is_success_req_1 && is_success_req_2 && is_success_res_1);
          return (is_success ? 'process_success_prev' : 'process_failure_prev');
        }
        else if ('verify_request' == cmd) {
          let s_headers = this.$sf_str(this.m_rs_API_VERI_CONDITION['REQ_HEADERS']);
          s_headers = ((s_headers.length > 0) ? this.$sf_json_stringify(this.$sf_json_parse(s_headers), null, 2) : s_headers); 
          let s_body = this.$sf_str(this.m_rs_API_VERI_CONDITION['REQ_BODY']);
          s_body = ((s_body.length > 0) ? this.$sf_json_stringify(this.$sf_json_parse(s_body), null, 2) : s_body); 

          s_headers = ((s_headers.length > 0) ? '[HEADERS]\n' : '') + s_headers;
          s_body = ((s_body.length > 0) ? '[BODY]\n' : '') + s_body;
          return (s_headers + ((s_headers.length > 0) ? '\n\n' : '') + s_body);
        }
        else if ('verify_response' == cmd) {
          let s_json = this.$sf_str(this.m_rs_API_VERI_CONDITION['RES_RESPONSE']);
          s_json = ((s_json.length > 0) ? this.$sf_json_stringify(this.$sf_json_parse(s_json), null, 2) : s_json);
          return (s_json);
        }
        else if ('assert_case' == cmd) {
          return (('normal' == this.m_rs_API_VERI_CONDITION['ASSERT_CASE']) ? '정상 Case' : (('except' == this.m_rs_API_VERI_CONDITION['ASSERT_CASE']) ? '예외 Case' : ''));
        }
        else if ('assert_field' == cmd) {
          return this.m_rs_API_VERI_CONDITION['ASSERT_FIELD'];
        }
        else if ('assert_operator' == cmd) {
          return this.m_rs_API_VERI_CONDITION['ASSERT_OPERATOR'];
        }
        else if ('assert_value' == cmd) {
          return this.m_rs_API_VERI_CONDITION['ASSERT_VALUE'];
        }
        else if ('assert_result' == cmd) {
          let assert_result = this.m_rs_API_VERI_CONDITION['ASSERT_RESULT'];
          result = '-';
          if ('OK' == assert_result) {
            result = '성공';
          }
          else if ('NK' == assert_result) {
            result = '실패';
          }
          else if ('NA' == assert_result) {
            result = '불가';
          }
        }
        return result;
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

    //-- API verify data 검색
    if (this.m_verify_seq.length > 0) {
      //-- :@api verify query call
      this.fn_call_api_apiVerify(this.m_verify_seq);
    }
  },
  updated: function () {
    this.$console_log('trace', 'updated()');
  },
  methods: {
    emit_update(arg) {
      //-- input 수정내용 적용
      if (this.$has_own(this.m_o_a_API_PARAM_TEST, arg.paramgroup) == true) {
        this.m_o_a_API_PARAM_TEST[arg.paramgroup][arg.idx]['value'] = arg.value;
      }
    },

    //-- @handler {
    fn_view_lamplog() {
      let req_gw_profile = this.$sf_str(this.m_rs_API_VERI_CONDITION['REQ_GW_PROFILE']);
      let req_search_date = this.$fmt_data(this.m_rs_API_VERI_CONDITION['VERIFICATION_DT'], 'fmt_date_02'); //-- yyyymmdd
      let req_transaction_id = this.$sf_str(this.m_rs_API_VERI_CONDITION['RES_TRANSACTION_ID']);
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
    //-- api verify 정보 query
    fn_call_api_apiVerify(verify_seq) {
      this.$fn_call_api_common((this.m_con_apiVerify_url + '/' + verify_seq), {}, 'get', this.fn_callback_api_apiVerify);
      //--@@this.$fn_call_api_common(this.m_con_apiVerify_url, { 'verify_seq': verify_seq }, 'post', this.fn_callback_api_apiVerify);
    },
    //-- api verify 정보 query 후처리
    fn_callback_api_apiVerify(call_ret, response, api_request, record) {
      this.$console_log('trace', 'fn_callback_api_apiVerify()', 'call_ret: ', call_ret, 'response :', response, 'api_request: ', api_request, 'record: ', record);
      
      //-- call_ret: 'ok', 'nk', 'failed', 'catched'
      if ('ok' == call_ret) {
        //-- adpt.select_API_VERI_CONDITION
        /*--[ref]
        {
          SEQ, DEPLOY_PROC_SEQ, TEST_CASE_SEQ, VERIFICATION_DT, RESULT_CD, RESULT_MSG, VERIFI_USR, SUCCESS_YN, ST_TIME, END_TIME, PROC_RESULT_CD, PROC_RESULT_MSG
          , REQ_GW_PROFILE, REQ_API_URL, REQ_HEADERS, REQ_BODY, REQ_TRANSACTION_ID, REQ_SEQUENCE_NO
          , RES_TRANSACTION_ID, RES_SEQUENCE_NO, RES_RETURN_CODE, RES_RETURN_DESCRIPTION, RES_ERROR_CODE, RES_ERROR_DESCRIPTION, RES_RESPONSE
          , API_NO, API_NM, TESTCASE_NM, PARAM_GUB, PARAM_HEADER, PARAM_BODY, PARAM_QUERY, PARAM_HEADER_JSON, PARAM_BODY_JSON, ASSERT_CASE, ASSERT_FIELD, ASSERT_OPERATOR, ASSERT_VALUE, ASSERT_RESULT
        }
        --*/
        this.m_rs_API_VERI_CONDITION = record;
        let a_udf_header = this.$sf_json_parse(this.m_rs_API_VERI_CONDITION['PARAM_HEADER']);
        let a_udf_body = this.$sf_json_parse(this.m_rs_API_VERI_CONDITION['PARAM_BODY']);
        let a_udf_query = this.$sf_json_parse(this.m_rs_API_VERI_CONDITION['PARAM_QUERY']);

        let a_udf_param_test = { 'header': a_udf_header, 'body': a_udf_body, 'query': a_udf_query };

        ['header', 'body', 'query'].forEach((paramgroup_key) => {
          this.m_o_a_API_PARAM_TEST[paramgroup_key] = [];
          a_udf_param_test[paramgroup_key].forEach((udf_param_test) => {
            let datatype = udf_param_test['d'];

            let do_push = true;
            do_push = do_push && ((('header' == paramgroup_key) && ('array' == datatype)) == false);  //--[i] header의 array를 처리하지 못함
            if (do_push == true) {
              let input = ((datatype != 'object') && (datatype != 'array')); //-- input가능
              let rs_param_test = fn_api_paramtest_object.fn_get_rs_param_object(udf_param_test);
              rs_param_test['input'] = input;
              rs_param_test['required'] = ('Y' == udf_param_test['r']);
              rs_param_test['readonly'] = true;
              rs_param_test['value'] = udf_param_test['v'];
             
              this.m_o_a_API_PARAM_TEST[paramgroup_key].push(rs_param_test);
            }
          });
        });

        this.m_link_API_PARAM_TEST_header = this.fn_convertToHierarchyLink(this.m_o_a_API_PARAM_TEST['header']);
        this.m_link_API_PARAM_TEST_body = this.fn_convertToHierarchyLink(this.m_o_a_API_PARAM_TEST['body']);
        this.m_link_API_PARAM_TEST_query = this.fn_convertToHierarchyLink(this.m_o_a_API_PARAM_TEST['query']);
      }
      else {
        this.$proc_api_resultCode_Fail(call_ret, response);
      }
    },
    //-- @api function }
    //-- @hierarchy {
    //-- resultset array to hierarchy link array
    fn_convertToHierarchyLink(a_items) {
      this.$console_log('trace', 'fn_convertToHierarchyLink()', 'a_items: ', a_items);
      //-- method {
      let lc_fn_create_nodes = (function(a_items) {
        let a_nodes = [];
        for (let n_ii = 0; n_ii < a_items.length; n_ii++) { 
          a_nodes.push({ 'self_idx': n_ii, 'children': [], 'parent_idx': -1 }); 
        }
        return a_nodes;
      });
      let lc_fn_get_parent_idx = (function(o_item, a_items) {
        for (let n_ii = 0; n_ii < a_items.length; n_ii++) { if (a_items[n_ii]['PARAM_NO'] == o_item['PRNTS_PARAM_NO']) { return n_ii; } }
        return -1;
      });
      //-- method }
      let a_nodes = lc_fn_create_nodes(a_items);
      for (let n_ii = a_nodes.length - 1; n_ii >= 0; n_ii--) {
        let o_item = a_items[n_ii];
        let o_node = a_nodes[n_ii];
        if (o_item['PRNTS_PARAM_NO'] == 0) { continue; }  //-- skip over root node.
        let parent_idx = lc_fn_get_parent_idx(o_item, a_items);
        if (parent_idx == -1) { continue; }
        o_node['parent_idx'] = parent_idx;
        a_nodes[parent_idx].children.push(o_node);
        a_nodes.splice(n_ii, 1);
      }
      //--What remains in a_nodes will be the root nodes.
      return a_nodes;
    },
    //-- resultset array to hierarchy node array // not used yet
    fn_convertToHierarchyNode(a_items) {
      this.$console_log('trace', 'fn_convertToHierarchyNode()', 'a_items: ', a_items);
      //-- method {
      let createStructure = (function(nodes) {
        let objects = [];
        for (let n_ii = 0; n_ii < nodes.length; n_ii++) { objects.push({ 'node': nodes[n_ii], 'children': [], 'parent': null }); }
        return objects;
      });
      let getParent = (function(child, nodes) {
        for (let n_ii = 0; n_ii < nodes.length; n_ii++) { if (nodes[n_ii].node['PARAM_NO'] == child.node['PRNTS_PARAM_NO']) { return nodes[n_ii]; } }
        return null;
      });
      //-- method }
  
      let a_nodes = createStructure(a_items);
      for (let n_ii = a_nodes.length - 1; n_ii >= 0; n_ii--) {
        let o_item = a_nodes[n_ii];
        if (o_item.node['PRNTS_PARAM_NO'] == 0) { continue; }  //-- skip over root node.
        let parent = getParent(o_item, a_nodes);
        if (parent == null) { continue; }
        o_item.parent = parent;
        parent.children.push(o_item);
        a_nodes.splice(n_ii, 1);
      }
      //--What remains in a_nodes will be the root nodes.
      return a_nodes;
    },
    //-- @hierarchy }

    //-- @function {
    fn_dialog_close() {
      this.$modal.hide(this.prop_name);
      let ret_data = { 'verify_seq': this.m_verify_seq };
      this.$emit('emit_proc_finished', ret_data);
    },
    fn_is_param_gub_json_mode() {
      return (this.m_rs_API_VERI_CONDITION['PARAM_GUB'] == this.m_enum_PARAM_GUB_JSON);
    },
    //-- @function }

    //-- @vue-json-pretty {
    handle_click_vueJsonPretty(path, data, treeName = '') {
      this.$console_log('trace', 'handle_click_vueJsonPretty()', 'path: ', path, 'data: ', data, 'treeName: ', treeName);
    },
    handle_change_vueJsonPretty(newVal, oldVal) {
      this.$console_log('trace', 'handle_change_vueJsonPretty()', 'newVal: ', newVal, 'oldVal: ', oldVal);
    }
    //-- @vue-json-pretty }
  } // methods:
};
</script>

<template>
  <div class="popup_wrap">
    <div class="popup_header">
      <span class="popup_header_title">Test Case</span>
      <button type="button" class="popup_header_button" role="button" @click.stop="fn_dialog_close()" title="Close">
        <span class="button_close">Close</span>
      </button>
    </div>
    <div class="pop_ver pop_ver2">
      <div class="popup_content distribution_cont">
        <div class="scroll_wrap">
          <div class="wrap_content">
  
            <div class="pkg_board ">
              <table class="table-vw table-vw6">
                <caption>테스트결과 Table</caption>
                <colgroup><col style="width:15.5%;"><col style="width:34.5%;"><col style="width:15.5%;"><col style="width:34.5%;"></colgroup>
                <tbody>
                  <tr>
                    <th scope="row"><div>Test Case 명</div></th>
                    <td>{{computed_verify_info('testcase_nm')}}</td>
                    <th scope="row"><div>API명</div></th>
                    <td><div>{{computed_verify_info('api_nm')}}</div></td>
                  </tr>
                  <tr>
                    <th scope="row"><div>테스트 결과</div></th>
                    <td class="text_result">
                      <div :class="computed_verify_info('verify_result_css')">{{computed_verify_info('verify_result')}}</div>
                    </td>
                    <th scope="row"><div>소요시간(msec)</div></th>
                    <td><div>{{computed_verify_info('verify_dur_msec')}}</div></td>
                  </tr>
                  <tr>
                    <th scope="row"><div>에러코드</div></th>
                    <td>{{computed_verify_info('error_code')}}</td>
                    <th scope="row"><div>Assert결과</div></th>
                    <td><div>{{computed_verify_info('assert_result')}}</div></td>
                  
                  </tr>
                </tbody>
              </table>
            </div><!-- .pkg_board -->
  
            <div class="pkg_board">
              <table class="table-vw table-vw4 table-vw5">
                <caption>테스트결과 Table</caption>
                <colgroup><col style="width:15.5%;"><col style="width:auto;"></colgroup>
                <tbody>
                  <tr>
                    <th scope="row"><div>에러메시지</div></th>
                    <td>{{computed_verify_info('error_message')}}</td>
                  </tr>
                  <tr>
                    <th scope="row"><div>테스트 Process</div></th>
                    <td class="text_process">
                      <div class="text_process_cont">
                        <!-- [D]process_success_next, process_success_prev, process_failure_next, process_failure_prev -->
                        <div :class="['data', computed_verify_info('css_req_1_arrow')]"><span class="data_link">img</span><p>Link</p></div>
                        <div :class="['data', computed_verify_info('css_res_1_arrow'), computed_verify_info('css_req_2_arrow')]"><span class="data_Gw">img</span><p>G/W</p></div>
                        <div :class="['data', computed_verify_info('css_res_2_arrow')]"><span class="data_enabler">img</span><p>Enabler</p></div>
                      </div>
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

                    </td>
                  </tr>
                  <tr>
                    <th scope="row"><div>요청전문</div></th>
                    <td class="response">
                      <pre>{{computed_verify_info('verify_request')}}</pre>
                    </td>
                  </tr>
                  <tr>
                    <th scope="row"><div>응답전문</div></th>
                    <td class="response">
                      <pre>{{computed_verify_info('verify_response')}}</pre>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div><!-- .pkg_board -->
  
            <div class="pkg_board pkg_board2">
              <table class="table-vw table-vw6">
                <caption>요청 Parameter Table</caption>
                <tbody>
                  <tr><th scope="row"><div>요청 Parameter</div></th></tr>
                </tbody>
              </table>
            </div>
  
            <template v-if="(true == fn_is_param_gub_json_mode())">

              <!-- header { -->
              <div class="pkg_board" v-show="(m_rs_API_VERI_CONDITION['PARAM_HEADER_JSON'].length > 0)">
                <div class="top_head"><p>Header</p><p><!-- dummy last-child --></p></div>
                <div class="scroll_view_json_wrap">
                  <div class="scroll_view_json">
                    <vue-json-pretty v-show="(m_rs_API_VERI_CONDITION['PARAM_HEADER_JSON'].length > 0)"
                      :path="'root'"
                      :data="$sf_json_parse(m_rs_API_VERI_CONDITION['PARAM_HEADER_JSON'])"
                      @click="handle_click_vueJsonPretty"
                      @change="handle_change_vueJsonPretty"
                      :show-line="false"
                      :show-double-quotes="true"
    
                      :highlight-mouseover-node="false"
                      :show-length="false"
                    >
                    </vue-json-pretty>
                  </div><!-- .scroll_view_json -->
                </div><!-- .scroll_view_json_wrap -->
  
              </div><!-- .pkg_board -->
              <!-- header } -->
  
              <!-- body { -->
              <div class="pkg_board" v-show="(m_rs_API_VERI_CONDITION['PARAM_BODY_JSON'].length > 0)">
                <div class="top_head"><p>Body</p><p><!-- dummy last-child --></p></div>
                <div class="scroll_view_json_wrap">
                  <div class="scroll_view_json">
                    <vue-json-pretty v-show="(m_rs_API_VERI_CONDITION['PARAM_BODY_JSON'].length > 0)"
                      :path="'root'"
                      :data="$sf_json_parse(m_rs_API_VERI_CONDITION['PARAM_BODY_JSON'])"
                      @click="handle_click_vueJsonPretty"
                      @change="handle_change_vueJsonPretty"
                      :show-line="false"
                      :show-double-quotes="true"
    
                      :highlight-mouseover-node="false"
                      :show-length="false"
                    >
                    </vue-json-pretty>
                  </div><!-- .scroll_view_json -->
                </div><!-- .scroll_view_json_wrap -->
  
              </div><!-- .pkg_board -->
              <!-- body } -->

            </template>
            <template v-else>
  
              <!-- header { -->
              <div class="pkg_board" v-show="(m_link_API_PARAM_TEST_header.length > 0)">
                <!-- writeform -->
                <div class="top_head"><p>Header</p><p><em>*</em>필수</p></div>
                <table class="table-vw table-vw3 ">
                  <caption>Header Table</caption>
                  <colgroup><col style="width:45%;"><col style="width:15%;"><col style="width:auto;"></colgroup>
                  <thead><tr><th>Parameter</th><th>Type</th><th>Value</th></tr></thead>
                </table>
                <div class="scroll_box_param">
          
                  <div class="div_depth">
                    <div class="btn_addParabox"><span>파라미터 추가</span></div>
                    <param-edit prop_paramGroup="header" :prop_paramRs="m_o_a_API_PARAM_TEST['header']" :prop_paramLink="m_link_API_PARAM_TEST_header" :prop_readonly="m_readonly" @emit_update="emit_update"></param-edit>
                  </div><!-- .div_depth -->
    
                </div><!-- .scroll_box_param -->
              </div><!-- .pkg_board -->
              <!-- header } -->
      
              <!-- body { -->
              <div class="pkg_board" v-show="(m_link_API_PARAM_TEST_body.length > 0)">
                <!-- writeform -->
                <div class="top_head">
                  <p>Body</p>
                  <p><em>*</em>필수</p>
                </div>
                <table class="table-vw table-vw3 ">
                  <caption>Body Table</caption>
                  <colgroup><col style="width:45%;"><col style="width:15%;"><col style="width:auto;"></colgroup>
                  <thead><tr><th>Parameter</th><th>Type</th><th>Value</th></tr></thead>
                </table>
                <div class="scroll_box_param">
              
                  <div class="div_depth">
                    <div class="btn_addParabox"><span>파라미터 추가</span></div>
                    <param-edit prop_paramGroup="body" :prop_paramRs="m_o_a_API_PARAM_TEST['body']" :prop_paramLink="m_link_API_PARAM_TEST_body" :prop_readonly="m_readonly" @emit_update="emit_update"></param-edit>
                  </div><!-- .div_depth -->
              
                </div><!-- .scroll_box_param -->
              </div><!-- .pkg_board -->
              <!-- body } -->
      
              <!-- query { -->
              <div class="pkg_board" v-show="(m_link_API_PARAM_TEST_query.length > 0)">
                <!-- writeform -->
                <div class="top_head">
                  <p>Query</p>
                  <p><em>*</em>필수</p>
                </div>
                <table class="table-vw table-vw3">
                  <caption>Query Table</caption>
                  <colgroup><col style="width:45%;"><col style="width:15%;"><col style="width:auto;"></colgroup>
                  <thead>
                    <thead><tr><th>Parameter</th><th>Type</th><th>Value</th></tr></thead>
                  </thead>
                </table>
                <div class="scroll_box_param">
    
                  <div class="div_depth">
                    <div class="btn_addParabox"><span>파라미터 추가</span></div>
                    <param-edit prop_paramGroup="query" :prop_paramRs="m_o_a_API_PARAM_TEST['query']" :prop_paramLink="m_link_API_PARAM_TEST_query" :prop_readonly="m_readonly" @emit_update="emit_update"></param-edit>
                  </div><!-- .div_depth -->
    
                </div><!-- .scroll_box_param -->
              </div><!-- .pkg_board -->
              <!-- query } -->
            </template> <!--  v-else="(true == fn_is_param_gub_json_mode())" -->
  
            <!-- assertion { -->
            <div class="pkg_board">
              <!-- writeform -->
              <h3>Assertion</h3>
              <div class="check_box">
                <div class="info_check_box">
                  <p>
                    <input type="radio" id="testGubun_01" name="testGubun" title="정상 Case" value="normal" v-model="m_rs_API_VERI_CONDITION['ASSERT_CASE']" @click.stop.prevent=""><label for="testGubun_01"><span></span>정상 Case</label>
                    <input type="radio" id="testGubun_02" name="testGubun" title="예외 Case" value="except" v-model="m_rs_API_VERI_CONDITION['ASSERT_CASE']" @click.stop.prevent=""><label for="testGubun_02"><span></span>예외 Case</label>
                  </p>
                </div>
              </div>
              
              <table class="table-vw table-vw6">
                <caption>Testcase Table</caption>
                <colgroup><col style="width:15.5%;"><col style="width:15.5%;"><col style="width:15.5%;"><col style="width:auto;"></colgroup>
                <tbody>
                  <tr>
                    <th scope="row"><div>Field</div></th>
                    <td colspan="3">{{computed_verify_info('assert_field')}}</td>
                  </tr>
                  <tr>
                    <th scope="row"><div>Operator</div></th>
                    <td>{{computed_verify_info('assert_operator')}}</td>
                    <th scope="row"><div>Value</div></th>
                    <td>{{computed_verify_info('assert_value')}}</td>
                  </tr>
                </tbody>
              </table>
            </div><!-- .pkg_board -->
            <!-- assertion } -->
    
          </div><!-- .wrap_content -->
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
  .distribution_cont .scroll_box_param {height:auto; max-height: 280px; overflow: auto; margin-bottom: 20px;}
  
  .distribution_cont .scroll_view_json_wrap {margin-top: 5px; padding:10px; background-color:#f7f7f7; }
  .distribution_cont .scroll_view_json {height:auto; min-height: 100px; max-height:208px; overflow: auto; background-color:#f7f7f7; }
</style>
