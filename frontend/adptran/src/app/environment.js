/*
 * environment.js
 */
const environment_prod = {
  resourcesBase: '/resources/adptran',

  //-- 공통영역 {
  defineUrl: {
    serviceHome: '/apidev/main/index.do',
    login: '/apidev/login/loginForm.do',
    mvApiPathReg: '/apidev/api/reg/mvApiPathReg.do?apiSpcNo=#apiSpcNo#',  //--[ref]
  },

  fn_get_pageinfo: function(key) {
    let s_ret = '';
    try {
      let adpt_pageinfo = (JSON.parse(localStorage.getItem('adpt_pageinfo')) || {});
      s_ret = (adpt_pageinfo[key] || '');
    }
    catch(e) {
      console.log('environment.js > ', 'fn_get_pageinfo() > ', '[e: ', e, ']', '[key: ', key, ']');
    }
    return s_ret;
  },
  fn_set_pageinfo: function(key, value) {
    if ((key||'').length == 0) { return false; }
    try {
      let adpt_pageinfo = (JSON.parse(localStorage.getItem('adpt_pageinfo')) || {});
      let obj = {};
      obj[key] = value;
      adpt_pageinfo = Object.assign(adpt_pageinfo, obj);
      localStorage.setItem('adpt_pageinfo', JSON.stringify(adpt_pageinfo));
    }
    catch(e) {
      console.log('environment.js > ', 'fn_set_pageinfo() > ', '[e: ', e, ']', '[key: ', key, ']', '[value: ', value, ']');
    }
    return true;
  },
  //-- 공통영역 }
};

const environment_dev = {
};

let environment = environment_prod;

//-- condition modified {
const config_runmode = environment.fn_get_pageinfo('config_runmode');
const request_uri = environment.fn_get_pageinfo('request_uri');
const adptran_api_url = environment.fn_get_pageinfo('adptran_api_url');
if ('dev' == config_runmode) {
  Object.assign(environment, environment_dev);
}

environment.adptranApiUrl = (adptran_api_url||'') + '/adptran_api/v1';
environment.apistatusApiUrl = (adptran_api_url||'') + '/apistatus_api/v1';

const is_eg_condition = (request_uri.indexOf("/__eg_condition__/") != -1);
if (true === is_eg_condition) {
  /*-- exception setting --*/
  environment.__key__ = '__value__';
}
//-- condition modified }

const Environment = Object.freeze(environment);
export default Environment;
