<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.Container" -->

<#if self.items?has_content>
<div class="cm-container cm-container--hero"<@cm.metadata data=[bp.getContainerMetadata(self),bp.getPlacementHighlightingMetaData(self)!""] />>
  <@cm.include self=self view="asCarousel" params={"modifier": "hero", "controlIcon": "triangle", "viewItems": "asHero", "displayPagination": true} />
</div>
</#if>
