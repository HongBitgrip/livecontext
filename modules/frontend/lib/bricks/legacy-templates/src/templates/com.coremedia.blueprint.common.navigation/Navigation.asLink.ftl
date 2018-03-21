<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.navigation.Navigation" -->
<#-- @ftlvariable name="openInTab" type="java.lang.Boolean" -->
<#-- @ftlvariable name="cssClass" type="java.lang.String" -->
<#-- @ftlvariable name="attr" type="java.lang.String" -->
<#-- @ftlvariable name="link" type="java.lang.String" -->

<#assign cssClass=cm.localParameters().cssClass!"" />
<#assign attr=cm.localParameters().attr!"" />
<#assign link=cm.localParameters().link!cm.getLink(self) />
<#assign target=cm.localParameters().openInTab!false?then(' target="_blank"', "") />

<a class="${cssClass}" href="${link}"${target?no_esc} ${attr?no_esc}>${self.title!""}</a>
