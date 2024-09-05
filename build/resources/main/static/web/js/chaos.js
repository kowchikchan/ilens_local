var headerObj={};
headerObj[tokenName]=tokenValue;
function triggerFun(uid){
    console.log("trigger: "+uid);
    var index = 0;
    if(uid.indexOf("auth") != -1) {
        index = parseInt(uid.replace("auth-",""));
        var input = inputSummary.authentication[index].testConfig;
        if(index == 0){
            certificateExpire(input);
        }else if(index == 1){
            var inputData = {"duration": input.duration, "userName": input.userId}
            functionalIdLocked(inputData);
        }else if(index == 2){
            functionalIdExpire(input);
        }
    }else if(uid.indexOf("bat") != -1){
        index = parseInt(uid.replace("bat-",""));
        var input = inputSummary.batch[index].testConfig;
        if(index == 0){
            zeroByteFile(input);
        }else if(index == 1){
            wrongFileFormat(input);
        }else if(index == 2){
            corruptedFile(input);
        }
    }else if(uid.indexOf("dbs") != -1){
        index = parseInt(uid.replace("dbs-",""));
        var input = inputSummary.database[index].testConfig;
        if(index == 0){
            dbTableLock(input);
        }else if(index == 1){
            dbConnectionPool(input);
        }
    }else if(uid.indexOf("mwt") != -1){
        index = parseInt(uid.replace("mwt-",""));
        var input = inputSummary.middleWare[index].testConfig;
        if(index == 0){
            messageFlood(input);
        }else if(index == 1){
            corruptedMessage(input);
        }else {
            lossOfMQListener(input);
        }
    }else if(uid.indexOf("nwt") != -1){
        index = parseInt(uid.replace("nwt-",""));
        var input = inputSummary.network[index].testConfig;
        if(index == 0){
            latencyIO(input);
        }else if(index == 1){
            packetLoss(input);
        }else if(index == 2) {
            packetCorruption(input);
        }else if(index == 3) {
            packetDuplication(input);
        }else if(index == 4) {
            lossOfDNS(input);
        }
    }else if(uid.indexOf("ser") != -1){
        index = parseInt(uid.replace("ser-",""));
        var input = inputSummary.server[index].testConfig;
        if(index == 0){
            cpu(input);
        }else if(index == 1){
            memory(input);
        }else if(index == 2){
            process(input);
        }else if(index == 3){
            diskIO(input);
        }else if(index == 4){
            fileSystem(input);
        }else if(index == 5){
            timeDrift(input);
        }
    }
}

function certificateExpire(inputData){
    var cmdbId = $("#cmdbId").val();
    var env = "null";
    if($.trim($("#environment").val()) != "") {
        env=$("#environment").val();
    }
    var server = 0;
    if($.trim($("#serverId").val()) != ""){
        server = $("#serverId").val();
    }

    $.ajax({
          contentType: "application/json;charset=UTF-8",
          url: "/api/v1/chaostik/certificateExpire/"+server+"/"+env+"/"+cmdbId,
          headers:headerObj,
          type: "POST",
          cache: !1,
          processData: !1,
          data: JSON.stringify(inputData),
          complete:function(xhr){
            if(xhr.status==200){
                 showAlertMessage("trigger_alertsMsg","success", "Certificate Expire triggered successfully");
                 setTimeout(clearAlertMessage, 2000, "trigger_alertsMsg");
            }else{
                showAlertMessage("trigger_alertsMsg","warning", "Failed to trigger Certificate Expire");
                setTimeout(clearAlertMessage, 2000, "trigger_alertsMsg");
            }
          }
    });
}

function functionalIdLocked(inputData) {
    var cmdbId = $("#cmdbId").val();
    var env = "null";
    if($.trim($("#environment").val()) != "") {
        env=$("#environment").val();
    }
    var server = 0;
    if($.trim($("#serverId").val()) != ""){
        server = $("#serverId").val();
    }
    $.ajax({
            contentType: "application/json;charset=UTF-8",
            url: "/api/v1/chaostik/blockUser/"+server+"/"+env+"/"+cmdbId,
            headers:headerObj,
            type: "POST",
            cache: !1,
            processData: !1,
            data: JSON.stringify(inputData),
            complete:function(xhr){
                if(xhr.status==200){
                    console.log("blockUser Success");
                    showAlertMessage("trigger_alertsMsg","success", "Functional ID Locked triggered successfully");
                    setTimeout(clearAlertMessage, 2000, "trigger_alertsMsg");
                }else{
                    console.log("blockUser error");
                    showAlertMessage("trigger_alertsMsg","warning", "Failed to trigger Functional ID Locked");
                    setTimeout(clearAlertMessage, 2000, "trigger_alertsMsg");
                }
            }
    });

}

function functionalIdExpire(inputData) {
    var cmdbId = $("#cmdbId").val();
    var env = "null";
    if($.trim($("#environment").val()) != "") {
        env=$("#environment").val();
    }
    var server = 0;
    if($.trim($("#serverId").val()) != ""){
        server = $("#serverId").val();
    }
    $.ajax({
            contentType: "application/json;charset=UTF-8",
            url: "/api/v1/chaostik/functionalIdExpire/"+server+"/"+env+"/"+cmdbId,
            headers:headerObj,
            type: "POST",
            cache: !1,
            processData: !1,
            data: JSON.stringify(inputData),
            complete:function(xhr){
                if(xhr.status==200){
                    console.log("Functional ID Expire Success");
                    showAlertMessage("trigger_alertsMsg","success", "Functional ID Expire triggered successfully");
                    setTimeout(clearAlertMessage, 2000, "trigger_alertsMsg");
                }else{
                    console.log("Functional ID Expire error");
                    showAlertMessage("trigger_alertsMsg","warning", "Failed to trigger Functional ID Expire");
                    setTimeout(clearAlertMessage, 2000, "trigger_alertsMsg");
                }
            }
    });
}

function zeroByteFile(inputData) {
    var cmdbId = $("#cmdbId").val();
    var env = "null";
    if($.trim($("#environment").val()) != "") {
        env=$("#environment").val();
    }
    var server = 0;
    if($.trim($("#serverId").val()) != ""){
        server = $("#serverId").val();
    }

    $.ajax({
            contentType: "application/json;charset=UTF-8",
            url: "/api/v1/chaostik/zeroByteFileAttack/"+server+"/"+env+"/"+cmdbId,
            headers:headerObj,
            type: "PUT",
            cache: !1,
            processData: !1,
            data: JSON.stringify(inputData),
            complete:function(xhr){
                if(xhr.status==200){
                    //console.log("200 Response");
                    showAlertMessage("trigger_alertsMsg","success", "0 Byte File Triggered");
                    setTimeout(clearAlertMessage, 2000, "trigger_alertsMsg");
                }else{
                    //console.log("Error");
                   showAlertMessage("trigger_alertsMsg","warning", "Failed to trigger 0 Byte File");
                   setTimeout(clearAlertMessage, 2000, "trigger_alertsMsg");
                }
            }
    });
}

function wrongFileFormat(inputData) {
    var cmdbId = $("#cmdbId").val();
    var env = "null";
    if($.trim($("#environment").val()) != "") {
        env=$("#environment").val();
    }
    var server = 0;
    if($.trim($("#serverId").val()) != ""){
        server = $("#serverId").val();
    }

    $.ajax({
        contentType: "application/json;charset=UTF-8",
        url: "/api/v1/chaostik/wrongFileFormat/"+server+"/"+env+"/"+cmdbId,
        headers:headerObj,
        type: "PUT",
        cache: !1,
        processData: !1,
        data: JSON.stringify(inputData),
        complete:function(xhr){
            if(xhr.status==200){
                showAlertMessage("trigger_alertsMsg","success", "Wrong File Format Triggered");
                setTimeout(clearAlertMessage, 2000, "trigger_alertsMsg");
            }else{
               showAlertMessage("trigger_alertsMsg","warning", "Failed to trigger Wrong File Format");
               setTimeout(clearAlertMessage, 2000, "trigger_alertsMsg");
            }
        }
    });
}

function corruptedFile(inputData) {
    var cmdbId = $("#cmdbId").val();
    var env = "null";
    if($.trim($("#environment").val()) != "") {
        env=$("#environment").val();
    }
    var server = 0;
    if($.trim($("#serverId").val()) != ""){
        server = $("#serverId").val();
    }

    $.ajax({
        contentType: "application/json;charset=UTF-8",
        url: "/api/v1/chaostik/corruptedFile/"+server+"/"+env+"/"+cmdbId,
        headers:headerObj,
        type: "PUT",
        cache: !1,
        processData: !1,
        data: JSON.stringify(inputData),
        complete:function(xhr){
            if(xhr.status==200){
                showAlertMessage("trigger_alertsMsg","success", "Corrupted FileTriggered");
                setTimeout(clearAlertMessage, 2000, "trigger_alertsMsg");
            }else{
               showAlertMessage("trigger_alertsMsg","warning", "Failed to trigger Corrupted File");
               setTimeout(clearAlertMessage, 2000, "trigger_alertsMsg");
            }
        }
     });
}

function dbTableLock(inputData) {
    var cmdbId = $("#cmdbId").val();
    var env = "null";
    if($.trim($("#environment").val()) != "") {
        env=$("#environment").val();
    }
    var server = 0;
    if($.trim($("#serverId").val()) != "") {
        server = $("#serverId").val();
    }
    $.ajax({
        contentType: "application/json;charset=UTF-8",
        url: "/api/v1/chaotic/db/table",
        headers:headerObj,
        type: "POST",
        cache: !1,
        processData: !1,
        data: JSON.stringify(inputData),
        success:function(data){
                 showAlertMessage("trigger_alertsMsg", "success", "DB Table Lock Triggered");
                 setTimeout(clearAlertMessage, 2000, "trigger_alertsMsg");
        },
        error:function(data){
                showAlertMessage("trigger_alertsMsg", "warning", "Failed to trigger DB Table Lock");
                setTimeout(clearAlertMessage, 2000, "trigger_alertsMsg");
        }
    });
}

function dbConnectionPool(inputData) {
    var cmdbId = $("#cmdbId").val();
    var env = "null";
    if($.trim($("#environment").val()) != "") {
        env=$("#environment").val();
    }
    var server = 0;
    if($.trim($("#serverId").val()) != ""){
        server = $("#serverId").val();
    }

    $.ajax({
        contentType: "application/json;charset=UTF-8",
        url: "/api/v1/chaotic/db/maxConnectionPool",
        headers:headerObj,
        type: "POST",
        cache: !1,
        processData: !1,
        data: JSON.stringify(inputData),
        success:function(data){
             showAlertMessage("trigger_alertsMsg", "success", "Connection Pooling Triggered");
             setTimeout(clearAlertMessage, 2000, "trigger_alertsMsg");
        },
        error:function(data){
             showAlertMessage("trigger_alertsMsg", "warning", "Failed to trigger Connection Pooling");
             setTimeout(clearAlertMessage, 2000, "trigger_alertsMsg");
        }
    });
}

function messageFlood(inputData) {
    var cmdbId = $("#cmdbId").val();
    var env = "null";
    if($.trim($("#environment").val()) != "") {
        env=$("#environment").val();
    }
    var server = 0;
    if($.trim($("#serverId").val()) != ""){
        server = $("#serverId").val();
    }
    $.ajax({
        contentType: "application/json;charset=UTF-8",
        url: "/api/v1/activeMQ/message/sender/"+server+"/"+env+"/"+cmdbId,
        headers:headerObj,
        type: "POST",
        cache: !1,
        processData: !1,
        data: JSON.stringify(inputData),
        complete:function(xhr){
            if(xhr.status==200){
                 showAlertMessage("trigger_alertsMsg", "success", "MQ Message Flood Triggered");
                  setTimeout(clearAlertMessage, 2000, "trigger_alertsMsg");
            }else{
               showAlertMessage("trigger_alertsMsg", "warning", "Failed to trigger MQ Message Flood");
                setTimeout(clearAlertMessage, 2000, "trigger_alertsMsg");
            }
        }
    });
}
function corruptedMessage(inputData) {
    var cmdbId = $("#cmdbId").val();
    var env = "null";
    if($.trim($("#environment").val()) != "") {
        env=$("#environment").val();
    }
    var server = 0;
    if($.trim($("#serverId").val()) != ""){
        server = $("#serverId").val();
    }
    $.ajax({
        contentType: "application/json;charset=UTF-8",
        url: "/api/v1/activeMQ/message/sender/"+server+"/"+env+"/"+cmdbId,
        headers:headerObj,
        type: "POST",
        cache: !1,
        processData: !1,
        data: JSON.stringify(inputData),
        complete:function(xhr){
            if(xhr.status==200){
                 showAlertMessage("trigger_alertsMsg", "success", "Corrupted Message Triggered");
                 setTimeout(clearAlertMessage, 2000, "trigger_alertsMsg");
            }else{
               showAlertMessage("trigger_alertsMsg", "warning", "Failed to trigger Corrupted Message");
               setTimeout(clearAlertMessage, 2000, "trigger_alertsMsg");
            }

        }
    });
}
function lossOfMQListener(inputData) {
    var cmdbId = $("#cmdbId").val();
    var env = "null";
    if($.trim($("#environment").val()) != "") {
        env=$("#environment").val();
    }
    var server = 0;
    if($.trim($("#serverId").val()) != ""){
        server = $("#serverId").val();
    }

    $.ajax({
        contentType: "application/json;charset=UTF-8",
        url: "/api/v1/activeMQ/lossOfMQListener/"+server+"/"+env+"/"+cmdbId,
        headers:headerObj,
        type: "POST",
        cache: !1,
        processData: !1,
        data: JSON.stringify(inputData),
        complete:function(xhr){
             if(xhr.status==200){
                 showAlertMessage("trigger_alertsMsg", "success", "Loss of MQ Listener Triggered");
                 setTimeout(clearAlertMessage, 2000, "trigger_alertsMsg");
            }else{
               showAlertMessage("trigger_alertsMsg", "warning", "Failed to trigger Loss of MQ Listener");
               setTimeout(clearAlertMessage, 2000, "trigger_alertsMsg");
            }
        }
    });
}

function latencyIO(inputData) {
    var cmdbId = $("#cmdbId").val();
    var env = "null";
    if($.trim($("#environment").val()) != "") {
        env=$("#environment").val();
    }
    var server = 0;
    if($.trim($("#serverId").val()) != ""){
        server = $("#serverId").val();
    }

    $.ajax({
        contentType: "application/json;charset=UTF-8",
        url: "/api/v1/chaostik/network/"+server+"/"+env+"/"+cmdbId,
        headers:headerObj,
        type: "PUT",
        cache: !1,
        processData: !1,
        data: JSON.stringify(inputData),
        success:function(data){
             showAlertMessage("trigger_alertsMsg", "success", "Latency IO Triggered");
             setTimeout(clearAlertMessage, 2000, "trigger_alertsMsg");
        },
        error:function(data){
             showAlertMessage("trigger_alertsMsg", "warning", "Failed to trigger Latency IO");
             setTimeout(clearAlertMessage, 2000, "trigger_alertsMsg");
        }
    });
}
function packetLoss(inputData) {
    var cmdbId = $("#cmdbId").val();
    var env = "null";
    if($.trim($("#environment").val()) != "") {
        env=$("#environment").val();
    }
    var server = 0;
    if($.trim($("#serverId").val()) != ""){
        server = $("#serverId").val();
    }

    $.ajax({
        contentType: "application/json;charset=UTF-8",
        url: "/api/v1/chaostik/packetLoss/"+server+"/"+env+"/"+cmdbId,
        headers:headerObj,
        type: "POST",
        cache: !1,
        processData: !1,
        data: JSON.stringify(inputData),
        complete:function(xhr){
            if(xhr.status==200){
                showAlertMessage("trigger_alertsMsg", "success", "Packet Loss Triggered");
                 setTimeout(clearAlertMessage, 2000, "trigger_alertsMsg");
                }else{
                   showAlertMessage("trigger_alertsMsg", "warning", "Failed to trigger Packet Loss");
                   setTimeout(clearAlertMessage, 2000, "trigger_alertsMsg");
            }
        }
    });
}
function packetCorruption(inputData) {
    var cmdbId = $("#cmdbId").val();
    var env = "null";
    if($.trim($("#environment").val()) != "") {
        env=$("#environment").val();
    }
    var server = 0;
    if($.trim($("#serverId").val()) != ""){
        server = $("#serverId").val();
    }

    $.ajax({
        contentType: "application/json;charset=UTF-8",
        url: "/api/v1/chaostik/packetMess/"+server+"/"+env+"/"+cmdbId,
        headers:headerObj,
        type: "POST",
        cache: !1,
        processData: !1,
        data: JSON.stringify(inputData),
        complete:function(xhr){
            if(xhr.status==200){
                showAlertMessage("trigger_alertsMsg", "success", "Packet Corruption Triggered");
                setTimeout(clearAlertMessage, 2000, "trigger_alertsMsg");
            }else{
               showAlertMessage("trigger_alertsMsg", "warning", "Failed to trigger Packet Corruption");
               setTimeout(clearAlertMessage, 2000, "trigger_alertsMsg");
            }
        }
    });
}
function packetDuplication(inputData) {
    var cmdbId = $("#cmdbId").val();
    var env = "null";
    if($.trim($("#environment").val()) != "") {
        env=$("#environment").val();
    }
    var server = 0;
    if($.trim($("#serverId").val()) != ""){
        server = $("#serverId").val();
    }

    $.ajax({
        contentType: "application/json;charset=UTF-8",
        url: "/api/v1/chaostik/packetDuplication/"+server+"/"+env+"/"+cmdbId,
        headers:headerObj,
        type: "POST",
        cache: !1,
        processData: !1,
        data: JSON.stringify(inputData),
        complete:function(xhr){
            if(xhr.status==200){
                showAlertMessage("trigger_alertsMsg", "success", "Packet Duplication Triggered");
                setTimeout(clearAlertMessage, 2000, "trigger_alertsMsg");
            }else{
               showAlertMessage("trigger_alertsMsg", "warning", "Failed to trigger Packet Duplication");
               setTimeout(clearAlertMessage, 2000, "trigger_alertsMsg");
            }
        }
    });
}
function lossOfDNS(inputData) {
    var cmdbId = $("#cmdbId").val();
    var env = "null";
    if($.trim($("#environment").val()) != "") {
        env=$("#environment").val();
    }
    var server = 0;
    if($.trim($("#serverId").val()) != ""){
        server = $("#serverId").val();
    }

    $.ajax({
        contentType: "application/json;charset=UTF-8",
        url: "/api/v1/lossOfDNS/"+server+"/"+env+"/"+cmdbId,
        headers:headerObj,
        type: "POST",
        cache: !1,
        processData: !1,
        data: JSON.stringify(inputData),
        complete:function(xhr){
            if(xhr.status==200){
                showAlertMessage("trigger_alertsMsg", "success", "Loss Of DNS Triggered");
                setTimeout(clearAlertMessage, 2000, "trigger_alertsMsg");
            }else{
               showAlertMessage("trigger_alertsMsg", "warning", "Failed to triggerLoss Of DNS");
               setTimeout(clearAlertMessage, 2000, "trigger_alertsMsg");
            }
        }
    });
}

function cpu(inputData) {
    var cmdbId = $("#cmdbId").val();
    var env = "null";
    if($.trim($("#environment").val()) != "") {
        env=$("#environment").val();
    }
    var server = 0;
    if($.trim($("#serverId").val()) != ""){
        server = $("#serverId").val();
    }

    $.ajax({
        contentType: "application/json;charset=UTF-8",
        url: "/api/v1/chaostik/cpu/"+server+"/"+env+"/"+cmdbId,
        headers:headerObj,
        type: "PUT",
        cache: !1,
        processData: !1,
        data: JSON.stringify(inputData),
        success:function(data){
        if(data=="Please enter duration or worker greater than 0!!!"||data=="Insufficient number of parameters"){
               showAlertMessage("trigger_alertsMsg", "warning", "Failed to trigger CPU");
               setTimeout(clearAlertMessage, 2000, "trigger_alertsMsg");
            }
        else {
            showAlertMessage("trigger_alertsMsg", "success", "CPU Triggered");
            setTimeout(clearAlertMessage, 2000, "trigger_alertsMsg");
        }

        },
        error:function(data){
             showAlertMessage("trigger_alertsMsg", "warning", "Failed to trigger CPU");
             setTimeout(clearAlertMessage, 2000, "trigger_alertsMsg");

        }
    });
}
function memory(inputData) {
    var cmdbId = $("#cmdbId").val();
    var env = "null";
    if($.trim($("#environment").val()) != "") {
        env=$("#environment").val();
    }
    var server = 0;
    if($.trim($("#serverId").val()) != ""){
        server = $("#serverId").val();
    }

    $.ajax({
        contentType: "application/json;charset=UTF-8",
        url: "/api/v1/chaostik/memory/"+server+"/"+env+"/"+cmdbId,
        headers:headerObj,
        type: "PUT",
        cache: !1,
        processData: !1,
        data: JSON.stringify(inputData),
        success:function(data){
        if(data=="Insufficient number of parameters"||data=="Please enter duration or worker greater than 0!!!"){
           showAlertMessage("trigger_alertsMsg", "warning", "Failed to trigger Memory");
           setTimeout(clearAlertMessage, 2000, "trigger_alertsMsg");
        }
        else {
            showAlertMessage("trigger_alertsMsg", "success", "Memory Triggered");
            setTimeout(clearAlertMessage, 2000, "trigger_alertsMsg");
        }
        },
        error:function(data){
           showAlertMessage("trigger_alertsMsg", "warning", "Failed to trigger Memory");
           setTimeout(clearAlertMessage, 2000, "trigger_alertsMsg");
        }
    });
}
function process(inputData) {
    var cmdbId = $("#cmdbId").val();
    var env = "null";
    if($.trim($("#environment").val()) != "") {
        env=$("#environment").val();
    }
    var server = 0;
    if($.trim($("#serverId").val()) != ""){
        server = $("#serverId").val();
    }

    $.ajax({
        contentType: "application/json;charset=UTF-8",
        url: "/api/v1/chaostik/process/"+server+"/"+env+"/"+cmdbId,
        headers:headerObj,
        type: "PUT",
        cache: !1,
        processData: !1,
        data: JSON.stringify(inputData),
        complete:function(xhr){
          if(xhr.status==200){
            showAlertMessage("trigger_alertsMsg", "success", "Process Triggered");
            setTimeout(clearAlertMessage, 2000, "trigger_alertsMsg");
          }else{
                   showAlertMessage("trigger_alertsMsg", "warning", "Failed to trigger Process");
                   setTimeout(clearAlertMessage, 2000, "trigger_alertsMsg");
          }
        },
        error:function(data){
               showAlertMessage("trigger_alertsMsg", "warning", "Failed to trigger Process");
               setTimeout(clearAlertMessage, 2000, "trigger_alertsMsg");
        }
    });
}
function diskIO(inputData) {
    var cmdbId = $("#cmdbId").val();
    var env = "null";
    if($.trim($("#environment").val()) != "") {
        env=$("#environment").val();
    }
    var server = 0;
    if($.trim($("#serverId").val()) != ""){
        server = $("#serverId").val();
    }

    $.ajax({
          contentType: "application/json;charset=UTF-8",
          url: "/api/v1/chaostik/io/"+server+"/"+env+"/"+cmdbId,
          headers:headerObj,
          type: "PUT",
          cache: !1,
          processData: !1,
          data: JSON.stringify(inputData),
          success:function(data){

          if(data=="Insufficient number of parameters"||data=="Please enter duration or worker greater than 0!!!"){
            showAlertMessage("trigger_alertsMsg", "warning", "Failed to trigger Disk I/O");
            setTimeout(clearAlertMessage, 2000, "trigger_alertsMsg");
          }
          else{
            showAlertMessage("trigger_alertsMsg", "success", "Disk I/O Triggered");
            setTimeout(clearAlertMessage, 2000, "trigger_alertsMsg");
          }
          },
          error:function(data){
             showAlertMessage("trigger_alertsMsg", "warning", "Failed to trigger DiskI/O");
             setTimeout(clearAlertMessage, 2000, "trigger_alertsMsg");
          }
    });
}
function fileSystem(inputData) {
    var cmdbId = $("#cmdbId").val();
    var env = "null";
    if($.trim($("#environment").val()) != "") {
        env=$("#environment").val();
    }
    var server = 0;
    if($.trim($("#serverId").val()) != ""){
        server = $("#serverId").val();
    }

    $.ajax({
          contentType: "application/json;charset=UTF-8",
          url: "/api/v1/chaostik/diskFull/"+server+"/"+env+"/"+cmdbId,
          headers:headerObj,
          type: "POST",
          cache: !1,
          processData: !1,
          data: JSON.stringify(inputData),
          complete:function(xhr){
                if(xhr.status==200){
                     showAlertMessage("trigger_alertsMsg", "success", "File System Triggered");
                     setTimeout(clearAlertMessage, 2000, "trigger_alertsMsg");

                    }else{
                   showAlertMessage("trigger_alertsMsg", "warning", "Failed to trigger File System");
                   setTimeout(clearAlertMessage, 2000, "trigger_alertsMsg");
                }
          }
    });
}
function timeDrift(inputData) {
    inputData.inputDate = new Date(inputData.inputDate).getTime()/1000;
    var cmdbId = $("#cmdbId").val();
    var env = "null";
    if($.trim($("#environment").val()) != "") {
        env=$("#environment").val();
    }
    var server = 0;
    if($.trim($("#serverId").val()) != ""){
        server = $("#serverId").val();
    }

    $.ajax({
        contentType: "application/json;charset=UTF-8",
        url: "/api/v1/chaostik/timeDrift/"+server+"/"+env+"/"+cmdbId,
        headers:headerObj,
        type: "POST",
        cache: !1,
        processData: !1,
        data: JSON.stringify(inputData),
        complete:function(xhr){
            if(xhr.status==200){
                 showAlertMessage("trigger_alertsMsg", "success", "Time Drift Triggered");
                 setTimeout(clearAlertMessage, 2000, "trigger_alertsMsg");
            }else{
                 showAlertMessage("trigger_alertsMsg", "warning", "Failed to trigger Time Drift");
                 setTimeout(clearAlertMessage, 2000, "trigger_alertsMsg");
            }
        }
    });
}

// Remove Function call
function removeFun(obj,uid) {
    console.log("remove:"+uid);
    $("#s-"+uid).remove();
    $("#"+uid).remove();
    var index = 0;
    if(uid.indexOf("auth") != -1) {
        index = parseInt(uid.replace("auth-",""));
        inputSummary.authentication[index].testConfig.duration="";
        if(index == 0) {
            inputSummary.authentication[index].testConfig.certPath="";
        }else{
            inputSummary.authentication[index].testConfig.userId="";
        }
    }else if(uid.indexOf("bat") != -1) {
        index = parseInt(uid.replace("bat-",""));
        inputSummary.batch[index].testConfig.duration="";
        inputSummary.batch[index].testConfig.fileLocation="";
        inputSummary.batch[index].testConfig.fileName="";
        if(index == 1) {
            inputSummary.batch[index].testConfig.separator="";
        }
    }else if(uid.indexOf("dbs") != -1) {
        index = parseInt(uid.replace("dbs-",""));
        inputSummary.database[1].testConfig.driverClassName = "";
        inputSummary.database[1].testConfig.url = "";
        inputSummary.database[1].testConfig.databaseType = "";
        inputSummary.database[1].testConfig.userName = "";
        inputSummary.database[1].testConfig.password = "";
        if(index == 0) {
            inputSummary.database[0].testConfig.tableName = "";
        }else if(index == 1) {
            inputSummary.database[1].testConfig.maxPoolSize = "";
        }
    }else if(uid.indexOf("mwt") != -1) {
        index = parseInt(uid.replace("mwt-",""));
        inputSummary.middleWare[1].testConfig.duration = "";
        inputSummary.middleWare[1].testConfig.ip = "";
        inputSummary.middleWare[1].testConfig.port = "";
        if(index == 0){
            inputSummary.middleWare[0].testConfig.password = "";
            inputSummary.middleWare[0].testConfig.queueName = "";
            inputSummary.middleWare[0].testConfig.userName = "";
        }else if(index == 1){
            inputSummary.middleWare[1].testConfig.password = "";
            inputSummary.middleWare[1].testConfig.queueName = "";
            inputSummary.middleWare[1].testConfig.userName = "";
        }
    }else if(uid.indexOf("nwt") != -1) {
        index = parseInt(uid.replace("nwt-",""));
        inputSummary.network[1].testConfig.duration = "";
        if(index == 0){
            inputSummary.network[0].testConfig.latency = "";
            inputSummary.network[0].testConfig.direction = "";
            inputSummary.network[0].testConfig.ip = "";
            inputSummary.network[0].testConfig.port = "";
        }else if(index == 4){
            inputSummary.network[4].testConfig.hostName = "";
        }else {
            inputSummary.network[index].testConfig.percentage = "";
        }
    }else if(uid.indexOf("ser") != -1) {
        index = parseInt(uid.replace("ser-",""));
        if(index == 0) {
            inputSummary.server[index].testConfig.duration="";
            inputSummary.server[0].testConfig.workers = "";
        }else if(index == 1) {
            inputSummary.server[index].testConfig.duration="";
            inputSummary.server[1].testConfig.workers = "";
        }else if(index == 2) {
            inputSummary.server[2].testConfig.processName = "";
        }else if(index == 3) {
            inputSummary.server[index].testConfig.duration="";
            inputSummary.server[3].testConfig.workers = "";
        }else if(index == 4) {
            inputSummary.server[4].testConfig.diskName = "";
            inputSummary.server[4].testConfig.duration = "";
        }else if(index == 5) {
            inputSummary.server[5].testConfig.inputDate = "";
            inputSummary.server[5].testConfig.duration = "";
        }
    }
    console.log(inputSummary);
}