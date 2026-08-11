$(function () {
    if (window.location.hash) {
        $(window.location.hash).attr("taonex", -1).focus();
    }

    var $mainBox = $('div.mainBox'); // 메인 박스
    var $naviBox = $('#gnb');
    var $naviBoxSub;
    var $naviWrapBox;
    var $movingBox;

    /* Setup */

    $naviBox.find('> li').on('click', naviBoxListOverHandler);

    function naviBoxListOverHandler() {
        $(this).closest('li').siblings().removeClass('active');
        $(this).closest('li').addClass('active');
    }
})
