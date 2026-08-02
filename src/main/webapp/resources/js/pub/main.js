$(function() {
    if (window.location.hash) {
        $(window.location.hash).attr("taonex", -1).focus();
    }

    var $mainBox = $('div.mainBox'); // 메인 박스
    var $naviBox;
    var $naviBoxSub;
    var $naviWrapBox;
    var $movingBox;

    /* Setup */
    function browserSet() {
        if ($mainBox.find('div.bgSet').length == 0) {
            $naviBox = $('ul.naviBox');
            $naviWrapBox = $('<div class="naviWrap" />').append('<div class="movingBox" />');
            $naviWrapBox.remove();
            $naviBox.wrap($naviWrapBox);
            $naviWrapBox = $('div.naviWrap');
            $movingBox = $('div.movingBox');
        }
    }

    browserSet();

    $naviBox.find('li').on('mouseenter', naviBoxOverHandler);
    $naviBox.find('> li > a').on('click', naviBoxOverHandler);
    $movingBox.on('mouseleave', naviBoxOutHandler);
    $naviBox.find('> li').on('mouseenter', naviBoxListOverHandler);

    var maxHeight = Math.max.apply(null, $naviBox.find('> li > ul').map(function (){
        return $(this).outerHeight();
    }).get());
    var heights = $naviBox.find('> li > ul').map(function (){
        return $(this).outerHeight();
    }).get(),

    maxHeight = Math.max.apply(null, heights) + 80 ;

    //--##[tag:adpt][drm][cmt] console.log(maxHeight);
    // GNB
    function naviBoxOverHandler() {
        $naviBox.find('ul').css({
            'visibility': 'visible'
        });
        $naviBox.stop().animate({
            'height': maxHeight + 'px'
        },  10);
        $movingBox.stop().animate({
            'height': maxHeight + 'px'
        },  400, 'easeOutBounce', function () {
            $movingBox.addClass('chk');
        });
    }

     function naviBoxOutHandler() {
        if ($movingBox.hasClass('chk')) {
            $naviBox.stop().animate({
                'height': '55px'
            }, 200);
            $movingBox.stop().animate({
                'height': '55px'
            }, 200, function () {
                $naviBox.find('ul').css({ 'visibility' : 'hidden' }) ;
                $movingBox.removeClass('chk');
            });
        }
        $naviBox.find('> li > a').removeClass('active');
    }

    function naviBoxListOverHandler(e) {
        e.stopPropagation();
        $(this).closest('li').siblings().find('> a').removeClass('active');
        $(this).closest('li').find('> a').addClass('active');
    }

    $naviBox.find('> li').on('focusin', naviBoxFocusinHandler);

    function naviBoxFocusinHandler(e) {
        if (!$movingBox.hasClass('chk')) {
            $movingBox.addClass('chk');
            naviBoxOverHandler();
        }
    }

    $naviBox.find('li:first a:first').on('keydown', naviBoxFirstKeydownHandler);

    function naviBoxFirstKeydownHandler(e) {
        if (e.keyCode == 9 && e.shiftKey) {
            naviBoxOutHandler();
        }
    }

    $naviBox.find('li:last').on('keydown', naviBoxLastKeydownHandler);

    function naviBoxLastKeydownHandler(e) {
        if (e.keyCode == 9) {
            naviBoxOutHandler();
            $movingBox.removeClass('chk');
        }
    }
    
    //패밀리사이트
    $("a.fms_box").on("click", function(e){
        e.stopPropagation();
        e.preventDefault();
        if($(".fms_wrp").hasClass("fms_open")){
            $(".fms_wrp").removeClass("fms_open");
        }else{
            $(".fms_wrp").addClass("fms_open");
        }
    });
    
    $("a.fms_x").on("click", function(e){
        e.stopPropagation();
        e.preventDefault();
        $(".fms_wrp").removeClass("fms_open");
    });
    
    //다른곳 클릭시 닫힘 스크립트 추가 
    if($("a.fms_box").length){
        $(document).on('click.fms_box',function(){
           if($(".fms_wrp").hasClass("fms_open")){
                $(".fms_wrp").removeClass("fms_open");
            }
        });
    }
});
