//-- kosXlsxService.js {
import Vue from 'vue'
import kosXlsxService from './kosXlsxService.vue'

var g_vm = new Vue({
  el: '#vid_kosXlsxService',
  template: '<vue-kos-xlsx-service ref="vid_kosXlsxService" />',
  components: { 'vue-kos-xlsx-service': kosXlsxService },  
  //-- render: h => h(kosXlsxService ),
});
//-- for access javascript
console.log('[o-o][kosXlsxService.js][before window[g_vm_comp]: %o', window['g_vm_comp']);
window['g_vm_comp'] = Object.assign({ 'kosXlsxService': g_vm.$refs.vid_kosXlsxService }, window['g_vm_comp']);
console.log('[o-o][kosXlsxService.js][after window[g_vm_comp]: %o', window['g_vm_comp']);
//-- kosXlsxService.js }
