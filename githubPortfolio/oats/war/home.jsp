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
<!-- This page is the home area for a user of OATS -->
<head>
<style>
#tabs-1.ui-widget-content { border:none; padding:0px; margin:0px;}
#tabs.ui-widget-content { border:none; padding:0px; margin:0px;}
#tabs.ui-widget { border:none; padding:0px; margin:0px;width:100%; height:100%}


</style>
    <script src="http://ajax.googleapis.com/ajax/libs/mootools/1.4.5/mootools-nocompat-yui-compressed.js"></script>
   <!--  <script src="//ajax.googleapis.com/ajax/libs/mootools/1.4.5/mootools-yui-compressed.js"></script>-->
    <link href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.10.3/themes/smoothness/jquery-ui.css" rel="stylesheet" type="text/css"/>
    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.9.1/jquery.min.js"></script>
    <script src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.10.3/jquery-ui.min.js"></script>
    <script type='text/javascript' src='https://www.google.com/jsapi'></script> <!-- Used for displaying the table of Files -->

    
    <script>
    
    //Set up the file uploader file validation
    function validateFile(){
        var fileSelect = document.getElementById("fileSelect");
        if (fileSelect.files.length > 0) {
            document.getElementById("uploadButton").disabled=false;
        } else {
            document.getElementById("uploadButton").disabled=true;
            return;
        }
        var name = fileSelect.files[0].name;
        var size = fileSelect.files[0].size;
        var type = fileSelect.files[0].type;

    }
    
    //Set up the submit function for the upload button
    function upload() {
        var formData = new FormData($('form')[0]);
        jQuery.ajax({
            type: 'POST',
            xhr: function() {  // Custom XMLHttpRequest
                var myXhr = $.ajaxSettings.xhr();
                if(myXhr.upload){ // Check if upload property exists
                    myXhr.upload.addEventListener('progress',progressHandlingFunction, false); // For handling the progress of the upload
                }
                return myXhr;
            },
            //Ajax events
            beforeSend: function(t,v){
                //Before we send the request, generate the blobstore url to upload to
                jQuery.ajax({
                    url: '/generateUploadUrl',
                    async: false,
                    success: function(data) {
                      v.url = data;
                    },
                    error: function(t,v){
                        //TODO handle various errors. User may not have been logged in
                        document.getElementById("errorsDiv").innerHTML="Sorry, your request failed"+JSON.stringify(t) + " " + v;
                    }
                });
            },
            success: function(objects){
                //TODO Use some sort of incrementing integer so earlier data can't override older
                dataLoaded = true;
                drawTable();
            },
            error: function(t,v){
                //TODO handle various errors. User may not have been logged in
                document.getElementById("errorsDiv").innerHTML="Sorry, your request failed"+JSON.stringify(t) + " " + v;
            },
            // Form data
            data: formData,
            //Options to tell jQuery not to process data or worry about content-type.
            cache: false,
            contentType: false,
            processData: false
        });
    }
    
    function progressHandlingFunction(e){
        if(e.lengthComputable){
            $('progress').attr({value:e.loaded,max:e.total});
        }
    }
    
    var tableLibraryLoaded = false;
    var dataLoaded = false;
    var files;
    
    google.load('visualization', '1.0', {'packages':['corechart','table']});
    google.setOnLoadCallback(tableLibraryCallback);
    
    //Can't set the boolean that the visualization library has loaded, then attempt to draw
    function tableLibraryCallback() {
        tableLibraryLoaded = true;
        drawTable();
    }
    
    function loadFiles() {
        //Load the file information of the logged in user
        jQuery.ajax({url: '/viewFiles', 
            success: function(objects){
                //Set the boolean that the data is loaded, then attempt to draw
                files = objects.files;
                dataLoaded = true;
                drawTable();

            },
            error : function(t,v){
                document.getElementById("errorsDiv").innerHTML="Sorry, your request failed"+JSON.stringify(t) + " " + v;
            }
        });
    }
        
    function drawTable() {
        //can't draw unless both the data and the visualization library are here
        if (dataLoaded && tableLibraryLoaded) {
             var data = new google.visualization.DataTable();
            data.addColumn('string', 'Name');
            data.addColumn('string', 'Progress');

            Array.each(files, function(object, index){
                data.addRow([object.fileName, '<a href="http://localhost:8888/view.jsp?fileInfoId='+object.fileInfoId+'">View</a>']);
            });
            var table = new google.visualization.Table(document.getElementById('files'));
            table.draw(data, {allowHtml: true});
        }
    }


    </script>

</head>
<body  topmargin="0" bottommargin="0" leftmargin="0" rightmargin="0" style="height:100%" onload="loadFiles()">

    <!--Parent div which holds both the menu bar and uploads list, forces the upload list div to have its own scrollbar  -->
    <div id="windowParent" style="position: absolute; top: 0px; bottom: 0px;width:100%;  overflow-y: hidden">
    <!-- Div to hold the text input for saying what file to display data for -->
        <div id="toolbar" style="width:100%; height:50px;">
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
                
        <!-- The div used to show the body of the page, including upload widget and table -->
        <div id="body" style="position:absolute; bottom:0px; top:50px; width:100%; overflow-y: scroll">
        <h1>Upload a file to Analyze</h1>
        <form enctype="multipart/form-data">
            <input id="fileSelect" name="myFile" type="file" onchange="validateFile()"/>
            <input id ="uploadButton" type="button" value="Upload" onclick="upload()" disabled/>
        </form>
        <progress></progress>
            <div id="files">
            </div>
             <!--  <button type="button" name="uploadButton" onclick="upload()">Upload</button>-->
        </div>
    </div>
        <!-- The div used to display errors and messages in a dialog -->
    <div id="errorsDiv" title="Errors">
        <div id="errorResult"></div>
    </div>

</body>
</html>