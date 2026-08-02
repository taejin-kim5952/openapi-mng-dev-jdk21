//-- egPartCase.js {
import Vue from 'vue'
import egPartCase from './egPartCase.vue'

var g_vm = new Vue({
  el: '#vid_egPartCase',
  template: '<vue-eg-part-case ref="vid_egPartCase" />',
  components: { 'vue-eg-part-case': egPartCase },  
  //-- render: h => h(egPartCase ),
});
//-- for access javascript
console.log('[o-o][egPartCase.js][before window[g_vm_comp]: %o', window['g_vm_comp']);
window['g_vm_comp'] = Object.assign({ 'egPartCase': g_vm.$refs.vid_egPartCase }, window['g_vm_comp']);
console.log('[o-o][egPartCase.js][after window[g_vm_comp]: %o', window['g_vm_comp']);
//-- egPartCase.js }
