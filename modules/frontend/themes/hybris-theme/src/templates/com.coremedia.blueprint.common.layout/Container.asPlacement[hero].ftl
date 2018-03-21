<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.Container" -->

<#if self.items?has_content>
  <div class="cm-container cm-container--fullwidth" <@cm.metadata data=[bp.getContainerMetadata(self),bp.getPlacementHighlightingMetaData(self)!""] />>
    <@cm.include self=self view="asCarousel" params={"modifier": "hero", "viewItems": "asTeaserHero"} />
  </div>
</#if>
