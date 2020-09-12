


//添加用户
var userInfoAdd = function () {


    var a = $("#userInfoAddForm").serializeJson();
    loading();
    $.ajax({
        url: "./role/add",
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
        url: "./role/deleteById",
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
        url: "./role/update",
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
        url: "./role/queryById",
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
            $("#editRoleName").val(data.roleName);
            $("#editStatus").val(data.status);
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
        manager.loadMenuLevel("parentMenuId","1");

        $("#userInfoAddModal").modal("show");
    })

    loadData();

});

function loadData() {
    loading();
    $.ajax({
        url: "./role/queryAllList?v="+new Date().getTime(),
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
                        return full.roleName;
                    }
                    },
                    {
                        targets: 1, render: function (data, type, full, meta) {
                            if(full.status==1){
                                return "开启"
                            }else{
                                return "关闭"
                            }
                        }
                    },
                    {
                        targets: 2, render: function (data, type, full, meta) {
                        return moment(full.createTime).format("YYYY-MM-DD HH:mm:ss");
                    }
                    },
                    {
                        targets: 3, render: function (data, type, full, meta) {
                            if (full.roleCode=='chaojiguanliyuan'){
                                return "";
                            }else{
                                return '<a class="btn btn-success btn-" onclick="queryByUserId(\'' + full.id + '\')"><i class="fa fa-edit">编辑</i> </a>'
                                    + '&nbsp;&nbsp;<a class="btn btn-success" onclick="showRoleMenuTree(\'' + full.id + '\')"> <i class="fa fa-edit">菜单权限</i></a>'
                                    +'&nbsp;&nbsp;<a class="btn btn-success" onclick="showSystemConfigRoleTemplate(\'' + full.id + '\')"> <i class="fa fa-edit">动态配置权限</i></a>'
                                    +'&nbsp;&nbsp;<a class="btn btn-warning btn-circle" onclick="showModal(\'' + full.id + '\')"> <i class="glyphicon glyphicon-trash"></i>删除</a>';
                            }

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

function showRoleMenu(id) {
    loading();
    $("#roleId").val(id);
    $.ajax({
        url: "./admin/menu/loadAllMenu?v="+new Date().getTime(),
        type: "post",
        dataType: "json",
        async:false,
        data:{"roleId":id},
        success: function (data) {
            loaded();
            if(data!=null){
                var html="";
                html+=getTree(data,0);
                $("#roleMenuForm").html(html);
            }
        }
    });


    $("#roleMenuModal").modal("show");


}

function getTree(data,level) {
    var html="";
    for(var i=0;i<data.length;i++){
        var dd=data[i];
        if(level==0){
            html+="<div>";
        }else{
            html+="<div style='padding-left:1.5rem;'>";
        }
        var checkedHtml="";
        if(dd.active==1){
            checkedHtml+="checked='true'"
        }
        html+="<input type='checkbox' onchange='treeOnChanger(this)' "+checkedHtml+" style='width: 1rem;height: 1rem;' data-parent='"+dd.parentMenuId+"' data-id='"+dd.id+"'>"+"<span style='font-size: 14px;'>"+dd.menuName+"</span>";

        if(dd.childMenu!=null && dd.childMenu.length>0){
           html+=getTree(dd.childMenu,1);
        }
        html+="</div>";
    }
    return html;
}
function treeOnChanger(obj) {
    if(obj.checked==true){
        if($(obj).attr("data-parent")==0){
            var inputs=$(obj).parent().find("input");
            for(var j=0;j<inputs.length;j++){
                inputs[j].checked=true;
            }
        }else{
            var inputs=$(obj).parent().parent().find("input");
            for(var j=0;j<inputs.length;j++){
                if($(inputs[j]).attr("data-parent")==0){
                    inputs[j].checked=true;
                }
            }
        }

    }else{
        if($(obj).attr("data-parent")==0){
            var inputs=$(obj).parent().find("input");
            for(var j=0;j<inputs.length;j++){
                inputs[j].checked=false;
            }
        }else{
            var inputs=$(obj).parent().parent().find("input");
            var allCheckStatus=false;
            for(var i=0;i<inputs.length;i++){
                //console.log(inputs[i].checked);
                if(inputs[i].checked && $(inputs[i]).attr("data-parent")!=0){
                    allCheckStatus=true;
                }
            }
            if(!allCheckStatus){
                for(var j=0;j<inputs.length;j++){
                    if($(inputs[j]).attr("data-parent")==0){
                        inputs[j].checked=false;
                    }
                }
            }
        }
    }

}

function submitMenuRole() {
    /*var inputs=$("#roleMenuForm").find("input[type=checkbox]:checked");
    var ids="";
    if(inputs.length>0){
        for(var i=0;i<inputs.length;i++){
            if(ids==""){
                ids+=$(inputs[i]).attr("data-id");
            }else{
                ids+=","+$(inputs[i]).attr("data-id");
            }
        }
    }*/
    var treeObj = $.fn.zTree.getZTreeObj("tree");
    var nodes = treeObj.getCheckedNodes(true);
    var ids="";
    if(nodes.length>0){
        for(var i=0;i<nodes.length;i++){
            if(ids==""){
                ids+=nodes[i].id;
            }else{
                ids+=","+nodes[i].id;
            }
        }
    }
    console.log("roleIds:"+ids);
    var roleId= $("#roleId").val();
    $.ajax({
        url: "./admin/menu/updateRoleMenu?v="+new Date().getTime(),
        type: "post",
        dataType: "json",
        async:false,
        data:{"roleId":roleId,"menuIds":ids},
        success: function (data) {
            if(data.code==0){
                $("#roleMenuModal").modal("hide");
            }else{
                alert(data.msg);
            }
        }
    });
}








function showSystemConfigRoleTemplate(roleId){
    $("#systemConfigDiv").html('');
    $("#roleId3").val(roleId);

    manager.loadEnv("envCode");
    manager.loadApp("appName");

    loadNoSearchSelect("envCode");
    loadNoSearchSelect("appName");
    $("#roleSystemConfigModal").modal("show");

}

$("#appName").on("change",function () {

    loadSystemConfigRole();
});
function loadSystemConfigRole() {
    var roleId = $("#roleId3").val();
    var envCode =$("#envCode").val();
    var appName = $("#appName").val();
    if (appName==null || appName==''){
        return;
    }
    syncSystemConfigRole();
    $.ajax({
        url: "./system-config-role-relation/selectBy?v="+new Date().getTime(),
        type: "post",
        dataType: "json",
        async:false,
        data:{"roleId":roleId,"envCode":envCode,"appName":appName},
        success: function (data) {
            loaded();
            $("#systemConfigDiv").html('');
            var html ="";
            for(var i=0;i<data.length;i++){
                var info = data[i];
                html+="<tr>";
                var id = info.id;
                html+="<td data='"+id+"'>"+info.fileName+"</td>";
                var view =info.viewStatus==1?true:false;
                var edit =info.editStatus==1?true:false;
                var push =info.pushStatus==1?true:false;
                var deleteStatus =info.deleteStatus==1?true:false;
                html+="<td><input name='systemRole' type='checkbox' class='checkboxInput' "+(view?'checked':'')+"  /></td>";
                html+="<td><input name='systemRole' type='checkbox' class='checkboxInput' "+(edit?'checked':'')+"  /></td>";
                html+="<td><input name='systemRole' type='checkbox' class='checkboxInput' "+(deleteStatus?'checked':'')+"   /></td>";
                html+="<td><input name='systemRole' type='checkbox' class='checkboxInput' "+(push?'checked':'')+"  /></td>";
            }


            $("#systemConfigDiv").html(html);
        }
    });
}

function syncSystemConfigRole(){
    var roleId = $("#roleId3").val();
    var envCode =$("#envCode").val();
    var appName = $("#appName").val();
    if (appName==null || appName==''){
        alert("请选择要同步的应用");
        return;
    }
    $.ajax({
        url: "./system-config-role-relation/sync-role?v="+new Date().getTime(),
        type: "post",
        dataType: "json",
        async:false,
        data:{"roleId":roleId,"envCode":envCode,"appName":appName},
        success: function (data) {
            loaded();
            if (data.code==0){
                //alert("同步成功")
            } else{
                alert(data.msg);
            }
        }
    });
}
function saveRole(){
    var list=new Array();
    var i=0;
    $("#roleTable tbody tr").each(function(){
        var row = $(this).children();//获取每一行
        var id =$(row[0]).attr("data");
        var viewStatus = $(row[1].lastChild)[0].checked?1:0;
        var editStatus = $(row[2].lastChild)[0].checked?1:0;
        var deleteStatus = $(row[3].lastChild)[0].checked?1:0;
        var pushStatus = $(row[4].lastChild)[0].checked?1:0;
        list[i]=(new systemConfigRole(id,viewStatus,editStatus,deleteStatus,pushStatus));
        i++;
    });

    $.ajax({
        url: "./system-config-role-relation/save-role?v="+new Date().getTime(),
        type: "post",
        dataType: "json",
        contentType: "application/json",
        async:false,
        data:JSON.stringify(list),
        success: function (data) {
            loaded();
            if (data.code==0){
                alert("修改成功");
                $("#roleSystemConfigModal").modal("hide");
            } else{
                alert(data.msg);
            }
        }
    });
}

function systemConfigRole(id,viewStatus,editStatus,deleteStatus,pushStatus){
    this.id = Number(id);
    this.viewStatus = Number(viewStatus);
    this.editStatus = Number(editStatus);
    this.deleteStatus = Number(deleteStatus);
    this.pushStatus = Number(pushStatus);
}

$("#envCode").on("change",function () {
    var envCode =$("#envCode").val();
    if (envCode!=null && envCode!=''){
        manager.loadProject("appName");
        loadNoSearchSelect("appName");
    }
});

$("#roleQuanxuan").on("click",function () {
   /* var inputs=$("#roleMenuForm").find("input");
    //console.log(inputs)
    if(inputs.length>0) {
        for (var i = 0; i < inputs.length; i++) {
            inputs[i].checked=true;
        }
    }*/
});
$("#roleQuanquxiao").on("click",function () {
    /*var inputs=$("#roleMenuForm").find("input");
    if(inputs.length>0) {
        for (var i = 0; i < inputs.length; i++) {
            inputs[i].checked=false;
        }
    }*/
})

$("#quanxuan").on("click",function () {
    var inputs=$("#roleTemplateForm").find("input");
    //console.log(inputs)
    if(inputs.length>0) {
        for (var i = 0; i < inputs.length; i++) {
            inputs[i].checked=true;
        }
    }
})
$("#quanquxiao").on("click",function () {
    var inputs=$("#roleTemplateForm").find("input");
    if(inputs.length>0) {
        for (var i = 0; i < inputs.length; i++) {
            inputs[i].checked=false;
        }
    }
})



var setting = {
    check: {
        enable: true
        ,chkboxType:{ "Y":"s", "N":"s"}
    },
    data: {
        simpleData: {
            enable: true,
            idKey: "id",
            pIdKey: "parentMenuId",
            rootPId: ""
        }
    }
};


function showRoleMenuTree(id) {
    loading();
    $("#roleId").val(id);
    $.ajax({
        url: "./admin/menu/loadAllMenu?v="+new Date().getTime(),
        type: "post",
        dataType: "json",
        async:false,
        data:{"roleId":id},
        success: function (data) {
            loaded();
            if(data!=null){

                $.fn.zTree.init($("#tree"), setting, data);
                $("#roleQuanxuan").bind("click", { type: "checkAllTrue" }, checkNode);
                $("#roleQuanquxiao").bind("click", { type: "checkAllFalse" }, checkNode);
            }
        }
    });
    $("#roleMenuModal").modal("show");
}



function checkNode(e) {
    var zTree = $.fn.zTree.getZTreeObj("tree"),
        type = e.data.type,
        nodes = zTree.getSelectedNodes();
    console.log(type.indexOf("All"));
    if (type.indexOf("All") < 0 && nodes.length == 0) {
        alert("请先选择一个节点");
    }
    if (type == "checkAllTrue") {
        zTree.checkAllNodes(true);
    } else if (type == "checkAllFalse") {
        zTree.checkAllNodes(false);
    }
}
