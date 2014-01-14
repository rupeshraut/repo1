<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib prefix="jtag" uri="http://jtags.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<style type="text/css">
a:link {text-decoration:none;}
a:visited {text-decoration:none;}
a:hover {text-decoration:underline;font-weight: }
a:active {text-decoration:underline;}
a:link {background-color:#FFFF85;}
a:visited {background-color:#FFFF85;}
a:hover {background-color:#FF704D;}
a:active {background-color:#FFFF85;} 
</style>
</head>
<body>

	<jtag:datatable items="items" name="list" style="width:600px;font-size:10pt;font-family:verdana" rowColors="#FFFFFF,#CCCCFF">
		<jtag:paginator rowsPerPage="20" location="header,footer" style="width:600px;background-color:#EEEEEE;font-size:10pt;font-family:verdana;font-weight:normal;text-align:left;padding-left:2px;padding-top:5px;padding-bottom:5px;border:1px dashed #000000"/>
		<jtag:column property="name" title="Item Name" sortable="false" style="font-weight:bold;background-color:#336699;color:#ffffff"/>
		<jtag:column property="price" title="Pricie" style="font-weight:bold;background-color:#336699;color:#ffffff"/>
		<jtag:column property="" title="Action" style="font-weight:bold;background-color:#336699;color:#ffffff"><a href="javascript:void(0)">Edit</a></jtag:column>
	</jtag:datatable>

</body>
</html>