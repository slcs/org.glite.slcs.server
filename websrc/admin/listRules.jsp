<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>

<h2>SLCS Access Control Rules</h2>

<p>
Theses attributes-based access control rules protect the access to the SLCS service. 
Each rule contains a list of attributes which must match the user's attributes in order 
to grant him/her a SCLS certificate.
</p>

<!-- 
<p>
ACL File: <bean:write name="rulesBean" property="filename" />
</p>

<p>
<html:form action="/admin/createRule">
<html:submit property="org.glite.slcs.struts.action.CREATE_RULE">New Access Rule</html:submit>
</html:form>
</p>
-->

<p>
<logic:iterate name="rulesBean" property="accessControlRules" id="rule">
	<bean:define id="ruleId" name="rule" property="id"/>
	<table class="rule-box">
		<tr>
			<td>
				<span class="group-name">Group:</span> 
				<span class="group-value"><bean:write name="rule" property="groupName" /></span>
			</td>
			<td align="right">
				<html:form action="/admin/deleteRule"><html:submit property='<%="org.glite.slcs.struts.action.DELETE_RULE[" + ruleId + "]"%>'><bean:message key="button.rule.delete"/></html:submit></html:form>
			</td>
			<td align="right">
    	  		<html:form action="/admin/editRule"><html:submit property='<%="org.glite.slcs.struts.action.EDIT_RULE[" + ruleId + "]"%>'><bean:message key="button.rule.edit"/></html:submit></html:form>
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
</p>

<p>
<html:form action="/admin/createRule">
	<html:submit property="org.glite.slcs.struts.action.CREATE_RULE"><bean:message key="button.rule.new"/></html:submit>
	<logic:notEmpty name="rulesBean" property="groupName">
		<html:hidden name="rulesBean" property="groupName" />
	</logic:notEmpty>
</html:form>
</p>
