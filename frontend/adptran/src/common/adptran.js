import KsmUtil from '@/common/ksmutil.js';
import Environment from '@/app/environment.js';

import Vue from 'vue';
import Vuetable from 'vuetable-2/src/components/Vuetable';
import VModal from 'vue-js-modal';
import { VueModalDialog, ModalService } from '@/modules/vue-modal-dialog';

import axios from 'axios';
import Moment from 'moment';

import CusVuetablePagination from '@/app/component/cus_vuetable_pagination.vue';
import CusMessagePopup from '@/app/component/cus_message_popup.vue';

/*-- componentName을 주지않으면 ModalComponent의 <modal>과  충돌이 일어남 --*/
Vue.use(VModal, {
  dialog: true,
  componentName: 'sc-vuemodal',
  /*componentName: 'sc-vuemodal', dialog: true, dynamic: true, injectModalsContainer: true*/
});
Vue.use(VueModalDialog);

// --[ref] axios.defaults.headers.post['header1'] = 'value';
// --[ref] axios.defaults.headers.common['Access-Control-Allow-Origin'] = '*';

const CommonMixin = {
  data: () => ({
    m_vue_id: 'Unknown',
    m_resources_base: Environment.resourcesBase,
    // --[drm][ref]
    m_axios_options: {
      // withCredentials: true,
      // crossdomain: true,
      // headers: {
      //   'Access-Control-Allow-Credentials': true, 
      //   'Access-Control-Allow-Origin': '*',
      //   'Access-Control-Allow-Methods': 'GET,PUT,POST,DELETE,OPTIONS',
      //   'Access-Control-Allow-Headers': '*',
      //   'Access-Control-Allow-Headers': 'Cache-Control, Pragma, Origin, Authorization, Content-Type, Content-Length, X-Requested-With',
      // }
    },
    //-- @constant {
    //-- verifyExecute.vue
    m_con_def_header_verify: { //-- 검증호출시 추가되는 고정 header
      'Authorization': 'Basic QUlJNDU2MDAzODE2MVNMT0pCRzpUQks0NTYwMDM4MTYxWEdNS09F',
      'userId': '91129294',
      'orgId': 'SPT8050',
      'chnlType': 'SH',
      'srcId': 'adp_user',
    },
    m_enum_VERIFY_INIT: 0,
    m_enum_VERIFY_ING: 1,
    m_enum_VERIFY_SUCC: 2,
    m_enum_VERIFY_FAIL: 3,

    //-- deployDialog.vue
    m_con_deplay_call_retry_num: 3, //-- apigw.deploy()재호출 횟수
    m_con_deplaystatus_call_retry_num: 3, //-- apigw.deployStatus()재호출 횟수
    m_con_INIT_deployrate: 10,
    
    //-- API호출/처리상태: m_con_API_PROC_INIT:초기, 0~n:호출/재호출, m_con_API_PROC_STARTED:호출완료, m_con_API_PROC_FINISHED:처리종료
    m_con_API_PROC_INIT: -1,
    m_con_API_PROC_STARTED: 999,
    m_con_API_PROC_FINISHED: -999,

    //-- testcaseDialog.vue
    m_con_ignore_number_check: false,
    m_con_ignore_integer_check: false,
    m_con_ignore_boolean_check: false,
    
    //-- testcaseDialog.vue, verifyViewDialog.vue
    m_enum_PARAM_GUB_JSON: 'json',  //-- testcase param유형 json

    //-- adptran api url
    m_con_apiDefWithApiSpc_url: '/apiDefWithApiSpc',
    m_con_apiParamTest_url: '/apiParamTest',
    m_con_apiTestCaseList_url: '/apiTestCaseList',
    m_con_apiTestCaseTrans_del_url: '/apiTestCaseTrans/del',
    m_con_apiTestCaseTrans_url: '/apiTestCaseTrans',
    m_con_apiTestCase_url: '/apiTestCase',
    m_con_apiVerify_url: '/apiVerify',
    m_con_apigw_LampLog_url: '/apigw_LampLog',
    m_con_apigw_cpApiGet_url: '/apigw_cpApiGet',
    m_con_apigw_deploy_url: '/apigw_deploy',
    m_con_apigw_deployDelete_url: '/apigw_deployDelete',
    m_con_apigw_deployStatus_url: '/apigw_deployStatus',
    //-- @constant }
  }),
  methods: {
    // -- Ksmutil {
    $console_log(cmd) {
      KsmUtil.$console_log(cmd, this.m_vue_id, ...Array.prototype.slice.call(arguments, 1));
    }, // -- console_log()
    // -- safe string, object
    $sf_str(str, def) { return KsmUtil.$sf_str(str, def); },
    $sf_obj(obj, def) { return KsmUtil.$sf_obj(obj, def); },
    $sf_arr(arr, def) { return KsmUtil.$sf_arr(arr, def); },
    $sf_int(num, def) { return KsmUtil.$sf_int(num, def); },
    $sf_func_call(func, ...a_arg) { return KsmUtil.$sf_func_call(func, ...a_arg); },
    $obj2str(obj) { return KsmUtil.$obj2str(obj); },
    $has_own(obj, key) { return KsmUtil.$has_own(obj, key); },
    $has_val(obj, key) { return KsmUtil.$has_val(obj, key); },
    $sf_obj_val(obj, key, def) { return KsmUtil.$sf_obj_val(obj, key, def); },
    $obj_clone(obj) { return KsmUtil.$obj_clone(obj); },
    $is_json_str(text) { return KsmUtil.$is_json_str(text); },
    $is_json_obj(value) { return KsmUtil.$is_json_obj(value); },
    $sf_json_parse(text, reviver) { return KsmUtil.$sf_json_parse(text, reviver); },
    $sf_json_stringify(value, replacer, space) { return KsmUtil.$sf_json_stringify(value, replacer, space); },
    $is_full_url(p_val) { return KsmUtil.$is_full_url(p_val); },
    $get_full_url_token(url, cmd) { return KsmUtil.$get_full_url_token(p_val); },
    $randomUUID() { return KsmUtil.$randomUUID(); },
    $fn_go_location(p_url, p_o_param) { return KsmUtil.$fn_go_location(p_url, p_o_param); },
    // -- Ksmutil }
    // -- util {
    $is_empty(p_val) { return KsmUtil.$is_empty(p_val); },
    $is_positive_number(p_val) { return KsmUtil.$is_positive_number(p_val); },
    $is_integer(p_val) { return KsmUtil.$is_integer(p_val); },
    $is_number(p_val) { return KsmUtil.$is_number(p_val); },
    $is_boolean(p_val) { return KsmUtil.$is_boolean(p_val); },
    $is_phoneno(p_val) { return KsmUtil.$is_phoneno(p_val); },
    $is_alphanumeric(p_val) { return KsmUtil.$is_alphanumeric(p_val); },
    $is_alphanumeric_korean(p_val) { return KsmUtil.$is_alphanumeric_korean(p_val); },
    $is_domain(p_val) { return KsmUtil.$is_domain(p_val); },
    $is_ip4(p_val) { return KsmUtil.$is_ip4(p_val); },
    $is_ip4_list(p_val) {
      return p_val.split(',').every((element) => { return this.$is_ip4(element.trim()); });
    },
    $is_userid(p_val) {
      return /^[A-Za-z0-9@_\.]+$/.test(p_val);
    },
    $fileDownload(data, filename, mime) {
      let blob = new Blob([data], {type: mime || 'application/octet-stream'});
      if (typeof window.navigator.msSaveBlob !== 'undefined') {
        // IE workaround for "HTML7007: One or more blob URLs were revoked by closing the blob for which they were created.
        // These URLs will no longer resolve as the data backing the URL has been freed."
        window.navigator.msSaveBlob(blob, filename);
      }
      else {
        let blobURL = window.URL.createObjectURL(blob);
        let tempLink = document.createElement('a');
        tempLink.style.display = 'none';
        tempLink.href = blobURL;
        tempLink.setAttribute('download', filename); 
        
        // Safari thinks _blank anchor are pop ups. We only want to set _blank target if the browser does not support the HTML5 download attribute.
        // This allows you to download files in desktop safari if pop up blocking is enabled.
        if (typeof tempLink.download === 'undefined') {
            tempLink.setAttribute('target', '_blank');
        }
        
        document.body.appendChild(tempLink);
        tempLink.click();
        document.body.removeChild(tempLink);
        window.URL.revokeObjectURL(blobURL);
      }
    },
    $read_filedata(file) {
      const reader = new FileReader();
      return new Promise((resolve, reject) => {
        reader.onload = event => { resolve(event.target.result); };
        reader.onerror = error => { reject(error); };
        reader.readAsDataURL(file);
      });
    },
    // --[ref]
    $read_filedata_direct(file, filedata) {
      const reader = new FileReader();
      reader.onload = event => {
        filedata = this.$fmt_data(event.target.result, 'fmt_fileblob');
        this.$console_log( 'o-o', '$read_filedata_direct()', 'filedata: ', filedata);
      };
      reader.readAsDataURL(file);
    },
    // Merge a 'source' object to a 'target' recursively
    $obj_merge(target, source) {
      // Iterate through 'source' properties and if an 'Object' set property to merge of 'target' and 'source' properties
      for (let key of Object.keys(source)) {
        if (source[key] instanceof Object) Object.assign(source[key], this.$obj_merge(target[key], source[key]))
      }
      // Join 'target' and modified 'source'
      Object.assign(target || {}, source)
      return target
    },
    // -- util }
    // -- util_2 {
    $input_invalid(b_is_invalid, s_msg, s_title) {
      if ((b_is_invalid == true) && ((s_msg || '').length > 0)) {
        this.$adpt_alert(s_msg, (s_title||''), false);
      }
      return b_is_invalid;
    },
    $adpt_alert(text, title, backdropClose) {
      title = (title||'&nbsp;');
      let popupOption = {
        backdropClose: ((backdropClose === true) ? backdropClose : false),
        data: {
          title: title,
          text: text,
          use_close_button: true,
          class: '',
          buttons: [{ is_submit: true, text: '확인', result: 'ok', class: 'ok', }],
        },
      };
      return ModalService.open(CusMessagePopup, popupOption);
    },
    $adpt_confirm(text, title, backdropClose) {
      title = (title||'&nbsp;');
      let popupOption = {
        backdropClose: ((backdropClose === true) ? backdropClose : false),
        data: {
          title: title,
          text: text,
          use_close_button: true,
          class: '',
          buttons: [
            { is_submit: true, text: '확인', result: 'ok', class: 'ok', },
            { is_submit: false, text: '취소', result: 'cancel', class: 'cancel', }
          ],
        },
      };
      return ModalService.open(CusMessagePopup, popupOption);
    },
    // -- util_2 }
    // -- biz {
    $fmt_data(value, tag) {
      value = this.$sf_str(value);
      let fmt_value = value;
      if (tag === 'fmt_date_01') { // -- YYYY-MM-DD HH:mm:ss -> YYYY.MM.DD
        if (value.length > 0) {
          let m = Moment.utc(value, 'YYYY-MM-DD HH:mm:ss', true);
          fmt_value = (m.isValid() ? m.format('YYYY.MM.DD') : '#N/A');
        }
      }
      else if (tag === 'fmt_date_02') { // -- YYYY-MM-DD HH:mm:ss -> YYYYMMDD
        if (value.length > 0) {
          let m = Moment.utc(value, 'YYYY-MM-DD HH:mm:ss', true);
          fmt_value = (m.isValid() ? m.format('YYYYMMDD') : '#N/A');
        }
      }
      return fmt_value;
    },
    
    $_ref_fmt_data(value, tag) {
      value = this.$sf_str(value);

      let fmt_value = value;
      if (tag === 'fmt_money') {
        fmt_value = this.$sf_str(value).replace(/\B(?=(\d{3})+(?!\d))/g, ",");
      }
      else if (tag === 'fmt_date_01') { // -- YYYYMMDD HHmmss -> YYYY.MM.DD
        if (value.length > 0) {
          let m = Moment.utc(value, 'YYYYMMDD HHmmss', true);
          fmt_value = (m.isValid() ? m.format('YYYY.MM.DD') : '#N/A');
        }
      }
      else if (tag === 'fmt_date_02') { // -- YYYY-MM-DD HH:mm:ss -> YYYY.MM.DD HH:mm:ss
        if (value.length > 0) {
          let m = Moment.utc(value, 'YYYY-MM-DD HH:mm:ss', true);
          fmt_value = (m.isValid() ? m.format('YYYY.MM.DD HH:mm:ss') : '#N/A');
        }
      }
      else if (tag === 'fmt_date_03') { // -- YYYYMMDD -> YYYY.MM.DD
        if (value.length > 0) {
          let m = Moment.utc(value, 'YYYYMMDD', true);
          fmt_value = (m.isValid() ? m.format('YYYY.MM.DD') : '#N/A');
        }
      }
      else if (tag === 'fmt_date_04') { // -- YYYYMMDD HHmmss -> YYYY.MM.DD HH:mm:ss
        if (value.length > 0) {
          let m = Moment.utc(value, 'YYYYMMDD HHmmss', true);
          fmt_value = (m.isValid() ? m.format('YYYY.MM.DD HH:mm:ss') : '#N/A');
        }
      }
      else if (tag === 'fmt_date_05') { // -- YYYYMMDD HHmmss -> YYYY.MM.DD HH:mm
        if (value.length > 0) {
          let m = Moment.utc(value, 'YYYYMMDD HHmmss', true);
          fmt_value = (m.isValid() ? m.format('YYYY.MM.DD HH:mm') : '#N/A');
        }
      }
      else if (tag === 'fmt_mask_id') {
        let pos = value.length - 3;
        fmt_value = value.substr(0, pos) + value.substr(pos).replace(/./gi, '*');
      }
      else if (tag === 'fmt_mask_name') {
        /*--##[drm][cmt][mask at server]
        let pos = value.length - 1;
        fmt_value = value.substr(0, pos) + value.substr(pos).replace(/./gi, '*'); 
        --*/
      }
      else if (tag === 'fmt_mask_phoneno') {
        /*--##[drm][cmt][mask at server]
        value = value.replace(/[^0-9]/gi, '');
        if (value.length == 10) {
          fmt_value = value.substr(0, 4) + '***' + value.substr(7); 
        }
        else if (value.length == 11) {
          fmt_value = value.substr(0, 5) + '***' + value.substr(8); 
        }
        else {
           let pos = value.length - 3;
          fmt_value = value.substr(0, pos) + value.substr(pos).replace(/./gi, '*');
        } 
        --*/
      }
      else if (tag === 'fmt_fileblob') { // -- readAsDataURL() return -> base64string
        // -- e.g. data:application/x-zip-compressed;base64,xxx data:application/x-mswebsite;base64,xxx
        if (value == 'data:') { value = ''; } // -- for 0byte file
        fmt_value = value.substr(value.indexOf(',') + 1);
      }
      else if (tag === 'fmt_requestbody') {
        fmt_value = fmt_value.replace(/&/g, '&amp;'); 
        fmt_value = fmt_value.replace(/</g, '&lt;'); 
        fmt_value = fmt_value.replace(/>/g, '&gt;');
        fmt_value = fmt_value.replace(/\"/g, '&quot;');
        fmt_value = fmt_value.replace(/\'/g, '&apos;');
        fmt_value = fmt_value.replace(/\t/g, ' &nbsp; &nbsp; &nbsp; &nbsp;');
        fmt_value = fmt_value.replace(/\n/g, '<br>');
        fmt_value = fmt_value.replace(/\s/g, '&nbsp;');
      }
      else if (tag === 'fmt_filesize') {
        let bytes = parseInt(value, 10);
        let si = true;
        var thresh = (si ? 1000 : 1024);
        if (Math.abs(bytes) < thresh) {
          fmt_value = bytes + 'B';
        }
        else {
          let units = (si ? ['KB','MB','GB','TB','PB','EB','ZB','YB'] : ['KiB','MiB','GiB','TiB','PiB','EiB','ZiB','YiB']);
          var u = -1;
          do {
              bytes /= thresh;
              ++u;
          } while((Math.abs(bytes) >= thresh) && (u < units.length - 1));
          fmt_value = (bytes.toFixed(1) + units[u]);
        }
      }
      return fmt_value;
    },
    $_ref_fmt_date_string(s_date) {
      let ret = 'YYYYMMDD HHmmss';
      s_date = this.$sf_str(s_date);
      if (s_date.length == 10) {
        ret = 'YYYYMMDDHH';
      }
      else if (s_date.length == 8) {
        ret = 'YYYYMMDD';
      }
      else if (s_date.length == 6) {
        ret = 'YYYYMM';
      }
      return ret;
    },
    $fmt_response_messge(msg, res_code, res_message) {
      // --##return (msg + '<br>' + res_message + '[' + res_code + ']')
      msg = this.$sf_str(msg);
      res_code = this.$sf_str(res_code);
      res_message = this.$sf_str(res_message);
      
      res_code = ((res_code.length > 0) ? ('[code: ' + res_code + ']<br>') : '');
      let sub_msg = (res_code + res_message);
      return (msg + ((sub_msg.length > 0) ? '<br><br>' : '') + sub_msg);
    },
    $getAdptranApiUrl() { return Environment.adptranApiUrl; },
    $getServiceHomeUrl() { return Environment.defineUrl.serviceHome; },
    $getLoginUrl() { return Environment.defineUrl.login; },
    $getApiRegUrl(apiSpcNo) { return Environment.defineUrl.mvApiPathReg.replace(/#apiSpcNo#/g, apiSpcNo); },  //--[ref]
    // -- http 호출
    /*--[ref]
      response: {
        data: {},	//-- response that was provided by the server
        status: 200,	//-- HTTP status code from the server response
        statusText: 'OK',	//-- HTTP status message from the server response
        headers: {},	//-- headers that the server responded with All header names are lower cased
        config: {},	//-- config that was provided to 'axios' for the request
        request: {}	//-- request that generated this response. It is the last ClientRequest instance in node.js (in redirects) and an XMLHttpRequest instance the browser
      }
    --*/
    $getHttpResponse(url, httpMethod, request, opt_axiosConfig) {
      this.$console_log('watch', '$getHttpResponse.', 'url: ', url, 'httpMethod: ', httpMethod, 'request: ', request, 'opt_axiosConfig: ', opt_axiosConfig);
      let axiosConfig = { 'url': url, 'method': httpMethod };
      axiosConfig[((httpMethod == 'get') ? 'params' : 'data')] = request;
      //--@@axiosConfig = ((opt_axiosConfig) ? Object.assign(axiosConfig, opt_axiosConfig) : axiosConfig);
      //--@@axiosConfig = Object.assign(axiosConfig, this.m_axios_options);
      axiosConfig = ((opt_axiosConfig) ? this.$obj_merge(axiosConfig, opt_axiosConfig) : axiosConfig);
      axiosConfig = this.$obj_merge(axiosConfig, this.m_axios_options);
      this.$console_log('watch', '$getHttpResponse()', 'axiosConfig: ', axiosConfig);
      return axios.request(axiosConfig);
    },
    // -- adpt api 호출
    $callAdptApi(apipath, request, success, failed, catched, opt_axiosConfig) {
      this.$console_log('watch', '$callAdptApi.', 'apipath: ', apipath, 'request: ', request);
      if (typeof(failed) != 'function') {
        failed = (response) => {
          this.$console_log('watch', '$callAdptApi().', 'apipath: ', apipath, 'failed', 'response: ', response);
        };
      }
      if (typeof(catched) != 'function') {
        catched = (error) => {
          this.$console_log('watch', '$callAdptApi().', 'apipath: ', apipath, 'catched', 'error: ', error);
        };
      }
      let method = this.$sf_obj_val(opt_axiosConfig, 'method', 'post');
      this.$getHttpResponse(this.$getAdptranApiUrl() + apipath, method, request, opt_axiosConfig).then(success, failed).catch(error => catched(error));
    },
    $isApiSuccess(result) {
      this.$console_log('watch', '$isApiSuccess.', 'result: ', result);
      let isSuccess = true;
      let resultCode = this.$sf_obj_val(result, 'resultCode', -1);
      isSuccess = (resultCode == 200);	//-- success
      return isSuccess;
    },
    $fn_call_api_common(api_url, api_request, method, fn_callback) {
      this.$console_log('watch', '$fn_call_api_common.', 'api_url: ', api_url, 'api_request: ', api_request, 'method: ', method, 'fn_callback: ', fn_callback);
      let success = ((response) => {
        this.$console_log('watch', '$fn_call_api_common().', 'success', 'response: ', response);
        let result = response.data;
        if (true == this.$isApiSuccess(result)) {
          fn_callback('ok', response, api_request, result['data']);
        }
        else {
          this.$console_log('watch', '$fn_call_api_common().', '$isApiSuccess() == false', 'response: ', response);
          fn_callback('nk', response, api_request);
        }
      });
      let failed = ((response) => {
        this.$console_log('watch', '$fn_call_api_common().', 'failed', 'response: ', response);
        fn_callback('failed', response, api_request);
      });
      let catched = ((error) => {
        this.$console_log('watch', '$fn_call_api_common().', 'catched', 'error: ', error);
        fn_callback('catched', error, api_request);
      });
      let o_option = ((method == 'get') ? {'method':'get'} : {});
      this.$callAdptApi(api_url, api_request, success, failed, catched, o_option);
    },
    //-- ret: 'ok', 'nk', 'failed', 'catched'
    $proc_api_resultCode_Fail(call_ret, response) {
      this.$console_log('watch', '$proc_api_resultCode_Fail.', 'call_ret: ', call_ret, 'response: ', response);
      let resultFailMessage = '';
      if ('nk' == call_ret) {
        resultFailMessage = 'api호출을 실패 하였습니다.';
        let response_data = response['data'];  //-- call result: {resultCode: 200, resultMessage: "SUCCESS", data: {…}, totalCount: null, pageSize: null, currentPage: null}
        let response_proc_data = response_data['data'];
        if ((true == this.$has_own(response_proc_data, 'resultCd')) && (true == this.$has_own(response_proc_data, 'resultMsg'))) {
          //-- proc data: {resultCd: 900, resultMsg: "TestCase정보 등록이 실패하였습니다."}
          let resultCd = response_proc_data['resultCd'];
          let resultMsg = response_proc_data['resultMsg'];
          resultFailMessage += '\n\n[code: ' + resultCd + '][메시지: ' + resultMsg + ']\n';
        }
        else {
          let resultCode = response_data['resultCode'];
          let resultMessage = response_data['resultMessage'];
          resultFailMessage += '\n\n[code: ' + resultCode + '][메시지: ' + resultMessage + ']\n';
        }
      }
      else if ('failed' == call_ret) {
        resultFailMessage = 'api호출시 오류가 발생 하였습니다.';
        let resultMsg = $sf_str(response['message']);
        resultMsg = ((resultMsg.length == 0) ? $sf_obj_val($sf_obj(response['response']), 'statusText') : resultMsg);

        let status = $sf_str($sf_obj_val($sf_obj(response['response']), 'status')); //-- 404...
        let extMsg = ((status.length > 0) ? '[code: ' + status + ']' : '');
        extMsg += ((resultMsg.length > 0) ? '[메시지: ' + resultMsg + ']' : '');
        
        resultFailMessage += ((extMsg.length > 0) ? ('\n\n' + extMsg + '\n') : '');
      }
      else if ('catched' == call_ret) {
        let errorMessage = response['message'];
        resultFailMessage = 'api호출시 예외가 발생 하였습니다.\n\n[메시지: ' + errorMessage + ']';
      }
      else {
        resultFailMessage = 'api호출시  정의되지 않은 응답값을 수신 하였습니다.\n\n[call_ret: ' + call_ret + ']';
      }
      if (resultFailMessage.length > 0) {
        this.$adpt_alert(resultFailMessage, '오류', false);
      }
    },

    // -- [ref] 공통코드조회
    $_ref_call_api_apiDefWithApiSpc(apiNo, fn_callback) {
      let success = ((response) => {
        this.$console_log('watch', '$call_api_apiDefWithApiSpc().', 'success', 'response: ', response);
        let result = response.data;
        if (true == this.$isApiSuccess(result)) {
          fn_callback('ok', response, result['data']);
        } else {
          this.$console_log('watch', '$call_api_apiDefWithApiSpc().', '$isApiSuccess() == false', 'response: ', response);
          fn_callback('nk', response, []);
        }
      });
      let failed = (response) => {
        this.$console_log('watch', '$call_api_apiDefWithApiSpc().', 'failed', 'response: ', response);
        fn_callback('failed', response, []);
      };
      let catched = (error) => {
        this.$console_log('watch', '$call_api_apiDefWithApiSpc().', 'catched', 'error: ', error);
        fn_callback('catched', error, []);
      };
      let api_request = {};
      this.$callAdptApi('/apiParam/' + apiNo, api_request, success, failed, catched);
    },
    // -- biz }
    //-- ui {
    $fn_ui_prepare_sc_vuetable_custom_scrollbar(selector, options) {
      let jq = $(selector ? selector : document).find('.vuetable-body-wrapper.fixed-header'); 
      this.$fn_ui_prepare_custom_scrollbar(jq, options);
    },
    $fn_ui_prepare_custom_scrollbar(selector, options) {
      if (this.$has_own($, 'mCustomScrollbar') == false) { 
        this.$console_log('warn', '$fn_ui_prepare_custom_scrollbar()', 'mCustomScrollbar is not installed');
        return;
      }
      options = Object.assign({
        theme: 'dark-3',
        mouseWheelPixels : 500, // 마우스휠 속도
        scrollInertia : 400     // 부드러운 스크롤 효과 적용
      }, options);
      let jq = $(selector ? selector : document);
      if (jq.length == 0) {
        this.$console_log('warn', '$fn_ui_prepare_custom_scrollbar()', 'mCustomScrollbar target selector is not found');
      }
      else {
        jq.mCustomScrollbar(options);
      }
    },
    //-- ui }
  } // -- methods:
}; // -- const CommonMixin{}

export {
  CommonMixin,
  Vuetable,
  CusMessagePopup,
  CusVuetablePagination
};
