var ad = [];
function getPrice(name){
	
	$.ajax({
	    url: "/PriceGrabber/grabber/PriceGrabber/GetItemPriceHistory?itemName='"+name+"'",
	    type: 'GET',
	    success: function(msg) {
	    	
	    	drawBasic(addData(msg));
	    	
	    }
	});
}

function addData(response){
	ad=[];
	var prices = new Array();
	$.each(response,function(i,item) {
		var arr = new Array(item["timestamp"],item["valueDef"]);
			
		ad.push(arr);
	});
	
	
	
}
function drawBasic(data) {
	dataTable = new google.visualization.DataTable();
	var numRows = ad.length;
    var numCols = ad[0].length;
    


    // all other columns are of type 'number'.
    dataTable.addColumn('string', 'timestamp');
    dataTable.addColumn('number', 'Value Def');

    for (var i = 0; i < ad.length; i++) {
    	dataTable.addRow([ad[i][0], ad[i][1]]);
    }
                 

    options = {
            vAxis: {title: 'Value Def', titleTextStyle: {color: 'red'}},
            hAxis: {title: 'Time', titleTextStyle: {color: 'red'}}
          };
    chart = new google.visualization.LineChart(document.getElementById('chart_div'));
    chart.draw(dataTable, options);       
  }
$(document).ready(function(){
	$('#item_select').change(function(){
		var value = $(this).val();
		$('#chart_div').empty();
	    getPrice(value);
	});
	
});