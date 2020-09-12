$(function () {
    $("#dataTables-userInfo_length").hide();
})


//添加用户
var userInfoAdd = function () {


    var a = $("#userInfoAddForm").serializeJson();
    loading();
    $.ajax({
        url: "./admin/menu/add",
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
        url: "./admin/menu/deleteById",
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
        },error: function (e) {
            loaded();
            alert("网络错误，请重试！！");
        }
    });
}

var userInfoEdit = function () {
    loading();
    var a = $("#userInfoEditForm").serializeJson();
    $.ajax({
        url: "./admin/menu/update",
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
            alert("网络错误，请重试！！");
        }
    });
}


var queryByUserId = function (id) {


    loading();
    $.ajax({
        url: "./admin/menu/queryById",
        type: "post",
        data: {"id": id},
        dataType: "json",
        success: function (result) {
            loaded();
            if (result.code!=0){
                alert(result.msg);
                return false;
            }
            var data = result.data;
            $("#editId").val(data.id);
            $("#editMenuName").val(data.menuName);
            $("#editMenuUrl").val(data.menuUrl);
            $("#editPriority").val(data.priority);
            $("#editMenuLevel").val(data.menuLevel);
            // manager.loadMenuLevel("editParentMenuId","1");


            var level = $("#editMenuLevel").val();
            console.log(level);
            if (level==2){
                manager.loadMenuLevel("editParentMenuId","1");
            }
            if (level==3){
                manager.loadMenuLevel("editParentMenuId","2");
            }
            $("#editParentMenuId").val(data.parentMenuId);

            $("#userInfoEditModal").modal("show");
        },error: function (e) {
            loaded();
            alert("网络错误，请重试！！");
        }
    });
}

//查询所有用户
$(document).ready(function () {

    $(".addProject").on("click",function () {

        $("input[name='addReset']").trigger("click");
        // manager.loadMenuLevel("parentMenuId","1");

        $("#userInfoAddModal").modal("show");
    })

    loadData();

});

$("#menuLevel").on("change",function () {
    var level = $("#menuLevel").val();
    if (level==2){
        manager.loadMenuLevel("parentMenuId","1");
    }
    if (level==3){
        manager.loadMenuLevel("parentMenuId","2");
    }
})
$("#editMenuLevel").on("change",function () {
    var level = $("#editMenuLevel").val();
    console.log(level);
    if (level==2){
        manager.loadMenuLevel("editParentMenuId","1");
    }
    if (level==3){
        manager.loadMenuLevel("editParentMenuId","2");
    }
})

function loadData() {
    loading();
    $.ajax({
        url: "./admin/menu/queryAllList?v="+new Date().getTime(),
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
                language:dataTable.language(),
                stateSave: true,
                searching: false,
                paging: true,
                info: true,
                bAutoWidth: false,
                lengthMenu: [[10,20,25,30], [10,20,25,30]],
                data: data.data,
                dom: '<fB<t>ip>',
                stripeClasses: ["odd", "even"],
                paginationType: "full_numbers",
                columnDefs: [
                    {
                        targets: 0, render: function (data, type, full, meta) {
                            return full.id;
                        }
                    },
                    {
                        targets: 1, render: function (data, type, full, meta) {
                            if(full.menuLevel==1){
                                return "一级菜单"
                            }else if (full.menuLevel==2){
                                return "二级菜单"
                            }else if (full.menuLevel==3){
                                return "按钮"
                            }

                        }
                    },
                    {
                        targets: 2, render: function (data, type, full, meta) {
                        return full.menuName;
                    }
                    },
                    {
                        targets: 3, render: function (data, type, full, meta) {
                        return full.menuUrl;
                    }
                    },
                    {
                        targets: 4, render: function (data, type, full, meta) {
                        return full.priority;
                    }
                    },
                    {
                        targets: 5, render: function (data, type, full, meta) {
                            return full.parentMenuName;
                    }
                    },{
                        targets: 6, render: function (data, type, full, meta) {
                            return full.code;
                        }
                    },
                    {
                        targets: 7, render: function (data, type, full, meta) {
                        return moment(full.createTime).format("YYYY-MM-DD HH:mm:ss");
                    }
                    },
                    {
                        targets: 8, render: function (data, type, full, meta) {
                        return '<a class="btn btn-success btn-" onclick="queryByUserId(\'' + full.id + '\')"><i class="fa fa-edit">修改</i> </a>'
                            + '&nbsp;&nbsp;<a class="btn btn-warning btn-circle" onclick="showModal(\'' + full.id + '\')"> <i class="fa fa-times">删除</i></a>';
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