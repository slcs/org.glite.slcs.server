<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>

<!-- $Id: navigation.jsp,v 1.3 2007/10/23 14:08:05 vtschopp Exp $ -->

<table width="100%">
	<tr>
		<td>
			<html:link action="/admin/home" styleClass="nav-item"><bean:message key="navigation.home"/></html:link>
		</td>
	</tr>
	<tr>
		<td>
			<html:link action="/admin/listRules" styleClass="nav-item"><bean:message key="navigation.rule.list"/></html:link>
		</td>
	</tr>
	<tr>
		<td>
			<html:link action="/admin/createRule" styleClass="nav-item"><bean:message key="navigation.rule.new"/></html:link>
		</td>
	</tr>
<!-- 
	<tr>
		<td>
			<html:link action="/admin/listEvents" styleClass="nav-item"><bean:message key="navigation.audit.list"/></html:link>
		</td>
	</tr>
-->
	<tr>
		<td>
			<html:link action="/admin/attributeDefinitions" styleClass="nav-item"><bean:message key="navigation.attributeDefinitions"/></html:link>
		</td>
	</tr>
	<tr>
		<td>
			<html:link href="http://www.switch.ch/grid/slcs/documents/webadmin/index.html" styleClass="nav-item" target="webadmin"><bean:message key="navigation.admin.guide"/></html:link>
		</td>
	</tr>
</table>
