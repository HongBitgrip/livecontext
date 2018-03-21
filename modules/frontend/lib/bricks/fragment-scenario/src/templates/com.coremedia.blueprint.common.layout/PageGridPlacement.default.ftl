<@cm.responseHeader name="Content-Type" value="text/html; charset=UTF-8"/>
<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.PageGridPlacement" -->

<!-- CoreMedia Placement ${self.name!""} -->
<div class="cm-placement-${self.name!""}" <@cm.metadata data=[bp.getPlacementPropertyName(self)!"", lc.fragmentHighlightingMetaData(self)!""] />>
  <@cm.include self=self params={"renderDiv": false}/>
</div>
