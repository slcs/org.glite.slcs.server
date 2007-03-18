<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page language="java"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>

<html:html>
<head>
<html:base />
<title><tiles:getAsString name="title" /></title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<link rel="stylesheet" type="text/css" href="../css/switch.css" />
</head>
<body>
<!-- layout table start -->
<table width="100%">
<tr>
	<td colspan="2">
	<!-- header:start -->
	<tiles:insert attribute="header" />
	<!-- header:end -->
	</td>
</tr>
<tr>
	<td class="logo-width" valign="top">
	<!-- navbar:start -->
	<tiles:insert attribute="navbar" />
	<!-- navbar:end -->
	</td>
	<td class="body-width">
	<!-- body:start -->
	<tiles:insert attribute="body" />
	<!-- body:end -->
	</td>
</tr>
<tr>
	<td colspan="2">
	<!-- footer:start -->
	<tiles:insert attribute="footer" />
	<!-- footer:end -->
	</td>
</tr>
</table>
<!-- layout table end -->
</body>
</html:html>
