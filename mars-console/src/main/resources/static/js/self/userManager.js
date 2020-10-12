


//添加用户
var userInfoAdd = function () {
    var superAdmin=$("#superAdmin").val();

    if (superAdmin==0 || superAdmin=="0"){
        var roleId=$("#addRoleId").val();
        if(roleId=='' || roleId==null){
            alert("请选择角色");
            return false;
        }
    }


    var a = $("#userInfoAddForm").serializeJson();
    loading();
    $.ajax({
        url: "./user/add",
        type: "post",
        data: JSON.stringify(a),
        contentType: "application/json",
        dataType: "json",
        success: function (data) {
            loaded();
            if (data.code == "0") {
                $("#userInfoAddModal").modal("hide");
                $("input[type=reset]").trigger("click");
                location.reload();
            } else {
                alert(data.msg);
            }
        },
        error: function (e) {
            loaded();
            alert("网络错误，请重试！！");
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
        url: "./user/deleteById",
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
        },
        error: function (e) {
            loaded();
            alert("网络错误，请重试！！");
        }
    });
}

 function userInfoEdit() {

     var superAdmin=$("#editSuperAdmin").val();

     if (superAdmin==0 || superAdmin=="0"){
         var roleId=$("#editRoleId").val();
         if(roleId=='' || roleId==null){
             alert("请选择角色");
             return false;
         }
     }


     var a = $("#userInfoEditForm").serializeJson();
    loading();
    $.ajax({
        url: "./user/update",
        type: "post",
        data: JSON.stringify(a),
        contentType: "application/json",
        dataType: "json",
        success: function (data) {
            loaded();
            if (data.code == "0") {
                $("#userInfoEditModal").modal("hide");
                loadData();
                //location.reload();
            } else {
                alert(data.msg);
            }
        },
        error: function (e) {
            loaded();
            alert("网络错误，请重试！！");
        }
    });
}

$("#editSuperAdmin").on("change",function () {
    var a =$("#editSuperAdmin").val();
    if (a==1){
        $("#editRoleIdDiv").hide();
    }else{
        $("#editRoleIdDiv").show();
    }
})
$("#superAdmin").on("change",function () {
    var a =$("#superAdmin").val();
    if (a==1){
        $("#addRoleIdDiv").hide();
    }else{
        $("#addRoleIdDiv").show();
    }
})

 function queryByUserId(id) {
    loading();
    $.ajax({
        url: "./user/queryById",
        type: "post",
        data: {"id": id},
        dataType: "json",
        success: function (result) {
            var data = result.data;
            loaded();
            if (result.code != 0) {
                alert(result.msg);
                return false;
            }

            $("#userInfoEditModal").modal("show");
            $("#editId").val(data.id);
            $("#editUserName").val(data.userName);
            $("#editRealName").val(data.realName);
            $("#editPassword").val(data.password);
            $("#editStatus").val(data.status);
            $("#editSuperAdmin").val(data.superAdmin);
            manager.loadRole("editRoleId");
            $("#editRoleId").val(data.roleId);
            if (data.superAdmin==1){
                $("#editRoleIdDiv").hide();
            }else{
                $("#editRoleIdDiv").show();
            }
            $("#userInfoEditModal").modal("show");
        },error: function (e) {
            loaded();
            alert("网络错误，请重试！！");
        }
    });

}
//查询所有用户
$(document).ready(function () {
    loadData();
});
function add() {
    manager.loadRole("addRoleId");
    $("#userInfoAddModal").modal("show");
}

function loadData() {

    $('#dataTables-userInfo').dataTable().fnDestroy();
    var table = $('#dataTables-userInfo')
        .on('xhr.dt', function (e, settings, json, xhr) {
            if (json.code == 0) {
                json.recordsTotal = json.data.itotalDisplayRecords;
                json.recordsFiltered = json.data.itotalDisplayRecords;
            } else {
                json.recordsTotal = 0;
                json.recordsFiltered = 0;
            }
        })
        .DataTable({
            ajax: {
                url: "./user/queryAll",
                type: "post",
                dataType: "json",
                data: {}
                , dataSrc: function (result) {
                    if (result.code != 0) {
                        alert(result.msg);
                        return false;
                    }
                    if (result.data != null) {
                        return result.data.data;
                    }
                    return [];
                },
                error: function (XMLHttpRequest, textStatus, errorThrown) {
                    alert("获取列表失败");
                }
            },
            dom: '<fB<t>ip>',
            stripeClasses: ["odd", "even"],
            paginationType: "full_numbers",
            serverSide: true,
            language: dataTable.language(),
            stateSave: true,
            searching: false,
            paging: true,
            info: true,
            bAutoWidth: false,
            lengthMenu: [[25, 50, 100, -1], [25, 50, 100, "All"]],
            // data: data,
            columnDefs: [
                {
                    targets: 0, render: function (data, type, full, meta) {
                        return full.userName;
                    }
                },
                {
                    targets: 1, render: function (data, type, full, meta) {
                        return full.realName;
                    }
                },
                {
                    targets: 2, render: function (data, type, full, meta) {
                        if (full.status == "1") {
                            return "开启";
                        } else {
                            return "停用";
                        }
                    }
                },
                {
                    targets: 3, render: function (data, type, full, meta) {
                        if (full.lastLoginTime == '' || full.lastLoginTime == null) {
                            return "";
                        }
                        return moment(full.lastLoginTime).format("YYYY-MM-DD HH:mm:ss");
                    }
                },
                {
                    targets: 4, render: function (data, type, full, meta) {
                        if (full.roleName != null) {
                            return full.roleName;
                        } else {
                            if (full.superAdmin == "1") {
                                return "超级管理员";
                            } else {
                                return "普通管理员";
                            }
                        }
                    }
                },
                {
                    targets: 5, render: function (data, type, full, meta) {
                        return '<a class="btn btn-success btn-" onclick="queryByUserId(\'' + full.id + '\')"><i class="glyphicon glyphicon-edit"></i>修改</a> &nbsp;&nbsp;' +
                            '<a class="btn btn-warning btn-circle" onclick="showModal(\'' + full.id + '\')"> <i class="glyphicon glyphicon-trash"></i>删除</a>';
                    }
                }
            ]
        });

}