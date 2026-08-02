<comment>
  message popup modal dialog
</comment>
<script>
import KsmUtil from '@/common/ksmutil.js';
import { ModalService } from '@/modules/vue-modal-dialog';

export default {
  name: 'cus_message_popup',
  props: {
    data: {
      type: Object,
      requird: true,
      default: (() => ({
        title: '', text: '', use_close_button: false, class: '',
        buttons: [{ is_submit: true, text: '확인', result: 'ok', class: '', }],
      })),
      validator: function (value) {
        return (('text' in value)
            && (value.buttons ? Array.isArray(value.buttons) : true)
        );
      },
    },
  },
  data () {
    return {
      m_vue_id: 'cus_message_popup',
    }
  }, // data ()
  created: function () {
    KsmUtil.$console_log('trace', this.m_vue_id, 'created()');
  },
  mounted: function () {
    KsmUtil.$console_log('trace', this.m_vue_id, 'mounted()');
  },
  updated: function () {
    KsmUtil.$console_log('trace', this.m_vue_id, 'updated()');
  },
  methods: {
    submit: function (data) {
      ModalService.submit(data);
    },
    cancel: function (data) {
      ModalService.cancel(data);
    }
  } // methods:
};
</script>

<template>
  <div :class="['cus-vuedialog', data.class]">
    <div class="cus-vuedialog-content">
      <div class="cus-vuedialog-title" v-if="data.title" v-html="(data.title || '')"></div>
      <button type="button" class="cus-vuedialog-close-button" v-if="data.use_close_button" @click.stop="cancel({result: 'close', event: $event})">X</button>
      <div class="cus-vuedialog-text" v-html="(data.text || '')"></div>
    </div>
    <div class="cus-vuedialog-buttons">
      <template v-for="(button, index) in data.buttons">
        <button v-if="button.is_submit"
          type="button"
          :key="index"
          :class="['cus-vuedialog-btn', button.class]"
          v-html="(button.text||'')"
          @click.stop="submit({result: button.result, event: $event})">
        </button>
        <button v-else
          type="button"
          :key="index"
          :class="['cus-vuedialog-btn', button.class]"
          v-html="(button.text||'')"
          @click.stop="cancel({result: button.result, event: $event})">
        </button>
      </template>
    </div>
  </div>
</template>

<x-cmt-style lang="scss">
// @import '~vue-modal-dialog/src/_modal.scss';
</x-cmt-style>

<style>
body.modal--active {
  /*--[drm][cmt] overflow: hidden;*/
  overflow: inherit;padding:0 !important; /*--[drm][add]--*/
}
.modal-container {
  position: fixed;
  z-index: 1090;
}
.modal-backdrop {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  /* background-color: rgba(64, 64, 64, .8); */
  background-color: rgba(0, 0, 0, 0.2);
  z-index: 0;
  opacity: 0;
  display: none;
  /*--[drm][cmt] transition: opacity 300ms ease;*/
}
.modal-backdrop.active {
  opacity: 1;
}
.modal-parent {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 0;
  overflow: auto;
}
.ref-modal {
  background-color: #fff;
  border-radius: 0 0 4px 4px;
  transition: opacity 300ms ease, transform 300ms ease;
  display: block;
  box-shadow: 0 1px 10px 4px rgba(0, 0, 0, 0.2);
  width: 400px;
  margin: 0 auto 40px auto;
  opacity: 1;
  position: relative;
  transform: translateY(-110%);
}

.modal {
  background-color:rgba(0,0,0,0);
  display: block;
  /* box-shadow: 0 1px 10px 4px rgba(0, 0, 0, 0.2); */
  margin: 0 auto 40px auto;
  opacity: 1;
}

/* [drm][add] { */
.modal {  /* ref bootstrap.css */
  width: auto;
  margin: 10px;
  overflow-x: hidden;
  overflow-y: auto;
}
@media (min-width: 576px) { /* copy bootstrap.css */
/*
  .modal {
    max-width: 500px;
    margin: 30px auto;
  }
  */
}
/* [drm][add] } */

.modal.modal--active {
  /*transform: translateY(0%);*/
}
.modal__header {
  padding: 20px 20px 0 20px;
  font-size: 1.2rem;
  font-weight: 500;
}
.modal__body {
  padding: 10px 20px 20px 20px;
}
.modal__footer {
  padding: 0 20px 20px 20px;
  text-align: right;
  cursor: default;
}
.modal__footer .btn {
  margin-left: 7px;
}
.modal--lg {
  width: 700px;
}
.modal--notice {  /*--[drm][add]--*/
  border-top: solid 8px #38d0eb;  /*--[drm][add]#d1ecf1--*/
}
.modal--notice .modal__header { /*--[drm][add]--*/
  color: #38d0eb;
}
.modal--warning {
  border-top: solid 8px #ffa75f;  /*--[drm][chg]#fff3cd--*/
}
.modal--warning .modal__header {
  color: #ffa75f;  /*--[drm][chg]#fff3cd--*/
}
.modal--error {
  border-top: solid 8px #ef6455;  /*--[drm][chg]#f8d7da--*/
}
.modal--error .modal__header {
  color: #ef6455;  /*--[drm][chg]#f8d7da--*/
}
.modal--success {
  border-top: solid 8px #598c54;  /*--[drm][chg]#d4edda;--*/
}
.modal--success .modal__header {
  color: #598c54;  /*--[drm][chg]#d4edda;--*/
}
.overlay-wrapper {
  user-select: none;
}
.overlay {
  position: fixed;
  border-radius: 4px;
  background-color: #fff;
  box-shadow: 0 1px 10px 4px rgba(0, 0, 0, 0.2);
  left: 0;
  top: 0;
}
.overlay__movable {
  position: relative;
  height: 20px;
  cursor: move;
}
.overlay__movable div {
  position: relative;
  height: 0;
  padding-top: 10px;
  margin: 0 auto 10px auto;
  width: 14px;
  border-bottom: solid 1px #d3d3d3;
}
.overlay__movable div::before, .overlay__movable div::after {
  border-bottom: solid 1px #d3d3d3;
  content: '';
  position: absolute;
  left: 0;
  right: 0;
}
.overlay__movable div::before {
  top: 7px;
}
.overlay__movable div::after {
  top: 13px;
}
.overlay {
  display: flex;
  flex-direction: column;
  cursor: default;
  resize: both;
  overflow: auto;
  z-index: 999;
}
.overlay__header {
  padding: 0 14px 10px 14px;
  font-size: 1.1rem;
  font-weight: 500;
  border-bottom: solid 1px #d3d3d3;
}
.overlay__body {
  padding: 14px;
  overflow: auto;
  flex-grow: 1;
  user-select: auto;
}
.overlay__footer {
  padding: 10px;
  text-align: right;
  border-top: solid 1px #d3d3d3;
}
.overlay__footer .btn {
  margin-left: 7px;
}
</style>

<style scoped>
div {
  box-sizing: border-box;
}

div.cus-vuedialog {
  position: absolute; top:7%; left:50%; 
  min-width:450px;margin-left:-225px;;
  background: #fff; box-shadow:0px 10px 15px 0px rgba(0,0,0,0.2);
  margin-top: 10%;
  /*display: none;*/ 
  /*padding: 0 !important;*/
}

.cus-vuedialog-content {
  flex: 1 0 auto;
  width: 100%;
  padding: 15px;
  font-size: 14px;
  font-family: 'Noto Sans KR';
}

.cus-vuedialog-content .cus-vuedialog-title {
  text-overflow: ellipsis; overflow: hidden; width: 98%; white-space: nowrap;
  line-height:30px; font-weight:700; color: #333a4b; font-size: 24px;
  padding: 10px 0 30px 15px; 
}

.cus-vuedialog-content .cus-vuedialog-close-button
{
  display:block;position:absolute;margin-top:0;right:26px;top:29px;width:23px;height:24px;text-indent:-990em;
  background-image: url('data:image/png;base64, \
    iVBORw0KGgoAAAANSUhEUgAAABgAAAAYBAMAAAASWSDLAAAACXBIWXMAAAsTAAALEwEAmpwYAAAAIGNIUk0AAHolAACAgwAA+f8A \
    AIDpAAB1MAAA6mAAADqYAAAXb5JfxUYAAAAPUExURRoeKRoeKRoeKRoeKRoeKXFZEWkAAAAFdFJOUwBBruvwMhKRuAAAAFhJREFU \
    GNNjUGSAAyEGYQEYm9EQhKAAJAyTAovCpCCCEBIqBqFgqkE03BwQA2GDsADCAqAUQoKBQcSRATsHRRmyAShGI1uK4hxkh6J4Adlz \
    KN5GCRDkoAIAsdoHtTxq7lQAAAAASUVORK5CYII=');
  background-repeat: no-repeat;
  background-position: 0 0;  
  /*background:url(/apidev/resources/images/common/icon/ico_closeB02.png) no-repeat 0 0;*/
  border:0 none;
  /*border: 1px solid #c5c5c5;*/
}

.cus-vuedialog-content .cus-vuedialog-text {
    line-height:24px; white-space:pre-line; font-size: 16px; font-weight: 300; text-align: center;
    padding: 10px 0 10px 15px;
    color: #333333;
}

.cus-vuedialog-buttons {
  flex: 0 1 auto;
  width: 100%;
  display: inline-block;
  text-align:center; margin: 20px 0 20px;
}

.cus-vuedialog-buttons .cus-vuedialog-btn {
  cursor: pointer;
  box-sizing: border-box;
  outline: none;
  display:inline-block; font-size:14px; font-weight: 400;
  line-height: 16px; padding: 11px 20px; border-radius: 3px; margin: 0 5px;
  background: #102e3b; color: #fff;
  border:0 none; height:unset;
}
.cus-vuedialog-buttons .cus-vuedialog-btn.ok, .cus-vuedialog-buttons .cus-vuedialog-btn.ok {
  width: 98px; background: #333; color: #fff;
}

.cus-vuedialog-buttons .cus-vuedialog-btn.cancel {
  width: 98px; background: #fff; border:1px solid #333; color: #333;
}
</style>
