<comment>
  @testcase edit
    popup-[testcase]
</comment>
<script>
import { CommonMixin } from '@/common/adptran.js';
import { fn_api_paramtest_object } from '@/common/fn_api_paramtest_object.js';
import VueJsonPretty from 'vue-json-pretty'

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
        else if ('cmd_param_nm_red_text' == cmd) {
          let a_cmd_except = this.$sf_arr(param['cmd_except']);
          return ((a_cmd_except.indexOf(cmd) != -1) ? 'red_txt' : '');
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
        <template v-if="('n' == compute_param_value(link, 'ui_display'))">
          <template v-if="(link.children.length > 0)">
            <!-- array of object -->
            <param-edit :prop_paramGroup="prop_paramGroup" :prop_paramRs="prop_paramRs" :prop_paramLink="link.children" :prop_readonly="prop_readonly" @emit_update="emit_update"></param-edit>
          </template>
        </template>
        <template v-else>
          <div :class="('inner para_contain-dp' + compute_param_value(link, 'LEVEL'))">
            <div class="para_content">
              <div class="pkg_board">
                <section>
                  <table class="table-noBrd">
                    <caption>table Table</caption>
                    <colgroup><col style="width:33%;"><col style="width:25%;"><col style="width:auto;"></colgroup>
                    <tbody>
                      <tr>
                        <td><span :class="computed_param_info(link, 'cmd_param_nm_red_text')">{{compute_param_value(link, 'PARAM_NM')}}</span> <em v-show="computed_param_info(link, 'required')">*</em></td>
                        <td>{{compute_param_value(link, 'datatype_name')}}</td>
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
      </template>

    </section>
    `
});
//-- param-edit component }

export default {
  name: 'testcaseDialog',
  props: {
    prop_name: {
      type: String,
      required: true,
    },
    prop_Data: {
      type: Object,
      required: true,
      default: (() => ({ 'proc_mode': 'new', 'api_no': '', 'testcase_id': '', 'options': { 'loadtestdata': 'n', 'readonly': 'n', 'fixedvalueedit': 'y' } })),
      validator: function(value) {
        let b_ret = false;
        if (('new' != value['proc_mode']) && ('update' != value['proc_mode'])) { return false; }
        if (value['proc_mode'] == 'new') {
          b_ret = (parseInt(value['api_no'], 10) > 0);
        }
        else if (value['proc_mode'] == 'update') {
          b_ret = (parseInt(value['testcase_id'], 10) > 0);
        }
        return b_ret;
      }
    },
  },
  components: {
    'param-edit': paramEdit,
    VueJsonPretty,
  },
  mixins: [CommonMixin],
  data() {
    return {
      m_vue_id: 'testcaseDialog',

      //-- @biz-data {
      m_proc_mode: this.$sf_str(this.prop_Data['proc_mode']),   //-- new, update, view
      m_api_no: this.$sf_str(this.prop_Data['api_no']),
      m_testcase_id: this.$sf_str(this.prop_Data['testcase_id']),
      m_api_nm: '',
      m_api_handler_cd_nm: '',
      //-- options
      m_loadtestdata: ('y' == this.$sf_obj_val(this.prop_Data['options'], 'loadtestdata', '').toLowerCase()),
      m_readonly: ('y' == this.$sf_obj_val(this.prop_Data['options'], 'readonly', '').toLowerCase()),
      m_fixedvalueedit: ('y' == this.$sf_obj_val(this.prop_Data['options'], 'fixedvalueedit', '').toLowerCase()),

      m_o_a_API_PARAM_TEST: {},  //-- 'header':[], 'body':[], 'query':[]

      m_link_API_PARAM_TEST_header: [],  //-- query_rs -> link_array(length -> root갯수)
      m_link_API_PARAM_TEST_body: [],
      m_link_API_PARAM_TEST_query: [],

      m_is_apiParamTest_loaded: false,  //-- fn_call_api_apiParamTest() queried
      m_is_apiTestCase_loaded: false,   //-- fn_callback_api_apiTestCase() queried
      
      m_option_assert_field: [],
      m_option_assert_operator: [ '==', '!=', '<', '>', '<=', '>=' ],

      m_cpapireq: { 'header': {}, 'body': {}, 'query': {}, }, //-- cpapireq param
      m_json_paramgroup_key: '',  //-- form모드 json입력시 param구분key  ['header', 'body']
      //-- @biz-data }

      //-- @ui-data {
      //-- copy prop data to member data
      m_model_testcase: {
        'is_queried': '',
        'api_no': this.$sf_str(this.prop_Data['api_no']),
        'testcase_id': this.$sf_str(this.prop_Data['testcase_id']),
        'testcase_nm': '',
        'assert_case': 'normal',  // [normal|except]
        'assert_field': '',
        'assert_operator': '',  // ['=='|'!='|'<'|'>'|'<='|'>=']
        'assert_value': '',
        'param_gub': null,  // ['json', '']
        'param_header_json': '',
        'param_body_json': '',
        'param_header': [],  // [{'n':,'f':,'d':,'v:}]  //-- v-model로 연결되지 않고 save시에만 사용됨
        'param_body': [],
        'param_query': [],
        'infoview_yn': 'N',
        'infoview_yn_checked': false,
      },
      m_model_json_string: '',
      //-- @ui-data }
    } // return{}
  }, // data()
  computed: {
    //--[drm][now work][watch]computed_m_o_a_API_PARAM_TEST() { return Object.assign({}, this.m_o_a_API_PARAM_TEST); },
    ui_ctrl_btn_saveas_disabled() {
      return ((this.m_is_apiParamTest_loaded == false) || (this.m_is_apiTestCase_loaded == false));
    },
    ui_ctrl_btn_upd_disabled() {
      return ((this.m_is_apiParamTest_loaded == false) || (this.m_is_apiTestCase_loaded == false));
    },
    ui_ctrl_btn_new_disabled() {
      return (this.m_is_apiParamTest_loaded == false);
    },
  },
  watch: {
    //--[drm][not work][watch]computed_m_o_a_API_PARAM_TEST: { handler: function(newVal, oldVal) {}, deep: true },
  },
  created: function () {
    this.$console_log('trace', 'created()');
  },
  mounted: function () {
    this.$console_log('trace', 'mounted()');

    //-- API parameter검색
    if (this.m_api_no.length > 0) {
      //-- :@api def query call
      this.fn_call_api_apiParamTest(this.m_api_no);
      if (this.m_model_testcase['testcase_id'].length == 0) {  //-- prop testcase_id 없을시 검색
        this.fn_call_api_apiDefWithApiSpc(this.m_api_no);
      }
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
    onclick_btn_clear() {
      this.fn_ui_clear_edit();
    },
    onclick_btn_testdata() {
      this.fn_load_testdata();
    },
    onclick_btn_json(paramgroup_key) {
      if (true == this.fn_is_param_gub_json_mode()) {
        if ('header' == paramgroup_key) {
          this.m_model_json_string = this.m_model_testcase['param_header_json'];
        }
        else if ('body' == paramgroup_key) {
          this.m_model_json_string = this.m_model_testcase['param_body_json'];
        }
      }
      else {
        this.m_model_json_string = this.fn_get_json_string(paramgroup_key);
      }
      this.m_json_paramgroup_key = paramgroup_key;
      this.$modal.show('modalJsonEditDialog');
    },
    onclick_btn_save() {
      this.fn_testcase_save();
    },
    onclick_btn_saveas() {
      this.fn_testcase_save('saveas');
    },
    onchange_assert_field() {
      if (this.m_model_testcase['assert_field'].length == 0) {
        this.m_model_testcase['assert_operator'] = '';
        this.m_model_testcase['assert_value'] = '';
      }
    },
    //-- @handler }

    //-- @api function {
    //-- api param test정보 query
    fn_call_api_apiParamTest(api_no) {
      this.$fn_call_api_common((this.m_con_apiParamTest_url + '/' + api_no), {}, 'get', this.fn_callback_api_apiParamTest);
      //--@@this.$fn_call_api_common(this.m_con_apiParamTest_url, { 'api_no': api_no }, 'post', this.fn_callback_api_apiParamTest);
    },
    //-- api param test정보 query 후처리
    fn_callback_api_apiParamTest(call_ret, response, api_request, recordset) {
      this.$console_log('trace', 'fn_callback_api_apiParamTest()', 'call_ret: ', call_ret, 'response :', response, 'api_request: ', api_request, 'recordset: ', recordset);
      
      //-- call_ret: 'ok', 'nk', 'failed', 'catched'
      if ('ok' == call_ret) {
        //-- adpt.select_API_PARAM_TEST_list
        this.fn_ui_prepare_edit(recordset);
        if (this.fn_is_mode_UPDATE() == true) {
          //-- :@testcase query call
          this.fn_call_api_apiTestCase(this.m_testcase_id);  //-- testcase load
        }
        else {
          //--기본입력모드(form)설정 
          this.m_model_testcase['param_gub'] = '';
        }
        this.m_is_apiParamTest_loaded = true;
      }
      else {
        this.$proc_api_resultCode_Fail(call_ret, response);
      }
    },

    //-- api testcase정보 query
    fn_call_api_apiTestCase(testcase_id) {
      this.$console_log('trace', 'fn_call_api_apiTestCase()', 'testcase_id: ', testcase_id);
      this.$fn_call_api_common((this.m_con_apiTestCase_url + '/' + testcase_id), {}, 'get', this.fn_callback_api_apiTestCase);
      //--@@this.$fn_call_api_common(this.m_con_apiTestCase_url, { 'testcase_id': testcase_id }, 'post', this.fn_callback_api_apiTestCase);
    },
    //-- api testcase정보 query 후처리
    fn_callback_api_apiTestCase(call_ret, response, api_request, record) {
      this.$console_log('trace', 'fn_callback_api_apiTestCase()', 'call_ret: ', call_ret, 'response :', response, 'api_request: ', api_request, 'record: ', record);
      
      //-- call_ret: 'ok', 'nk', 'failed', 'catched'
      if ('ok' == call_ret) {
        //-- adpt.select_API_TESTCASE
        //-- {TESTCASE_ID, API_NO, TESTCASE_NM, TESTCASE_DESC, PARAM_GUB, PARAM_HEADER, PARAM_BODY, PARAM_QUERY, PARAM_HEADER_JSON, PARAM_BODY_JSON, ASSERT_CASE, ASSERT_FIELD, ASSERT_OPERATOR, ASSERT_VALUE, INFOVIEW_YN, REG_DT, AMD_DT, def_API_NO, def_API_NM, def_API_HANDLER_CD_NM}
        
        //-- :@testcase load
        let testcase_id = this.$sf_obj_val(record, 'TESTCASE_ID', '');
        this.m_model_testcase['is_queried'] = ((testcase_id == this.m_testcase_id) ? 'y' : '?');

        this.m_model_testcase['testcase_id'] = testcase_id;
        this.m_model_testcase['api_no'] = this.$sf_obj_val(record, 'def_API_NO', '');
        this.m_model_testcase['testcase_nm'] = this.$sf_obj_val(record, 'TESTCASE_NM', '');

        this.m_model_testcase['param_gub'] = this.$sf_obj_val(record, 'PARAM_GUB', '');
        this.m_model_testcase['param_header_json'] = this.$sf_obj_val(record, 'PARAM_HEADER_JSON', '');
        this.m_model_testcase['param_body_json'] = this.$sf_obj_val(record, 'PARAM_BODY_JSON', '');

        this.m_model_testcase['assert_case'] = this.$sf_obj_val(record, 'ASSERT_CASE', '');
        this.m_model_testcase['assert_field'] = this.$sf_obj_val(record, 'ASSERT_FIELD', '');
        this.m_model_testcase['assert_operator'] = this.$sf_obj_val(record, 'ASSERT_OPERATOR', '');
        this.m_model_testcase['assert_value'] = this.$sf_obj_val(record, 'ASSERT_VALUE', '');
        
        this.m_model_testcase['infoview_yn'] = this.$sf_obj_val(record, 'INFOVIEW_YN', '');
        this.m_model_testcase['infoview_yn_checked'] = ('Y' == this.m_model_testcase['infoview_yn']);
        

        this.m_api_nm = this.$sf_obj_val(record, 'def_API_NM', '');
        this.m_api_handler_cd_nm = this.$sf_obj_val(record, 'def_API_HANDLER_CD_NM', '');

        //-- @@ m_model_testcase의 header, body, query 항목에 testcase 정보를 설정 - not used yet
        let a_rs_field = ['PARAM_HEADER', 'PARAM_BODY', 'PARAM_QUERY'];
        let a_model_testcase_key = ['param_header', 'param_body', 'param_query'];
        ['header', 'body', 'query'].forEach((paramgroup_key, idx) => {
          let testcase_key = a_model_testcase_key[idx];
          this.m_model_testcase[testcase_key] = [];
          let s_param_test = this.$sf_obj_val(record, a_rs_field[idx], '');
          let a_param_test = [];
          if (this.$is_json_str(s_param_test) == false) {
            alert_message('\'' + a_rs_field[idx] + '\' JSON parse error', 'Test Case');
          }
          else {
            a_param_test = this.$sf_json_parse(s_param_test);
            if (Array.isArray(a_param_test) == false) {
              alert_message('\'' + a_rs_field[idx] + '\' JSON object is not array', 'Test Case');
              a_param_test = [];
            }
          }
          a_param_test.forEach((param_test) => {
            let paramnmfull = this.$sf_str(param_test['f'], '');
            let value = this.$sf_str(param_test['v']);
            //-- :@load value
            let self_idx = this.m_o_a_API_PARAM_TEST[paramgroup_key].findIndex((rs_param_test) => { return (rs_param_test['PARAM_NM_FULL'] == paramnmfull); });
            if (self_idx != -1) {
              let rs_param_test = this.m_o_a_API_PARAM_TEST[paramgroup_key][self_idx];
              rs_param_test['value'] = value;
              this.m_o_a_API_PARAM_TEST[paramgroup_key].splice(self_idx, 1, rs_param_test);  //-- for ui update
            }
            this.m_model_testcase[testcase_key].push(param_test);
          });
        });
        this.m_is_apiTestCase_loaded = true;
      }
      else {
        this.$proc_api_resultCode_Fail(call_ret, response);
      }
    },
    //-- api정보 query
    fn_call_api_apiDefWithApiSpc(api_no) {
      this.$console_log('trace', 'fn_call_api_apiDefWithApiSpc()', 'api_no: ', api_no);
      this.$fn_call_api_common((this.m_con_apiDefWithApiSpc_url + '/' + api_no), {}, 'get', this.fn_callback_api_apiDefWithApiSpc);
      //--@@this.$fn_call_api_common(this.m_con_apiDefWithApiSpc_url, { 'api_no': api_no }, 'post', this.fn_callback_api_apiDefWithApiSpc);
    },
    //-- api정보 query 후처리
    fn_callback_api_apiDefWithApiSpc(call_ret, response, api_request, record) {
      this.$console_log('trace', 'fn_callback_api_apiDefWithApiSpc()', 'call_ret: ', call_ret, 'response :', response, 'api_request: ', api_request, 'record: ', record);

      //-- call_ret: 'ok', 'nk', 'failed', 'catched'
      //-- select_API_DEF_with_API_SPC
      if ('ok' == call_ret) {
        this.m_api_nm = this.$sf_obj_val(record, 'API_NM', '');
        this.m_api_handler_cd_nm  = this.$sf_obj_val(record, 'API_HANDLER_CD_NM', '');
      }
      else {
        this.$proc_api_resultCode_Fail(call_ret, response);
      }
    },

    //-- api testcase정보 transaction
    //-- trans: insert, update, delete
    fn_call_api_apiTestCaseTrans(req_trans, req_model_testcase) {
      this.$console_log('trace', 'fn_call_api_apiTestCaseTrans()', 'req_trans: ', req_trans, 'req_model_testcase: ', req_model_testcase);

      if (('ins' != req_trans) && ('upd' != req_trans) && ('del' != req_trans)) { return; }

      req_model_testcase['trans'] = req_trans;  //-- append 'trans' for callback
      this.$fn_call_api_common((this.m_con_apiTestCaseTrans_url + '/' + req_trans), req_model_testcase, 'post', this.fn_callback_api_apiTestCaseTrans);
    },
    //-- api testcase정보 transaction 후처리 // result_data not used
    fn_callback_api_apiTestCaseTrans(call_ret, response, api_request, result_data) {
      this.$console_log('trace', 'fn_callback_api_apiTestCaseTrans()', 'call_ret: ', call_ret, 'response :', response, 'api_request: ', api_request, 'result_data: ', result_data);
      
      //-- call_ret: 'ok', 'nk', 'failed', 'catched'
      let s_msg = '';
      let is_close = false;
      let is_emit = false;
      let tran_gub = 'Test Case 처리';
      let req_trans = api_request['trans'];

      if ('ins' == req_trans) { tran_gub = 'Test Case 입력처리'; }
      else if ('upd' == req_trans) { tran_gub = 'Test Case 수정처리'; }
      else if ('del' == req_trans) { tran_gub = 'Test Case 삭제처리'; }
      if ('ok' == call_ret) {
        if (200 == result_data['resultCd']) { //-- data: {resultCd: 200, resultMsg: 'xxx'}
          is_emit = true;
          if ('ins' == req_trans) {
            s_msg = 'Test Case를 신규 저장 하였습니다.';
            is_close = true;
          }
          else if ('upd' == req_trans) {
            s_msg = 'Test Case를 수정 하였습니다.';
          }
          else if ('del' == req_trans) {
            s_msg = 'Test Case를 삭제 하였습니다.';
            is_close = true;
          }
        }
        else {
          s_msg = this.$fmt_response_messge(tran_gub + '에 실패하였습니다.', result_data['resultCd'], result_data['resultMsg']);
        }
      }
      else if ('nk' == call_ret) {
        s_msg = this.$fmt_response_messge(tran_gub + '에 실패하였습니다.', response['resultCode'], response['resultMessage']);
      }
      else if ('failed' == call_ret) {
        let res_message = response['message'];
        let res_stack = response['stack'];
        let sub_response_status = response['response']['status']; //-- 404
        //--@@let sub_response_statusText = response['response']['statusText']; //-- ''
        s_msg = this.$fmt_response_messge(tran_gub + '시 오류가 발생했습니다.', sub_response_status, res_message);
      }
      else if ('catched' == call_ret) {
        let errorMessage = response['message'];
        s_msg = this.$fmt_response_messge(tran_gub + '시 예외가 발생했습니다.', '', errorMessage);
      }
      else {
        s_msg = tran_gub + '시  정의되지 않은 응답값을 수신 하였습니다.\n\n[call_ret: ' + call_ret + ']';
      }
      this.$adpt_alert(s_msg, '', false).then(((response) => {
        if (true == is_close) { this.fn_dialog_close(); }
      })
      , ((response) => {
        if (true == is_close) { this.fn_dialog_close(); }
      }));
      if (true == is_emit) {
        this.fn_proc_finished(req_trans, api_request);
      }
    },
    //-- @api function }
    
    //-- @editor {
    //-- {LEVEL, PARAM_NM_FULL, PARAM_NO, API_NO, PARAM_TYPE_CD, SORT_ODRG, PARAM_NM, DATA_TYPE_CD_NM, EXAM, PRNTS_PARAM_NO, PARAM_LOC, REQUIRED, DO_NOT_SEND, FIXED_VALUE, HIDDEN}
    fn_ui_prepare_edit(rs_API_PARAM_TEST) {
      this.$console_log('trace', 'fn_ui_prepare_edit()');

      //-- array -> object화 (for direct access)
      this.m_o_a_API_PARAM_TEST = { 'header': [], 'body': [], 'query': [] }; //-- group별 rs[]

      //-- for cpapireq object
      let udf_param_array = { 'header': [], 'body': [], 'query': [] };

      let a_assert_field_header = [];
      let a_assert_field_body = [];
      
      //--[drm][test]
      //--##rs_API_PARAM_TEST = rs_API_PARAM_TEST.splice(0, 10);

      //-- [i]logic공유//testcaseDialog.vue/testcaseListDialog.vue
      //-- [tag:object_in_array]
      //-- [drm][chg][forEach to for loop]
      //--@@rs_API_PARAM_TEST.forEach((rs_param_test) => {});
      for (let n_ii = 0; n_ii < rs_API_PARAM_TEST.length; n_ii++) {
        let rs_param_test = rs_API_PARAM_TEST[n_ii];

        //-- input binding
        let input = false;
        let readonly = false;

        let value = '';
        let paramnm = this.$sf_str(rs_param_test['PARAM_NM']);
        let paramtype = this.$sf_str(rs_param_test['PARAM_TYPE_CD']);
        let datatype = this.$sf_str(rs_param_test['DATA_TYPE_CD_NM']).toLowerCase();
        let datatype_name = this.$sf_str(rs_param_test['DATA_TYPE_CD_NM']);
        let paramloc = this.$sf_str(rs_param_test['PARAM_LOC']);
        let fixedvalue = this.$sf_str(rs_param_test['FIXED_VALUE']);
        let exam = this.$sf_str(rs_param_test['EXAM']);
        let required = ('Y' == this.$sf_str(rs_param_test['REQUIRED']).toUpperCase());

        if ('PRMTYP1010' == paramtype) {  //-- request

          //-- [tag:object_in_array]
          //-- object_in_array except처리 {
          let is_array_of_object = false;
          if ('array' == datatype) {
            let n_jj = n_ii + 1;
            if (n_jj < rs_API_PARAM_TEST.length) {
              let rs_param_test_next = rs_API_PARAM_TEST[n_jj];
  
              let datatype_next = this.$sf_str(rs_param_test_next['DATA_TYPE_CD_NM']).toLowerCase();
              if ('object' == datatype_next) {
                let param_no = this.$sf_str(rs_param_test['PARAM_NO']);
                let paramnmfull = this.$sf_str(rs_param_test['PARAM_NM_FULL']);
  
                let paramnm_next = this.$sf_str(rs_param_test_next['PARAM_NM']);
                let prntsparamno_next = this.$sf_str(rs_param_test_next['PRNTS_PARAM_NO']);
                let paramnmfull_next = this.$sf_str(rs_param_test_next['PARAM_NM_FULL']);
                //-- array next param이 object이고 PRNTS_PARAM_NO의 관계, PARAM_NM_FULL의 관계가 일치할경우 array of object로 처리 
                if ((prntsparamno_next == param_no) && (paramnmfull_next == (paramnmfull + '.' + paramnm_next))) {
                  is_array_of_object = true;
                }
                if (true == is_array_of_object) {
                  rs_param_test['datatype_name'] = 'Array of Object';
                  rs_param_test_next['datatype_name'] = 'Object in Array';
                }
                rs_param_test_next['ui_display'] = (is_array_of_object ? 'n' : ''); //-- 화면표기여부
              }
            }
          }
          //-- object_in_array except처리 } 

          let do_push = true;
          do_push = do_push && this.$has_own(this.m_o_a_API_PARAM_TEST, paramloc);
          do_push = do_push && ((('header' == paramloc) && ('array' == datatype)) == false);  //--[i] header의 array를 처리하지 못함

          if (true == do_push) {
            input = ((datatype != 'object') && (datatype != 'array')); //-- input가능
            if (true == input) {  //-- 입력가능일시
              //-- prepare ui {
              readonly = (readonly || this.m_readonly) //-- options readonly == 'y'
              readonly = (readonly || ((false == this.m_fixedvalueedit) && (fixedvalue.length > 0))); //-- options fixedvalueedit != 'y'  + fixedvalue exist
              if (true == this.m_loadtestdata) { //-- assign value
                value = ((fixedvalue.length > 0) ? fixedvalue : exam);
                if (false == required) { value = value.trim(); }
              }
              //-- prepare ui }
            }
            rs_param_test['datatype_name'] = this.$sf_obj_val(rs_param_test, 'datatype_name', datatype_name);
            rs_param_test['value'] = value;
            rs_param_test['input'] = input;
            rs_param_test['required'] = required;
            rs_param_test['readonly'] = readonly;

            if ('KOS' == this.m_api_handler_cd_nm) {
              //-- KOS case header의 특정 항목명을 red text처리
              if ('header' == paramloc) {
                let a_red_text_field = 'appName,svcName,fnName,chnlType,userId,orgId,srcId'.toLowerCase().split(',');
                if (a_red_text_field.indexOf(this.$sf_str(paramnm).toLowerCase()) != -1) {
                  this.$sf_arr(rs_param_test['cmd_except']).push('cmd_param_nm_red_text'); //-- 예외처리구분자
                }
              }
            }
            this.m_o_a_API_PARAM_TEST[paramloc].push(rs_param_test);

            //-- for cpapireq object
            let udf_param = fn_api_paramtest_object.fn_get_udf_param_object(rs_param_test);
            udf_param_array[paramloc].push(udf_param);
          } //--- if (true == do_push) {
        }
        else if ('PRMTYP1020' == paramtype) {  //-- response
          //-- prepare assertion field
          if ((('header' == paramloc) || ('body' == paramloc)) && ('object' != datatype) && ('array' != datatype)) {
            let paramnmfull = rs_param_test['PARAM_NM_FULL'];
            let text = '';
            let value = '';
            if ('header' == paramloc) {
              value = paramnmfull;
              text = ('Header : ' + value);
              if (value.length > 0) { a_assert_field_header.push({'t': text,'v': value}); }
            }
            else if ('body' == paramloc) {
              value = paramnmfull.split('.').splice(1).join('.'); //-- 맨앞1개삭제 // splice(1) => [1]부터 나머지 return
              text = ('Body : ' + value);
              if (value.length > 0) { a_assert_field_body.push({'t': text,'v': value}); }
            }
          }
        }
      }

      this.m_link_API_PARAM_TEST_header = this.fn_convertToHierarchyLink(this.m_o_a_API_PARAM_TEST['header']);
      this.m_link_API_PARAM_TEST_body = this.fn_convertToHierarchyLink(this.m_o_a_API_PARAM_TEST['body']);
      this.m_link_API_PARAM_TEST_query = this.fn_convertToHierarchyLink(this.m_o_a_API_PARAM_TEST['query']);

      this.m_option_assert_field = a_assert_field_header.concat(a_assert_field_body);

      //-- for cpapireq object
      let is_set_testdata = false;   //-- api testdata를 설정
      this.m_cpapireq = {
        'header': fn_api_paramtest_object.fn_get_api_param_object(udf_param_array['header'], is_set_testdata),
        'body': fn_api_paramtest_object.fn_get_api_param_object(udf_param_array['body'], is_set_testdata),
        'query': fn_api_paramtest_object.fn_get_api_param_object(udf_param_array['query'], is_set_testdata),
      };
    },
    //-- 화면초기화
    fn_ui_clear_edit() {
      //-- v-model clear
      this.m_model_testcase['assert_case'] = '';
      this.m_model_testcase['assert_field'] = '';
      this.m_model_testcase['assert_operator'] = '';
      this.m_model_testcase['assert_value'] = '';

      if (true == this.fn_is_param_gub_json_mode()) {
        this.m_model_testcase['param_header_json'] = '';
        this.m_model_testcase['param_body_json'] = '';
      }
      else {
        //-- <param-edit/> input clear
        let a_model_testcase_key = ['param_header', 'param_body', 'param_query'];
        ['header', 'body', 'query'].forEach((paramgroup_key, idx) => {
          let testcase_key = a_model_testcase_key[idx];
          this.m_model_testcase[testcase_key] = [];
          this.m_o_a_API_PARAM_TEST[paramgroup_key].forEach((param_test, sub_idx) => {
            param_test['value'] = '';
            this.m_o_a_API_PARAM_TEST[paramgroup_key].splice(sub_idx, 1, param_test);  //-- for ui update
          });
        });
        //--##$('.cid_param_edit_input').val(''); //-- clear with jquery
      }
    },
    //-- load testdata
    fn_load_testdata() {
      ['header', 'body', 'query'].forEach((paramgroup_key, idx) => {
        this.m_o_a_API_PARAM_TEST[paramgroup_key].forEach((param_test, sub_idx) => {
          let fixedvalue = this.$sf_str(param_test['FIXED_VALUE']);
          let exam = this.$sf_str(param_test['EXAM']);
          let required = ('Y' == param_test['REQUIRED'].toUpperCase());
          let value = ((fixedvalue.length > 0) ? fixedvalue : exam);
          if (false == required) { value = value.trim(); }
          param_test['value'] = value;
          this.m_o_a_API_PARAM_TEST[paramgroup_key].splice(sub_idx, 1, param_test);  //-- for ui update
        });
      });
    },
    //-- @editor }

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
        if (0 == o_item['PRNTS_PARAM_NO']) { continue; }  //-- skip over root node.
        let parent_idx = lc_fn_get_parent_idx(o_item, a_items);
        if (-1 == parent_idx) { continue; }
        o_node['parent_idx'] = parent_idx;
        //--@@a_nodes[parent_idx].children.push(o_node);
        a_nodes[parent_idx].children.splice(0, 0, o_node); //-- 맨앞에삽입
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
        if (0 == o_item.node['PRNTS_PARAM_NO']) { continue; }  //-- skip over root node.
        let parent = getParent(o_item, a_nodes);
        if (null == parent) { continue; }
        o_item.parent = parent;
        //--@@parent.children.push(o_item);
        parent.children.splice(0, 0, o_item); //-- 맨앞에삽입
        a_nodes.splice(n_ii, 1);
      }
      //--What remains in a_nodes will be the root nodes.
      return a_nodes;
    },
    //-- @hierarchy }

    //-- @function {
    //-- testcase 저장
    fn_testcase_save(cmd) {
      this.$console_log('trace', 'fn_testcase_save()', 'cmd: ', cmd);

      //-- input validation and param setting to model
      if (this.fn_validation() == false) {
        return;
      }

      this.$console_log('watch', 'fn_testcase_save.', 'this.m_model_testcase: ', this.m_model_testcase);
      //--##this.$console_log('watch', 'fn_testcase_save.', 'this.$sf_json_stringify(this.m_model_testcase): ', this.$sf_json_stringify(this.m_model_testcase));
      //--##this.$console_log('watch', 'fn_testcase_save.', 'this.$sf_json_stringify(this.m_model_testcase, null, 2): ', this.$sf_json_stringify(this.m_model_testcase, null, 2));
      //--##this.$console_log('watch', 'fn_testcase_save.', 'this.$sf_json_stringify(this.m_model_testcase[body], null, 2): ', this.$sf_json_stringify(this.m_model_testcase['body'], null, 2));
      
      //-- :@trans data
      let req_trans = (((this.fn_is_mode_UPDATE() == false) || ('saveas' == cmd)) ? 'ins' : 'upd');

      //-- clone object
      let req_model_testcase = this.$obj_clone(this.m_model_testcase);
      req_model_testcase['param_header_json'] = this.m_model_testcase['param_header_json'];
      req_model_testcase['param_body_json'] = this.m_model_testcase['param_body_json'];
      req_model_testcase['param_header'] = this.$sf_json_stringify(this.m_model_testcase['param_header']);
      req_model_testcase['param_body'] = this.$sf_json_stringify(this.m_model_testcase['param_body']); 
      req_model_testcase['param_query'] = this.$sf_json_stringify(this.m_model_testcase['param_query']);
      if (true == this.fn_is_param_gub_form_mode()) {
        req_model_testcase['infoview_yn_checked'] = false;
      }
      req_model_testcase['infoview_yn'] = (req_model_testcase['infoview_yn_checked'] ? 'Y' : 'N');

      this.fn_call_api_apiTestCaseTrans(req_trans, req_model_testcase);
    },
    fn_validation() {
      this.$console_log('trace', 'fn_validation()');

      let o_data = this.m_model_testcase;

      //-- mandatory {
      if (this.$input_invalid(this.$is_empty(o_data['api_no']), 'ApiNo 항목이 설정되지 않았습니다.')) { return false; }
      if (this.$input_invalid(this.$is_empty(o_data['testcase_nm']), 'TestCase명 항목을 입력하세요.')) { return false; }
      if (this.$input_invalid(!this.fn_is_valid_param_gub_mode(), '파라미터 입력방식을 선택하세요.')) { return false; }
      if (this.$input_invalid(this.$is_empty(o_data['assert_case']), 'Assertion Case 항목을 선택하세요.')) { return false; }
      if (o_data['assert_field'].length > 0) {
        if (this.$input_invalid(this.$is_empty(o_data['assert_operator']), 'Assertion Operator 항목을 입력하세요.')) { return false; }
        if (this.$input_invalid(this.$is_empty(o_data['assert_value']), 'Assertion Value 항목을 입력하세요.')) { return false; }
      }
      else {
        o_data['assert_operator'] = '';
        o_data['assert_value'] = '';
      }
      //-- mandatory }

      //-- 상위 param의 required를 판단/반영
      let lc_fn_get_parents_required = ((api_no, a_param_test, def_required) => {
        if (0 == api_no) { return def_required; }
        let param_test = a_param_test.find((item) => { return (item['API_NO'] == api_no); });  //-- find parent
        if (null == param_test) { return def_required; } //-- parent 없음
        let datatype = param_test['DATA_TYPE_CD_NM'].toLowerCase();
        let required = ('Y' == param_test['REQUIRED'].toUpperCase());
        if (0 == param_test['PRNTS_PARAM_NO']) { return required; } //-- root node면 required return
        if (('object' == datatype) || ('array' == datatype)) { return false; }
        if (false == param_test['REQUIRED']) { return false; } //-- parent가 필수가 아니면 return
        return lc_fn_get_parents_required(param_test['PARNTS_PARAM_NO'], a_param_test, required); //-- recursive call
      });

      let lc_fn_param_valid = ((title, param_test, a_param_test) => {
        let paramnm = param_test['PARAM_NM'];
        let datatype = param_test['DATA_TYPE_CD_NM'].toLowerCase();
        let required = param_test['required'];
        let input = param_test['input'];
        let value = param_test['value'];
        let is_value_exist = (this.$sf_str(value).length > 0);
        
        if (true == input) {
          required = lc_fn_get_parents_required(param_test['PRNTS_PARAM_NO'], a_param_test, required);  // 상위 param의 required를 판단/반영
          if (required) {
            //--@@if (this.$input_invalid(this.$is_empty(value), title + ' \'' + paramnm + '\' 항목을 입력하세요.(필수항목)')) { return false; }
            //--[drm][cmt][empty value permit - for not send]
            //--@@if (this.$input_invalid((is_value_exist == false), title + ' \'' + paramnm + '\' 항목을 입력하세요.(필수항목)')) { return false; }
          }
          if ('integer' == datatype) {
            if ((true == is_value_exist) && (false == this.m_con_ignore_integer_check)) {
              if (this.$input_invalid(!this.$is_integer(value), title + ' \'' + paramnm + '\' 항목의 형식을 확인 하세요.')) { return false; }
            }
          }
          else if ('number' == datatype) {
            if ((true == is_value_exist) && (false == this.m_con_ignore_number_check)) {
              if (this.$input_invalid(!this.$is_number(value), title + ' \'' + paramnm + '\' 항목의 형식을 확인 하세요.')) { return false; }
            }
          }
          else if ('boolean' == datatype) {
            if ((true == is_value_exist) && (false == this.m_con_ignore_boolean_check)) {
              if (this.$input_invalid(!this.$is_boolean(value), title + ' \'' + paramnm + '\' 항목의 형식을 확인 하세요.')) { return false; }
            }
          }
        }
        return true;
      });

      let is_valid = true; 
      if (true == this.fn_is_param_gub_json_mode()) {
        if (this.$is_empty(o_data['param_header_json']) == false) {
          if (this.$input_invalid(!this.$is_json_str(o_data['param_header_json']), 'header의 입력 형식이 유효하지 않습니다.')) { return false; }
        }
        if (this.$is_empty(o_data['param_body_json']) == false) {
          if (this.$input_invalid(!this.$is_json_str(o_data['param_body_json']), 'body의 입력 형식이 유효하지 않습니다.')) { return false; }
        }
      }
      else {
        let a_title = ['Header', 'Body', 'Query'];
        let a_model_testcase_key = ['param_header', 'param_body', 'param_query'];
        is_valid = ['header', 'body', 'query'].every((paramgroup_key, idx) => {
          let testcase_key = a_model_testcase_key[idx];
          this.m_model_testcase[testcase_key] = [];  //-- param_test value 초기화
          //-- paramgroup별 input validation
          return this.m_o_a_API_PARAM_TEST[paramgroup_key].every((rs_param_test, sub_idx, a_param_test) => {
            //-- validation
            if (lc_fn_param_valid(a_title[idx], rs_param_test, a_param_test) == false) { return false; }
            if (true == rs_param_test['input']) { //-- input param인 경우 param value 저장
              //-- :@save value
              let udf_param = fn_api_paramtest_object.fn_get_udf_param_object(rs_param_test);
              udf_param['v'] = rs_param_test['value'];
              this.m_model_testcase[testcase_key].push(udf_param);
            }
            return true;
          });
        });
      }

      return is_valid;
    },
    fn_get_json_string(paramgroup_key) {
      let idx = ['header', 'body', 'query'].indexOf(paramgroup_key);
      if (-1 == idx) { return; }

      //-- ui input -> udf_param_array
      let a_udf_param = [];
      let a_param_test = this.m_o_a_API_PARAM_TEST[paramgroup_key];
      a_param_test.forEach((rs_param_test, idx, a_param_test) => {
        if (true == rs_param_test['input']) { //-- input param인 경우 param value 저장
          let udf_param = fn_api_paramtest_object.fn_get_udf_param_object(rs_param_test);
          udf_param['v'] = rs_param_test['value'];
          a_udf_param.push(udf_param);
        }
      });

      //-- udf_param_array -> object
      let o_api_param = fn_api_paramtest_object.fn_assign_testcase_value(this.m_cpapireq[paramgroup_key], a_udf_param);
      return this.$sf_json_stringify(o_api_param, null, 2);
    },
    fn_set_json_object(paramgroup_key, o_param_json) {
      let idx = ['header', 'body', 'query'].indexOf(paramgroup_key);
      if (-1 == idx) { return; }

      let assigned_value_cnt = 0;
      this.m_o_a_API_PARAM_TEST[paramgroup_key].forEach((param_test, sub_idx) => {
        if (true == param_test['input']) { //-- input param인 경우 param value 설정
          let value = '';
          let paramnmfull = param_test['PARAM_NM_FULL'];
          let a_paramnmfull = paramnmfull.split('.');
          let paramnm = a_paramnmfull[a_paramnmfull.length - 1];
          let param_node = fn_api_paramtest_object.fn_get_param_node(o_param_json, paramnmfull);
          if ((param_node != null) && ('object' == typeof(param_node))) {
            if ((Array.isArray(param_node) == true) && (param_node.length > 0)) {
              if ('object' != typeof(param_node[0])) {
                value = param_node[0];
                assigned_value_cnt++;
              }
            }
            else {
              if (this.$has_own(param_node, paramnm) == true) { 
                value = param_node[paramnm];
                if ('object' != typeof(value)) {
                  assigned_value_cnt++;
                }
              }
            }
          }
          param_test['value'] = this.$sf_str(value);
          this.m_o_a_API_PARAM_TEST[paramgroup_key].splice(sub_idx, 1, param_test);  //-- for ui update
        }
      });
      if (assigned_value_cnt > 0) {
        alert_message('입력하신 JSON Object에서 ' + assigned_value_cnt + '개의 값을 설정 하였습니다.', 'Test Case'); return;
      }
    },
    fn_is_mode_UPDATE() { return (('update' == this.m_proc_mode) && (this.m_testcase_id.length > 0)) },
    fn_proc_finished(req_trans, req_model_testcase) {
      let ret_data = { 'trans': req_trans };
      if (('ins' == req_trans) || ('upd' == req_trans) || ('del' == req_trans)) {
        //-- emit {
        //--[i] JSON.parse()의 값은 this.m_model_testcase['param_header'], ['param_body'], ['param_query']의 array와 같음
        req_model_testcase['param_header'] = this.$sf_json_parse(req_model_testcase['param_header']);
        req_model_testcase['param_body'] = this.$sf_json_parse(req_model_testcase['param_body']);
        req_model_testcase['param_query'] = this.$sf_json_parse(req_model_testcase['param_query']);
        ret_data = Object.assign(ret_data, { 'testcase': req_model_testcase });
      }
      this.$emit('emit_proc_finished', ret_data);
      this.$console_log('o-o', 'fn_proc_finished().', '$emit(emit_proc_finished)', 'ret_data: ', ret_data);
      //-- emit }
    },
    fn_dialog_close_with_emit() {
      this.fn_proc_finished('close');
      this.fn_dialog_close();
    },
    fn_dialog_close() {
      this.$modal.hide(this.prop_name);
    },
    fn_JsonEdit_dialog_ok() {
      let o_param_json = {};
      
      if (this.m_model_json_string.length > 0) {
        //-- validation {
        if (this.$is_json_str(this.m_model_json_string) == false) {
          alert_message('JSON parse error', 'Test Case');
          return;
        }
  
        o_param_json = this.$sf_json_parse(this.m_model_json_string);
        if (null == o_param_json) {
          alert_message('JSON object가 유효하지 않습니다. (null)', 'Test Case'); return;
        }
        if (Array.isArray(o_param_json) == true) {
          alert_message('JSON object가 유효하지 않습니다. (배열)', 'Test Case'); return;
        }
        //-- validation }
      }

      if (true == this.fn_is_param_gub_json_mode()) {
        if ('header' == this.m_json_paramgroup_key) {
          this.m_model_testcase['param_header_json'] = this.m_model_json_string;
        }
        else if ('body' == this.m_json_paramgroup_key) {
          this.m_model_testcase['param_body_json'] = this.m_model_json_string;
        }
      }
      else {
        this.fn_set_json_object(this.m_json_paramgroup_key, o_param_json);
      }
      this.$modal.hide('modalJsonEditDialog');
    },
    fn_JsonEdit_dialog_close() {
      this.$modal.hide('modalJsonEditDialog');
    },
    fn_is_param_gub_json_mode() {
      return (this.m_model_testcase['param_gub'] == this.m_enum_PARAM_GUB_JSON);
    },
    fn_is_param_gub_form_mode() {
      return (this.m_model_testcase['param_gub'] == '');
    },
    fn_is_valid_param_gub_mode() {
      return (this.fn_is_param_gub_form_mode() || this.fn_is_param_gub_json_mode());
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
      <button type="button" class="popup_header_button" role="button" @click.stop="fn_dialog_close_with_emit()" title="Close">
        <span class="button_close">Close</span>
      </button>
    </div>
    <div class="pop_ver pop_ver2">
      <div class="popup_content distribution_cont">
        <div class="scroll_wrap">

          <div class="pkg_board">
            <table class="table-vw table-vw6">
              <caption>Testcase Table</caption>
              <colgroup><col style="width:15.5%;"><col style="width:34.5%;"><col style="width:15.5%;"><col style="width:34.5%;"></colgroup>
              <tbody>
                <tr>
                  <th scope="row"><div>Test Case 명</div></th>
                  <td><div class="popup_input"><input type="text" maxlength="20" v-model="m_model_testcase['testcase_nm']"></div></td>
                  <th scope="row"><div>API명</div></th>
                  <td><div>{{m_api_nm}}</div></td>
                </tr>
                <tr>
                  <th colspan="2" scope="row">
                    <div>요청 Parameter</div>
                  </th>
                  <th scope="row" class="test_gub_select">
                    <div v-show="(true == fn_is_valid_param_gub_mode())">
                      <select class="popup_select" v-model="m_model_testcase['param_gub']">
                        <option value="">양식입력</option>
                        <option :value="m_enum_PARAM_GUB_JSON">JSON입력</option>
                      </select>
                    </div>
                  </th>
                  <th scope="row" class="test_btn">
                    <div v-show="(true == fn_is_valid_param_gub_mode())">
                      <button type="button" title="초기화" class="btn btn_white btn_sml" @click.stop="onclick_btn_clear()">초기화</button>
                      <template v-if="(true == fn_is_param_gub_form_mode())">
                        <button type="button" title="TESTDATA 입력" class="btn btn_white btn_sml_130" @click.stop="onclick_btn_testdata()">TESTDATA 입력</button>
                      </template>
                      <template v-if="(true == fn_is_param_gub_json_mode())">
                        <span class="chk_infoview"><input id="id_chk_infoview" type="checkbox" v-model="m_model_testcase['infoview_yn_checked']"><label for="id_chk_infoview"><span></span>이용가이드</label></span>
                      </template>
                    </div>
                  </th>
                </tr>
              </tbody>
            </table>
          </div>

          <template v-if="(true == fn_is_param_gub_json_mode())">

            <!-- header { -->
            <div class="pkg_board">
              <div class="top_head">
                <p>Header</p>
                <p><button type="button" title="JSON 입력" class="btn btn_white btn_sml" @click.stop="onclick_btn_json('header')">JSON 입력</button></p>
              </div>
              <div class="scroll_view_json_wrap">
                <div class="scroll_view_json">
                  <vue-json-pretty v-show="(m_model_testcase['param_header_json'].length > 0)"
                    :path="'root'"
                    :data="$sf_json_parse(m_model_testcase['param_header_json'])"
                    @click="handle_click_vueJsonPretty"
                    @change="handle_change_vueJsonPretty"
                    :show-line="true"
                    :show-double-quotes="true"
  
                    :highlight-mouseover-node="true"
                    :show-length="true"
                  >
                  </vue-json-pretty>
                </div><!-- .scroll_view_json -->
              </div><!-- .scroll_view_json_wrap -->

            </div><!-- .pkg_board -->
            <!-- header } -->

            <!-- body { -->
            <div class="pkg_board">
              <div class="top_head">
                <p>Body</p>
                <p><button type="button" title="JSON 입력" class="btn btn_white btn_sml" @click.stop="onclick_btn_json('body')">JSON 입력</button></p>
              </div>
              <div class="scroll_view_json_wrap">
                <div class="scroll_view_json">
                  <vue-json-pretty v-show="(m_model_testcase['param_body_json'].length > 0)"
                    :path="'root'"
                    :data="$sf_json_parse(m_model_testcase['param_body_json'])"
                    @click="handle_click_vueJsonPretty"
                    @change="handle_change_vueJsonPretty"
                    :show-line="true"
                    :show-double-quotes="true"
  
                    :highlight-mouseover-node="true"
                    :show-length="true"
                  >
                  </vue-json-pretty>
                </div><!-- .scroll_view_json -->
              </div><!-- .scroll_view_json_wrap -->

            </div><!-- .pkg_board -->
            <!-- body } -->
    
          </template><!-- v-if="(true == fn_is_param_gub_json_mode())" -->
          <template v-if="(true == fn_is_param_gub_form_mode())">

            <!-- header { -->
            <div class="pkg_board" v-show="(m_link_API_PARAM_TEST_header.length > 0)">
              <div class="top_head">
                <p>Header</p>
                <p>
                  <button type="button" title="JSON 입력" class="btn btn_white btn_sml" @click.stop="onclick_btn_json('header')">JSON 입력</button>
                  <em>*</em>필수
                </p>
              </div>
              <table class="table-vw table-vw3 ">
                <caption>Header Table</caption>
                <colgroup><col style="width:45%;"><col style="width:15%;"><col style="width:auto;"></colgroup>
                <thead><tr><th>Parameter</th><th>Type</th><th>Value</th></tr></thead>
              </table>
              <div class="scroll_box_header_param">
        
                <div class="div_depth">
                  <div class="btn_addParabox"><span>파라미터 추가</span></div>
                  <param-edit prop_paramGroup="header" :prop_paramRs="m_o_a_API_PARAM_TEST['header']" :prop_paramLink="m_link_API_PARAM_TEST_header" :prop_readonly="m_readonly" @emit_update="emit_update"></param-edit>
                </div><!-- .div_depth -->
  
              </div><!-- .scroll_box_header_param -->
            </div><!-- .pkg_board -->
            <!-- header } -->
    
            <!-- body { -->
            <div class="pkg_board" v-show="(m_link_API_PARAM_TEST_body.length > 0)">
              <div class="top_head">
                <p>Body</p>
                <p>
                  <button type="button" title="JSON 입력" class="btn btn_white btn_sml" @click.stop="onclick_btn_json('body')">JSON 입력</button>
                  <em>*</em>필수
                </p>
              </div>
              <table class="table-vw table-vw3 ">
                <caption>Body Table</caption>
                <colgroup><col style="width:45%;"><col style="width:15%;"><col style="width:auto;"></colgroup>
                <thead><tr><th>Parameter</th><th>Type</th><th>Value</th></tr></thead>
              </table>
              <div class="scroll_box_body_param">
            
                <div class="div_depth">
                  <div class="btn_addParabox"><span>파라미터 추가</span></div>
                  <param-edit prop_paramGroup="body" :prop_paramRs="m_o_a_API_PARAM_TEST['body']" :prop_paramLink="m_link_API_PARAM_TEST_body" :prop_readonly="m_readonly" @emit_update="emit_update"></param-edit>
                </div><!-- .div_depth -->
            
              </div><!-- .scroll_box_body_param -->
            </div><!-- .pkg_board -->
            <!-- body } -->
    
            <!-- query { -->
            <div class="pkg_board" v-show="(m_link_API_PARAM_TEST_query.length > 0)">
              <div class="top_head">
                <p>Query</p>
                <p>
                  <button type="button" title="JSON 입력" class="btn btn_white btn_sml" @click.stop="onclick_btn_json('query')">JSON 입력</button>
                  <em>*</em>필수
                </p>
              </div>
              <table class="table-vw table-vw3">
                <caption>Query Table</caption>
                <colgroup><col style="width:45%;"><col style="width:15%;"><col style="width:auto;"></colgroup>
                <thead>
                  <thead><tr><th>Parameter</th><th>Type</th><th>Value</th></tr></thead>
                </thead>
              </table>
              <div class="scroll_box_query_param">
  
                <div class="div_depth">
                  <div class="btn_addParabox"><span>파라미터 추가</span></div>
                  <param-edit prop_paramGroup="query" :prop_paramRs="m_o_a_API_PARAM_TEST['query']" :prop_paramLink="m_link_API_PARAM_TEST_query" :prop_readonly="m_readonly" @emit_update="emit_update"></param-edit>
                </div><!-- .div_depth -->
  
              </div><!-- .scroll_box_query_param -->
            </div><!-- .pkg_board -->
            <!-- query } -->

          </template> <!-- v-if="(true == fn_is_param_gub_form_mode())" -->

          <!-- assertion { -->
          <div class="pkg_board" v-show="(true == fn_is_valid_param_gub_mode())">
            <!-- writeform -->
            <h3>Assertion</h3>
            <div class="check_box">
              <div class="info_check_box">
                <p>
                  <input type="radio" id="testGubun_01" name="testGubun" title="정상 Case" value="normal" v-model="m_model_testcase['assert_case']"><label for="testGubun_01"><span></span>정상 Case</label>
                  <input type="radio" id="testGubun_02" name="testGubun" title="예외 Case" value="except" v-model="m_model_testcase['assert_case']"><label for="testGubun_02"><span></span>예외 Case</label>
                </p>
              </div>
            </div>
            <table class="table-vw table-vw6">
              <caption>Testcase Table</caption>
              <colgroup><col style="width:15.5%;"><col style="width:15.5%;"><col style="width:15.5%;"><col style="width:auto;"></colgroup>
              <tbody>
                <tr>
                  <th scope="row"><div>Field</div></th>
                  <td colspan="3">
                    <select class="popup_select" v-model="m_model_testcase['assert_field']" @change="onchange_assert_field()">
                      <option value="">선택</option>
                      <option v-for="field in m_option_assert_field" :value="field['v']">{{field['t']}}</option>
                    </select>
                  </td>
                </tr>
                <tr>
                  <th scope="row"><div>Operator</div></th>
                  <td>
                    <select class="popup_select" v-model="m_model_testcase['assert_operator']">
                      <option v-for="operator in m_option_assert_operator" :value="operator">{{operator}}</option>
                    </select>  
                  </td>
                  <th scope="row"><div>Value</div></th>
                  <td><div class="popup_input"><input type="text" name="" title="" placeholder="" v-model="m_model_testcase['assert_value']"></div></td>
                </tr>
              </tbody>
            </table>
          </div><!-- .pkg_board -->
          <!-- assertion } -->
  
        </div><!-- .scroll_wrap -->

        <div class="brd_tp process_btn">
          <template v-if="('y' == m_model_testcase['is_queried'])">
            <button type="button" :class="['btn', 'btn_gray', 'btn_sml', (ui_ctrl_btn_saveas_disabled ? 'disabled' : '')]" :disabled="ui_ctrl_btn_saveas_disabled"  
              v-if="('y' == m_model_testcase['is_queried'])" @click.stop="onclick_btn_saveas()" title="TEST CASE 새로저장">새로저장</button>
            <button type="button" :class="['btn', 'btn_gray', 'btn_sml', (ui_ctrl_btn_upd_disabled ? 'disabled' : '')]" :disabled="ui_ctrl_btn_upd_disabled"  
              @click.stop="onclick_btn_save()" title="TEST CASE 수정">수정</button>
          </template>
          <template v-else>
            <button type="button" :class="['btn', 'btn_gray', 'btn_sml', (ui_ctrl_btn_new_disabled ? 'disabled' : '')]" :disabled="ui_ctrl_btn_new_disabled"  
              @click.stop="onclick_btn_save()" title="TEST CASE 저장">저장</button>
          </template>
          <button type="button" class="btn btn_sml" @click.stop="fn_dialog_close_with_emit()" title="닫기">닫기</button>
        </div>
  
      </div><!-- .popup_content -->
    </div><!--.pop_ver pop_ver2 -->
    
    <!-- popup-[json입력] -->
    <sc-vuemodal
      ref="modalJsonEditDialog"
      name="modalJsonEditDialog"
      width="680"
      height="600"
      :clickToClose="false"
      :draggable="false"
      :resizable="false"
      :scrollable="false"
      :adaptive="false"
      :minWidth="340"
      :minHeight="300"
    >
      <div class="popup_header">
        <span class="popup_header_title">JSON 입력</span>
        <button type="button" class="popup_header_button" role="button" @click.stop="fn_JsonEdit_dialog_close()" title="Close">
          <span class="button_close">Close</span>
        </button>
      </div><!-- .popup_header -->
      <div class="pop_ver pop_ver2">
        <div class="popup_content distribution_cont">
          <div class="pkg_board">
            <textarea spellcheck="false" class="scroll_box_json" v-model="m_model_json_string"></textarea>
          </div>
          <div class="brd_tp process_btn">
            <button type="button" title="취소" class="btn btn_sml" @click.stop="fn_JsonEdit_dialog_close()">취소</button>
            <button type="button" title="확인" class="btn btn_gray btn_sml" @click.stop="fn_JsonEdit_dialog_ok()">확인</button>
          </div>
        </div>
      </div><!-- .pop_ver pop_ver2 -->
    </sc-vuemodal>
    
  </div><!-- .popup_wrap -->
</template>

<style scoped>
  /*add for modal dialog*/
  .pkg_board .table-vw {text-align:center}

  /*add for local*/
  .distribution_cont {height: calc(100% - 35px - 35px);}/*100% - padding - 버튼area*/
  .distribution_cont .scroll_box_header_param {height:auto; max-height: 208px; overflow: auto; margin-bottom: 20px;}
  .distribution_cont .scroll_box_body_param {height:auto; max-height: 208px; overflow: auto; margin-bottom: 20px;}
  .distribution_cont .scroll_box_query_param {height:auto; max-height: 208px; overflow: auto; margin-bottom: 20px;}
  .distribution_cont .scroll_box_json {height: 440px; width:97%; overflow: auto; background-color:#404040; color: #fff766;}

  /*.btn.disabled{color:rgba(255,255,255,0.5) !important;}*/

  .distribution_cont .scroll_view_json_wrap {margin-top: 5px; padding:10px; background-color:#f7f7f7; }
  .distribution_cont .scroll_view_json {height:208px; min-height: 208px; overflow: auto; background-color:#f7f7f7; }

</style>
