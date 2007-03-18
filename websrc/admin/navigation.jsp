<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<table width="100%">
	<tr>
		<td><html:link action="/admin/home" styleClass="nav-item">Home</html:link>
		</td>
	</tr>
	<tr>
		<td><html:link action="/admin/listRules" styleClass="nav-item">Users ACL</html:link>
		</td>
	</tr>
	<tr>
		<td> <html:link action="/admin/listEvents" styleClass="nav-item">Audit Events</html:link>
		</td>
	</tr>
</table>
