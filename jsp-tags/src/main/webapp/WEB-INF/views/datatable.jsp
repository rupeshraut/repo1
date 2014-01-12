<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib prefix="jtag" uri="http://jtags.org/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>

	<jtag:datatable items="items" name="list" style="width:300px">
		<jtag:paginator rowsPerPage="10" location="header,footer" style="width:300px"/>
		<jtag:column property="name" title="Item Name" sortable="true" style="font-weight:normal"/>
		<jtag:column property="price" title="Pricie" style="font-weight:normal"/>
		<jtag:column property="" title="Action" style="font-weight:normal"><a href="javascript:void(0)">Edit</a></jtag:column>
	</jtag:datatable>

</body>
</html>