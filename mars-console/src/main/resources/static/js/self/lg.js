$("#loginIn").on("click",function () {
    lg();
})
$(function () {
    var top = getTopWinow()
    if (window.location.href != top.location.href){
        top.location.href = window.location.href;
    }
})

$(document).keyup(function(event){
    if(event.keyCode ==13){
        $("#loginIn").trigger("click");
    }
});
function lg() {
    var userName=$(".userName").val();
    if(userName==null || userName==''){
        alert("请输入用户名");
        return false;
    }
    var password=$(".password").val();
    if(password==null || password==''){
        alert("请输入密码");
        return false;
    }

    $.ajax({
        url: "./user/doLogin",
        type: "post",
        data: {"userName":userName,"password":password},
        dataType: "json",
        success: function (data) {
            if (data.code == "0") {
                location.href='./index';
            } else {
                alert(data.msg);
            }
        }
    });
}