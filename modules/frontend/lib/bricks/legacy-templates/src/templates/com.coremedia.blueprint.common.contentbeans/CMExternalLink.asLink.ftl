<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMExternalLink" -->
<#-- @ftlvariable name="openInTab" type="java.lang.Boolean" -->
<#-- @ftlvariable name="cssClass" type="java.lang.String" -->

<#-- same as CMTeasable.asLink but with target=_blank as default -->
<#assign cssClass=cm.localParameters().cssClass!"" />
<#assign target=cm.localParameters().openInTab!true?then(' target="_blank"', "") />

<#if self.url?has_content>
  <a class="${cssClass!""}" href="${self.url}"<@cm.unescape target/><@cm.metadata data=[self.content, "properties.teaserTitle"] />>${self.teaserTitle!""}</a>
</#if>
