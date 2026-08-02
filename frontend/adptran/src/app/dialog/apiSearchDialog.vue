<comment>
  @api search
    popup-[API검색]
</comment>
<script>
import { CommonMixin, Vuetable } from '@/common/adptran.js';
import _ from 'lodash';

export default {
  name: 'apiSearchDialog',
  props: {
    prop_name: {
      type: String,
      required: true,
    },
    prop_apiDataList: {
      type: Array,
      required: true,
      default: (() => []),  //-- [{ 'api_group': '', 'api_nm': '', 'api_no': '', 'path': '', 'method': '', }]
      validator: function(value) {
        return value.every((item) => {
          if (typeof(item) != 'object') { return false; }
          if (item.hasOwnProperty('api_group') == false) { return false; }
          if (item.hasOwnProperty('api_nm') == false) { return false; }
          if (item.hasOwnProperty('api_no') == false) { return false; }
          if (item.hasOwnProperty('path') == false) { return false; }
          if (item.hasOwnProperty('method') == false) { return false; }
          if ((parseInt(item['api_no'], 10) > 0) == false) { return false; }
          return true;
        });
      }
    },
    prop_autoComplete: { type: Boolean, required: true, default: true, },
  },
  components: {
    'cus-vuetable': Vuetable,
  },
  mixins: [CommonMixin],
  data () {
    return {
      m_vue_id: 'apiSearchDialog',
      //-- @table {
      table_apiSearchList: {
        fields: [
          { name: 'PK', visible: false },
          { name: 'api_group', title: 'API그룹', width: '12%', titleClass: 'center-aligned', dataClass: 'center-aligned',
            formatter: ((value) => this.fmt_keyword(value)), },
          { name: 'slot_api_nm', title: 'API', width: '33%', titleClass: 'center-aligned', dataClass: 'center-aligned', },
          { name: 'slot_path', title: 'Path', width: 'auto', titleClass: 'center-aligned', dataClass: 'left-aligned', },
        ],
        css: { table: { tableWrapper: 'cid_custom_scrollbar', tableClass: 'cus-vuetable tb_console', }, },
        pageRow: 5,
      }, // table_apiSearchList{}
      // -- vuetable-2 tr height(px)
      m_table_height_tr: 41,
      // -- vuetable-2 noDataTemplate
      m_noDataTemplate: 'API가 없습니다.',
      // -- table data
      m_apiDataList: this.prop_apiDataList,
      //-- @table }
      m_model_txt_filter: '',
      
      m_con_autoComplete: this.prop_autoComplete,
    } // return{}
  }, // data ()
  computed: {
  },
  watch: {
    //--$$m_apiDataList(newVal, oldVal) { this.$refs.refTable_apiSearchList.refresh(); },
    m_model_txt_filter(newVal, oldVal) {
      if (this.m_con_autoComplete == true) {
        this.$refs.refTable_apiSearchList.refresh();
      }
    },
  },
  created: function () {
    this.$console_log('trace', 'created()');
  },
  mounted: function () {
    this.$console_log('trace', 'mounted()');
    this.$fn_ui_prepare_sc_vuetable_custom_scrollbar('.cid_custom_scrollbar');
    this.$refs.txt_search.focus();
  },
  updated: function () {
    this.$console_log('trace', 'updated()');
  },
  methods: {
    //-- @handler {
    onclick_btn_search() { //-- 검색
      this.$refs.refTable_apiSearchList.refresh();
    },
    onclick_row_action(cmd, rowData) {
      this.$console_log('trace', 'onclick_row_action()', 'cmd: ', cmd, 'rowData: ', rowData);
      this.$emit('emit_proc_action', { 'cmd': cmd, 'rowdata': rowData, 'fn_close': this.fn_dialog_close });
    },
    //-- @handler }
    //-- @table {
    fn_dataManager(sortOrder, pagination) {
      //-- [i][ref][https://www.vuetable.com/guide/api-vs-data-mode.html#data-mode]
      this.$console_log('trace', 'fn_dataManager()', 'sortOrder: ', sortOrder, 'pagination: ', pagination);
      let local = [];

      //--@@if ((this.m_model_txt_filter.length == 0) || (this.m_apiDataList.length < 1)) { return local; }

      local = this.m_apiDataList;

      // sortOrder can be empty, so we have to check for that as well
      if (sortOrder.length > 0) {
        this.$console_log('trace', 'fn_dataManager()', 'sortField: ', sortOrder[0].sortField, 'direction: ', sortOrder[0].direction);
        local = _.orderBy(local, sortOrder[0].sortField, sortOrder[0].direction);
      }
      
      local = local.filter((item_api) => {
        let txt_filter = this.m_model_txt_filter.toLowerCase();
        return ((item_api.api_nm.toLowerCase(txt_filter).indexOf() != -1)
          || (item_api.path.toLowerCase().indexOf(txt_filter) != -1)
          || (item_api.api_group.toLowerCase().indexOf(txt_filter) != -1));
      });

      return local;
    },
    //-- @table }
    //-- @function {
    fmt_keyword(text) {
      let s_regexp = this.m_model_txt_filter.replace(/[-\/\\^$*+?.()|[\]{}]/g, '\\$&');  // $& means the whole matched string
      var regexp = new RegExp(s_regexp, 'g');
      let html = text.replace(regexp, '<span class="matched_text">' + this.m_model_txt_filter + '</span>');
      return html;
    },
    fn_dialog_close() {
      let ret_data = { 'cmd': 'close-dialog' };
      this.$emit('emit_proc_finished', ret_data);
      this.$modal.hide(this.prop_name);
    },
    //-- @function }
  } // methods:
};
</script>

<template>
  <div class="popup_wrap">
    <div class="popup_header">
      <span class="popup_header_title">API Search</span>
      <button type="button" class="popup_header_button" role="button" @click.stop="fn_dialog_close()" title="Close">
        <span class="button_close">Close</span>
      </button>
    </div>
    <div class="pop_ver pop_ver2">
      <div class="popup_content">

        <div class="pkg_board">
        
          <div class="searching_line">
            <em>검색어</em>
            <div class="select_form">
              <span class="input_txt"><input type="text" title="검색어 입력" placeholder="검색어를 입력하세요." ref="txt_search" v-model="m_model_txt_filter" @keyup.enter="onclick_btn_search"></span>
              <button type="button" class="btn-lg btn_searching" v-show="true || (m_con_autoComplete == false)" @click.stop.prevent="onclick_btn_search"><span>검색</span></button>
            </div>
          </div>

          <cus-vuetable ref="refTable_apiSearchList" :fields="table_apiSearchList.fields" :css="table_apiSearchList.css.table"
            :noDataTemplate="m_noDataTemplate"
            :table-height="((m_table_height_tr * table_apiSearchList.pageRow) + 'px')"
            :api-mode="false"
            :dum-data="m_apiDataList"
            :data-manager="fn_dataManager"
            pagination-path=""
          >
            <template slot="slot_api_nm" slot-scope="props">
              <a href="javascript:void(0)" class="n_line" @click.stop.prevent="onclick_row_action('click-api_nm', props.rowData)"><span v-html="fmt_keyword(props.rowData.api_nm)"></span></a>
            </template>
            <template slot="slot_path" slot-scope="props">
              <span :class="('tag_' + props.rowData.method.toLowerCase())">{{props.rowData.method}}</span>
              <a href="javascript:void(0)" @click.stop.prevent="onclick_row_action('click-path', props.rowData)"><span v-html="fmt_keyword(props.rowData.path)"></span></a>
            </template>
          </cus-vuetable>
        </div><!-- .pkg_board -->

        <div class="bottom_btn">
          <button type="button" class="btn btn_black btn_sml" @click.stop="fn_dialog_close()" title="닫기">닫기</button>
        </div>

      </div><!-- .popup_content -->
    </div><!--.pop_ver pop_ver2 -->
  </div><!-- .popup_wrap -->
</template>

<style scoped>
@import './../../css/cus-vuetable.css';
</style>
<style>
  .tb_console {width:100%;font-size:13px;border-collapse:separate;}
  .tb_console thead th {border-top:2px solid #333333;}
  .tb_console thead th,
  .tb_console tbody th {padding:10px 0;background:#f6f6f6;border-bottom:1px solid #333333;border-left:1px solid #e5e5e5;color:#222222;font-weight:700;}
  .tb_console thead th:first-child,
  .tb_console tbody th:first-child {border-left:none;}
  .tb_console tbody th {padding:10px 0;border-bottom:1px solid #e5e5e5;border-left:1px solid #e5e5e5;color:#666666;}
  .tb_console tbody td {padding:10px;border-bottom:1px solid #e5e5e5;border-left:1px solid #e5e5e5;color:#666666;text-align:center;word-break:break-all;}
  .tb_console tbody td:first-child {border-left:none;}

  .tb_console .matched_text { font-weight:700; background-color:#D4FF32; }
</style>

<style scoped>
  /* t, l, w, h provide by model dialog */
  .popup_wrap {z-index: 900; height: 100%; width: 100%; overflow: hidden;}
  .popup_header {background: #333; height: 55px; width: calc(100% - 40px); line-height: 55px;padding: 0 20px;}
  .popup_header_title {color:#fff; font-size: 18px;}
  .popup_header_button {float: right;}
  .button_close { display: inline-block; width: 23px; height: 23px; text-indent: -9999px;
    background-image: url('data:image/png;base64, \
      iVBORw0KGgoAAAANSUhEUgAAABcAAAAXCAYAAADgKtSgAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyJp \
      VFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlk \
      Ij8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuMy1jMDExIDY2 \
      LjE0NTY2MSwgMjAxMi8wMi8wNi0xNDo1NjoyNyAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9y \
      Zy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRw \
      Oi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1s \
      bnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0i \
      QWRvYmUgUGhvdG9zaG9wIENTNiAoV2luZG93cykiIHhtcE1NOkluc3RhbmNlSUQ9InhtcC5paWQ6RjAzRjYxMTRDNUVBMTFFNzhC \
      OTdGNDYwM0MyNThFQzEiIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6RjAzRjYxMTVDNUVBMTFFNzhCOTdGNDYwM0MyNThFQzEi \
      PiA8eG1wTU06RGVyaXZlZEZyb20gc3RSZWY6aW5zdGFuY2VJRD0ieG1wLmlpZDpGMDNGNjExMkM1RUExMUU3OEI5N0Y0NjAzQzI1 \
      OEVDMSIgc3RSZWY6ZG9jdW1lbnRJRD0ieG1wLmRpZDpGMDNGNjExM0M1RUExMUU3OEI5N0Y0NjAzQzI1OEVDMSIvPiA8L3JkZjpE \
      ZXNjcmlwdGlvbj4gPC9yZGY6UkRGPiA8L3g6eG1wbWV0YT4gPD94cGFja2V0IGVuZD0iciI/PoEtARUAAADzSURBVHjarJU9DsIw \
      DIVN1L0ZYG4XbsAReoTekiMwMTccghmGMoMUHOpKUdUmtoOlp/4ln5PIzzXee0A1KEv3pbLEAwMALeqKuqAslIUlTuA1AT6inqhT \
      YYIZHDgP1CveivNTDIojWp2fHaAFL+GaBMnx4gmScaCcGH93WwvQbJkFzsHXErRcMAe+TPDmgmeH5iKYrEd9UBVde3qfDMN03jkC \
      V/RsS+GxpR3qiLqxW0XmrIdMtQyaatkCi4ymAasdygWLe4sUzO6KMdj9s59biaUlCQzV6p7qt+M4L+HkjjgHVL37LX/6SY8F4KXx \
      atT9K8AAk0+7AL1fFF0AAAAASUVORK5CYII='); /* ico_closeB.png */ 
    background-repeat: no-repeat;
    margin-top: 15px;
  }
  .pop_ver2 {
    height: calc(100% - 55px - 3px);/* -55(header) -3(border-bottom) */
    background: #fff; border-left: 3px solid #333; border-right: 3px solid #333; border-bottom: 3px solid #333;
  }
  .popup_content {
    /*height: calc(100% - 55px - 40px + 2px - 40px - 38px ); -55(header) -40(t,b padding) +2(border-bottom) -40(t,b padding) -38(serachbox) */
    padding:20px 20px;margin-bottom:20px;overflow:auto;word-break:break-all;
  }
  .popup_content .bottom_btn {margin-top: 20px; text-align: center;}
</style>

<style scoped>
  .tb_console [class^="tag_"]{width:50px; height:20px;padding:0;line-height:20px;vertical-align:middle;text-align:center;color:#fff;font-size:11px;font-weight:700;border-radius:4px;display:inline-block;}
  .tb_console .tag_get{background:#0ec3bc;}
  .tb_console .tag_post{background:#69c4fd;}
  .tb_console .tag_put{background:#93ca3b;}
  .tb_console .tag_delete{background:#fbb104;}
  .tb_console .tag_head{background:#6d81a2;}
  .tb_console .tag_patch{background:#ff6a7d;}
  .tb_console .tag_options{background:#7e679a;}

  .btn{height:40px;line-height:40px;min-width:160px;text-align:center;color:#333;font-size:15px;border:1px solid #333;font-weight:500; border-radius: 5px;}
  .btn_sml.btn{min-width:100px;height:35px;line-height:34px;font-size:13px; border-radius: 5px;}
  .btn_black{color:#fff !important;background:#333;}
  .btn-lg{height:37px;line-height:36px;}

  /* searching_line */
  .searching_line{position:relative;margin-bottom:10px;text-align:left;}
  .searching_line div span {display:inline-block;vertical-align:middle;}
  .searching_line div span.input_txt {width:200px;}
  .searching_line div span.input_txt input{height:34px;}

  .searching_line > em{min-width:40px;display:inline-block;font-size:15px;line-height:34px;color:#333;font-weight:700;margin-right:2px;}
  .searching_line .select_form{display:inline-block;}
  .select_form .btn_searching{display:inline-block; min-width: 85px; color: #fff; background: #353535;}
</style>

