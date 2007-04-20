<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>

<h2>SLCS Administration</h2>

<html:errors/>

<div id="groups">
<p>This is the list of access control groups you can administer</p>
<table class="attributes-box">
	<tr>
		<th class="attributes-box-header" colspan="2">Access Control Groups</th>
	</tr>
	<logic:iterate name="userBean" property="groups" id="group">
		<bean:define id="groupName" name="group" property="name" />
		<tr>
			<td class="group-value" valign="top"><bean:write name="groupName" /></td>
			<td valign="top" align="right">
				<html:form action="/admin/listRules"><html:submit property='<%="org.glite.slcs.struts.action.LIST_RULES[" + groupName + "]"%>'><bean:message key="button.rule.list"/></html:submit></html:form>
			</td>
		</tr>
	</logic:iterate>
</table>
</div>

<div id="attributes">
<p>
The list of your AAI attributes
</p>
<table class="attributes-box">
	<logic:iterate name="userBean" property="attributes" id="attribute">
		<tr>
			<td class="attribute-name"><bean:write name="attribute" property="displayName" /></td>
			<td class="attribute-value"><bean:write name="attribute" property="value" /></td>
		</tr>
	</logic:iterate>
</table>
</div>

