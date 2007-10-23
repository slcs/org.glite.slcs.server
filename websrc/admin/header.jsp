<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>

<!-- $Id: header.jsp,v 1.2 2007/10/23 14:08:32 vtschopp Exp $ -->

<table width="100%" border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td class="logo-width"><html:img page="/images/head-SWITCH-logo.gif"
			alt="SWITCH Logo" border="0" /></td>
		<td width="100%" align="right" valign="bottom">
		<span class="blue"><b>Short Lived Credential Service</b></span><br>
		<small>Version: <bean:write name="serverVersion" /></small><br>
		<small>A Service of <a href="http://www.switch.ch/grid/" target="switch">SWITCH</a> - &copy; 2007 Members of the <a href="http://www.eu-egee.org" target="egee">EGEE</a> Collaboration</small>
		</td>
	</tr>
	<tr>
		<td colspan="2"><html:img page="/images/head-stripes.gif"
			border="0" width="100%" height="10" /></td>
	</tr>
	<tr>
		<td colspan="2"><html:img page="/images/head-gradient.jpg"
			border="0" width="100%" height="10" /></td>
	</tr>
</table>

