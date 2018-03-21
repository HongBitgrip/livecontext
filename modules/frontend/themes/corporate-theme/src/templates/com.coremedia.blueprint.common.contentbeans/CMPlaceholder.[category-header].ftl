<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMPlaceholder" -->
<#-- @ftlvariable name="cmpage.content" type="com.coremedia.blueprint.common.contentbeans.CMChannel" -->

<#-- load page/category as mini gap (only on categories/channels) -->
<#if (cmpage.content.header)?has_content>
<div class="cm-placeholder"<@cm.metadata self.content />>
  <@cm.include self=cmpage.content view="asMiniGap" />
</div>
</#if>
