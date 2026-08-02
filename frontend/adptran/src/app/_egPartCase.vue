<comment>
  e.g. vue Component for jsp embeded
</comment>
<script>
import { CommonMixin } from '@/common/adptran.js';

export default {
  name: 'egPartCase',
  components: { },
  mixins: [CommonMixin],
  props: { },
  data() {
    return {
      m_vue_id: 'egPartCase',
      //-- @constant {
      //-- @constant }
      //-- @property {
      //-- @property }
      //-- @biz-data {
      //-- @biz-data }
      //-- @ui-data {
      //-- @ui-data }
    } // return{}
  }, // data ()
  computed: {
  },
  watch: {
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
    //-- @handler {
    //-- @handler }
    //-- @api function {
    //-- api xxx_list정보 query (list)
    fn_call_api_xxx_list(param, fn_callback) {
      //--@@this.$fn_call_api_common(url, {}, 'get', fn_callback);
      //--@@this.$fn_call_api_common(url, { 'param': param }, 'post', fn_callback);
    },
    //-- api xxx_list정보 query 후처리
    fn_callback_api_xxx_list(ret, response, request, recordset) {
      this.$console_log('trace', 'fn_callback_api_xxx_list()', 'ret: ', ret, 'response: ', response, 'request: ', request, 'recordset: ', recordset);
      //-- ret: 'ok', 'nk', 'failed', 'catched'
      if ('ok' == ret) {
        //--[todo] with recordset
        recordset.forEach((record) => {
        });
      }
      else {
        this.$proc_api_resultCode_Fail(ret, response);
      }
    },

    //-- api xxx정보 query (record)
    fn_call_api_xxx(param, fn_callback) {
      //--@@this.$fn_call_api_common(url, {}, 'get', fn_callback);
      //--@@this.$fn_call_api_common(url, { 'param': param }, 'post', fn_callback);
    },
    //-- api xxx정보 query 후처리
    fn_callback_api_xxx(ret, response, request, record) {
      this.$console_log('trace', 'fn_callback_api_xxx()', 'ret: ', ret, 'response: ', response, 'request: ', request, 'recordset: ', recordset);
      //-- ret: 'ok', 'nk', 'failed', 'catched'
      if ('ok' == ret) {
        //--[todo] with record
      }
      else {
        this.$proc_api_resultCode_Fail(ret, response);
      }
    },
    //-- @api function }
    //-- @function {
    fn_dialog_close() {
      this.$modal.hide(this.prop_name);
    },
    //-- @function }
  } // methods:
};
</script>

<template>
  <section>

    <div>
      {{m_vue_id}}
    </div>

  </section>
</template>

<style scoped>
</style>
