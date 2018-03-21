<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.PageGrid" -->

<#if self?has_content>
  <div class="cm-grid ${(self.cssClassName)!""} container-fluid">
    <#list self.rows![] as row>
      <#assign cssClass=""/>
      <#if ((row.placements![])?size > 1)>
        <#assign cssClass="cm-multicolumn"/>
      </#if>
      <div class="cm-row row ${cssClass!""}">
        <#-- Iterate over each placement-->
        <#list row.placements![] as placement>
          <#if placement.items?has_content || (placement.name == "header") || (placement.name == "main")>
            <@cm.include self=placement />
          <#else>
            <div class="col-xs-12 col-md-${placement.colspan!1}" <@cm.metadata data=[bp.getPlacementPropertyName(placement)!"",bp.getPlacementHighlightingMetaData(placement)!""]/>></div>
          </#if>
        </#list>
      </div>
    </#list>
  </div>
</#if>
