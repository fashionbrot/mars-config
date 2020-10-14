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
    manager.loadProject("appName");
    loadSelect("templateKey");
    var appNameId="appName";
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


var queryByUserId = function (id) {
    loading();
    $.ajax({
        url: "../data/queryById",
        type: "post",
        data: {"id": id},
        //contentType: "application/json",
        dataType: "json",
        success: function (data) {
            loaded();
            $("#editEnvCode").val(data.envCode);
            $("#editAppName").val(data.appName);
            $("#editDataType").val(data.dataType);
            $("#editJson").val(formatJson(data.json));

            $("#editModal").modal("show");

            setTimeout(function () {
                //readyNumber();
            },500)
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
                url: "./admin/data/page?v="+new Date().getTime(),
                type: "get",
                dataType: "json",
                data: function(data){
                    data.page = data.start / data.length + 1;
                    data.pageSize = data.length;
                    data.envCode = envCode;
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
                    data : 'userName',
                    bSortable : true,
                    width : "20px",
                    className : "text-center",
                    render : dataTableConfig.DATA_TABLES.RENDER.ELLIPSIS
                },
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
                    data : 'dataType',
                    bSortable : true,
                    width : "20px",
                    render : function (data, type, full, meta) {
                        if (data==0){
                            return "导入"
                        }else if (data==1){
                            return "导出"
                        }else if (data==2){
                            return "编辑"
                        }else  if (data==3){

                        }else if (data==4){

                        }
                        return html;
                    }
                },{
                    data : 'id',
                    bSortable : true,
                    width : "20px",
                    render : function (data, type, full, meta) {
                        if(full.endTime=='' || full.endTime==null){
                            return '';
                        }
                        var html="";
                        var nowTime=new Date();
                        if(full.endTime!=null ) {
                            var time=moment(full.endTime).format("YYYY-MM-DD HH:mm:ss")
                            if(nowTime.getTime()>(new Date(full.endTime)).getTime()){
                                html+="<span style='color:red;font-weight: bold;'>"+time+"</span>"
                            }else{
                                html+=time;
                            }
                        }
                        return html;
                    }
                },
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
                    data : 'releaseType',
                    bSortable : true,
                    width : "20px",
                    className : "text-center",
                    render :function (data, type, full, meta) {
                        if (full.releaseType == "1") {
                            return "已发布";
                        } else {
                            return "<span style='color:red;font-weight: bold;'>未发布</span>";
                        }
                    }
                },
                {
                    data : 'userName',
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






    $('#dataTables-userInfo').dataTable().fnDestroy();
    var table = $('#dataTables-userInfo').DataTable({
        ajax:{
            url: "../data/queryAllCustom",
            type: "post",
            dataType: "json",
            data:{envCode:envCode,appName:appName}
        },
        language: {
            "sProcessing": "处理中...",
            "sLengthMenu": "显示 _MENU_ 项结果",
            "sZeroRecords": "没有匹配结果",
            "sInfo": "显示第 _START_ 至 _END_ 项结果，共 _TOTAL_ 项",
            "sInfoEmpty": "显示第 0 至 0 项结果，共 0 项",
            "sInfoFiltered": "(由 _MAX_ 项结果过滤)",
            "sInfoPostFix": "",
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
        serverSide:true,
        ordering:false,
        stateSave: false,
        searching: false,
        paging: true,
        info: false,
        bAutoWidth: false,
        lengthMenu: [[10, 20, 25, -1], [10, 20, 25, "All"]],
        columnDefs: [
            {
                targets: 0, render: function (data, type, full, meta) {
                return full.id;
            }
            },
            {
                targets: 1, render: function (data, type, full, meta) {
                return full.userName;
            }
            },
            {
                targets: 2, render: function (data, type, full, meta) {
                return full.envCode;
            }
            },
            {
                targets: 3, render: function (data, type, full, meta) {
                return full.appName;
            }
            },
            {
                targets: 4, render: function (data, type, full, meta) {
                return full.templateKey;
            }
            },
            {
                targets: 5, render: function (data, type, full, meta) {
                if (full.dataType == 0) {
                    return "<span style='color: #55ea55'>导入</span>";
                } else {
                    return "导出";
                }
            }
            },
            {
                targets: 6, render: function (data, type, full, meta) {
                return moment(full.createTime).format("YYYY-MM-DD HH:mm:ss");
            }
            },
            {
                targets: 7, render: function (data, type, full, meta) {
                return '<a class="btn btn-success btn-" onclick="queryByUserId(\'' + full.id + '\')"><i class="fa fa-edit">查看</i> </a>'
                    + '&nbsp;&nbsp;';
                /*<a class="btn btn-warning btn-circle" onclick="showModal(\'' + full.id + '\',this)"> <i class="fa fa-times">删除</i></a>*/
            }
            }
        ]

    });
    loaded();

}