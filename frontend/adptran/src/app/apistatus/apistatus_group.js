//-- apistatus_group.js {
import Vue from 'vue'
import apistatus_group from './apistatus_group.vue'

var g_vm = new Vue({
  el: '#vid_vue_page_mount_apistatus',
  template: '<vue-apistatus-group ref="vid_vue_page_mount_apistatus" />',
  components: { 'vue-apistatus-group': apistatus_group },
  //-- render: h => h(apistatus_group),
});
//-- for access javascript
console.log('[o-o][apistatus_group.js][before window[g_vm_comp]: %o', window['g_vm_comp']);
window['g_vm_comp'] = Object.assign({ 'apistatus_group': g_vm.$refs.vid_vue_page_mount_apistatus }, window['g_vm_comp']);
console.log('[o-o][apistatus_group.js][after window[g_vm_comp]: %o', window['g_vm_comp']);

//-- apistatus_group.js }
