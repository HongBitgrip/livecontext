<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMPicture" -->
<#-- @ftlvariable name="att_class" type="java.lang.String" -->

<#assign additionalCssClasses=att_class!""/>

<div class="cm-teasable--embedded ${additionalCssClasses}">
  <@cm.include self=self view="asTeaser" />
</div>
