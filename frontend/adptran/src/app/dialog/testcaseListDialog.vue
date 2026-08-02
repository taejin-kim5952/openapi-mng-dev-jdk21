<comment>
  @testcase list
    popup-[testcase불러오기]
</comment>
<script>
import { CommonMixin } from '@/common/adptran.js';
import { fn_api_paramtest_object } from '@/common/fn_api_paramtest_object.js';

export default {
  name: 'testcaseListDialog',
  props: {
    prop_name: {
      type: String,
      required: true,
    },
    prop_Data: {
      type: Object,
      required: true,
      default: (() => ({ 'api_no': '', 'filtered_testcase_id_list': [] })),
      validator: function(value) {
        if ((parseInt(value['api_no'], 10) > 0) == false) { return false; }
        if (Array.isArray(value['filtered_testcase_id_list']) == false) { return false; }
        return true;
      }
    },
  },
  mixins: [CommonMixin],
  data() {
    return {
      m_vue_id: 'testcaseListDialog',

      //-- @biz-data {
      m_api_no: this.$sf_str(this.prop_Data['api_no']),
      //-- for make cpapireq data {
      //--{'n','f','d','v'} 형식으로 변환저장
      m_udf_param_header: [],  //-- def param test header
      m_udf_param_body: [],  //-- def param test body
      m_udf_param_query: [],   //-- def param test query
      m_cpapireq_header: {}, //-- cpapireq param header
      m_cpapireq_body: {}, //-- cpapireq param body
      m_cpapireq_query: {},  //-- cpapireq param query
      m_is_apiParamTest_loaded: false,  //-- fn_call_api_apiParamTest() queried
      //-- for make cpapireq data }
      
      m_filtered_testcase_id_list: this.prop_Data['filtered_testcase_id_list'],  //-- 목록제외 testcase_id prop
      //-- @biz-data }

      //-- @ui-data {
      m_model_testcase_data: [], //-- testcase data
      m_model_testcase_checked: [], //-- checkbox selected testcase_id
      m_model_testcase_check_all: false,

      m_model_json_string: '',  //-- for modalJsonViewDialog 
      //-- @ui-data }
    } // return{}
  }, // data()
  computed: {
    ui_ctrl_btn_ok_disabled() {
      return (this.m_is_apiParamTest_loaded == false);
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
        else if ('amd_dt' == cmd) {
          return (('default' == testcase_item['testcase_id']) ? '-' : this.$fmt_data(testcase_item['amd_dt'], 'fmt_date_01'));
        }
        else if ('param_gub' == cmd) {
          return ((testcase_item['param_gub'] == 'json') ? 'JSON' : 'form');
        }
        else if ('infoview_yn' == cmd) {
          return ((testcase_item['infoview_yn'] == 'Y') ? '노출' : '비노출');
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
    this.fn_query_testcaselist();
    if (this.m_api_no.length > 0) {
      this.fn_call_api_apiParamTest(this.m_api_no); //-- param test 검색
    }
  },
  updated: function () {
    this.$console_log('trace', 'updated()');
  },
  methods: {
    //-- @handler {
    onclick_chk_all(evt) {
      let is_no_checked = (this.m_model_testcase_checked.length == 0);
      this.m_model_testcase_checked = [];
      if (is_no_checked == true) {
        this.m_model_testcase_data.forEach((testcase) => {
          this.m_model_testcase_checked.push(testcase['testcase_id']);
        });
      }
      let is_ctrl_checked = (this.m_model_testcase_checked.length > 0)
      if (is_ctrl_checked == this.m_model_testcase_check_all) { //-- 이전과 상태가 같으면
        evt.preventDefault();
      }
    },
    onclick_btn_ok() {
      if (this.m_model_testcase_checked.length == 0) { 
        this.$adpt_alert('선택된 Test Case가 없습니다.', 'Test Case 불러오기', false);
        return;
      }
      this.fn_proc_selected_testcase();
    },
    onclick_btn_delete() {
      //-- 'default' filter
      this.m_model_testcase_checked = this.m_model_testcase_checked.filter(function(elem) {
        return ('default' != elem);
      });
      if (this.m_model_testcase_checked.length == 0) { 
        this.$adpt_alert('선택된 Test Case가 없습니다.', 'Test Case 삭제', false);
        return; 
      }
      
      let s_msg = '선택하신 Test Case ' + this.m_model_testcase_checked.length + '건을 삭제 하시겠습니까?';
      this.$adpt_confirm(s_msg, 'Test Case 삭제').then((response) => {
        this.fn_del_testcase(this.m_model_testcase_checked);
      });
    },
    onclick_btn_testcase_view(testcase_id) {
      //-- find testcaseData
      let testcaseData = this.m_model_testcase_data.find(testcase => (testcase['testcase_id'] == testcase_id));
      if (undefined == testcaseData) { 
        this.$adpt_alert('선택된 Test Case가 없습니다.', 'Test Case 보기', false);
        return; 
      }

      let o_req_header = {};
      let o_req_body = {};
      if ('default' == testcase_id) {
        o_req_header = this.m_cpapireq_header;
        o_req_body = this.m_cpapireq_body;
      }
      else {
        if (this.m_enum_PARAM_GUB_JSON == testcaseData['param_gub']) {
          o_req_header = this.$sf_json_parse(testcaseData['param_header_json']);
          o_req_body = this.$sf_json_parse(testcaseData['param_body_json']);
        }
        else {
          o_req_header = fn_api_paramtest_object.fn_assign_testcase_value(this.m_cpapireq_header, testcaseData['param_header']);
          o_req_body = fn_api_paramtest_object.fn_assign_testcase_value(this.m_cpapireq_body, testcaseData['param_body']);
        }
      }

      let json_header = this.$sf_json_stringify(o_req_header, null, 2);
      let json_body = this.$sf_json_stringify(o_req_body, null, 2);
      this.m_model_json_string = '[요청  Header]\n' + json_header + '\n\n' + '[요청 Body]\n' + json_body;

      this.$modal.show('modalJsonViewDialog');
    },
    //-- @handler }

    //-- @api function {
    //-- api testcaselist정보 query
    fn_call_api_apiTestCaseList(api_no) {
      this.$fn_call_api_common((this.m_con_apiTestCaseList_url + '/' + api_no), {}, 'get', this.fn_callback_api_apiTestCaseList);
      //--@@this.$fn_call_api_common(this.m_con_apiTestCaseList_url, { 'api_no': api_no }, 'post', this.fn_callback_api_apiTestCaseList);
    },
    //-- api testcaselist정보 query 후처리
    fn_callback_api_apiTestCaseList(call_ret, response, api_request, recordset) {
      this.$console_log('trace', 'fn_callback_api_apiTestCaseList()', 'call_ret: ', call_ret, 'response :', response, 'api_request: ', api_request, 'recordset: ', recordset);

      //-- call_ret: 'ok', 'nk', 'failed', 'catched'
      if ('ok' == call_ret) {
        //-- adpt.select_API_TESTCASE
        //-- {TESTCASE_ID, API_NO, TESTCASE_NM, TESTCASE_DESC, PARAM_GUB, PARAM_HEADER, PARAM_BODY, PARAM_QUERY, PARAM_HEADER_JSON, PARAM_BODY_JSON, ASSERT_CASE, ASSERT_FIELD, ASSERT_OPERATOR, ASSERT_VALUE, INFOVIEW_YN, REG_DT, AMD_DT, def_API_NO, def_API_NM, def_API_HANDLER_CD_NM}

        this.m_model_testcase_data = []; //-- clear data
        this.m_model_testcase_checked = []; //-- clear checked

        //-- 기본 Test Data
        let testcase_id = 'default';
        if (this.m_filtered_testcase_id_list.indexOf(testcase_id) == -1) {
          let udf_testcaseData = {
            'testcase_id': testcase_id, 'testcase_nm': '기본 Test Case', 'amd_dt': '-', 'param_gub': '', 
            'param_header': [], 'param_body': [], 'param_query': [],
            'param_header_json': '', 'param_body_json': '',
            'assert_case': '', 'assert_field': '', 'assert_operator': '', 'assert_value': '',
            'cpapireq_header': {}, 'cpapireq_body': {},
          };
          this.m_model_testcase_data.push(udf_testcaseData);
        }

        recordset.forEach((rs_testcase) => {
          let testcase_id = this.$sf_str(rs_testcase['TESTCASE_ID']);
          if (this.m_filtered_testcase_id_list.indexOf(testcase_id) == -1) {
            let a_param_header = [], a_param_body = [], a_param_query = [];
            a_param_header = this.$sf_json_parse(this.$sf_str(rs_testcase['PARAM_HEADER']));
            a_param_body = this.$sf_json_parse(this.$sf_str(rs_testcase['PARAM_BODY']));
            a_param_query = this.$sf_json_parse(this.$sf_str(rs_testcase['PARAM_QUERY']));
  
            let o_testcaseData = {
              'testcase_id': testcase_id,
              'testcase_nm': this.$sf_str(rs_testcase['TESTCASE_NM']),
              'amd_dt': this.$sf_str(rs_testcase['AMD_DT']),
              'param_gub': this.$sf_str(rs_testcase['PARAM_GUB']),
              'param_header': a_param_header, 'param_body': a_param_body, 'param_query': a_param_query,
              'param_header_json': this.$sf_str(rs_testcase['PARAM_HEADER_JSON']),
              'param_body_json': this.$sf_str(rs_testcase['PARAM_BODY_JSON']),
              'assert_case': this.$sf_str(rs_testcase['ASSERT_CASE']),
              'assert_field': this.$sf_str(rs_testcase['ASSERT_FIELD']),
              'assert_operator': this.$sf_str(rs_testcase['ASSERT_OPERATOR']),
              'assert_value': this.$sf_str(rs_testcase['ASSERT_VALUE']),
              'infoview_yn': this.$sf_str(rs_testcase['INFOVIEW_YN']),
            }
            this.m_model_testcase_data.push(o_testcaseData);
          }
        });
      }
      else {
        this.$proc_api_resultCode_Fail(call_ret, response);
      }
    },

    //-- api param test정보 query
    fn_call_api_apiParamTest(api_no) {
      this.$console_log('trace', 'fn_call_api_apiParamTest()', 'api_no: ', api_no);

      this.m_udf_param_header = [],  //-- def param test header
      this.m_udf_param_body = [],  //-- def param test body
      this.m_udf_param_query = [],   //-- def param test query
      this.m_cpapireq_header = {}, //-- cpapireq param header
      this.m_cpapireq_body = {}, //-- cpapireq param body
      this.m_cpapireq_query = {};  //-- cpapireq param query
      this.m_is_apiParamTest_loaded = false;

      this.$fn_call_api_common((this.m_con_apiParamTest_url + '/' + api_no + '?param_type_cd_list=PRMTYP1010'), {}, 'get', this.fn_callback_api_apiParamTest);
      //--@@this.$fn_call_api_common(this.m_con_apiParamTest_url, { 'api_no': api_no, 'param_type_cd_list': 'PRMTYP1010', }, 'post', this.fn_callback_api_apiParamTest);
    },
    //-- api param test정보 query 후처리
    fn_callback_api_apiParamTest(call_ret, response, api_request, recordset) {
      this.$console_log('trace', 'fn_callback_api_apiParamTest()', 'call_ret: ', call_ret, 'response :', response, 'api_request: ', api_request, 'recordset: ', recordset);

      //-- call_ret: 'ok', 'nk', 'failed', 'catched'
      if ('ok' == call_ret) {
        //-- adpt.select_API_PARAM_TEST_list
        //-- {LEVEL, PARAM_NM_FULL, PARAM_NO, API_NO, PARAM_TYPE_CD, SORT_ODRG, PARAM_NM, DATA_TYPE_CD_NM, EXAM, PRNTS_PARAM_NO, PARAM_LOC, REQUIRED, DO_NOT_SEND, FIXED_VALUE, HIDDEN}

        this.m_udf_param_header = [],  //-- def param test header
        this.m_udf_param_body = [],  //-- def param test body
        this.m_udf_param_query = [],   //-- def param test query
        this.m_cpapireq_header = {}, //-- cpapireq param header
        this.m_cpapireq_body = {}, //-- cpapireq param body
        this.m_cpapireq_query = {};  //-- cpapireq param query

        //-- [i]logic공유//testcaseDialog.vue/testcaseListDialog.vue
        //-- [tag:object_in_array]
        //-- [drm][chg][forEach to for loop]
        //--@@recordset.forEach((rs_param_test) => {});

        for (let n_ii = 0; n_ii < recordset.length; n_ii++) {
          let rs_param_test = recordset[n_ii];

          let paramloc = this.$sf_str(rs_param_test['PARAM_LOC']);
          let datatype = this.$sf_str(rs_param_test['DATA_TYPE_CD_NM']).toLowerCase();

          //-- [tag:object_in_array]
          //-- object_in_array except처리 {
          let is_array_of_object = false;
          if ('array' == datatype) {
            let n_jj = n_ii + 1;
            if (n_jj < recordset.length) {
              let rs_param_test_next = recordset[n_jj];
  
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
              }
            }
          }
          //-- object_in_array except처리 } 

          let do_push = true;
          do_push = do_push && ((('header' == paramloc) && ('array' == datatype)) == false);  //--[i] header의 array를 처리하지 못함

          if (do_push == true) {
            let udf_param = fn_api_paramtest_object.fn_get_udf_param_object(rs_param_test);
            if ('header' == paramloc) { this.m_udf_param_header.push(udf_param); }
            else if ('body' == paramloc) { this.m_udf_param_body.push(udf_param); }
            else if ('query' == paramloc) { this.m_udf_param_query.push(udf_param); }
          }
        }

        let is_set_testdata = true; //-- api testdata를 설정
        this.m_cpapireq_header = fn_api_paramtest_object.fn_get_api_param_object(this.m_udf_param_header, is_set_testdata);
        this.m_cpapireq_body = fn_api_paramtest_object.fn_get_api_param_object(this.m_udf_param_body, is_set_testdata);
        this.m_cpapireq_query = fn_api_paramtest_object.fn_get_api_param_object(this.m_udf_param_query, is_set_testdata);
        
        this.m_is_apiParamTest_loaded = true;
      }
      else {
        this.$proc_api_resultCode_Fail(call_ret, response);
      }
    },

    //-- api testcase정보 delete
    fn_call_api_apiTestCaseTrans_del(req_param) {
      this.$console_log('trace', 'fn_call_api_apiTestCaseTrans_del()', 'req_param: ', req_param);

      this.$fn_call_api_common(this.m_con_apiTestCaseTrans_del_url, req_param, 'post', this.fn_callback_api_apiTestCaseTrans_del);
    },
    //-- api testcase정보 delete 후처리
    fn_callback_api_apiTestCaseTrans_del(call_ret, response, api_request, result_data) {
      this.$console_log('trace', 'fn_callback_api_apiTestCaseTrans_del()', 'call_ret: ', call_ret, 'response :', response, 'api_request: ', api_request, 'result_data: ', result_data);
      
      //-- call_ret: 'ok', 'nk', 'failed', 'catched'
      let s_msg = '';
      let is_deleted = false;
      let tran_gub = 'Test Case 삭제처리';
      if ('ok' == call_ret) {
        if (result_data['resultCd'] == 200) { //-- data: {resultCd: 200, resultMsg: 'xxx'}
          s_msg = 'Test Case를 삭제 하였습니다.';
          is_deleted = true;
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
        if (is_deleted) { 
          this.fn_query_testcaselist();
          //-- emit with deleted data {
          let s_testcase_id_list = this.$sf_obj_val(api_request, 'testcase_id_list', []);
          let ret_data = { 'testcase_id_list': s_testcase_id_list.split(',') };
          this.$emit('emit_proc_deleted', ret_data);
          this.$console_log('o-o', 'fn_callback_api_apiTestCaseTrans_del().', '$emit(emit_proc_deleted)', 'ret_data: ', ret_data);
          //-- emit with deleted data }
        }
      })
      , ((response) => {
        if (is_requery) { this.fn_query_testcaselist();  }
      }));
    },
    //-- @api function }

    //-- @function {
    //-- testcase list검색
    fn_query_testcaselist() {
      this.$console_log('trace', 'fn_query_testcaselist()');
      if (this.m_api_no.length > 0) {
        this.m_model_testcase_data = []; //-- clear data
        this.m_model_testcase_checked = []; //-- clear checked
        this.fn_call_api_apiTestCaseList(this.m_api_no); //-- testcaselist 검색
      }
    },
    //-- 선택 testcase 삭제처리
    fn_del_testcase(a_testcase_id) {
      this.$console_log('trace', 'fn_del_testcase()', 'a_testcase_id: ', a_testcase_id);
      let req_param = { 'testcase_id_list': a_testcase_id.join(',') };
      this.fn_call_api_apiTestCaseTrans_del(req_param);
    },
    //-- 선택 testcase 전달처리
    fn_proc_selected_testcase() {
      //-- 선택된 testcase_id정보를 filter
      let a_selected_testcaselist = this.m_model_testcase_data.filter((elem) => {
        return (this.m_model_testcase_checked.indexOf(elem['testcase_id']) != -1);
      });

      /*--[ref]
      let udf_testcaseData = {
        'testcase_id': testcase_id, 'testcase_nm': '기본 Test Case', 'amd_dt': '-', 'param_gub': '', 
        'param_header': [], 'param_body': [], 'param_query': [],
        'param_header_json': '', 'param_body_json': '',
        'assert_case': '', 'assert_field': '', 'assert_operator': '', 'assert_value': '',
        'cpapireq_header': {}, 'cpapireq_body': {},
      };
      --*/
      //-- cpapireq parameter + testcase
      //--@@a_selected_testcaselist = a_selected_testcaselist.slice();  //-- array copy
      a_selected_testcaselist.forEach((testcase) => {
        if ('default' == testcase['testcase_id']) {
          testcase['cpapireq_header'] = this.m_cpapireq_header;
          testcase['cpapireq_body'] = this.m_cpapireq_body;
          testcase['param_header'] = this.m_udf_param_header;
          testcase['param_body'] = this.m_udf_param_body;
          testcase['param_query'] = this.m_udf_param_query;
        }
        else {
          testcase['cpapireq_header'] = fn_api_paramtest_object.fn_assign_testcase_value(this.m_cpapireq_header, testcase['param_header'], this.m_udf_param_header);
          testcase['cpapireq_body'] = fn_api_paramtest_object.fn_assign_testcase_value(this.m_cpapireq_body, testcase['param_body'], this.m_udf_param_body);
          testcase['param_header'] = fn_api_paramtest_object.fn_get_udf_param_assign_value(this.m_udf_param_header, testcase['param_header']);
          testcase['param_body'] = fn_api_paramtest_object.fn_get_udf_param_assign_value(this.m_udf_param_body, testcase['param_body']);
          testcase['param_query'] = fn_api_paramtest_object.fn_get_udf_param_assign_value(this.m_udf_param_query, testcase['param_query']);
        }
      });

      //-- emit with 선택testcase data {
      let ret_data = { 'testcaselist': a_selected_testcaselist };
      this.$emit('emit_proc_finished', ret_data);
      this.$console_log('o-o', 'fn_proc_selected_testcase().', '$emit(emit_proc_finished)', 'ret_data: ', ret_data);
      this.fn_dialog_close();
      //-- emit with 선택testcase data }
    },
    fn_dialog_close() {
      this.$modal.hide(this.prop_name);
    },
    fn_JsonView_dialog_close() {
      this.$modal.hide('modalJsonViewDialog');
    },
    //-- @function }
  } // methods:
};
</script>

<template>
  <div class="popup_wrap">
    <div class="popup_header">
      <span class="popup_header_title">Test Case 불러오기</span>
      <button type="button" class="popup_header_button" role="button" @click.stop="fn_dialog_close()" title="Close">
        <span class="button_close">Close</span>
      </button>
    </div>
    <div class="pop_ver pop_ver2">
      <div class="popup_content distribution_cont">
        <div class="scroll_wrap">

          <div class="pkg_board">
            <table class="table-vw table-vw6">
              <caption>Test Case 불러오기 Table</caption>
              <colgroup>
                <col style="width:58px;">
                <col style="width:auto;">
                <col style="width:90px;">
                <col style="width:90px;">
                <col style="width:100px;">
                <col style="width:90px;">
                <col style="width:58px;">
              </colgroup>
              <thead>
                <tr>
                  <th><p class="popup_check"><input type="checkbox" id="id_chk_all" :value="true" v-model="m_model_testcase_check_all" @click.stop="onclick_chk_all($event)"><label for="id_chk_all"><span></span></label></p></th>
                  <th>Test Case 명</th>
                  <th>Case유형</th>
                  <th>등록구분</th>
                  <th>이용가이드</th>
                  <th>등록일</th>
                  <th>보기</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(testcase_item, index) in m_model_testcase_data" :key="index">
                  <td><p class="popup_check"><input type="checkbox" :id="('id_chk_' + index)" :value="testcase_item['testcase_id']" v-model="m_model_testcase_checked"><label :for="('id_chk_' + index)"><span></span></label></p></td>
                  <td>{{computed_testcase_info(testcase_item, 'testcase_nm')}}</td>
                  <td>{{computed_testcase_info(testcase_item, 'assert_case')}}</td>
                  <td>{{computed_testcase_info(testcase_item, 'param_gub')}}</td>
                  <td>{{computed_testcase_info(testcase_item, 'infoview_yn')}}</td>
                  <td>{{computed_testcase_info(testcase_item, 'amd_dt')}}</td>
                  <td>
                    <button type="button" :class="['btn_view', (ui_ctrl_btn_ok_disabled ? 'disabled' : '')]" :disabled="ui_ctrl_btn_ok_disabled" @click.stop="onclick_btn_testcase_view(testcase_item['testcase_id'])"><span>{{testcase_item['testcase_id']}}</span></button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div><!-- .pkg_board -->

        </div><!-- .scroll_wrap -->
        
        <div class="brd_tp process_btn">
          <button type="button" :class="['btn', 'btn_black', 'btn_sml', (ui_ctrl_btn_ok_disabled ? 'disabled' : '')]" :disabled="ui_ctrl_btn_ok_disabled" @click.stop="onclick_btn_ok()" title="확인">확인</button>
          <button type="button" class="btn btn_gray btn_sml" @click.stop="onclick_btn_delete()" title="삭제">삭제</button>
          <button type="button" class="btn btn_sml" @click.stop="fn_dialog_close()" title="닫기">닫기</button>
        </div>
        
      </div><!-- .popup_content -->
    </div><!--.pop_ver pop_ver2 -->
    
    <!-- popup-[json보기] -->
    <sc-vuemodal
      ref="modalJsonViewDialog"
      name="modalJsonViewDialog"
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
        <span class="popup_header_title">JSON 보기</span>
        <button type="button" class="popup_header_button" role="button" @click.stop="fn_JsonView_dialog_close()" title="Close">
          <span class="button_close">Close</span>
        </button>
      </div><!-- .popup_header -->
      <div class="pop_ver pop_ver2">
        <div class="popup_content distribution_cont">
          <div class="pkg_board">
            <textarea spellcheck="false" class="scroll_box_json" v-model="m_model_json_string" readonly></textarea>
          </div>
          <div class="brd_tp process_btn">
            <button type="button" title="닫기" class="btn btn_sml" @click.stop="fn_JsonView_dialog_close()">닫기</button>
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

  .distribution_cont .scroll_box_json {height: 440px; width:100%; overflow: auto; background-color:#404040; color: #fff766;}
  
  .btn.disabled{color:rgba(255,255,255,0.5) !important;}
  .btn_view.disabled{background: #ccc;}
</style>
