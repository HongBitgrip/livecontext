<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.PageGridPlacement" -->

<#--
    Template Description:

    This template renders a placement and all its items or the detailview of a content in the main section.
    Please check the brick "livecontext" or "generic templates" for a more detailed version.
-->

<div id="cm-placement-${self.name!""}" class="cm-placement"<@cm.metadata data=[bp.getPlacementPropertyName(self)!"",bp.getPlacementHighlightingMetaData(self)!""]/>>

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
