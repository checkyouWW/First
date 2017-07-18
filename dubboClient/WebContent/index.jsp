<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd"> 
<html> 
<head> 
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"> 
<title>Spring MVC</title> 
<script src="jquery.min.js"></script> 
<script> 
$(function(){ 
    $("#btnGet").click(function(){ 
        $.ajax({ 
            type: 'GET', 
            url : 'lifeWeb',   //通过url传递name参数
            dataType : 'json', 
            data: {title: "Mr"},   //通过data传递title参数
            success : function(data) { 
                alert(data.name);   
            }, 
            error : function(data) {   
                alert(data.responseText); 
            }   
        });  
    }); 
}); 
</script> 
</head> 
<body> 
<!-- 显示model中的hello字符串和client对象的name -->
${hello} 
${client.name} 
<br/> 
<input id="btnGet" type="button" value="get client" /> 
</body> 
</html> 