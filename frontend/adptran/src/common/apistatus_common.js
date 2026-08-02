import KsmUtil from '@/common/ksmutil.js';
import Environment from '@/app/environment.js';

const ApistatusCommonMixin = {
  data: () => ({
    //-- @constant {
    //-- [실행설정]
    //-- use_static_data: api호출결과를  static_data를 사용
    //-- use_demo_table: _DEMO_ prefix table을 사용하여 검색
    //--[drm][test][ing]@@
    m_con_def_api_param: {
      'dev_opt': '#use_static_data;#use_demo_table',
    },
    
    //-- apistatus api url
    m_con_apistatus_group_api_status_spc_group_list_url: '/group/api_status_spc_group_list',
    m_con_apistatus_group_group_summary_list_url: '/group/group_summary_list',
    m_con_apistatus_group_group_api_status_info_url: '/group/group_api_status_info',
    m_con_apistatus_group_tran_status_spc_group_user_link_url: '/group/tran_status_spc_group_user_link',
    m_con_apistatus_list_api_status_info_list_url: '/list/api_status_info_list',
    m_con_apistatus_list_api_status_info_daily_list_url: '/list/api_status_info_daily_list',
    m_con_apistatus_list_api_status_check_hist_list_url: '/list/api_status_check_hist_list',
    m_con_apistatus_view_api_status_check_hist_url: '/view/api_status_check_hist',

    m_con_apistatus_common_api_system_spc_list_url: '/common/api_system_spc_list',
    m_con_apistatus_common_api_status_spc_group_list_url: '/common/api_status_spc_group_list',
    
    m_con_gw_profile_lamplog: 'PROD', //-- 'PROD', 'TB'
    //-- @constant }
  }),
  methods: {
    $getApistatusApiUrl() { return Environment.apistatusApiUrl; },
    // -- apistatus api 호출
    $fn_call_apistatus_api(api_path, api_request, method, fn_callback) {
      this.$console_log('watch', '$fn_call_apistatus_api.', 'api_path: ', api_path, 'api_request: ', api_request, 'method: ', method, 'fn_callback: ', fn_callback);
      
      fn_callback = ((typeof(fn_callback) == 'function') ? fn_callback : (new Function()));

      let success = ((response) => {
        this.$console_log('watch', '$fn_call_apistatus_api().', 'success', 'response: ', response);

        let result = response['data'];

        let isSuccess = (200 == result['resultCode']);  //-- success
        if (true == isSuccess) {
          fn_callback('ok', response, api_request, result['data']);
        }
        else {
          this.$console_log('watch', '$fn_call_apistatus_api().', '$isApiSuccess() == false', 'response: ', response);
          fn_callback('nk', response, api_request);
        }
      });
      let failed = ((response) => {
        this.$console_log('watch', '$fn_call_apistatus_api().', 'failed', 'response: ', response);
        fn_callback('failed', response, api_request);
      });
      let catched = ((error) => {
        this.$console_log('watch', '$fn_call_apistatus_api().', 'catched', 'error: ', error);
        fn_callback('catched', error, api_request);
      });

      let o_option = (('get' == method) ? {'method':'get'} : {});
      this.$getHttpResponse(this.$getApistatusApiUrl() + api_path, method, api_request, o_option).then(success, failed).catch(error => catched(error));
    },
  } // -- methods:
}; // -- const ApistatusCommonMixin{}

export {
  ApistatusCommonMixin,
};
