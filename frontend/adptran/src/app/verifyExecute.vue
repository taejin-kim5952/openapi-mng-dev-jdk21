<comment>
  @verify Execute
</comment>
<script>
import { CommonMixin } from '@/common/adptran.js';
import { fn_api_paramtest_object } from '@/common/fn_api_paramtest_object.js';
//-- popup-[testcase]
import testcaseDialog from '@approot/dialog/testcaseDialog.vue';
//-- popup-[testcase불러오기]
import testcaseListDialog from '@approot/dialog/testcaseListDialog.vue';
//-- popup-[verify상세보기]
import verifyViewDialog from '@approot/dialog/verifyViewDialog.vue';

export default {
  name: 'verifyExecute',
  components: {
    'testcase-dialog': testcaseDialog,
    'testcase-list-dialog': testcaseListDialog,
    'verify-view-dialog': verifyViewDialog,
  },
  mixins: [CommonMixin],
  props: {
    prop_api_no: {
      type: String,
      required: true,
      validator: function(value) { return (parseInt(value, 10) > 0); },
    },
    prop_proc_seq: {
      type: String,
      required: true,
      validator: function(value) { return (parseInt(value, 10) >= 0); },
    },
    prop_gw_profile: {
      type: String,
      required: true,
      validator: function(value) { return ((value == 'TB') || (value == 'PROD') || (value == 'VERI')); },
    },
    //--[tag:sr-20201001][add]
    prop_fn_emit: {
      type: String,
      default: '',
    },
  },
  data() {
    return {
      m_vue_id: 'verifyExecute',

      //-- @property {
      m_prop_testcaseData: {},
      m_prop_testcaseListData: {},
      m_prop_verifyViewData: {},
      //-- @property }

      //-- @biz-data {
      m_api_no: this.prop_api_no,
      m_proc_seq: this.prop_proc_seq,
      m_gw_profile: this.prop_gw_profile,
      //--[tag:sr-20201001][add]
      m_fn_emit: (void 0),

      //-- api정보query
      m_api_nm: '',
      m_api_url: '',
      //-- [tag:PRJ-20220901]
      m_api_veri_baseurl: '',
      //--##m_api_handler_cd_nm: '',
      m_is_apiDefWithApiSpc_loaded: false,	//-- fn_call_api_apiDefWithApiSpc() queried

      m_added_header: {},

      m_cur_tab_idx: 0, //-- current tab index
      m_total_vefify_result: 0, // 0:초기, 1:진행: 2:성공, 3:실패
      //-- @biz-data }

      //-- @ui-data {
      /*--[ref]
        m_model_testcase_data: {
          'testcase_id':, 'testcase_nm':, 'amd_dt':, 'param_gub':, 'param_header': [], 'param_body': [], 'param_query': [],
          'param_header_json':, 'param_body_json':,
          'assert_case':, 'assert_field':, 'assert_operator':, 'assert_value':,
          'cpapireq_header': {}, 'cpapireq_body': {},
          'verify_result':, 'verifi_proc_succ':, 'verify_seq':, 'verify_dur_msec':
        };
      --*/
      m_model_testcase_data: [],  //-- testcase data
      m_model_testcase_data_filtered: [], //-- testcase data filter for ui
      //-- @ui-data }
    } // return{}
  }, // data()
  computed: {
    compute_testcase_verify_ing() {
      return (this.m_enum_VERIFY_ING == this.m_total_vefify_result);
    },
    computed_testcase_info() {
      return ((testcase_item, cmd) => {
        let result = '';
        if ('testcase_nm' == cmd) {
          return testcase_item['testcase_nm'];
        }
        else if ('assert_case' == cmd) {
          return ((testcase_item['assert_case'] == 'normal') ? '정상' : ((testcase_item['assert_case'] == 'except') ? '예외' : '-'));
        }
        else if ('btn_testcase_update' == cmd) {
          return (testcase_item['testcase_id'] != 'default'); //-- 기본 Test Data제외
        }
        else if ('css_result_text' == cmd) {
          if (this.m_enum_VERIFY_SUCC == testcase_item['verify_result']) { result = 'blue_txt'; }
          else if (this.m_enum_VERIFY_FAIL == testcase_item['verify_result']) { result = 'red_txt'; }
          return result;
        }
        else if ('css_verify_ing' == cmd) {
          if (this.m_enum_VERIFY_ING == testcase_item['verify_result']) { result = 'loadersmall'; }
          return result;
        }
        else if ('btn_testcase_result_detail' == cmd) {
          let verify_seq = this.$sf_int(testcase_item['verify_seq'], 0);
          return (verify_seq > 0);
        }
        else if ('verify_result' == cmd) {
          if (this.m_enum_VERIFY_SUCC == testcase_item['verify_result']) { result = '성공'; }
          else if (this.m_enum_VERIFY_FAIL == testcase_item['verify_result']) { result = '실패'; }
          else if (this.m_enum_VERIFY_INIT == testcase_item['verify_result']) { result = '-'; }
          else if (this.m_enum_VERIFY_ING == testcase_item['verify_result']) { result = ''; }
          else { result = '?'; }
          return result;
        }
        else if ('verify_dur_msec' == cmd) {
          return testcase_item['verify_dur_msec']
        }
        return result;
      });
    },
    computed_testcase_verify_info() {
      return ((cmd) => {
        if ('verify_succ_count' == cmd) {
          if (this.m_enum_VERIFY_INIT == this.m_total_vefify_result) { return '-'; }
          return this.m_model_testcase_data.filter((testcase) => {
            return (this.m_enum_VERIFY_SUCC == testcase['verify_result']);
          }).length;
        }
        else if ('verify_fail_count' == cmd) {
          if (this.m_total_vefify_result == this.m_enum_VERIFY_INIT) { return '-'; }
          return this.m_model_testcase_data.filter((testcase) => {
            return (this.m_enum_VERIFY_FAIL == testcase['verify_result']);
          }).length;
        }
      });
    }
  },
  watch: {
    m_cur_tab_idx: { 
      handler: function(newVal, oldVal) {
        this.fn_filter_testcase_data(newVal);
      }
    },
    //--m_model_testcase_data_filtered: { handler: function(newVal, oldVal) {}, deep: true },
  },  
  created: function() {
    this.$console_log('trace', 'created()');
    this.m_total_vefify_result = this.m_enum_VERIFY_INIT;
    this.m_added_header = this.m_con_def_header_verify;
    //--[tag:sr-20201001][add] {
    //--[i][assign emit function at parent]
    this.m_fn_emit = eval(this.prop_fn_emit);
    if (typeof(this.m_fn_emit) != 'function') {
      this.m_fn_emit = (function() {
        this.$console_log('warn', 'called m_fn_emit without define', 'arguments: ', arguments);
      });
    }
    //--[tag:sr-20201001][add] }
  },
  mounted: function() {
    this.$console_log('trace', 'mounted()');
    if (this.m_api_no.length > 0) {
      this.fn_call_api_apiDefWithApiSpc(this.m_api_no);
    }
  },
  updated: function() {
    this.$console_log('trace', 'updated()');
  },
  methods: {
    //-- @handler {
    onclick_btn_tab(tab_idx) {
      if (tab_idx != this.m_cur_tab_idx) {
        this.m_cur_tab_idx = tab_idx;
      }
    },
    onclick_btn_testcase_edit() { //-- 추가
      if (this.compute_testcase_verify_ing == true) { return; }
      this.fn_popup_testcase_edit();
    },
    onclick_btn_testcase_list() { //-- 불러오기
      if (this.compute_testcase_verify_ing == true) { return; }
      let filtered_testcase_id_list = [];
      this.m_model_testcase_data.forEach((testcase) => {
        filtered_testcase_id_list.push(testcase['testcase_id']);
      });
      this.fn_popup_testcase_list({ 'api_no': this.m_api_no, 'filtered_testcase_id_list': filtered_testcase_id_list });
    },
    onclick_btn_testcase_update(testcase_item) {  //-- 수정
      if (this.compute_testcase_verify_ing == true) { return; }
      this.fn_popup_testcase_edit(testcase_item['testcase_id']);
    },
    onclick_btn_testcase_delete(testcase_item) {  //-- 삭제
      if (this.compute_testcase_verify_ing == true) { return; }
      this.fn_del_testcase_list([testcase_item['testcase_id']]);
    },
    onclick_btn_testcase_delete_all() { //-- 전체삭제 {
      if (this.compute_testcase_verify_ing == true) { return; }
      if (this.m_model_testcase_data.length > 0) {
        this.m_model_testcase_data = [];
        this.fn_filter_testcase_data(this.m_cur_tab_idx); //-- update ui array
      }
    },
    onclick_btn_testcase_result_detail(testcase_item) { //-- 상세보기
      if (this.compute_testcase_verify_ing == true) { return; }
      let verify_seq = this.$sf_int(testcase_item['verify_seq'], 0);
      if (verify_seq > 0) {
        this.fn_popup_verify_view(verify_seq);
      }
    },
    onclick_btn_verify() {  //-- 검증
      if (this.compute_testcase_verify_ing == true) { return; }
      if (this.m_model_testcase_data.length == 0) {
        this.$adpt_alert('선택된 Test Case가 없습니다.', 'API 검증', false);
        return; 
      }
      if (this.m_is_apiDefWithApiSpc_loaded == false) {
        this.$adpt_alert('API정보가 검색되지 않았습니다.', 'API 검증', false);
        return; 
      }

      let s_msg = '선택하신 Test Case ' + this.m_model_testcase_data.length + '건에 대한 검증을 시작 하시겠습니까?';
      this.$adpt_confirm(s_msg, 'API 검증').then((response) => {
        this.fn_verify_testcase_list();
      });
    },
    //-- @handler }

    //-- @api function {
    //-- api정보 query
    fn_call_api_apiDefWithApiSpc(api_no) {
      this.$console_log('trace', 'fn_call_api_apiDefWithApiSpc()', 'api_no: ', api_no);

      this.m_api_nm = '';
      this.m_api_url = '';
      //-- [tag:PRJ-20220901]
      this.m_api_veri_baseurl = '';
      //--##this.m_api_handler_cd_nm = '';
      this.m_is_apiDefWithApiSpc_loaded = false;
    
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
        this.m_api_url = this.$sf_obj_val(record, 'API_PATH', '');
        //-- [tag:PRJ-20220901]
        this.m_api_veri_baseurl = this.$sf_obj_val(record, 'spc_API_VERI_BASEURL', '');
        //--##this.m_api_handler_cd_nm = this.$sf_obj_val(record, 'API_HANDLER_CD_NM', '');
        this.m_is_apiDefWithApiSpc_loaded = true;
      }
      else {
        this.$proc_api_resultCode_Fail(call_ret, response);
      }
    },
    
    fn_call_api_apigw_cpApiGet(req_param) {
      this.$fn_call_api_common(this.m_con_apigw_cpApiGet_url, req_param, 'post', this.fn_callback_api_apigw_cpApiGet);
    },
    //-- api cpapiget호출결과처리
    //-- call_ret: [ok | nk | failed | catched]
    //-- ok|no일시 parameter: call_ret, response, result_data
    //-- failed일시 parameter: call_ret, response
    //-- catched일시 parameter: call_ret, error
    fn_callback_api_apigw_cpApiGet(call_ret, response, api_request, result_data) {
      this.$console_log('trace', 'fn_callback_api_apigw_cpApiGet()', 'call_ret: ', call_ret, 'response :', response, 'api_request: ', api_request, 'result_data: ', result_data);

      //-- 결과정보 {
      let verifi_result_vo = null;
      let resultMessage = '';

      let verifi_hst_succ = null; //--[i] not used yet
      let verifi_proc_succ = null;

      let vo_resReturnCode = null;
      let vo_seq = null;
      let vo_successYn = null;
      let vo_stTime = null;
      let vo_endTime = null;
      //-- 결과정보 }

      if ('ok' == call_ret) { //-- 호출성공(not 처리함수성공)
        /*--[ref]
               호출 : this.fn_call_api_common(): local script
               처리함수: apigw_cpApiGet(): Controller
               호출함수: adptranApiService.apigw_cpApiGet(): Service
               호출서비스: cpApiService.get(): apigw Service
        --*/
        let response_fn_data = result_data['data']; //-- 호출-처리함수반환data
        let response_fn_resultCd = result_data['resultCd'];
        let response_fn_resultMsg = result_data['resultMsg'];

        verifi_result_vo = this.$sf_obj_val(response_fn_data, 'verifi_result_vo', null);  //-- verifi_result_vo
        verifi_hst_succ = this.$sf_obj_val(response_fn_data, 'verifi_hst_succ', ''); //-- 호출결과기록succ여부(y|n)
        verifi_proc_succ = this.$sf_obj_val(response_fn_data, 'verifi_proc_succ', '');  //-- 프로세스전환처리succ여부(y|n|'') // DEPLOY1030 -> DEPLOY1040

        if (verifi_result_vo != null) {
          //-- result basic
          /*--[ref]
          private int seq;
          private int deployProcSeq;
          private String testCaseSeq;
          private String verificationDt;  
          private String resultCd;
          private String resultMsg;
          private String verifiUsr;
          private String successYn;
          private String stTime;
          private String endTime;
          --*/
          //-- proc result
          /*--[ref]
          //-- Proc result
          private String procResultCd;
          private String procResultMsg;
          --*/
          //-- CpApiRequest
          /*--[ref]
          private String reqGwProfile;
          private String reqApiUrl;
          private String reqHeaders;
          private String reqBody;
          private String reqTransactionId;
          private String reqSequenceNo;
          --*/
          //-- CpApiResponse
          /*--[ref]
          private String resTransactionId;
          private String resSequenceNo;
          private String resReturnCode;
          private String resReturnDescription;
          private String resErrorCode;
          private String resErrorDescription;
          private String resResponse;
          --*/
          /*--[ref]
          //-- api_def / api_testcase
          private int apiNo;
          private String apiNm;
          private String testcaseNm;
          private String paramGub;
          private String paramHeader;
          private String paramBody;
          private String paramQuery;
          private String paramHeaderJson;
          private String paramBodyJson;
          private String assertCase;
          private String assertField;
          private String assertOperator;
          private String assertValue;
          --*/
          //-- 결과정보  {
          vo_resReturnCode = this.$sf_obj_val(verifi_result_vo, 'returncode', null);
          vo_seq = this.$sf_obj_val(verifi_result_vo, 'seq', null);
          vo_successYn = this.$sf_obj_val(verifi_result_vo, 'successYn', null);
          vo_stTime = this.$sf_obj_val(verifi_result_vo, 'stTime', null);
          vo_endTime = this.$sf_obj_val(verifi_result_vo, 'endTime', null);
          //-- 결과정보  }
        }
        if (vo_resReturnCode == null) {
          call_ret = 'ok_no_data';
          resultMessage = 'G/W로 부터 response를 수신하지 못했습니다.';
          resultMessage += '\n\n[resultCd: ' + response_fn_resultCd + '][resultCd: ' + response_fn_resultMsg + ']';
        }
      }
      else if (('nk' == call_ret) || ('failed' == call_ret)) {
        if ('nk' == call_ret) {
          resultMessage = 'G/W호출을 실패 하였습니다.';
        }
        else if ('failed' == call_ret) {
          resultMessage = 'G/W호출시 오류가 발생 하였습니다.';
        }
        response = response['response'];
        if ($has_own(response, 'data') == true) {
          let response_data = response['data']; //-- 호출-반환data
          let data_resultCode = response_data['resultCode'];
          let data_resultMessage = response_data['resultMessage'];
          let data = response_data['data'];
          resultMessage += '\n\n[code: ' + data_resultCode + '][메시지: ' + data_resultMessage + ']\n\n[data: ' + data + ']\n';
        }
      }
      else if ('catched' == call_ret) {
        let errorMessage = response['message'];
        resultMessage = 'G/W호출시  예외가 발생 하였습니다.\n\n[메시지: ' + errorMessage + ']';
      }
      else {
        resultMessage = 'G/W호출시  정의되지 않은 응답값을 수신 하였습니다.\n\n[call_ret: ' + call_ret + ']';
      }

      /*--[ref] api_request
      let req_param = {
        'proc_seq': this.m_proc_seq, 'gw_profile': this.m_gw_profile, 'api_url': this.m_api_url, 'headers': s_header, 'body': s_body,
        'api_no': this.m_api_no, 'api_nm': this.m_api_nm, 'api_veri_baseurl': m_api_veri_baseurl, 
        'testcase_id': testcase['testcase_id'], 'testcase_seq': testcase_seq, 'testcase_nm': testcase['testcase_nm'],
        'param_gub': testcase['param_gub'],
        'param_header': param_header, 'param_body': param_body, 'param_query': param_query,
        'param_header_json': testcase['param_header_json'], 'param_body_json': testcase['param_body_json'],
        'assert_case':  testcase['assert_case'], 'assert_field':  testcase['assert_field'], 'assert_operator':  testcase['assert_operator'], 'assert_value':  testcase['assert_value'],
      };
      --*/
      let n_StTime = this.$sf_int(vo_stTime, -1);
      let n_EndTime = this.$sf_int(vo_endTime, -1);
      let verify_dur_msec = (((n_StTime > 0) && (n_EndTime > 0)) ? (n_EndTime - n_StTime) : '-');

      let res_param = {
        'verify_result': (('Y' == vo_successYn) ? this.m_enum_VERIFY_SUCC : this.m_enum_VERIFY_FAIL),
        //--[tag:sr-20201001][i][DEPLOY1030 -> DEPLOY1040전환후 후처리용]
        'verifi_proc_succ': ((('y' == verifi_proc_succ) || ('n' == verifi_proc_succ)) ? verifi_proc_succ : ''),
        'verify_seq': this.$sf_int(vo_seq, 0),
        'verify_dur_msec': verify_dur_msec,
      };
      //-- 결과처리
      this.fn_proc_verify_testcase_result(api_request['testcase_id'], res_param);
    },
    //-- @api function }

    //-- @function {
    fn_filter_testcase_data(tab_idx) {
      let assert_case = ((1 == tab_idx) ? 'normal' : ((2 == tab_idx) ? 'except' : ''));
      this.m_model_testcase_data_filtered = this.m_model_testcase_data.filter((elem) => {
        return ((assert_case.length > 0) ? (elem['assert_case'] == assert_case) : true)
      });
    },
    //-- 검증결과정보
    fn_default_verify_object() {
      return { 'verify_result': this.m_enum_VERIFY_INIT, 'verify_seq': 0, 'verify_dur_msec': '-', };
    },
    //-- for testcase {
    fn_popup_testcase_edit(testcase_id) {
      testcase_id = this.$sf_str(testcase_id);
      let proc_mode = ((testcase_id.length > 0) ? 'update' : 'new');

      let options = { 'loadtestdata': 'n', 'readonly': 'n', 'fixedvalueedit': 'n' };
      this.m_prop_testcaseData = { 'proc_mode': proc_mode, 'api_no': this.m_api_no, 'testcase_id': testcase_id, 'options': options };
      this.$modal.show('modalTestcaseDialog');
    },
    //-- ret_data: { 'trans': req_trans, 'testcase': req_model_testcase }
    /*--[ref]
      trans: 'ins', 'upd', 'del'(not yet)
      m_model_testcase: {
        'is_queried': '', 'api_no': '', 'testcase_id': '', 'api_nm': '', 'testcase_nm': '',
        'assert_case': '', 'assert_field': '', 'assert_operator': '', 'assert_value': '',
        'param_gub': '', 'param_header_json': '', 'param_body_json': '', 
        'param_header': [], 'param_body': [], 'param_query': [],
      },
    --*/
    emit_proc_finished_testcase(ret_data) {  //-- emit when dialog job finished
      this.$console_log('trace', 'emit_proc_finished_testcase.', 'ret_data: ', ret_data);
      let trans = this.$sf_obj_val(ret_data, 'trans', '');
      if ('upd' == trans) { //-- 'testcase' is option when trans is 'ins', 'upd', 'del'
        let testcase = this.$sf_obj_val(ret_data, 'testcase', {});
        this.fn_upd_testcase_list(testcase);
      }
    },
    //-- for testcase }

    //-- for testcaselist {
    fn_popup_testcase_list(prop_param) {
      this.m_prop_testcaseListData = prop_param;
      this.$modal.show('modalTestcaseListDialog');
    },
    //-- ret_data: { 'testcaselist': a_selected_testcaselist }
    /*--[ref]
      m_model_testcase_data: {
        'testcase_id':, 'testcase_nm':, 'amd_dt':, 'param_gub':, 'param_header': [], 'param_body': [], 'param_query': [],
        'param_header_json':, 'param_body_json':,
        'assert_case':, 'assert_field':, 'assert_operator':, 'assert_value':,
        'cpapireq_header': {}, 'cpapireq_body': {},
        'verify_result':, 'verifi_proc_succ':, 'verify_seq':, 'verify_dur_msec':
      };
    --*/
    emit_proc_finished_testcaselist(ret_data) {  //-- emit when dialog job finished
      this.$console_log('trace', 'emit_proc_finished_testcaselist.', 'ret_data: ', ret_data);

      let a_selected_testcaselist = this.$sf_obj_val(ret_data, 'testcaselist', []);
      this.fn_add_testcase_list(a_selected_testcaselist);
    },
    //-- ret_data: { 'testcase_id_list': a_testcase_id_list }
    emit_proc_deleted_testcaselist(ret_data) {  //-- emit when testcase deleted
      this.$console_log('trace', 'emit_proc_deleted_testcaselist.', 'ret_data: ', ret_data);

      let a_testcase_id_list = this.$sf_obj_val(ret_data, 'testcase_id_list', []);
      this.fn_del_testcase_list(a_testcase_id_list);
    },
    //-- for testcaselist }

    //-- for verifyview {
    fn_popup_verify_view(verify_seq) {
      this.m_prop_verifyViewData = { 'verify_seq': verify_seq };
      this.$modal.show('modalVerifyViewDialog');
    },
    //-- for verifyview }

    //-- testcase 목록추가
    fn_add_testcase_list(a_testcaselist) {
      let add_cnt = 0;
      a_testcaselist.forEach((testcase) => {
        let testcase_id = testcase['testcase_id'];
        let find_idx = this.m_model_testcase_data.findIndex((sub_testcase) => {
          return (sub_testcase['testcase_id'] == testcase_id);
        });
        if (find_idx == -1) {
          let n_pos = ((testcase_id == 'default') ? 0 : this.m_model_testcase_data.length);
          //@:add-testcase
          testcase = Object.assign(testcase, this.fn_default_verify_object()); //-- verify정보추가
          this.m_model_testcase_data.splice(n_pos, 0, testcase); //-- 'default'면 맨앞, else 맨뒤에 추가
          this.$console_log('o-o', 'fn_add_testcase_list()', 'n_pos: ', n_pos);
          add_cnt++;
        }
      });
      if (add_cnt > 0) {
        this.fn_filter_testcase_data(this.m_cur_tab_idx); //-- update ui array
      }
    },
    //-- testcase 목록수정
    fn_upd_testcase_list(testcase) {
      let upd_cnt = 0;
      let testcase_id = this.$sf_obj_val(testcase, 'testcase_id', '');
      let find_idx = this.fn_get_testcase_index(testcase_id);
      if (find_idx != -1) {
        let testcase_data = this.m_model_testcase_data[find_idx];
        /*--[ref]
          m_model_testcase_data: {
            'testcase_id':, 'testcase_nm':, 'amd_dt':, 'param_gub':, 'param_header': [], 'param_body': [], 'param_query': [],
            'param_header_json':, 'param_body_json':,
            'assert_case':, 'assert_field':, 'assert_operator':, 'assert_value':,
            'cpapireq_header': {}, 'cpapireq_body': {},
            'verify_result':, 'verifi_proc_succ':, 'verify_seq':, 'verify_dur_msec':
          };
          m_model_testcase: {
            'is_queried': '', 'api_no': '', 'testcase_id': '', 'api_nm': '', 'testcase_nm': '',
            'assert_case': '', 'assert_field': '', 'assert_operator': '', 'assert_value': '',
            'param_gub': '', 'param_header_json': '', 'param_body_json': '', 
            'param_header': [], 'param_body': [], 'param_query': [],
          },
        --*/
        testcase_data['testcase_nm'] = testcase['testcase_nm'];
        testcase_data['param_gub'] = testcase['param_gub'];
        testcase_data['assert_case'] = testcase['assert_case'];
        testcase_data['assert_field'] = testcase['assert_field'];
        testcase_data['assert_operator'] = testcase['assert_operator'];
        testcase_data['assert_value'] = testcase['assert_value'];

        testcase_data['param_header'] = testcase['param_header'];
        testcase_data['param_body'] = testcase['param_body'];
        testcase_data['param_query'] = testcase['param_query'];
        testcase_data['param_header_json'] = testcase['param_header_json'];
        testcase_data['param_body_json'] = testcase['param_body_json'];

        //-- testcase value재작성
        testcase_data['cpapireq_header'] = fn_api_paramtest_object.fn_assign_testcase_value(testcase_data['cpapireq_header'], testcase_data['param_header'], null);
        testcase_data['cpapireq_body'] = fn_api_paramtest_object.fn_assign_testcase_value(testcase_data['cpapireq_body'], testcase_data['param_body'], null);

        upd_cnt++;
      }
      if (upd_cnt > 0) {
        this.fn_filter_testcase_data(this.m_cur_tab_idx); //-- update ui array
      }
    },
    //-- testcase 목록삭제
    fn_del_testcase_list(a_testcase_id_list) {
      let del_cnt = 0;
      a_testcase_id_list.forEach((testcase_id) => {
        let find_idx = this.m_model_testcase_data.findIndex((sub_testcase) => {
          return (sub_testcase['testcase_id'] == testcase_id);
        });
        if (find_idx != -1) {
          this.m_model_testcase_data.splice(find_idx, 1);
          del_cnt++;
        }
      });
      if (del_cnt > 0) {
        this.fn_filter_testcase_data(this.m_cur_tab_idx); //-- update ui array
      }
    },
    //-- testcase검증(전체)
    fn_verify_testcase_list() {
      if (this.compute_testcase_verify_ing == true) {
        this.$adpt_alert('검증이 진행중 입니다.', 'API 검증', false);
        return;
      }

      let testcase_seq = ((new Date()).getTime()).toString();

      //-- validation before call
      let is_valid_testcase_lilst = this.m_model_testcase_data.every((testcase, idx) => {
        return this.fn_validation_verify_testcase(testcase_seq, testcase, idx);
      });
      if (is_valid_testcase_lilst == false) { return; }
      
      this.fn_set_total_verify_result(this.m_enum_VERIFY_ING);

      this.m_model_testcase_data.forEach((testcase) => {
        this.fn_verify_testcase(testcase_seq, testcase);
      });
    },
    //-- testcase검증정보 validation
    fn_validation_verify_testcase(testcase_seq, testcase, idx) {
      let testcase_id = testcase['testcase_id'];
      let testcase_nm = testcase['testcase_nm'];

      let s_testcase_msg_prefix = ((idx + 1) + '번째  Test Case');
      let s_testcase_msg_suffix = ('Test Case명: ' + testcase_nm);

      if (true == this.fn_is_param_gub_json_mode(testcase['param_gub'])) {
        if (this.$is_empty(testcase['param_header_json']) == false) {
          if (this.$input_invalid(!this.$is_json_str(testcase['param_header_json']), s_testcase_msg_prefix + ' header의 형식이 유효하지 않습니다.\n\n' + s_testcase_msg_suffix)) { return false; }
        }
        if (this.$is_empty(testcase['param_body_json']) == false) {
          if (this.$input_invalid(!this.$is_json_str(testcase['param_body_json']), s_testcase_msg_prefix + ' body의 형식이 유효하지 않습니다.\n\n' + s_testcase_msg_suffix)) { return false; }
        }
      }
      else {
        if (this.$is_json_obj(testcase['cpapireq_header']) == false) { alert_message(s_testcase_msg_prefix + ' header JSON stringify처리 오류.\n\n' + s_testcase_msg_suffix + '\n\n' + e, 'API 검증'); return false; }
        if (this.$is_json_obj(testcase['cpapireq_body']) == false) { alert_message(s_testcase_msg_prefix + ' body JSON stringify처리 오류.\n\n' + s_testcase_msg_suffix + '\n\n' + e, 'API 검증'); return false; }
      }

      let s_title = 'API 검증';
      if (this.$input_invalid(this.$is_empty(testcase_id), s_testcase_msg_prefix + ' ID 항목이 없습니다.\n\n' + s_testcase_msg_suffix, s_title)) { return false; }
      if (this.$input_invalid(this.$is_empty(testcase_seq), s_testcase_msg_prefix + ' 순번 항목이 없습니다.\n\n' + s_testcase_msg_suffix, s_title)) { return false; }
      if (this.$input_invalid(this.$is_empty(this.m_proc_seq), s_testcase_msg_prefix + ' 프로세스번호 항목이 없습니다.\n\n' + s_testcase_msg_suffix, s_title)) { return false; }
      if (this.$input_invalid(this.$is_empty(this.m_gw_profile), s_testcase_msg_prefix + ' G/W 프로파일 구분 항목이 없습니다.\n\n' + s_testcase_msg_suffix, s_title)) { return false; }
      if (this.$input_invalid(this.$is_empty(this.m_api_url), s_testcase_msg_prefix + ' API URL 항목이 없습니다.\n\n' + s_testcase_msg_suffix, s_title)) { return false; }
      return true;
    },
    //-- testcase검증(개별)
    fn_verify_testcase(testcase_seq, testcase) {
      let testcase_id = testcase['testcase_id'];

      //-- object -> string
      let param_header = this.$sf_json_stringify(testcase['param_header']);
      let param_body = this.$sf_json_stringify(testcase['param_body']);
      let param_query = this.$sf_json_stringify(testcase['param_query']);

      let cpapireq_header = {};
      let cpapireq_body = {};
      if (true == this.fn_is_param_gub_json_mode(testcase['param_gub'])) {
        if (this.$is_empty(testcase['param_header_json']) == false) {
          cpapireq_header = this.$sf_json_parse(testcase['param_header_json']);
        }
        if (this.$is_empty(testcase['param_body_json']) == false) {
          cpapireq_body = this.$sf_json_parse(testcase['param_body_json']);
        }
      }
      else {
        cpapireq_header = testcase['cpapireq_header'];
        cpapireq_body = testcase['cpapireq_body'];
      }

      //-- 추가header설정
      //--[tag:sr-20200525][i][필수항목이 없을경우에만 추가처리]
      //--###cpapireq_header = Object.assign(cpapireq_header, this.m_added_header);
      for (let key of Object.keys(this.m_added_header)) {
        if (false == cpapireq_header.hasOwnProperty(key)) {
          cpapireq_header[key] = this.m_added_header[key];
        }
      }

      let s_header = this.$sf_json_stringify(cpapireq_header);
      let s_body = this.$sf_json_stringify(cpapireq_body);

      this.fn_set_testcase_verify_result(testcase_id, this.m_enum_VERIFY_ING);

      let req_param = {
        'proc_seq': this.m_proc_seq, 'gw_profile': this.m_gw_profile, 'api_url': this.m_api_url, 'headers': s_header, 'body': s_body,
        'api_no': this.m_api_no, 'api_nm': this.m_api_nm, 'api_veri_baseurl': this.m_api_veri_baseurl,
        'testcase_id': testcase['testcase_id'], 'testcase_seq': testcase_seq, 'testcase_nm': testcase['testcase_nm'],
        'param_gub': testcase['param_gub'],
        'param_header': param_header, 'param_body': param_body, 'param_query': param_query,
        'param_header_json': testcase['param_header_json'], 'param_body_json': testcase['param_body_json'],
        'assert_case':  testcase['assert_case'], 'assert_field':  testcase['assert_field'], 'assert_operator':  testcase['assert_operator'], 'assert_value':  testcase['assert_value'],
      };
      this.fn_call_api_apigw_cpApiGet(req_param);
    },
    //-- testcase검증결과 처리
    fn_proc_verify_testcase_result(testcase_id, res_param) {
      let find_idx = this.fn_get_testcase_index(testcase_id);
      if (find_idx == -1) {
        this.$console_log('warn', 'fn_verify_testcase_result().', 'testcase not found', 'testcase_id: ', testcase_id, 'res_param: ', res_param);
        return;
      }
      /*--[ref]
        let res_param = {
          'verify_result': (('Y' == vo_successYn) ? this.m_enum_VERIFY_SUCC : this.m_enum_VERIFY_FAIL),
          'verifi_proc_succ': ((('y' == verifi_proc_succ) || ('n' == verifi_proc_succ)) ? verifi_proc_succ : ''),
          'verify_seq': this.$sf_int(vo_seq, 0),
          'verify_dur_msec': verify_dur_msec,
        };
        m_model_testcase_data: {
          'testcase_id':, 'testcase_nm':, 'amd_dt':, 'param_gub':, 'param_header': [], 'param_body': [], 'param_query': [],
          'param_header_json':, 'param_body_json':,
          'assert_case':, 'assert_field':, 'assert_operator':, 'assert_value':,
          'cpapireq_header': {}, 'cpapireq_body': {},
          'verify_result':, 'verifi_proc_succ':, 'verify_seq':, 'verify_dur_msec':
        };
      --*/

      //--:@verify result set
      let testcase_data = this.m_model_testcase_data[find_idx];
      testcase_data['verify_result'] = res_param['verify_result'];
      //--[tag:sr-20201001][i][DEPLOY1030 -> DEPLOY1040전환후 후처리용]
      testcase_data['verifi_proc_succ'] = res_param['verifi_proc_succ'];
      testcase_data['verify_seq'] = res_param['verify_seq'];
      testcase_data['verify_dur_msec'] = res_param['verify_dur_msec'];

      //-- 전체검사종료여부처리 {
      let succ_count = 0;
      let fail_count = 0;
      let etc_count = 0;
      let verifi_proc_succ_count = 0;
      let verifi_proc_fail_count = 0;
      this.m_model_testcase_data.forEach((testcase) => {
        let verify_result = testcase['verify_result'];
        let verifi_proc_succ = testcase['verifi_proc_succ'];
        if (this.m_enum_VERIFY_SUCC == verify_result) {
          succ_count++;
        }
        else if (this.m_enum_VERIFY_FAIL == verify_result) {
          fail_count++;
        }
        else {
          etc_count++;
        }
        if ('y' == verifi_proc_succ) {
          verifi_proc_succ_count++;
        }
        else if ('n' == verifi_proc_succ) {
          verifi_proc_fail_count++;
        }
      });
      if ((succ_count + fail_count) == this.m_model_testcase_data.length) {
        this.fn_set_total_verify_result((fail_count > 0) ? this.m_enum_VERIFY_FAIL : this.m_enum_VERIFY_SUCC);
        //--[tag:sr-20201001][add][parent의 emit function을 호출]
        window.setTimeout(() => {
          this.m_fn_emit('emit_total_verify_finished', { 'succ_count': succ_count, 'fail_count': fail_count, 'etc_count': etc_count, 'verifi_proc_succ_count': verifi_proc_succ_count, 'verifi_proc_fail_count': verifi_proc_fail_count, });
        }, 100);
      }
      //-- 전체검사종료여부처리 }
    },
    //-- get testcase index
    fn_get_testcase_index(testcase_id) {
      return this.m_model_testcase_data.findIndex((testcase) => { return (testcase['testcase_id'] == testcase_id); });
    },
    //-- 검증상태유효판단
    fn_is_valid_result(result) {
      let a_verify_result = [ this.m_enum_VERIFY_INIT, this.m_enum_VERIFY_ING, this.m_enum_VERIFY_SUCC, this.m_enum_VERIFY_FAIL, ];
      return (a_verify_result.indexOf(result) != -1);
    },
    //-- 전체검증상태설정
    fn_set_total_verify_result(result) {
      if (this.fn_is_valid_result(result) == false) { return; }
      this.m_total_vefify_result = result;
    },
    //-- testcase검증상태설정
    fn_set_testcase_verify_result(testcase_id, result) {
      if (this.fn_is_valid_result(result) == false) { return; }
      let find_idx = this.fn_get_testcase_index(testcase_id);
      if (find_idx == -1) { return; }
      let testcase_data = this.m_model_testcase_data[find_idx];
      testcase_data['verify_result'] = result;
    },
    //-- testcase param모드
    fn_is_param_gub_json_mode(param_gub) {
      return (param_gub == this.m_enum_PARAM_GUB_JSON);
    },
    //-- @function }
  } // methods:
};
</script>

<template>
  <section>

    <div class="date_setting setting_tab">
      <div class="setting_tab_cont">
        <div class="tab_cont"><a href="javascript:void(0)" :class="((m_cur_tab_idx == 0) ? 'active' : '')" @click.stop="onclick_btn_tab(0)">전체</a></div>
        <div class="tab_cont"><a href="javascript:void(0)" :class="((m_cur_tab_idx == 1) ? 'active' : '')" @click.stop="onclick_btn_tab(1)">정상 Case</a></div>
        <div class="tab_cont"><a href="javascript:void(0)" :class="((m_cur_tab_idx == 2) ? 'active' : '')" @click.stop="onclick_btn_tab(2)">예외 Case</a></div>
      </div>
    </div><!-- .date_setting setting_tab -->

    <div class="verify_all">
      <div class="verify_all_table_cont">
        <div class="verify_all_table_test">
          <div class="test_btn_cont">
            <div class="verify_test_btn">
              <p class="txt_deco"><span>1</span>Test Case</p>
              <p class="testCase_btn">
                <a class="btn btn3 btn_black" href="javascript:void(0)" @click.stop="onclick_btn_testcase_edit()"><span>1</span>추가</a>
                <a class="btn btn3 btn_white" href="javascript:void(0)" @click.stop="onclick_btn_testcase_list()"><span>2</span>불러오기</a>
              </p>
            </div><!-- .verify_test_btn -->
            <div class="pkg_board verify_table">
              <table class="table-list">
                <caption>API 관리 list</caption>
                <colgroup>
                  <col style="width:40%">
                  <col style="width:35%">
                  <col style="width:auto">
                  <col style="width:auto">
                </colgroup>
                <thead>
                  <tr>
                    <th scope="row">Test Case Name</th>
                    <th scope="row">Case 유형</th>
                    <th scope="row">수정</th>
                    <th scope="row">
                      <a class="delete" href="javascript:void(0)" @click.stop="onclick_btn_testcase_delete_all()"><span>2</span></a>
                    </th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(testcase_item, index) in m_model_testcase_data_filtered" :key="index">
                    <td>{{computed_testcase_info(testcase_item, 'testcase_nm')}}</td>
                    <td>{{computed_testcase_info(testcase_item, 'assert_case')}}</td>
                    <td>
                      <span v-show="computed_testcase_info(testcase_item, 'btn_testcase_update')">
                        <a class="correction" href="javascript:void(0)" @click.stop="onclick_btn_testcase_update(testcase_item)"><span>1</span></a>
                      </span
                    </td>
                    <td><a class="delete" href="javascript:void(0)" @click.stop="onclick_btn_testcase_delete(testcase_item)"><span>2</span></a></td>
                  </tr>
                  <tr v-for="(n, index) in Math.max((Math.max(5, m_model_testcase_data.length) - m_model_testcase_data_filtered.length), 0)" :key="('dum-' + index)">
                    <td></td>
                    <td></td>
                    <td></td>
                    <td></td>
                  </tr>
                </tbody>
              </table>
            </div><!-- .pkg_board verify_table -->
          </div><!-- .test_btn_cont -->
        </div><!-- .verify_all_table_test -->
        <!-- verify_all_table_img의  이미지 수정 2019.06.03 -->
        <div class="verify_all_table_img"><span>1</span></div>
        <div class="verify_all_table_result">
          <div class="test_btn_cont">
            <div class="verify_test_btn">
              <p class="txt_deco"><span>1</span>Result</p>
              <p><!--<a class="btn btn3 btn_white line_red" href="#">엑셀 저장</a>--></p>
            </div>
            <div class="pkg_board verify_table">
              <table class="table-list">
                <caption>API 관리 list</caption>
                <colgroup>
                  <col style="width:35%">
                  <col style="width:25%">
                  <col style="width:auto">
                  <col style="width:25%">
                </colgroup>
                <thead>
                  <tr>
                    <th scope="row">Test Case Name</th>
                    <!--@@<th scope="row">Case 유형</th>-->
                    <th scope="row">소요시간(msec)</th>
                    <th scope="row">결과</th>
                    <th scope="row">비고</th>
                  </tr>
                </thead>
                <tbody>
                  <tr v-for="(testcase_item, index) in m_model_testcase_data_filtered" :key="index">
                    <td>{{computed_testcase_info(testcase_item, 'testcase_nm')}}</td>
                    <!--<td>{{computed_testcase_info(testcase_item, 'assert_case')}}</td>-->
                    <td>{{computed_testcase_info(testcase_item, 'verify_dur_msec')}}</td>
                    <td :class="['', computed_testcase_info(testcase_item, 'css_result_text')]">
                      <div :class="computed_testcase_info(testcase_item, 'css_verify_ing')"></div>
                      {{computed_testcase_info(testcase_item, 'verify_result')}}
                    </td>
                    <td class="detail_plus">
                      <span v-show="computed_testcase_info(testcase_item, 'btn_testcase_result_detail')">
                        <a class="btn2 btn_gray" href="javascript:void(0)" @click.stop="onclick_btn_testcase_result_detail(testcase_item)"><span>1</span>상세보기</a>
                      </span>
                    </td>
                  </tr>
                  <tr v-for="(n, index) in Math.max((Math.max(5, m_model_testcase_data.length) - m_model_testcase_data_filtered.length), 0)" :key="('dum-' + index)">
                    <td></td>
                    <td></td>
                    <td></td>
                    <td></td>
                  </tr>
                </tbody>
              </table>
            </div><!-- .pkg_board verify_table -->
          </div><!-- .test_btn_cont -->
        </div><!-- .verify_all_table_result -->
        <div class="btn_verification">
          <a class="btn btn3 btn_blueGreen" href="javascript:void(0)" @click.stop="onclick_btn_verify()"><span>1</span>검증</a>
        </div>
      </div><!-- .verify_all_table_cont -->

      <div class="verify_all_result">
        <div class="result_cont">
          <div class="result">
            <p>검증결과</p>
            <p class="result blue_txt" v-show="(m_total_vefify_result == m_enum_VERIFY_SUCC)">성공<span class="ico_success">1</span></p>
            <p class="result red_txt" v-show="(m_total_vefify_result == m_enum_VERIFY_FAIL)">실패<span class="ico_fail">1</span></p>
            <p class="result" v-show="((m_total_vefify_result != m_enum_VERIFY_SUCC) && (m_total_vefify_result != m_enum_VERIFY_FAIL))"><span>-</span></p>
          </div>
          <span class="icon1">icon</span>
          <div class="sum">
            <p class="blue_txt">성공</p>
            <p class="blue_txt">{{computed_testcase_verify_info('verify_succ_count')}}</p>
          </div>
          <span class="icon2">icon</span>
          <div class="sum ">
            <p class="red_txt">실패</p>
            <p class="red_txt">{{computed_testcase_verify_info('verify_fail_count')}}</p>
          </div>
        </div>
      </div><!-- .verify_all_result -->

      <div class="verify_all_completion" v-show="((m_total_vefify_result == m_enum_VERIFY_SUCC) || (m_total_vefify_result == m_enum_VERIFY_FAIL))">
              검증이 완료되었습니다.
      </div><!-- .verify_all_completion -->
    </div><!-- .verify_all -->

    <!-- popup-[testcase] -->
    <sc-vuemodal
      ref="modalTestcaseDialog"
      name="modalTestcaseDialog"
      width="880"
      height="780"
      :clickToClose="false"
      :draggable="false"
      :resizable="false"
      :scrollable="false"
      :adaptive="false"
      :minWidth="440"
      :minHeight="390"
    >
      <testcase-dialog
        ref="testcaseDialog"
        prop_name="modalTestcaseDialog"
        :prop_Data="m_prop_testcaseData"
        @emit_proc_finished="emit_proc_finished_testcase"
      >
      </testcase-dialog>
    </sc-vuemodal>

    <!-- popup-[testcase불러오기] -->
    <sc-vuemodal
      ref="modalTestcaseListDialog"
      name="modalTestcaseListDialog"
      width="720"
      height="540"
      :clickToClose="false"
      :draggable="false"
      :resizable="false"
      :scrollable="false"
      :adaptive="false"
      :minWidth="360"
      :minHeight="270"
    >
      <testcase-list-dialog
        ref="testcaseListDialog"
        prop_name="modalTestcaseListDialog"
        :prop_Data="m_prop_testcaseListData"
        @emit_proc_deleted="emit_proc_deleted_testcaselist"
        @emit_proc_finished="emit_proc_finished_testcaselist"
      >
      </testcase-list-dialog>
    </sc-vuemodal>

    <!-- popup-[verify상세보기] -->
    <sc-vuemodal
      ref="modalVerifyViewDialog"
      name="modalVerifyViewDialog"
      width="886"
      height="800"
      :clickToClose="false"
      :draggable="false"
      :resizable="false"
      :scrollable="false"
      :adaptive="false"
      :minWidth="443"
      :minHeight="400"
    >
      <verify-view-dialog
        ref="verifyViewDialog"
        prop_name="modalVerifyViewDialog"
        :prop_Data="m_prop_verifyViewData"
      >
      </verify-view-dialog>
    </sc-vuemodal>

    <modal-dialog/>
  </section>
</template>

<style scoped>
  .loadersmall {
    border: 3px solid #f3f3f3;
    -webkit-animation: spin 1s linear infinite;  /* Safari */
    animation: spin 1s linear infinite;
    border-top: 3px solid #555;
    border-radius: 50%;
    height: 10px;
    width: 10px;
    margin: auto;
  }
  /* Safari */
  @-webkit-keyframes spin {
    0% { -webkit-transform: rotate(0deg); }
    100% { -webkit-transform: rotate(360deg); }
  }
  
  @keyframes spin {
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
  }
</style>
