


//添加用户
var userInfoAdd = function () {
    var envName=$("#envName").val();
    if(envName==null || envName==''){
        alert("请输入环境名称");
        return false;
    }
    var envCode =$("#envCode").val();
    if(envCode==null || envCode==''){
        alert("请输入环境code");
        return false;
    }

    var a = $("#userInfoAddForm").serializeJson();
    loading();
    $.ajax({
        url: "./env/add",
        type: "post",
        data: JSON.stringify(a),
        contentType: "application/json",
        dataType: "json",
        success: function (data) {
            loaded();
            if (data.code == "0") {
                $("#userInfoAddModal").modal("hide");
                $("input[type=reset]").trigger("click");
                loadData();
            } else {
                alert(data.msg);
            }
        }
    });
}

function showModal(id) {
    $("#userInfoId").val(id);
    $('#userInfoDelModal').modal('show');

}

var userInfoDel = function () {
    loading();
    $.ajax({
        url: "./env/deleteById",
        type: "post",
        data: {"id": $("#userInfoId").val()},
        dataType: "json",
        success: function (data) {
            loaded();
            if (data.code == "0") {
                $('#userInfoDelModal').modal('hide');
                loadData();
            } else {
                alert(data.msg);
            }
        }
    });

}

var userInfoEdit = function () {
    loading();
    var a = $("#userInfoEditForm").serializeJson();
    $.ajax({
        url: "./env/update",
        type: "post",
        data: JSON.stringify(a),
        contentType: "application/json",
        dataType: "json",
        success: function (data) {
            loaded();
            if (data.code == "0") {
                $('#userInfoEditModal').modal('hide');
                loadData();
            } else {
                alert(data.msg);
            }
        }
    });
}


var queryByUserId = function (id) {

    loading();
    $.ajax({
        url: "./env/queryById",
        type: "post",
        data: {"id": id},
        dataType: "json",
        success: function (data) {
            loaded();
            $("#envId").val(id);
            $("#editEnvCode").attr("readonly","readonly");
            $("#editEnvName").val(data.envName);
            $("#editEnvCode").val(data.envCode);
            $("#editStatus").val(data.status);
            $("#userInfoEditModal").modal("show");
        }
    });
}

//查询所有用户
$(document).ready(function () {

    $(".addProject").on("click",function () {
        $("#userInfoAddModalLabel").text("添加环境");
        $("input[name='addReset']").trigger("click");
        $("#userInfoAddModal").modal("show");
    })

    loadData();

});

function loadData() {
    loading();
    $.ajax({
        url: "./env/queryAll?v="+new Date().getTime(),
        type: "post",
        dataType: "json",
        success: function (data) {
            loaded();
            $('#dataTables-userInfo').dataTable().fnDestroy();
            var table = $('#dataTables-userInfo').DataTable({
                language: dataTable.language(),
                stateSave: true,
                searching: false,
                paging: true,
                info: true,
                bAutoWidth: false,
                lengthMenu: [[25, 50, 100, -1], [25, 50, 100, "All"]],
                data: data,
                columnDefs: [

                    {
                        targets: 0, render: function (data, type, full, meta) {
                        return full.envName;
                    }
                    },
                    {
                        targets: 1, render: function (data, type, full, meta) {
                        return full.envCode;
                    }
                    },/*
                    {
                        targets: 2, render: function (data, type, full, meta) {
                        if (full.status == "1") {
                            return "开启";
                        } else {
                            return "停用";
                        }
                    }
                    },*/
                    {
                        targets: 2, render: function (data, type, full, meta) {
                        return moment(full.lastLoginTime).format("YYYY-MM-DD HH:mm:ss");
                    }
                    },
                    {
                        targets: 3, render: function (data, type, full, meta) {
                        return '<a class="btn btn-success" onclick="queryByUserId(\'' + full.id + '\')"><i class="glyphicon glyphicon-edit">修改</i> </a>'
                            + '&nbsp;&nbsp;<a class="btn btn-warning" onclick="showModal(\'' + full.id + '\')"> <i class="glyphicon glyphicon-trash"></i>删除</a>';
                        }
                    }
                ]
            });
        }
    });
}