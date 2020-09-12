


//添加用户
var userInfoAdd = function () {
    var appName=$("#appName").val();
    if(appName==null || appName==''){
        alert("请输入项目名称");
        return false;
    }
    var appDesc=$("#appDesc").val();
    if(appDesc==null || appDesc==''){
        alert("请输入项目描述");
        return false;
    }


    loading();
    var a = $("#userInfoAddForm").serializeJson();
    $.ajax({
        url: "./app/add",
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
        },error: function (e) {
            loaded();
            alert("网络错误,请重试!");
        }
    });
}

function showModal(id) {
    $("#userInfoId").val(id);
    $('#userInfoDelModal').modal('show');
}

var userInfoDel = function (appName) {
    loading();
    $.ajax({
        url: "./app/deleteByAppName",
        type: "post",
        data: {"appName": $("#userInfoId").val()},
        dataType: "json",
        success: function (data) {
            loaded();
            if (data.code == "0") {
                $('#userInfoDelModal').modal('hide');
                loadData();
            } else {
                alert(data.msg);
            }
        },error: function (e) {
            loaded();
            alert("网络错误,请重试!");
        }
    });

}

var userInfoEdit = function () {
    loading();
    var a = $("#userInfoEditForm").serializeJson();
    $.ajax({
        url: "./app/update",
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
        },error: function (e) {
            loaded();
            alert("网络错误,请重试!");
        }
    });
}



var queryByUserId = function (appName) {


    loading();
    $.ajax({
        url: "./app/queryById",
        type: "post",
        data: {"appName": appName},
        dataType: "json",
        success: function (result) {
            loaded();
            if (result.code!=0){
                alert(result.msg);
                return false;
            }
            var data = result.data;
            $("#editAppName").attr("readonly","readonly");
            $("#editAppName").val(data.appName);
            $("#editAppDesc").val(data.appDesc);
            $("#editReleaseType").val(data.releaseType);
            $("#userInfoEditModal").modal("show");
        },error: function (e) {
            loaded();
            alert("网络错误,请重试!");
        }
    });
}

//查询所有用户
$(document).ready(function () {

    $(".addProject").on("click",function () {
        $("#userInfoAddModalLabel").text("添加项目");
        $("input[name='addReset']").trigger("click");
        $("#appName").attr("readonly", false);
        $("#userInfoAddModal").modal("show");
    })

    loadData();

});


function loadData() {
    loading();
    $.ajax({
        url: "./app/queryAllList?v="+new Date().getTime(),
        type: "post",
        dataType: "json",
        success: function (data) {
            loaded();
            if (data.code!=0){
                alert(data.msg);
                return false;
            }
            $('#dataTables-userInfo').dataTable().fnDestroy();
            var table = $('#dataTables-userInfo').DataTable({
                language: dataTable.language(),
                stateSave: true,
                searching: false,
                paging: true,
                info: true,
                bAutoWidth: false,
                lengthMenu: [[25, 50, 100, -1], [25, 50, 100, "All"]],
                data: data.data,
                dom: '<fB<t>ip>',
                stripeClasses: ["odd", "even"],
                paginationType: "full_numbers",
                columnDefs: [

                    {
                        targets: 0, render: function (data, type, full, meta) {
                            return full.appName;
                        }
                    },
                    {
                        targets: 1, render: function (data, type, full, meta) {
                            return full.appDesc;
                        }
                    },
                    {
                        targets: 2, render: function (data, type, full, meta) {
                            return moment(full.lastLoginTime).format("YYYY-MM-DD HH:mm:ss");
                        }
                    },
                    {
                        targets: 3, render: function (data, type, full, meta) {
                        return '<a class="btn btn-success btn-" onclick="queryByUserId(\'' + full.appName + '\')"><i class="glyphicon glyphicon-edit"></i>修改</a>'
                            + '&nbsp;&nbsp;<a class="btn btn-warning btn-circle" onclick="showModal(\'' + full.appName + '\')"> <i class="glyphicon glyphicon-trash"></i>删除</a>';
                    }
                    }
                ]
            });
        },error: function (e) {
            loaded();
            alert("网络错误，请重试！！");
        }
    });
}