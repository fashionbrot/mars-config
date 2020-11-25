var envCode;
var appName;
var templateKey;
var envJson;

//查询所有用户
$(document).ready(function () {
    loadNoSearchSelect("envCode");
    manager.loadEnv("envCode");
    appNameChange("appName","templateKey");

    $("#templateKey").on("change",function () {
        loadData(false);
    })
    $("#envCode").on("change",function () {
        $("#appName").val('-1');
    })
});

function add() {
    $("input[name='addReset']").trigger("click");

    manager.loadEnv("addEnvCode");
    loadNoSearchSelect("addEnvCode");

    appNameChange("addAppName","addTemplateKey");
    $("#addPropertyDiv").html('');

    initDateTime("addStartTime");
    initDateTime("addEndTime")


    $("#addTemplateKey").on("change",function () {
        var appName=$("#addAppName").val();
        var templateKey =$("#addTemplateKey").val();
        if(templateKey!=null && templateKey!=''){
            //queryByTemplateKeyAndAppName(appName,templateKey,true);
            loadPropertyAttrDiv("addPropertyDiv",templateKey,appName,"addPropertyClass",'',true,false);
        }
    });

    /**
     * 根据 选中的环境赋值到 新增的选中
     * @type {*|{}|jQuery}
     */
    var envCode=$("#envCode").val();
    if(!isNullObj(envCode)){
        $("#addEnvCode").val(envCode);
    }
    /**
     * 根据 选中的环境赋值到 新增的选中
     * @type {*|{}|jQuery}
     */
    var appName=$("#appName").val();
    if(!isNullObj(appName)){
        $("#addAppName").val(appName);
        manager.loadTemplate("addTemplateKey",appName);
        $("#addAppNameDiv").hide();
    }else{
        $("#addAppNameDiv").show();
    }

    /**
     * 根据 选中的环境赋值到 新增的选中
     * @type {*|{}|jQuery}
     */
    var templateKey=$("#templateKey").val();
    if (!isNullObj(templateKey) && !isNullObj(appName)){
        manager.loadTemplate("addTemplateKey",appName);
        $("#addTemplateKey").val(templateKey);

        $("#addTemplateKeyDiv").hide();

        loadPropertyAttrDiv("addPropertyDiv",templateKey,appName,"addPropertyClass",'',true,false);
    }else{
        $("#addTemplateKeyDiv").show();
    }

    $('#addModal').modal('show');
}



//添加用户
var addValue = function () {


    var envCode=$("#addEnvCode").val();
    if (isNullObj(envCode)){
        alert("请选择环境");
        return false;
    }
    var appName=$("#addAppName").val();
    if (isNullObj(appName)){
        alert("请选择应用");
        return false;
    }
    var templateKey=$("#addTemplateKey").val();
    if (isNullObj(templateKey)){
        alert("请选择模板");
        return false;
    }
    var json=getJson("addPropertyClass");
    try {
        JSON.parse(json);
    }catch (e) {
        console.log(e);
        alert("输入内容格式有误");
        return false;
    }
    if (!json){
        return false;
    }


    $("#addJson").val(json);
    var a = $("#templateAddForm").serializeJson();
    loading();
    $.ajax({
        url: "../admin/config/value/insert",
        type: "post",
        data: JSON.stringify(a),
        contentType: "application/json",
        dataType: "json",
        success: function (data) {
            loaded();
            if (data.code == "0") {
                $("#addModal").modal("hide");
                $("input[type=reset]").trigger("click");
                loadData(true);
            } else {
                alert(data.msg);
            }
        }
    });
}

function showModal(id,obj) {
    $("#userInfoId").val(id);
    $('#userInfoDelModal').modal('show');
}

var userInfoDel = function () {

    loading();
    $.ajax({
        url: "../admin/config/value/deleteById",
        type: "post",
        data: {"id": $("#userInfoId").val()},
        dataType: "json",
        success: function (data) {
            loaded();
            if (data.code == "0") {
                $('#userInfoDelModal').modal('hide');
                loadData(true);
            } else {
                alert(data.msg);
            }
        }
    });


}



var queryByUserId = function (id) {
    manager.loadEnv("editEnvCode");
    manager.loadApp("editAppName");
    initDateTime("editStartTime");
    initDateTime("editEndTime");
    loading();
    $.ajax({
        url: "../admin/config/value/selectById",
        type: "post",
        data: {"id": id},
        dataType: "json",
        success: function (result) {
            loaded();


            var data=result.data;

            $("#configId").val(data.id);
            $("#editEnvCode").val(data.envCode);
            $("#editAppName").val(data.appName);

            $("#editStatus").val(data.status);
            $("#editTitle").val(data.title);
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
                    //queryByTemplateKeyAndAppName(appName,data.templateKey,false);
                    loadPropertyAttrDiv("editPropertyDiv",templateKey,appName,"editPropertyClass",data.json,false,false);
                }
            });
            manager.loadTemplate("editTemplateKey",data.appName);
            $("#editTemplateKey").val(data.templateKey);
            $("#editModal").modal("show");
        }
    });


}
function loadDataLike() {
    envCode=$("#envCode").val();
    if(envCode==null || envCode==''){
        alert('请选环境');
        return false;
    }
    appName=$("#appName").val();
    if (appName==null || appName==''){
        alert("请选择应用");
        return false;
    }
    var description=$("#description").val();
    if (description==null || description==''){
        alert("请输入说明");
        return false;
    }
    loadDataEnvAndApp(envCode,appName,'',description);
}

function loadData(flag) {
    envCode=$("#envCode").val();
    if(envCode==null || envCode==''){
        if(flag){
            return false;
        }
        alert('请选环境');
        return false;
    }
    appName=$("#appName").val();
    if (appName==null || appName==''){
        if(flag){
            return false;
        }
        alert("请选择应用");
        return false;
    }
    templateKey=$("#templateKey").val();
    /*if(templateKey==null || templateKey==''){
        if(flag){
            return false;
        }
        alert('请选择模板');
        return false;
    }*/
    loadDataEnvAndApp(envCode,appName,templateKey,'');
}
function loadDataEnvAndApp(envCode,appName,templateKey,description) {

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
                url: "./admin/config/value/page?v="+new Date().getTime(),
                type: "get",
                dataType: "json",
                data: function(data){
                    data.page = data.start / data.length + 1;
                    data.pageSize = data.length;
                    data.envCode = envCode;
                    data.templateKey = templateKey;
                    data.description = description;
                    data.appName = appName;
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
                    data : 'priority',
                    bSortable : true,
                    width : "20px",
                    className : "text-center",
                    render : dataTableConfig.DATA_TABLES.RENDER.ELLIPSIS
                },
                {
                    data : 'description',
                    bSortable : true,
                    width : "20px",
                    className : "text-center",
                    render :dataTableConfig.DATA_TABLES.RENDER.ELLIPSIS
                },/*{
                    data : 'id',
                    bSortable : true,
                    width : "20px",
                    render : function (data, type, full, meta) {
                        if(full.startDate=='' || full.startDate==null){
                            return '';
                        }
                        var html="";
                        var nowTime=new Date();
                        if(full.startDate!=null ) {
                            var time=moment(full.startDate).format("YYYY-MM-DD HH:mm:ss");
                            if(nowTime.getTime()<(new Date(full.startDate)).getTime()){
                                html+="<span style='color:red;font-weight: bold;'>"+time+"</span>"
                            }else{
                                html+=time;
                            }
                        }
                        return html;
                    }
                },{
                    data : 'id',
                    bSortable : true,
                    width : "20px",
                    render : function (data, type, full, meta) {
                        if(full.endDate=='' || full.endDate==null){
                            return '';
                        }
                        var html="";
                        var nowTime=new Date();
                        if(full.endDate!=null ) {
                            var time=moment(full.endDate).format("YYYY-MM-DD HH:mm:ss")
                            if(nowTime.getTime()>(new Date(full.endDate)).getTime()){
                                html+="<span style='color:red;font-weight: bold;'>"+time+"</span>"
                            }else{
                                html+=time;
                            }
                        }
                        return html;
                    }
                },*/
                {
                    data : 'description',
                    bSortable : true,
                    width : "20px",
                    className : "text-center",
                    render :function (data, type, full, meta) {
                        if (full.status == "1") {
                            return "开启";
                        } else {
                            return "<span style='color:red;font-weight: bold;'>停用</span>";
                        }
                    }
                },{
                    data : 'release_status',
                    bSortable : true,
                    width : "20px",
                    className : "text-center",
                    render :function (data, type, full, meta) {
                        if (data == "1") {
                            return "已发布";
                        } else if (data==0){
                            return "<span style='color:red;font-weight: bold;'>未发布</span>";
                        }else if (data==2){
                            return "<span style='color:red;font-weight: bold;'>已删除</span>";
                        }
                    }
                },
                {
                    data : 'user_name',
                    bSortable : true,
                    width : "20px",
                    className : "text-center",
                    render :dataTableConfig.DATA_TABLES.RENDER.ELLIPSIS
                },  {
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
                        return '<a class="btn btn-success btn-" onclick="queryByUserId(\'' + full.id + '\')"><i class="glyphicon glyphicon-edit"></i>修改</a>'
                            + '&nbsp;&nbsp;<a class="btn btn-warning btn-circle" onclick="showModal(\'' + full.id + '\',this)"> <i class="glyphicon glyphicon-trash"></i>删除</a>';
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









function strToJson(str){
    var json = (new Function("return " + str))();
    return json;
}





function appNameChange(appNameId,templateId) {
    loadNoSearchSelect(appNameId);
    loadSelect(templateId);
    manager.loadApp(appNameId);

    var appName=$("#"+appNameId).val();
    console.log(appName)
    manager.loadTemplateCustom(templateId,appName,"选择模板");

    $("#"+appNameId).on("change",function () {
        var appName=$("#"+appNameId).val();
        manager.loadTemplateCustom(templateId,appName,"选择模板");
        loadSelect(templateId);
    })

}






function releaseConfig(releaseType) {
    var envCode=$("#envCode").val();
    if (envCode==null || envCode==''){
        alert("请选择环境");
        return false;
    }
    var appName=$("#appName").val();
    if (appName==null || appName==''){
        alert("请选择应用");
        return false;
    }
    releaseType=0;
    var tempalteText="";
    if(releaseType==1){
        tempalteText="-《已修改》模板";
    }else if(releaseType==0){
        tempalteText="-《全部》模板";
    }
    var res = confirm("您确定要发布<"+$("#envCode option:selected").text()+">-<"+$("#appName option:selected").text().trim()+"项目> "+tempalteText+" 吗?");

    if(res == true){
        loading();
        $.ajax({
            url: "/admin/config/value/release",
            type: "post",
            dataType: "json",
            data: {envCode: envCode, appName: appName,releaseType:releaseType},
            success: function (data) {
                loaded();
                if(data.code==0){
                    loadData();
                }else{
                    alert(data.msg);
                }
            }
        });
    }

}