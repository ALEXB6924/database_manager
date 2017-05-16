/**
 * Created by alexandru_bobernac on 5/12/17.
 */

/*-----------------------------CREATE EDITOR-------------------------------------*/
window.onload = function() {
    var mime = 'text/x-mariadb';
    // get mime type
    if (window.location.href.indexOf('mime=') > -1) {
        mime = window.location.href.substr(window.location.href.indexOf('mime=') + 5);
    }
    window.editor = CodeMirror.fromTextArea(document.getElementById('editor'), {
        mode: mime,
        lineWrapping: true,
        indentWithTabs: true,
        smartIndent: true,
        lineNumbers: true,
        matchBrackets : true,
        autofocus: true,
        matchBrackets: true,
        extraKeys: {"Ctrl-Space": "autocomplete"},
        renderLine: true,
        hintOptions: {tables: {
            users: {name: null, score: null, birthDate: null},
            countries: {name: null, population: null, size: null}
        }}
    });
};
/*----------------------------------------------------------------------------------*/

function create_table(startDate, endDate) {

    var contextPath = '/' + (window.location.pathname.split('/').length>2 ? window.location.pathname.split('/')[1] : '');
    var endPath = '/getLogs';
    var pathname = contextPath=='/' ? endPath : contextPath + endPath;

    var table = $('#logTable').DataTable({
        "paging":         true,
        "processing": true,
        "retrieve": true,
        "order": [],
        "ajax": {
            "url": pathname,
            "data":{
                "startDate": startDate,
                "endDate": endDate
            },
            "dataSrc": "logs",
            "type": "GET"
        },
        "columns": [
            { "data": "ID" },
            { "data": "Username" },
            { "data": "DatabaseUser" },
            { "data": "Database" },
            { "data": "Statement" },
            { "data": "CreateDate" }
        ]
    });
};

/*---------------------- SELECT TABLE OR DISPLAY TABLE CONTENT -----------------------------------*/
$(document).ready(function(){

    var origin = window.location.origin;
    var contextPath = '/' + (window.location.pathname.split('/').length>2 ? window.location.pathname.split('/')[1] : '');
    var endPath = '/databaseStructureTableColumns.json';
    var pathname = origin + (contextPath=='/' ? endPath : contextPath + endPath);

    var clicks = 0;
    $('.tables ul li').live({
        click: function() {
            node = $(this);
            clicks++;
            if (clicks == 1) {
                setTimeout(function() {
                    if(clicks == 1) {
                        if(node.hasClass("selected")){
                            node.removeClass('selected');
                            $('#databaseTableColumn li').remove();
                            $('#databaseTableColumn').append('<li>No table is selected.</li>');
                        }else{
                            $('ul').children().removeClass('selected');
                            node.addClass('selected');
                            var selectedTable = $.trim(node.text());
                            $.getJSON(pathname, {table : selectedTable},  function(data){
                                $('#databaseTableColumn li').remove();
                                $.each(data.databaseStructure, function(key, val){
                                    $('#databaseTableColumn').append('<li>'+val.column+' '+'<span id="column_types">' + val.column_type + '<span>'+'</li>');
                                });
                            });
                        }
                    } else {
                        var databaseName = $.trim(node.text());
                        editor.replaceRange(databaseName, CodeMirror.Pos(editor.lastLine()));
                    }
                    clicks = 0;
                }, 200);
            }
        }
    });
});
/*--------------------------------------------------------------------------------------------------*/

/*------------------------------------- SELECT COLUMN ------------------------------------------*/
$(document).on('dblclick', '.tableColumns ul li', function(){
    var databaseName = $.trim($(this).text());
    editor.replaceRange(databaseName, CodeMirror.Pos(editor.lastLine()));
});
/*---------------------------------------------------------------------------------------------*/

/*--------------------CREATE CALENDAR-------------------*/
$(function(){
    $('#startDate').datepicker({
        dateFormat: 'yy-mm-dd',
        changeMonth: true,
        changeYear: true
    });
    $('#endDate').datepicker({
        dateFormat: 'yy-mm-dd',
        changeMonth: true,
        changeYear: true
    });
});
/*------------------------------------------------------*/

/*----------------------- FILTER LIST ------------------------------*/
function filterList() {
    // Declare variables
    var input, filter, ul, li, a, i;
    input = $('#tableSearch');
    filter = input.val().toUpperCase();
    ul = $('#databaseTable');
    li = ul.children();

    // Loop through all list items, and hide those who don't match the search query
    for (i = 0; i < li.length; i++) {
        a = li[i].getElementsByTagName('a')[0];
        if (a.innerHTML.toUpperCase().indexOf(filter) > -1) {
            li[i].style.display = '';
        } else {
            li[i].style.display = 'none';
        }
    }
}

/*------------------------------------------------------------------*/

function removeError(){
    $('.error').remove();
}

function removeSuccess(){
    $('.success').remove();
}

/*----------------------------------------------------------- RESULT TABLE ---------------------------------------------------------------*/
$(document).on('click', '#execute', function(){

    var contextPath = '/' + (window.location.pathname.split('/').length>2 ? window.location.pathname.split('/')[1] : '');
    var endPath = '/executeStatement';
    var pathname = contextPath=='/' ? endPath : contextPath + endPath;
    var query = editor.getValue();

    if($.trim(query)==''){
        $('.textBox').after('<span class="error"><p>Executing an empty string is not allowed!</p></span>');
        setTimeout(removeError, 2000);
        return null;
    }
    $.ajax({
        "url": pathname,
        // send data to server
        "data": {
            statement: $.trim(query)
        },
        "success": function(json) {
            if(json.error == undefined && json.success == undefined){

                var tableHeaders = [];
                $.each(json.columns, function(i, val){
                    tableHeaders += "<th>"
                        + val
                        + "</th>";
                });
                // empty container
                $(".tableContainer").empty();
                // createTable
                $(".tableContainer").append('<table id="resultTable" cellspacing="0" style="width: 100%; max-width: 1000px;"><thead><tr>'
                    + tableHeaders
                    + '</tr></thead></table>');
                // create JQuery Table
                $('#resultTable').DataTable(json, {
                    "paging":         true,
                    "processing": true,
                    "retrieve": true,
                    "order": [],
                    "scrollX": true
                });
                //show table
                $('.tableContainer').show();
            }else if(json.error != undefined){
                var error = json.error[0][0];
                $('.textBox').after('<span class="error"><p>'+error+'</p></span>');
                setTimeout(removeError, 10000);
            }else if(json.success != undefined){
                var success = json.success[0][0];
                $('.textBox').after('<span class="success"><p>'+success+'</p></span>');
                setTimeout(removeSuccess, 10000);
            }
        },
        "dataType": "json"
    });
});

/*DISPLAY ALL USERS*/
$(document).on('click', '#viewUsers', function(){

    var contextPath = '/' + (window.location.pathname.split('/').length>2 ? window.location.pathname.split('/')[1] : '');
    var endPath = '/getUsers';
    var pathname = contextPath=='/' ? endPath : contextPath + endPath;

    $.ajax({
        "url": pathname,
        // send data to server
        "success": function(json) {
            var tableHeaders =[];
            $.each(json.columns, function(i, val){
                tableHeaders += "<th>"
                    + val
                    + "</th>";
            });
            // empty container
            $(".tableContainer").empty();
            console.log(tableHeaders);
            // createTable
            $(".tableContainer").append('<table id="resultTable" cellspacing="0" style="width: 100%; max-width: 1000px;"><thead><tr>'
                + tableHeaders
                + '</tr></thead></table>');
            //create JQuery Table
            $('#resultTable').DataTable(json, {
                "paging":         true,
                "processing": true,
                "retrieve": true,
                "order": [],
                "scrollX": true
            });
            //show table
            $('.tableContainer').show();

        },
        "dataType": "json"
    });
});

$(document).on('click', '#clear', function(){
    editor.setValue('');
    editor.clearHistory();
    $('#resultTable_wrapper').remove();
    $('.tableContainer').hide();
});

/*--------------------------------------------------------------------------------------------------------------------------------------------*/

/*-------------------------------CONNECT TO DATABASE AND GET TABLES----------------------------------------*/
$(document).on('click', '#connect', function(event){
    var database = $('#database').val();
    var username = $('#username').val();

    if($.trim(database)=='' || $.trim(username)==''){
        alert('The following fields are empty:\n'
            + ($.trim(database)==''?$('#database').attr('placeholder'):'')
            + ($.trim(username)==''?$('#username').attr('placeholder'):''));
        event.preventDefault();
    }
});
/*-----------------------------------------------------------------------------------------------------------*/

/*----------------------------------------------------- GET DATABASE CONTENT -----------------------------------------------------*/
$(document).ready(function(){

    var origin = window.location.origin;
    var contextPath = '/' + (window.location.pathname.split('/').length>2 ? window.location.pathname.split('/')[1] : '');
    var endPath = '/databaseStructureTables.json';
    var pathname = origin + (contextPath=='/' ? endPath : contextPath + endPath);

    $.getJSON(pathname, function(data){
        $('#databaseTable li').remove();
        $.each(data.databaseStructure, function(key, val){
            $('#databaseTable').append('<li><a href="#">'+val.table+'</a></li>');
        });
    });
});
/*---------------------------------------------------------------------------------------------------------------------------------*/

/*---------------------------CREATE USER DIALOG------------------------------------*/
$(document).on('click', '#createUser', function(){
    //Get the modal
    dialog = document.getElementById('createUserDialog');

    // Get the button that opens the modal
    cancel = document.getElementById("cancelCreate");

    // Get the <span> element that closes the modal
    span = dialog.getElementsByClassName("close")[0];

    dialog.style.display = "block";

    // When the user clicks on <span> (x), close the modal
    span.onclick = function() {
        dialog.style.display = "none";
    }

    // When the user clicks on Cancel, close the modal
    cancel.onclick = function() {
        dialog.style.display = "none";
    }

    // When the user clicks anywhere outside of the modal, close it
    window.onclick = function(event) {
        if (event.target == dialog) {
            dialog.style.display = "none";
        }
    }

});
/*---------------------------------------------------------------------------------*/

/*---------------------------DELETE USER DIALOG------------------------------------*/
$(document).on('click', '#deleteUser', function(){
    //Get the modal
    dialog = document.getElementById('deleteUserDialog');

    // Get the button that opens the modal
    //btn = document.getElementById("createUser");
    cancel = document.getElementById("cancelDelete");

    // Get the <span> element that closes the modal
    span = dialog.getElementsByClassName("close")[0];

    dialog.style.display = "block";

    // When the user clicks on <span> (x), close the modal
    span.onclick = function() {
        dialog.style.display = "none";
    }

    // When the user clicks on Cancel, close the modal
    cancel.onclick = function() {
        dialog.style.display = "none";
    }

    // When the user clicks anywhere outside of the modal, close it
    window.onclick = function(event) {
        if (event.target == dialog) {
            dialog.style.display = "none";
            $('#username').val('');
        }
    }

});
/*---------------------------------------------------------------------------------*/

/*--------------------------------- LOGS DIALOG------------------------------------*/
$(document).on('click', '#showLogs', function(){
    //Get the modal
    dialog = document.getElementById('logs');

    // Get the <span> element that closes the modal
    span = dialog.getElementsByClassName("close")[0];

    startDate = $('#startDate').val();
    endDate = $('#endDate').val();

    var data = {
        startDate: startDate,
        endDate: endDate
    }
    if($.trim(startDate) != '' && $.trim(endDate) != ''){
        if(new Date(startDate) <= new Date(endDate)){
            //append table on click
            $('#logs .dialog-content .dialog-body').append(
                '<table id="logTable" style="width: 96%;">'
                +'<thead>'
                +'<tr>'
                +'<th style="width:5%">ID</th>'
                +'<th style="width:10%">Username</th>'
                +'<th style="width:10%">DatabaseUser</th>'
                +'<th style="width:10%">Database</th>'
                +'<th style="width:45%">Statement</th>'
                +'<th style="width:20%">CreateDate</th>'
                +'</tr>'
                +'</thead>'
                +'</table>'
            );
            //create dataTable
            create_table(startDate, endDate);
            //display dialog
            dialog.style.display = "block";
        }else{
            alert('Start Date is higher als End Date');
            $('#startDate').val('')
            $('#endDate').val('')
        }
    }else{
        alert('Select the date interval!')
    }

    // When the user clicks on <span> (x), close the modal
    span.onclick = function() {
        dialog.style.display = "none";
        //remove table on dialog close
        $('#logTable_wrapper').remove();

    }

    // When the user clicks anywhere outside of the modal, close it
    window.onclick = function(event) {
        if (event.target == dialog) {
            dialog.style.display = "none";
            //remove table on dialog close
            $('#logTable_wrapper').remove();
        }
    }

});
/*----------------------------------------------------------------------------------*/

/*-----BLOCK CREATE USER FOR AEMPTY FIELDS-------*/
$(document).ready(function() {
    $("#create").click(function (event) {
        if ($("#passwd").val() != $("#repasswd").val()) {
            event.preventDefault();
            $("#createUserDialog .dialog-body .error").remove();
            $("#createUserDialog .dialog-body").append('<span class="error">Passwords do not match!</span>');
        } else if ($.trim($("#newUsername").val()) == '' || $.trim($("#passwd").val()) == '' || $.trim($("#permission").val()) == '') {
            event.preventDefault();
            $("#createUserDialog .dialog-body .error").remove();
            $("#createUserDialog .dialog-body").append('<span class="error">All fields are mandatory. Fill the empty fields!</span>');
        }
    });
});
/*-----------------------------------------------*/