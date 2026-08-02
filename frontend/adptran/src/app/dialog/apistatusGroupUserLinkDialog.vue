<comment>
  popup-[API상태그룹사용자설정]
</comment>
<script>
import { CommonMixin } from '@/common/adptran.js';
import { ApistatusCommonMixin } from '@/common/apistatus_common.js';

export default {
  name: 'apistatusGroupUserLinkDialog',
  props: {
    prop_name: {
      type: String,
      required: true,
    },
    prop_Data: {
      type: Object,
      required: true,
      default: (() => ({ 'group_link_count': 5, 'api_status_spc_group_list': [] })),
      validator: function(value) {
        if ((parseInt(value['group_link_count'], 10) > 0) == false) { return false; }
        if (Array.isArray(value['api_status_spc_group_list']) == false) { return false; }
        return true;
      }
    },
  },
  mixins: [CommonMixin, ApistatusCommonMixin],
  data() {
    return {
      m_vue_id: 'apistatusGroupUserLinkDialog',
      //-- @constant {
      //-- @constant }
      //-- @biz-data {
      m_con_group_summary_count: 5,  //-- 출력API상태갯수
      //-- @biz-data }
      //-- @ui-data {
      m_ui_api_status_spc_group_user_link_list: [], //-- user선택
      m_ui_api_status_spc_group_list: [], //-- group목록
      m_model_user_link: '',  //-- 선택item
      //-- @ui-data }
    } // return{}
  }, // data()
  computed: {
  },
  watch: {
  },
  created: function () {
    this.$console_log('trace', 'created()');
    this.m_con_group_summary_count = this.prop_Data['group_link_count']; 
    this.prop_Data['api_status_spc_group_list'].forEach((api_status_spc_group) => {
      let b_is_user_link = ('Y' == api_status_spc_group['user_link_yn']);
      if (true == b_is_user_link) {
        this.m_ui_api_status_spc_group_user_link_list.push(api_status_spc_group);
      }
      else {
        this.m_ui_api_status_spc_group_list.push(api_status_spc_group);
      }
    });
    //--[i] status_group_nm의 정렬이 query정렬과는 다름(영문,한글 비교순서 다름)
    this.m_ui_api_status_spc_group_list = this.fn_sort_api_status_spc_group_list(this.m_ui_api_status_spc_group_list);
  },
  mounted: function () {
    this.$console_log('trace', 'mounted()');
    this.fn_ui_reset_rdo_user_link();
  },
  updated: function () {
    this.$console_log('trace', 'updated()');
  },
  methods: {
    //-- @handler {
    onclick_del_group_item(idx) {
      let len = this.m_ui_api_status_spc_group_user_link_list.length;
      if ((idx < 0) || (idx >= len)) { return; }
      let api_status_spc_group = this.m_ui_api_status_spc_group_user_link_list[idx];

      this.m_ui_api_status_spc_group_user_link_list.splice(idx, 1);
      this.m_ui_api_status_spc_group_list.push(api_status_spc_group);
      this.m_ui_api_status_spc_group_list = this.fn_sort_api_status_spc_group_list(this.m_ui_api_status_spc_group_list);
      this.fn_ui_reset_rdo_user_link();
    },
    onclick_add_group_item(idx) {
      let len = this.m_ui_api_status_spc_group_list.length;
      if ((idx < 0) || (idx >= len)) { return; }
      let api_status_spc_group = this.m_ui_api_status_spc_group_list[idx];
      this.m_ui_api_status_spc_group_list.splice(idx, 1);
      this.m_ui_api_status_spc_group_list = this.fn_sort_api_status_spc_group_list(this.m_ui_api_status_spc_group_list);
      this.m_ui_api_status_spc_group_user_link_list.push(api_status_spc_group);
      this.fn_ui_reset_rdo_user_link();
    },
    onclick_item_down() {
      if (this.m_model_user_link.length == 0) {
        this.$adpt_alert('선택된 정보가 없습니다.', '알림', false); return;
      }
      if (this.m_ui_api_status_spc_group_user_link_list.length < 2) { return; }

      let find_idx = this.fn_findIndex_api_status_spc_group_user_link_list(this.m_model_user_link);
      if ((find_idx == -1) || (find_idx == (this.m_ui_api_status_spc_group_user_link_list.length - 1))) { return; }

      let item_save = this.m_ui_api_status_spc_group_user_link_list[find_idx + 1];
      this.m_ui_api_status_spc_group_user_link_list[find_idx + 1] = this.m_ui_api_status_spc_group_user_link_list[find_idx];
      this.m_ui_api_status_spc_group_user_link_list[find_idx] = item_save;
      this.m_ui_api_status_spc_group_user_link_list.splice();
    },
    onclick_item_up() {
      if (this.m_model_user_link.length == 0) {
        this.$adpt_alert('선택된 정보가 없습니다.', '알림', false); return;
      }
      if (this.m_ui_api_status_spc_group_user_link_list.length < 2) { return; }

      let find_idx = this.fn_findIndex_api_status_spc_group_user_link_list(this.m_model_user_link);
      if ((find_idx == -1) || (find_idx == 0)) { return; }

      let item_save = this.m_ui_api_status_spc_group_user_link_list[find_idx - 1];
      this.m_ui_api_status_spc_group_user_link_list[find_idx - 1] = this.m_ui_api_status_spc_group_user_link_list[find_idx];
      this.m_ui_api_status_spc_group_user_link_list[find_idx] = item_save;
      this.m_ui_api_status_spc_group_user_link_list.splice();
    },
    onclick_save_group() {
      let arr_api_status_spc_group_user_link = [];
      this.m_ui_api_status_spc_group_user_link_list.forEach((api_status_spc_group, idx) => {
        arr_api_status_spc_group_user_link.push({
          'status_group_no': api_status_spc_group['status_group_no'],
          'sort_odrg': (idx + 1),
        });
      }); 
    
      let param = { 'api_status_spc_group_user_link_list': arr_api_status_spc_group_user_link };
      this.fn_call_api_group_tran_status_spc_group_user_link(param);
    },
    //-- @handler }

    //-- @api function {
    //-- api 사용자그룹설정 저장
    fn_call_api_group_tran_status_spc_group_user_link(param) {
      this.$console_log('trace', 'fn_call_api_group_tran_status_spc_group_user_link()', 'param: ', param);

      let url = this.m_con_apistatus_group_tran_status_spc_group_user_link_url;
      this.$fn_call_apistatus_api(url, Object.assign(this.m_con_def_api_param, param), 'post', this.fn_apicb_api_group_tran_status_spc_group_user_link);
    },
    //-- api 사용자그룹설정 저장 후처리
    fn_apicb_api_group_tran_status_spc_group_user_link(call_ret, response, api_request, result_data) {
      this.$console_log('trace', 'fn_apicb_api_group_tran_status_spc_group_user_link()', 'call_ret: ', call_ret, 'response :', response, 'api_request: ', api_request, 'result_data: ', result_data);
      
      let b_is_succ = false;
      //-- call_ret: 'ok', 'nk', 'failed', 'catched'
      let s_msg = '';
      if ('ok' == call_ret) {
        if (result_data['resultCd'] == 200) { //-- data: {resultCd: 200, resultMsg: 'xxx'}
          s_msg = '저장 하였습니다.';
          b_is_succ = true;
        }
        else {
          s_msg = this.$fmt_response_messge('저장에 실패하였습니다.', result_data['resultCd'], result_data['resultMsg']);
        }
      }
      else if ('nk' == call_ret) {
        s_msg = this.$fmt_response_messge('저장에 실패하였습니다.', response['resultCode'], response['resultMessage']);
      }
      else if ('failed' == call_ret) {
        let res_message = response['message'];
        let res_stack = response['stack'];
        let sub_response_status = response['response']['status']; //-- 404
        //--@@let sub_response_statusText = response['response']['statusText']; //-- ''
        s_msg = this.$fmt_response_messge('저장시 오류가 발생했습니다.', sub_response_status, res_message);
      }
      else if ('catched' == call_ret) {
        let errorMessage = response['message'];
        s_msg = this.$fmt_response_messge('저장시 예외가 발생했습니다.', '', errorMessage);
      }
      else {
        s_msg = '저장시  정의되지 않은 응답값을 수신 하였습니다.\n\n[call_ret: ' + call_ret + ']';
      }
      this.$adpt_alert(s_msg, '', false);

      if (true == b_is_succ) { 
        let ret_data = { 'api_status_spc_group_user_link_list': this.m_ui_api_status_spc_group_user_link_list };
        this.$emit('emit_proc_finished_save', ret_data);
      }
    },
    //-- @api function }

    //-- @function {
    fn_sort_api_status_spc_group_list(arr_api_status_spc_group_list) {
      arr_api_status_spc_group_list.sort(function(item_a, item_b) {
        let order_a = item_a['sort_odrg'];
        let order_b = item_b['sort_odrg'];
        let ret = ((order_a < order_b) ? -1 : ((order_a > order_b) ? 1 : 0));
        if (ret != 0) { return ret; } 
        
        order_a = item_a['status_group_nm'];
        order_b = item_b['status_group_nm'];
        ret = ((order_a < order_b) ? -1 : ((order_a > order_b) ? 1 : 0));
        if (ret != 0) { return ret; } 
  
        order_a = item_a['status_group_no'];
        order_b = item_b['status_group_no'];
        ret = ((order_a < order_b) ? -1 : ((order_a > order_b) ? 1 : 0));
        return ret;
      });
      return arr_api_status_spc_group_list;
    },
    fn_findIndex_api_status_spc_group_user_link_list(status_group_no) {
      let find_idx = this.m_ui_api_status_spc_group_user_link_list.findIndex(function(elem, idx) {
        return (elem['status_group_no'] == status_group_no);
      });
      return find_idx;
    },
    fn_ui_reset_rdo_user_link() {
      if (this.m_model_user_link.length > 0) {
        let find_idx = this.fn_findIndex_api_status_spc_group_user_link_list(this.m_model_user_link);
        this.m_model_user_link = ((find_idx == -1) ? '' : this.m_ui_api_status_spc_group_user_link_list[find_idx]['status_group_no']);
      }
      if (this.m_model_user_link.length == 0) {
        this.m_model_user_link = ((this.m_ui_api_status_spc_group_user_link_list.length > 0) ? this.m_ui_api_status_spc_group_user_link_list[0]['status_group_no'] : '');
      }
    },
    fn_dialog_close() {
      this.$modal.hide(this.prop_name);
    },
    //-- @function }
  } // methods:
};
</script>

<template>
  <div class="popup_wrap">
    <div class="popup_header">
      <span class="popup_header_title">API상태그룹 사용자 설정</span>
      <button type="button" class="popup_header_button" role="button" @click.stop="fn_dialog_close()" title="Close">
        <span class="button_close">Close</span>
      </button>
    </div>
    <div class="pop_ver pop_ver2">
      <div class="popup_content distribution_cont">

        <div class="user_link_wrap">
          <div class="left_cont">
            <div class="pkg_board">
              <table class="table-list popup_table">
                <caption>사용자 선택 API상태그룹 Table</caption>
                <colgroup>
                  <col style="width:30px;">
                  <col style="width:30px;">
                  <col style="width:auto;">
                </colgroup>
                <thead>
                  <tr>
                    <th><a class="btn_item_down" href="javascript:void(0)" @click.stop="onclick_item_down()"><span>아래로</span></a></th>
                    <th><a class="btn_item_up" href="javascript:void(0)" @click.stop="onclick_item_up()"><span>위로</span></a></th>
                    <th>그룹명</th>
                  </tr>
                </thead>
              </table><!-- .table-list popup_table -->
            </div><!-- .pkg_board -->
            <div class="scroll_wrap">
              <div class="pkg_board">
                <table class="table-list popup_table">
                  <caption> API상태그룹  Table</caption>
                  <colgroup>
                    <col style="width:60px">
                    <col style="width:auto;">
                    <col style="width:60px;">
                  </colgroup>
                  <tbody>
                    <tr v-for="(spc_group_item, index) in m_ui_api_status_spc_group_user_link_list" :key="index">
                      <td>
                        <a href="javascript:void(0)">
                          <input type="radio" :id="('id_rdo_user_link_' + index)" name="rdo_user_link" title="목록선택" :value="spc_group_item['status_group_no']" v-model="m_model_user_link">
                          <label :for="('id_rdo_user_link_' + index)"><span></span></label>
                        </a>
                      </td>
                      <td>{{spc_group_item['status_group_nm']}}</td>
                      <td>
                        <a class="btn_item_del" href="javascript:void(0)" @click.stop="onclick_del_group_item(index)"><span>삭제</span></a>
                      </td>
                    </tr>
                  </tbody>
                </table><!-- .table-list popup_table -->
              </div><!-- .pkg_board -->
            </div><!-- .scroll_wrap -->

          </div><!-- .left_cont -->

          <div class="center_cont">

          </div><!-- .center_cont -->

          <div class="right_cont">

            <div class="pkg_board">
              <table class="table-list popup_table">
                <caption>사용자 선택 API상태그룹 Table</caption>
                <colgroup>
                  <col style="width:auto;">
                  <col style="width:60px;">
                </colgroup>
                <thead>
                  <tr>
                    <th>그룹명</th>
                    <th></th>
                  </tr>
                </thead>
              </table><!-- .table-list popup_table -->
            </div><!-- .pkg_board -->
            <div class="scroll_wrap">
              <div class="pkg_board">
                <table class="table-list popup_table">
                  <caption>API상태점검이력목록 Table</caption>
                  <colgroup>
                    <col style="width:auto;">
                    <col style="width:60px;">
                  </colgroup>
                  <tbody>
                    <tr v-for="(spc_group_item, index) in m_ui_api_status_spc_group_list" :key="index">
                      <td>{{spc_group_item['status_group_nm']}}</td>
                      <td>
                        <a class="btn_item_add" href="javascript:void(0)" @click.stop="onclick_add_group_item(index)"><span>추가</span></a>
                      </td>
                    </tr>
                  </tbody>
                </table><!-- .table-list popup_table -->
              </div><!-- .pkg_board -->
            </div><!-- .scroll_wrap -->

          </div><!-- .right_cont -->
          
        </div><!-- .user_link_wrap -->

        <div class="brd_tp process_btn">
          <button type="button" title="닫기" class="btn btn_sml" @click.stop="fn_dialog_close()">닫기</button>
          <button type="button" title="저장" class="btn btn_gray btn_sml" @click.stop="onclick_save_group()">저장</button>
          
        </div>
        
      </div><!-- .popup_content -->
    </div><!--.pop_ver pop_ver2 -->
  </div><!-- .popup_wrap -->
</template>

<style scoped>
  /*add for local*/
  /*.distribution_cont {height: calc(100% - 35px - 35px);}*//*100% - padding - 버튼area*/
  .distribution_cont {height: 100%;}

  .process_btn {text-align: left;}
  .process_btn > button {position:relative;left:-120px; margin-right: 10px;}
</style>
