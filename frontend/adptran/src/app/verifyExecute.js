//-- verifyExecute.js {
import Vue from 'vue'
import verifyExecute from './verifyExecute.vue'

// Utility to escape attribute values for safe HTML embedding
function escapeHtmlAttr(str) {
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/"/g, '&quot;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;');
}

// Function to check attribute name safety: allow only prop_ followed by safe chars
function isValidAttrName(name) {
  // accept names like prop_foo, prop_foo-bar, prop_foo:bar (if needed)
  return /^prop_[a-zA-Z0-9_\-:.]+$/.test(name);
}

let elem_vue_part = document.getElementById('vid_verifyExecute');
let a_custom_properties = []; //-- prop_로 시작하는 attribute만 추가
if (elem_vue_part) {
  for (let n_ii = 0; n_ii < elem_vue_part.attributes.length; n_ii++) {
    let attr = elem_vue_part.attributes[n_ii];
    let name = attr['name'];
    let value = attr['value'];
	
    //if (name.indexOf('prop_') == 0) {
	if (name.indexOf('prop_') == 0 && isValidAttrName(name)) {
      //a_custom_properties.push(name + '="' + value + '"');
	  a_custom_properties.push(name + '="' + escapeHtmlAttr(value) + '"');
    }
  }
}
console.log('[o-o][verifyExecute.js][a_custom_properties: {}]', a_custom_properties);

let g_vm = new Vue({
  el: '#vid_verifyExecute',
  template: '<vue-verify-execute ref="vid_verifyExecute" ' + a_custom_properties.join(' ') + '/>',
  components: { 'vue-verify-execute': verifyExecute },
  //-- render: h => h(verifyExecute ),
});
//-- for access javascript
console.log('[o-o][verifyExecute.js][before window[g_vm_comp]: %o', window['g_vm_comp']);
window['g_vm_comp'] = Object.assign({ 'verifyExecute': g_vm.$refs.vid_verifyExecute }, window['g_vm_comp']);
console.log('[o-o][verifyExecute.js][after window[g_vm_comp]: %o', window['g_vm_comp']);
//-- verifyExecute.js }
