import { OverlayService } from './overlay.service';
import { DeferPromise } from './utils';

export function OverlayComponent(Vue) {
  Vue.component('overlay', {
    template:
`<div class="overlay-wrapper">
    <div class="overlay"
         ref="overlay"
         v-for="(overlay, key) in list"
         :key="key"
         :is="overlay.component"
         :style="{width:400+'px', height: 200+'px'}">
         <div slot="movable"
              class="overlay__movable"
              @mousedown="startMove(key)"
              @mouseup="stopMove()"><div></div></div>
    </div>
</div>`,

    mounted() {
      OverlayService.passComponent(this);
    },

    data() {
      return {
        //--[drm][add][for IE (movementX, movementY)]
        prevX: 0, prevY: 0,
        list: []
      }
    },

    methods: {
      open(component, id, config={}) {
        if (this.list.find(item => item.id === id)) return;
        let defer = new DeferPromise();
        this.list.push({
          id,
          component,
          defer
        });
        return defer.defer;
      },

      startMove(key) {
        this.movingEl = this.$refs.overlay[key].$el;
        document.addEventListener('mousemove', this.move);
      },

      stopMove() {
        document.removeEventListener('mousemove', this.move);
        this.movingEl = null;
      },

      move(e) {
        /*--[drm][chg] {
        let x = parseInt(this.movingEl.style.left.replace('px', '')) || 0;
        let y = parseInt(this.movingEl.style.top.replace('px', '')) || 0;
        this.movingEl.style.left = (x + e.movementX) + 'px';
        this.movingEl.style.top = (y + e.movementY) + 'px';
        [drm][chg] { --*/
        let x = parseInt(this.movingEl.style.left.replace('px', '')) || 0;
        let y = parseInt(this.movingEl.style.top.replace('px', '')) || 0;
        let movementX = e.movementX;
        let movementY = e.movementY;
        let is_support_movementXY = ((typeof(e.movementX) === 'number') && (typeof(e.movementY) === 'number'));
        if (false === is_support_movementXY) {
          movementX = (this.prevX ? e.screenX - this.prevX : 0);
          movementY = (this.prevY ? e.screenY - this.prevY : 0);
          this.prevX = e.screenX;
          this.prevY = e.screenY;
        }
        this.movingEl.style.left = (x + movementX) + 'px';
        this.movingEl.style.top = (y + movementY) + 'px';
      },

      submit(id, data) {
        this.close(id, data, 'resolve');
      },

      cancel(id, data) {
        this.close(id, data, 'reject');
      },

      close(id, data=null, respond) {
        let overlay = this.list.find(item => {
          return item.id === id;
        });
        overlay.defer[respond](data);
        let index = this.list.findIndex(item => {
          return item.id === id;
        });
        this.list = this.list.slice(0, index).concat(this.list.slice(index+1));
      }
    }
  });
}