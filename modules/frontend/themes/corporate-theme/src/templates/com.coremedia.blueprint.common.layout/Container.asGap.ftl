<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.Container" -->

<#if (self.items?has_content)>
  <div class="cm-container cm-container--gap"<@cm.metadata data=[bp.getContainerMetadata(self),bp.getPlacementHighlightingMetaData(self)!""] />>
    <#-- render the first item as gap -->
    <@cm.include self=self.flattenedItems?first view="asGap" />
  </div>
</#if>
