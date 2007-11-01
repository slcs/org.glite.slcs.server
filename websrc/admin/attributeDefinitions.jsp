<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>

<!-- $Id: attributeDefinitions.jsp,v 1.2 2007/11/01 14:38:53 vtschopp Exp $ -->

<h2>Attribute Definitions</h2>

<html:errors/>

<div id="attribute-definitions">
<p>
This is the list of the Shibboleth attribute definitions used by the SLCS server.
</p>
<table class="attributes-box">
	<tr>
		<th class="attributes-box-header">Display Name</th>
		<th class="attributes-box-header">Attribute Name</th>
		<th class="attributes-box-header">Case Sensitive Value?</th>
	</tr>
	<logic:iterate name="attributeDefinitions" property="attributeDefinitions" id="attributeDefinition">
		<tr>
			<td class="attribute-def-name"><bean:write name="attributeDefinition" property="displayName" /></td>
			<td class="attribute-def-value"><bean:write name="attributeDefinition" property="name" /></td>
			<td class="attribute-def-value"><bean:write name="attributeDefinition" property="caseSensitive" /></td>
		</tr>
	</logic:iterate>
</table>
<p>
Attribute value are used in access control rule. To match a rule the attribute value is compared case sensitive or unsensitive as defined by these definitions. This is typically the case with the <span class="attribute-name">Email</span> attribute: the rule based authorization decision will be evaluated case insensitively for the email address of the user (i.e. JohnDoe@example.org == johndoe@EXAMPLE.org)
</p>
</div>

