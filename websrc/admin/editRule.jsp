<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<h2>Edit Access Control Rule</h2>

<p>
If the group defines a rule constraint, the constrained attributes (marked with <font color="red">*</font>) 
can not be deleted.
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
				<c:choose>
				<c:when test="${attribute.required}">
					<td class="attribute-name">
						<bean:write name="attribute" property="displayName" />
						<html:hidden name="attribute"  property="name" indexed="true"/>
					</td>
					<td class="attribute-value">
						<bean:write name="attribute" property="value" />
						<html:hidden name="attribute" property="value" indexed="true"/>
					</td>
					<td><font color="red">*</font></td>
				</c:when>
				<c:otherwise>					
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
				</c:otherwise>
				</c:choose>
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
<small>Only attribute with a non empty value will be stored when you click on <span class="button"><bean:message key="button.rule.save"/></span></small>
</p>