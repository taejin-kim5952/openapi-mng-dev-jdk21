//-- apistatus_list.js {
import Vue from 'vue'
import apistatus_list from './apistatus_list.vue'

var g_vm = new Vue({
  el: '#vid_vue_page_mount_apistatus',
  template: '<vue-apistatus-list ref="vid_vue_page_mount_apistatus" />',
  components: { 'vue-apistatus-list': apistatus_list },  
  //-- render: h => h(apistatus_list),
});
//-- for access javascript
console.log('[o-o][apistatus_list.js][before window[g_vm_comp]: %o', window['g_vm_comp']);
window['g_vm_comp'] = Object.assign({ 'apistatus_list': g_vm.$refs.vid_vue_page_mount_apistatus }, window['g_vm_comp']);
console.log('[o-o][apistatus_list.js][after window[g_vm_comp]: %o', window['g_vm_comp']);

//-- apistatus_list.js }
