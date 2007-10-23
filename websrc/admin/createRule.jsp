<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>

<!-- $Id: createRule.jsp,v 1.5 2007/10/23 14:08:05 vtschopp Exp $ -->

<h2>New Access Control Rule</h2>

<p>
You can create a new attributes-based access control rule for the group(s) you belong to. 
</p>

<p>
If the chosen group defines a rule constraint, all the constrained attributes (marked with <font color="red">*</font>)
 will be automatically added and can not be deleted.
</p>

<html:errors />

<html:form action="/admin/createRule">
	<table class="rule-box">
		<tr>
			<td>
				<span class="group-name">Group:</span> 
				<html:select name="ruleBean" property="groupName" styleClass="group-value">
					<html:options name="ruleBean" property="userGroupNames" />
				</html:select>
			</td>
			<td align="right">
				<html:submit property="org.glite.slcs.struts.action.CHANGE_GROUP"><bean:message key="button.rule.setgroup"/></html:submit>
			</td>
		</tr>
		<tr>
			<td colspan="2">
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
						<html:text name="attribute" property="value" indexed="true" styleClass="attribute-value-input" /> 
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
			<td align="right" colspan="2">
				<html:cancel><bean:message key="button.rule.cancel"/></html:cancel> 
				<html:submit property="org.glite.slcs.struts.action.SAVE_RULE"><bean:message key="button.rule.create"/></html:submit>
			</td>
		</tr>
	</table>
</html:form>

<p>
<small>
<ul>
<li>First select the group for the rule and press <span class="button"><bean:message key="button.rule.setgroup"/></span></li>
<li>Then add/edit/remove the attributes required to identify the user. The attributes <span class="attribute-name">AAI UniqueID</span> or <span class="attribute-name">Email</span> are recommended for this purpose.</li>
<li>The attribute value must match the value of the user's attribute. Check the <html:link action="/admin/attributeDefinitions" styleClass="button"><bean:message key="navigation.attributeDefinitions"/></html:link> to determine if an attribute value is case sensitive or not.</li>
<li>You can ask users to dump their attributes using the <a href="https://aai-viewer.switch.ch/aai" target="aai-viewer">AAI Attributes Viewer</a> to determine the exact attribute value.</li>
<li>When you are finished press on <span class="button"><bean:message key="button.rule.create"/></span> to store the rule.</li>
</ul>
</small>
</p>
