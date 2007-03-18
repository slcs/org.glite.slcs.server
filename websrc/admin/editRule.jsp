<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>

<h2>Edit Access Control Rule</h2>

<html:form action="/admin/editRule">
	<html:hidden name="ruleBean" property="id" />
	<table class="rule-box">
		<tr>
			<td><span class="group-name">ACL Group:</span> <html:select name="ruleBean" property="group" styleClass="group-value">
			<html:options name="ruleBean" property="userGroups" />
			</html:select></td>
		</tr>
		<tr>
			<td>
			<table class="attributes-box">
				<logic:iterate name="ruleBean" property="attributes" id="attribute" indexId="i">
					<tr>
						<td>
							<html:select name="attribute" property="name" indexed="true" styleClass="attribute-name-select">
							<html:optionsCollection name="ruleBean"	property="attributeDefinitions" value="name" label="displayName" />
							</html:select>
						</td>
						<td>
							<html:text name="attribute" property="value" indexed="true" styleClass="attribute-value-input"/> 
							<html:submit property="org.glite.slcs.struts.action.DELETE_ATTRIBUTE" indexed="true">-</html:submit>
							<html:submit property="org.glite.slcs.struts.action.ADD_ATTRIBUTE">+</html:submit>
						</td>
					</tr>
				</logic:iterate>
			</table>
			</td>
		</tr>
		<tr>
			<td align="right">
				<html:cancel>Cancel</html:cancel>
				<bean:define id="ruleId" name="ruleBean" property="id"/>
				<html:submit property='<%="org.glite.slcs.struts.action.SAVE_RULE[" + ruleId + "]"%>'>Save Rule</html:submit>
			</td>
		</tr>
	</table>
</html:form>
