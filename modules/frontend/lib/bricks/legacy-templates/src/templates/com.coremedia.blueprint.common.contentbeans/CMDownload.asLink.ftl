<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMDownload" -->
<#-- @ftlvariable name="openInTab" type="java.lang.Boolean" -->
<#-- @ftlvariable name="cssClass" type="java.lang.String" -->

<#-- same as CMTeasable.asLink but with target=_blank, a link to the blob data and type and size information -->
<#assign cssClasses=cm.localParameter("cssClass")!"" />
<#assign target=cm.localParameters().openInTab!true?then(' target="_blank"', "") />

<#if self.data?has_content>
  <a class="${cssClasses}" href="${cm.getLink(self.target)}"<@cm.unescape target/> title="${self.teaserTitle!''}"<@cm.metadata data=[self.content, "properties.teaserTitle"] />>
    ${self.teaserTitle!""}
    <@cm.include self=self view="infos" />
  </a>
</#if>
