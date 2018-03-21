<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->
<#-- @ftlvariable name="openInTab" type="java.lang.Boolean" -->
<#-- @ftlvariable name="cssClass" type="java.lang.String" -->
<#-- @ftlvariable name="attr" type="java.lang.String" -->
<#-- @ftlvariable name="link" type="java.lang.String" -->

<#assign cssClass=cm.localParameters().cssClass!"" />
<#assign attr=cm.localParameters().attr!"" />
<#assign link=cm.localParameters().link!cm.getLink(self.target!cm.UNDEFINED) />
<#assign target=cm.localParameters().openInTab!false?then(' target="_blank"', "") />

<a class="${cssClass}" href="${link}" <@cm.unescape target/> <@cm.unescape attr /> <@cm.metadata data=[self.content, "properties.teaserTitle"] />>${self.teaserTitle!""}</a>
