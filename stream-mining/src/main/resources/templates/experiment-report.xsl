<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns="http://www.w3.org/TR/REC-html40">

<xsl:output method="html"/>

<xsl:template match="/experiment">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
		<link rel="stylesheet" type="text/css" href="style.css"></link>
		<title></title>
	</head>
   <body style="text-align: center;">
      <xsl:apply-templates/>
   </body>
</html>
</xsl:template>

<xsl:template match="experiment/title">
	<h1>
		<xsl:apply-templates />
	</h1>
</xsl:template>

<xsl:template match="experiment/description">
	<p>
		<xsl:value-of select="text()" />
	</p>
</xsl:template>

<xsl:template match="settings">
<div class="section">
	<h3>Experiment global Settings</h3>

	<table class="learner" align="center">
		<xsl:for-each select="property">
		<tr class="baseline">
	   		<td class="parameter">
				<span class="key"><xsl:value-of select="@name" /></span> 
			</td>
			<td class="parameter">
			 	<span class="value"><xsl:value-of select="@value" /></span>
		   </td>
		</tr>
		</xsl:for-each>
	</table>    
</div>
</xsl:template>


<xsl:template match="para">
	<p>
		<xsl:value-of select="text()" />
	</p>
</xsl:template>

<xsl:template match="section">
	<div class="section">
		<xsl:apply-templates />	
	</div>
</xsl:template>


<xsl:template match="title">
	<h3><xsl:value-of select="text()" /></h3>
</xsl:template>

<xsl:template match="TestAndTrain">
<div class="section">
	<h3>Evaluation (Test-and-Train)</h3>
	
	<table class="learner" align="center">
	<tr class="baseline">
		<td class="name">
			<xsl:value-of select="Baseline/@class" />
			(Baseline)
		</td>
	   <td class="parameter">
 				<span class="key">class</span> = <span class="value"><xsl:value-of select="@class" /></span> <br/>
		<xsl:for-each select="Baseline/@*">
			<xsl:if test="name() != 'class'" >
				<xsl:if test="name() != 'name'">
					<span class="key"><xsl:value-of select="name()" /></span> = <span class="value"><xsl:value-of select="." /></span> <br/>
				</xsl:if>
			</xsl:if>
		</xsl:for-each>
	   </td>
	</tr>
	
	<xsl:for-each select="Learner">
	<tr>
	   <td class="name">
		  <xsl:value-of select="@name"/>
	   </td>
	   <td class="parameter">
 				<span class="key">class</span> = <span class="value"><xsl:value-of select="@class" /></span> <br/>
		<xsl:for-each select="@*">
			<xsl:if test="name() != 'class'" >
				<xsl:if test="name() != 'name'">
				<div class="parameter">
					<span class="key"><xsl:value-of select="name()" /></span> = <span class="value"><xsl:value-of select="." /></span>
				</div>
				</xsl:if>
			</xsl:if>
		</xsl:for-each>
	   </td>
	</tr>
	</xsl:for-each>
	</table>
	
<!-- 
	<xsl:for-each select="Plot">
		<div class="section">
			<xsl:variable name="file"><xsl:value-of select="name"/></xsl:variable>
			<img src="${file}" border="0" align="center" />		
		</div>
	</xsl:for-each>
 -->	
	
	<div class="section">
		<h3>Memory Usage</h3>
		<img src="memory.png" border="0" align="center"/>
	</div>
	
	<div class="section">
		<h3>Model Error</h3>
		<img src="model-error.png" border="0" align="center"/>
	</div>
	
	CONFUSION_MATRIX
	
	<!-- 
	<div class="section">
		<img src="vm-memory.log.png" border="0" align="center"/>
	</div>
	 -->
</div>
</xsl:template>


<xsl:template match="Learner">
	<p>
		<b><xsl:value-of select="@name" /></b>
	</p>
</xsl:template>

<xsl:template match="stream|Stream">
<div class="section dataStream">
    <h3>Data Stream</h3>
	<table class="learner" align="center">
		<xsl:for-each select="@*">
		<tr class="baseline">
	   		<td class="parameter">
				<span class="key"><xsl:value-of select="name()" /></span> 
			</td>
			<td class="parameter">
			 	<span class="value"><xsl:value-of select="." /></span>
		   </td>
		</tr>
		</xsl:for-each>
	</table>    
    
    <xsl:apply-templates />

	<xsl:if test="text() != ''">
	<p class="description">
		<xsl:value-of select="text()" />
	</p>
	</xsl:if>    
	
	STREAM_SUMMARY
	
	<!-- 
	<img src="distribution.png" border="0" align="center" />
	<img src="nominal-distribution.png" border="0" align="center" />
	 -->
</div>
</xsl:template>


<xsl:template match="processing">
<div class="section">
	<table>
		<tr>
		<td>
			<xsl:for-each select="processor">
				<xsl:apply-templates select="." />
			</xsl:for-each>		
		</td>
		</tr>
	</table>
</div>
</xsl:template>


<xsl:template match="processor">
<div class="processor">
	<div class="parameter value name"><xsl:value-of select="@class" /></div>
	<div class="parameters">
	<xsl:for-each select="@*">
		<xsl:if test="name() != 'class'" >
			<xsl:if test="name() != 'name'">
			<div class="parameter">
				<span class="key"><xsl:value-of select="name()" /></span> = <span class="value"><xsl:value-of select="." /></span>
			</div>
			</xsl:if>
		</xsl:if>
	</xsl:for-each>
	</div>
</div>
</xsl:template>


<xsl:template match="description">
	<div class="description">
		<xsl:apply-templates />
	</div>
</xsl:template>

<xsl:template match="plot|Plot">
	<div class="section">
		<h3><xsl:value-of select="@title" /></h3>
		
		<xsl:variable name="file"><xsl:value-of select="@name" /></xsl:variable>
		<img src="${file}" border="0" align="center" />
	</div>
</xsl:template>


<xsl:template match="p">
	<p>
		<xsl:value-of select="text()" />
	</p>
</xsl:template>


<xsl:template match="*">
	<xsl:value-of select="text()" />
</xsl:template>

</xsl:stylesheet>