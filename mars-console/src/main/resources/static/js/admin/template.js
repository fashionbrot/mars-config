var envId;
var appId;


//添加用户
var addTemplate = function () {

    var addTemplateName=$("#addTemplateName").val();
    if(addTemplateName==null || addTemplateName==''){
        alert("请输入模板名称");
        return false;
    }
    var addTemplateKey=$("#addTemplateKey").val();
    if(addTemplateKey==null || addTemplateKey==''){
        alert("请输入模板key");
        return false;
    }
    if (addTemplateKey.substring(addTemplateKey.length-1,addTemplateKey.length)=='.'){
        alert("最后一个字符不能是点");
        return false;
    }
    if (addTemplateKey.substring(0,1)=='.'){
        alert("第一个字符不能是点");
        return false;
    }
    var addAppId=$("#addAppName").val();
    if(addAppId==null || addAppId==''){
        alert("请选择应用");
        return false;
    }


    var a = $("#templateAddForm").serializeJson();

    loading();
    $.ajax({
        url: "../admin/template/add",
        type: "post",
        data: JSON.stringify(a),
        contentType: "application/json",
        dataType: "json",
        success: function (data) {
            loaded();
            if (data.code == "0") {
                $("#addModal").modal("hide");
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
    $("#delId").val(id);
    $('#userInfoDelModal').modal('show');
}

var userInfoDel = function () {
    loading();
    $.ajax({
        url: "../admin/template/deleteById",
        type: "post",
        data: {"id": $("#delId").val()},
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

    var addTemplateName=$("#editTemplateName").val();
    if(addTemplateName==null || addTemplateName==''){
        alert("请输入模板名称");
        return false;
    }
    var addTemplateKey=$("#editTemplateKey").val();
    if(addTemplateKey==null || addTemplateKey==''){
        alert("请输入模板key");
        return false;
    }
    if (addTemplateKey.substring(addTemplateKey.length-1,addTemplateKey.length)=='.'){
        alert("最后一个字符不能是点");
        return false;
    }
    if (addTemplateKey.substring(0,1)=='.'){
        alert("第一个字符不能是点");
        return false;
    }
    var addAppId=$("#editAppName").val();
    if(addAppId==null || addAppId==''){
        alert("请选择应用");
        return false;
    }

    var a = $("#userInfoEditForm").serializeJson();
    loading();
    $.ajax({
        url: "../admin/template/update",
        type: "post",
        data: JSON.stringify(a),
        contentType: "application/json",
        dataType: "json",
        success: function (data) {
            loaded();
            if (data.code == "0") {
                loadData();
                $("#editModal").modal("hide");
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
        url: "../admin/template/selectById",
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

            $("#editTemplateName").val(data.templateName);
            $("#editTemplateDesc").val(data.templateDesc);
            $("#editTemplateKey").val(data.templateKey);
            $("#editAppName").val(data.appName);
            $("#id").val(data.id);
            if (data.templateUrl!=null && data.templateUrl!=''){
                $("#editTemplateUrl").val(data.templateUrl);
            }else{
                $("#editTemplateUrl").val('');
            }

            $("#editAppName").attr("readonly", "readonly");
            $("#editTemplateKey").attr("readonly", "readonly");
            $("#editModal").modal("show");
        },error: function (e) {
            loaded();
            alert("网络错误，请重试！！");
        }
    });

}




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
                url: "./admin/template/page?v="+new Date().getTime(),
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
                    data : 'id',
                    bSortable : true,
                    width : "25px",
                    className : "text-center",
                    render :  function (data, type, full, meta) {
                        if (full.templateUrl==null || full.templateUrl==''){
                            return "";
                        }
                        var item=full.templateUrl.split(",");
                        var html="";
                        if(item!=null&& item.length>0){
                            for(var i=0;i<item.length;i++){
                                html+="<div style='float:left' onmousemove='showPic(event,\""+item[i]+"\");' onmouseout='hiddenPic()'><image   style='height: 30px;width: 30px;' src='"+item[i]+"'/></div>";
                            }
                        }

                        return html;
                    }
                },
                {
                    data : 'templateKey',
                    bSortable : true,
                    width : "20px",
                    className : "text-center",
                    render : dataTableConfig.DATA_TABLES.RENDER.ELLIPSIS
                },
                {
                    data : 'appName',
                    bSortable : true,
                    width : "20px",
                    className : "text-center",
                    render :dataTableConfig.DATA_TABLES.RENDER.ELLIPSIS
                },{
                    data : 'templateName',
                    bSortable : true,
                    width : "20px",
                    render : dataTableConfig.DATA_TABLES.RENDER.ELLIPSIS
                }, {
                    data : 'templateDesc',
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


function add() {
    $("input[type=reset]").trigger("click");
    $('#addModal').modal('show');
    manager.loadApp("addAppName","选择应用");
    $("#addAppName").val($("#appName").val());
    loadNoSearchSelect("addAppName");

}


function showPic(e,sUrl){
    var x,y;
    x = e.clientX;
    y = e.clientY;
    document.getElementById("Layer1").style.left = x+30+'px';
    document.getElementById("Layer1").style.top = y-200+'px';
    document.getElementById("Layer1").innerHTML = "<img border='0' src=\"" + sUrl + "\">";
    document.getElementById("Layer1").style.display = "";
}
function hiddenPic(){
    document.getElementById("Layer1").innerHTML = "";
    document.getElementById("Layer1").style.display = "none";
}

//查询所有用户
$(document).ready(function () {
    loadNoSearchSelect("appName");
    manager.loadApp("appName","请选择应用");
    loadData();

    $("#appName").on("change",function () {
        loadData();
    });

});

$('#customFile').on('change', function() {
    //当chooseImage的值改变时，执行此函数
    var filePath = $(this).val(), //获取到input的value，里面是文件的路径
    fileFormat = filePath.substring(filePath.lastIndexOf(".")).toLowerCase(),
    src = window.URL.createObjectURL(this.files[0]); //转成可以在本地预览的格式

    // 检查是否是图片
    if(!fileFormat.match(/.png|.jpg|.jpeg/)) {
        alert('上传错误,文件格式必须为：png/jpg/jpeg');
        return;
    }else{
        $('#imgUrl').attr('src', src);
    }
});





