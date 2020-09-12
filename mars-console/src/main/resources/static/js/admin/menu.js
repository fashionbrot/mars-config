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
    var tableId = "#dataTables-userInfo";
    $(tableId).dataTable().fnDestroy();
    $(tableId)
        .on('xhr.dt', function( e, settings, json, xhr ){
            if(json.code==0){
                json.recordsTotal = json.data.itotalDisplayRecords;
                json.recordsFiltered = json.data.itotalDisplayRecords;
            }else{
                json.recordsTotal = 0;
                json.recordsFiltered = 0;
            }
        })
        .DataTable({
            ajax:{
                url: "./admin/menu/queryAllList?v="+new Date().getTime(),
                type: "post",
                dataType: "json",
                data: function(data){
                    data.page = data.start / data.length + 1;
                    data.pageSize = data.length;
                    delete  data.columns;
                    delete  data.search;
                },
                dataType: "json",
                dataSrc : function(result) {
                    if (result.code != 0) {
                        alert("获取数据失败:"+result.msg);
                        return false;
                    }
                    if (result.data!=null ){
                        return  result.data.data ;
                    }
                    return [];
                },
                error : function(XMLHttpRequest, textStatus, errorThrown) {
                    alert("获取列表失败");
                }
            },
            dom: '<fB<t>ip>',
            stripeClasses: ["odd", "even"],
            paginationType: "full_numbers",
            responsive: true,//自适应
            serverSide:true,
            language: dataTable.language(),
            stateSave: true,
            searching: false,
            paging: true,
            info: true,
            bAutoWidth: false,
            order:[],
            orderable: true,
            lengthMenu: [[25, 50, 100, -1], [25, 50, 100, "All"]],
            columns : [
                {
                    data : 'id',
                    bSortable : true,
                    width : "25px",
                    className : "text-center",
                    render :  dataTableConfig.DATA_TABLES.RENDER.ELLIPSIS
                },
                {
                    data : 'menuLevel',
                    bSortable : true,
                    width : "20px",
                    className : "text-center",
                    render :function (data, type, full, meta) {
                        if(full.menuLevel==1){
                            return "一级菜单"
                        }else if (full.menuLevel==2){
                            return "二级菜单"
                        }else if (full.menuLevel==3){
                            return "按钮"
                        }

                    }
                },{
                    data : 'menuName',
                    bSortable : true,
                    width : "20px",
                    render : dataTableConfig.DATA_TABLES.RENDER.ELLIPSIS
                }, {
                    data : 'menuUrl',
                    bSortable : true,
                    width : "20px",
                    render : dataTableConfig.DATA_TABLES.RENDER.ELLIPSIS
                }, {
                    data : 'priority',
                    bSortable : true,
                    width : "20px",
                    className : "text-center",
                    render : dataTableConfig.DATA_TABLES.RENDER.ELLIPSIS
                }, {
                    data : 'parentMenuName',
                    bSortable : true,
                    width : "25px",
                    className : "text-center",
                    render : dataTableConfig.DATA_TABLES.RENDER.ELLIPSIS
                }, {
                    data : 'code',
                    bSortable : true,
                    width : "25px",
                    className : "text-center",
                    render : dataTableConfig.DATA_TABLES.RENDER.ELLIPSIS
                }, {
                    data : 'createTime',
                    bSortable : true,
                    width : "25px",
                    className : "text-center",
                    render : function (data, type, full, meta) {
                        return moment(full.createTime).format("YYYY-MM-DD HH:mm:ss");
                    }
                }, {
                    data: 'operate',
                    bSortable: false,
                    visible: true,
                    width : '130px',
                    render: function (data, type, full) {
                        return '<a class="btn btn-success btn-" onclick="queryByUserId(\'' + full.id + '\')"><i class="fa fa-edit">修改</i> </a>'
                            + '&nbsp;&nbsp;<a class="btn btn-warning btn-circle" onclick="showModal(\'' + full.id + '\')"> <i class="fa fa-times">删除</i></a>';
                    }
                }
            ]
        });
    $(tableId+'_wrapper').on("change", ":checkbox", function() {
        // 列表复选框
        if ($(this).is("[name='topCheckboxName']")) {
            // 全选
            $(":checkbox", '#dataTableId').prop("checked",$(this).prop("checked"));
        }else{
            // 一般复选
            var checkbox = $("tbody :checkbox", '#dataTableId');
            $(":checkbox[name='cb-check-all']", '#dataTableId').prop('checked', checkbox.length == checkbox.filter(':checked').length);
        }
    }).on('preXhr.dt', function(e, settings, data) {
        // ajax 请求之前事件
        data.page = data.start / data.length + 1;
        data.limit = data.length;
        delete data.start;
        delete data.order;
        delete data.search;
        delete data.length;
        delete data.columns;
    });
    /*loading();
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
    });*/
}