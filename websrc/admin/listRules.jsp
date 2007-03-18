<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>

<h2>User Access Control Rules</h2>

<p>
ACL File: <bean:write name="rulesBean" property="filename" />

<p>
<logic:iterate name="rulesBean" property="accessControlRules" id="rule">
<bean:define id="ruleId" name="rule" property="id"/>
	<table class="rule-box">
		<tr>
			<td><span class="group-name">ACL Group:</span> <span class="group-value"><bean:write name="rule" property="group" /></span>
			</td>
			<td align="right">
				<html:form action="/admin/deleteRule"><html:submit property='<%="org.glite.slcs.struts.action.DELETE_RULE[" + ruleId + "]"%>'>Delete</html:submit></html:form>
			</td>
			<td align="right">
    	  		<html:form action="/admin/editRule"><html:submit property='<%="org.glite.slcs.struts.action.EDIT_RULE[" + ruleId + "]"%>'>Edit</html:submit></html:form>
			</td>
		</tr>
		<tr>
			<td colspan="3">
				<table class="attributes-box">
				<logic:iterate name="rule" property="attributes" id="attribute">
				<tr>
      				<td class="attribute-name"><bean:write name="attribute" property="displayName" /></td>
     				<td class="attribute-value"><bean:write name="attribute" property="value" /></td>
				</tr>
				</logic:iterate>
				</table>
			</td>
		</tr>
	</table>
</logic:iterate>

<html:form action="/admin/createRule">
<html:submit property="org.glite.slcs.struts.action.CREATE_RULE">New Rule</html:submit>
</html:form>
