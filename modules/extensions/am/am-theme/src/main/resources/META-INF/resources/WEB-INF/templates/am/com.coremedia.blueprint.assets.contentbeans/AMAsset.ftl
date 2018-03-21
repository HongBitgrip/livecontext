<#-- @ftlvariable name="self" type="com.coremedia.blueprint.assets.contentbeans.AMAsset" -->

<#list self.renditions as rendition>
  <#if rendition.blob?has_content>
    <#assign name=rendition.name!"" />
    <#if name?has_content && name == "web">
      <#assign viewName="[" + name + "]" />
      <@cm.include self=rendition view=viewName />
    </#if>
  </#if>
</#list>

