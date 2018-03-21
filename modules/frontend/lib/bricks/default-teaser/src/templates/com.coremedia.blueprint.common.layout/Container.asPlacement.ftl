<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.Container" -->

<#list self.items![] as item>
  <@cm.include self=item view="asTeaser"/>
</#list>
