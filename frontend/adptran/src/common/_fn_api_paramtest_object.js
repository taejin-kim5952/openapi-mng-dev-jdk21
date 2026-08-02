  //-- query select_API_PARAM_TEST_list rs  -> cpapirequest
  //-- api_paramtest_object {
import KsmUtil from '@/common/ksmutil.js';

const fn_api_paramtest_object = {
  //-- rs_param => udf_param mapping
  fn_get_udf_param_object: (function(rs_param) {
    return {
      'l': KsmUtil.$sf_obj_val(rs_param, 'LEVEL', ''),
      'c': KsmUtil.$sf_obj_val(rs_param, 'PARAM_NO', ''),
      'p': KsmUtil.$sf_obj_val(rs_param, 'PRNTS_PARAM_NO', ''),
      'n': KsmUtil.$sf_obj_val(rs_param, 'PARAM_NM', ''),
      'f': KsmUtil.$sf_obj_val(rs_param, 'PARAM_NM_FULL', ''),
      'd': KsmUtil.$sf_obj_val(rs_param, 'DATA_TYPE_CD_NM', '').toLowerCase(),
      'r': KsmUtil.$sf_obj_val(rs_param, 'REQUIRED', ''),
      'v': KsmUtil.$sf_obj_val(rs_param, 'EXAM', ''),
    }
  }),
  //-- udf_param => rs_param mapping
  fn_get_rs_param_object: (function(udf_param) {
    return {
      'LEVEL': KsmUtil.$sf_obj_val(udf_param, 'l', ''),
      'PARAM_NO': KsmUtil.$sf_obj_val(udf_param, 'c', ''),
      'PRNTS_PARAM_NO': KsmUtil.$sf_obj_val(udf_param, 'p', ''),
      'PARAM_NM': KsmUtil.$sf_obj_val(udf_param, 'n', ''),
      'PARAM_NM_FULL': KsmUtil.$sf_obj_val(udf_param, 'f', ''),
      'DATA_TYPE_CD_NM': KsmUtil.$sf_obj_val(udf_param, 'd', '').toLowerCase(),
      'REQUIRED': KsmUtil.$sf_obj_val(udf_param, 'r', '').toLowerCase(),
      'EXAM': KsmUtil.$sf_obj_val(udf_param, 'v', ''),
    }
  }),
  //-- udf_param에 testcase값을 assign
  fn_get_udf_param_assign_value: (function(a_udf_param, a_param_testcase) {
    let ret_a_udf_param = [];
    for (let n_ii = 0; n_ii < a_udf_param.length; n_ii++) {
      let udf_param = KsmUtil.$obj_clone(a_udf_param[n_ii]);
      let param_idx = a_param_testcase.findIndex(function(udf_param_testcase) { return (udf_param_testcase['f'] == udf_param['f']); });
      if (param_idx != -1) {
        udf_param['v'] = KsmUtil.$sf_str(a_param_testcase[param_idx]['v'], '');
      }
      ret_a_udf_param.push(udf_param);
    }
    return ret_a_udf_param;
  }),
  //-- datatype의 기본값을 구함
  fn_get_param_def_val: (function(datatypecdnm, def_val) {
    let o_ret;
    if ('object' == datatypecdnm) { o_ret = {}; }
    else if ('array' == datatypecdnm) { o_ret = []; }
    else { o_ret = def_val; }
    return o_ret;
  }),
  //--  param object에서 full paramnm의 node를 구함
  fn_get_param_node: (function(param_obj, paramnm, datatypecdnm_of_array) {
    let a_paramnm = paramnm.split('.');

    let obj = param_obj;
    for (let n_ii = 0; n_ii < a_paramnm.length; n_ii++) {
      let paramnm = a_paramnm[n_ii];
      if (KsmUtil.$has_own(obj, paramnm) == false) { 
        break;
      }
      let obj_child = obj[paramnm];
      if ((obj_child != null) && ('object' == typeof(obj_child))) {
        obj = obj_child;
        if (Array.isArray(obj_child) == true) {
          if (obj_child.length == 0) {
            let param_def_val = this.fn_get_param_def_val(datatypecdnm_of_array, null);
            if (param_def_val != null) {
              //-- 기본값이있는경우만 추가
              obj_child.push(param_def_val);
            }
          }
          if (obj_child.length > 0) { 
            obj = obj_child[0];
          }
        }
      }
    }
    return obj;
  }),
  //-- param test 정보로 param object를 구성
  fn_get_api_param_object: (function(a_udf_param, is_set_testdata) {
    let api_param_obj = {};
    
    for (let n_ii = 0; n_ii < a_udf_param.length; n_ii++) {
      let udf_param = a_udf_param[n_ii];
      let paramnm = udf_param['n'];
      let paramnmfull = udf_param['f'];
      let datatypecdnm = udf_param['d'];
      let def_val = (is_set_testdata ? udf_param['v'] : null);
      let param_def_val = this.fn_get_param_def_val(datatypecdnm, def_val);
      if (paramnmfull == paramnm) { //-- root node
        api_param_obj[paramnm] = param_def_val;
      }
      else {
        //-- [tag:arrayof]
        let datatypecdnm_of_array = datatypecdnm;
        if ('array' == datatypecdnm) {
          //-- [todo]
        }
        let param_node = this.fn_get_param_node(api_param_obj, paramnmfull, datatypecdnm_of_array);
        if ((param_node != null) && ('object' == typeof(param_node))) {
          if (Array.isArray(param_node) == true) {
            if (param_def_val != null) {
              param_node.push(param_def_val);
            }
          }
          else {
            param_node[paramnm] = param_def_val;
          }
        }
      }
    }
    return api_param_obj;
  }),
  //-- param object에 testcase value assign
  //-- cpapireq_param: cpapireq parameter object
  //-- a_param_testcase: query select_API_TESTCASE rs -> udf_param
  //-- a_udf_param: query select_API_PARAM_TEST_list rs -> udf_param (optional)
  fn_assign_testcase_value: (function(cpapireq_param, a_param_testcase, a_udf_param) {
    cpapireq_param = KsmUtil.$obj_clone(cpapireq_param);

    for (let n_ii = 0; n_ii < a_param_testcase.length; n_ii++) {
      let param_testcase = a_param_testcase[n_ii];
      let paramnmfull = param_testcase['f'];
      if (paramnmfull.length > 0) {
        //-- a_udf_param가 주어지면 유효항목여부를 점검
        if (Array.isArray(a_udf_param) == true) {
          let param_idx = a_udf_param.findIndex(function(udf_param) { return (udf_param['f'] == paramnmfull); });
          if (param_idx == -1) { continue; }
        }
        
        let value = param_testcase['v'];
        //-- [drm][cmt][CpApiRequest의 headers, request가 value를 String으로만 취급함]
        /*-- [drm][cmt]
        if (Array.isArray(a_udf_param) == true) {
          let datatypecdnm = KsmUtil.$sf_str(a_udf_param[param_idx]['d']);
          if ('string' == datatypecdnm) {
            value = KsmUtil.$sf_str(value);
          }
          else if (('number' == datatypecdnm) || ('integer' == datatypecdnm)) {
            value = parseInt(value, 10);
            value = (isNaN(value) ? Number.NEGATIVE_INFINITY : value);
          }
          else if ('boolean' == datatypecdnm) {
            value = Boolean(value);
          }
          else {
            value = null;
          }
        }
        --*/

        //-- get parent node, assign value
        let a_paramnmfull = paramnmfull.split('.');
        let paramnm = a_paramnmfull[a_paramnmfull.length - 1];
        a_paramnmfull.splice(-1, 1);  //-- remove last element
        let parent_paramnmfull = a_paramnmfull.join('.');
        if (parent_paramnmfull.length == 0) {
          if (KsmUtil.$has_own(cpapireq_param, paramnm) == true) { 
            cpapireq_param[paramnm] = value;
          }
        }
        else {
          let param_node = this.fn_get_param_node(cpapireq_param, parent_paramnmfull);
          if ((param_node != null) && ('object' == typeof(param_node))) {
            if (Array.isArray(param_node) == true) {
              if (value != null) {
                param_node.push(value);
              }
            }
            else {
              param_node[paramnm] = value;
            }
          }
        }
      }
    }
    return cpapireq_param;
  }),
};  //-- const fn_api_paramtest_object

//export default fn_api_paramtest_object;
export { fn_api_paramtest_object };
//-- api_paramtest_object }
