<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.Container" -->
<#-- @ftlvariable name="displayIndicators" type="java.lang.Boolean" -->
<#-- @ftlvariable name="displayControls" type="java.lang.Boolean" -->
<#-- @ftlvariable name="displayPagination" type="java.lang.Boolean" -->
<#-- @ftlvariable name="modifier" type="java.lang.String" -->
<#-- @ftlvariable name="index" type="java.lang.Integer" -->
<#-- @ftlvariable name="items" type="java.util.List" -->
<#-- @ftlvariable name="carouselItemParams" type="java.util.List" -->
<#-- @ftlvariable name="viewItems" type="java.lang.String" -->
<#-- @ftlvariable name="controlIcon" type="java.lang.String" -->
<#-- @ftlvariable name="additionalClass" type="java.lang.String" -->
<#-- @ftlvariable name="viewItemCssClass" type="java.lang.String" -->

<#assign modifier=cm.localParameters().modifier!"" />
<#assign index=cm.localParameters().index!0 />
<#assign additionalClass=cm.localParameters().additionalClass!"" />
<#assign viewItemCssClass=cm.localParameters().viewItemCssClass!"" />
<#assign carouselItemParams=cm.localParameters().carouselItemParams!{} />
<#-- this variable seems not to be used -->
<#assign metadataItemsName=cm.localParameters().metadataItemsName!'items' />
<#assign viewItems=cm.localParameters().viewItems!"asTeaser" />
<#assign displayPagination=cm.localParameters().displayPagination!false />
<#assign displayControls=cm.localParameters().displayControls!true />
<#assign displayIndicators=cm.localParameters().displayIndicators!false />
<#assign controlIcon=cm.localParameters().controlIcon!"chevron" />

<#assign items=self.flattenedItems![] />
<#if items?has_content>
  <#assign carouselId=bp.generateId("carousel")/>
  <#assign clsModifier="" />
  <#if modifier?has_content>
    <#assign clsModifier="cm-carousel__control--${modifier}" />
  </#if>

<div id="${carouselId}" class="cm-carousel carousel slide ${additionalClass}" data-cm-carousel='{"interval":"6000"}'<@cm.metadata data=[bp.getContainerMetadata(self),bp.getPlacementHighlightingMetaData(self)!""] />>
<#-- Indicators -->
  <#if displayIndicators && (items?size > 1) >
      <ol class="cm-carousel-indicators carousel-indicators">
        <#list items as item>
            <li data-target="#${carouselId}" data-slide-to="${item_index}"<#if item_index == 0> class="active"</#if>>
            </li>
        </#list>
      </ol>
  </#if>

<#-- Wrapper for slides -->
    <div class="cm-carousel-inner carousel-inner" role="listbox">
      <#list items as item>
        <#assign itemCssClass="item"/>
        <#if item_index == 0>
          <#assign itemCssClass=itemCssClass + " active"/>
        </#if>
          <div class="${itemCssClass!"item"}">
            <@cm.include self=item view=viewItems params={"index": index, "viewItemCssClass": viewItemCssClass} + carouselItemParams/>
          </div>
      </#list>
    </div>

<#-- Controls -->
  <#if displayControls && (items?size > 1) >
      <div class="cm-carousel__control ${clsModifier}">
          <a class="left cm-carousel-control carousel-control" href="#${carouselId}" role="button" data-slide="prev">
              <span class="glyphicon glyphicon-${controlIcon}-left" aria-hidden="true"></span>
              <span class="sr-only">${bp.getMessage("button_previous")}</span>
          </a>
        <#if displayPagination>
            <p class="cm-carousel__pagination">
                <span class="cm-carousel__pagination-index">1</span>
                /
                <span class="cm-carousel__pagination-total">${items?size}</span>
            </p>
        </#if>
          <a class="right cm-carousel-control carousel-control" href="#${carouselId}" role="button" data-slide="next">
              <span class="glyphicon glyphicon-${controlIcon}-right" aria-hidden="true"></span>
              <span class="sr-only">${bp.getMessage("button_next")}</span>
          </a>
      </div>
  </#if>
</div>

</#if>
