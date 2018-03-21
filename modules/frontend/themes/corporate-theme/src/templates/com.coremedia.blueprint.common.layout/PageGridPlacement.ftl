<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.PageGridPlacement" -->

<#assign placementName=self.name!"" />
<#assign isInSidebar = (placementName == "sidebar") />
<#assign cssClasses = isInSidebar?then("cm-sticky", "") />

<#-- special placement: header -->
<#if placementName == "header">
  <@cm.include self=self view="asHeader" />

<#--  special placement: footer -->
<#elseif placementName == "footer">
  <@cm.include self=self view="asFooter" />

<#-- show placement if detailview or not empty -->
<#elseif (self.items?size > 0 || cmpage.detailView)>
  <div class="${cssClasses} col-xs-12 col-md-${self.colspan!1}" <@cm.metadata data=[bp.getPlacementPropertyName(self)!"",bp.getPlacementHighlightingMetaData(self)!""]/>>
    <#-- special placement: main if in detailview -->
    <#if placementName == "main" && cmpage.detailView>
      <#-- replace main placement with the single content and display it in detail view-->
      <@cm.include self=bp.getContainerFromBase(self, [cmpage.content]) view="asDetails" />

    <#else>
      <#-- all others -->
      <#-- check setting (linklist) on page, if this placement should display the first element as header (gap) -->
      <#assign stringlist=bp.setting(cmpage, "placementsWithFirstItemAsHeader", []) />
      <#assign withGap=stringlist?seq_contains(placementName) />

      <#-- display first element as Gap -->
      <#assign items=self.items![] />
      <#if withGap && items?has_content>
        <@cm.include self=bp.getContainerFromBase(self, [items?first]) view="asGap" />
        <@cm.include self=bp.getContainerFromBase(self, items[1..]) view="asPlacement" params={"isInSidebar": isInSidebar} />
      <#else>
        <@cm.include self=self view="asPlacement" params={"isInSidebar": isInSidebar} />
      </#if>
    </#if>
  </div>
</#if>
