//--##const KsmUtil = {
//--##}; //-- const KsmUtil
//--##export default KsmUtil;
  function $console_log(cmd, id) {
    if (typeof(console) != 'object') { return; }
    //-- [dev]if ((arguments[0]||'').indexOf('[dbg]') == -1) { return; }
    id = (id||'');
    try {
      var prefix = ((cmd == 'trace') ? '##' : '');
      prefix = ((cmd == 'watch') ? '@@' : prefix);
      prefix = ((cmd == 'debug') ? '[debug]>>>>>' : prefix);
      prefix = ((cmd == 'o-o') ? '(o-o)? =>' : prefix);
      prefix = ((cmd == 'warn') ? '[warn]>>>>>' : prefix);
      prefix = ((cmd == 'error') ? '[error]>>>>>' : prefix);
      var slice_num = 0 
      if (prefix.length > 0) {
        slice_num = 1;
        if ('string' == typeof(id)) {
          prefix += '[' + id + ']';
          slice_num = 2;
        }
      }
      var color = '';
      color = ((cmd == 'trace') ? 'color:darkgrey;' : color);
      color = ((cmd == 'watch') ? 'color:blue;' : color);
      color = ((cmd == 'debug') ? 'background:#dcf5a0;' : color);
      color = ((cmd == 'o-o') ? 'background:#dcf5a0;font-weight: bold;' : color);
      color = ((cmd == 'warn') ? 'background:yellow;font-weight: bold;' : color);
      color = ((cmd == 'error') ? 'background:yellow;color:red;font-weight: bold;' : color);
      //-- [2023:codeeyes][console.log issue]
      console['log']('%c' + prefix, color, ...Array.prototype.slice.call(arguments, slice_num));
    }
    catch(e) {  
      //-- [2023:codeeyes][console.log issue]
      console['log']('[catch:e]', e);
    }
  } //-- console_log()
  //-- safe string, object
  function $sf_str(str, def) { str = (((str == (void 0)) || (typeof(str) == 'object')) ? (def||'') : str); return str.toString(); }
  function $sf_obj(obj, def) { obj = (obj||(def||{})); return ((typeof(obj) == 'object') ? obj : {}); }
  function $sf_arr(arr, def) { arr = (arr||(def||[])); return (Array.isArray(arr) ? arr : []); }
  function $sf_int(num, def) { num = parseInt(num, 10); return (isNaN(num) ? def : num); }
  function $sf_func_call(func) {
    var args = Array.prototype.slice.call(arguments, 1);
    if ('function' == typeof(func)) { return func.apply(null, a_arg); }
    if ('function' != typeof(window[func])) { $console_log('error', '$sf_func_call()', 'not found function', func); return; }
    return window[func].apply(null, args);
  }
  /*--[dep][chg]
  function $sf_func_call(func, ...a_arg) { 
    if (typeof(func) == 'string') { //-- string일시 eval처리
      try { func = eval(func); }
      catch(e) { $console_log('error', '$sf_func_call()', 'e: ', e); }
    }
    if (typeof(func) == 'function') { return func.apply(null, a_arg); } return '#N/A#';
    //--##if (typeof(func) == 'function') { return { 'return': func.apply(null, a_arg) }; } return false;
  }
  --*/
  function $obj2str(obj) {
    //--@@ var s = '{\n'; for (var p in o) s += ('    "' + p + '": "' + o[p] + '"\n'); return (s + '}');
    return $sf_json_stringify(obj);
  }
  function $has_own(obj, key) { return ((obj != null) && (typeof(obj) == 'object') && ((key in obj) || Object.prototype.hasOwnProperty.call(obj, key))); }
  function $has_val(obj, key) { return ($has_own(obj, key) && (obj[key] !== null) && ((typeof(obj[key]) =='object') || ($sf_str(obj[key]).length > 0))); }
  function $sf_obj_val(obj, key, def) { return ($has_val(obj, key) ? obj[key] : (def||'')); }
  function $obj_clone(obj) {
    if (obj === null || typeof(obj) !== 'object') { return obj; }
    var copy = obj.constructor();
    for (var attr in obj) { if (obj.hasOwnProperty(attr)) { copy[attr] = $obj_clone(obj[attr]); } }
    return copy;
  }
  function $is_json_str(text) {
    try { var o = JSON.parse(text); return ((null !== o) && (typeof(o) == 'object')); } catch (e) { return false; }
  }
  function $is_json_obj(value) {
    value = ((null === value) ? '' : value);  //-- typeof(null) == 'object'
    if (typeof(value) != 'object') return false;
    try { var s = JSON.stringify(value); return (typeof(s) == 'string'); } catch (e) { return false; }
  }
  function $sf_json_parse(text, reviver) {
    var o_json = null;
    try { o_json = JSON.parse(text, reviver); }
    catch(e) { $console_log('error', '$sf_json_parse()', 'e: ', e, 'text: ', text, 'reviver: ', reviver); }
    return o_json;
  }
  function $sf_json_stringify(value, replacer, space) {
    var s_json = '';
    if (false == $is_json_obj(value)) { return s_json; }
    try { s_json = JSON.stringify(value, replacer, space); }
    catch(e) {
      $console_log('error', '$sf_json_stringify()', 'e: ', e, 'value: ', value, 'replacer: ', replacer, 'space: ', space);
    };
    return s_json;
  }
  function $sf_html(p_val) {
    //-- [20210213][chg]
    //--##return String(p_val).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
    //--##return String(p_val).replace(/[&<>"'`=\/]/g, function fromEntityMap (s) { return { '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;', '/': '&#x2F;', '`': '&#x60;', '=': '&#x3D;' }[s]; });
    return (p_val||'').replace(/[&<>"'`=\/]/g, (function(char) { return ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;', '/': '&#x2F;', '`': '&#x60;', '=': '&#x3D;' }[char] || p_val); }));
  }

  //-- full url check
  function $is_full_url(p_val) {
    //-- url의 유효여부 // http(s):uname:pass@//domain.com:1234/xxx check
    return /^(http[s]?)\:\/\/(\w+:{0,1}\w*@)?([\w-]+(\.[\w-]+)+)+(:([0-9]+))?(\/|\/([\w#!:.?+=&%@!\-\/])+)$/.test(p_val);
  }
  //-- url의 부분 group
  //-- e.g. https://user:pass@www.abc.com:1234/dir/file#123
  //-- group 0: https://www.abc.com:1234/dir/file#123
  //-- group 1: https
  //-- group 2: user:pass@
  //-- group 3: www.abc.com
  //-- group 4: .com
  //-- group 5: :1234
  //-- group 6: 1234
  //-- group 7: /dir/file#123
  //-- group 8: 3
  function $get_full_url_token(url, cmd) {
    var s_ret = '';
    var regex = /^(http[s]?)\:\/\/(\w+:{0,1}\w*@)?([\w-]+(\.[\w-]+)+)+(:([0-9]+))?(\/|\/([\w#!:.?+=&%@!\-\/])+)$/;
    var a_match = url.match(regex);
    if (a_match != null) {
      if ('protocol' == cmd) { s_ret = a_match[1]; }
      else if ('auth' == cmd) { s_ret = a_match[2]; }
      else if ('domain' == cmd) { s_ret = a_match[3]; }
      else if ('port' == cmd) { s_ret = a_match[6]; }
      else if ('path' == cmd) { s_ret = a_match[7]; }
      s_ret = $sf_str(s_ret);
    }
    return s_ret;
  }
  function $fn_parseUrl(url) {
    var m = url.match(/^(([^:\/?#]+:)?(?:\/\/((?:([^\/?#:]*):([^\/?#:]*)@)?([^\/?#:]*)(?::([^\/?#:]*))?)))?([^?#]*)(\?[^#]*)?(#.*)?$/);
    var r = {
      hash: m[10] || '',                   // #asd
      host: m[3] || '',                    // localhost:257
      hostname: m[6] || '',                // localhost
      href: m[0] || '',                    // http://username:password@localhost:257/deploy/?asd=asd#asd
      origin: m[1] || '',                  // http://username:password@localhost:257
      pathname: m[8] || (m[1] ? '/' : ''), // /deploy/
      port: m[7] || '',                    // 257
      protocol: m[2] || '',                // http:
      search: m[9] || '',                  // ?asd=asd
      username: m[4] || '',                // username
      password: m[5] || ''                 // password
    };
    if (r.protocol.length == 2) {
      r.protocol = 'file:///' + r.protocol.toUpperCase();
      r.origin = r.protocol + '//' + r.host;
    }
    r.href = r.origin + r.pathname + r.search + r.hash;
    return (m && r);
  }
  //-- uuid
  function $randomUUID() {
    var s4 = function() { return Math.floor((1 + Math.random()) * 0x10000).toString(16).substring(1); }
    return 'ss-s-s-s-sss'.replace(/s/g, s4);
  }
  //-- window location이동
  function $fn_go_location(p_url, p_o_param) {
    p_url = (p_url||'');
    p_o_param = (p_o_param||{});
    
    var s_method = (p_o_param['method']||'');
    var s_target = (p_o_param['target']||''); 
    var o_param = (p_o_param['param']||{});
    if (p_url.length == 0) { return; }
    if ($.isEmptyObject(o_param) == false) {
      var s_frm_name = 'frm_go_location';
      var s_frm_id = ('id_' + s_frm_name);
      var jq_frm = $('#' + s_frm_id);
      if (jq_frm.length == 0) { jq_frm = $('<form id="' + s_frm_id + '" name="' + s_frm_name + '"></form>').hide().prependTo('body'); }
      jq_frm.empty();
      $.each(o_param, function(p_key, p_value) { jq_frm.append($('<input type="hidden" name="' + p_key + '">').val(p_value)); });
      s_method = ((s_method.toLowerCase() == 'get') ? 'get' : 'post');
      var o_attr = {'action': p_url, 'method': s_method};
      if (s_target.length > 0) {
        o_attr['target'] = s_target; 
      }
      jq_frm.attr(o_attr).submit().empty();
    }
    else {
      if (s_target.length > 0) {
        var jq_a = $('<a>', { href: p_url, target: s_target });
        if (jq_a[0].click) { $('body').append(jq_a); jq_a[0].click(); jq_a.remove(); }
        else if (jq_a[0].dispatchEvent) { var e = document.createEvent('MouseEvents'); e.initEvent('click', true, true); jq_a[0].dispatchEvent(e); }
        else { window.open(p_url, s_target); }
      }
      else { window.location.href = p_url; }
    }
  }
  //-- util {
  function $is_empty(p_val) {
    return !(/\S+/.test(p_val||''));
  }
  function $is_positive_number(p_val) {
    return (parseInt(p_val, 10) > 0);
  }
  function $is_integer(p_val) {
    return ((new Number(p_val)).valueOf() === parseInt(p_val, 10));
  }
  function $is_number(p_val) {
    //--return (+p_val === p_val);
    return (!isNaN(p_val));
  }
  function $is_boolean(p_val) {
    return ((!!p_val === p_val) || ((p_val||'').toLowerCase() == 'true') || ((p_val||'').toLowerCase() == 'false'));
  }
  function $is_phoneno(p_val) {
    return /^[0-9]{10,11}$/.test(p_val);
  }
  function $is_alphanumeric(p_val) {
    return /^[A-Za-z0-9]+$/.test(p_val);
  }
  function $is_alphanumeric_korean(p_val) {
    return /^[A-Za-z0-9가-힣]+$/.test(p_val);
  }
  function $is_domain(p_val) {
    return /\b((?=[a-z0-9-]{1,63}\.)(xn--)?[a-z0-9]+(-[a-z0-9]+)*\.)+[a-z]{2,63}\b/.test(p_val);
  }
  function $is_ip4(p_val) {
    return /^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/.test(p_val);
  }
  //-- util }

  //-- util_ext {
  var $fn_localStorage = {
    get: function(key) { return $sf_str(localStorage.getItem(key)); },
    set: function(key, value) { localStorage.setItem(key, value); }
  }

  function $fn_fmt_date(p_cmd, p_date, p_gub) {
    var d_yyyy = p_date.getFullYear().toString();
    var d_mm = ('0' + (p_date.getMonth() + 1)).substr(-2);
    var d_dd = ('0' + p_date.getDate()).substr(-2);
    var t_hh = ('0' + p_date.getHours()).substr(-2);
    var t_mm = ('0' + p_date.getMinutes()).substr(-2);
    var t_ss = ('0' + p_date.getSeconds()).substr(-2);

    var s_ret = d_yyyy + '-' + d_mm + '-' + d_dd + ' ' + t_hh + ':' + t_mm + ':' + t_ss;
    p_gub = $sf_str(p_gub);
    if ('fmt_ymdhms' == p_cmd) { s_ret =  d_yyyy + d_mm + d_dd + t_hh + t_mm + t_ss; }
    else if ('fmt_ymd' == p_cmd) { s_ret =  d_yyyy + p_gub + d_mm + p_gub + d_dd; }
    else if ('fmt_hms' == p_cmd) { s_ret =  t_hh + p_gub + t_mm + p_gub + t_ss; }
    else if ('fmt_ymdhms_kor' == p_cmd) {  //-- 2020년 07월 21일 12시12분12초
      s_ret =  d_yyyy + '년 ' + d_mm + '월 ' + d_dd + '일 ' + t_hh + '시 ' + t_mm + '분 ' + t_ss + '초';  
    }
    return s_ret;
  }
  // -- util_ext }
  // -- util-properties {
  //-- exp_prop에서 특정 property query
  //-- a_prop_key = [] 일시 모든 property get
  function $fn_get_ext_prop(a_prop_key, ext_prop_val) {
    var o_ext_prop = {};
    var a_key = [];
    $sf_arr(a_key).forEach(function(key) {
      key = $sf_str(key).toLowerCase().trim();
      if ((key.length > 0) && (a_key.indexOf(key) == -1)) {
        a_key.push(key);
      }
    });
    ext_prop_val.split('\n').forEach(function(prop) {
      var a_prop = $sf_str(prop).trim().split('=');
      if (a_prop.length > 1) {
        var key = $sf_str(a_prop[0]).toLowerCase().trim();
        var val = $sf_str(a_prop.slice(1).join('=')).trim();
        if ((key.length > 0) && ((a_key.length == 0) || (a_key.indexOf(key) != -1))) {
          o_ext_prop[key] = val;
        }
      }
    });
    return o_ext_prop;
  }
  //-- exp_prop값 설정
  //-- prop_key: CUD property key
  //-- prop_val: propertiy value (null일시 삭제처리), 
  function $fn_set_ext_prop(prop_key, prop_val, ext_prop_val) {
    var prop_key = $sf_str(prop_key).toLowerCase().trim();
    if (prop_key.length == 0) { return ext_prop_val; }
    var b_is_delete = (null == prop_val);
    var b_is_set = false;

    var a_ext_prop = [];
    ext_prop_val.split('\n').forEach(function(prop) {
      var a_prop = $sf_str(prop).trim().split('=');
      if (a_prop.length > 1) {
        var key = $sf_str(a_prop[0]).toLowerCase().trim();
        var val = $sf_str(a_prop.slice(1).join('=')).trim();
        if (key == prop_key) {
          if (false == b_is_delete) {
            a_ext_prop.push(key + '=' + $sf_str(prop_val));
            b_is_set = true;
          }
        }
        else {
          a_ext_prop.push(key + '=' + val);
        }
      }
    });
    if ((false == b_is_delete) && (false == b_is_set)) {
      a_ext_prop.push(prop_key + '=' + prop_val);
    }

    return a_ext_prop.join('\n');
  }
  // -- util-properties }
