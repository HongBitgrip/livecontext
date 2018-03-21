<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.Container" -->
<#-- @ftlvariable name="claimModifier" type="java.lang.String" -->
<#-- @ftlvariable name="isInSidebar" type="java.lang.Boolean" -->

<#assign claimModifier=cm.localParameter("claimModifier", "") />
<#assign isInSidebar=cm.localParameter("isInSidebar", false) />
<#assign items=self.flattenedItems![] />
<#assign numberOfItems=items?size />
<#assign itemsPerRow=3 />

<div class="cm-container cm-container--claim" <@cm.metadata data=[bp.getContainerMetadata(self),bp.getPlacementHighlightingMetaData(self)!""] />>
  <#if (numberOfItems > 0)>
    <div class="row">
      <#list items as item>
        <#-- add new row -->
        <@bp.renderNewRow item_index itemsPerRow />
        <#-- render the items as claim teaser -->
        <#assign offsetClassTablet=bp.getOffsetClass(item_index, numberOfItems, itemsPerRow, " col-sm-") />
        <#assign columnClasses="col-xs-12 col-sm-4 ${offsetClassTablet}" />
        <#if isInSidebar>
          <#assign columnClasses=columnClasses + " col-md-12 col-md-offset-0" />
        </#if>
        <div class="${columnClasses}">
          <@cm.include self=item view="asClaim" params={"cssClass": claimModifier, "islast": item?is_last} />
        </div>
      </#list>
    </div>
  </#if>
</div>
