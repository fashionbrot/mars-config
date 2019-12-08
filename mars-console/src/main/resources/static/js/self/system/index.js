
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

    var envCode = $("#envCode").val();
    if (envCode==null || envCode==''){
        manager.loadEnv("addEnvCode");
        $("#addEnvCode").val(envCode);
        loadNoSearchSelect("addEnvCode");
        $(".addEnvCode").show();
    }else{
        $("#addEnvCode").val(envCode);
        $(".addEnvCode").hide();
    }


    var appName = $("#appName").val();
    if (appName==null || appName==''){
        manager.loadApp("addAppName");
        $("#addAppName").val(appName);
        loadNoSearchSelect("addAppName");
        $(".addAppName").show();
    }else{
        $("#addAppName").val(appName);
        $(".addAppName").hide();
    }

    // loadNoSearchSelect("addAppName");
    // loadNoSearchSelect("addEnvCode");



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
        url: "./system/add?v="+new Date().getTime(),
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

        edit();
});

$(".systemConfigDiff").on("click",function () {
    $('#diffModal').modal('show');

    setTimeout(function () {
        initUI("editDiff",editEditor.getValue(),oldContent,editMode);
    },500);
});

$(".cancelDiff").on("click",function () {
    $('#diffModal').modal('hide');
    $('#editModal').modal('show');
})

function showModal(id) {
    $("#delId").val(id);
    $('#delModal').modal('show');
}

function del() {
    loading();
    $.ajax({
        url: "./system/deleteById?v="+new Date().getTime(),
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

function edit() {
    loading();

    /*initUI("editDiff");*/

    editEditor.getValue()
    $("#editJson").val(editEditor.getValue());
    var a = $("#editForm").serializeJson();
    $.ajax({
        url: "./system/update?v="+new Date().getTime(),
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
        url: "./system/publishById?v="+new Date().getTime(),
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
     /*$('#editModal').modal('show');*/
    loading();
     $(".CodeMirror").remove();
    $.ajax({
        url: "./system/queryById?v="+new Date().getTime(),
        type: "post",
        data: {"id": id},
        dataType: "json",
        success: function (dataData) {
            loaded();

            if (dataData.code==0) {
                var data= dataData.data;

                $("#id").val(data.id);
                $("#editFileName").val(data.fileName);
                $("#editFileName").attr("readonly", "readonly");
                $("#editFileDesc").val(data.fileDesc);
                var radioList = $(".editFileType");
                for (var i = 0; i < radioList.length; i++) {
                    if (radioList[i].value == data.fileType) {
                        radioList[i].checked = true;
                    } /*else {
                        radioList[i].readOnly = 'readOnly';
                    }*/
                }
                $("input[className=editFileType]").attr("disabled", true);
                $("#editContent").val(data.json);
                oldContent = data.json;
                editCodeMirror("editContent");
                //editEditor.resetAutosize();
                $("#editModal").modal("show");

                setTimeout(function () {
                    if (data.fileType == 'text') {
                        editMode = "textile";
                        editChangeMode("textile")
                    } else {
                        editMode = data.fileType;
                        editChangeMode(data.fileType);
                    }

                }, 500);
            }else{
                alert(dataData.msg)
            }
        }
    });
}

function viewDiv(id) {

    loading();
    $(".CodeMirror").remove();
    $.ajax({
        url: "./system/queryById?v="+new Date().getTime(),
        type: "post",
        data: {"id": id},
        dataType: "json",
        success: function (dataData) {
            loaded();
            if (dataData.code==0){
                var data=dataData.data;
                $("#viewFileName").val(data.fileName);
                $("#viewFileDesc").val(data.fileDesc);
                var radioList= $(".viewFileType");
                for(var i=0;i<radioList.length;i++){
                    if (radioList[i].value == data.fileType){
                        radioList[i].checked=true;
                    }else{
                        radioList[i].readOnly='readOnly';
                    }
                }
                $("input[className=viewFileType]").attr("disabled",true);
                $("#viewContent").val(data.json);
                oldContent=data.json;
                editCodeMirror("viewContent");


                setTimeout(function () {
                    if (data.fileType=='text'){
                        editMode="textile";
                        editChangeMode("textile")
                    } else{
                        editMode=data.fileType;
                        editChangeMode(data.fileType);
                    }

                },500);

                $("#viewModal").modal("show");

            } else{
                alert(dataData.msg);
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
    loading();

    var appName=$("#appName").val();
    var envCode=$("#envCode").val();
    $.ajax({
        url: "./system/queryByAppAndEnv?v="+new Date().getTime(),
        type: "post",
        dataType: "json",
        data:{appName:appName,envCode:envCode},
        success: function (data) {
            loaded();
            $('#dataTables').dataTable().fnDestroy();
            var table = $('#dataTables').DataTable({
                language:dataTable.language(),
                stateSave: true,
                searching: false,
                paging: false,
                info: true,
                bAutoWidth: false,
                lengthMenu: [[25, 50, 100, -1], [25, 50, 100, "All"]],
                data: data,
                columnDefs: [

                    {
                        targets: 0, render: function (data, type, full, meta) {
                            return full.fileName;
                        }
                    },
                    {
                        targets: 1, render: function (data, type, full, meta) {
                            return full.fileDesc;
                        }
                    },
                    {
                        targets: 2, render: function (data, type, full, meta) {
                            if (full.status == "1") {
                                return "已发布";
                            } else {
                                return "<span style='color:red;font-weight: bold;'>未发布</span>";
                            }
                        }
                    },
                    {
                        targets: 3, render: function (data, type, full, meta) {
                            return full.modifier;
                        }
                    },
                    {
                        targets:4, render: function (data, type, full, meta) {
                            if(full.modifyTime!=null){
                                return moment(full.modifyTime).format("YYYY-MM-DD HH:mm:ss");
                            }else{
                                return moment(full.createTime).format("YYYY-MM-DD HH:mm:ss");
                            }
                        }
                    },
                    {
                        targets:5, render: function (data, type, full, meta) {
                            var html='<a class="btn btn-success btn-" onclick="publishById(\'' + full.id + '\')"><i class="glyphicon glyphicon-send"></i>发布 </a>';

                            var html2= ""
                                + '&nbsp;&nbsp;<a class="btn btn-success btn-" onclick="viewDiv(\'' + full.id + '\')"><i class="glyphicon glyphicon-eye-open"></i>查看 </a>'
                                + '&nbsp;&nbsp;<a class="btn btn-success btn-" onclick="queryById(\'' + full.id + '\')"><i class="glyphicon glyphicon-edit"></i>修改 </a>'
                                + '&nbsp;&nbsp;<a class="btn btn-warning btn-circle" onclick="showModal(\'' + full.id + '\',this)"> <i class="glyphicon glyphicon-trash"></i>删除</a>';
                            if (full.status==0){
                                return html+html2;
                            }else{
                                return html+html2;
                            }
                        }
                    }
                ]

            });
        }
    });
}




