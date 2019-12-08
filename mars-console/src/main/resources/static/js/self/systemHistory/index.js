
$(function () {
    manager.loadEnv("envCode");

    manager.loadApp("appName");
    loadNoSearchSelect("envCode");
    loadNoSearchSelect("appName");
    $("#dataTables_length").hide();
    $("#dataTables_filter").hide();
})
var oldContent;
var editMode;
function addView() {
    $(".CodeMirror").remove();
    $("#addContent").val('');

    manager.loadEnv("addEnvCode");
    $("#addEnvCode").val($("#envCode").val());

    manager.loadApp("addAppName");
    $("#addAppName").val($("#appName").val());

    loadNoSearchSelect("addAppName");
    loadNoSearchSelect("addEnvCode");



    initCodeMirror("addContent");
    $('#addModal').modal('show');

    setTimeout(function () {
        changeMode("textile")
    },500);
}

var addEditor;
function initCodeMirror(id) {

    addEditor=CodeMirror.fromTextArea(document.getElementById(id), {
        lineNumbers: true,
        theme: 'abcdef',
        autofocus:true,
        mode: "properties",
        matchBrackets: true,
        height: 200,
        width: 500,
        lint: true,
        tabMode: 'indent',
        autoMatchParens: true,
        textWrapping: true,
        rtlMoveVisually:true,
        autofocus:false,
        showCursorWhenSelecting:true,
        gutters: ['CodeMirror-lint-markers'],
        folding: true,
        showFoldingControls: 'always',
        wordWrap: 'wordWrapColumn',
        //cursorStyle: 'line',
        automaticLayout: true,
        selectOnLineNumbers: true,
        roundedSelection: true,
        smartIndent:true,
        tabSize:4,
        styleActiveLine:false,
        cursorHeight:1.2
    });

}
var editEditor;
function editCodeMirror(id) {

    editEditor=CodeMirror.fromTextArea(document.getElementById(id), {
        lineNumbers: true,
        theme: 'abcdef',
        autofocus:true,
        mode: "properties",
        matchBrackets: true,
        height: 200,
        width: 500,
        lint: true,
        tabMode: 'indent',
        autoMatchParens: true,
        textWrapping: true,
        rtlMoveVisually:true,
        autofocus:false,
        showCursorWhenSelecting:true,
        gutters: ['CodeMirror-lint-markers'],
        folding: true,
        showFoldingControls: 'always',
        wordWrap: 'wordWrapColumn',
        //cursorStyle: 'line',
        automaticLayout: true,
        selectOnLineNumbers: true,
        roundedSelection: true,
        smartIndent:true,
        tabSize:4,
        styleActiveLine:false,
        cursorHeight:1.2
    });

}

function changeMode(mode) {
    if (addEditor){
        addEditor.setOption('autofocus',false);
        addEditor.setOption("mode",mode);
    }
}

function editChangeMode(mode) {
    if (editEditor){
        editEditor.setOption('autofocus',false);
        editEditor.setOption("mode",mode);
    }
}


function checkParam(id,msg) {
    var param=$("#"+id).val();
    if(param==null || param==''){
        alert(msg);
        return false;
    }
    return true;
}

function add() {

    var envCode =$("#addEnvCode").val();
    if(envCode==null || envCode==''){
        alert("请选择环境");
        return false;
    }
    var addAppName=$("#addAppName").val();
    if(addAppName==null || addAppName==''){
        alert("请选择应用");
        return false;
    }
    if(!checkParam("addFileName","请输入文件名称")){
        return false;
    }
    $("#addJson").val(addEditor.getValue());

    var a = $("#addForm").serializeJson();
    loading();
    $.ajax({
        url: "../system/add?v="+new Date().getTime(),
        type: "post",
        data: JSON.stringify(a),
        contentType: "application/json",
        dataType: "json",
        success: function (data) {
            loaded();
            if (data.code == "0") {
                $('#addModal').modal('hide');
                $("input[type=reset]").trigger("click");
                loadData();
            } else {
                alert(data.msg);
            }
        }
    });
}

$(".saveSystemConfig").on("click",function () {
    add();
})
$(".saveEditSystemConfig").on("click",function () {

    if (editEditor.getValue()==oldContent){
        edit();
    } else{

        $('#editModal').modal('hide');
        $('#diffModal').modal('show');

        setTimeout(function () {
            initUI("editDiff",editEditor.getValue(),oldContent,editMode);
        },500);
    }
})

function callback(data){
    $("#diffId").val(data.id);
    $('#diffModal').modal('show');
    setTimeout(function () {
        initUI("editDiff",data.preJson,data.json,"textile");
    },500);
}

$(".cancelDiff").on("click",function () {
    $('#diffModal').modal('hide');
})

function showModal(id) {
    $("#delId").val(id);
    $('#delModal').modal('show');
}

function del() {
    loading();
    $.ajax({
        url: "../system/deleteHistoryById?v="+new Date().getTime(),
        type: "post",
        data: {"id": $("#delId").val()},
        dataType: "json",
        success: function (data) {
            loaded();
            if (data.code == "0") {
                $('#delModal').modal('hide');
                loadData();
            } else {
                alert(data.msg);
            }
        }
    });

}
function rollback() {
    var id=$("#diffId").val();
    $.ajax({
        url: "../system/rollBackById?v="+new Date().getTime(),
        type: "post",
        data: {"id":id},
        dataType: "json",
        success: function (data) {
            loaded();
            if (data.code == "0") {
                $('#diffModal').modal('hide');
                loadData();
            } else {
                alert(data.msg);
            }
        }
    });
}

function edit() {
    loading();

    /*initUI("editDiff");*/

    editEditor.getValue()
    $("#editJson").val(editEditor.getValue());
    var a = $("#editForm").serializeJson();
    $.ajax({
        url: "../system/update?v="+new Date().getTime(),
        type: "post",
        data: JSON.stringify(a),
        contentType: "application/json",
        dataType: "json",
        success: function (data) {
            loaded();
            if (data.code == "0") {
                $('#editModal').modal('hide');
                $('#diffModal').modal('hide');
                loadData();
            } else {
                alert(data.msg);
            }
        }
    });
}

function publishById(id) {
    loading();

    $.ajax({
        url: "../system/publishById?v="+new Date().getTime(),
        type: "post",
        data: {id:id},
        dataType: "json",
        success: function (data) {
            loaded();
            if (data.code == "0") {
                loadData();
            } else {
                alert(data.msg);
            }
        }
    });
}

 function queryById(id) {
     $('#editModal').modal('show');
    loading();
     $(".CodeMirror").remove();
    $.ajax({
        url: "../system/queryHistoryById?v="+new Date().getTime(),
        type: "post",
        data: {"id": id},
        dataType: "json",
        success: function (data) {
            loaded();
            if (data.code==0){
                callback(data.data);
            } else {
                alert(data.msg);
            }
        }
    });
}

//查询所有用户
$(document).ready(function () {


    $("#appName").on("change",function () {
        if (this.value!=''){
            loadData();
        }
    })
});

function loadData() {

    var appName=$("#appName").val();
    var envCode=$("#envCode").val();
            $('#dataTables').dataTable().fnDestroy();
            var table = $('#dataTables').DataTable({
                ajax:{
                    url: "../system/queryHistoryAll?v="+new Date().getTime(),
                    type: "post",
                    dataType: "json",
                    data:{appName:appName,envCode:envCode},
                },
                language:dataTable.language(),
                serverSide:true,
                ordering:true,
                stateSave: true,
                searching: false,
                paging: true,
                info: true,
                bAutoWidth: false,
                lengthMenu: [[10, 20, 25, -1], [10, 20, 25, "All"]],
                columnDefs: [

                    {
                        targets: 0, render: function (data, type, full, meta) {
                            return full.fileName;
                        }
                    },
                    {
                        targets: 1, render: function (data, type, full, meta) {
                            return full.modifier;
                        }
                    },
                    {
                        targets: 2, render: function (data, type, full, meta) {
                            if(full.operationType==1){
                                return "新增";
                            }else if(full.operationType==2){
                                return "修改";
                            }else if(full.operationType==3){
                                return "删除";
                            }else if(full.operationType==4){
                                return "导入";
                            }else if(full.operationType==5){
                                return "发布全部";
                            }else if(full.operationType==6){
                                return "回滚";
                            }else{
                                return "";
                            }
                        }
                    },
                    {
                        targets:3, render: function (data, type, full, meta) {
                            if(full.modifyTime!=null){
                                return moment(full.modifyTime).format("YYYY-MM-DD HH:mm:ss");
                            }else{
                                return moment(full.createTime).format("YYYY-MM-DD HH:mm:ss");
                            }
                        }
                    },
                    {
                        targets:4, render: function (data, type, full, meta) {

                            var html2= ""
                                + '&nbsp;&nbsp;<a class="btn btn-success btn-" onclick="queryById(\'' + full.id + '\')"><i class="glyphicon glyphicon-edit"></i>查看 </a>'
                                + '&nbsp;&nbsp;<a class="btn btn-warning btn-circle" onclick="showModal(\'' + full.id + '\',this)"> <i class="glyphicon glyphicon-trash"></i>删除</a>';
                                return html2;
                        }
                    }
                ]

            });

}




