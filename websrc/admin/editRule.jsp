<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>

<!-- $Id: editRule.jsp,v 1.5 2007/10/23 14:08:32 vtschopp Exp $ -->

<h2>Edit Access Control Rule</h2>

<p>
If the group defines a rule constraint, the constrained attributes (marked with <font color="red">*</font>) 
can not be edited or deleted.
</p>

<html:errors/>

<html:form action="/admin/editRule">
	<html:hidden name="ruleBean" property="id" />
	<table class="rule-box">
		<tr>
			<td>
				<span class="group-name">Group:</span> 
				<span class="group-value"><bean:write name="ruleBean" property="groupName" /></span>
				<html:hidden name="ruleBean" property="groupName"/>
			</td>
		</tr>
		<tr>
			<td>
			<table class="attributes-box">
			<logic:iterate name="ruleBean" property="attributes" id="attribute" indexId="i">
				<tr>
				<logic:equal name="attribute" property="constrained" value="true">
					<td class="attribute-name">
						<bean:write name="attribute" property="displayName" />
						<html:hidden name="attribute"  property="name" indexed="true"/>
					</td>
					<td class="attribute-value">
						<bean:write name="attribute" property="value" />
						<html:hidden name="attribute" property="value" indexed="true"/>
					</td>
					<td><font color="red">*</font></td>
                </logic:equal>
				<logic:equal name="attribute" property="constrained" value="false">
					<td>
						<html:select name="attribute" property="name" indexed="true" styleClass="attribute-name-select">
							<html:optionsCollection name="ruleBean"	
								property="attributeDefinitions" value="name" label="displayName" />
						</html:select>
					</td>
					<td>
						<html:text name="attribute" property="value" indexed="true" styleClass="attribute-value-input"/> 
					</td>
					<td align="right">
						<html:submit property="org.glite.slcs.struts.action.DELETE_ATTRIBUTE" indexed="true"><bean:message key="button.rule.attribute.delete"/></html:submit>
					</td>
                </logic:equal>
				</tr>
			</logic:iterate>
				<tr>
					<td align="right" colspan="3">
						<html:submit property="org.glite.slcs.struts.action.ADD_ATTRIBUTE"><bean:message key="button.rule.attribute.add"/></html:submit>
					</td>
				</tr>
			</table>
			</td>
		</tr>
		<tr>
			<td align="right">
				<html:cancel><bean:message key="button.rule.cancel"/></html:cancel>
				<bean:define id="ruleId" name="ruleBean" property="id"/>
				<html:submit property='<%="org.glite.slcs.struts.action.SAVE_RULE[" + ruleId + "]"%>'><bean:message key="button.rule.save"/></html:submit>
			</td>
		</tr>
	</table>
</html:form>

<p>
<small>
<ul>
<li>The attribute value must match the value of the user's attribute. Check the <html:link action="/admin/attributeDefinitions" styleClass="button"><bean:message key="navigation.attributeDefinitions"/></html:link> to determine if an attribute value is case sensitive or not.</li>
<li>You can ask users to dump their attributes using the <a href="https://aai-viewer.switch.ch/aai" target="aai-viewer">AAI Attributes Viewer</a> to determine the exact attribute value.</li>
<li>Only attribute with a non empty value will be stored when you click on <span class="button"><bean:message key="button.rule.save"/></span></li>
</small>
</p>
