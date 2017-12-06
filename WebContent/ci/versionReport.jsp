<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="ibs.common.ci.VersionReport"%>
<%@page import="ibs.common.ci.ArtifactBean"%>
<%@page import="java.util.Iterator"%>

<%!
private String nvl(String value) {
	String result;
	if(null == value || 0 == value.length()) {
		result = "Unknown";
	} else {
		result = value;
	}
	return result;
}
%>

<style>
<!--
@CHARSET "UTF-8" ;

/* Published Data List portlet*/
TABLE.published_data_table {
	width: 100%;
	border-top:1px solid #FFFFFF;
	border-left:1px solid #FFFFFF;
}

TABLE.published_data_table tr th {
	border-right:1px solid #FFFFFF;
	border-bottom:1px solid #FFFFFF;
	font-weight: normal;
	font-size: 11px;
	color: #FFFFFF;
	background: #527AAC;
	padding: 3px;
}

TABLE.published_data_table tr td {
	border-right:1px solid #FFFFFF;
	border-bottom:1px solid #FFFFFF;
	padding: 3px;
	background: #FFFFFF
}

TABLE.published_data_table tr.select td {
	border-right:1px solid #FFFFFF;
	border-bottom:1px solid #FFFFFF;
	padding: 3px;
	background: #CFD9E5;
}
-->
</style>

<h1>Version report</h1>

<table width="50%" class="published_data_table">
<%
Iterator artifacts = new VersionReport().getArtifacts().iterator();
for(int index = 0; artifacts.hasNext(); index++) {
	ArtifactBean artifact = (ArtifactBean)artifacts.next();%>
	<tr<%=index%2 ==0?" class=select":""%>>
	<td>
		<ul>
			<li>Name: <%=nvl(artifact.getName())%></li>
			<li>Version: <%=nvl(artifact.getVersion())%></li>
			<li>Build-tag: <%=nvl(artifact.getTag())%></li>
			<li>Build-date: <%=nvl(artifact.getBuildTime())%></li>
		</ul>
	</td>
</tr>
<%}%>
</table>