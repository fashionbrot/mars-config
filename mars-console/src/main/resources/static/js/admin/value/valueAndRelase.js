


/**
 *
 * @param id           要渲染到的标签id
 * @param templateKey  模板key
 * @param appName      应用名
 * @param className    标记是add 还是 edit
 * @param json         编辑时回显的json
 * @param flag         true 代表add  false 代表编辑
 * @param diff         true 代表比较 false 代表不比较
 */
function loadPropertyAttrDiv(id,templateKey,appName,className,json,flag,diff) {


    $.ajax({
        url: "../admin/property/queryList?template_key="+templateKey,
        type: "get",
        dataType: "json",
        async:false,
        success: function (data) {
            loaded();
            $("#"+id).html('');
            if(data!=null){
                var rows=data;
                var html="";
                var dateIds=[];
                var selectIds=[];
                for(var i=0;i<rows.length;i++){
                    var row=rows[i];
                    var value;
                    var selectVariableKey;
                    if(json!=null && json!=''){
                        var valueJson=eval('(' + json + ')');
                        //var valueJson=strToJson(json);
                        value=forJson(valueJson,row.propertyKey);
                        selectVariableKey=forJson(valueJson,row.propertyKey+"Prefix");
                    }
                    if(value==null || value==''){
                        value='';
                    }

                    html+="<span style='color:#3300FF'>"+row.propertyName+":<span>";
                    var type=row.propertyType;
                    var key=row.propertyKey;
                    var name=row.propertyName;
                    var labelType=row.labelType;
                    var labelValue=row.labelValue;
                    var variableKey=row.variableKey;

                    /**
                     * 如果是编辑 把lableValue 赋值输入框内
                     */
                    if(flag && (labelType=='textarea' || labelType=='input')){
                        value=labelValue;
                    }

                    var propertyHtml="property-type='"+type+"'   property-key='"+key+"'  property-name='"+name+"' ";

                    var strValue="";
                    if (value!=null && value!=''){
                        strValue= JSON.stringify(value).replace(/\"/g,"");
                    }


                    if(labelType=='textarea'){

                        propertyHtml+=" placeholder='key:"+key+"  "+type+"类型'";
                        html+="<textarea type='text'   "+propertyHtml+" class='form-control "+className+"' >"+strValue+"</textarea>";

                    }else if(labelType=='select'){
                        var selectId=(flag?"add":(diff?"edit":"edit2"))+"selectJson"+key;
                        selectIds.push(selectId);
                        propertyHtml+=" id='"+selectId+"'  style='width:100%'";

                        var valueJson=eval('(' + labelValue + ')');
                        html += "<select   class='form-control " + className + "' "+propertyHtml+">";
                        for(var k in valueJson){
                            if(value==k){
                                html += "<option value='"+k+"' selected='selected' >"+valueJson[k]+" : "+k+"</option>";
                            }else{
                                html += "<option value='"+k+"'  >"+valueJson[k]+" : "+k+"</option>";
                            }
                        }
                        html += "</select>"

                    }else if(labelType=='input' ){
                        propertyHtml+=" placeholder='key:"+key+"  "+type+"类型'";

                        if (type=='boolean') {
                            html += "<select  class='form-control " + className + "' "+propertyHtml+" >";
                            if (value == true || value=='true') {
                                html += "<option value='true' selected='selected' >true</option>";
                                html += "<option value='false'>false</option>";
                            } else {
                                html += "<option value='false'>false</option>";
                                html += "<option value='true'>true</option>";
                            }
                            html += "</select>"
                        }else if(type=='long'){
                            var onkeyup="this.value=this.value.replace(/\\D/g,\"\")";
                            var onafterpaste="this.value=this.value.replace(/\\D/g,\"\")";
                            html+="<input type='text' onkeyup='"+onkeyup+"' onafterpaste='"+onafterpaste+"'  value='"+value+"' "+propertyHtml+" class='form-control "+className+"' />";
                        }else if(type=='date') {
                            var dateId = key + "-date-" + row.id;
                            dateIds.push(dateId);
                            html += "<input type='text' id='" + dateId + "' value='" + value + "' "+propertyHtml+" class='form-control layui-input " + className + "'  />";
                        }else if(type=='double'){
                            var onkeyup="if(isNaN(value))execCommand(\"undo\")";
                            var onafterpaste="if(isNaN(value))execCommand(\"undo\")";
                            html+="<input type='text' onkeyup='"+onkeyup+"' onafterpaste='"+onafterpaste+"'  value='"+value+"' "+propertyHtml+" class='form-control "+className+"'  />";
                        }else{

                            if (variableKey!=null && variableKey!=''){
                                var stringHtml="<input type='text' value='"+strValue+"'  "+propertyHtml+" style='width:70%' class='form-control btn-group "+className+"'  />";
                                var selectHtml=getVariableSelect(selectVariableKey,key,className);
                                html+="<div>"+selectHtml+stringHtml+"</div>";
                            }else{
                                var stringHtml="<input type='text' value='"+strValue+"' "+propertyHtml+" class='form-control "+className+"'  />";
                                html+=stringHtml;
                            }
                        }

                    }




                }
            }
            $("#"+id).html(html);
            for(var i=0; i<dateIds.length; i++){
                var dateId=dateIds[i];
                initDateTime(dateId);
            }
            if (selectIds!=null){
                for(var i=0; i<selectIds.length; i++){
                    var selectId=selectIds[i];
                    loadSelectByCount(selectId);
                }
                $.fn.modal.Constructor.prototype.enforceFocus = function () { };
            }
        }
    });
}

/**
 * 获取 环境变量 列表
 * @type {null}
 */
var variableData=null;
function loadVariableData() {
    if (variableData==null){
        $.ajax({
            url: "../admin/variable/queryList",
            type: "get",
            contentType: "application/json",
            dataType: "json",
            async: false,
            success: function (data) {
                variableData=data;
            }
        });
    }
}

/**
 *  新增|编辑 环境变量
 * @param selectVariableKey
 * @param propertyKey
 * @param className
 * @returns {string}
 */
function getVariableSelect(selectVariableKey,propertyKey,className) {

    loadVariableData();
    if(variableData!=null){
        var propertyKey =propertyKey;
        var className= className;
        var selectVariableKey=selectVariableKey;
        var html="<select property-key='"+propertyKey+"Prefix' property-type='prefix' class='form-control btn-group "+className+"' style='width:30%;float: left;'>";

        if(selectVariableKey=='-1'){
            html+="<option value='-1' selected='selected' >无前缀</option>";
        }else{
            html+="<option value='-1' >无前缀</option>";
        }
        for(var i=0;i<variableData.length;i++){
            var variable=variableData[i];
            if(variable.variableKey==selectVariableKey){
                html+="<option selected='selected' value='"+variable.variableKey+"'>"+variable.variableName+"</option>";
            }else{
                html+="<option  value='"+variable.variableKey+"'>"+variable.variableName+"</option>";
            }
        }

        html+="</select>";
        return html;
    }
    return "";
}



function forJson(json,key) {

    for(var k in json){
        if(key==k){
            return json[k]+"";
        }
    }
    return "";
}


 function updateConfig(operationType) {

    var templateKey=$("#editTemplateKey").val();
    if (isNullObj(templateKey)){
        alert("请选择模板");
        return false;
    }
    var json=getJson("editPropertyClass");
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
    $("#operationType").val(operationType);

    $("#editJson").val(json);

    var a = $("#userInfoEditForm").serializeJson();
    loading();
    $.ajax({
        url: "../admin/config/value/updateById",
        type: "post",
        data: JSON.stringify(a),
        contentType: "application/json",
        dataType: "json",
        success: function (data) {
            loaded();
            if (data.code == "0") {
                $("#editModal").modal("hide");
                loadData(true);
            } else {
                alert(data.msg);
            }
        }
    });
}

function rollBack() {
    var envCode = $("#editEnvCode").val();
    var appName = $("#editAppName").val();
    var templateKey=$("#editTemplateKey").val();
    if (isNullObj(envCode)){
        alert("请选择环境");
        return false;
    }
    if (isNullObj(appName)){
        alert("请选应用");
        return false;
    }
    if (isNullObj(templateKey)){
        alert("请选择模板");
        return false;
    }
    var id=$("#releaseId").val();
    loading();
    $.ajax({
        url: "../config/rollBack",
        type: "post",
        data:{"envCode":envCode,"appName":appName,"templateKey":templateKey,"operationType":6,"id":id},
        dataType: "json",
        success: function (data) {
            loaded();
            if (data.code == "0") {
                $("#editModal").modal("hide");
                loadData(true);
            } else {
                alert(data.msg);
            }
        }
    });
}

function getJson(className) {
    var keyValues=$("."+className);
    if(keyValues==null || keyValues.length==0){
        alert("请关联模板属性后再添加");
        return false;
    }
    var json='{';
    var flag=false;
    for (var i=0;i<keyValues.length;i++){
        var keyValue=keyValues[i];
        var key=$(keyValue).attr("property-key");
        var value=$(keyValue).val();
        var type=$(keyValue).attr("property-type");
        var name=$(keyValue).attr("property-name");
        if (flag){
            json+=",";
        }


        if (type=='double' && !checkDouble(value)){
            alert("<"+name+"> 的值不属于"+type);
            return false;
        }
        if (type=='long' && !checkLong(value)){
            alert("<"+name+"> 的值不属于"+type);
            return false;
        }
        if(type=='prefix' ){
            json+='"'+key+'"';
            json+=':';
            json+="\""+value+"\"";
            flag=true;
        }else if(type=='string' || type=='date'){
            json+='"'+key+'"';
            json+=':';
            json+="\""+(value)+"\"";
            flag=true;
        }else if(type=='boolean' || type=='double' || type=='long'){
            json+='"'+key+'":'+value+'';
            flag=true;
        }else{
            flag=false;
        }

    }
    json+="}";
    return json;
}

function checkDouble(val){
    var str = val;
    if(!str.match(/^(:?(:?\d+.\d+)|(:?\d+))$/)){
        return false;
    }
    return true;
}
function checkLong(val) {
    var regu = /^(\-|\+)?\d+(\.\d+)?$/;
    if(!(regu.test(val))){
        return false;
    }
    return true;
}
