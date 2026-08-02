<comment>
  @adptran service for jsp embeded
</comment>
<script>
import { CommonMixin } from '@/common/adptran.js';
//-- popup-[배포하기]
import deployDialog from '@approot/dialog/deployDialog.vue';
//-- popup-[testcase]
import testcaseDialog from '@approot/dialog/testcaseDialog.vue';
//-- popup-[testcase불러오기]
import testcaseListDialog from '@approot/dialog/testcaseListDialog.vue';
//-- popup-[verify상세보기]
import verifyViewDialog from '@approot/dialog/verifyViewDialog.vue';
//-- popup-[API검색]
import apiSearchDialog from '@approot/dialog/apiSearchDialog.vue';
//-- popup-[xlsx등록]
import importXlsxDialog from '@approot/dialog/importXlsxDialog.vue';

export default {
  name: 'adptranService',
  components: {
    'deploy-dialog': deployDialog,
    'testcase-dialog': testcaseDialog,
    'testcase-list-dialog': testcaseListDialog,
    'verify-view-dialog': verifyViewDialog,
    'api-search-dialog': apiSearchDialog,
    'import-xlsx-dialog': importXlsxDialog,
  },
  mixins: [CommonMixin],
  props: { },
  data() {
    return {
      m_vue_id: 'adptranService',

      //-- @property {
      m_prop_deployData: [],
      m_prop_testcaseData: {},
      m_prop_testcaseListData: {},
      m_prop_verifyViewData: {},
      m_prop_apiDataList: [],
      m_prop_importXlsxData: {},
      //-- @property }

      m_fn_cb_proc_deploy: null,  //-- proc_deploy callback함수
      m_fn_cb_proc_deploy_delete: null,  //-- proc_deploy_delete callback함수
      m_fn_cb_proc_testcase: null,  //-- proc_testcase callback함수
      m_fn_cb_proc_testcaselist: null,  //-- proc_testcaselist callback함수
      m_fn_cb_proc_proc_verify_view: null,  //-- proc_verify_view callback함수
      m_fn_cb_proc_apiSearch: null,  //-- proc_apiSearch callback함수
      m_fn_cb_proc_importXlsx: null,  //-- proc_importXlsx callback함수
    } // return{}
  }, // data ()
  computed: {
  },
  created: function() {
    this.$console_log('trace', 'created()');
  },
  mounted: function() {
    this.$console_log('trace', 'mounted()');
  },
  updated: function() {
    this.$console_log('trace', 'updated()');
  },
  methods: {
    //-- for deploy {
    proc_deploy(a_api_list, fn_cb_deploy) {
      this.m_prop_deployData = a_api_list.slice();  //-- array copy
      this.m_fn_cb_proc_deploy = fn_cb_deploy; //-- callback function
      this.$modal.show('modalDeployDialog');
    },
    //-- ret_data: { 'summary_count': o_summary_count }
    emit_proc_finished_deploy(ret_data) {  //-- emit when dialog job finished
      this.$console_log('trace', 'emit_proc_finished_deploy.', 'ret_data: ', ret_data);
      this.$sf_func_call(this.m_fn_cb_proc_deploy, { 'return': true, 'data': ret_data });
    },

    proc_deploy_delete(gw_profile, api_no, fn_cb_deploy_delete) {
      this.m_fn_cb_proc_deploy_delete = fn_cb_deploy_delete; //-- callback function
      let gw_profile_title = '';
      if (this.$is_empty(gw_profile) == true) { this.$adpt_alert('GW Profile이 지정되지 않았습니다.', 'API 배포삭제', false); return false; }
      if ('PROD' == gw_profile) {
        gw_profile_title = '상용'
      }
      else if ('TB' == gw_profile) {
        gw_profile_title = 'TB'
      }
      else {
        this.$adpt_alert('정의되지 않은 GW Profile지정 입니다. - [gw_profile: ' + gw_profile + ']', 'API 배포삭제', false); return false;
      }
      if (this.$is_empty(api_no) == true) { this.$adpt_alert('API NO가 지정되지 않았습니다.', 'API 배포삭제', false); return false; }
      
      var s_msg = gw_profile_title + '배포를 삭제 하시겠습니까?\n\n[API NO: ' + api_no + ']'
      this.$adpt_confirm(s_msg, 'API 배포삭제').then((response) => {
        let api_param = { 'gw_profile': gw_profile, 'api_no': api_no };
        this.$fn_call_api_common(this.m_con_apigw_deployDelete_url, api_param, 'post', this.fn_callback_api_apigw_deployDelete);
      });
    },
    //-- apigw_deployDelete 후처리
    fn_callback_api_apigw_deployDelete(call_ret, response, api_request, result_data) {
      this.$console_log('trace', 'fn_callback_api_apigw_deployDelete()', 'call_ret: ', call_ret, 'response :', response, 'api_request: ', api_request, 'result_data: ', result_data);

      let resultMessage = '';
      let deployresult = null;
      
      if ('ok' == call_ret) { //-- 호출성공(not 처리함수성공)
        /*--[ref]
                 호출 : this.$fn_call_api_common(): local script
                 처리함수: apigw_deployDelete(): Controller
                 호출함수: adptranApiService.apigw_deployDelete(): Service
                 호출서비스: gwApiService.deploy(): apigw Service
        --*/
        let response_fn_data = result_data['data']; //-- 호출-처리함수반환data
        let response_fn_resultCd = result_data['resultCd'];
        let response_fn_resultMsg = result_data['resultMsg'];

        let response_deployresult = response_fn_data['deployresult']; //-- 호출-처리함수-호출서비스-apigw서비스반환data(deployresult)
        if (response_deployresult != null) {
          deployresult = response_deployresult;
        }
      }
      else if (('nk' == call_ret) || ('failed' == call_ret)) {
        if ('nk' == call_ret) {
          resultMessage = 'apigw호출을 실패 하였습니다.';
        }
        else if ('failed' == call_ret) {
          resultMessage = 'apigw호출시 오류가 발생 하였습니다.';
        }
        response = response['response'];
        if ($has_own(response, 'data') == true) {
          let response_data = response['data']; //-- 호출반환data
          let data_resultCode = response_data['resultCode'];
          let data_resultMessage = response_data['resultMessage'];
          let data = response_data['data'];
          resultMessage += '\n\n[code: ' + data_resultCode + '][메시지: ' + data_resultMessage + ']\n\n[data: ' + data + ']\n';
        }
      }
      else if ('catched' == call_ret) {
        let errorMessage = response['message'];
        resultMessage = 'apigw호출시  예외가 발생 하였습니다.\n\n[메시지: ' + errorMessage + ']';
      }
      else {
        resultMessage = 'apigw호출시  정의되지 않은 응답값을 수신 하였습니다.\n\n[call_ret: ' + call_ret + ']';
      }

      let ret_data = { 'ret': call_ret, 'ret_msg': resultMessage, 'deployresult': deployresult };
      this.$sf_func_call(this.m_fn_cb_proc_deploy_delete, { 'return': true, 'data': ret_data });
    },
    //-- for deploy }

    //-- for testcase {
    //--[i]not used yet / call at verifyExecute.vue
    //-- options: { 'loadtestdata': 'n', 'readonly': 'n', 'fixedvalueedit': 'n' }
    proc_testcase(prop_param, fn_cb_testcase) {
      this.m_prop_testcaseData = { 'proc_mode': prop_param['proc_mode'], 'api_no': prop_param['api_no'], 'testcase_id': prop_param['testcase_id'], 'options': prop_param['options'], 'api_nm': prop_param['api_nm'] };
      this.m_fn_cb_proc_testcase = fn_cb_testcase; //-- callback function
      this.$modal.show('modalTestcaseDialog');
    },
    emit_proc_finished_testcase(ret_data) {  //-- emit when dialog job finished
      this.$console_log('trace', 'emit_proc_finished_testcase.', 'ret_data: ', ret_data);
      this.$sf_func_call(this.m_fn_cb_proc_testcase, { 'return': true, 'data': ret_data });
    },
    //-- for testcase }

    //-- for testcaselist {
    //--[i]not used yet / call at verifyExecute.vue
    proc_testcaselist(prop_param, fn_cb_testcaselist) {
      this.m_prop_testcaseListData = { 'api_no': prop_param['api_no'], 'filtered_testcase_id_list': prop_param['filtered_testcase_id_list'] };
      this.m_fn_cb_proc_testcaselist = fn_cb_testcaselist; //-- callback function
      this.$modal.show('modalTestcaseListDialog');
    },
    emit_proc_deleted_testcaselist(ret_data) {  //-- emit when testcase deleted
      this.$console_log('trace', 'emit_proc_deleted_testcaselist.', 'ret_data: ', ret_data);
      this.$sf_func_call(this.m_fn_cb_proc_testcaselist, { 'return': true, 'data': ret_data });
    },
    emit_proc_finished_testcaselist(ret_data) {  //-- emit when dialog job finished
      this.$console_log('trace', 'emit_proc_finished_testcaselist.', 'ret_data: ', ret_data);
      this.$sf_func_call(this.m_fn_cb_proc_testcaselist, { 'return': true, 'data': ret_data });
    },
    //-- for testcaselist }

    //-- for verifyview {
    //--[i]not used yet / call at verifyExecute.vue
    proc_verify_view(verify_seq, fn_cb_proc_verify_view) {
      this.m_prop_verifyViewData = { 'verify_seq': verify_seq };
      this.m_fn_cb_proc_proc_verify_view = fn_cb_proc_verify_view; //-- callback function
      this.$modal.show('modalVerifyViewDialog');
    },
    emit_proc_finished_verify_view(ret_data) {  //-- emit when dialog job finished
      this.$console_log('trace', 'emit_proc_finished_verify_view.', 'ret_data: ', ret_data);
      this.$sf_func_call(this.m_fn_cb_proc_proc_verify_view, { 'return': true, 'data': ret_data });
    },
    //-- for verifyview }

    //-- for apiSearch {
    proc_apiSearch(a_apiDatalist, fn_cb_apiSearch) {
      this.m_prop_apiDataList = a_apiDatalist.slice();  //-- array copy
      this.m_fn_cb_proc_apiSearch = fn_cb_apiSearch; //-- callback function
      this.$modal.show('modalApiSearchDialog');
    },
    //-- ret_data: { 'summary_count': o_summary_count }
    emit_proc_finished_apiSearch(ret_data) {  //-- emit when dialog job finished
      this.$console_log('trace', 'emit_proc_finished_apiSearch.', 'ret_data: ', ret_data);
      this.$sf_func_call(this.m_fn_cb_proc_apiSearch, { 'return': true, 'data': ret_data });
    },
    emit_proc_action_apiSearch(ret_data) {  //-- emit when dialog action
      this.$console_log('trace', 'emit_proc_action_apiSearch.', 'ret_data: ', ret_data);
      this.$sf_func_call(this.m_fn_cb_proc_apiSearch, { 'return': true, 'data': ret_data });
    },
    //-- for apiSearch }
    
    //-- for import xlsx {
    proc_importXlsx(prop_param, fn_cb_importXlsx) {
      this.m_prop_importXlsxData = prop_param;
      this.m_fn_cb_proc_importXlsx = fn_cb_importXlsx; //-- callback function
      this.$modal.show('modalImportXlsxDialog');
    },
    emit_proc_finished_import_xlsx(ret_data) {  //-- emit when dialog job finished
      this.$console_log('trace', 'emit_proc_finished_import_xlsx.', 'ret_data: ', ret_data);
      this.$sf_func_call(this.m_fn_cb_proc_importXlsx, { 'return': true, 'data': ret_data });
    },
    //-- for import xlsx }
  } // methods:
};
</script>

<template>
  <section>

    <!-- popup-[배포하기] -->
    <sc-vuemodal
      ref="modalDeployDialog"
      name="modalDeployDialog"
      width="880"
      height="452"
      :clickToClose="false"
      :draggable="false"
      :resizable="false"
      :scrollable="false"
      :minWidth="375"
      :minHeight="226"
    >
      <deploy-dialog
        ref="deployDialog"
        prop_name="modalDeployDialog"
        :prop_Data="m_prop_deployData"
        @emit_proc_finished="emit_proc_finished_deploy"
      >
      </deploy-dialog>
    </sc-vuemodal>

    <!-- popup-[testcase] -->
    <sc-vuemodal
      ref="modalTestcaseDialog"
      name="modalTestcaseDialog"
      width="880"
      height="800"
      :clickToClose="false"
      :draggable="false"
      :resizable="false"
      :scrollable="false"
      :adaptive="false"
      :minWidth="440"
      :minHeight="400"
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
      width="600"
      height="540"
      :clickToClose="false"
      :draggable="false"
      :resizable="false"
      :scrollable="false"
      :adaptive="false"
      :minWidth="600"
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
        @emit_proc_finished="emit_proc_finished_verify_view"
      >
      </verify-view-dialog>
    </sc-vuemodal>

    <!-- popup-[API검색] -->
    <sc-vuemodal
      ref="modalApiSearchDialog"
      name="modalApiSearchDialog"
      width="750"
      height="456"
      :clickToClose="true"
      :draggable="false"
      :resizable="false"
      :scrollable="false"
      :minWidth="375"
      :minHeight="228"
      @keydown.esc="$modal.hide('modalApiSearchDialog')"
    >
      <api-search-dialog
        ref="apiSearchDialog"
        prop_name="modalApiSearchDialog"
        :prop_apiDataList="m_prop_apiDataList"
        @emit_proc_finished="emit_proc_finished_apiSearch"
        @emit_proc_action="emit_proc_action_apiSearch"
        :prop_autoComplete="true"
      >
      </api-search-dialog>
    </sc-vuemodal>

    <!-- popup-[xlsx등록] -->
    <sc-vuemodal
      ref="modalImportXlsxDialog"
      name="modalImportXlsxDialog"
      width="880"
      height="620"
      :clickToClose="false"
      :draggable="false"
      :resizable="false"
      :scrollable="false"
      :minWidth="440"
      :minHeight="310"
    >
      <import-xlsx-dialog
        ref="importXlsxDialog"
        prop_name="modalImportXlsxDialog"
        :prop_Data="m_prop_importXlsxData"
        @emit_proc_finished="emit_proc_finished_import_xlsx"
      >
      </import-xlsx-dialog>
    </sc-vuemodal>

    <modal-dialog/>
  </section>
</template>

<style scoped>
</style>
