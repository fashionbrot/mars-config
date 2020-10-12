var propertyKey;



//查询所有用户
$(document).ready(function () {


    loadData();
    appNameChange("appName","templateKey");

    $("#appName").on("change",function () {
        loadData();
    });
    $("#templateKey").on("change",function () {
        loadData();
    });
});

//添加用户
var addTemplate = function () {

    var addPropertyName=$("#addPropertyName").val();
    if(addPropertyName==null || addPropertyName==''){
        alert("请输入属性名称");
        return false;
    }
    var addPropertyKey=$("#addPropertyKey").val();
    if(addPropertyKey==null || addPropertyKey==''){
        alert("请输入属性key");
        return false;
    }
    var appName=$("#addAppName").val();
    if (appName=='' || appName==null){
        alert("请选择应用");
        return false;
    }
    var labelType=$("#addLabelType").val();
    var labelValue;
    if(labelType=='select'){
        labelValue=getPropertyJson('addLableSelectClass');
        if (labelValue==false ){
            alert("请添加标签属性值");
            return false;
        }
    }else if(labelType=='input'){
        labelValue=$("input[name='labelValue']").val();
    }
    $("#addLabelValue").val(labelValue);
    var templateName = getSelectText("templateKey");
    if (templateName=='公共模板'){
        $("#attributeType").val('0');
    }else{
        $("#attributeType").val('1');
    }


    var a = $("#templateAddForm").serializeJson();
    loading();
    $.ajax({
        url: "../admin/property/insert",
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
    $("#userInfoId").val(id);
    $('#userInfoDelModal').modal('show');
}

var userInfoDel = function (id) {


    loading();
    $.ajax({
        url: "../admin/property/deleteById",
        type: "post",
        data: {"id":$("#userInfoId").val()},
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
    var addPropertyName=$("#editPropertyName").val();
    if(addPropertyName==null || addPropertyName==''){
        alert("请输入属性名称");
        return false;
    }
    var addPropertyKey=$("#editPropertyKey").val();
    if(addPropertyKey==null || addPropertyKey==''){
        alert("请输入属性key");
        return false;
    }
    var appName=$("#editAppName").val();
    if (appName=='' || appName==null){
        alert("请选择应用");
        return false;
    }
    var labelType=$("#editLabelType").val();
    var labelValue;
    if(labelType=='select'){
        labelValue=getPropertyJson('editLableSelectClass');
        if (labelValue==false ){
            alert("请添加标签属性值");
            return false;
        }
    }else if(labelType=='input'){
        labelValue=$("input[name='labelValue']").val();
    }
    var templateName = getSelectText("editTemplateKey");
    if (templateName=='公共模板'){
        $("#editAttributeType").val('0');
    }else{
        $("#editAttributeType").val('1');
    }

    $("#editLabelValue").val(labelValue);

    var a = $("#userInfoEditForm").serializeJson();
    loading();
    $.ajax({
        url: "../admin/property/updateById",
        type: "post",
        data: JSON.stringify(a),
        contentType: "application/json",
        dataType: "json",
        success: function (data) {
            loaded();
            if (data.code == "0") {
                $("#editModal").modal("hide");
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
        url: "../admin/property/selectById",
        type: "post",
        data: {"id": id},
        dataType: "json",
        success: function (result) {



            loaded();
            if (result.code!=0){
                alert(result.msg);
                return false;
            }
            var data=result.data;
            $("#editId").val(data.id);
            $("#editPropertyName").val(data.propertyName);
            $("#editPropertyKey").val(data.propertyKey);
            $("#editPropertyType").val(data.propertyType);
            $("#editLabelType").val(data.labelType);
            $("#editAppName").val(data.appName);
            manager.loadTemplateCustom("editTemplateKey",data.appName,"");
            $("#editTemplateKey").val(data.templateKey);
            var json=data.labelValue;
            $("#editAttributeType").val(data.attributeType);



            //manager.loadVariable("editVariableKey");
            $("#editVariableKey").val(data.variableKey);

            addDivClass('editLableSelectClass');
            if(data.labelType=='select'){
                addSelectHtml("editLableSelectClass");
                var valueJson=eval('(' + json + ')');
                $(".editLableSelectClass1").html(getforJsonHtml(valueJson));

            }else if(data.labelType=='input'){
                inputHtml("editLableSelectClass","editLabelValue",data.labelValue);
            }else if(data.labelType=='textarea'){
                inputHtml("editLableSelectClass","editLabelValue",data.labelValue);
            }
            changeLableType("editLabelType","editLableSelectClass");
            $("#editPropertyKey").attr("readonly","readonly");

            appNameChange("editAppName","editTemplateKey");
            $("#editAppName").val(data.appName);
            $("#editTemplateKey").val(data.templateKey);

            loadNoSearchSelect("editVariableKey");
            loadNoSearchSelect("editLabelType");
            loadNoSearchSelect("editPropertyType");
            $("#editModal").modal("show");

        },error: function (e) {
            loaded();
            alert("网络错误，请重试！！");
        }
    });


}

function getforJsonHtml(json) {
    var html='';
    for(var k in json){
        html+=propertyHtml(k,json[k]);
    }
    return html;
}


function loadData() {

    propertyKey=$("#propertyKey").val();
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
                url: "./admin/property/page?v="+new Date().getTime(),
                type: "get",
                dataType: "json",
                data: function(data){
                    data.page = data.start / data.length + 1;
                    data.pageSize = data.length;
                    data.appName = appName;
                    data.propertyKey= propertyKey;
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
                    data : 'appName',
                    bSortable : true,
                    width : "20px",
                    className : "text-center",
                    render : dataTableConfig.DATA_TABLES.RENDER.ELLIPSIS
                },
                {
                    data : 'templateKey',
                    bSortable : true,
                    width : "20px",
                    className : "text-center",
                    render :dataTableConfig.DATA_TABLES.RENDER.ELLIPSIS
                },{
                    data : 'propertyName',
                    bSortable : true,
                    width : "20px",
                    render : dataTableConfig.DATA_TABLES.RENDER.ELLIPSIS
                }, {
                    data : 'propertyKey',
                    bSortable : true,
                    width : "20px",
                    render : dataTableConfig.DATA_TABLES.RENDER.ELLIPSIS
                }, {
                    data : 'propertyType',
                    bSortable : true,
                    width : "20px",
                    render : dataTableConfig.DATA_TABLES.RENDER.ELLIPSIS
                }, {
                    data : 'labelType',
                    bSortable : true,
                    width : "20px",
                    render : dataTableConfig.DATA_TABLES.RENDER.ELLIPSIS
                }, {
                    data : 'variableKey',
                    bSortable : true,
                    width : "20px",
                    render : dataTableConfig.DATA_TABLES.RENDER.ELLIPSIS
                },{
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

var variableData=null;
function loadVariableData() {
    $.ajax({
        url: "../admin/variable/queryAll",
        type: "post",
        dataType: "json",
        async: false,
        success: function (data) {
            variableData=data;
        }
    });
}



function loadDataEnvAndApp(propertyKey,appName,templateKey) {
    loadVariableData();

    loading();
    $.ajax({
        url: "../admin/property/queryAll",
        type: "post",
        dataType: "json",
        cache:false,
        data:{propertyKey:propertyKey,appName:appName,templateKey:templateKey},
        success: function (data) {
            loaded();



            $('#dataTables-userInfo').dataTable().fnDestroy();
            var table = $('#dataTables-userInfo').DataTable({
                language: {
                    "sProcessing": "处理中...",
                    "sLengthMenu": "显示 _MENU_ 项结果",
                    "sZeroRecords": "没有匹配结果",
                    "sInfo": "显示第 _START_ 至 _END_ 项结果，共 _TOTAL_ 项",
                    "sInfoEmpty": "显示第 0 至 0 项结果，共 0 项",
                    "sInfoFiltered": "(由 _MAX_ 项结果过滤)",
                    "sInfoPostFix": "",
                    // "sSearch": "搜索:",
                    "sUrl": "",
                    "sEmptyTable": "表中数据为空",
                    "sLoadingRecords": "载入中...",
                    "sInfoThousands": ",",
                    "oPaginate": {
                        "sFirst": "首页",
                        "sPrevious": "上页",
                        "sNext": "下页",
                        "sLast": "末页"
                    }
                },

                stateSave: true,
                searching: false,
                paging: true,
                info: true,
                bAutoWidth: false,
                dom: 'T<"right"><"toolbar">Clfrtip',
                lengthMenu: [[25, 50, 100, -1], [25, 50, 100, "All"]],
                data: data,
                columnDefs: [
                    {
                        targets: 0,width:100, render: function (data, type, full, meta) {
                        return full.appName;
                    }
                    },
                    {
                        targets: 1,width:100, render: function (data, type, full, meta) {
                        return full.templateKey;
                    }
                    },

                    {
                        targets: 2,width:100, render: function (data, type, full, meta) {
                            return full.propertyName;
                        }
                    },
                    {
                        targets: 3,width:100, render: function (data, type, full, meta) {
                                return full.propertyKey;
                            }
                    },
                    {
                        targets: 4,width:100, render: function (data, type, full, meta) {
                                return full.propertyType;
                            }
                    },
                    {
                        targets: 5,width:80, render: function (data, type, full, meta) {
                            return full.labelType;
                        }
                    }
                    ,
                    {
                        targets: 6,width:80, render: function (data, type, full, meta) {
                           var variable= getVariable(variableData,full.variableKey);
                           if(variable!=null){
                               return variable.variableName;
                           }
                        return full.variableKey;
                    }
                    },
                    {
                        targets: 7, width:50,render: function (data, type, full, meta) {
                            return moment(full.createTime).format("YYYY-MM-DD HH:mm:ss");
                        }
                    },
                    {
                        targets: 8, render: function (data, type, full, meta) {
                        var updateHtml= '<a class="btn btn-success btn-" onclick="queryByUserId(\'' + full.id + '\')"><i class="glyphicon glyphicon-edit"></i>修改</a>';

                        var deleteHtml= '&nbsp;&nbsp;<a class="btn btn-warning btn-circle" onclick="showModal(\'' + full.id + '\')"> <i class="glyphicon glyphicon-trash"></i>删除</a>';


                            return updateHtml+deleteHtml;
                    }
                    }
                ]
            });
        }
    });
}


function getVariable(variableData,variableKey) {
    if(variableData!=null){
        for (var i=0;i<variableData.length;i++){
            var variable=variableData[i];
            if(variable.variableKey==variableKey){
                return variable;
            }
        }
    }
    return null;
}



function add() {
    $("input[name='addReset']").trigger("click");

    appNameChange("addAppName","addTemplateKey");

    loadNoSearchSelect("addAttributeType");
    loadNoSearchSelect("addVariableKey");
    /*

    loadNoSearchSelect("addLabelType");
    loadNoSearchSelect("addPropertyType");



    initPropertyInfo("addLabelType","addLableSelectClass");
    changeLableType("addLabelType","addLableSelectClass");

    manager.loadVariable("addVariableKey");
    $("#addTemplateKey").show();
    $("#addAttributeType").on("change",function () {
        var flag=$("#addAttributeType").val();
        if('0'==flag || 0==flag){
            $("#addTemplateKey").hide();
        }else{
            $("#addTemplateKey").show();
        }
    });

    var appName=$("#appName").val();
    if (appName!=null && appName!=''){
        $("#addAppName").val(appName);
        manager.loadTemplate("addTemplateKey",appName);
        var templateKey=$("#templateKey").val();
        if(templateKey!=null && templateKey!=''){
            $("#addTemplateKey").val(templateKey);
        }
    }*/





    $('#addModal').modal('show');


}


function appNameChange(appNameId,templateKeyId) {

    loadSelect(templateKeyId);
    loadNoSearchSelect(appNameId);
    manager.loadApp(appNameId);

    var appName= $("#"+appNameId).val();
    manager.loadTemplateCustom(templateKeyId,appName,"公共模板");

    $("#"+appNameId).on("change",function () {
        var appName= $("#"+appNameId).val();
        manager.loadTemplateCustom(templateKeyId,appName,"公共模板");
        loadSelect(templateKeyId);
    })
}

function propertyHtml(key,value) {
    var propertyHtml="<div style='margin-top:5px' class='propertyGroupValue' > ";
    if(value!=null && value!=''){
        propertyHtml+="属性名称：<input class='form-control btn-group btn-value' placeholder='中文名称' value='"+value+"' style='width:32%;' /> ";
    }else{
        propertyHtml+="属性名称：<input class='form-control btn-group btn-value' placeholder='中文名称' style='width:32%;' /> ";
    }
    if (key!=null && key!=''){
        propertyHtml+="属性值:<input class='form-control btn-group btn-key' placeholder='对应值' value='"+key+"'  style='width:32%;' /> ";
    }else{
        propertyHtml+="属性值:<input class='form-control btn-group btn-key'  placeholder='对应值'  style='width:32%;' /> ";
    }


    propertyHtml+="<button type='button' class='btn btn-warning' onclick='removePropertyButtom(this)' >删除</button>";
    propertyHtml+="";
    propertyHtml+="</div>";
    return propertyHtml;
}
function addDivClass(divClass) {
    $("."+divClass).attr("style","border:1px #3300FF dashed;margin-top: 15px;margin-bottom:20px;")
}
function addSelectHtml(divClass) {
    $("."+divClass+"2").html("<button type='button' class='btn btn-block btn-success' style='margin-top: 10px;margin-bottom: 5px;' onclick='addPropertyButtom(\""+divClass+"\")' >添加</button>");
}
function inputHtml(divClass,labelValueId,labelValue) {
    $("."+divClass+"1").html('标签默认值：<input type="text" name="labelValue" id="'+labelValueId+'" value="'+labelValue+'" class="form-control" />');
    $("."+divClass+"2").html('');
}

function changeLableType(lableId,divClass) {



    $("#"+lableId).on("change",function () {

        initPropertyInfo(lableId,divClass);
    });
}
function initPropertyInfo(lableId,divClass) {
    var value=$("#"+lableId).val();
    if (value=='select'){
        $("."+divClass+"1").html(propertyHtml('',''));
        addDivClass(divClass);
        addSelectHtml(divClass);
    }else if('input'==value){
        addDivClass(divClass);
        inputHtml(divClass,"addLabelValue",'');
    }else{
        addDivClass(divClass);
        inputHtml(divClass,"addLabelValue",'');
    }
}

function removePropertyButtom(obj) {
    $(obj).parent().remove();
}

function addPropertyButtom(divClass) {
    $("."+divClass+"1").append(propertyHtml('',''));
}

function getPropertyJson(divClass) {
    var groups=$("."+divClass+" .propertyGroupValue");

    if (groups!=null && groups.length!=0){
        var json='';
        for (var i=0;i<groups.length;i++){
            var group=groups[i];
            var key=$(group).find("input.btn-key").val();
            var value=$(group).find("input.btn-value").val();
            if(json!=''){
                json+=",";
            }
            if(key==null || key=='' || value==null || value==''){
                return false;
            }
            json+='"'+key+'":';
            json+='"'+value+'"';
        }
        return "{"+json+"}";
    }
    return false;
}
