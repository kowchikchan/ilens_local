var chartConfig = {
     layout: {
        padding: {
            left: 15,
            right: 15,
            top: 10,
            bottom: 10
        }
    },
    responsive: true,
    maintainAspectRatio: false,
    title: {
        display: false,
        text: 'Time Series'
    },
    tooltips: {
        mode: 'index',
        intersect: false,
    },
    hover: {
        mode: 'nearest',
        intersect: true
    },
    legend: {
        fontColor: "#afafaf",
        fontSize: 12,
        boxWidth: 25,
        onClick: function (e) {
            e.stopPropagation();
        }
    },
    scales: {
        xAxes: [{
            display: true,
            ticks: {
                autoSkip:true,
            },
            gridLines:{
                display:false,
                zeroLineColor: '#ffffff',
                color: '#b0b6bf'
            },
            scaleLabel: {
                display: true,
            }
        }],
        yAxes: [{
            display: true,
            ticks: {
                beginAtZero: true,
                min: 0,
                max: this.max,
                autoSkip:true
            },
            gridLines:{
                display:false,
                zeroLineColor: '#ffffff',
                color: '#b0b6bf'
            },
            scaleLabel: {
                display: true,
                labelString: 'Count'
            }
        }]
    }
};

var seriesData = {
    labels: [],
    datasets : [{
        label: 'Normal',
        backgroundColor: "#52FF33",
        borderColor: "#52FF33",
        borderWidth: 1,
        data: [],
        fill: false,
    },
    {
        label: 'Warning',
        backgroundColor: "#F6FF33",
        borderColor: "#F6FF33",
        borderWidth: 1,
        data: [],
        fill: false,
    },
    {
        label: 'Danger',
        backgroundColor: "#FF4C33",
        borderColor: "#FF4C33",
        borderWidth: 1,
        data: [],
        fill: false,
    }]
};

var timeSeries;
var chartElement = document.getElementById("ctxTimeSeries").getContext("2d");
timeSeries = new Chart(chartElement,{type: 'line', data: seriesData, options: chartConfig });

function loadTimeSeries(tokenName, tokenValue) {
        var headerObj={};
        headerObj[tokenName]=tokenValue;
        if ($("#boards").val() != null && $("#boards").val() != "") {
            $.ajax({
                url: "/api/v1/traffic/timeseries?" + getFilter(),
                headers:headerObj,
                dataType: "json",
                cache:!1,
                success: function(data) {
                    //console.log(data);
                    var label=[];
                    var normalData = [];
                    var warningData = [];
                    var dangerData = [];
                    for(var i=0; i<data.length; i++){
                        label.push(getTimeDisplay(data[i].date,data[i].interval));
                        normalData.push(data[i].normal);
                        warningData.push(data[i].warning);
                        dangerData.push(data[i].danger);
                    }
                    timeSeries.data.labels = label;
                    timeSeries.data.datasets[0].data = normalData;
                    timeSeries.data.datasets[1].data = warningData;
                    timeSeries.data.datasets[2].data = dangerData;
			        timeSeries.update();
                }
            });
        }
    }

function getFilter() {
    var fromDate;
    var toDate;
    if($('#autoLoadFil').prop('checked')==true){
        toDate = new Date();
        fromDate =new Date();
        fromDate.setMinutes(toDate.getMinutes()-2);
    } else {
        fromDate = new Date(toValidDate($("#fromDateFil").val()));
        toDate = new Date(toValidDate($("#toDateFil").val()));
    }
    var query = "";
    query = "rangeFrom=" + fromDate.getTime();
    query += "&rangeTo=" + toDate.getTime();
    query += "&boardId=" + $("#boards").val();
    query += "&appCode=" + $("#appCode").val();

    return query;
}
Chart.Legend.prototype.afterFit = function() {
    this.height = this.height + 20;
};