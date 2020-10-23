$(function () {


    $('#side-menu').metisMenu();

    $('.sidebar-toggle').on('click', function () {
        $('body').toggleClass('sidebar-collapse');
        setTimeout(function () {
            $(window).resize()
        }, 850)
    });

    $('.sidebar-search-collapse').on('click', function () {
        $('body').toggleClass('sidebar-collapse');
        $('.sidebar-search input').focus()
        setTimeout(function () {
            $(window).resize()
        }, 500)
    });

    $('.panel').on('click', '.panel-collapse', function () {
        var $panel = $(this).closest('.panel')
        $('.panel-heading .panel-collapse i', $panel).toggleClass('fa-caret-down').toggleClass('fa-caret-up')
        $('.panel-body', $panel).toggleClass('hidden')
    })


    if (jQuery().sparkline)
        $("#page-title-statistics").sparkline([10, 3, 4, -3, -2, 5, 8, 11, 6, 7, -7, -5, 8, 9, 5, 6, 7, 2, 0, -4, -2, 4], {
            type: 'bar',
            barColor: '#00a652',
            negBarColor: '#00a652'
        });

    $('#toggle-right-sidebar').on('click', function () {
        $('.sidebar-right').toggleClass('open')

        var width = $(window).width();
        if (width < 768) {
            $('.sidebar-right').attr('style', '')
        } else {
            $('.sidebar-right').height($('body').height() - 50)
        }

        $('.sidebar-right').css('display', 'block');

        setTimeout(function () {
            $(window).resize()
            if (!$('.sidebar-right').hasClass('open'))
                $('.sidebar-right').hide(0);
        }, 500)
    })

    var a= Base64.decode(getCookie("edcvfr"));
    $("#realName").html(a);

})


$(window).resize(function () {
    var width = $(window).width();
    if (width < 768) {
        $('.sidebar-right.open').attr('style', '')
    } else {
        $('.sidebar-right.open').height($('body').height() - 50)
    }
})


//Loads the correct sidebar on window load,
//collapses the sidebar on window resize.
// Sets the min-height of #page-wrapper to window size
$(function () {
    $(window).bind("load resize", function () {
        topOffset = 50;
        width = (this.window.innerWidth > 0) ? this.window.innerWidth : this.screen.width;
        if (width < 768) {
            $('div.navbar-collapse').addClass('collapse')
            topOffset = 100; // 2-row-menu
        } else {
            $('div.navbar-collapse').removeClass('collapse')
        }

        height = (this.window.innerHeight > 0) ? this.window.innerHeight : this.screen.height;
        height = height - topOffset;
        if (height < 1)
            height = 1;
        if (height > topOffset) {
            $("#page-wrapper").css("min-height", (height) + "px");
        }
    })
})


$.fn.serializeJson = function () {
    var o = {};
    var a = this.serializeArray();
    $.each(a, function () {
        if (o[this.name]) {
            o[this.name] = o[this.name].concat(",").concat(this.value);
        } else {
            o[this.name] = this.value || '';
        }
    });
    return o;
};

/**
 *转换日期对象为日期字符串
 * @param date 日期对象
 * @param isFull 是否为完整的日期数据,
 *               为true时, 格式如"2000-03-05 01:05:04"
 *               为false时, 格式如 "2000-03-05"
 * @return 符合要求的日期字符串
 */
function getSmpFormatDate(date, isFull) {
    var pattern = "";
    if (isFull == true || isFull == undefined) {
        pattern = "yyyy-MM-dd hh:mm:ss";
    } else {
        pattern = "yyyy-MM-dd";
    }
    return getFormatDate(date, pattern);
}
/**
 *转换当前日期对象为日期字符串
 * @param date 日期对象
 * @param isFull 是否为完整的日期数据,
 *               为true时, 格式如"2000-03-05 01:05:04"
 *               为false时, 格式如 "2000-03-05"
 * @return 符合要求的日期字符串
 */
function getSmpFormatNowDate(isFull) {
    return getSmpFormatDate(new Date(), isFull);
}
/**
 *转换long值为日期字符串
 * @param l long值
 * @param isFull 是否为完整的日期数据,
 *               为true时, 格式如"2000-03-05 01:05:04"
 *               为false时, 格式如 "2000-03-05"
 * @return 符合要求的日期字符串
 */
function getSmpFormatDateByLong(l, isFull) {
    return getSmpFormatDate(new Date(l), isFull);
}
/**
 *转换long值为日期字符串
 * @param l long值
 * @param pattern 格式字符串,例如：yyyy-MM-dd hh:mm:ss
 * @return 符合要求的日期字符串
 */
function getFormatDateByLong(l, pattern) {
    return getFormatDate(new Date(l), pattern);
}
/**
 *转换日期对象为日期字符串
 * @param l long值
 * @param pattern 格式字符串,例如：yyyy-MM-dd hh:mm:ss
 * @return 符合要求的日期字符串
 */
function getFormatDate(date, pattern) {
    if (date == undefined) {
        date = new Date();
    }
    if (pattern == undefined) {
        pattern = "yyyy-MM-dd hh:mm:ss";
    }
    return date.formats(pattern);
}

Date.prototype.formats = function (format) {
    var o = {
        "M+": this.getMonth() + 1,
        "d+": this.getDate(),
        "h+": this.getHours(),
        "m+": this.getMinutes(),
        "s+": this.getSeconds(),
        "q+": Math.floor((this.getMonth() + 3) / 3),
        "S": this.getMilliseconds()
    }
    if (/(y+)/.test(format)) {
        format = format.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    }
    for (var k in o) {
        if (new RegExp("(" + k + ")").test(format)) {
            format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length));
        }
    }
    return format;
}

/*$(".x-navigation  li > a").click(function(){

    var li = $(this).parent('li');
    var ul = li.parent("ul");

    ul.find(" > li").not(li).removeClass("active");

});*/

/*$(".x-navigation li").click(function(event){
    event.stopPropagation();

    var li = $(this);

    if(li.children("ul").length > 0 || li.children(".panel").length > 0 || $(this).hasClass("xn-profile") > 0){
        if(li.hasClass("active")){
            li.removeClass("active");
            li.find("li.active").removeClass("active");
        }else
            li.addClass("active");

        onresize();

        if($(this).hasClass("xn-profile") > 0)
            return true;
        else
            return false;
    }
});*/

/*function onresize(timeout){
    timeout = timeout ? timeout : 200;

    setTimeout(function(){
        page_content_onresize();
    },timeout);
}*/
/*
function page_content_onresize(){
    var vpW = Math.max(document.documentElement.clientWidth, window.innerWidth || 0)
    var vpH = Math.max(document.documentElement.clientHeight, window.innerHeight || 0);

    $(".page-content,.content-frame-body,.content-frame-right,.content-frame-left").css("width","").css("height","");

    $(".sidebar .sidebar-wrapper").height(vpH);

    var content_minus = 0;
    content_minus = ($(".page-container-boxed").length > 0) ? 40 : content_minus;
    content_minus += ($(".page-navigation-top-fixed").length > 0) ? 50 : 0;

    var content = $(".page-content");
    var sidebar = $(".page-sidebar");

    if(content.height() < vpH - content_minus){
        content.height(vpH - content_minus);
    }

    if(sidebar.height() > content.height()){
        content.height(sidebar.height());
    }

    if($(".page-content-adaptive").length > 0)
        $(".page-content-adaptive").css("min-height",vpH-80);

    if(vpW > 1024){

        if($(".page-sidebar").hasClass("scroll")){
            if($("body").hasClass("page-container-boxed")){
                var doc_height = vpH - 40;
            }else{
                var doc_height = vpH;
            }
            $(".page-sidebar").height(doc_height);

        }

        var fbm = $("body").hasClass("page-container-boxed") ? 200 : 162;

        var cfH = $(".content-frame").height();
        if($(".content-frame-body").height() < vpH-162){

            var cfM = vpH-fbm < cfH-80 ? cfH-80 : vpH-fbm;

            $(".content-frame-body,.content-frame-right,.content-frame-left").height(cfM);

        }else{
            $(".content-frame-right,.content-frame-left").height($(".content-frame-body").height());
        }

        $(".content-frame-left").show();
        $(".content-frame-right").show();
    }else{
        $(".content-frame-body").height($(".content-frame").height()-80);

        if($(".page-sidebar").hasClass("scroll"))
            $(".page-sidebar").css("height","");
    }

    if(vpW < 1200){
        if($("body").hasClass("page-container-boxed")){
            $("body").removeClass("page-container-boxed").data("boxed","1");
        }
    }else{
        if($("body").data("boxed") === "1"){
            $("body").addClass("page-container-boxed").data("boxed","");
        }
    }
    //$(window).trigger("resize");
}*/
function getSelectText(obj){
    return $("#"+obj).find("option:selected").text();
}
function getSelectVaule(obj){
    return $("#"+obj).find("option:selected").val();
}


var manager={
    loadTemplate:function (labelId,appName) {
        $.ajax({
            url: "../admin/template/list?appName="+appName,
            type: "get",
            dataType: "json",
            async:false,
            success: function (result) {
                var data= result.data;
                if(data!=null){
                    var rows=data;
                    var html="<option value=''>请选择模板</option>";
                    for(var i=0;i<rows.length;i++){
                        var row=rows[i];
                        html+="<option value='"+row.templateKey+"'>"+row.templateName+"</option>";
                    }
                }
                $("#"+labelId).html(html);
            }
        });
    },
    loadTemplateCustom:function (labelId,appName,firstOption) {
        $.ajax({
            url: "../admin/template/list?appName="+appName,
            type: "get",
            contentType: "application/json",
            dataType: "json",
            async:false,
            success: function (result) {
                var data= result.data;
                if(data!=null){
                    var rows=data;
                    var html="<option value='-1'>"+firstOption+"</option>";
                    for(var i=0;i<rows.length;i++){
                        var row=rows[i];
                        html+="<option value='"+row.templateKey+"'>"+row.templateName+" - "+row.templateKey+"</option>";
                    }
                }
                $("#"+labelId).html(html);
            }
        });
    },loadVariable:function (id) {
        $.ajax({
            url: "../admin/variable/queryList",
            type: "get",
            contentType: "application/json",
            dataType: "json",
            async:false,
            success: function (data) {
                if(data!=null){
                    var rows=data;
                    var html="<option value=''>无前缀</option>";
                    for(var i=0;i<rows.length;i++){
                        var row=rows[i];
                        html+="<option value='"+row.variableKey+"'>"+row.variableKey+"  -  "+row.variableName+"</option>";
                    }
                }
                $("#"+id).html(html);
            }
        });
    },
    loadPlan:function (labelId) {
        $.ajax({
            url: "./admin/plan/list",
            type: "post",
            dataType: "json",
            async:false,
            success: function (data) {
                if(data!=null){
                    var rows=data.data;
                    var html="<option value=''>请选择</option>";
                    for(var i=0;i<rows.length;i++){
                        var row=rows[i];
                        html+="<option value='"+row.id+"'>"+row.planTitle+"</option>";
                    }
                }
                $("#"+labelId).html(html);
            }
        });
    },loadMenuLevel:function (id,level) {
        $.ajax({
            url: "./admin/menu/queryMenuLevel?menuLevel="+level,
            type: "post",
            dataType: "json",
            async:false,
            success: function (data) {
                if(data!=null){
                    var rows=data;
                    var html="<option value='0'>请选择</option>";
                    for(var i=0;i<rows.length;i++){
                        var row=rows[i];
                        html+="<option value='"+row.id+"'>"+row.menuName+"</option>";
                    }
                }
                $("#"+id).html(html);
            }
        });
    }
    ,loadRole:function (id) {
        $.ajax({
            url: "./user/queryRoleAll",
            type: "post",
            dataType: "json",
            async:false,
            success: function (data) {
                if(data!=null){
                    var rows=data;
                    var html="<option value=''>请选择</option>";
                    for(var i=0;i<rows.length;i++){
                        var row=rows[i];
                        html+="<option value='"+row.id+"'>"+row.roleName+"</option>";
                    }
                }
                $("#"+id).html(html);
            }
        });
    },loadEnv:function (id) {
        $.ajax({
            url: "./env/queryAll",
            type: "post",
            contentType: "application/json",
            dataType: "json",
            async: false,
            success: function (data) {
                if (data != null) {
                    var rows = data;
                    var html = "";
                    var jsonStr = '';
                    for (var i = 0; i < rows.length; i++) {
                        var row = rows[i];
                        html += "<option value='" + row.envCode + "'>" + row.envName + "</option>";
                        if (jsonStr != '') {
                            jsonStr += ',';
                        }
                        jsonStr += '"' + row.id + '":"' + row.envName + '"';
                    }
                    jsonStr = "{" + jsonStr + "}";
                    envJson = eval('(' + jsonStr + ')');
                }
                $("#" + id).html(html);
            }
        });
    },loadApp:function (id,topOption) {
        $.ajax({
            url: "./app/queryAll",
            type: "post",
            contentType: "application/json",
            dataType: "json",
            async:false,
            success: function (data) {
                if(data!=null){
                    var rows=data;
                    var html="";
                    if (topOption!=null){
                        html="<option value='-1'>"+topOption+"</option>";
                    }
                    for(var i=0;i<rows.length;i++){
                        var row=rows[i];
                        html+="<option value='"+row.appName+"'>"+row.appDesc+"</option>";
                    }
                }
                $("#"+id).html(html);
            }
        });
    }
}


var formatJson = function(json, options) {
    var reg = null,
        formatted = '',
        pad = 0,
        PADDING = '    '; // one can also use '\t' or a different number of spaces
    // optional settings
    options = options || {};
    // remove newline where '{' or '[' follows ':'
    options.newlineAfterColonIfBeforeBraceOrBracket = (options.newlineAfterColonIfBeforeBraceOrBracket === true) ? true : false;
    // use a space after a colon
    options.spaceAfterColon = (options.spaceAfterColon === false) ? false : true;

    // begin formatting...

    // make sure we start with the JSON as a string
    if (typeof json !== 'string') {
        json = JSON.stringify(json);
    }
    // parse and stringify in order to remove extra whitespace
    json = JSON.parse(json);
    json = JSON.stringify(json);

    // add newline before and after curly braces
    reg = /([\{\}])/g;
    json = json.replace(reg, '\r\n$1\r\n');

    // add newline before and after square brackets
    reg = /([\[\]])/g;
    json = json.replace(reg, '\r\n$1\r\n');

    // add newline after comma
    reg = /(\,)/g;
    json = json.replace(reg, '$1\r\n');

    // remove multiple newlines
    reg = /(\r\n\r\n)/g;
    json = json.replace(reg, '\r\n');

    // remove newlines before commas
    reg = /\r\n\,/g;
    json = json.replace(reg, ',');

    // optional formatting...
    if (!options.newlineAfterColonIfBeforeBraceOrBracket) {
        reg = /\:\r\n\{/g;
        json = json.replace(reg, ':{');
        reg = /\:\r\n\[/g;
        json = json.replace(reg, ':[');
    }
    if (options.spaceAfterColon) {
        reg = /\:/g;
        json = json.replace(reg, ': ');
    }

    $.each(json.split('\r\n'), function(index, node) {
        var i = 0,
            indent = 0,
            padding = '';

        if (node.match(/\{$/) || node.match(/\[$/)) {
            indent = 1;
        } else if (node.match(/\}/) || node.match(/\]/)) {
            if (pad !== 0) {
                pad -= 1;
            }
        } else {
            indent = 0;
        }

        for (i = 0; i < pad; i++) {
            padding += PADDING;
        }

        formatted += padding + node + '\r\n';
        pad += indent;
    });

    return formatted;
};

function formatJson2(msg) {
    var rep = "~";
    var jsonStr = JSON.stringify(msg, null, rep)
    var str = "";
    for (var i = 0; i < jsonStr.length; i++) {
        var text2 = jsonStr.charAt(i)
        if (i > 1) {
            var text = jsonStr.charAt(i - 1)
            if (rep != text && rep == text2) {
                str += "<br/>"
            }
        }
        str += text2;
    }
    jsonStr = "";
    for (var i = 0; i < str.length; i++) {
        var text = str.charAt(i);
        if (rep == text)
            jsonStr += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
        else {
            jsonStr += text;
        }
        if (i == str.length - 2)
            jsonStr += "<br/>"
    }
    return jsonStr;
}

function readyNumber() {

    $('textarea').each(function () {
        this.setAttribute('style', 'height:' + (this.scrollHeight) + 'px;overflow-y:hidden;');
    }).on('input', function () {
        this.style.height = 'auto';
        this.style.height = (this.scrollHeight) + 'px';
    })
}


var loadingHtml="<div class=\"loadEffect\">\n" +
    "        <span></span>\n" +
    "        <span></span>\n" +
    "        <span></span>\n" +
    "        <span></span>\n" +
    "        <span></span>\n" +
    "        <span></span>\n" +
    "        <span></span>\n" +
    "        <span></span>\n" +
    "</div>";

//document.write(loadingHtml);

/*
 * 显示loading遮罩层
 */
function loading() {
    var mask_bg = document.createElement("div");
    mask_bg.id = "mask_bg";
    mask_bg.style.position = "fixed";
    mask_bg.style.top = "0px";
    mask_bg.style.left = "0px";
    mask_bg.style.width = "100%";
    mask_bg.style.height = "100%";
    mask_bg.style.backgroundColor = "#777";
    mask_bg.style.opacity = 0.6;
    mask_bg.style.zIndex = 10001;
    document.body.appendChild(mask_bg);

    var mask_msg = document.createElement("div");
    mask_msg.style.position = "absolute";
    mask_msg.style.top = "35%";
    mask_msg.style.left = "42%";
    mask_msg.style.textAlign = "center";
    mask_msg.style.fontSize = "1.1em";
    mask_msg.style.fontWeight = "bold";
    mask_msg.style.padding = "0.5em 3em 0.5em 3em";
    mask_msg.style.zIndex = 10002;
    mask_msg.innerHTML = loadingHtml;
    mask_bg.appendChild(mask_msg);
}
/*
 * 关闭遮罩层
 */
function loaded() {
   var mask_bg = document.getElementById("mask_bg");
    if (mask_bg != null)
        mask_bg.parentNode.removeChild(mask_bg);
    //$("#mask_bg").hide();
}


$(document).ready(function() {
    $(document).ajaxComplete(function(event, xhr, settings) {
        if (xhr.getResponseHeader('REQUIRE_AUTH') === 'true'){
            var top = getTopWinow();
            top.location.href = './user/login';
        }
    });
});



function error(jqXHR, textStatus, errorThrown) {
    switch (jqXHR.status){
        case(500):
            alert("服务器系统内部错误");
            break;
        case(401):
            var aa=confirm("登陆已过期,确定要重新登录吗？");
            if(aa){
                var top = getTopWinow();
                top.location.href = '../user/login';

            }
            if(true){
                loaded();
                throw new Error("登陆超时");
                //return false;
            }

            break;
        case(403):
            alert("无权限执行此操作");
            break;
        case(408):
            alert("请求超时");
            break;
        case (200):
            break;
        default:
            alert("未知错误");
    }
}

/**
 * 在页面中任何嵌套层次的窗口中获取顶层窗口
 * @return 当前页面的顶层窗口对象
 */
function getTopWinow(){
    var p = window;
    while(p != p.parent){
        p = p.parent;
    }
    return p;
}



function loadSelectByCount(id) {
    var optionSize=$("select[id='"+id+"'] option").size();
    //console.log("id:"+id +" size:"+optionSize)
    if (optionSize>5){
        $("#"+id).select2({
            theme: "classic"
        });
    } else {
        $("#"+id).select2({
            theme: "classic",
            minimumResultsForSearch: -1
        });
    }
}

function loadSelect(id) {
    $("#"+id).select2({
        theme: "classic"
    });
}
function loadNoSearchSelect(id) {
    $("#"+id).select2({
        theme: "classic",
        minimumResultsForSearch: -1
    });
}

function isEmptyObject(obj) {
    for (var key in obj) {
        return false;
    }
    return true;
}

function isNullObj(obj){
    for(var i in obj){
        if(obj.hasOwnProperty(i)){
            return false;
        }
    }
    return true;
}

function initDateTime(dateId) {
    lay('#'+dateId).each(function(){
        laydate.render({
            elem: this
            //,area:['600px','500px']
            ,position:'fixed'
            ,format:'yyyy-MM-dd HH:mm:ss'
            ,type:'datetime'
            ,trigger: 'click'
            ,theme: 'grid'
            ,zIndex: 99999999
            ,top:30
        });
    });
}
function initDateOpt(dateId) {
    var format="";
    var type="";
    if (dateId.indexOf("date-year")!=-1){
        format="yyyy";
        type="year";
    }else if (dateId.indexOf("date-time")!=-1){
        format="HH:mm:ss";
        type="time";
    }else if (dateId.indexOf("date-datetime")!=-1){
        format="yyyy-MM-dd HH:mm:ss";
        type="datetime";
    }else if (dateId.indexOf("date-date")!=-1){
        format="yyyy-MM-dd";
        type="date";
    }
    console.log(dateId,format,type);
    lay('#'+dateId).each(function(){
        laydate.render({
            elem: this
            ,position:'fixed'
            ,format:format
            ,type:type
            ,trigger: 'click'
            ,theme: 'grid'
            ,zIndex: 99999999
            ,top:30
        });
    });
}


$("#logout").on("click",function () {
    if(window.confirm('确定要退出登录吗?')){
        var top = getTopWinow();
        top.location.href = '../user/logout';
    }
})
$("#changePwd").on("click",function () {
    $("#pwdViewId").modal('show');
})
function resetPwd() {

    var pass =  $("#oldPwd").val();
    var $newpass = $("#newPwd").val();
    var $rePass = $("#newPwd2").val();
    if (pass == '') {
        alert('请输入原密码');
        return false;
    }
    if ($newpass == '') {
        alert('请输入新密码！');
        return false;
    }
    if ($rePass == '') {
        alert('请在一次输入新密码！');
        return false;
    }

    if ($newpass != $rePass) {
        alert('两次密码不一至！请重新输入');
        return false;
    }

    $.ajax({
        type: 'POST',
        url: '../user/resetPwd',
        data: { pwd:pass,newPwd:$newpass},
        dataType: "JSON",
        success: function (data) {
            if (data.code == 0){
                alert('恭喜您，密码修改成功！');
                $("#oldPwd").val('');
                $("#newPwd").val('');
                $("#newPwd2").val('');
                $("#pwdViewId").modal("hide");
            }else {
                alert( data.msg);
            }
        },
        error: function () {
            alert("网络异常，请稍后再试");
        }
    });
}



/** 拖拽模态框*/
var dragModal={
    mouseStartPoint:{"left":0,"top":  0},
    mouseEndPoint : {"left":0,"top":  0},
    mouseDragDown : false,
    basePoint : {"left":0,"top":  0},
    moveTarget:null,
    topleng:0
}
$(document).on("mousedown",".modal-header",function(e){
    //webkit内核和火狐禁止文字被选中
    $('body').addClass('select');
    //ie浏览器禁止文字选中
    document.body.onselectstart=document.body.ondrag=function(){
        return false;
    }
    if($(e.target).hasClass("close"))//点关闭按钮不能移动对话框
        return;
    dragModal.mouseDragDown = true;
    dragModal.moveTarget = $(this).parent().parent();
    dragModal.mouseStartPoint = {"left":e.clientX,"top":  e.pageY};
    dragModal.basePoint = dragModal.moveTarget.offset();
    dragModal.topLeng=e.pageY-e.clientY;
});
$(document).on("mouseup",function(e){
    dragModal.mouseDragDown = false;
    dragModal.moveTarget = undefined;
    dragModal.mouseStartPoint = {"left":0,"top":  0};
    dragModal.basePoint = {"left":0,"top":  0};
});
$(document).on("mousemove",function(e){
    if(!dragModal.mouseDragDown || dragModal.moveTarget == undefined)return;
    var mousX = e.clientX;
    var mousY = e.pageY;
    if(mousX < 0)mousX = 0;
    if(mousY < 0)mousY = 25;
    dragModal.mouseEndPoint = {"left":mousX,"top": mousY};
    var width = dragModal.moveTarget.width();
    var height = dragModal.moveTarget.height();
    var clientWidth=document.body.clientWidth
    var clientHeight=document.body.clientHeight;
    if(dragModal.mouseEndPoint.left<dragModal.mouseStartPoint.left - dragModal.basePoint.left){
        dragModal.mouseEndPoint.left=0;
    }
    else if(dragModal.mouseEndPoint.left>=clientWidth-width+dragModal.mouseStartPoint.left - dragModal.basePoint.left){
        dragModal.mouseEndPoint.left=clientWidth-width-38;
    }else{
        dragModal.mouseEndPoint.left =dragModal.mouseEndPoint.left-(dragModal.mouseStartPoint.left - dragModal.basePoint.left);//移动修正，更平滑

    }
    if(dragModal.mouseEndPoint.top-(dragModal.mouseStartPoint.top - dragModal.basePoint.top)<dragModal.topLeng){
        dragModal.mouseEndPoint.top=dragModal.topLeng;
    }else if(dragModal.mouseEndPoint.top-dragModal.topLeng>clientHeight-height+dragModal.mouseStartPoint.top - dragModal.basePoint.top){
        dragModal.mouseEndPoint.top=clientHeight-height-38+dragModal.topLeng;
    }
    else{
        dragModal.mouseEndPoint.top = dragModal.mouseEndPoint.top - (dragModal.mouseStartPoint.top - dragModal.basePoint.top);
    }
    dragModal.moveTarget.offset(dragModal.mouseEndPoint);
});
$(document).on('hidden.bs.modal','.modal',function(e){
    $('.modal-dialog').css({'top': '0px','left': '0px'})
    $('body').removeClass('select')
    document.body.onselectstart=document.body.ondrag=null;
})



/*
var path=window.location.pathname;
var url=window.location.href;
selectTable();
function selectTable() {
    var a1=$(".xn-openable");
    var temp="";
    for(var i=0;i<a1.length;i++){
        var a2=$(a1[i]).find("ul li a");
        for(var j=0;j<a2.length;j++){
            var a3=$(a2[j]).attr("href");
            if(path==a3){
                $(a1[i]).addClass("active");
                $(a2[j]).parent().addClass("active");
                temp=a3;
            }else{
                if(path!=temp){
                    $(a1[i]).removeClass("active");
                    $(a2[j]).removeClass("active");
                }
            }
        }

    }
    $(".xn-openable").addClass('active');
}
*/

var dataTable={
    language:function () {
        return {
            "sProcessing": "处理中...",
            "sLengthMenu": "显示 _MENU_ 项结果",
            "sZeroRecords": "没有匹配结果",
            "sInfo": "显示第 _START_ 至 _END_ 项结果，共 _TOTAL_ 项",
            "sInfoEmpty": "显示第 0 至 0 项结果，共 0 项",
            "sInfoFiltered": "",
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
            },
            "oAria": {
                "sSortAscending": ": 以升序排列此列",
                "sSortDescending": ": 以降序排列此列"
            }
        };
    }
}

function initOpenTime(dateId) {
    lay('#'+dateId+'Temp').each(function(){
        laydate.render({
            elem: this
            //,area:['600px','500px']
            ,position:'fixed'
            ,format:'yyyy-MM-dd'
            ,type:'date'
            ,trigger: 'click'
            ,theme: 'grid'
            ,zIndex: 99999999
            ,top:30
            ,ready:function (date) {

            },done:function (value,date) {
                $("#"+dateId).val(value+" 00:00:00");
            }
        });
    });
}

function initEndTime(dateId) {
    lay('#'+dateId+'Temp').each(function(){
        laydate.render({
            elem: this
            //,area:['600px','500px']
            ,position:'fixed'
            ,format:'yyyy-MM-dd'
            ,type:'date'
            ,trigger: 'click'
            ,theme: 'grid'
            ,zIndex: 99999999
            ,top:30
            ,done:function (value,date) {
                $("#"+dateId).val(value+" 23:59:59");
            }
        });
    });
}




//读取cookies
function getCookie(name) {
    var arr,reg=new RegExp("(^| )"+name+"=([^;]*)(;|$)");
    if(arr=document.cookie.match(reg))
        return unescape(arr[2]);
    else
        return "";
}

//删除cookies
function delCookie(name) {
    var exp = new Date();
    exp.setTime(exp.getTime() - 1);
    var value=getCookie(name);
    if(value!=null)
        document.cookie= name + "="+value+";expires="+exp.toGMTString();
}

/*
$(function () {
    $('.nav-item >a').click(function () {
        console.log($(this).attr('src'));
        var frame =$('#body-iframe');
        frame.prop('src',$(this).attr('src'));
        // frame.attr("height",window.height-50);
        // console.log(frame.attr("height"));
    });
})
*/


var dataTableConfig = {
    DATA_TABLES : {
        DEFAULT_OPTION : { // DataTables初始化选项
            LANGUAGE : {
                sProcessing : "处理中...",
                sLengthMenu : "显示 _MENU_ 项结果",
                sZeroRecords : "没有匹配结果",
                sInfo : "显示第 _START_ 至 _END_ 项结果，共 _TOTAL_ 项",
                sInfoEmpty : "显示第 0 至 0 项结果，共 0 项",
                sInfoFiltered : "(由 _MAX_ 项结果过滤)",
                sInfoPostFix : "",
                sSearch : "搜索:",
                searchPlaceholder : "关键字搜索",
                sUrl : "",
                sEmptyTable : "表中数据为空",
                sLoadingRecords : "载入中...",
                sInfoThousands : ",",
                oPaginate : {
                    sFirst : "首页",
                    sPrevious : "上页",
                    sNext : "下页",
                    sLast : "末页"
                },
                oAria : {
                    sSortAscending : ": 以升序排列此列",
                    sSortDescending : ": 以降序排列此列"
                }
            },
            // 禁用自动调整列宽
            autoWidth : false,
            // 为奇偶行加上样式，兼容不支持CSS伪类的场合
            stripeClasses : [ "odd", "even" ],
            // 取消默认排序查询,否则复选框一列会出现小箭头
            order : [],
            // 隐藏加载提示,自行处理
            processing : false,
            // 启用服务器端分页
            serverSide : true,
            // 禁用原生搜索
            searching : false
        },
        COLUMN : {
            // 复选框单元格
            CHECKBOX : {
                className: "td-checkbox",
                orderable : false,
                bSortable : false,
                data : "id",
                width : '10px',
                render : function(data, type, row, meta) {
                    var content = '<label class="mt-checkbox mt-checkbox-single mt-checkbox-outline">';
                    content += '	<input type="checkbox" class="group-checkable" value="' + data + '" />';
                    content += '	<span></span>';
                    content += '</label>';
                    return content;
                }
            }
        },
        // 回调
        CALLBACKS : {
            // 表格绘制前的回调函数
            PREDRAWCALLBACK : function(settings) {
                if (settings.oInit.scrollX == '100%') {
                    // 给表格添加css类，处理scrollX : true出现边框问题
                    $(settings.nTableWrapper).addClass('dataTables_DTS');
                }
            },
            INITCOMPLETE : function(settings, json) {
                if (settings.oInit.scrollX == '100%' && $(settings.nTable).parent().innerWidth() - $(settings.nTable).outerWidth() > 5) {
                    $(settings.nScrollHead).children().width('100%');
                    $(settings.nTHead).parent().width('100%');
                    $(settings.nTable).width('100%');
                }
            },
            // 表格每次重绘回调函数
            DRAWCALLBACK : function(settings) {
                if ($(settings.aoHeader[0][0].cell).find(':checkbox').length > 0) {
                    // 取消全选
                    $(settings.aoHeader[0][0].cell).find(':checkbox').prop('checked', false);
                }
                // 高亮显示当前行
                $(settings.nTable).find("tbody tr").click(function(e) {
                    $(e.target).parents('table').find('tr').removeClass('warning');
                    $(e.target).parents('tr').addClass('warning');
                });
            }
        },
        // 常用render可以抽取出来，如日期时间、头像等
        RENDER : {
            ELLIPSIS : function(data, type, row, meta) {
                data = data || "";
                return '<span title="' + data + '">' + data + '</span>';
            }
        }

    }

};