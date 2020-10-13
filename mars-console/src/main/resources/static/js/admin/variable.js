


//添加用户
var userInfoAdd = function () {
    var envName=$("#addVariableName").val();
    if(envName==null || envName==''){
        alert("请输入变量名称");
        return false;
    }
    var divs=$("input[name='addVariableRelationInput']");
    if(divs!=null){
        var json="[";
        for (var i=0;i<divs.length;i++){
            var input=divs[i];
            var envCode=$(input).attr("data-envcode");
            var value=$(input).val();
            //console.log(envCode+value);
            if(i!=0){
                json+=",";
            }
            json+="{";
            json+="'envCode':'"+envCode+"'";
            json+=",'variableValue':'"+value+"'";
            json+="}";
        }
        json+="]";
        var relationJson=json;
        $("#addRelationJson").val(relationJson);
        //a["relation"]=relationJson;
    }


    var a = $("#userInfoAddForm").serializeJson();
    loading();
    $.ajax({
        url: "../admin/variable/insert",
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
            alert("请求失败或超时，请稍后再试");
        }
    });
}


function loadVariableRelation(id,inputName,variableKey){
    var variableRelationList=null;
    if (variableKey!=null){
        $.ajax({
            url: "../admin/variable-relation/queryList",
            type: "get",
            dataType: "json",
            data:{variable_key:variableKey},
            async: false,
            success: function (result) {
                if (result.code!=0){
                    alert("请求失败或超时，请稍后再试");
                    return;
                }
                variableRelationList=result.data;
            },error: function (e) {
                loaded();
                alert("请求失败或超时，请稍后再试");
            }
        });
    }


    var envList = null;
    $.ajax({
        url: "../env/queryAll",
        type: "post",
        contentType: "application/json",
        dataType: "json",
        async: false,
        success: function (envData) {
            envList = envData;
        },error: function (e) {
            loaded();
            alert("请求失败或超时，请稍后再试");
        }
    });

    var html = "";
    $("#" + id).html(html);
    for (var i = 0; i < envList.length; i++) {
        var envData = envList[i];
        var variableValue = getRelation(variableRelationList, envData.envCode);
        //html += "<span style='color: #3300FF'>" + envData.envName + "：</span><input class='form-control' data-envCode='" + envData.envCode + "' type='text' name='" + inputName + "' value='" + variableValue + "' />";
        html+=envHtml(envData.envName,envData.envCode,inputName,variableValue);
    }
    //console.log(html);
    $("#" + id).html(html);
}

function envHtml(envName,envCode,inputName,variableValue) {
    var html="<div class=\"form-group form-group-30\" >";
            html+="<div  class=\"form-text-right\" style=\"color: red;\" ><span style=\"color: red;\">*</span>"+envName+"</div>";
            html+="<div class=\"col-sm-8\">";
                html+="<input class='form-control' data-envCode='" + envCode + "' type='text' name='" + inputName + "' value='" + variableValue + "' />";
            html+="</div>";
        html+="</div>";
    return html;
}

function getRelation(relationDataList,envCode) {
    if(relationDataList!=null){
        for (var i=0;i<relationDataList.length;i++){
            var relation=relationDataList[i];
            if(relation.envCode==envCode){
                return relation.variableValue;
            }
        }
    }
    return "";
}



function showModal(id) {
    $("#userInfoId").val(id);
    $('#userInfoDelModal').modal('show');

}

var userInfoDel = function () {

    loading();
    $.ajax({
        url: "../admin/variable/deleteById",
        type: "post",
        dataType: "json",
        async: false,
        data:{"id": $("#userInfoId").val()},
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
            alert("请求失败或超时，请稍后再试");
        }
    });

}

var userInfoEdit = function () {


    var editVariableKey=$("#editVariableKey").val();
    var divs=$("input[name='editVariableRelationInput']");
    if(divs!=null){
        var json="[";
        for (var i=0;i<divs.length;i++){
            var input=divs[i];
            var envCode=$(input).attr("data-envcode");
            var value=$(input).val();
            //console.log(envCode+value);
            if(i!=0){
                json+=",";
            }
            json+="{";
            json+="'envCode':'"+envCode+"'";
            json+=",'variableValue':'"+value+"'";
            json+=",'VariableKey':'"+editVariableKey+"'";
            json+="}";
        }
        json+="]";
        var relationJson=json;
        $("#editRelationJson").val(relationJson);
        //a["relation"]=relationJson;
    }
    var a = $("#userInfoEditForm").serializeJson();
    loading();
    $.ajax({
        url: "../admin/variable/updateById",
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
            alert("请求失败或超时，请稍后再试");
        }
    });
}


var queryByUserId = function (id) {

    loading();
    $.ajax({
        url: "../admin/variable/selectById",
        type: "post",
        dataType: "json",
        async: false,
        data:{"id": id},
        success: function (result) {
            loaded();
            if (result.code!=0){
                alert(result.msg);
                return false;
            }
            var data =result.data;

            $("#variableId").val(data.id);
            $("#editVariableKey").attr("readonly","readonly");
            $("#editVariableName").val(data.variableName);
            $("#editVariableDesc").val(data.variableDesc);
            $("#editVariableKey").val(data.variableKey);
            loadVariableRelation("editVariableRelationDiv","editVariableRelationInput",data.variableKey);

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
        loadVariableRelation("addVariableRelationDiv","addVariableRelationInput",null);
        $("#userInfoAddModal").modal("show");
    })

    loadData();

});

function loadData() {


    var appId=$("#appName").val();
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
                url: "./admin/variable/page?v="+new Date().getTime(),
                type: "get",
                dataType: "json",
                data: function(data){
                    data.page = data.start / data.length + 1;
                    data.pageSize = data.length;
                    data.appName = appId;
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
                    data : 'variableKey',
                    bSortable : true,
                    width : "20px",
                    className : "text-center",
                    render : dataTableConfig.DATA_TABLES.RENDER.ELLIPSIS
                },
                {
                    data : 'variableName',
                    bSortable : true,
                    width : "20px",
                    className : "text-center",
                    render :dataTableConfig.DATA_TABLES.RENDER.ELLIPSIS
                },{
                    data : 'variableDesc',
                    bSortable : true,
                    width : "20px",
                    render : dataTableConfig.DATA_TABLES.RENDER.ELLIPSIS
                },  {
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





}