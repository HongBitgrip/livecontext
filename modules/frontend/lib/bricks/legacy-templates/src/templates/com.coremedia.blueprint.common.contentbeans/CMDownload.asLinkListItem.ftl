<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMDownload" -->
<#-- @ftlvariable name="classItem" type="java.lang.String" -->

<#if self.data?has_content>
  <li class="${classItem!""}"<@cm.metadata self.content />><@cm.include self=self view="asLink" /></li>
</#if>