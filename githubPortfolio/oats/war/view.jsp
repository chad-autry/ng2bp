<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<%
    //First thing first, get the user and set on the context so the script and body can access it
    //TODO Turn this into a tag instead of having the code here
    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();
    if (user != null) {
        pageContext.setAttribute("user", user);
    }
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<style>
.splitter-bar-horizontal {
    height: 6px;
    background-image: url(images/hgrabber.gif);
    background-repeat: no-repeat;
    background-position: center;
}
#tabs-1.ui-widget-content { border:none; padding:0px; margin:0px;}
#tabs.ui-widget-content { border:none; padding:0px; margin:0px;}
#tabs.ui-widget { border:none; padding:0px; margin:0px;width:100%; height:100%}
#MySplitter.hsplitbar {
    height: 20px;
    background: url(images/hgrabber.gif) no-repeat center;
    /* No margin, border, or padding allowed */
}
.dygraph-legend > span { display: none; }

.dygraph-legend > span.highlight { display: inline; }


</style>
    <script src="scripts/dygraph-combined.js"></script>

        <script src="http://code.jquery.com/jquery-1.10.2.min.js"></script>
   <script src="http://cdn.datatables.net/1.10.0/js/jquery.dataTables.js"></script>
    <script src="http://ajax.googleapis.com/ajax/libs/mootools/1.4.5/mootools-nocompat-yui-compressed.js"></script>
   <!--  <script src="//ajax.googleapis.com/ajax/libs/mootools/1.4.5/mootools-yui-compressed.js"></script>-->
    <link href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.10.3/themes/smoothness/jquery-ui.css" rel="stylesheet" type="text/css"/>
    <!--  <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>-->
    <script src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.10.3/jquery-ui.min.js"></script>
<link href="http://cdn.datatables.net/1.10.0/css/jquery.dataTables.css" rel="stylesheet" type="text/css"/>
    <script src="scripts/jquery.tinysort.min.js"></script>
    <script type='text/javascript' src='https://www.google.com/jsapi'></script>
    <script src="scripts/splitter.js"></script>


    
    <script>
    $(function() {
    	 $("#MySplitter").splitter({type: 'h', anchorToWindow: true, onResize:splitterResize, sizeTop: 400});
    $( "#view-range-slider" ).slider({
        range: true,
        min: 0,
        max: 500,
        step: 0.01,
        values: [ 0, 60000 ],
        slide: function( event, ui ) {
          $( "#view-range-text" ).val( ui.values[ 0 ].toFixed(2) + " - " + ui.values[ 1 ].toFixed(2) + "seconds");
        }
      });
      $( "#view-range-text" ).val(  $( "#view-range-slider" ).slider( "values", 0 ).toFixed(2) +
        " - " + $( "#view-range-slider" ).slider( "values", 1 ).toFixed(2) + "seconds");
    });


    //google.load('visualization', '1.0', {'packages':['corechart','table']});
    var audioContext;
    window.addEventListener('load', init, false);
    function init() {
      try {
        // Fix up for prefixing
        window.AudioContext = window.AudioContext||window.webkitAudioContext;
        audioContext = new AudioContext();
      }
      catch(e) {
          document.getElementById("errorsDiv").innerHTML="Web Audio API is not supported in this browser";
        alert('Web Audio API is not supported in this browser');
      }
    }
    
        var gs = []; //an array of the graphs
        var ampsAndData = []; //An array of objects contain all the amp nodes and the data
        var ampAndDataReverseLookup = {}; //Reverse lookup of the amps and data object using a graphOverlayDiv id
        var blockRedraw = false;
        var contexts = 0;
        var drawnGraphs = 0;
        var maxX = 0;
        var maxY = 0;
        var minX = -1;
        var contextDuration = 0; //The duration of the contexts, used to set the view range
        var data =[];
        var interval;
        var intervalIndex;
        var myGraph;
        var myTable;
        var dataTable;
        var samplesPerSecond;
        var myTable;
        var headerHeight;
        var graphed = [];
        function reload() {
        	//Reset all variables
            gs = [];
            ampsAndData = [];
            ampAndDataReverseLookup = {};
            contexts = 0;
            drawnGraphs = 0;
            maxX = 0;
            maxY = 0;
            minX = -1;
            data =[];
            samplesPerSecond=samplesPerSecond = Math.ceil(document.getElementById("samplesPerGraph").value/contextDuration);
            //Clear out the previous inner html
            document.getElementById("graph1").innerHTML="";
            viewAllContexts();
            myGraph.destroy();
            myGraph = null;
            myTable.clear();
            
        }
        function splitterResize() {
            if (myGraph) {
            	myGraph.resize();
            }
            
            if (myTable) {
            	$('div.dataTables_scrollBody').css('height',$("#tableDiv").height() - headerHeight);
            }
        }
        function viewAllContexts() {
        	
            var fileId = getURLParameter("fileInfoId");
            jQuery.ajax({url: '/contextList', 
                data: {fileInfoId: fileId},
                success: function(objects){
                    if (contextDuration == 0) {
                        //document.getElementById("endFieldLabel").innerHTML="End "+objects.duration+":";
                        //document.getElementById("endField").value=objects.duration;
                        $( "#view-range-slider" ).slider("option", "max", objects.duration); 
                        contextDuration = objects.duration;
                        samplesPerSecond = Math.ceil(document.getElementById("samplesPerGraph").value/contextDuration);
                    }
                    contexts = objects.frequencyContexts.length;
                    Array.each(objects.frequencyContexts, function(object, index){

                        //Create an oscillator for re-synthesizing
                        var oscillator = audioContext.createOscillator();
                        //fixOscillator(oscillator);
                        oscillator.frequency.value = object.targetFrequency;
                        var amp = audioContext.createGain();
                        amp.gain.value = 0;
        
                        // Connect oscillator to amp and amp to the mixer of the context.
                        // This is like connecting cables between jacks on a modular synth.
                        oscillator.connect(amp);
                        amp.connect(audioContext.destination);
                        oscillator.start(0);
                        var ampAndData = {amp:amp};
                        ampAndData.contextId = object.contextId;
                        ampAndData.targetFrequency = object.targetFrequency;
                        ampAndData.contextName = object.contextName;
                        ampAndData.maxValue = object.maxValue;
                        ampsAndData.push(ampAndData);
                        
                        
                        //Setup the reverse lookup which is used when sorting
                       // ampAndDataReverseLookup[graphOverlayDiv.id] = ampAndData;
                        
                        //Call viewContext
                        viewContext(object.contextId, ampAndData);
                     });
                },
                error : function(t,v){
                    document.getElementById("errorsDiv").innerHTML="Sorry, your request failed"+JSON.stringify(t) + " " + v;
                }
            });
        }
        
        function graphedChanged(index) {
        	graphed[index] = !graphed[index];
        	myGraph.setVisibility(index, graphed[index]);
        }
        
        function viewContext(contextId, ampAndData) {
        	
        	startTime = $( "#view-range-slider" ).slider( "values", 0 );
            jQuery.ajax({url: '/viewContext', 
                data: {contextId: contextId, 
                	samplesPerSecond:samplesPerSecond,
                	startTime:startTime,
                	endTime:$( "#view-range-slider" ).slider( "values", 1 )},
                success: function(objects){

                	Array.each(objects, function(object, index){
                        if (index == 0) {
                        	ampAndData.data = [];
                        }
                        ampAndData.data[index] = 0;
                        if (index < 10 || index == objects.length -1) {
                        	
                        } else {
                        	ampAndData.data[index] = 0;
                        	for (i=index;i >= index - 9; i--) {
                        	    ampAndData.data[index] = ampAndData.data[index] + objects[i];
                            }
                        	ampAndData.data[index] = ampAndData.data[index]/10;
                        }

                    });
                    //ampAndData.data=objects;
                    

                    drawnGraphs++;

                    

                    //Once the final result comes in, draw the graph and the table
                    if (drawnGraphs == contexts) {
                    	firstView = true;
                       if (myTable) {
                    	   myTable.clear();
                    	   firstView = false;
                       } else {
                           myTable = $('#myHtmlTable').DataTable({paging: false, info: false, searching: false, scrollY: 1});
                           headerHeight = $("#myHtmlTable_wrapper").height();
                           //queu up an ordering on frequency the first time shown
                           myTable.order([ 2, "asc" ]);
                       }

                        Array.each(ampsAndData, function(object, index){
                            Array.each(object.data, function(object, dataIndex){
                                if (index == 0) {
                                	data[dataIndex] = [];
                                	data[dataIndex][0] = startTime+dataIndex/samplesPerSecond;
                                }
                                data[dataIndex][index + 1] = object;

                            });
                            
                            if (firstView) {
                            	graphed[index]=true;
                            }
                            
                            myTable.row.add( ['<input type="checkbox" onChange="graphedChanged('+index+')" checked="'+graphed[index]+'">',
                                    object.contextName, 
                                    object.targetFrequency, 
                                    object.maxValue]).draw();
                        });

                        
                        
                        $('div.dataTables_scrollBody').css('height',$("#tableDiv").height() - headerHeight);
                        myTable.draw();
                        myGraph = new Dygraph(document.getElementById("graph1"), data,
                                {
                            sigFigs:2,
                            //connectSeparatedPoints:[true],
                            visibility: graphed,
                            highlightCircleSize: 2,
                           // strokeWidth: 1,
                           // strokeBorderWidth: 1,

                            highlightSeriesOpts: {
                              strokeWidth: 3,
                              strokeBorderWidth: 1,
                              highlightCircleSize: 5,
                            },
                        });
                        var onclick = function(ev) {
                            if (myGraph.isSeriesLocked()) {
                            	myGraph.clearSelection();
                            } else {
                            	myGraph.setSelection(myGraph.getSelection(), myGraph.getHighlightSeries(), true);
                            }
                          };
                          myGraph.updateOptions({clickCallback: onclick}, true);
                          myGraph.setSelection(false, 's005');
                    }
                },
                error : function(t,v){
                    document.getElementById("errorsDiv").innerHTML="Sorry, your request failed"+JSON.stringify(t) + " " + v;
                }
            });
        }
        
        function drawBMP() {
            var canvas = document.getElementById('graphOverlayCanvas');
            var imageContext = canvas.getContext('2d');
            var offset = .5; //Offset in time, not pixels
            var bpm = 60;
            var canvasXStart = gs[0].toDomXCoord(offset);
            var maxCanvasX = gs[0].toDomXCoord(maxX);
            var canvasCoordsPerBeat = (bpm/60)*(gs[0].toDomXCoord(1) - gs[0].toDomXCoord(0)); //BPM -> BPS -> Pixels Per Beat
            
            imageContext.lineWidth = 1;
            
            imageContext.beginPath();
            for (var i = canvasXStart; i <= maxCanvasX; i += canvasCoordsPerBeat) {
            var adaptedX = Math.floor(i)+0.5; //adapt the x coord to get precise lines which align to pixels
            
                imageContext.moveTo(adaptedX, 0);
                imageContext.lineTo(adaptedX, 500);
            }
            imageContext.stroke();
        }
        
        //The Play function
        function resynthesize() {
        	var startTime = audioContext.currentTime;
        	var intervalIncrement = 1000/60;
        	var intervalCount = 1;
        	var priorIntervalj = 0;
        	clearInterval(interval);
        	interval = setInterval(function(){
        	    //first indicate at what point in time sound is being played
        		myGraph.setSelection(Math.floor(samplesPerSecond*intervalCount*intervalIncrement/1000));

        		for (var i=0; i<ampsAndData.length; i++) {
        			//Don't include anything not drawn
        		    if (!graphed[i]) {
        		    	continue;
        		    }
        			tempj = 0;
                    for (var j=priorIntervalj; 
                    j<ampsAndData[i].data.length && j/samplesPerSecond < intervalCount*intervalIncrement/1000;
                    j++) {

                        ampsAndData[i].amp.gain.setValueAtTime(ampsAndData[i].data[j], startTime + j/samplesPerSecond);
                        tempj = j;
                        //On the very last set of data when passing through
                        if (i == ampsAndData.length -1) {
                        	//Save where we started from last
                        	
                        	//Check if this is the final data, and stop the timed code
	                        if (j == ampsAndData[i].data.length -1 ) {
	                            clearInterval(interval);
	                        }
                        }
                    }
                    priorIntervalj = tempj;

        		}

        		intervalCount++;
            },intervalIncrement);
        }
        
        /*
        * Discards the given frequency dataset
        */
        function discardDataset(contextId) {
            //Get the overlayDiv and hide it
            document.getElementById( "graphOverlayDiv-"+contextId).style.display='none';
            //Locate it within the ampsAndData array
            Array.each(ampsAndData, function(object, index){
                if (object.contextId == contextId) {
                    //Use splice to remove it from the array
                    ampsAndData.splice ( index, 1);
                    //Safe to do since we are not iterating over the array anymore
                    return;
                }
            });
            
            
        }
        
        function error(){
            document.getElementById("errorsDiv").innerHTML="Sorry, your request failed";
        }
        
        /*
        * Simple helper function, does what it says
        */
        function getURLParameter(name) {
            return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.search)||[,""])[1].replace(/\+/g, '%20'))||null;
        }
    </script>

</head>
<body  topmargin="0" bottommargin="0" leftmargin="0" rightmargin="0" style="height:100%" onload="viewAllContexts()">


    <!--Parent div which holds both the menu bar, toolbar, and graphs. forces the graphs div to have its scrollbar  -->
    <div id="analysis" style="position: absolute; top: 0px; bottom: 0px;width:100%;  overflow-y: hidden; overflow-x: hidden">
        <div id="menubar" style="width:100%; height:50px;">
        <!-- This div holds links to the faq and news on the left. An advertisement in the middle, and logout/account information on the right -->
            <div id="menuDiv" style="width:33%; float:left; text-align:center">
                <p>Menu Div Column</p>
            </div>
            <div id="advertDiv" style="width:33%; float:left; text-align:center">
                <p>Possible Advert Div Column</p>
            </div>
            <div id="userDiv" style="width:33%; float:left; text-align:center">
                <p>Welcome: ${fn:escapeXml(user.nickname)} 
                <a href="<%= userService.createLogoutURL(request.getRequestURI()) %>">Log Off</a></p>
            </div>
        </div>
    <!-- Div to hold the text input for saying what file to display data for -->
        <div id="toolbar" style="width:100%; height:50px;">
            <div id ="buttonsDiv" style="float:left; text-align:center"> 
                <button type="button" name="resynthButton" onclick="resynthesize()">Resynthesize</button>
            </div>
            <div id = rangeWidgetDiv style="float:left; text-align:center">
                <label for="amount">Viewed range:</label>
                <input type="text" id="view-range-text" style="border:0; color:#f6931f; font-weight:bold;">

 
                <div id="view-range-slider" ></div>
            </div>
            Samples Per Graph:<input type="text" id="samplesPerGraph" value="100"/>
            <button type="button" name="reloadButton" onclick="reload()">Reload</button>
            BMP Overlay:<input type="checkbox"/>
            BPM:<input type="text" id="bmpValue" value="80"/>
            BPM Offset:<input type="text" id="bmpValue" value="0"/>
            
            
        </div>
        
<div id="MySplitter">

        <!-- The div used to display the graphs -->
        <!--  <div id="graph" title="Graph" style="position:absolute; bottom:0px; top:100px; width:100%; overflow-y: none; z-index:1">-->
                    <!-- A canvas used to overlay all the graphs and draw BMP + playback animation on -->
			        <!-- <canvas id="graphOverlayCanvas" width=900px style="position:absolute; left:0px; top:0px; z-index:3">
			        </canvas>-->
			                <div id="graph1">

                </div>
                

                
                <div id="tableDiv" >
<table id="myHtmlTable" class="display" cellspacing="0" width="100%">
        <thead>
            <tr>
                <th>Graphed</th>
                <th>Name</th>
                <th>Frequency</th>
                <th>Max Value</th>

            </tr>
        </thead>

    </table>
                </div>
        </div>
    </div>
        <!-- The div used to display errors and messages in a dialog -->
    <div id="errorsDiv" title="Errors">
        <div id="errorResult"></div>
    </div>

</body>
</html>