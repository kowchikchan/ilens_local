$(document).ready(function(){
    $('input[type="checkbox"]').click(function(){
        isChecked($(this));
    });

    $('.S-dropdown').click(function () {
        $(this).attr('tabindex', 1).focus();
        $(this).toggleClass('active');
        $(this).find('.S-dropdown-menu').slideToggle(300);
    });
    $('.S-dropdown .S-dropdown-menu li').click(function () {
        $(this).parents('.S-dropdown').find('span').text($(this).text());
        $(this).parents('.S-dropdown').find('input').attr('value', $(this).attr('id'));
        $(this).parents('.S-dropdown').find('input').attr('text', $(this).attr('value'));
    });

    $("input[type='radio']").on("click",function(){
        $("input[type='radio']").siblings().removeClass("checked-radio");
        $("input[type='radio']:checked").siblings().addClass("checked-radio");
    });
});

function isChecked(curObj) {
    if(curObj.prop("checked") == true){
        curObj.parent(".container-checkbox").addClass("check");
        curObj.parent(".container-checkbox").removeClass("notcheck");
    }
    else if(curObj.prop("checked") == false){
        curObj.parent(".container-checkbox").addClass("notcheck");
        curObj.parent(".container-checkbox").removeClass("check");
    }

}