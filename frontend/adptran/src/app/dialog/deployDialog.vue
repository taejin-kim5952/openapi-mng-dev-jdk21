<comment>
  @apigw deploy
    popup-[배포하기]
</comment>
<script>
import { CommonMixin } from '@/common/adptran.js';
import { mixin as VueTimers } from 'vue-timers';

export default {
  name: 'deployDialog',
  props: {
    prop_name: {
      type: String,
      required: true,
    },
    prop_Data: {
      type: Array,
      required: true,
      default: (() => []),  //-- [{ 'proc_seq': '', 'api_no': '', 'gw_profile': 'TB|PROD', 'api_id': '', 'api_nm': '', 'deployapply_seq': '' }]
      validator: function(value) {
        return value.every((item) => {
          if (typeof(item) != 'object') { return false; }
          if (item.hasOwnProperty('proc_seq') == false) { return false; }
          if (item.hasOwnProperty('api_no') == false) { return false; }
          if (item.hasOwnProperty('gw_profile') == false) { return false; }
          if (item.hasOwnProperty('api_nm') == false) { return false; }
          if (item.hasOwnProperty('api_id') == false) { return false; }
          if ((parseInt(item['proc_seq'], 10) > 0) == false) { return false; }
          if ((parseInt(item['api_no'], 10) > 0) == false) { return false; }
          if ((item['gw_profile'] != 'TB') && (item['gw_profile'] != 'PROD')) { return false; }
          if (item['gw_profile'] == 'PROD') { //-- PROD일떄만 추가
            if (item.hasOwnProperty('deployapply_seq') == false) { return false; }
            if ((parseInt(item['deployapply_seq'], 10) > 0) == false) { return false; }
          }
          return true;
        });
      }
    },
  },
  mixins: [CommonMixin, VueTimers],
  data() {
    return {
      m_vue_id: 'deployDialog',

      //-- @biz-data {
      m_arr_deployData: [], //-- for order

      m_is_deploy_finished: false,
      //-- @biz-data }
    } // return{}
  }, // data()
  computed: {
    ui_ctrl_btn_ok_disabled() {
      return (this.m_is_deploy_finished == false);
    },
    computed_api_item_info() {
      return ((deploy_item, cmd) => {
        let s_info = '';
        if ('idx' == cmd) {
          s_info = deploy_item['idx'] + 1;
        }
        else if ('api_nm' == cmd) {
          s_info = deploy_item['api_nm'];
        }
        else if ('message' == cmd) {
          s_info = deploy_item['info']['message'];
        }
        else if ('deployrate' == cmd) {
          s_info = deploy_item['info']['deployrate'];
          s_info = s_info.toFixed(1);
        }
        else if ('result' == cmd) {
          s_info = deploy_item['info']['result'];
        }
        else if ('css_result' == cmd) {
          s_info = deploy_item['info']['css_result'];  //-- blue_txt, red_txt
        }
        else if ('css_pgbar' == cmd) {
          s_info = deploy_item['info']['css_pgbar'];  //-- failStatus
        }
        return s_info;
      });
    },
  },
  watch: {
  },
  created: function () {
    this.$console_log('trace', 'created()');
    //-- copy prop data to member data
    this.m_arr_deployData = [];
    let idx = 0;
    this.prop_Data.forEach((item) => {
      if (this.m_arr_deployData.findIndex((elem) => { return (elem['api_no'] == item['api_no']); }) == -1) {  //-- 미등록시에만 추가
        let deploy_item = Object.assign(item, {
          'idx': idx,
          'deployresult': this.fn_get_init_deployresult(),
          'info': { 'message': '', 'deployrate': 0, 'result': '', 'css_result': '', css_pgbar: '' },
          'deploy_call_cnt': this.m_con_API_PROC_INIT,  //-- m_con_API_PROC_INIT:초기, 0~n:호출/재호출, m_con_API_PROC_STARTED:호출완료, m_con_API_PROC_FINISHED:처리종료
          'deploystatus_call_cnt': this.m_con_API_PROC_INIT,  //-- m_con_API_PROC_INIT:초기, 0~n:호출/재호출
        });
        this.m_arr_deployData.push(deploy_item);
        idx++;
      }
      else {
        this.$console_log('warn', 'created().', 'm_arr_deployData[] duplicate item', 'item[api_no]: ', item['api_no']);
      }
    });
    /*--[ref]
      deployresult.status: 
        0: STANDBY
        1: INIT
        2: DEPLOYING
        3-1: FAIL ROLLING_BACK
        3-2: DONE,
       jobStatus:
        0: STANDBY
        1: DOING
        2: DONE, FAILURE
    */
  },
  mounted: function () {
    this.$console_log('trace', 'mounted()');
    //-- 배포처리
    this.fn_start_deploy();
  },
  updated: function () {
    this.$console_log('trace', 'updated()');
  },
  timers: {
    proc_deploy_loop: { time: 3000, autostart: false, repeat: true, },
    //--@@ log_timer: { time: 1000, autostart: true, repeat: true, },
  },
  methods: {
    //-- @function {
    log_timer() {
      console.log('Hello world');
    },
    //-- deployResult초기객체
    fn_get_init_deployresult() {
      return { 'status': 'STANDBY', 'message': '', 'servers': [], 'alServers': [], };
    },
    //-- 전체 api 배포
    fn_start_deploy() {
      this.$console_log('trace', 'fn_start_deploy()');

      this.m_arr_deployData.forEach((api_item, idx) => {
        api_item['deploy_call_cnt'] = 0;
        this.fn_call_api_deploy(api_item, idx); //-- deploy호출
      });

      this.$timer.stop('proc_deploy_loop');
      this.$timer.start('proc_deploy_loop');  //-- 모든api deploy처리후 timer시작
      this.$console_log('o-o', 'fn_start_deploy()', 'this.$timer.start(\'proc_deploy_loop\')');
    },
    //-- 전체 api 배포상태 점검/처리
    proc_deploy_loop() {
      this.$console_log('trace', 'proc_deploy_loop()');

      let finished_cnt = 0; //-- 종료된api
      let retry_deploy_cnt = 0;  //-- deploy재호출api
      let retry_deploystatus_cnt = 0;  //-- deploystatus재호출api
      let o_summary_count = {};

      this.m_arr_deployData.forEach((api_item, idx) => {
        //-- deploy_call_cnt: m_con_API_PROC_INIT:초기, 0~n:호출/재호출, m_con_API_PROC_STARTED:호출완료, m_con_API_PROC_FINISHED:처리종료
        //-- deploystatus_call_cnt: m_con_API_PROC_INIT:초기, 0~n:호출/재호출

        let deploy_call_cnt = api_item['deploy_call_cnt'];
        let deploystatus_call_cnt = api_item['deploystatus_call_cnt'];

        if (this.m_con_API_PROC_STARTED == deploy_call_cnt) { //-- 호출완료상태
          //-- deploystatus호출횟수 처리
          deploystatus_call_cnt++;
          api_item['deploystatus_call_cnt'] = deploystatus_call_cnt;
          if (deploystatus_call_cnt > this.m_con_deplaystatus_call_retry_num) {  //-- deploystatus재호출횟수초과 처리
            deploy_call_cnt = this.m_con_API_PROC_FINISHED;
            api_item['deploy_call_cnt'] = deploy_call_cnt;
          }
          if (this.m_con_API_PROC_FINISHED == deploy_call_cnt) {  //-- 처리종료상태
            finished_cnt++;
            this.$nextTick(() => { this.fn_update_api_deploy_info(idx, 'deploy_loop'); }); //-- 상태출력갱신
          }
          else {
            this.fn_call_api_deploy_status(api_item, idx);  //-- deploystatus호출
            retry_deploystatus_cnt++;
          }
        }
        else if (this.m_con_API_PROC_FINISHED == deploy_call_cnt) { //-- 처리종료상태
          finished_cnt++;
        }
        else {
          deploy_call_cnt++;
          deploy_call_cnt = ((deploy_call_cnt > this.m_con_deplay_call_retry_num) ? this.m_con_API_PROC_FINISHED : deploy_call_cnt);  //-- deploy재호출횟수초과 처리
          api_item['deploy_call_cnt'] = deploy_call_cnt;
          if (this.m_con_API_PROC_FINISHED == deploy_call_cnt) {  //-- 처리종료상태
            finished_cnt++;
            this.$nextTick(() => { this.fn_update_api_deploy_info(idx, 'deploy_loop'); }); //-- 상태출력갱신
          }
          else {
            this.fn_call_api_deploy(api_item, idx); //-- deploy재호출
            retry_deploy_cnt++;
          }
        }
        
        //-- 처리요약 {
        let deploy_status = this.$sf_str(api_item['deployresult']['status']);
        let status_cnt;
        let deploy_status_list = ';STANDBY;INIT;DEPLOYING;FAIL;ROLLING_BACK;DONE;';
        //--@@if ((deploy_status_list.indexOf(';' + deploy_status + ';') != -1) {
        if (deploy_status.length == 0) {
          deploy_status ='ETC';
        }

        status_cnt = parseInt(this.$sf_obj_val(o_summary_count, deploy_status, '0'));
        o_summary_count[deploy_status] = (status_cnt + 1);
        //-- 처리요약 }
      });

      let deployData_cnt = this.m_arr_deployData.length;
      this.m_is_deploy_finished = (finished_cnt >= deployData_cnt);
      if (this.m_is_deploy_finished == true) {
        this.$timer.stop('proc_deploy_loop'); //-- 모든api 호출중단상태면 timer종료
        this.$console_log('o-o', 'proc_deploy_loop()', 'this.$timer.stop(\'proc_deploy_loop\')');

        //-- emit with 처리요약 {
        let ret_data = { 'summary_count': o_summary_count };
        this.$emit('emit_proc_finished', ret_data);
        this.$console_log('o-o', 'proc_deploy_loop().', '$emit(emit_proc_finished)', 'ret_data: ', ret_data);
        //-- emit with 처리요약 }
      }

      this.$console_log('o-o', 'proc_deploy_loop().', 'finished_cnt: ', finished_cnt, 'retry_deploy_cnt: ', retry_deploy_cnt, 'retry_deploystatus_cnt: ', retry_deploystatus_cnt);
      this.$console_log('o-o', 'proc_deploy_loop().', 'o_summary_count: ', o_summary_count);
    },    
    //-- api 배포호출
    fn_call_api_deploy(api_item, idx) {
      this.$console_log('trace', 'fn_call_api_deploy()', 'api_item: ', api_item);
      let call_func = 'deploy';

      let success = ((response) => {
        let response_data = response['data'];
        let call_ret = (this.$isApiSuccess(response_data) ? 'ok' : 'nk');
        this.$console_log('watch', 'fn_call_api_deploy().', 'success', 'idx: ', idx, 'call_func: ', call_func, 'call_ret: ', call_ret, 'response: ', response);
        this.$nextTick(() => { this.fn_update_api_deploy_info(idx, call_func, call_ret, response_data); });
      });
      let failed = ((response) => {
        let call_ret = 'failed';
        this.$console_log('watch', 'fn_call_api_deploy().', 'failed', 'idx: ', idx, 'call_func: ', call_func, 'call_ret: ', call_ret, 'response: ', response);
        this.$nextTick(() => { this.fn_update_api_deploy_info(idx, call_func, call_ret); });
      });
      let catched = ((error) => {
        let call_ret = 'catched';
        this.$console_log('watch', 'fn_call_api_deploy().', 'catched', 'idx: ', idx, 'call_func: ', call_func, 'call_ret: ', call_ret, 'error: ', error);
        this.$nextTick(() => { this.fn_update_api_deploy_info(idx, call_func, call_ret); });
      });
      let action_type = "CREATE";
      let async = "Y";
      let check_deploystatus = "Y";  //-- deploy호출전 deployStatus check 여부
      let api_request = { 'proc_seq': api_item['proc_seq'], 'api_no': api_item['api_no'], 'api_id': api_item['api_id'], 'gw_profile': api_item['gw_profile'], 'action_type': action_type, 'async': async, 'check_deploystatus': check_deploystatus };
      this.$callAdptApi(this.m_con_apigw_deploy_url, api_request, success, failed, catched);
    },
    //-- api 배포상태조회
    fn_call_api_deploy_status(api_item, idx) {
      this.$console_log('trace', 'fn_call_api_deploy_status()', 'api_item: ', api_item);
      let call_func = 'deploy_status';
    
      let success = ((response) => {
        let response_data = response['data'];
        let call_ret = (this.$isApiSuccess(response_data) ? 'ok' : 'nk');
        this.$console_log('watch', 'fn_call_api_deploy_status().', 'success', 'idx: ', idx, 'call_func: ', call_func, 'call_ret: ', call_ret, 'response: ', response);
        this.$nextTick(() => { this.fn_update_api_deploy_info(idx, call_func, call_ret, response_data); });
      });
      let failed = ((response) => {
        let call_ret = 'failed';
        this.$console_log('watch', 'fn_call_api_deploy_status().', 'failed', 'idx: ', idx, 'call_func: ', call_func, 'call_ret: ', call_ret, 'response: ', response);
        this.$nextTick(() => { this.fn_update_api_deploy_info(idx, call_func, call_ret); });
      });
      let catched = ((error) => {
        let call_ret = 'catched';
        this.$console_log('watch', 'fn_call_api_deploy_status().', 'catched', 'idx: ', idx, 'call_func: ', call_func, 'call_ret: ', call_ret, 'error: ', error);
        this.$nextTick(() => { this.fn_update_api_deploy_info(idx, call_func, call_ret); });
      });
      let api_request = { 'proc_seq': api_item['proc_seq'], 'api_no': api_item['api_no'], 'api_id': api_item['api_id'], 'gw_profile': api_item['gw_profile'] };
      if ('PROD' == api_item['gw_profile']) {
        api_request = Object.assign(api_request, { 'deployapply_seq': api_item['deployapply_seq'] });
      }
      this.$callAdptApi(this.m_con_apigw_deployStatus_url, api_request, success, failed, catched);
    },
    //-- api 정보update
    //-- call_func: [deploy | deploy_status | deploy_loop]
    //-- call_ret: [ok | nk | failed | catched]
    fn_update_api_deploy_info(idx, call_func, call_ret, response_data) {
      if ((idx < 0) || (idx >= this.m_arr_deployData.length)) {
        this.$console_log('warn', 'fn_update_api_deploy_info().', 'invalid index', 'idx: ', idx);
        return;
      }

      let api_item = this.m_arr_deployData[idx];
      this.$console_log('trace', 'fn_update_api_deploy_info()', 'api_item: ', api_item, 'idx: ', idx, 'call_func: ', call_func, 'call_ret: ', call_ret, 'response_data: ', response_data);
    
      /*--[ref]
      api_item = {
        'proc_seq': '', 'api_no': '', 'gw_profile': 'TB|PROD', 'api_id':'', 'api_nm': '', 'deployapply_seq': '',
        'idx': idx,
        'deployresult': { 'status': 'STANDBY', 'message': '', 'servers': [], 'alServers': [], },
        'info': { 'message': '', 'deployrate': 0, 'result': '', 'css_result': '', css_pgbar: '' },
        'deploy_call_cnt': this.m_con_API_PROC_INIT,  //-- m_con_API_PROC_INIT:초기, 0~n:호출/재호출, m_con_API_PROC_STARTED:호출완료, m_con_API_PROC_FINISHED:처리종료
        'deploystatus_call_cnt: m_con_API_PROC_INIT:초기, 0~n:호출/재호출
      }
      --*/
      let deployresult = api_item['deployresult'];     //-- this.fn_get_init_deployresult();
      let api_item_info = api_item['info'];

      let info_message = api_item_info['message'];
      let info_deployrate = api_item_info['deployrate'];
      let info_result = api_item_info['result'];
      let info_css_result =  api_item_info['css_result'];
      let info_css_pgbar = api_item_info['css_pgbar'];

      let deploy_call_cnt = api_item['deploy_call_cnt'];
      let deploystatus_call_cnt = api_item['deploystatus_call_cnt'];

      let is_reclac_deployrate = false; //-- 진행률 재산정여부
      
      if ('ok' == call_ret) { //-- 호출성공(not 처리함수성공)
        /*--[ref]
                 호출 : this.$callAdptApi(): local script
                 처리함수: apigw_deploy(): Controller
                 호출함수: adptranApiService.apigw_deploy(): Service
                 호출서비스: gwApiService.deploy(): apigw Service
        --*/
        let response_fn_data = this.$sf_obj(response_data['data']); //-- 호출-처리함수반환data

        let response_fn_resultCd = this.$sf_obj_val(response_fn_data, 'resultCd');
        let response_fn_resultMsg = this.$sf_obj_val(response_fn_data, 'resultMsg');

        let response_deploy_data = this.$sf_obj(response_fn_data['data']);  //-- 호출-처리함수-호출서비스반환data
        //--[i]apientity설정함수 success일시 data.deployresult 설정됨
        let response_deployresult = null;
        if (true == this.$has_val(response_deploy_data, 'deployresult')) {
          response_deployresult = this.$sf_obj(response_deploy_data['deployresult']); //-- 호출-처리함수-호출서비스-apigw서비스반환data(deployresult)
        }

        let is_deploystatus_call_cnt_init = false;  //-- 상태조회 재호출횟수 초기화 여부

        let deploystatus = '#N/A';
        if (response_deployresult != null) {
          deployresult = response_deployresult;
          deploystatus = deployresult['status'];
        }
        else {
          if ('deploy' ==  call_func) {
            if (response_fn_resultCd == -101) {  //-- RC_APIGW_FN_DEPLOY_SET_APIENTITY_ERR
              //--[i]호출-처리함수-apientity설정함수(return set_ApiInfo_To_ApiEntity() resultCd, resultMsg)
              let response_apientity_resultCd = this.$sf_str(this.$sf_obj_val(response_deploy_data, 'set_apientity_resultCd'));
              let response_apientity_resultMsg = this.$sf_str(this.$sf_obj_val(response_deploy_data, 'set_apientity_resultMsg'));
              deploystatus = 'FN_DEPLOY_PROC_FINISHED';
              info_message = '배포정보 설정 오류.';
              if (response_apientity_resultMsg.length > 0) {
                info_message = response_apientity_resultMsg;
              }
              else {
                info_message += ((response_apientity_resultCd.length > 0) ? ("(" + response_apientity_resultCd + ")") : "");
              }
            }
            else if ((response_fn_resultCd <= -201) && (response_fn_resultCd >= -204)) { //-- RC_APIGW_FN_DEPLOY_CHECK_STATUS_STANDBY, _INIT, _DEPLOYING, _ROLLING_BACK
              deploystatus = 'FN_DEPLOY_PROC_FINISHED';
              info_message = '선행 배포작업 처리중.';
              if (response_fn_resultMsg.length > 0) {
                info_message = response_fn_resultMsg;
              }
            }
            else {
              deploystatus = 'FN_DEPLOY_PROC_FINISHED';
              info_message = '미정의 복귀값.[' + response_fn_resultCd + ']';
            }
          }
          else if ('deploy_status' ==  call_func) {
            deploystatus = 'JOB_IS_NOT_EXIST';
          }
        }
        
        if (deploystatus == 'FN_DEPLOY_PROC_FINISHED') {
          deploy_call_cnt = this.m_con_API_PROC_FINISHED;
          info_result = '미처리';
          info_css_result = 'red_txt'
          info_css_pgbar = 'failStatus';
          info_deployrate = 100;
        }
        else if (deploystatus == 'JOB_IS_NOT_EXIST') {
          info_message = '배포상태조회 오류.';
          info_result = '상태미확인';
          info_css_result = 'red_txt'
          info_css_pgbar = 'failStatus';
        }
        else if (deploystatus == 'STANDBY') {
          deploy_call_cnt = this.m_con_API_PROC_STARTED;
          info_message = '대기중...';
          info_result = '대기중';
          info_css_result = 'blue_txt';
          info_deployrate = 0;
          is_deploystatus_call_cnt_init = true;
        }
        else if (deploystatus == 'INIT') {
          deploy_call_cnt = this.m_con_API_PROC_STARTED;
          info_message = '초기화중...';
          info_result = '진행중';
          info_css_result = 'blue_txt';
          info_deployrate = this.m_con_INIT_deployrate;
          is_deploystatus_call_cnt_init = true;
        }
        else if (deploystatus == 'DEPLOYING') {
          deploy_call_cnt = this.m_con_API_PROC_STARTED;
          info_message = '배포중...';
          info_result = '진행중';
          info_css_result = 'blue_txt';
          is_reclac_deployrate = true;
          is_deploystatus_call_cnt_init = true;
        }
        else if (deploystatus == 'DONE') {
          deploy_call_cnt = this.m_con_API_PROC_FINISHED;
          info_deployrate = 100;
          info_message = '배포에 성공하였습니다.';
          info_result = '성공';
          info_css_result = 'blue_txt';
        }
        else if (deploystatus == 'ROLLING_BACK') {
          deploy_call_cnt = this.m_con_API_PROC_STARTED;
          info_message = '롤백중...';
          info_result = '실패';
          info_css_result = 'red_txt';
          info_css_pgbar = 'failStatus';
          is_deploystatus_call_cnt_init = true;
        }
        else if (deploystatus == 'FAIL') {
          deploy_call_cnt = this.m_con_API_PROC_FINISHED;
          info_deployrate = 100;
          info_message = '배포 중 에러가 발생하였습니다.';
          info_result = '실패';
          info_css_result = 'red_txt'
          info_css_pgbar = 'failStatus';
        }
        else {
          info_message = '정의되지 않은 상태 입니다.';
          info_result = deploystatus;
          info_css_pgbar = 'failStatus';
        }

        if (is_deploystatus_call_cnt_init == true) {
          deploystatus_call_cnt = this.m_con_API_PROC_INIT;  //-- 상태조회 재호출 초기화
        }
      }
      
      if ('ok' != call_ret) { //-- 'nk', 'falied', catched'
        if ('deploy' ==  call_func) {
          info_message = '배포호출 중 에러가 발생하였습니다.';
          info_result = '-';
          info_css_result = 'red_txt'
          info_css_pgbar = 'failStatus';
        }
        else if ('deploy_status' ==  call_func) {
          info_message = '배포상태조회 중 에러가 발생하였습니다.';
          info_result = '-';
          info_css_result = 'red_txt'
          info_css_pgbar = 'failStatus';
        }
      }

      //-- 재호출상태처리
      if ('deploy' ==  call_func) {
        if (deploy_call_cnt == this.m_con_deplay_call_retry_num) {
          deploy_call_cnt = this.m_con_API_PROC_FINISHED;
          info_result = '미처리';
          info_deployrate = 100;
        }
        else if ((deploy_call_cnt > 0) && (deploy_call_cnt < this.m_con_deplay_call_retry_num)) {
          info_result = '재호출: ' + deploy_call_cnt + '회-미처리';
          //-- [test][ing]@@
          /*--@@
          let max = (100 - info_deployrate);
          let rnd = ((max * 10) / 100); //-- 남은중의 최소 10% 증가
          max = (max - rnd);
          rnd = Math.floor(Math.random() * max);
          info_deployrate = info_deployrate + rnd;
          --*/
        }
      }
      else if ('deploy_status' ==  call_func) {
        if (deploystatus_call_cnt == this.m_con_deplaystatus_call_retry_num) {
          deploy_call_cnt = this.m_con_API_PROC_FINISHED;
          info_result = '미처리';
          info_deployrate = 100;
        }
        else if ((deploystatus_call_cnt > 0) && (deploystatus_call_cnt < this.m_con_deplaystatus_call_retry_num)) {
          info_result = '재조회: ' + deploystatus_call_cnt + '회';
          //-- [test][ing]@@
          /*--@@
          info_css_pgbar = '';  //-- blue bar
          let max = (100 - info_deployrate);
          let rnd = ((max * 10) / 100); //-- 남은중의 최소 10% 증가
          max = (max - rnd);
          rnd = Math.floor(Math.random() * max);
          info_deployrate = info_deployrate + rnd;
          --*/
        }
      }
      if (call_func == 'deploy_loop') {
        if (this.m_con_API_PROC_FINISHED == deploy_call_cnt) {
          info_deployrate = 100;
          info_result = '미처리';
        }
      }

      //-- 진행률 재계산
      if (is_reclac_deployrate == true) {
        let arr_server = [].concat(deployresult['servers'], deployresult['alServers']);
        let total_cnt = arr_server.length;
        let done_cnt = 0;
        arr_server.forEach((elem) => {
          if (('DONE' == elem['status']) || ('FAILURE' == elem['status'])) { // jobStatus: STANDBY, DOING, DONE, FAILURE
            done_cnt++;
          }
        });
        let done_rate = ((done_cnt / total_cnt) * 100);
        info_deployrate = (this.m_con_INIT_deployrate + ((done_rate * (100 - this.m_con_INIT_deployrate)) / 100));  //-- INIT상태의 진행률 반영
        info_deployrate = Math.min(info_deployrate, 100);
      }
      
      //-- 정보저장 {
      api_item['deployresult'] = deployresult;
      api_item_info['message'] = info_message;
      api_item_info['deployrate'] = info_deployrate;
      api_item_info['result'] = info_result;
      api_item_info['css_result'] = info_css_result;
      api_item_info['css_pgbar'] = info_css_pgbar;
      api_item['deploy_call_cnt'] = deploy_call_cnt;
      api_item['deploystatus_call_cnt'] = deploystatus_call_cnt;

      //--@@this.m_arr_deployData[idx] = api_item;  //-- maybe not update ui
      this.m_arr_deployData = this.m_arr_deployData.slice();  //-- array copy for update

      this.$console_log('o-o', 'fn_update_api_deploy_info()', 'updated', 'api_item: ', api_item);
      //-- 정보저장 }
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
      <span class="popup_header_title">배포하기</span>
      <button type="button" class="popup_header_button" role="button" :disabled="ui_ctrl_btn_ok_disabled" @click.stop="fn_dialog_close()" title="Close">
        <span class="button_close">Close</span>
      </button>
    </div>
    <div class="pop_ver pop_ver2">
      <div class="popup_content distribution_cont">
        <div class="btn_set-right mt20">
          <p class="list_count">전체: <span>{{m_arr_deployData.length}}</span> 건</p>
        </div>
        <div class="scroll_wrap">

          <div class="pkg_board">
            <table class="table-vw">
              <caption>배포하기 Table</caption>
              <colgroup>
                <col style="width:10%;">
                <col style="width:35%;">
                <col style="">
                <col style="width:15%;">
              </colgroup>
              <thead>
                <tr>
                  <th>No.</th>
                  <th>API</th>
                  <th>배포 진행</th>
                  <th>결과</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(deploy_item, index) in m_arr_deployData" :key="index">
                  <td>{{computed_api_item_info(deploy_item, 'idx')}}</td>
                  <td>{{computed_api_item_info(deploy_item, 'api_nm')}}</td>
                  <td>
                    <div :class="['pgbar_ui', computed_api_item_info(deploy_item, 'css_pgbar')]">
                      <div class="progress_msg">{{computed_api_item_info(deploy_item, 'message')}}</div>
                      <div class="progress_percent">{{computed_api_item_info(deploy_item, 'deployrate')}}%</div>
                      <div class="pgbar" :style="('width: ' + computed_api_item_info(deploy_item, 'deployrate') + '%')"></div>
                    </div>
                  </td>
                  <td :class="computed_api_item_info(deploy_item, 'css_result')">{{computed_api_item_info(deploy_item, 'result')}}</td>
                </tr>
              </tbody>
            </table><!-- .table-vw -->
          </div><!-- .pkg_board -->

        </div><!-- .scroll_wrap -->

        <p class="causion_msg">* 주의 : 배포 중에는 페이지 이동, 브라우저 종료 및 시스템을 종료하지 마시기 바랍니다.</p>
        <div class="lPop_bottom brd_tp">
          <!-- 
                      배포 미완료시 disabled class가 추가되고 클릭 event를 삭제,
                      배포 완료시 disabled class remove후 클릭 event를 추가해주세요. 
                      해당 부분의 이벤트를 막는 css가 있지만 크롬만 적용이 되서 사용하지 못합니다.
          -->
          <button type="button" :class="['btn', 'btn_black', 'btn_popup_close', (ui_ctrl_btn_ok_disabled ? 'disabled' : '')]" :disabled="ui_ctrl_btn_ok_disabled" @click.stop="fn_dialog_close()" title="닫기">닫기</button>
        </div><!-- .lPop_bottom -->
      </div><!-- .popup_content -->
    </div><!--.pop_ver pop_ver2 -->
  </div><!-- .popup_wrap -->
</template>

<style scoped>
  /*add for modal dialog*/
  .pkg_board .table-vw {text-align:center}

  /*add for local*/
  .distribution_cont {height: calc(100% - 20px - 95px);}/*100% - padding - 버튼area*/

  /*add by puble*/
  .pkg_board .table-vw td .pgbar_ui{margin:5px 10px;}
  .pgbar_ui{height:18px;background-color:#d0c5af;text-align:right;position:relative;border-radius:30px;background-color:#f6f6f6;border:1px solid #ddd;overflow:hidden;}
  .pgbar_ui .pgbar{height:18px;background-color:#9ad5f4;display:block;border-radius:25px;}
  .pgbar_ui em{position:relative;padding-left:10px;height:16px;line-height:16px;display:inline-block;color:#9e9e9e;z-index:2;font-size:14px;}
  
  .progress_msg{position:absolute;left:0;top:1px;width:100%;font-size:11px;color:#333;text-align:center;}
  .progress_percent{position:absolute;right:0;top:1px;width:40px;text-align:center;font-size:8px;color:#333;}
  
  .failStatus .pgbar {background-color:#f6acaf;}
  .causion_msg{padding:10px 0;margin-bottom:10px;color:#c93137;font-size:13px;}
  
  .btn.disabled{color:rgba(255,255,255,0.5) !important;}
</style>

<style scoped>
  /*popup_wrap*/
  /*[tag:adpt][drm][chg][cmt org dum-]*/
  .dum-popup_box {z-index: 900; position: fixed; left: 0; top: 0; right: 0; bottom: 0; background-color: rgba(0,0,0,0.75);}
  .dum-popup_wrap {/*position: absolute; height: 445px; width: 750px;*/ z-index: 10050; left: 50%; top: 50%; transform: translate(-50%,-50%);}
  .dum-pop_ver2 {height: 352px; background: #fff; border-left: 3px solid #333; border-right: 3px solid #333; border-bottom: 3px solid #333; padding: 20px 20px 15px;}
  
  /*t, l, w, h provide by modal dialog*/
  .dum .popup_wrap {z-index: 901; height: 100%; width: 100%; overflow: hidden;}
  .dum .popup_header {background: #333; height: 55px; width: calc(100% - 40px); line-height: 55px;padding: 0 20px;}
  .dum .popup_header_title {color:#fff; font-size: 18px;}
  .dum .popup_header_button {float: right;}
  .dum .button_close {display: inline-block; width: 23px; height: 23px; text-indent: -9999px; background: url(/apidev/resources/images/common/icon/ico_closeB.png) no-repeat; margin-top: 15px;}
  .dum .pop_ver2 {height: calc(100% - 55px - 40px + 2px); background: #fff; border-left: 3px solid #333; border-right: 3px solid #333; border-bottom: 3px solid #333; padding: 20px 20px 15px;}
  .dum .pop_ver2 .scroll_wrap {height: 100%; overflow: auto;}
  .dum .pop_ver2 .lPop_bottom {text-align: center;}
</style>

<style scoped>
  /*resigtration.css를 사용하는 page에는 layout.css가 없기때문에 추가되었고 layout.css사용 페이지에서는 필요없음*/
  /*[tag:adpt][drm][add][from layout.css] { */
  .dummy .btn{height:40px;line-height:40px;min-width:160px;text-align:center;color:#333;font-size:15px;border:1px solid #333;font-weight:500; border-radius: 5px;}

  /*   board_view / write */
  .dummy .pkg_board .table-vw {border-top:2px solid #333;}
  .dummy .pkg_board .table-vw ~ .table-vw {margin-top:20px;}
  
  .dummy .pkg_board .table-vw tbody th{height:59px;font-size:15px;color:#333;background:#f7f7f7;font-weight:700;border-bottom:1px solid #ccc;}
  .dummy .pkg_board .table-vw tbody td{padding:5px 10px;font-size:13px;color:#757575;position:relative;border-bottom:1px solid #ccc;}
  .dummy .pkg_board .table-vw tbody td .txtarea_wrap{min-height:200px;line-height:20px;white-space:pre-line;}
  .dummy .pkg_board .table-vw tbody td .txtarea_wrap textarea{min-height:320px;resize:vertical;}
  .dummy .pkg_board .table-vw tbody td .txtarea_wrap.txtarea-view{min-height:auto;padding:30px 10px;}
  .dummy .popup_content .pkg_board .table-vw {min-width:300px;}
  
  /*distribution_cont*/
  .dummy .distribution_cont {height: calc(100% - 95px - 20px);}/*- 하단button - padding*/
  .dummy .distribution_cont .pkg_board .table-vw thead th {border-right: 1px solid #ccc; height: 20px; font-size: 14px; color: #333; background: #f7f7f7; font-weight: 700; border-bottom: 1px solid #ccc; padding:10px 14px;}
  .dummy .distribution_cont .pkg_board .table-vw thead th:last-child { border-right: none; }
  .dummy .distribution_cont .pkg_board .table-vw tbody td {border-right: 1px solid #ccc; text-align: center; height: 20px; font-size: 13px;}
  .dummy .distribution_cont .pkg_board .table-vw tbody td:last-child { border-right: none; }
  /*[tag:adpt][drm][add][from layout.css] } */
</style>
