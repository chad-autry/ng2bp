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
#tabs-1.ui-widget-content { border:none; padding:0px; margin:0px;}
#tabs.ui-widget-content { border:none; padding:0px; margin:0px;}
#tabs.ui-widget { border:none; padding:0px; margin:0px;width:100%; height:100%}


</style>
    <script src="scripts/dygraph-combined.js"></script>

    <script src="http://ajax.googleapis.com/ajax/libs/mootools/1.4.5/mootools-nocompat-yui-compressed.js"></script>
   <!--  <script src="//ajax.googleapis.com/ajax/libs/mootools/1.4.5/mootools-yui-compressed.js"></script>-->
    <link href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.10.3/themes/smoothness/jquery-ui.css" rel="stylesheet" type="text/css"/>
    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
    <script src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.10.3/jquery-ui.min.js"></script>
    <script src="scripts/jquery.tinysort.min.js"></script>

    
    <script>
    $(function() {
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
            //Clear out the previous inner html
            document.getElementById("graphs").innerHTML="";
            viewAllContexts();
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
                    }
                    contexts = objects.frequencyContexts.length;
                    Array.each(objects.frequencyContexts, function(object, index){
                        //Create a div to view the context with
                        var graphsDiv = document.getElementById("graphs")
                        
                        //This is an overlayDiv to help position titles and my other custom components absolutely over the graph
                        var graphOverlayDiv = document.createElement('div');
                        graphOverlayDiv.style.position= "relative";
                        graphOverlayDiv.id = "graphOverlayDiv-"+object.contextId;
                        graphsDiv.appendChild(graphOverlayDiv);
                        
                        //A div to display the title of the context centered on the graph
                        var titleDiv = document.createElement('div');
                        graphOverlayDiv.appendChild(titleDiv);
                        titleDiv.id = "titleDiv-"+object.contextId;
                        titleDiv.style.width="100%";
                        titleDiv.style.textAlign="center";
                        titleDiv.style.position= "absolute";
                        titleDiv.style.top="0";
                        titleDiv.style.zIndex="2000";
                        titleDiv.innerHTML="Target Frequency:  " + object.targetFrequency;
                        
                        //The discard button 
                        var discardButton = document.createElement('button');
                        discardButton.innerHTML ='Discard';
                        discardButton.onclick = function(){discardDataset(object.contextId);};
                        discardButton.style.position= "absolute";
                        discardButton.style.zIndex="2001";
                        //titleDiv.appendChild(discardButton);
                        graphOverlayDiv.appendChild(discardButton);
                        
                        //The div itself which will be given to dygraphs to draw with
                        var graphDiv = document.createElement('div');
                        graphDiv.style.width="100%";
                        graphDiv.id = "graphDiv-"+object.contextId;
                        graphOverlayDiv.appendChild(graphDiv);
                        
                        //Create an oscillator for re-synthesizing
                        var oscillator = audioContext.createOscillator();
                        //fixOscillator(oscillator);
                        oscillator.frequency.value = object.targetFrequency;
                        var amp = audioContext.createGainNode();
                        amp.gain.value = 0;
        
                        // Connect ooscillator to amp and amp to the mixer of the context.
                        // This is like connecting cables between jacks on a modular synth.
                        oscillator.connect(amp);
                        amp.connect(audioContext.destination);
                        oscillator.start(0);
                        var ampAndData = {amp:amp};
                        ampAndData.contextId = object.contextId;
                        ampAndData.targetFrequency = object.targetFrequency
                        ampsAndData.push(ampAndData);
                        
                        
                        //Setup the reverse lookup which is used when sorting
                        ampAndDataReverseLookup[graphOverlayDiv.id] = ampAndData;
                        
                        //Call viewContext
                        viewContext(object.contextId, ampAndData);
                     });
                },
                error : function(t,v){
                    document.getElementById("errorsDiv").innerHTML="Sorry, your request failed"+JSON.stringify(t) + " " + v;
                }
            });
        }
        
        function viewContext(contextId, ampAndData) {
            jQuery.ajax({url: '/viewContext', 
                data: {contextId: contextId, 
                	maxSamples:document.getElementById("samplesPerGraph").value,
                	startTime:$( "#view-range-slider" ).slider( "values", 0 ),
                	endTime:$( "#view-range-slider" ).slider( "values", 1 )},
                success: function(objects){

                    ampAndData.data=objects;
                    var graph = new Dygraph(document.getElementById("graphDiv-"+contextId),
                            objects,
                            {
                                labels: [ "seconds", "y" ],
                                sigFigs:2,
                                //connectSeparatedPoints:[true],
                                stepPlot:[true],
                            }
                        );
                    gs.push(graph);
                    drawnGraphs++;
                    if (minX < 0 || graph.xAxisRange()[0] < minX) {
                        minX = graph.xAxisRange()[0];
                    }
                    if (graph.xAxisRange()[1] > maxX) {
                        maxX = graph.xAxisRange()[1];
                    }
                    
                    if (graph.yAxisRange()[1] > maxY) {
                        maxY = graph.yAxisRange()[1];
                    }
                    

                    
                    if (drawnGraphs == contexts) {
                        Array.each(gs, function(object, index){
                            object.updateOptions( {
                              dateWindow: [minX, maxX],
                              valueRange: [0, maxY]
                            } );
                          });
                        //Sort the graph divs
                        sortByFrequency("asc");
                        //Draw the BPM overlay
                        drawBMP();
                        
                        //TODO Remove the loading screen and enable buttons
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
        
        function resynthesize() {
            //The time at which the button was hit, with a 1 second buffer
            //TODO Synchronize this with an animation on the graph(s)
            var startTime = audioContext.currentTime + 1;
           
            var maxXHit = false;
            var difference = 0;
            //For each value starting with the first

                //For every graph's data
                Array.each(ampsAndData, function(object, index){
                    Array.each(object.data, function(object2, index){
                        //Set the gain at the given time (relative to start) to the given value
                         object.amp.gain.setValueAtTime(object2[1], startTime + object2[0] - minX);
                    
                    });
                });
            
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
        
        /*
        * A function to sort the graphs by frequency and the given direction "asc" or "desc")
        */
        function sortByFrequency(direction) {
            $('div#graphs > div').tsort('',{sortFunction:function(a,b){
                //The jquery serach string will give all direct child divs of the parent div called "graphs" (all the graphOverlay divs)
                var frequencyA = parseFloat(ampAndDataReverseLookup[a.e[0].id].targetFrequency);
                var frequencyB = parseFloat(ampAndDataReverseLookup[b.e[0].id].targetFrequency);
                var value = frequencyA===frequencyB?0:(frequencyA>frequencyB?1:-1);
                if (direction === "asc") {
                    return value;
                } else {
                    return -1*value;
                }
                
                }});
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
    <div id="analysis" style="position: absolute; top: 0px; bottom: 0px;width:100%;  overflow-y: hidden">
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
                <button type="button" name="sortFreqDesc" onclick="sortByFrequency('desc')">Sort by Freq Desc</button>
                <button type="button" name="sortFreqAsc" onclick="sortByFrequency('asc')">Sort by Freq Asc</button>
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
        

        
        <!-- The div used to display the graphs -->
        <div id="graphs" title="Graphs" style="position:absolute; bottom:0px; top:100px; width:100%; overflow-y: scroll; z-index:1">
                    <!-- A canvas used to overlay all the graphs and draw BMP + playback animation on -->
			        <canvas id="graphOverlayCanvas" width=900px style="position:absolute; left:0px; top:0px; z-index:3">
			        </canvas>
        </div>
    </div>
        <!-- The div used to display errors and messages in a dialog -->
    <div id="errorsDiv" title="Errors">
        <div id="errorResult"></div>
    </div>

</body>
</html>