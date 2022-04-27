var refreshArray = [{"data": "5", "value": "5"}, {"data": "10", "value": "10"}, {"data": "25", "value": "25"}, {"data": "50", "value": "50"}, {"data": "60", "value": "60"}];
$(window).on('load', function() {
    $(".pre-loading").hide();
});
// display alert base on the header  pages.
function  showAlert(header,msg){
       clearAlert();
       $('#alerts').addClass("alert alert-"+header);
       if(header=='danger'){
            $('#alerts').html("<strong> Error :  </strong> "+msg);
       }else{
             $('#alerts').html("<strong>"+header.charAt(0).toUpperCase()+header.slice(1)+" :  </strong> "+msg);
            
       }
}

// clear the alert message on page.
function  clearAlert(){
       // $(".content").find(".alert").remove();
       $('#alerts').removeClass();
       $('#alerts').html("");
}

// display alert message based on id.
function  showAlertMessage(id, header, msg){
       clearAlertMessage(id);
       $('#'+id).addClass("alert alert-"+header);
       if(header=='danger'){
            $('#'+id).html("<strong> Error :  </strong> "+msg);
       }else{
             $('#'+id).html("<strong>"+header.charAt(0).toUpperCase()+header.slice(1)+" :  </strong> "+msg);
       }
}

// clear the alert message based on id.
function  clearAlertMessage(id){
       $('#'+id).removeClass();
       $('#'+id).html("");
}

//if progress true load the image
function startProcess(progress){
	  if(progress==true){
	    $(".content").append('<div class="loading"><img class="loader" src="/web/images/loader.gif"/></div>');
	  }else if(progress==false){
	    $(".content").append('<div class="loading"></div>');
	  }
}

function stopProcess(){
	 $(".content").find(".loading").remove();
}

function startPartload(progress){
    if(progress==true){
      $(".loadpart").append('<div class="loading-part"><img class="small-loader" src="/web/images/loader.gif"/></div>');
    }else if(progress==false){
      $(".loadpart").append('<div class="loading-part"></div>');
    }
}

function stopPartload(){
    $(".loadpart").find(".loading-part").remove();
}

function getMembers(searchStr,token,tokenVal){
       var members = [];
      if(searchStr.indexOf("[")<0){
          var headerObj={};
          headerObj[token]=tokenVal;

            $.ajax({
               url: "/api/v1/directory/members/"+searchStr,
               dataType: "json",
               async: !1,
               headers:headerObj,
               success:function(data){
                   members = [];
                   for(var i=0; i<data.length; i++) {
                        members.push({"value":data[i].fullName +" ["+data[i].userName+"]", "data":data[i].userName});
                   }
               }
            });
     }
     return members;

}
//check session is valid or not
function isSessionValid(pageStr) {
    if (pageStr.toUpperCase().indexOf("LOGIN") > -1 && pageStr.toUpperCase().indexOf("SIGN IN") > -1 && pageStr.toUpperCase().indexOf("PASSWORD") > -1) {
        location.reload();
    }
}
