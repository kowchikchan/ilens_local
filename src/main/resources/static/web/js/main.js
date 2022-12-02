var Status=[];
$(document).ready(function(){
  //onLoadMenu();
	$(".date-move").on("click",function(){
		$(".Date-time-div").toggleClass("close-div");
    $(this).toggleClass("fa-chevron-down");
    $(this).toggleClass("fa-chevron-up");
	});
	window.setInterval(function(){
		dateTime();
}, 1000);

$(".navbar-icon .fa").on("click",function(){
  var status=Status[Status.length - 1];
  if(status==true){
    status=false; 
  }else{
     status=true;
  }
  Status.push(status); 
     var data={
       id:1,
       status:status
     }
     $.ajax({
         contentType: "application/json;charset=UTF-8",
         url: "api/v1/menuStatus/save",   
         headers:{'CLIENT_KEY': "[[${session.API_TOKEN}]]"},
         type: 'POST', 
         data: JSON.stringify(data),
         async: false,
         success: function(){ 
           toggleMenu();
         },
         error: function(err){
           console.log(err);
         }
     });
   });

});

function onLoadMenu(){
  $.ajax({
    contentType: "application/json;charset=UTF-8",
    url: "/api/v1/menuStatus/list",   
    headers:{'CLIENT_KEY': "[[${session.API_TOKEN}]]"},
    type: 'GET',
    async: false,
    success: function(data){ 
//      if(data.status==true){
//        toggleMenu();
//      }
      Status.push(data.status);
    },
    error: function(err){
      console.log(err);
    }
    });
}

 function toggleMenu(){
  $(".navbar-vertical").toggleClass("active");
  $(".visble-menu").toggleClass("active");
  $(".visble-menu i").toggleClass("inactive1");
  $(".visble-menu em").toggleClass("inactive1");
  $(".contant").toggleClass("active");
  $(".user").toggleClass("active");
  $(".top-nav").toggleClass("active");
  $(".heading").toggleClass("active");
  $(".move-chart").toggleClass("active");
  $(".move-chart-1").toggleClass("active");
  $(".gp-text").toggleClass("active");
  $("#Adms").toggleClass("active");
};


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

function fullTimeFormat(date){
  var date=moment(date).format("DD-MM-YYYY hh:mm A");
  return date;
}

function timeOnlyFormat(date){
  var date=moment(date).format("hh:mm A");
  return date;
}