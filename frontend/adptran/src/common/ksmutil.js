const KsmUtil = {
  $console_log(cmd, id) {
    if (typeof(console) != 'object') { return; }
    //-- [dev]if ((arguments[0]||'').indexOf('[dbg]') == -1) { return; }
    id = (id||'');
    try {
      let prefix = ((cmd == 'trace') ? '##' : '');
      prefix = ((cmd == 'watch') ? '@@' : prefix);
      prefix = ((cmd == 'debug') ? '[debug]>>>>>' : prefix);
      prefix = ((cmd == 'o-o') ? '(o-o)? =>' : prefix);
      prefix = ((cmd == 'warn') ? '[warn]>>>>>' : prefix);
      prefix = ((cmd == 'error') ? '[error]>>>>>' : prefix);
      let slice_num = 0 
      if (prefix.length > 0) {
        slice_num = 1;
        if ('string' == typeof(id)) {
          prefix += '[' + id + ']';
          slice_num = 2;
        }
      }
      let color = '';
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
  }, //-- console_log()
  //-- safe string, object
  $sf_str(str, def) { str = (((str == (void 0)) || (typeof(str) == 'object')) ? (def||'') : str); return str.toString(); },
  $sf_obj(obj, def) { obj = (obj||(def||{})); return ((typeof(obj) == 'object') ? obj : {}); },
  $sf_arr(arr, def) { arr = (arr||(def||[])); return (Array.isArray(arr) ? arr : []); },
  $sf_int(num, def) { num = parseInt(num, 10); return (isNaN(num) ? def : num); },
  $sf_func_call(func) {
    var args = Array.prototype.slice.call(arguments, 1);
    if ('function' == typeof(func)) { return func.apply(null, a_arg); }
    if ('function' != typeof(window[func])) { this.$console_log('error', '$sf_func_call()', 'not found function', func); return; }
    return window[func].apply(null, args);
  },
  /*--[dep][chg]
  $sf_func_call(func, ...a_arg) {
    if (typeof(func) == 'string') { //-- string일시 eval처리
      try { func = eval(func); }
      catch(e) { this.$console_log('error', '$sf_func_call()', 'e: ', e); }
    }
    if (typeof(func) == 'function') { return func.apply(null, a_arg); } return '#N/A#';
    //--##if (typeof(func) == 'function') { return { 'return': func.apply(null, a_arg) }; } return false;
  },
  --*/
  $obj2str(obj) {
    //--@@ let s = '{\n'; for (let p in o) s += ('    "' + p + '": "' + o[p] + '"\n'); return (s + '}');
    return this.$sf_json_stringify(obj);
  },
  $has_own(obj, key) { return ((obj != null) && Object.prototype.hasOwnProperty.call(obj, key)); },
  $has_val(obj, key) { return (this.$has_own(obj, key) && (obj[key] !== null) && ((typeof(obj[key]) =='object') || (this.$sf_str(obj[key]).length > 0))); },
  $sf_obj_val(obj, key, def) { return (this.$has_val(obj, key) ? obj[key] : (def||'')); },
  $obj_clone(obj) {
    if (obj === null || typeof(obj) !== 'object') { return obj; }
    let copy = obj.constructor();
    for (let attr in obj) { if (obj.hasOwnProperty(attr)) { copy[attr] = this.$obj_clone(obj[attr]); } }
    return copy;
  },
  $is_json_str(text) {
    try { let o = JSON.parse(text); return (typeof(o) == 'object'); } catch (e) { return false; }
  },
  $is_json_obj(value) {
    value = ((null === value) ? '' : value);  //-- typeof(null) == 'object'
    if (typeof(value) != 'object') return false;
    try { let s = JSON.stringify(value); return (typeof(s) == 'string'); } catch (e) { return false; }
  },
  $sf_json_parse(text, reviver) {
    let o_json = null;
    try { o_json = JSON.parse(text, reviver); }
    catch(e) { this.$console_log('error', '$sf_json_parse()', 'e: ', e, 'text: ', text, 'reviver: ', reviver); }
    return o_json;
  },
  $sf_json_stringify(value, replacer, space) {
    let s_json = '';
    if (false == $is_json_obj(value)) { return s_json; }
    try { s_json = JSON.stringify(value, replacer, space); }
    catch(e) { this.$console_log('error', '$sf_json_stringify()', 'e: ', e, 'value: ', value, 'replacer: ', replacer, 'space: ', space); };
    return s_json;
  },
  $sf_html(p_val) {
    return (p_val||'').replace(/[&<>"'`=\/]/g, (function(char) { return ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;', '/': '&#x2F;', '`': '&#x60;', '=': '&#x3D;' }[char] || p_val); }));
  },
  $sf_filename(p_val) {
    return (p_val||'').replace(/[\\/:"*?<>|]/g, '');
  },
  //-- full url check
  $is_full_url(p_val) {
    //-- url의 유효여부 // http(s):uname:pass@//domain.com:1234/xxx check
    return /^(http[s]?)\:\/\/(\w+:{0,1}\w*@)?([\w-]+(\.[\w-]+)+)+(:([0-9]+))?(\/|\/([\w#!:.?+=&%@!\-\/])+)$/.test(p_val);
  },
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
  $get_full_url_token(url, cmd) {
    let s_ret = '';
    let regex = /^(http[s]?)\:\/\/(\w+:{0,1}\w*@)?([\w-]+(\.[\w-]+)+)+(:([0-9]+))?(\/|\/([\w#!:.?+=&%@!\-\/])+)$/;
    let a_match = url.match(regex);
    if (a_match != null) {
      if ('protocol' == cmd) { s_ret = a_match[1]; }
      else if ('auth' == cmd) { s_ret = a_match[2]; }
      else if ('domain' == cmd) { s_ret = a_match[3]; }
      else if ('port' == cmd) { s_ret = a_match[6]; }
      else if ('path' == cmd) { s_ret = a_match[7]; }
      s_ret = $sf_str(s_ret);
    }
    return s_ret;
  },
  //-- uuid
  $randomUUID() {
    let s4 = function() { return Math.floor((1 + Math.random()) * 0x10000).toString(16).substring(1); }
    return 'ss-s-s-s-sss'.replace(/s/g, s4);
  },
  //-- window location이동
  $fn_go_location(p_url, p_o_param) {
    p_url = (p_url||'');
    p_o_param = (p_o_param||{});
    
    let s_method = (p_o_param['method']||'');
    let s_target = (p_o_param['target']||''); 
    let o_param = (p_o_param['param']||{});
    if (p_url.length == 0) { return; }
    if ($.isEmptyObject(o_param) == false) {
      let s_frm_name = 'frm_go_location';
      let s_frm_id = ('id_' + s_frm_name);
      let jq_frm = $('#' + s_frm_id);
      if (jq_frm.length == 0) { jq_frm = $('<form id="' + s_frm_id + '" name="' + s_frm_name + '"></form>').hide().prependTo('body'); }
      jq_frm.empty();
      $.each(o_param, function(p_key, p_value) { jq_frm.append($('<input type="hidden" name="' + p_key + '">').val(p_value)); });
      s_method = ((s_method.toLowerCase() == 'get') ? 'get' : 'post');
      let o_attr = {'action': p_url, 'method': s_method};
      if (s_target.length > 0) {
        o_attr['target'] = s_target; 
      }
      jq_frm.attr(o_attr).submit().empty();
    }
    else {
      if (s_target.length > 0) {
        let jq_a = $('<a>', { href: p_url, target: s_target });
        if (jq_a[0].click) { $('body').append(jq_a); jq_a[0].click(); jq_a.remove(); }
        else if (jq_a[0].dispatchEvent) { let e = document.createEvent('MouseEvents'); e.initEvent('click', true, true); jq_a[0].dispatchEvent(e); }
        else { window.open(p_url, s_target); }
      }
      else { window.location.href = p_url; }
    }
  },
  // -- util {
  $is_empty(p_val) {
    return !(/\S+/.test(p_val||''));
  },
  $is_positive_number(p_val) {
    return (parseInt(p_val, 10) > 0);
  },
  $is_integer(p_val) {
    return ((new Number(p_val)).valueOf() === parseInt(p_val, 10));
  },
  $is_number(p_val) {
    //--return (+p_val === p_val);
    return (!isNaN(p_val));
  },
  $is_boolean(p_val) {
    return ((!!p_val === p_val) || ((p_val||'').toLowerCase() == 'true') || ((p_val||'').toLowerCase() == 'false'));
  },
  $is_phoneno(p_val) {
    return /^[0-9]{10,11}$/.test(p_val);
  },
  $is_alphanumeric(p_val) {
    return /^[A-Za-z0-9]+$/.test(p_val);
  },
  $is_alphanumeric_korean(p_val) {
    return /^[A-Za-z0-9가-힣]+$/.test(p_val);
  },
  $is_domain(p_val) {
    return /\b((?=[a-z0-9-]{1,63}\.)(xn--)?[a-z0-9]+(-[a-z0-9]+)*\.)+[a-z]{2,63}\b/.test(p_val);
  },
  $is_ip4(p_val) {
    return /^(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/.test(p_val);
  },
  // -- util }
}; //-- const KsmUtil

export default KsmUtil;
