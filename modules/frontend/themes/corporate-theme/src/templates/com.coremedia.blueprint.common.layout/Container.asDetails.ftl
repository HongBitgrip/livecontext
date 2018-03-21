<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.Container" -->

<#assign items=self.items![] />
<#assign numberOfItems=items?size />

<main id="cm-main" class="cm-container cm-container--details"<@cm.metadata data=[bp.getContainerMetadata(self), bp.getPlacementHighlightingMetaData(self)!""] />>
  <#list items as item>
    <@cm.include self=item />
  </#list>
</main>
