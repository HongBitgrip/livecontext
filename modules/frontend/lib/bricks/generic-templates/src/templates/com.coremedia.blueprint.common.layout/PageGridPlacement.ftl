<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.PageGridPlacement" -->

<div id="cm-placement-${self.name!""}" class="cm-placement cm-placement-${self.name!""}"<@cm.metadata data=[bp.getPlacementPropertyName(self)!"",bp.getPlacementHighlightingMetaData(self)!""]/>>

  <#-- replace main section with the content in detailView -->
  <#if self.name! == "main" && cmpage.detailView>
    <@cm.include self=cmpage.content/>

  <#-- render the placement items -->
  <#else>
    <#list self.items![] as item>
      <@cm.include self=item />
    </#list>
  </#if>
</div>
