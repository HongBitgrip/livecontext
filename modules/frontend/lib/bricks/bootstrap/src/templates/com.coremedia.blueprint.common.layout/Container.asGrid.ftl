<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.Container" -->
<#-- @ftlvariable name="viewItems" type="java.lang.String" -->
<#-- @ftlvariable name="itemsPerRow" type="java.lang.Integer" -->
<#-- @ftlvariable name="itemsPerMobileRow" type="java.lang.Integer" -->
<#-- @ftlvariable name="additionalClass" type="java.lang.String" -->
<#-- @ftlvariable name="viewItemCssClass" type="java.lang.String" -->
<#-- @ftlvariable name="columnCssClass" type="java.lang.String" -->
<#-- @ftlvariable name="center" type="java.lang.Boolean" -->
<#-- @ftlvariable name="addRows" type="java.lang.Boolean" -->
<#-- @ftlvariable name="showHeadline" type="java.lang.Boolean" -->

<#assign viewItems=cm.localParameter("viewItems", "asTeaser") />
<#assign items=self.flattenedItems![] />
<#assign numberOfItems=items?size />
<#assign itemsPerRow=cm.localParameter("itemsPerRow", 3) />
<#assign itemsPerMobileRow=cm.localParameter("itemsPerMobileRow", 1) />
<#assign additionalClass=cm.localParameters().additionalClass!"" />
<#assign viewItemCssClass=cm.localParameters().viewItemCssClass!"" />
<#assign columnCssClass=cm.localParameters().columnCssClass!"" />
<#assign center=cm.localParameters().center!true />
<#assign addRows=cm.localParameters().addRows!true />
<#assign showHeadline=cm.localParameters().showHeadline!true />

<div class="cm-container ${additionalClass}" <@cm.metadata data=[bp.getContainerMetadata(self),bp.getPlacementHighlightingMetaData(self)!""] />>
  <#if showHeadline && self.teaserTitle?has_content>
    <h2 class="cm-container__headline" <@cm.metadata "properties.teaserTitle"/>><span>${self.teaserTitle}</span></h2>
  </#if>
  <#if (numberOfItems > 0)>
    <div class="row-grid row">
      <#list items as item>
        <#if addRows>
          <#-- add new row -->
          <@bp.renderNewRow index=item_index itemsPerRow=itemsPerRow additionalClass="row-grid "/>
        </#if>
        <#assign offsetClassTablet=""/>
        <#if center>
          <#assign offsetClassTablet=bp.getOffsetClass(item_index, numberOfItems, itemsPerRow, " col-sm-") />
        </#if>
        <#assign columnClasses="${columnCssClass} col-xs-${12/itemsPerMobileRow} col-sm-${12/itemsPerRow}${offsetClassTablet}" />
          <div class="${columnClasses}">
            <@cm.include self=item view=viewItems params={"islast": item?is_last, "cssClass": viewItemCssClass} />
          </div>
      </#list>
    </div>
  </#if>
</div>
