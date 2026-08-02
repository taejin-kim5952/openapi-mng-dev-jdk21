<comment>
  @import xlsx
    popup-[xlsx등록]
</comment>
<script>
import { CommonMixin, Vuetable } from '@/common/adptran.js';
import XLSX from 'xlsx';
import parse_kos_excel from '@/common/parse_kos_excel.js';

export default {
  name: 'importXlsxDialog',
  props: {
    prop_name: {
      type: String,
      required: true,
    },
    prop_Data: {
      type: Object,
      required: true,
      default: (() => ({ 'api_spc_no': '', 'api_ctgry_no': '', 'ctgry_nm': '', 'yaml_obj': {}, })),
      validator: function(value) {
        if ((parseInt(value['api_spc_no'], 10) > 0) == false) { return false; }
        if ((parseInt(value['api_ctgry_no'], 10) > 0) == false) { return false; }
        if (((value['ctgry_nm']||'').length == 0)) { return false; }
        if (typeof(value['yaml_obj']) != 'object') { return false; }
        return true;
      }
    },
  },
  components: {
    'cus-vuetable': Vuetable,
  },
  mixins: [CommonMixin],
  data() {
    return {
      m_vue_id: 'importXlsxDialog',
      //-- @table {
      table_importXlsxList: {
        fields: [
          { name: 'PK', visible: false },
          { name: 'slot_no', title: '#', width: '35px', titleClass: 'center-aligned', dataClass: 'center-aligned', },
          { name: 'slot_file', title: '파일선택', width: '40%', titleClass: 'center-aligned', dataClass: 'left-aligned', },
          { name: 'slot_path', title: 'Path', width: 'auto', titleClass: 'center-aligned', dataClass: 'left-aligned', },
          { name: 'slot_delete', title: '삭제', width: '50px', titleClass: 'center-aligned', dataClass: 'left-aligned', },
        ],
        css: { table: { tableWrapper: 'cid_custom_scrollbar', tableClass: 'cus-vuetable tb_console', }, },
        pageRow: 8,
      }, // table_importXlsxList{}
      // -- vuetable-2 tr height(px)
      m_table_height_tr: 41 + 6,
      // -- vuetable-2 noDataTemplate
      m_noDataTemplate: '선택된 파일이 없습니다.',
      // -- table data
      m_arr_importData: [],
      m_current_importdData_idx: -1,
      //-- @table }

      //-- @biz-data {
      m_api_spc_no: this.$sf_str(this.prop_Data['api_spc_no']),
      m_api_ctgry_no: this.$sf_str(this.prop_Data['api_ctgry_no']),
      m_ctgry_nm: this.$sf_str(this.prop_Data['ctgry_nm']),
      m_yaml_obj: this.$sf_obj(this.prop_Data['yaml_obj']),

      m_is_import_ongoing: false,
      //-- @biz-data }
    } // return{}
  }, // data()
  computed: {
    ui_ctrl_btn_import_disabled() {
      return (this.computed_importData_count == 0);
    },
    ui_ctrl_btn_ok_disabled() {
      return (this.m_is_import_ongoing == true);
    },
    computed_importData_count() {
      return this.m_arr_importData.filter((importData) => {
        return ($sf_str(importData['file_nm']).length > 0);
      }).length;
    }    
  },
  watch: {
  },
  created: function () {
    this.$console_log('trace', 'created()');

    //-- parse_kos_excel의 외부 handler설정
    parse_kos_excel.fn_alert_message = this.$adpt_alert;
    parse_kos_excel.fn_handler_on_proc_error_KOS = this.fn_handler_on_proc_error_KOS;

    this.m_arr_importData.push(this.fn_get_new_row());  //-- row추가
  },
  mounted: function () {
    this.$console_log('trace', 'mounted()');
    this.$fn_ui_prepare_sc_vuetable_custom_scrollbar('.cid_custom_scrollbar');
  },
  updated: function () {
    this.$console_log('trace', 'updated()');
  },
  methods: {
    //-- @handler {
    onclick_btn_add_row() { //-- row추가
      //--##let find_idx = this.m_arr_importData.findIndex(function(importData) { return ($sf_str(importData['file_nm']).length == 0); });
      //--##if (this.$input_invalid((find_idx != -1), (find_idx + 1) + '번째 행을 먼저 선택 하세요.')) { return false; }

      this.m_arr_importData.push(this.fn_get_new_row());
    },
    onclick_btn_import() { //-- 등록
      this.$adpt_alert('preparing for service');
    },
    onclick_row_action(cmd, rowData, rowIndex) {
      this.$console_log('trace', 'onclick_row_action()', 'cmd: ', cmd, 'rowData: ', rowData, 'rowIndex: ', rowIndex);
      if ('click-file' == cmd) {
        let input_file = this.$refs['input_file'];
        input_file.click();
        this.m_current_importdData_idx = rowIndex;
      }
      else if ('delete-row' == cmd) {
        this.m_arr_importData.splice(rowIndex, 1);
      }
      else if ('view-json-data' == cmd) {
        let importData = this.m_arr_importData[rowIndex];
        let oas = importData['oas'];
        this.$adpt_alert( this.$sf_json_stringify(oas, null, 2) );
      }
    },
    //-- @handler }
    //-- @function {
    fn_get_new_row() {
      return { 'file_nm': '', 'path': '', 'method': '', 'oas': null, }
    },
    fn_dialog_close() {
      let ret_data = {};
      this.$emit('emit_proc_finished', ret_data);
      this.$modal.hide(this.prop_name);
    },
    //-- @function }

    //-- for KOS load {
    readFile(evt, fn_callback) {
      if (evt.target.files.length == 0) {
        return;
      }
      // get File object from input tag
      const file = evt.target.files[0];
      const fileName = file.name;
      
      // declare FileReader, temp result, sheet name list
      const reader = new FileReader();
      let result_sheets = {};
      let arr_sheetName = [];

      //-- ref default function
      let fn_onload = ((evt, filename) => {
        let data = evt.target.result;
        data = new Uint8Array(data);
        let excelFile = XLSX.read(data, { type: "array" }); // get excel file
        // get prased object
        excelFile.SheetNames.forEach((sheetName) => {
            const roa = XLSX.utils.sheet_to_json(excelFile.Sheets[sheetName], { header: 1 });
            if (roa.length) {
              result_sheets[sheetName] = roa;
              arr_sheetName.push(sheetName);
            }
          });
      });
      fn_onload = ((typeof(fn_callback) == 'function') ? fn_callback : fn_onload);

      let a_filename = (evt.target.value).split('\\');
      let filename = a_filename[a_filename.length - 1];
      // if you use "this", don't use "function(e) {...}"
      reader.onload = ((evt) => {
        fn_onload(evt, filename);
      });
      reader.readAsArrayBuffer(file);
    },
    readFile_KOS(evt) {
      let fn_reset_file_data = ((evt) => {
        var source = (evt.target || evt.srcElement);
        source.value = null;
      });

      let files = (evt.target.files || evt.dataTransfer.files);
      if (!files.length) { return false; }
      let file = evt.target.files[0];
      
      //-- [config]
      let uploadfile_min_size = 0;  // bytes
      let uploadfile_max_size = 0;  // bytes

      let b_is_accept_ext = (/(\.xls|\.xlsx)$/i).exec(file.name); 
      if (this.$input_invalid(!b_is_accept_ext, '허용되지 않는 확장자 입니다.')) { fn_reset_file_data(evt); return false; }
      if (this.$input_invalid((file.size <= uploadfile_min_size), '파일 용량은 ' + this.$fmt_data(uploadfile_min_size, 'fmt_filesize') + ' 이상만 가능합니다.')) { fn_reset_file_data(evt); return false; }
      if (uploadfile_max_size > 0) {
        if (this.$input_invalid((file.size > uploadfile_max_size), '파일 용량은 ' + this.$fmt_data(uploadfile_max_size, 'fmt_filesize') + ' 이하만 가능합니다.')) { fn_reset_file_data(evt); return false; }
      }

      let fn_callback = ((evt, filename) => {
        let workbook = XLSX.read((new Uint8Array(evt.target.result)), { type: "array" });

        if (workbook.SheetNames.length < 4) {
          this.proc_error_KOS(-101);
          return;
        }
        let sheetname = this.$sf_str(workbook.SheetNames[3]);
        if (sheetname.length == 0) {
          this.proc_error_KOS(-102);
          return;
        }
        //-- for read empty colume
        //-- {
        let range = workbook.Sheets[sheetname]['!ref'];
        let arr_range = range.split(':');
        arr_range[0] = 'A1';
        range = arr_range.join(':'); 
        //-- }
        let json_data = XLSX.utils.sheet_to_json(workbook.Sheets[sheetname], { raw:false, header: 1, range: range });
        this.proc_data_KOS(json_data, sheetname, filename);
      });
      this.readFile(evt, fn_callback);
    },
    proc_error_KOS(error_code, error_message) {
      this.$console_log('o-o', '[proc_error_KOS()]', 'error_code: ', error_code);
      parse_kos_excel.fn_proc_error_KOS(error_code, error_message);
    },
    proc_data_KOS(json_data, sheetname, filename) {
      this.$console_log('o-o', '[proc_data_KOS()]', 'json_data: ', json_data, 'sheetname: ', sheetname, 'filename: ', filename);
      let idx = this.m_current_importdData_idx;
      let importData = this.m_arr_importData[idx];

      let o_ret = parse_kos_excel.fn_KOS_data_to_OAS2(json_data, sheetname);
      let s_path = this.$sf_obj_val(o_ret, 'path', '');
      let s_method = this.$sf_obj_val(o_ret, 'method', '');
      let o_oas = this.$sf_obj_val(o_ret, 'oas', null);
      if ((s_path.length > 0) && (s_method.length > 0) && (typeof(o_oas) == 'object')) {
        if (true == this.fn_valid_importFile(s_path, s_method)) {
          importData['file_nm'] = filename;
          importData['path'] = s_path;
          importData['method'] = s_method;
          importData['oas'] = o_oas;
          this.m_arr_importData.splice();
        }
      }
      //--[i] clear <input file> for same file select 
      this.$refs['input_file'].value = null;
    },
    fn_handler_on_proc_error_KOS(error_code, error_data) {
      this.$console_log('o-o', '[fn_handler_on_proc_error_KOS()]', 'error_code: ', error_code, 'error_data: ', error_data);
      //-- clear
      //--##let idx = this.m_current_importdData_idx;
      //--##this.m_arr_importData[idx] = this.fn_get_new_row();
      //--##this.m_arr_importData.splice();
    },
    //-- for KOS load }
    fn_valid_importFile(path, method) {
      //-- search in yaml {
      let yaml_paths = this.$sf_obj(this.m_yaml_obj['paths']);
      let path_item = this.$sf_obj(yaml_paths[path]);
      let b_is_exist = this.$has_own(path_item, method);
      if (this.$input_invalid(b_is_exist, '이미 등록된 API 입니다.')) { return false; }
      //-- search in yaml }

      //-- search in selected file {
      let find_idx = this.m_arr_importData.findIndex(function(importData) {
        return ((importData['path'] == path) && (importData['method'] == method)); 
      });
      if (this.$input_invalid((find_idx != -1), '이미 선택된 API 입니다.')) { return false; }
      //-- search in selected file }

      return true;
    },
  } // methods:
};
</script>

<template>
  <div class="dummy popup_wrap">
    <div class="popup_header">
      <span class="popup_header_title">API 등록하기</span>
      <button type="button" class="popup_header_button" role="button" :disabled="ui_ctrl_btn_ok_disabled" @click.stop="fn_dialog_close()" title="Close">
        <span class="button_close">Close</span>
      </button>
    </div>
    <div class="pop_ver pop_ver2">
      <div class="popup_content">
        <div class="btn_set-right">
          <p class="list_count">전체: <span>{{computed_importData_count}}</span> 건</p>
          <button type="button" class="btn_add_row" @click.stop.prevent="onclick_btn_add_row"><span>추가</span></button>
        </div>

        <div class="pkg_board">
          <input class="disp_none" ref="input_file" type="file" @change="readFile_KOS" accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet">
          <cus-vuetable ref="refTable_importXlsxList" :fields="table_importXlsxList.fields" :css="table_importXlsxList.css.table"
            :noDataTemplate="m_noDataTemplate"
            :table-height="((m_table_height_tr * table_importXlsxList.pageRow) + 'px')"
            :api-mode="false"
            :data="m_arr_importData"
            pagination-path=""
          >
            <template slot="slot_no" slot-scope="props">
              {{props.rowIndex + 1}}
            </template>
            <template slot="slot_file" slot-scope="props">
              <span class="td_file_left">
                <a href="javascript:void(0)" @click.stop.prevent="onclick_row_action('view-json-data', props.rowData, props.rowIndex)">
                  <span class="td_text">{{props.rowData.file_nm}}</span>
                </a>
              </span>
              <span class="td_file_right">
                <a href="javascript:void(0)" class="ico_selfile" @click.stop.prevent="onclick_row_action('click-file', props.rowData, props.rowIndex)"><span>파일선택</span></a>
              </span>
            </template>
            <template slot="slot_path" slot-scope="props">
              <span class="td_path_left">
                <span :class="('tag_' + props.rowData.method.toLowerCase())">{{props.rowData.method}}</span>
              </span>
              <span class="td_path_right">
                <span class="td_text">{{props.rowData.path}}</span>
              </span>
            </template>
            <template slot="slot_delete" slot-scope="props">
              <a href="javascript:void(0)" class="ico_delete" @click.stop.prevent="onclick_row_action('delete-row', props.rowData, props.rowIndex)"><span>삭제</span></a>
            </template>
          </cus-vuetable>

        </div><!-- .pkg_board -->

        <div class="bottom_btn">
          <button type="button" :class="['btn', 'btn_black', 'btn_sml', (ui_ctrl_btn_import_disabled ? 'disabled' : '')]" :disabled="ui_ctrl_btn_import_disabled" @click.stop="onclick_btn_import()" title="등록">등록</button>
          <button type="button" :class="['btn', 'btn_sml', (ui_ctrl_btn_ok_disabled ? 'disabled' : '')]" :disabled="ui_ctrl_btn_ok_disabled" @click.stop="fn_dialog_close" title="닫기">닫기</button>
        </div><!-- .bottom_btn -->
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

  .btn.disabled{color:rgba(255,255,255,0.5) !important;}
  .disp_none {display:none;}
  
  .td_text { line-height:24px; display:block; overflow:hidden;white-space:nowrap;text-overflow:ellipsis;}
  .td_file_left { display:inline-block; width:89%;}
  .td_file_right {float:right;}

  .td_path_left {float:left;}
  .td_path_right { display:inline-block; margin-left:10px; width:82%;}

  .ico_selfile {display: inline-block; border: 1px solid #ebebeb; width: 24px; height: 24px; border-radius: 50%; text-align:center; background: #ebebeb;}
  .ico_selfile span {margin-top: 5px; display: inline-block; width: 11px; height: 13px; text-indent: -9999px; 
    background-image: url('data:image/png;base64, \
      iVBORw0KGgoAAAANSUhEUgAAAAsAAAANCAYAAAB/9ZQ7AAABP0lEQVR42pWQPauCYBTH/Qp+EHGWMHtBo0tCTb3Q1u4QBI4uzo26 \
      hCj4CSrdCqK9LYWmWgIR2hqi+tdz4HZxuEMHDgee8+Oc8/w47pvwfR+6rkPTtHeqhWy1WnAcBx+40+nger3ivxBF8Q/+aTaRZRmG \
      wyF6vR4GgwH6/T663S7O5zNt/MDNN3y73RDHMZbLJaIoorpYLHC/31Gv14vw6XRCrVZDpVJBtVqFoigol8s0WVXVIswiTVMkSYL9 \
      fo/j8UgbPM+DYRhFmDVlWYYkSSiVShiPx3TG5XKhHtsymUxA8OPxwGazwXq9xna7JV1BECDP8191mM1mIBvsNua00WhgNBrRVNM0 \
      CeJ5HqvVit64druN5/NZcDudTqnato3D4YD5fI7dbgfOdV0IgkA2WDIbzLFlWfRJdkYYhuC+jRdw8CL5PUZC/wAAAABJRU5ErkJg \
      gg==');
    background-repeat: no-repeat;
  }
  .ico_delete {display: inline-block; border: 1px solid #ebebeb; width: 24px; height: 24px; border-radius: 50%; text-align:center; background: #ebebeb;}
  .ico_delete span {margin-top: 5px; display: inline-block; width: 11px; height: 13px; text-indent: -9999px; 
    background-image: url('data:image/png;base64, \
      iVBORw0KGgoAAAANSUhEUgAAAAsAAAANCAYAAAB/9ZQ7AAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyJp \
      VFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlk \
      Ij8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuMy1jMDExIDY2 \
      LjE0NTY2MSwgMjAxMi8wMi8wNi0xNDo1NjoyNyAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9y \
      Zy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRw \
      Oi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1s \
      bnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0i \
      QWRvYmUgUGhvdG9zaG9wIENTNiAoV2luZG93cykiIHhtcE1NOkluc3RhbmNlSUQ9InhtcC5paWQ6NzBCQjkyMDZENTZBMTFFN0Iy \
      MkVFMjI3NjQ2NjgzOUIiIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6NzBCQjkyMDdENTZBMTFFN0IyMkVFMjI3NjQ2NjgzOUIi \
      PiA8eG1wTU06RGVyaXZlZEZyb20gc3RSZWY6aW5zdGFuY2VJRD0ieG1wLmlpZDo3MEJCOTIwNEQ1NkExMUU3QjIyRUUyMjc2NDY2 \
      ODM5QiIgc3RSZWY6ZG9jdW1lbnRJRD0ieG1wLmRpZDo3MEJCOTIwNUQ1NkExMUU3QjIyRUUyMjc2NDY2ODM5QiIvPiA8L3JkZjpE \
      ZXNjcmlwdGlvbj4gPC9yZGY6UkRGPiA8L3g6eG1wbWV0YT4gPD94cGFja2V0IGVuZD0iciI/PjI8lZ8AAADCSURBVHjaYvTw8GBA \
      A3FAvBCIrwOxFrIEEwMm0AXie0DMDsRCyBIsQFwKxKZoGs5C6RlIYqdBitNw2IAODEGKM4E4lAjFq0AmCkBNfwXEnlD2KSgN4r+E \
      sgVAim9BdT6FhgAI7IbS16HiIHAbpPgOlCMDxE/QrH4MxLJQ9h2Q4m9Q3SDBR1gUy0Dlv8FC4Q5U8WMcJt9BjpRbOJzxBKr4FrJi \
      kE5FIE6A8tuhdDxUHMXk5dAojoTyo5Do+1B5BoAAAwBVKiv3NwzzFQAAAABJRU5ErkJggg==');
    background-repeat: no-repeat;
  }
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
    padding:20px 20px;margin-bottom:20px;overflow:auto;word-break:break-all;
  }
  
  .popup_content .btn_set-right{position:relative;text-align:right;padding:0 0 12px 0;}
  .popup_content .list_count{ position: absolute; left: 0; top: 10px; }
  .popup_content .btn_set-right .btn_add_row{display:inline-block; height: 31px; line-height: 31px; min-width: 85px; border: 1px solid #333; background: transparent; }
  
  .popup_content .bottom_btn {margin-top: 20px; text-align: center;}
</style>

<style scoped>
  .tb_console [class^="tag_"]{margin-left:10px;width:50px; height:20px;padding:0;line-height:20px;vertical-align:middle;text-align:center;color:#fff;font-size:11px;font-weight:700;border-radius:4px;display:inline-block;}
  .tb_console .tag_get{background:#0ec3bc;}
  .tb_console .tag_post{background:#69c4fd;}
  .tb_console .tag_put{background:#93ca3b;}
  .tb_console .tag_delete{background:#fbb104;}
  .tb_console .tag_head{background:#6d81a2;}
  .tb_console .tag_patch{background:#ff6a7d;}
  .tb_console .tag_options{background:#7e679a;}

  .btn{height:40px;line-height:40px;min-width:160px;text-align:center;color:#333;font-size:15px;border:1px solid #333;font-weight:500; border-radius: 5px;}
  .btn_sml.btn{min-width:100px;height:35px;line-height:34px;font-size:13px; border-radius: 5px;}
  .btn_black{color:#fff;background:#333;}
</style>
