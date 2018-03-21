<#-- @ftlvariable name="self" type="com.coremedia.livecontext.contentbeans.CMMarketingSpot" -->

<#if self.items?has_content>
<#-- there is at least 1 marketing spot, so zero does not need to be defined -->
  <#assign itemsPerRow=3 />
  <#switch self.items?size>
    <#case 1>
      <#assign itemsPerRow=1 />
      <#break>
    <#case 2>
      <#assign itemsPerRow=2 />
      <#break>
  </#switch>
  <@cm.include self=self view="asGrid" params={"itemsPerRow": itemsPerRow, "additionalClass": "cm-collection--marketingspot"} />
</#if>