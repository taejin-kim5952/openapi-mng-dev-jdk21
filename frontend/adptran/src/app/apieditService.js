//-- apieditService.js {
import Vue from 'vue'
import apieditService from './apieditService.vue'

var g_vm = new Vue({
  el: '#vid_apieditService',
  template: '<vue-apiedit-service ref="vid_apieditService" />',
  components: { 'vue-apiedit-service': apieditService },  
  //-- render: h => h(apieditService ),
});
//-- for access javascript
console.log('[o-o][apieditService.js][before window[g_vm_comp]: %o', window['g_vm_comp']);
window['g_vm_comp'] = Object.assign({ 'apieditService': g_vm.$refs.vid_apieditService }, window['g_vm_comp']);
console.log('[o-o][apieditService.js][after window[g_vm_comp]: %o', window['g_vm_comp']);
//-- apieditService.js }
