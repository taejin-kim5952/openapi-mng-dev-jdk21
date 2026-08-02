import { ModalService } from './modal.service';
import { DeferPromise } from './utils';

export function ModalComponent(Vue) {
  Vue.component('modal', {
    template: `
<div class="modal-container">
    <div class="modal-parent"
         v-for="(modal, index) in modalList"
         :key="index"
         :style="{zIndex: index+1}"
         @click="backdropClick($event)">

        <div class="modal"
             ref="modals"
             @click.stop
             :class="modalClasses(modal.config)">
            <container :is="modal.component"
                       :data="modal.config.data">
            </container>        
        </div>
    </div>
    <div class="modal-backdrop" ref="backdrop" :style="{zIndex: modalIndex}"></div>
</div>`,

    mounted() {
      ModalService.passComponent(this);
      document.addEventListener('keydown', this.keydownHandler);
      //--[drm][add]
      document.addEventListener('keyup', this.keyupHandler);
    },

    data() {
      return {
        active: false,
        modalList: [],
        modalIndex: -1,
        modalQueue: [],
        opening: false
      }
    },

    methods: {
      open(component, config) {
        let defer = new DeferPromise();

        config = Object.assign({
          backdrop: true, //--[drm][add]
          backdropClose: true,
          size: 'md',
          escapeClose: true,
          //--[drm][chg]type: 'notice'
          type: ''
        }, config);

        if (!this.active && this.modalList.length) {
          this.modalQueue.push({
            component,
            config,
            defer
          });
        } else {
          this.add({
            component,
            config,
            defer
          })
        }
        return defer.defer;
      },

      add(modal) {
        this.modalIndex++;
        this.modalList.push(modal);

        let index = this.modalList.length - 1;

        if (!this.active) {
          this.opening = true;
          this.activate(index); //--[drm][chg] index
        }
        this.animateModalIn(index);
      },

      close(status, data) {
        let index = this.modalIndex,
            modal = this.modalList[index];
        this.modalIndex--;

        setTimeout(() => {
          modal.defer[status ? 'resolve' : 'reject'](data);
        }, index*100);

        this.animateModalOut(index, () => {
          this.modalList = this.modalList.slice(0, index).concat(this.modalList.slice(index+1));
          if (!this.modalList.length && this.modalQueue.length) {
            this.add(this.modalQueue[0]);
            this.modalQueue = [];
          }
        });

        if (this.modalIndex === -1) {
          if (this.modalQueue.length) {
            this.add(this.modalQueue[0]);
            this.modalQueue = [];
          } else {
            this.deactivate(index);  //--[drm][chg] index
          }
        }
      },

      submit(data=null) {
        //--[drm][chg]this.close(true, data);
        this.close(true, (data||null));
      },

      cancel(data=null) {
        //--[drm][chg]this.close(false, data);
        this.close(false, (data||null));
      },

      animateModalIn(nextModalIndex) {
        // the timeout gives time for the $refs to propagate
        setTimeout(() => {
          let el = this.$refs.modals[nextModalIndex];
          el.classList.add('modal--active');
        }, 100);
        // after modal finishes opening
        if (this.opening) {
          setTimeout(() => {
            this.opening = false;
          }, 300);
        }
      },

      animateModalOut(outModalIndex, cb) {
        let el = this.$refs.modals[outModalIndex];
        el.classList.remove('modal--active');
        setTimeout(cb, 300 - 300);  //--[drm][chg]300 -> 0
      },

      /*-- [drm][chg] {
      activate_org() {
        document.body.classList.add('modal--active');
        this.$refs.backdrop.style.display = 'block';
        setTimeout(() => {
          this.$refs.backdrop.classList.add('active');
        }, 10);
        this.active = true;
      },
      [drm][chg] } --*/
      activate(index) {
        let isBackdrop = (index >= 0 && this.modalList[index].config.backdrop);
        if (false === isBackdrop) {
          document.body.classList.add('modal--active');
          this.active = true;
          return;
        }

        document.body.classList.add('modal--active');
        this.$refs.backdrop.style.display = 'block';
        setTimeout(() => {
          this.$refs.backdrop.classList.add('active');
          //--[drm][add][focus to modal]
          let modal = this.$refs.modals[this.$refs.modals.length - 1];
          console.log('o-o-o', 'activate()', 'modal.tabIndex: ', modal.tabIndex);
          modal.tabIndex = 1; //-- enable focus
          modal.focus();
        }, 10);
        this.active = true;
      },

      /*-- [drm][chg] {
      deactivate_org() {
        this.$refs.backdrop.classList.remove('active');
        setTimeout(() => {
          document.body.classList.remove('modal--active');
          this.$refs.backdrop.style.display = 'none';
        }, 300);
        this.active = false;
      },
      [drm][chg] } --*/
      deactivate(index) {
        let isBackdrop = (index >= 0 && this.modalList[index].config.backdrop);
        if (false === isBackdrop) {
          document.body.classList.remove('modal--active');
          this.active = false;
          return;
        }
        //--[drm][add] }

        this.$refs.backdrop.classList.remove('active');
        setTimeout(() => {
          if (false == this.active) {
            document.body.classList.remove('modal--active');
            this.$refs.backdrop.style.display = 'none';
          }
        }, 300 - 300);  //--[drm][chg] 300 -> 0
        this.active = false;
      },

      backdropClick(event) {    //--[drm][chg] event
        if (this.modalIndex >= 0 &&
            this.modalList[this.modalIndex].config.backdropClose &&
            this.opening === false) {
          //--[drm][chg]this.cancel();
          this.cancel({result: 'backdropclick', event: event});
        }
      },

      keydownHandler(event) {
        //--[drm][cmt]
        // if (event.key === 'Escape' &&
        //     this.modalIndex >= 0 &&
        //     this.modalList[this.modalIndex].config.escapeClose){
        //   this.cancel();
        // }
      },

      //--[drm][add]
      keyupHandler(event) {
        if (this.modalIndex >= 0) {
          if (event.key === 'Escape') {
            event.stopPropagation();
            //--event.stopImmediatePropagation();
            //-- event.preventDefault();
            if (this.modalList[this.modalIndex].config.escapeClose) {
              //--[drm][chg]this.cancel();
              this.cancel({result: 'escape', event: event});
            }
          }
        }
      },

      modalClasses(config) {
        return {
          'modal--md': config.size==='md',
          'modal--lg': config.size==='lg',
          'modal--notice': config.type==='notice',  //--[drm][add]
          'modal--warning': config.type==='warning',
          'modal--error': config.type==='error',
          'modal--success': config.type==='success'
        }
      }
    }
  });
}