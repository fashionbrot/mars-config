var setting = {
    view : {
        dblClickExpand : false
    },
    data : {
        simpleData : {
            enable : true,
            idKey : "id",
            pIdKey: "pid",
            rootPId: 0
        }
    }
};

var zNodes = [ {
    id : 1,
    pId : 0,
    name : "北京"
}, {
    id : 2,
    pId : 0,
    name : "天津"
}, {
    id : 3,
    pId : 0,
    name : "上海"
} ];
//初始化树形下拉框

function initAjaxSelect2Tree(url,method,selectId){
    $.ajax({
        url: url,
        type: method,
        dataType: "json",
        async:false,
        success: function (data) {
            if (data.code!=0){
                console.log("加载失败");
                return false;
            }
            initSelect2Tree(selectId,data.data);
        },error:function (){
            alert("请求失败");
        }
    });
}

function initSelect2Tree(selectId,selectData){
    $('#'+selectId).select2ztree({
        textField : 'name',
        titleField : 'name',
        theme: "classic",
        placeholder:"请选择机构",
        ztree : {
            setting : setting,
            zNodes : selectData
        }
    });
}
function selectedValue(selectId,value){
    $('#'+selectId).select2ztree('val',[value]);
}