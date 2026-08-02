
<t:layout type="empty">
<!--// regFormPrivateHandlerParam_inc.jsp -->
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>

<c:set var="bIsSysIdBiznaru" value="${requestScope.isSysIdBiznaru eq 'Y'}" />

<div class="handlerParam_box">
  <table class="table-edit">
    <caption>Handler Parameter</caption>
    <colgroup>
      <col width="30%">
      <col width="">
    </colgroup>
    <tbody class="cid_handler_param_tb">
      <!--
      <tr class="hide cid_handler_param_tr">
        <th>handler_param_1</th>
        <td><input type="text" title="handler_param_1" id="id_txt_handler_param_1" name="handler_param_1"></td>
      </tr>
      -->
    </tbody>
  </table>
</div>

<script>
  <%-- //-- handler param 설정 --%>
  <%-- //-- prefix: api_XXX, req_XXX, res_XXX, prop_XXX --%>
  <%-- //-- input_type: stext, mtext --%>
  var g_handler_param_cfg = {
      'req_CLIENT_IP_RULE'       : {'title': 'Request Client IP 매핑키'    , 'el_key': 'hdp_req_client_ip_rule'       , 'el_gub': 'stxt', 'man': 'Y', 'max_len': 200 , 'fd_nm': 'endpntClientIp'      },
      'res_RESMAP_RES_CD_FIELD'  : {'title': 'Response 결과매핑-결과필드'     , 'el_key': 'hdp_res_resmap_res_cd_field'  , 'el_gub': 'stxt', 'man': 'N', 'max_len': 200 , 'fd_nm': 'resmapResCdField'    },
      'res_RESMAP_SUCC_VAL'      : {'title': 'Response 결과매핑-성공기준'     , 'el_key': 'hdp_res_resmap_succ_val'      , 'el_gub': 'stxt', 'man': 'N', 'max_len': 100 , 'fd_nm': 'resmapSuccVal'       },
      'res_RESMAP_ERR_CD_FIELD'  : {'title': 'Response 결과매핑-에러코드필드'  , 'el_key': 'hdp_res_resmap_err_cd_field'  , 'el_gub': 'stxt', 'man': 'N', 'max_len': 200 , 'fd_nm': 'resmapErrCdField'    },
      'res_RESMAP_ERR_MSG_FIELD' : {'title': 'Response 결과매핑-에러메시지필드' , 'el_key': 'hdp_res_resmap_err_msg_field', 'el_gub': 'stxt', 'man': 'N', 'max_len': 200  , 'fd_nm': 'resmapErrMsgField'   },
      'api_OUT_FORMAT'           : {'title': 'out-format'                , 'el_key': 'hdp_api_out_format'           , 'el_gub': 'stxt', 'man': 'Y', 'max_len': 50  , 'fd_nm': 'hdpApiOutFormat'      },
      'api_OUT_COMMON_PARAM'     : {'title': 'out-common-param'          , 'el_key': 'hdp_api_out_common_param'     , 'el_gub': 'stxt', 'man': 'Y', 'max_len': 50  , 'fd_nm': 'hdpApiOutCommonParam' },
      'api_ENDPOINT_ID'          : {'title': 'endpoint-id'               , 'el_key': 'hdp_api_endpoint_id'          , 'el_gub': 'stxt', 'man': 'Y', 'max_len': 100 , 'fd_nm': 'hdpApiEndpointId'     },
      'req_API_NAME'             : {'title': 'Request API_NAME'          , 'el_key': 'hdp_req_api_name'             , 'el_gub': 'stxt', 'man': 'Y', 'max_len': 200 , 'fd_nm': 'hdpReqApiName'        },
      'req_CONFIG_TO_BODY'       : {'title': 'Request CONFIG_TO_BODY'    , 'el_key': 'hdp_req_config_to_body'       , 'el_gub': 'mtxt', 'man': 'N', 'max_len': 8000, 'fd_nm': 'hdpReqConfigToBody'   },
      'req_HEADER_TO_BODY'       : {'title': 'Request HEADER_TO_BODY'    , 'el_key': 'hdp_req_header_to_body'       , 'el_gub': 'mtxt', 'man': 'N', 'max_len': 8000, 'fd_nm': 'hdpReqHeaderToBody'   },
      'req_MAPPING_TO_BODY'      : {'title': 'Request MAPPING_TO_BODY'   , 'el_key': 'hdp_req_mapping_to_body'      , 'el_gub': 'mtxt', 'man': 'N', 'max_len': 8000, 'fd_nm': 'hdpReqMappingToBody'  },
      'req_URL_DECODE'           : {'title': 'Request URL_DECODE'        , 'el_key': 'hdp_req_url_decode'           , 'el_gub': 'stxt', 'man': 'N', 'max_len': 50  , 'fd_nm': 'hdpReqUrlDecode'      },
      'req_URL_ENCODE'           : {'title': 'Request URL_ENCODE'        , 'el_key': 'hdp_req_url_encode'           , 'el_gub': 'stxt', 'man': 'N', 'max_len': 50  , 'fd_nm': 'hdpReqUrlEncode'      },
      'res_MAPPING_TO_BODY'      : {'title': 'Response MAPPING_TO_BODY'  , 'el_key': 'hdp_res_mapping_to_body'      , 'el_gub': 'mtxt', 'man': 'N', 'max_len': 8000, 'fd_nm': 'hdpResMappingToBody'  },
      'res_PROVIDE_PARAM'        : {'title': 'Response PROVIDE_PARAM'    , 'el_key': 'hdp_res_provide_param'        , 'el_gub': 'mtxt', 'man': 'N', 'max_len': 8000, 'fd_nm': 'hdpResProvideParam'   },
      'res_URL_ENCODE'           : {'title': 'Response URL_ENCODE'       , 'el_key': 'hdp_res_url_encode'           , 'el_gub': 'stxt', 'man': 'N', 'max_len': 50  , 'fd_nm': 'hdpResUrlEncode'      },
      'ext_prop_IS_BIZNARU'      : {'title': '비즈나루API 여부'              , 'el_key': 'hdp_api_biznaru'              , 'el_gub': 'chkyn', 'man': 'Y', 'max_len': -1  , 'fd_nm': 'hdpExtProp', 'prop_key': 'is_biznaru' },
      //-- [tag:SR-20230113][add]
      'HNDLROPTN_CONFIG'         : {'title': 'handler option CONFIG'     , 'el_key': 'hdp_hndlroptn_config'         , 'el_gub': 'mtxt', 'man': 'N', 'max_len': 8000, 'fd_nm': 'hdpHndlroptnConfig'   },
  };
  <%-- //-- api handler 별 handler param --%> 
  var g_handler_param = {
      'APIHDR1010': ['req_CLIENT_IP_RULE', 'HNDLROPTN_CONFIG'], //-- COMMON
      'APIHDR1020': ['req_CLIENT_IP_RULE', 'res_RESMAP_RES_CD_FIELD', 'res_RESMAP_SUCC_VAL', 'res_RESMAP_ERR_CD_FIELD', 'res_RESMAP_ERR_MSG_FIELD', 'HNDLROPTN_CONFIG'], //-- ANYCOMMON
      'APIHDR1030': ['HNDLROPTN_CONFIG'], //-- KOS
      'APIHDR1040': ['HNDLROPTN_CONFIG'], //-- KOSMOS
      'APIHDR1050': ['api_OUT_COMMON_PARAM', 'api_ENDPOINT_ID', 'req_API_NAME', 'req_URL_DECODE', 'res_URL_ENCODE',
        'req_CONFIG_TO_BODY', 'req_HEADER_TO_BODY', 'req_MAPPING_TO_BODY', 'res_MAPPING_TO_BODY', 'res_PROVIDE_PARAM', 'HNDLROPTN_CONFIG'], //-- SCAP
      'APIHDR1060': ['req_URL_DECODE', 'req_URL_ENCODE', 'res_URL_ENCODE',
        'req_CONFIG_TO_BODY', 'req_HEADER_TO_BODY', 'req_MAPPING_TO_BODY', 'res_MAPPING_TO_BODY', 'res_PROVIDE_PARAM', 'HNDLROPTN_CONFIG'], //-- CAPRI
      'APIHDR1070': ['api_OUT_FORMAT', 'api_OUT_COMMON_PARAM', 'req_API_NAME', 'req_URL_DECODE', 'res_URL_ENCODE',
        'req_CONFIG_TO_BODY', 'req_HEADER_TO_BODY', 'req_MAPPING_TO_BODY', 'res_MAPPING_TO_BODY', 'res_PROVIDE_PARAM', 'HNDLROPTN_CONFIG'], //-- SB
  }
  
  <c:if test="${bIsSysIdBiznaru}">
  <%-- //-- [tag:SR-20210515] --%>
  g_handler_param['APIHDR1010'].push('ext_prop_IS_BIZNARU');
  </c:if>

  <%-- //-- handler param form 초기화 --%> 
  function fn_hdp_edit_form_init() {
    $('.cid_handler_param_tb tr.cid_handler_param_tr').remove();
  }
  
  <%-- //-- api handler 별 handler form 설정 --%>
  function fn_hdp_build_edit_form(p_handler_cd) {
    fn_hdp_edit_form_init();
    var jq_tb = $('.cid_handler_param_tb');
    
    var a_handler_param = $sf_obj_val(g_handler_param, p_handler_cd, []);
    a_handler_param.forEach(function(param_item) {
      var jq_edit_line = fn_hdp_build_edit_line(param_item);
      jq_tb.append(jq_edit_line);
    });
  }
  
  <%-- //-- handler param별 입력 ui 생성 --%>
  function fn_hdp_build_edit_line(p_hdp_key) {
    var o_hdp_cfg = $sf_obj_val(g_handler_param_cfg, p_hdp_key);

    var s_title = $sf_obj_val(o_hdp_cfg, 'title');
    var el_key = $sf_obj_val(o_hdp_cfg, 'el_key');
    var el_gub = $sf_obj_val(o_hdp_cfg, 'el_gub');
    var man = $sf_obj_val(o_hdp_cfg, 'man');
    var max_len = $sf_obj_val(o_hdp_cfg, 'max_len', 0);
    var fd_nm = $sf_obj_val(o_hdp_cfg, 'fd_nm');
    var prop_key = $sf_obj_val(o_hdp_cfg, 'prop_key');

    var el_nm = el_key;
    var el_id = 'id_' + el_key;
    var cls_man = (('Y' == man.toUpperCase()) ? 'essential' : '');
    var tag_el = '';

    if ('stxt' == el_gub) {
      tag_el = '<input type="text" class="cid_handler_param_el" title="' + s_title + '" name="' + el_key + '" id="' + el_id + '" maxlength="' + max_len + '">';
    }
    else if ('mtxt' == el_gub) {
      var on_script = 'apiRegCheckStrLength(' + max_len + ', $(this).attr(\'id\'))';
      tag_el = '<textarea class="cid_handler_param_el" title="' + s_title + '" name="' + el_key + '" id="' + el_id + '" onchange="' + on_script + '" onkeyup="' + on_script + '"></textarea>';
    }
    else if ('chkyn' == el_gub) {
      tag_el = '<a href="javascript:void(0)"><input type="checkbox" class="cid_handler_param_el" title="' + s_title + '" name="' + el_key + '" id="' + el_id + '"><label for="' + el_id + '"><span></span></label></a>';
    }
    var jq_line = $((tag_el.length > 0) ? ('<tr class="cid_handler_param_tr"><th><span class="' + cls_man + '">' + s_title + '</span></th><td>' + tag_el + '</td></tr>') : '');
    jq_line.data('hdp_key', p_hdp_key); //-- handle param key저장

    var fd_val = gfn_get_apiDef(fd_nm);
    if ('hdpExtProp' == fd_nm) {
      //-- property값 query
      var o_ext_prop = $fn_get_ext_prop([prop_key], fd_val);
      fd_val = $sf_str($sf_obj_val(o_ext_prop, prop_key)).trim();
    }

    if ('chkyn' == el_gub) {
      jq_line.find('.cid_handler_param_el').prop('checked', ('Y' == $sf_str(fd_val).toUpperCase()));
    }
    else {
      jq_line.find('.cid_handler_param_el').val(fd_val);
    }

    return jq_line;
  }

  <%-- //-- get handler param별 입력정보 object --%>
  function fn_hdp_get_handler_param_obj() {
    var o_data = {};
    $('.cid_handler_param_tb tr.cid_handler_param_tr').each(function(index, item) {
      var hdp_key = $sf_str($(item).data('hdp_key'));
      var o_hdp_cfg = $sf_obj_val(g_handler_param_cfg, $sf_str($(item).data('hdp_key')));
      var el_gub = $sf_obj_val(o_hdp_cfg, 'el_gub');
      var fd_nm = $sf_obj_val(o_hdp_cfg, 'fd_nm');
      var prop_key = $sf_obj_val(o_hdp_cfg, 'prop_key');
      if (fd_nm.length > 0) {
        var fd_val = $(item).find('.cid_handler_param_el').val();
        if ('chkyn' == el_gub) {
          fd_val = ($(item).find('.cid_handler_param_el').is(':checked') ? 'Y' : '');
        }
        if ('hdpExtProp' == fd_nm) {
          fd_val = ((('is_biznaru' == prop_key) && ('Y' == fd_val)) ? 'Y' : null);
          var ext_prop = gfn_get_apiDef(fd_nm);
          //-- property값 set
          fd_val = $fn_set_ext_prop(prop_key, fd_val, ext_prop)
        }
        o_data[fd_nm] = fd_val;
      }
    });
    return o_data;
  }
</script>

<!-- regFormPrivateHandlerParam_inc.jsp //-->

</t:layout>