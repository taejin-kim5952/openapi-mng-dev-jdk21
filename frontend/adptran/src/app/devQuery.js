//-- devQuery.js {
import Vue from 'vue'
import devQuery from './devQuery.vue'

Vue.config.ignoredElements = ['canvas-datagrid'];

// Utility to escape attribute names and values for HTML
function escapeHTMLAttr(str) {
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;');
}

let a_vue_page_properties = []; //-- prop_로 시작하는 attribute만 추가
let elem_vue_page = document.getElementById('vid_vue_page_mount');
if (elem_vue_page) {
  for (let n_ii = 0; n_ii < elem_vue_page.attributes.length; n_ii++) {
    let attr = elem_vue_page.attributes[n_ii];
    let name = attr['name'];
    let value = attr['value'];
    if ((name.indexOf(':') == 0) || (name.indexOf('prop_') == 0)) {
	  //git Code scanning 취약점 조치
      //a_vue_page_properties.push(name + '="' + value + '"');
	  a_vue_page_properties.push(escapeHTMLAttr(name) + '="' + escapeHTMLAttr(value) + '"');
    }
  }
}

var g_vm = new Vue({
  el: '#vid_vue_page_mount',
  template: '<vue-dev-query ref="vid_vue_page_mount" ' + a_vue_page_properties.join(' ') + '/>',
  components: { 'vue-dev-query': devQuery },  
  //-- render: h => h(devQuery),
});
//-- for access javascript
console.log('[o-o][devQuery.js][before window[g_vm_comp]: %o', window['g_vm_comp']);
window['g_vm_comp'] = Object.assign({ 'devQuery': g_vm.$refs.vid_vue_page_mount }, window['g_vm_comp']);
console.log('[o-o][devQuery.js][after window[g_vm_comp]: %o', window['g_vm_comp']);

//-- devQuery.js }
