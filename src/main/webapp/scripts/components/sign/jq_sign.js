$(function () {
    if (/#(.*)/.exec(location.href) && /#(.*)/.exec(location.href)[1] == "signin") {
        signin();
    } else {
        signup();
    }
    $(".navs-slider a").click(function () {
        var _this = this;
        $(".navs-slider a").removeClass("active");
        $(".navs-slider").attr("data-active-index", _this.id == 'signin' ? 1 : 2);
        $(this).addClass("active");
        switch_form(_this);
    });
    function signin() {
        $(".navs-slider a").removeClass("active");
        $(".navs-slider a:eq(1)").addClass("active");
        $(".navs-slider").attr("data-active-index", 1);
        $(".view-signin").attr("style","display: block;");
        $(".view-signup").attr("style","display: none;");
    }

    function signup() {
        $(".navs-slider a").removeClass("active");
        $(".navs-slider a:eq(0)").addClass("active");
        $(".navs-slider").attr("data-active-index", 2);
        $(".view-signup").attr("style","display: block;");
        $(".view-signin").attr("style","display: none;");
    }

    function switch_form(active){
        if(!active){return}
        if(active.id == "signin"){
            $(".view-signin").attr("style","display: block");
            $(".view-signup").attr("style","display: none");
        }else{
            $(".view-signin").attr("style","display: none");
            $(".view-signup").attr("style","display: block");
        }
    }


})