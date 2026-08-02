<comment>
  dynamic query
  /adptran/devQuery
</comment>
<script>
import { CommonMixin, Vuetable, CusVuetablePagination } from '@/common/adptran.js';
import Environment from '@/app/environment.js';

export default {
  name: 'devQuery',
  components: {
    'cus-vuetable': Vuetable,
    'cus-vuetable-pagination': CusVuetablePagination,
  },
  mixins: [CommonMixin],
  props: {
    prop_data: {
      type: Object,
      default: (() => ({ auto_load_schema_list: 'y' })),
      validator: function(value) {
        if ('object' != typeof(value)) { return false; }
        return true; 
      }
    },
  },
  data() {
    return {
      m_vue_id: 'devQuery',
      //-- @constant {
      m_con_using_vuetable: false,
      m_con_def_api_param: {},
      m_con_adptranApiUrl: '/apidev/ref_adptran_api/v1',
      m_con_api_select_dynamic: '/ref/select_dynamic',
      //-- @constant }
      //-- @property {
      //-- @property }
      //-- @biz-data {
      m_opt_auto_load_schema_list: true,
      m_information_schema_list: [],
      m_information_schema_grid: {'data':[]},
      m_select_dynamic_grid: {'data':[]},
      //-- @biz-data }
      //-- @ui-data {
      m_ui_table_list: [],
      //-- @ui-data }

      //-- @table {
      table_tableSchemaList: {
        fields: [
          { name: 'PK', visible: false },
          { name: 'ORDINAL_POSITION', title: '#', width: '40px', titleClass: 'center-aligned', dataClass: 'center-aligned', },
          { name: 'COLUMN_NAME', title: '이름', width: 'auto', titleClass: 'center-aligned', dataClass: 'center-aligned', },
          { name: 'DATA_TYPE', title: '데이터 유형', width: '15%', titleClass: 'center-aligned', dataClass: 'center-aligned', },
          { name: 'CHARACTER_MAXIMUM_LENGTH', title: '길이', width: '15%', titleClass: 'center-aligned', dataClass: 'center-aligned', },
          { name: 'IS_NULLABLE', title: 'NULL 허용', width: '15%', titleClass: 'center-aligned', dataClass: 'center-aligned', },
          { name: 'COLUMN_DEFAULT', title: '기본값', width: '15%', titleClass: 'center-aligned', dataClass: 'center-aligned', },
        ],
        css: { table: { tableWrapper: 'cid_custom_scrollbar', tableClass: 'cus-vuetable tb_console', }, },
        pageRow: 5,
        table_height_tr: 41,  // -- vuetable-2 tr height(px)
        noDataTemplate: '정보가 없습니다.', // -- vuetable-2 noDataTemplate
        //--##tableSchemaList: [], // -- table data
      }, // table_tableSchemaList{}
      //-- @table }

      //-- @table-#1 {
      table_selectDynamicList: {
        fields: [
          { name: 'PK', visible: false },
          { name: 'no', title: 'NO', width: '5%', titleClass: 'center-aligned', dataClass: 'center-aligned', },
        ],
        css: { table: { tableWrapper: 'cid_custom_scrollbar_yx', tableClass: 'cus-vuetable tb_console', }, pagination: {}, },
        pageRow: 30,
        table_height_tr: 41,  // -- vuetable-2 tr height(px)
        noDataTemplate: '검색 결과가 없습니다.', // -- vuetable-2 noDataTemplate
      }, // table_selectDynamicList{}
      //-- @table-#1 }

      //-- @table-query {
      m_tc_selectDynamicList: 0,
      m_prop_appendParams_selectDynamicList: {},

      //-- vue-binding
      m_model_sel_table: '',
      m_model_txt_select: '',
      m_model_txt_from: '',
      m_model_txt_where: '',
      m_model_txt_groupby: '',
      m_model_txt_orderby: '',
      //-- @table-query }
    } // return{}
  }, // data ()
  computed: {
    computed_api_url_selectDynamicList() {
      return this.m_con_adptranApiUrl + this.m_con_api_select_dynamic;
    },
  },
  created: function() {
    this.$console_log('trace', 'created()');

    this.m_opt_auto_load_schema_list = (this.$sf_obj_val(this.prop_data, 'auto_load_schema_list').toLowerCase() != 'n');
    if (true == this.m_opt_auto_load_schema_list) {
      this.fn_apicall_information_schema_list();
    }
  },
  mounted: function() {
    this.$console_log('trace', 'mounted()');
    if (true == this.m_con_using_vuetable) {
      this.$fn_ui_prepare_sc_vuetable_custom_scrollbar('.cid_custom_scrollbar');
      this.$fn_ui_prepare_sc_vuetable_custom_scrollbar('.cid_custom_scrollbar_yx', { 'axis': 'yx' });
    }
    else {
      let grid_1 = this.$refs.refDataGrid_tableSchemaList;
      let grid_2 = this.$refs.refDataGrid_selectDynamicList;

      let arr_grid = [ grid_1, grid_2 ];  //-- 공통처리

      let c_grid_Font = '12px NanumGothic,sans-serif';
      
      for (let n_ii = 0; n_ii < arr_grid.length; n_ii++) {
        let grid = arr_grid[n_ii];
        //--grid.attributes.allowColumnReordering = true;
        grid.attributes.allowSorting = false;
        grid.attributes.autoResizeColumns = true;
        grid.attributes.columnHeaderClickBehavior = 'select';  //-- sort, select, none
        grid.attributes.editable = false;
        grid.attributes.multiLine = true;
        //--grid.attributes.selectionMode = 'cell',  //-- cell, row
  
        grid.attributes.showClearSettingsOption = false;
        //--grid.attributes.showColumnHeaders = true; 
        //--grid.attributes.showColumnSelector = true;
        //--grid.attributes.showCopy = true;
        grid.attributes.showFilter = false;
        //--grid.attributes.showNewRow = true;
        //--grid.attributes.showOrderByOption = true;
        //--grid.attributes.showOrderByOptionTextAsc = 'Order by %s desc';
        //--grid.attributes.showOrderByOptionTextDesc = 'Order by %s ascending';
        //--grid.attributes.showPaste = false;
        //--grid.attributes.showPerformance = false;
        //--grid.attributes.showRowHeaders = true;
        //--grid.attributes.showRowNumbers = true;
        grid.style = {
          cellFont: c_grid_Font,
          activeCellFont: c_grid_Font,
          columnHeaderCellFont: c_grid_Font,
          contextMenuChildArrowFontSize: c_grid_Font,
          contextMenuFontSize: c_grid_Font,
          editCellFontSize: c_grid_Font,
          rowHeaderCellFont: c_grid_Font,
        };
      }
      
      grid_1.className = 'grid_tableSchemaList';
      grid_1.attributes.allowColumnReordering = false;
      grid_1.attributes.showColumnSelector = false;

      grid_2.className = 'grid_selectDynamicList';
    }
  },
  updated: function() {
    this.$console_log('trace', 'updated()');
  },
  methods: {
    //-- @table {
    fn_dataManager_tableSchemaList(sortOrder, pagination) {
      //-- [i][ref][https://www.vuetable.com/guide/api-vs-data-mode.html#data-mode]
      this.$console_log('trace', 'fn_dataManager_tableSchemaList()', 'sortOrder: ', sortOrder, 'pagination: ', pagination);

      let table_name = this.m_model_sel_table;
      let local = [];

      // sortOrder can be empty, so we have to check for that as well
      /*--
      if (sortOrder.length > 0) {
        this.$console_log('trace', 'fn_dataManager_tableSchemaList()', 'sortField: ', sortOrder[0].sortField, 'direction: ', sortOrder[0].direction);
        local = _.orderBy(local, sortOrder[0].sortField, sortOrder[0].direction);
      }
      --*/

      local = this.m_information_schema_list.filter((item_schema) => {
        let txt_filter = this.m_model_sel_table.toLowerCase();
        return (item_schema['TABLE_NAME'].toLowerCase() == txt_filter)
      });

      return local;
    },
    //-- @table }
    //-- @table-#1 {
    onPaginationData_selectDynamicList(paginationData) {
      this.$console_log('watch', 'onPaginationData_selectDynamicList.', 'paginationData: ', paginationData);
      this.$refs.refPagination_selectDynamicList.setPaginationData(paginationData)
    },
    onChangePage_selectDynamicList(page) {
      this.$refs.refTable_selectDynamicList.changePage(page)
    },
    prop_transform_selectDynamicList(data) {
      this.$console_log('watch', 'prop_transform_selectDynamicList.', 'data: ', data);
      try {
        let isSuccess = (200 == data['resultCode']);  //-- success
        if (true == isSuccess) {
          let table = this.$refs.refTable_selectDynamicList;

          let total = this.$sf_int(data['totalCount'], 0);
          let perPage = this.$sf_int(data['pageSize'], 0);
          let currentPage = Math.max(this.$sf_int(data['currentPage'], 0), 1);
          let baseIndex = ((currentPage - 1) * perPage);

          data = (data['data']||{});

          this.m_tc_selectDynamicList = total;

          this.table_selectDynamicList.fields = [
            { name: 'PK', visible: false },
            { name: 'no', title: '#', width: '40px', titleClass: 'center-aligned', dataClass: 'center-aligned', },
          ];

          let mydata = (data||[]);
          if (mydata.length > 0) {
            for (let column in mydata[0]) {
              this.table_selectDynamicList.fields.push(
                { name: column, title: column, titleClass: 'center-aligned', dataClass: 'center-aligned', }
              );
            }
          }
          
          mydata.forEach((element, index) => {
            element.PK = baseIndex + index;
            element.no = (baseIndex + index + 1);
          });

          let mypagination = table.makePagination(total, perPage, currentPage);
          return { 'mydata': mydata, 'mypagination': mypagination };
        }
        this.$proc_api_returnCd_Fail(data);
      }
      catch (e) {
        this.$console_log('error', 'prop_transform_selectDynamicList.', 'e: ', e);
      }
      return { 'mydata': [], 'mypagination': {} };
    },
    //-- override vuetable default queryParam
    prop_queryParams_selectDynamicList(sortOrder, currentPage, perPage) {
      let param = { 'pz': this.table_selectDynamicList.pageRow, 'pg': currentPage, 'tc': this.m_tc_selectDynamicList };
      param = Object.assign(this.m_con_def_api_param, param)
      return param;
    },
    //-- @table-#1 }
    //-- @handler {
    onchange_sel_table() { //-- table select
      if (true == this.m_con_using_vuetable) {
        this.$refs.refTable_tableSchemaList.refresh();
      }
      else {
        let table_name = this.m_model_sel_table.toLowerCase();
        let local = [];

        this.m_information_schema_list.forEach((item_schema) => {
          if  (item_schema['TABLE_NAME'].toLowerCase() == table_name) {
            local.push({
              '이름': item_schema['COLUMN_NAME'],
              '데이터 유형': item_schema['DATA_TYPE'],
              '길이': item_schema['CHARACTER_MAXIMUM_LENGTH'],
              'NULL 허용': item_schema['IS_NULLABLE'],
              '기본값': item_schema['COLUMN_DEFAULT'],
            });
          }
        });
        this.m_information_schema_grid['data'] = local;

        //--##let grid = this.$refs.refDataGrid_tableSchemaList;
        //--##grid.resetColumnWidths();
        //--##grid.resetRowHeights();
      }
    },
    onclick_sel_table_apply() {
      let arr_select = [];
      if (true == this.m_con_using_vuetable) {
        let tableData = this.$refs.refTable_tableSchemaList.tableData;

        tableData.forEach((data_item) => {
          arr_select.push(data_item['COLUMN_NAME']);
        });
      }
      else {
        let gridData = this.m_information_schema_grid['data'];

        gridData.forEach((data_item) => {
          arr_select.push(data_item['이름']);
        });
      }

      this.m_model_txt_select = arr_select.join(', ');
      this.m_model_txt_from = this.m_model_sel_table;
      this.m_model_txt_where = '';
      this.m_model_txt_groupby = '';
      this.m_model_txt_orderby = '';
    },
    //-- 검색click
    onclick_search_selectDynamicList() {
      if (this.m_con_using_vuetable == true) {
        this.fn_search_selectDynamicList(this.m_model_txt_select, this.m_model_txt_from, this.m_model_txt_where, this.m_model_txt_groupby, this.m_model_txt_orderby);
      }
      else {  
        this.fn_apicall_dymanic_query_list(this.m_model_txt_select, this.m_model_txt_from, this.m_model_txt_where, this.m_model_txt_groupby, this.m_model_txt_orderby);
      }
    },
    onclick_row_action(cmd, rowData) {
      this.$console_log('trace', 'onclick_row_action()', 'cmd: ', cmd, 'rowData: ', rowData);
      if ('e.g' == cmd) {
      }
    },
    //-- @handler }
    //-- @api function {
    // -- api 호출
    $fn_call_api(api_path, api_request, method, fn_callback) {
      this.$console_log('watch', '$fn_call_api.', 'api_path: ', api_path, 'api_request: ', api_request, 'method: ', method, 'fn_callback: ', fn_callback);
      
      fn_callback = ((typeof(fn_callback) == 'function') ? fn_callback : (function() {}));

      let success = ((response) => {
        this.$console_log('watch', '$fn_call_api().', 'success', 'response: ', response);

        let result = response['data'];

        let isSuccess = (200 == result['resultCode']);  //-- success
        if (true == isSuccess) {
          fn_callback('ok', response, api_request, result['data']);
        }
        else {
          this.$console_log('watch', '$fn_call_api().', '$isApiSuccess() == false', 'response: ', response);
          fn_callback('nk', response, api_request);
        }
      });
      let failed = ((response) => {
        this.$console_log('watch', '$fn_call_api().', 'failed', 'response: ', response);
        fn_callback('failed', response, api_request);
      });
      let catched = ((error) => {
        this.$console_log('watch', '$fn_call_api().', 'catched', 'error: ', error);
        fn_callback('catched', error, api_request);
      });

      let o_option = (('get' == method) ? {'method':'get'} : {});
      this.$getHttpResponse(this.m_con_adptranApiUrl + api_path, method, api_request, o_option).then(success, failed).catch(error => catched(error));
    },

    //-- information_schema query
    fn_apicall_information_schema_list() {
      this.$console_log('trace', 'fn_apicall_information_schema_list()');

      //-- 초기화
      this.m_information_schema_list = [];
      this.m_ui_table_list = [];

      let url = this.m_con_api_select_dynamic;
      let param = {
        'select': 'TABLE_NAME, ORDINAL_POSITION, COLUMN_NAME, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH, IS_NULLABLE, COLUMN_DEFAULT',
        'from': 'INFORMATION_SCHEMA.COLUMNS',
        'where': '',
        'orderby': 'TABLE_NAME, ORDINAL_POSITION',
      };
      this.$fn_call_api(url, Object.assign(this.m_con_def_api_param, param), 'post', this.fn_apicb_common_information_schema);
    },
    //-- api callback common_information_schema_list
    fn_apicb_common_information_schema(ret, response, request, payload) {
      this.$console_log('trace', 'common_information_schema_list()', 'ret: ', ret, 'response: ', response, 'request: ', request, 'payload: ', payload);
      //-- ret: 'ok', 'nk', 'failed', 'catched'
      if ('ok' == ret) {
        let data_list = payload;
        if (Array.isArray(data_list) == false) {
          throw {'message': 'common_information_schema_list: payload is not array'};
          return;
        }

        data_list.forEach((data_item) => {
          this.m_information_schema_list.push(data_item);
          let table_name = data_item['TABLE_NAME'];
          if (this.m_ui_table_list.indexOf(table_name) == -1) {
            this.m_ui_table_list.push(table_name);
          }
        });
      }
      else {
        this.$proc_api_resultCode_Fail(ret, response);
      }
    },

    //-- dynamic query
    fn_apicall_dymanic_query_list(q_select, q_from, q_where, q_groupby, q_orderby) {
      this.$console_log('trace', 'fn_apicall_dymanic_query_list()');

      let url = this.m_con_api_select_dynamic;
      let param = { 'select': q_select, 'from': q_from, 'where': q_where, 'groupby': q_groupby, 'orderby': q_orderby, }; 
      this.$fn_call_api(url, Object.assign(this.m_con_def_api_param, param), 'post', this.fn_apicb_common_dymanic_query);
    },
    //-- api callback common_dymanic_query_list
    fn_apicb_common_dymanic_query(ret, response, request, payload) {
      this.$console_log('trace', 'common_dymanic_query_list()', 'ret: ', ret, 'response: ', response, 'request: ', request, 'payload: ', payload);
      //-- ret: 'ok', 'nk', 'failed', 'catched'
      if ('ok' == ret) {
        let data_list = payload;
        if (Array.isArray(data_list) == false) {
          throw {'message': 'common_dymanic_query_list: payload is not array'};
          return;
        }
        this.m_tc_selectDynamicList = data_list.length;
        //--##((typeof(fn_tgrid_load_sheet_data) != 'undefined') ? this.$sf_func_call(fn_tgrid_load_sheet_data, data_list) : void(0)); //-- display sheet

        this.m_select_dynamic_grid['data'] = data_list;
        //-- [i][cmt][select의 함수사용 ,에 대한 처리수용이 안됨
        /*--[ref]
        let arr_select = (request['select']||'').split(',');
        let obj_select = arr_select.reduce((accumulator, currentValue) => {
          accumulator[currentValue.trim()] = null;
          return accumulator;
        }, {});

        let local = [];
        //-- for column order
        data_list.forEach((item_schema) => {
          let obj = Object.assign({}, obj_select);
          for (let item_data in obj) {
            obj[item_data] = item_schema[item_data];
          }
          local.push(obj);
        });
        this.m_select_dynamic_grid['data'] = local;
        --*/

        //--##let grid = this.$refs.refDataGrid_selectDynamicList;
        //--##grid.resetColumnWidths();
        //--##grid.resetRowHeights();

      }
      else {
        this.$proc_api_resultCode_Fail(ret, response);
      }
    },
    //-- @api function }

    //-- @function {
    fn_search_selectDynamicList(q_select, q_from, q_where, q_groupby, q_orderby) {
      //-- 초기화
      this.m_tc_selectDynamicList = 0;

      //-- prepare query parameter
      this.m_prop_appendParams_selectDynamicList = { 'select': q_select, 'from': q_from, 'where': q_where, 'groupby': q_groupby, 'orderby': q_orderby, };
      this.$nextTick(() => { this.$refs.refTable_selectDynamicList.refresh(); });
    },
    //-- @function }
  } // methods:
};
</script>

<template>
  <section>
    <div class="header"></div>

    <div class="conBox">
      <div id="content">

        <div class="comm_wrap">
          <div class="wrap_left">
            <div class="searching_wrap">
              TABLE: 
              <span class="combo_box">
                <select v-model="m_model_sel_table" @change="onchange_sel_table()">
                  <option v-for="option in m_ui_table_list" :value="(option)">{{option}}</option>
                </select>
              </span>

              <button v-show="(m_information_schema_list.length == 0)" type="button" class="btn-lg btn_searching" @click.stop="fn_apicall_information_schema_list()"><span>TABLE검색</span></button>
              <button type="button" class="btn-lg btn_searching" @click.stop="onclick_sel_table_apply()"><span>적용</span></button>
            </div><!-- .searching_wrap -->

            <div class="table_box_wrap table_schemalist">

              <template v-if="(true == m_con_using_vuetable)">
                <cus-vuetable ref="refTable_tableSchemaList" :fields="table_tableSchemaList.fields" :css="table_tableSchemaList.css.table"
                  :noDataTemplate="table_tableSchemaList.noDataTemplate"
                  :table-height="((table_tableSchemaList.table_height_tr * table_tableSchemaList.pageRow) + 'px')"
                  :api-mode="false"
                  :dum-data="table_tableSchemaList.tableSchemaList"
                  :data-manager="fn_dataManager_tableSchemaList"
                  pagination-path=""
                >
                </cus-vuetable>
              </template>
              <template v-else>
                <canvas-datagrid ref="refDataGrid_tableSchemaList" v-bind.prop="m_information_schema_grid"></canvas-datagrid>
              </template>

            </div><!-- .table_box_wrap -->
          </div><!-- .wrap_left -->

          <div class="wrap_right">
            <div class="searching_wrap">
              <div>
                <ul class="search_txt_ul">
                  <li><span class="title">SELECT:  </span><span class="text"><input type="text" v-model="m_model_txt_select"  title="select"></span></li>
                  <li><span class="title">FROM:    </span><span class="text"><input type="text" v-model="m_model_txt_from"    title="from"></span></li>
                  <li><span class="title">WHERE:   </span><span class="text"><input type="text" v-model="m_model_txt_where"   title="where"></span></li>
                  <li><span class="title">GROUPBY: </span><span class="text"><input type="text" v-model="m_model_txt_groupby" title="groupby"></span></li>
                  <li><span class="title">ORDERBY: </span><span class="text"><input type="text" v-model="m_model_txt_orderby" title="orderby"></span></li>
                </ul>
              </div>
            </div><!-- .searching_wrap -->
            <button type="button" class="btn-lg btn_searching" @click.stop="onclick_search_selectDynamicList()"><span>검색</span></button>
          </div><!-- .wrap_right -->

        </div><!-- .comm_wrap -->

        <div class="table_box_wrap mt50">
          <div class="table_head">
            <div class="top_left queryboard_list">
              <div class="listBox popup_table">
                <h5 class="queryboard_list_title">전체 : {{m_tc_selectDynamicList}} 건</h5>
              </div>
            </div>
            <div class="top_right queryboard_list">
              <div class="listBox popup_table">
                <div class="queryboard_list_title">
                  <ol class="thislegend"></ol>
                </div>
              </div>
            </div>
          </div>

          <template v-if="(true == m_con_using_vuetable)">
            <cus-vuetable ref="refTable_selectDynamicList" :fields="table_selectDynamicList.fields" :css="table_selectDynamicList.css.table"
              :noDataTemplate="table_selectDynamicList.noDataTemplate" data-path="mydata" pagination-path="mypagination" track-by="PK"
              :table-height="((table_selectDynamicList.table_height_tr * table_selectDynamicList.pageRow) + 'px')"
              :api-mode="true" :load-on-start="false" :api-url="computed_api_url_selectDynamicList" http-method="post" :http-options="m_axios_options"
              :transform="prop_transform_selectDynamicList" :queryParams="prop_queryParams_selectDynamicList" :appendParams="m_prop_appendParams_selectDynamicList"
              @vuetable:pagination-data="onPaginationData_selectDynamicList"
            >
            </cus-vuetable>
            <cus-vuetable-pagination ref="refPagination_selectDynamicList" :css="table_selectDynamicList.css.pagination" :on-each-side="4" @vuetable-pagination:change-page="onChangePage_selectDynamicList" />
          </template>
          <template v-else>
            <div id="id_canvas_datagrid"></div>
            <canvas-datagrid ref="refDataGrid_selectDynamicList" v-bind.prop="m_select_dynamic_grid"></canvas-datagrid>
          </template>

        </div><!-- .table_box_wrap -->
      
      </div><!-- #content -->
    </div><!-- .conBox -->

    <modal-dialog/>
    <!--// <div>{{m_vue_id}}</div> //-->
  </section>
</template>

<style scoped>
@import './../css/cus-vuetable.css';
</style>
<style>
.header { height: 107px; }

.tb_console {width:100%;border-top:2px solid #333333;font-size:13px;table-layout:fixed;}
.tb_console thead th ,
.tb_console tbody th {padding:10px 0;background:#f6f6f6;border-bottom:1px solid #333333;border-left:1px solid #e5e5e5;color:#222222;font-weight:700;}
.tb_console thead th:first-child ,
.tb_console tbody th:first-child {border-left:none;}
.tb_console tbody th.tit_th {border-left:1px solid #e5e5e5;background:#fbfafa;}
.tb_console tbody th {padding:10px 0;border-bottom:1px solid #e5e5e5;border-left:1px solid #e5e5e5;color:#666666;}
.tb_console tbody td {padding:10px;border-bottom:1px solid #e5e5e5;border-left:1px solid #e5e5e5;color:#666666;text-align:center;}
.tb_console tbody td:first-child {border-left:none;}

.table_box_wrap .table_head {overflow: hidden; margin-bottom: 10px;}
.table_box_wrap .table_head .top_left {float: left;}
.table_box_wrap .table_head .top_right {float: right;}
.table_box_wrap .queryboard_list {margin-top: 0;}
.table_box_wrap .queryboard_list .listBox {margin-bottom: 0; min-height: auto; padding-right: 0; width: 100%;}
.table_box_wrap .queryboard_list .listBox .queryboard_list_title {font-size: 16px; border-bottom: 0; padding: 0 10px;}
.table_box_wrap .top_left.queryboard_list .listBox .queryboard_list_title {height: 34px; line-height: 34px;}
.table_box_wrap .top_right.queryboard_list .listBox .queryboard_list_title {height: 34px; line-height: 34px; /*background-color: #f6f6f6;*/}

.table_box_wrap .queryboard_list .listBox .queryboard_list_title .thislegend {position: unset; display: inline-block;}
.table_box_wrap .top_right.queryboard_list .listBox .queryboard_list_title .thislegend li {font-size: 13px; color:#757575;}
.table_box_wrap .top_right.queryboard_list .listBox .queryboard_list_title .thislegend li:before {width: 15px; height: 15px; background-size: 15px;}

.table_box_wrap.table_schemalist { }

.comm_wrap { display: flex; }
.comm_wrap .wrap_left { width: 50%; padding: 0 10px; }
.comm_wrap .wrap_right { width: 50%; padding: 0 10px; }

.search_txt_ul {  }
.search_txt_ul li { height: 45px; }
.search_txt_ul li .title { width: 15%; }
.search_txt_ul li .text { width: 85%; }
.search_txt_ul input { height: 40px; }

#id_canvas_datagrid { width: 100%; }

.grid_tableSchemaList { width: 100%; height: 266px; }
.grid_selectDynamicList { width: 100%; height: 518px; }
</style>
