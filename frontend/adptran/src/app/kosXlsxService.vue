<comment>
  KOS api spc excel 파일 read => API정보저장
</comment>
<script>
import { CommonMixin } from '@/common/adptran.js';
import XLSX from 'xlsx';

export default {
  name: 'kosXlsxService',
  mixins: [CommonMixin],
  props: { },
  data() {
    return {
      m_vue_id: 'kosXlsxService',
    } // return{}
  }, // data ()
  computed: {
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
      let fn_onload = ((evt) => {
        let data = evt.target.result;
        data = new Uint8Array(data);
        let excelFile = XLSX.read(data, { type: "array" });	// get excel file
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
      reader.onload = (evt) => {
      	fn_onload(evt, filename);
      };
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
      this.$sf_func_call(fn_proc_error_KOS, error_code, error_message);
    },
    proc_data_KOS(json_data, sheetname, filename) {
      this.$console_log('o-o', '[proc_data_KOS()]', 'json_data: ', json_data, 'sheetname: ', sheetname, 'filename: ', filename);
      this.$sf_func_call(fn_proc_data_KOS, json_data, sheetname, filename);
    },
    //-- for KOS load }
  } // methods:
};
</script>

<template>
  <section>

    <div>
      {{m_vue_id}}
      <input class="file_button" type="file" @change="readFile_KOS" accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet">
    </div>

  </section>
</template>

<style scoped>
  .file_button {display:block;}
</style>
