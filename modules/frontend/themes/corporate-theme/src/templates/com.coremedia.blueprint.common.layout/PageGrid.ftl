<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.PageGrid" -->

<#if self?has_content>
  <#assign gridModifier="" />
  <#assign superheroPlacement=bp.getPlacementByName("hero", self)!cm.UNDEFINED />
  <#if ((superheroPlacement.viewTypeName)?has_content && superheroPlacement.viewTypeName == "superhero" && superheroPlacement.getItems()?size > 0)>
    <#assign gridModifier="cm-grid--with-superhero" />
  </#if>
  <div class="cm-grid ${(self.cssClassName)!""} ${gridModifier} container-fluid">
    <#list self.rows![] as row>
      <#assign cssClass=""/>
      <#if ((row.placements![])?size > 1)>
        <#assign cssClass="cm-multicolumn"/>
      </#if>
      <div class="cm-row row ${cssClass!""}">
      <#-- Iterate over each placement-->
        <#list row.placements![] as placement>
          <#if placement.items?has_content || (placement.name == "main")>
            <@cm.include self=placement />
          <#else>
            <div class="col-xs-12 col-md-${placement.colspan!1}" <@cm.metadata data=[bp.getPlacementPropertyName(placement)!"",bp.getPlacementHighlightingMetaData(placement)!""]/>></div>
          </#if>
        </#list>
      </div>
    </#list>
  </div>
</#if>
