<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.Container" -->
<#-- @ftlvariable name="index" type="java.lang.Integer" -->

<#assign index=cm.localParameters().index!0 />

<div class="cm-container cm-container--default"<@cm.metadata data=[bp.getContainerMetadata(self),bp.getPlacementHighlightingMetaData(self)!""] />>
  <#-- render the items as Teaser -->
  <#list self.items![] as item>
    <@cm.include self=item view="asTeaser" params={"index": index + item_index } />
  </#list>
</div>
