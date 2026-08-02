//-- adptranService.js {
import Vue from 'vue'
import adptranService from './adptranService.vue'

var g_vm = new Vue({
  el: '#vid_adptranService',
  template: '<vue-adptran-service ref="vid_adptranService" />',
  components: { 'vue-adptran-service': adptranService },  
  //-- render: h => h(adptranService ),
});
//-- for access javascript
console.log('[o-o][adptranService.js][before window[g_vm_comp]: %o', window['g_vm_comp']);
window['g_vm_comp'] = Object.assign({ 'adptranService': g_vm.$refs.vid_adptranService }, window['g_vm_comp']);
console.log('[o-o][adptranService.js][after window[g_vm_comp]: %o', window['g_vm_comp']);
//-- adptranService.js }
