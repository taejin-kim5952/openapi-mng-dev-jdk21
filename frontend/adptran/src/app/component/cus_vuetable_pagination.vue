<script>
import VuetablePaginationMixin from 'vuetable-2/src/components/VuetablePaginationMixin'

export default {
  name: 'cus_vuetable_pagination',
  mixins: [VuetablePaginationMixin],
  created: function () {
    //-- set default class
    this.css.wrapperClass = (this.css.wrapperClass||'cus-vuetable-pagination');
    this.css.linkClass = (this.css.linkClass||'link');
    this.css.linkDisabledClass = (this.css.linkDisabledClass||'disabled');
    this.css.pagesClass = (this.css.pagesClass||'page');
    this.css.pageClass = (this.css.pageClass||'page');
    this.css.pageActiveClass = (this.css.pageActiveClass||'on');
    this.css.linkFirstImageSrc = (this.css.linkFirstImageSrc||'/apidev/resources/images/common/icon/pg_first.png');
    this.css.linkPrevImageSrc = (this.css.linkPrevImageSrc||'/apidev/resources/images/common/icon/pg_prev.png');
    this.css.linkNextImageSrc = (this.css.linkNextImageSrc||'/apidev/resources/images/common/icon/pg_next.png');
    this.css.linkLastImageSrc = (this.css.linkLastImageSrc||'/apidev/resources/images/common/icon/pg_last.png');
  },
  methods: {
  } // methods:
};
</script>

<template>
  <div v-show="(tablePagination && (tablePagination.last_page > 1))" :class="css.wrapperClass">
    <div>
      <a href="javascript:void(0)"
        :class="[css.linkClass, isOnFirstPage ? css.linkDisabledClass : '']"
        @click.stop="loadPage(1)">
        <img alt="맨앞으로" :src="css.linkFirstImageSrc">
      </a>
      <a href="javascript:void(0)"
        :class="[css.linkClass, isOnFirstPage ? css.linkDisabledClass : '']"
        @click.stop="loadPage('prev')">
        <img alt="앞으로" :src="css.linkPrevImageSrc">
      </a>
      <span :class="css.pagesClass">
        <template v-if="notEnoughPages">
          <template v-for="n in totalPage">
            <a href="javascript:void(0)"
              :key="n" 
              :class="[css.pageClass, isCurrentPage(n) ? css.pageActiveClass : '']"
              @click.stop="loadPage(n)"
              v-html="n">
            </a>
          </template>
        </template>
        <template v-else>
          <template v-for="n in windowSize">
            <a href="javascript:void(0)"
              :key="n"
              :class="[css.pageClass, isCurrentPage(windowStart + n - 1) ? css.pageActiveClass : '']"
              @click.stop="loadPage(windowStart + n - 1)"
              v-html="(windowStart + n - 1)">
            </a>
          </template>
        </template>
      </span>
      <a href="javascript:void(0)"
        :class="[css.linkClass, isOnLastPage ? css.linkDisabledClass : '']"
        @click.stop="loadPage('next')">
        <img alt="뒤로" :src="css.linkNextImageSrc">
      </a>
      <a href="javascript:void(0)"
        :class="[css.linkClass, isOnLastPage ? css.linkDisabledClass : '']"
        @click.stop="loadPage(totalPage)">
        <img alt="맨뒤로" :src="css.linkLastImageSrc">
      </a>
    </div>
  </div>
</template>

<style scoped>
/*-- apimanager style --*/
.cus-vuetable-pagination {margin:30px auto 0;text-align:center;}
.cus-vuetable-pagination div{overflow:hidden;width:auto;display:inline-block;vertical-align:top;}
.cus-vuetable-pagination div>a.link:first-child{margin-left:0; margin-right:6px; /*border-radius:2px 0 0 2px;*/}
.cus-vuetable-pagination div>a.link:last-child{margin-left:6px; /*border-radius:0 2px 2px 0;*/}
.cus-vuetable-pagination a {font-size: 15px;}
.cus-vuetable-pagination a.link {overflow:hidden;float:left;width:38px;height:38px;vertical-align:top;margin-left:0;line-height:38px;border:1px solid #dcdcdc;margin-left:-1px;color:#757575;}
.cus-vuetable-pagination a.link.disabled, .vuetable-pagination a.link:disabled { pointer-events: none; cursor: not-allowed; opacity: .65; }
.cus-vuetable-pagination span.pages {float:left;}
.cus-vuetable-pagination a.page {overflow:hidden;float:left;width:30px;height:38px;vertical-align:top;margin-left:0;line-height:38px; margin-left:-1px; color:#757575;}
.cus-vuetable-pagination a.page.on{color:#c93137;font-weight:700;}
.cus-vuetable-pagination a.page:first-child{margin-left:20px;}
.cus-vuetable-pagination a.page:last-child{margin-right:20px;}

/*-- bcconsole style --*/
/*--
.cus-vuetable-pagination {margin:30px auto 0;text-align:center;}
.cus-vuetable-pagination div{overflow:hidden;width:auto;display:inline-block;vertical-align:top;}
.cus-vuetable-pagination div>a.link:first-child{margin-left:0;border-radius:2px 0 0 2px;}
.cus-vuetable-pagination div>a.link:last-child{border-radius:0 2px 2px 0;}
.cus-vuetable-pagination a.link {overflow:hidden;float:left;width:41px;height:36px;vertical-align:top;margin-left:0;line-height:36px;border:1px solid #e1e3e9;margin-left:-1px;color:#515a6a;}
.cus-vuetable-pagination a.link.disabled, .vuetable-pagination a.link:disabled { pointer-events: none; cursor: not-allowed; opacity: .65; }
.cus-vuetable-pagination span.pages {float:left;}
.cus-vuetable-pagination a.page {overflow:hidden;float:left;width:41px;height:36px;vertical-align:top;margin-left:0;line-height:36px;border:1px solid #e1e3e9;margin-left:-1px;color:#515a6a;}
.cus-vuetable-pagination a.page.on{background:#737881;border:1px solid #737881;color:#fff;}
--*/
</style>

