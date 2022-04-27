$(document).ready(function(){
	$(".date-move").on("click",function(){
		$(".Date-time-div").toggleClass("close-div");
    $(this).toggleClass("fa-chevron-down");
    $(this).toggleClass("fa-chevron-up");
	});
	window.setInterval(function(){
		dateTime();
}, 1000);
       
$(".navbar-icon .fa").on("click",function(){
  $(".navbar-vertical").toggleClass("active");
  $(".visble-menu").toggleClass("active");
  $(".contant").toggleClass("active");
  $(".user").toggleClass("active");
  $(".top-nav").toggleClass("active");
  $(".heading").toggleClass("active");
  $(".move-chart").toggleClass("active");
  $(".move-chart-1").toggleClass("active");
  $(".gp-text").toggleClass("active");
});

});

function ddmm(n) {
  return (n < 10 ? '0' : '') + n;
}

function dateTime(){
      var current = new Date();
      var cDate = ddmm(current.getDate()) + '-' + ddmm(current.getMonth() + 1) + '-' + current.getFullYear();
      var hours = current.getHours();
      var am_pm = hours >= 12 ? 'PM' : 'AM';

      hours = hours % 12;
      hours = hours ? hours : 12;

      var cTime = (hours < 10 ? '0' : '') + hours + ":" + (current.getMinutes() < 10 ? '0' : '') + current.getMinutes() + ":" + (current.getSeconds() < 10 ? '0' : '') + current.getSeconds();
      var dateTime = cDate + '   ' + cTime;
      $(".Date-time").html(dateTime);
      $(".am_pm").html(am_pm)
}

$(window).on('load', function() {
    $(".pre-loading").hide();
});

// validate configure
// function configValidate(){

// }