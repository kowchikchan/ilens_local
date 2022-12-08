var Status=false;
$(document).ready(function(){
  onLoadMenu();
	$(".date-move").on("click",function(){
		$(".Date-time-div").toggleClass("close-div");
    $(this).toggleClass("fa-chevron-down");
    $(this).toggleClass("fa-chevron-up");
	});
	window.setInterval(function(){
		dateTime();
}, 1000);

$(".navbar-icon .fa").on("click",function(){
  if(Status==true){
    Status=false; 
  }else{
    Status=true;
  }
     var data={
       id:1,
       status:Status
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
      Status=data.status;
    },
    error: function(err){
      console.log(err);
    }
    });
}

 function toggleMenu(){
  $(".navbar-vertical").toggleClass("active");
  $(".visble-menu").toggleClass("active");
  $(".contant").toggleClass("active");
  $(".user").toggleClass("active");
  $(".top-nav").toggleClass("active");
  $(".move-chart").toggleClass("active");
  $(".move-chart-1").toggleClass("active");
  $(".gp-text").toggleClass("active");
  $("#Adms").toggleClass("active");
};


function ddmm(n) {
  return (n < 10 ? '0' : '') + n;
}

function dateTime(){
    var date= moment().format("DD-MM-YYYY hh:mm:ss A");
    $(".Date-time").html(date);
}

$(window).on('load', function() {
    $(".pre-loading").hide();
});


function fullTimeFormat(date){
  var date=moment(date).format("DD-MM-YYYY hh:mm A");
  return date;
}

function timeOnlyFormat(date){
  var date=moment(date).format("hh:mm A");
  return date;
}