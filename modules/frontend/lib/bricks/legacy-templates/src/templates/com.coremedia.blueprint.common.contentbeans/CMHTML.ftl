<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMHTML" -->

<#if preview.isPreviewCae()><span<@preview.metadata self.content/>></#if>
  <#if self.data?has_content>
    <@cm.include self=self.data view="script"/>
  </#if>
<#if preview.isPreviewCae()></span></#if>
