<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.Container" -->

<#assign items=self.flattenedItems![] />
<#assign numberOfItems=items?size />
<#assign itemsPerRow=3 />

<div class="cm-container cm-container--text"<@cm.metadata data=[bp.getContainerMetadata(self),bp.getPlacementHighlightingMetaData(self)!""] />>
<#if (numberOfItems > 0)>
    <div class="cm-row--full-height row">
      <#list items as item>
      <#-- add new row -->
        <@bp.renderNewRow item_index itemsPerRow "cm-row--full-height " />
      <#-- render the items as claim teaser -->
        <#assign offsetClass=bp.getOffsetClass(item_index, numberOfItems, itemsPerRow, " col-sm-") />
          <div class="cm-col--full-height col-xs-12 col-sm-4${offsetClass}">
            <@cm.include self=item view="asText" params={"islast": item?is_last} />
          </div>
      </#list>
    </div>
</#if>
</div>
