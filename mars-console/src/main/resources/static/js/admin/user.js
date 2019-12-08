$(function () {
    initDateTime("investDate");

    initOpenTime("releaseDate");
    initEndTime("endDate");

    initEndTime("predictArrivalDate");
    manager.loadPlan("planId");
    loadSelect("planId");
    loadNoSearchSelect("repayType");

})

function addUser() {
    var userIds=$("#userIds").val();
    loading();
    $.ajax({
        url: "./admin/user/addUser",
        type: "post",
        data: {"userIds": userIds},
        dataType: "json",
        success: function (data) {
            loaded();
            if (data.code == "0") {
                alert("操作成功");
            } else {
                alert(data.msg);
            }
        }
    });
}

function delUser() {
    if (window.confirm("确定要删除吗？")){
        var userIds=$("#userIds").val();
        loading();
        $.ajax({
            url: "./admin/user/delUser",
            type: "post",
            data: {"userIds": userIds},
            dataType: "json",
            success: function (data) {
                loaded();
                if (data.code == "0") {
                    alert(data.data);
                } else {
                    alert(data.msg);
                }
            }
        });
    }
}

$("#queryByUserId").on("click",function () {

    var userId=$("#userId").val();
    loading();
    $.ajax({
        url: "./admin/user/queryByUserId",
        type: "post",
        data: {"userId": userId},
        dataType: "json",
        success: function (data) {
            loaded();
            if (data.code == "0") {
                $("#realName").val(data.data);
            } else {
                alert(data.msg);
            }
        }
    });
});

$("#query").on("click",function () {
    manager.loadPlan("planId");
});

$("#planId").on("change",function () {
    var planId = $("#planId").val();
    if (planId!=null && planId!=''){
        queryByPlanId(planId);
    }else{
        window.location.reload();
    }
});

function queryByPlanId(id) {
    $("#repayType").prop("disabled", true);
    loading();
    $.ajax({
        url: "./admin/plan/queryById",
        type: "post",
        data: {"id": id},
        dataType: "json",
        success: function (data) {
            loaded();
            if (data.code == "0") {

                var plan = data.data;
                for(var p in plan){
                    if (p=='detailUrl' || p=='moreInfoUrl') {
                        continue;
                    }
                    $("#"+p).val(plan[p]);
                }
                $("#repayType").val(plan.repayType);
                $("#repayType").val(plan.repayType).select2();

                if (plan.releaseDate!=null){
                    var releaseDate= moment(plan.releaseDate).format("YYYY-MM-DD");
                    $("#releaseDateTemp").val(releaseDate);
                    $("#releaseDate").val(releaseDate+" 00:00:00");
                }

                if (plan.endDate!=null){
                    var endDate= moment(plan.endDate).format("YYYY-MM-DD");
                    $("#endDateTemp").val(endDate);
                    $("#endDate").val(endDate+" 23:59:59");

                }

                if (plan.predictArrivalDate!=null){
                    var predictArrivalDate= moment(plan.predictArrivalDate).format("YYYY-MM-DD");
                    $("#predictArrivalDateTemp").val(predictArrivalDate);
                    $("#predictArrivalDate").val(predictArrivalDate+" 23:59:59");
                }


            } else {
                alert(data.msg);
            }
        }
    });
}

function addWealthAssets() {
    var form = $("#form").serializeJson();
    loading();
    $.ajax({
        url: "./admin/user/addWealthAssets?v="+new Date().getTime(),
        type: "post",
        dataType: "json",
        contentType: "application/json",
        async:false,
        data:JSON.stringify(form),
        success: function (data) {
            loaded();
            if (data.code==0){
                alert("操作成功");
                window.location.reload();
            } else{
                alert(data.msg);
            }
        }
    });
}