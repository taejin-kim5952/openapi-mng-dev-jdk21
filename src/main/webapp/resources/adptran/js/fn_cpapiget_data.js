  //--
  //-- yaml -> request object -> cpapirequest object
  //-- dependency with swagger-parser.min.js

  //-- yaml -> cpapirequest
  //-- cpapiget_data {
  var fn_cpapiget_data = {
    //-- get api cpapirequest object from yaml
    fn_get_api_cpapirequest_object: (function(p_o_api_info) {
      var o_request = this.fn_get_api_request_object(p_o_api_info);
      var o_api_cpapirequest_object = {
        'headers': fn_api_cpapirequest_object.fn_get_headers(o_request['headers']),
        'body': fn_api_cpapirequest_object.fn_get_body(o_request['body']),
        'query': fn_api_cpapirequest_object.fn_get_query(o_request['query']),
      };
      
      return o_api_cpapirequest_object;
    }),

    //-- get api request object from yaml
    fn_get_api_request_object: (function(p_o_api_info) {
      var a_parameters = this.fn_get_api_parameters_array(p_o_api_info);
      var a_headers = [];
      var o_body = {};
      var a_query = [];

      a_parameters.forEach(function(parameter) {
        var s_in = $sf_obj_val(parameter, 'in', '');
        if (s_in == 'header') {
          a_headers.push(parameter);
        }
        else if (s_in == 'body') {  //-- just 1
          o_body = parameter;
        }
        else if (s_in == 'query') {
          a_query.push(parameter);
        }
      });

      var o_api_request_object = {
        'headers': fn_api_request_object.fn_get_headers(a_headers),
        'body': fn_api_request_object.fn_get_body(o_body),
        'query': fn_api_request_object.fn_get_query(a_query),
      };
      
      return o_api_request_object;
    }),

    //-- get api object from yaml
    fn_get_api_parameters_array: (function(p_o_api_info) {
      var YAML = SwaggerParser.YAML;
      var o_yamlSbst = YAML.parse(p_o_api_info['yaml']);
      var s_apiPath = $sf_str(p_o_api_info['path']);
      var s_apiMethod = $sf_str(p_o_api_info['method']).toLowerCase();

      var a_ret = [];
      var o_target = o_yamlSbst;
      if ($has_own(o_target, 'paths') == false) { return a_ret;}
      o_target = o_yamlSbst['paths'];
      if ($has_own(o_target, s_apiPath) == false) { return a_ret;}
      o_target = o_yamlSbst['paths'][s_apiPath];
      if ($has_own(o_target, s_apiMethod) == false) { return a_ret;}
      o_target = o_yamlSbst['paths'][s_apiPath][s_apiMethod];
      if ($has_own(o_target, 'parameters') == false) { return a_ret;}

      return $sf_arr(o_yamlSbst['paths'][s_apiPath][s_apiMethod]['parameters']);
    }),
  };
  //-- cpapiget_data }

  //-- yaml object -> request object for cpapirequest
  //-- api_request_object {
  var fn_api_request_object = {
    //-- array type parameter처리
    fn_get_array: (function(p_s_name, p_o_item) {
      p_s_name = $sf_str(p_s_name); if (p_s_name.length == 0) { return null; }

      var o_req_param = null;

      var o_item = $sf_obj_val(p_o_item, 'items', {});
      var s_type = $sf_obj_val(o_item, 'type', ''); if (s_type.length == 0) { return null; }
      var o_ret = null;
      if ('array' == s_type) {       o_ret = this.fn_get_array(p_s_name, o_item); }
      else if ('object' == s_type) { o_ret = this.fn_get_object(p_s_name, o_item); }
      else {                         o_ret = this.fn_get_primitive(p_s_name, o_item); }
      if (o_ret != null) {
        var s_required = $sf_obj_val(p_o_item, 'x-required', '');
        o_req_param = { 'name': p_s_name, 'type': 'array', 'required': s_required, 'items': o_ret, };
      }

      return o_req_param;
    }),

    //-- object type parameter처리
    fn_get_object: (function(p_s_name, p_o_item) {
      p_s_name = $sf_str(p_s_name); if (p_s_name.length == 0) { return null; }

      var o_req_param = null;

      var o_ret_properties = null;
      var o_properties = $sf_obj_val(p_o_item, 'properties', {});
      for (var s_key in o_properties) {
        var o_item = o_properties[s_key];
        var s_type = $sf_obj_val(o_item, 'type', ''); if (s_type.length == 0) { continue; }
        var o_ret = null;
        if ('array' == s_type) {       o_ret = this.fn_get_array(s_key, o_item); }
        else if ('object' == s_type) { o_ret = this.fn_get_object(s_key, o_item); }
        else {                         o_ret = this.fn_get_primitive(s_key, o_item); }
        if (o_ret != null) {
          o_ret_properties = (o_ret_properties||{});
          o_ret_properties[s_key] = o_ret;
        }
      }
      if (o_ret_properties != null) {
        var s_required = $sf_obj_val(p_o_item, 'x-required', '');
        o_req_param = { 'name': p_s_name, 'type': 'object', 'required': s_required, 'properties': o_ret_properties, };
      }

      return o_req_param;
    }),

    //-- primitive type parameter처리
    fn_get_primitive: (function(p_s_name, p_o_item) {
      p_s_name = $sf_str(p_s_name); if (p_s_name.length == 0) { return null; }
      var s_type = $sf_obj_val(p_o_item, 'type', ''); if (s_type.length == 0) { return null; }
      var s_required = $sf_obj_val(p_o_item, 'x-required', '');
      var s_example = $sf_obj_val(p_o_item, 'x-example', '');
      return { 'name': p_s_name, 'type': s_type, 'required': s_required, 'testdata': s_example, };
    }),

    //-- header parameter[] -> request headers{}
    fn_get_headers: (function(p_a_headers) {
      var o_headers = {};

      for (var idx in p_a_headers) {
        var o_item = p_a_headers[idx];
        var s_name = $sf_obj_val(o_item, 'name', ''); if (s_name.length == 0) { continue; }
        var s_type = $sf_obj_val(o_item, 'type', ''); if (s_type.length == 0) { continue; }

        var o_ret = null;
        if ('array' == s_type) { 
          //--[cmt][ing][ignore array type in headers]
          //--##o_ret = this.fn_get_array(s_name, o_item); 
        }
        else {
          o_ret = this.fn_get_primitive(s_name, o_item);
        }
        if (o_ret != null) {
          o_headers[s_name] = o_ret;
        }
      }

      return o_headers;
    }),
    //-- body parameter{} -> request body{}
    fn_get_body: (function(p_o_body) {
      var o_body = {};

      var s_name = $sf_obj_val(p_o_body, 'name', ''); if (s_name.length == 0) { return o_body; }
      var o_schema = $sf_obj_val(p_o_body, 'schema', {});
      var s_type = $sf_obj_val(o_schema, 'type', ''); if (s_type.length == 0) { return o_body; }
      
      var o_ret = null;
      if ('array' == s_type) {       o_ret = this.fn_get_array(s_name, o_schema); }
      else if ('object' == s_type) { o_ret = this.fn_get_object(s_name, o_schema); }
      else {                         o_ret = this.fn_get_primitive(s_name, o_schema); }
      if (o_ret != null) {
        o_body[s_name] = o_ret;
      }

      return o_body;
    }),
    //-- query parameter[] -> request query{}
    fn_get_query: (function(p_a_query) {
      var o_query = {};

      for (var idx in p_a_query) {
        var o_item = p_a_query[idx];
        var s_name = $sf_obj_val(o_item, 'name', ''); if (s_name.length == 0) { continue; }
        var s_type = $sf_obj_val(o_item, 'type', ''); if (s_type.length == 0) { continue; }

        var o_ret = null;
        if ('array' == s_type) { 
          //--[cmt][ing][ignore array type in query]
          //--##o_ret = this.fn_get_array(s_name, o_item); 
        }
        else {
          o_ret = this.fn_get_primitive(s_name, o_item);
        }
        if (o_ret != null) {
          o_query[s_name] = o_ret;
        }
      }

      return o_query;
    }),
  };
  //-- api_request_object }

  //-- request object for cpapirequest -> cpapirequest object
  //-- api_cpapirequest_object {
  var fn_api_cpapirequest_object = {
    //-- array type 처리
    fn_get_array: (function(p_s_name, p_o_item) {
      p_s_name = $sf_str(p_s_name); if (p_s_name.length == 0) { return null; }

      var a_req_param = null;

      var o_item = $sf_obj_val(p_o_item, 'items', {});
      var s_type = $sf_obj_val(o_item, 'type', ''); if (s_type.length == 0) { return null; }
      var o_ret = null;
      if ('array' == s_type) {       o_ret = this.fn_get_array(p_s_name, o_item); }
      else if ('object' == s_type) { o_ret = this.fn_get_object(p_s_name, o_item); }
      else {                         o_ret = this.fn_get_primitive(o_item); }
      if (o_ret != null) {
        a_req_param = (a_req_param||[]);
        a_req_param.push(o_ret);
      }

      return a_req_param;
    }),

    //-- object type 처리
    fn_get_object: (function(p_s_name, p_o_item) {
      p_s_name = $sf_str(p_s_name); if (p_s_name.length == 0) { return null; }

      var o_req_param = null;

      var o_ret_properties = null;
      var o_properties = $sf_obj_val(p_o_item, 'properties', {});
      for (var s_key in o_properties) {
        var o_item = o_properties[s_key];
        var s_type = $sf_obj_val(o_item, 'type', ''); if (s_type.length == 0) { continue; }
        var o_ret = null;
        if ('array' == s_type) {       o_ret = this.fn_get_array(s_key, o_item); }
        else if ('object' == s_type) { o_ret = this.fn_get_object(s_key, o_item); }
        else {                         o_ret = this.fn_get_primitive(o_item); }
        if (o_ret != null) {
          o_ret_properties = (o_ret_properties||{});
          o_ret_properties[s_key] = o_ret;
        }
      }
      if (o_ret_properties != null) {
        o_req_param = o_ret_properties;
      }

      return o_req_param;
    }),

    //-- primitive type 처리
    fn_get_primitive: (function(p_o_item) {
      var s_type = $sf_obj_val(p_o_item, 'type', '');
      var o_testdata = $sf_obj_val(p_o_item, 'testdata', null);
      if ('string' == s_type) {
        o_testdata = $sf_str(o_testdata);
      }
      else if (('number' == s_type) || ('integer' == s_type)) {
        o_testdata = parseInt(o_testdata, 10);
        o_testdata = (isNaN(o_testdata) ? Number.NEGATIVE_INFINITY : o_testdata);
      }
      else if ('boolean' == s_type) {
        o_testdata = Boolean(o_testdata);
      }
      return o_testdata;
    }),

    //-- request header -> cpapirequest headers object
    fn_get_headers: (function(p_o_headers) {
      var o_headers = {};

      for (var key in p_o_headers) {
        var o_item = p_o_headers[key];
        var s_name = $sf_obj_val(o_item, 'name', ''); if (s_name.length == 0) { continue; }
        var s_type = $sf_obj_val(o_item, 'type', ''); if (s_type.length == 0) { continue; }

        var o_ret = null;
        if ('array' == s_type) { o_ret = this.fn_get_array(s_name, o_item); }
        else {                   o_ret = this.fn_get_primitive(o_item); }
        if (o_ret != null) {
          o_headers[s_name] = o_ret;
        }
      }

      return o_headers;
    }),
    //-- request body object -> cpapirequest headers object
    fn_get_body: (function(p_o_body) {
      var o_body = {};
      
      var s_key = (Object.keys(p_o_body)[0]||'');
      //--@@var o_item = p_o_body;
      var o_item = $sf_obj_val(p_o_body, s_key, {});
      var s_name = $sf_obj_val(o_item, 'name', ''); if (s_name.length == 0) { return o_body; }
      var s_type = $sf_obj_val(o_item, 'type', ''); if (s_type.length == 0) { return o_body; }
      
      var o_ret = null;
      if ('array' == s_type) {       o_ret = this.fn_get_array(s_name, o_item); }
      else if ('object' == s_type) { o_ret = this.fn_get_object(s_name, o_item); }
      else {                         o_ret = this.fn_get_primitive(o_item); }
      if (o_ret != null) {
        o_body[s_name] = o_ret;
      }

      return o_body;
    }),
  };
  //-- api_cpapirequest_object }
