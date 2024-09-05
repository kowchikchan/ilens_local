window.chartColors = {
    utilized: '#f99326',
    free: '#064690'
};

function applyFilter(){

       var fromTime=new Date(toValidDate($("#fromDateFil").val()));
       var toTime=new Date(toValidDate($("#toDateFil").val()));
       var query="";

       if($('#autoLoadFil').prop("checked")==true){
           var now = new Date();
           query="rangeTo="+now.getTime();
           now.setMinutes(now.getMinutes() - 2);
           query+="&rangeFrom="+now.getTime();
       }else {
           query="rangeFrom="+fromTime.getTime();
           query+="&rangeTo="+toTime.getTime();
       }
       if($("#cmdbId").val()!=null && $("#cmdbId").val()!=''){
            query+="&appCode="+$("#cmdbId").val();
       }
       if($("#envName").val()!=null && $("#envName").val()!=''){
            query+="&env="+$("#envName").val();
       }
       if($("#host").val()!=null && $("#host").val()!=''){
            query+="&host="+$("#host").val();
       }
       return query;
}

var diskUtilConfig = {
        responsive: true,
        maintainAspectRatio: false,
        layout:{
            padding: 10
        },
        legend: {
            labels:{
                fontColor:"#90969D"
            }
        },
        tooltips: {
            callbacks: {
                title: function(tooltipItem, data) {
                    return data['labels'][tooltipItem[0]['index']];
                },
                label: function(tooltipItem, data) {
                    return "Disk Utilized: "+data['datasets'][0]['data'][tooltipItem['index']] +" GB";
                }
            }
        },
        scales: {
            xAxes: [{
                ticks: {
                    fontColor: "#90969D",
                    autoSkip:true,

                },
                gridLines: {
                    color: "#37393F",
                    display:false
                }
            }],
            yAxes: [{
                ticks: {
                        min: 0,
                        // max: this.max,
                        suggestedMax: 10,
                        autoSkip:true,
                        callback: function(label, index, labels) {
                            return label+" GB";
                        },
                        fontColor: "#90969D",
                },
                gridLines: {
                    color: "#37393F",
                    display:false
                }
            }]
        }
};

var processConfig = {
            responsive: true,
            maintainAspectRatio: false,
            layout:{
                padding: 10
            },
            legend: {
                labels:{
                    fontColor:"#90969D"
                }
            },
            scales: {
                xAxes: [{
                    ticks: {
                        fontColor: "#90969D",
                        autoSkip:true,

                    },
                    gridLines: {
                        color: "#37393F",
                        display:false
                    }

                }],
                yAxes: [{
                    ticks: {
                            min: 0,
                            // max: this.max,
                            suggestedMax: 10,
                            autoSkip:true,
                            fontColor: "#90969D",
                    },
                    gridLines: {
                        color: "#37393F",
                        display:false
                    }
                }]
            }
        };

var globalOptions = {
            responsive: true,
            maintainAspectRatio: false,
            layout:{
                padding: 10
            },
            legend: {
                labels:{
                    fontColor:"#90969D"
                }
            },
            scales: {
                xAxes: [{
                    ticks: {
                        fontColor: "#90969D",
                        autoSkip:true,
                    },
                    gridLines: {
                        color: "#37393F",
                        display:false
                    }

                }],
                yAxes: [{
                    ticks: {
                            min: 0,
                            // max: this.max,
                            suggestedMax: 10,
                            autoSkip:true,

                        fontColor: "#90969D",
                        callback: function(label, index, labels) {
                                                return label+'%';
                        }
                    },
                    gridLines: {
                        color: "#37393F",
                        display:false
                    }
                }]
            }
        };


var sharpLineData = {
              labels: [],
              datasets: [
                  {
                      label: "Time Series CPU",
                      backgroundColor: 'rgba(255,112,0, 0.1)',
                      borderColor: "#ff7000",
                      pointBorderWidth: 0,
                      pointRadius: 2,
                      pointBorderColor: '#ff7000',
                      borderWidth: 1,
                      data: [],
                      lineTension: 0
                  }
              ]

          };

var memLineData = {
                labels: [],
                datasets: [
                    {
                        label: "Time Series Memory",
                        backgroundColor: 'rgba(255,112,0, 0.1)',
                        borderColor: "#ff7000",
                        pointBorderWidth: 0,
                        pointRadius: 2,
                        pointBorderColor: '#ff7000',
                        borderWidth: 1,
                        data: [],
                        lineTension: 0
                    }
                ]
            };

var diskLineData = {
                    labels: [],
                    datasets: [
                        {
                            label: "Time Series Disk",
                            backgroundColor: 'rgba(255,112,0, 0.1)',
                            borderColor: "#ff7000",
                            pointBorderWidth: 0,
                            pointRadius: 2,
                            pointBorderColor: '#ff7000',
                            borderWidth: 1,
                            data: [],
                            lineTension: 0
                        }
                    ]
        };

var processLineData = {
            labels: [],
            datasets: [
                {
                    label: "Time Series Process Count",
                    backgroundColor: 'rgba(255,112,0, 0.1)',
                    borderColor: "#ff7000",
                    pointBorderWidth: 0,
                    pointRadius: 2,
                    pointBorderColor: '#ff7000',
                    borderWidth: 1,
                    data: [],
                    lineTension: 0
                }
            ]

        };

var cpuTime = document.getElementById("cpu-Time-area").getContext("2d");
cpuTimeChart= new Chart(cpuTime, {type: 'line', data: sharpLineData, options: globalOptions});
cpuTimeChart.options.legend.display=false;


var memTime = document.getElementById("mem-Time-area").getContext("2d");
memLineData.datasets[0].label="Time Series for memory"
memTimeChart= new Chart(memTime, {type: 'line', data: memLineData, options: globalOptions});
memTimeChart.options.legend.display=false;

var diskTime = document.getElementById("disk-Time-area").getContext("2d");
diskLineData.datasets[0].label="Disk Utilization"
diskTimeChart= new Chart(diskTime, {type: 'line', data: diskLineData, options: diskUtilConfig});
diskTimeChart.options.legend.display=false;

var processTime = document.getElementById("process-Time-area").getContext("2d");
processLineData.datasets[0].label="Process Count"
processTimeChart= new Chart(processTime, {type: 'line', data: processLineData, options: processConfig});
processTimeChart.options.legend.display=false;

function loadTimeSeries(tokenObj,tokenValue){
           // console.log("::"+token+"::"+tokenValue);
            var headerObj={};
            headerObj[tokenObj]=tokenValue;
            //CPU..
            $.ajax({
                 url: "/api/v1/cpu/aggs/times?"+applyFilter(),
                 dataType: "json",
                 headers:headerObj,
                 async: true,
                 success:function(data){
                    var intervals=data.intervals;
                    var dataSets=data.dataSet;
                    var labelStr=[];
                    var dataList=[];
                    for(var i=0;i<dataSets.length;i++){
                        labelStr.push(getTimeDisplay(dataSets[i].time,intervals));
                        dataList.push(dataSets[i].value);
                    }
                    cpuTimeChart.data.labels=labelStr;
                    cpuTimeChart.data.datasets[0].data=dataList;
                    cpuTimeChart.update();
                 }
            });

            //Memory..

            $.ajax({
               url: "/api/v1/mem/aggs/times?"+applyFilter(),
               dataType: "json",
               headers:headerObj,
               async: true,
               success:function(data){
                    var intervals=data.intervals;
                    var dataSets=data.dataSet;
                    var labelStr=[];
                    var dataList=[];
                    for(var i=0;i<dataSets.length;i++){
                      labelStr.push(getTimeDisplay(dataSets[i].time,intervals));
                      dataList.push(dataSets[i].value);
                    }

                    memTimeChart.data.labels=labelStr;
                    memTimeChart.data.datasets[0].data=dataList;
                    memTimeChart.update();
               }
            });

                $.ajax({
                         url: "/api/v1/memory/total?"+applyFilter(),
                         dataType: "json",
                         headers:headerObj,
                         async: true,
                         success:function(data){
                            if(data != ""){
                                var val = (data.TotalMemorySize).replace("kB","");
                                $("#memorySize").html("[Total Size: "+Math.round(Number(val)/1000000)+"GB]");
                            }
                         }
                 });
                //Disk usage

                $.ajax({
                       url: "/api/v1/disk/aggs/times?"+applyFilter(),
                       dataType: "json",
                     headers:headerObj,
                       async: true,
                       success:function(data){
                          console.log("Disk: "+data);
                          var intervals=data.intervals;
                          var dataSets=data.dataSet;
                          var labelStr=[];
                          var dataList=[];
                          for(var i=0;i<dataSets.length;i++){
                              labelStr.push(getTimeDisplay(dataSets[i].time,intervals));
                              dataList.push(dataSets[i].value);
                          }

                         diskTimeChart.data.labels=labelStr;
                         diskTimeChart.data.datasets[0].data=dataList;
                         diskTimeChart.update();
                       }
                });

                $.ajax({
                         url: "/api/v1/disk/total?"+applyFilter(),
                         dataType: "json",
                         headers:headerObj,
                         async: true,
                         success:function(data){
                            if (data != ""){
                                $("#diskTotal").html("[Total Size: "+data.TotalDiskSize+"B]");
                            }
                         }
                 });
                //process count

                $.ajax({
                   url: "/api/v1/process/aggs/times?"+applyFilter(),
                   dataType: "json",
                  headers:headerObj,
                   async: true,
                   success:function(data){
                    console.log("Process: "+data);
                       var intervals=data.intervals;
                       var dataSets=data.dataSet;
                       var labelStr=[];
                       var dataList=[];
                       for(var i=0;i<dataSets.length;i++){
                          labelStr.push(getTimeDisplay(dataSets[i].time,intervals));
                          dataList.push(dataSets[i].value);
                       }

                     processTimeChart.data.labels=labelStr;
                     processTimeChart.data.datasets[0].data=dataList;
                     processTimeChart.update();
                   }
              });
         }


         function getTimeDisplay(timeVal,interval){

            var dt=new Date();
            dt.setTime(timeVal);
             var sMonth = padValue(dt.getMonth() + 1);
                         var sDay = padValue(dt.getDate());
                         var sYear = dt.getFullYear();
                         var sHour = dt.getHours();
                         var sMinute = padValue(dt.getMinutes());
                         var sSeconds = padValue(dt.getSeconds());
                         var sAMPM = "AM";

                         var iHourCheck = parseInt(sHour);

                         if (iHourCheck > 12) {
                             sAMPM = "PM";
                             sHour = iHourCheck - 12;
                         }
                         else if (iHourCheck === 0) {
                             sHour = "12";
                         }

                         sHour = padValue(sHour);

             var displayStr= sMonth + "-" + sDay + "-" + sYear + " " + sHour + ":" + sMinute + " " + sAMPM;
             var intervalTime = interval.substring(interval.length-1,interval.length);
            if(intervalTime=='s'){
            displayStr=sMinute + ":" + sSeconds ;
            }
            else if(intervalTime=="m"){
             displayStr=sHour + ":" + sMinute  ;
            }
            else if(intervalTime=='h'){
            displayStr=sMonth+"/"+sDay +" "+ sHour +":" +sMinute;
            }
            else if(intervalTime=='d'){
               displayStr= sMonth+"/"+sDay ;
            }


            return displayStr;
         }


         function formatDate(newDate) {



         }

         function padValue(value) {
             return (value < 10) ? "0" + value : value;
         }