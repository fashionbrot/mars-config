function exportData() {
    var envCode=$("#envCode").val();
    if(envCode==null || envCode==''){
        alert("请选择环境");
        return false;
    }
    var appName=$("#appName").val();
    if(appName==null || appName==''){
        alert("请选择应用");
        return false;
    }
    var templateName=$("#templateKey option:selected").text();
    var tempalteText="";
    if(templateName!='请选择模板'){
        tempalteText="-《"+templateName+"》模板";
    }else{
        tempalteText="-《全部》模板";
    }
    var res = confirm("您确定要导出<"+$("#envCode option:selected").text()+">-<"+$("#appName option:selected").text()+"项目> "+tempalteText+" 吗?");

    if(res == true){

    }else{
        return false;
    }
    var templateKey=$("#templateKey").val();


    window.location.href='../data/export?envCode='+envCode+'&appName='+appName+"&templateKey="+templateKey;
    setTimeout(function () {
        loadData();
    },1500)
    loaded();
}

$(function () {
    loadNoSearchSelect("envCode")
    manager.loadEnv("envCode");
    loadNoSearchSelect("appName")
    manager.loadApp("appName");
    loadSelect("templateKey");
    var appNameId="appName";

    var appName=$("#"+appNameId).val();
    manager.loadTemplate("templateKey",appName);
    loadSelect("templateKey");

    $("#"+appNameId).on("change",function () {
        var appName=$("#"+appNameId).val();
        manager.loadTemplate("templateKey",appName);
        loadSelect("templateKey");
    })

    $("#importId").on("change",function () {
        var filePath = $(this).val(), //获取到input的value，里面是文件的路径
            fileFormat = filePath.substring(filePath.lastIndexOf(".")).toLowerCase(),
            src = window.URL.createObjectURL(this.files[0]); //转成可以在本地预览的格式
        //console.log(filePath);
        // 检查是否是图片
        if(!fileFormat.match(/.json/)) {
            alert('上传错误,文件格式必须为：.json');
            return;
        }else{
            $("#fileView").show();
            $("#viewFileName").html(filePath);
            $(".importDiv").hide();
        }

    });

    loadData();

    $("#envCode").on("change",function () {
        $("#appName").val('');
    })

    $("#appName").on("change",function () {
        loadData();
    });
    $("#templateKey").on("change",function () {
        loadData();
    });
})
function showModal(id,obj) {
    $("#userInfoId").val(id);
    $('#userInfoDelModal').modal('show');
}

var userInfoDel = function () {


    loading();
    $.ajax({
        url: "../data/deleteById",
        type: "post",
        data: {"id": $("#userInfoId").val()},
        //contentType: "application/json",
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




function importData() {
    var envCode=$("#envCode").val();
    if (envCode=='' || envCode==null){
        alert("请选择环境");
        return false;
    }
    var viewFileName=$("#viewFileName").html();
    if(viewFileName==null || viewFileName==''){
        alert("请选择json文件");
        return false;
    }

    var res = confirm("您确定要导入<"+$("#envCode option:selected").text()+">吗?");

    if(res == true){
    }else{
        return false;
    }
    manager.loadEnv("envCode");

    var formData = new FormData();
    formData.append("importId", $("#importId")[0].files[0]);
    formData.append("envCode",envCode);
    loading();
    $.ajax({
        type:'POST',
        url:'../data/import',
        data:formData,
        contentType:false,
        processData:false,//这个很有必要，不然不行
        dataType:"json",
        mimeType:"multipart/form-data",
        success:function(data){
            loaded();
            if (data.code==0) {
                setTimeout(function () {

                    alert('导入成功');
                    $("#fileView").hide();
                    $("#viewFileName").html('');
                    $(".importDiv").show();

                    window.location.reload();
                },500)
            }else{
                alert(data.msg);
            }
        }
    });
}


function loadData() {
    var envCode=$("#envCode").val();
    var appName=$("#appName").val();
    var templateKey=$("#templateKey").val();
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
                url: "./admin/config/record/page?v="+new Date().getTime(),
                type: "get",
                dataType: "json",
                data: function(data){
                    data.page = data.start / data.length + 1;
                    data.pageSize = data.length;
                    data.envCode = envCode;
                    data.appName = appName;
                    data.templateKey = templateKey;
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
                    data : 'envCode',
                    bSortable : true,
                    width : "20px",
                    className : "text-center",
                    render :dataTableConfig.DATA_TABLES.RENDER.ELLIPSIS
                },
                {
                    data : 'appName',
                    bSortable : true,
                    width : "20px",
                    className : "text-center",
                    render :dataTableConfig.DATA_TABLES.RENDER.ELLIPSIS
                },{
                    data : 'templateKey',
                    bSortable : true,
                    width : "20px",
                    className : "text-center",
                    render :dataTableConfig.DATA_TABLES.RENDER.ELLIPSIS
                },{
                    data : 'operationType',
                    bSortable : true,
                    width : "20px",
                    render : function (data, type, full, meta) {
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
                },{
                    data : 'userName',
                    bSortable : true,
                    width : "20px",
                    className : "text-center",
                    render :dataTableConfig.DATA_TABLES.RENDER.ELLIPSIS
                },{
                    data : 'description',
                    bSortable : true,
                    width : "20px",
                    className : "text-center",
                    render :dataTableConfig.DATA_TABLES.RENDER.ELLIPSIS
                },
                  {
                    data : 'id',
                    bSortable : true,
                    width : "25px",
                    className : "text-center",
                    render : function (data, type, full, meta) {
                        if(full.updateDate!=null){
                            return moment(full.updateDate).format("YYYY-MM-DD HH:mm:ss");
                        }else{
                            return moment(full.createDate).format("YYYY-MM-DD HH:mm:ss");
                        }
                    }
                }, {
                    data: 'operate',
                    bSortable: false,
                    visible: true,
                    width : '130px',
                    render: function (data, type, full) {
                        return '<a class="btn btn-success btn-" onclick="queryByUserId(\'' + full.id + '\')"><i class="glyphicon glyphicon-eye-open"></i>查看 </a>';
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

var queryByUserId = function (id) {
    loading();
    $.ajax({
        url: "../admin/config/record/selectById",
        type: "post",
        data: {"id": id},
        //contentType: "application/json",
        dataType: "json",
        success: function (result) {
            loaded();
            if (result.code!=0){
                alert(result.msg);
                return false;
            }
           /* $("#editEnvCode").val(data.envCode);
            $("#editAppName").val(data.appName);
            $("#editDataType").val(data.dataType);
            $("#editJson").val(formatJson(data.json));
            $("#editModal").modal("show");*/
            showConfig(result.data);
        },error(){
            loaded();
            alert("请求失败");
        }
    });
}


function showConfig(row) {

    var data = null;
    var updateData = null;
    if (!isNullObj(row.json)){
        data =JSON.parse(row.json);
    }
    if (!isNullObj(row.newJson)){
        updateData =JSON.parse(row.newJson);
    }
    $("#releaseId").val(row.id);
    if (data!=null && updateData!=null){
        $("#rollback").show();
        $("#showDialogId").attr("style","width:800px;");
        $("#configInfo").attr("style","width:49%;display:block;float:left;");
        $("#newConfigInfo").attr("style","width:50%;display:block;float:right;");
    }else{
        $("#showDialogId").attr("style","width:500px;");
        $("#rollback").hide();
    }

    if(data!=null && updateData==null){
        $("#newConfigInfo").attr("style","display:none");
        $("#configInfo").attr("style","width:100%;display:block;");
    }
    if (updateData!=null && data==null){
        $("#configInfo").attr("style","display:none");
        $("#newConfigInfo").attr("style","width:100%;display:block;");
    }

    /**
     * 修改前 页面展示
     */
    loadConfigInfo(data);
    /**
     * 修改后 页面展示
     */
    loadNewConfigInfo(updateData);



    $("#editModal").modal("show");
}
function loadConfigInfo(data) {
    if (data!=null) {
        var appName = data.appName;
        manager.loadEnv("editEnvCode");
        manager.loadApp("editAppName");
        initDateTime("editStartTime");
        initDateTime("editEndTime");
        $("#editStatus").val(data.status);
        $("#configId").val(data.id);
        $("#editEnvCode").val(data.envCode);
        $("#editAppName").val(data.appName);
        $("#editPriority").val(data.priority);
        $("#editDescription").val(data.description);
        $("#editEnvId").val(data.envId);
        $("#editConfigKey").val(data.configKey);
        if (data.startTime!='' && data.startTime!=null){
            $("#editStartTime").val(moment(data.startTime).format("YYYY-MM-DD HH:mm:ss"));
        }else{
            $("#editStartTime").val('');
        }
        if (data.endTime!='' && data.endTime!=null) {
            $("#editEndTime").val(moment(data.endTime).format("YYYY-MM-DD HH:mm:ss"));
        }else{
            $("#editEndTime").val('');
        }

        loadPropertyAttrDiv("editPropertyDiv",data.templateKey,data.appName,"editPropertyClass",data.json,false,false);

        $("#editTemplateKey").on("change",function () {
            var templateKey =$("#editTemplateKey").val();
            var appName=$("#editAppName").val();
            if(templateKey!=null && templateKey!=''){
                queryByTemplateKeyAndAppName(appName,data.templateKey,false);
                loadPropertyAttrDiv("editPropertyDiv",templateKey,appName,"editPropertyClass",data.json,false,false);
            }
        });
        manager.loadTemplate("editTemplateKey",data.appName);
        $("#editTemplateKey").val(data.templateKey);
    }
}

function loadNewConfigInfo(data) {
    if (data!=null) {
        $("#editStatus2").val(data.status);
        //$("#editTitle").val(data.title);
        $("#editPriority2").val(data.priority);
        $("#editDescription2").val(data.description);
        //$("#editEnvId").val(data.envId);
        $("#editConfigKey2").val(data.configKey);
        if (data.startTime != '' && data.startTime != null) {
            $("#editStartTime2").val(moment(data.startTime).format("YYYY-MM-DD HH:mm:ss"));
        } else {
            $("#editStartTime2").val('');
        }
        if (data.endTime != '' && data.endTime != null) {
            $("#editEndTime2").val(moment(data.endTime).format("YYYY-MM-DD HH:mm:ss"));
        } else {
            $("#editEndTime2").val('');
        }


        loadPropertyAttrDiv("editPropertyDiv2", data.templateKey, data.appName, "editPropertyClass2", data.json, false, true);
    }
}



