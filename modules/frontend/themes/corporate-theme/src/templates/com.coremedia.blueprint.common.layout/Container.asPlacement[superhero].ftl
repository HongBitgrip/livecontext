<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.Container" -->

<#if self.items?has_content>
  <div class="cm-container cm-container--superhero"<@cm.metadata data=[bp.getContainerMetadata(self),bp.getPlacementHighlightingMetaData(self)!""] />>
  <@cm.include self=self view="asCarousel" params={"modifier": "superhero", "controlIcon": "triangle", "viewItems": "asSuperhero", "displayPagination": true} />
    <div class="cm-container__more">
      <i class="glyphicon glyphicon-chevron-down"></i>
    </div>
  </div>
</#if>