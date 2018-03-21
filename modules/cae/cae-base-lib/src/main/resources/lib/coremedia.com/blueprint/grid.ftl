<#ftl strip_whitespace=true>
<#-- @ftlvariable name="blueprintFreemarkerFacade" type="com.coremedia.blueprint.cae.web.taglib.BlueprintFreemarkerFacade" -->

<#-- -------------------------------------------------------------------------------------------------------------------
 *
 * Please check the section "Freemarker API" in chapter "Reference" in the frontend manual for details and examples
 * for the following directives.
 * Any changes, additions or removals need to be documented in the manual.
 *
 ------------------------------------------------------------------------------------------------------------------- -->


<#function getPageMetadata page>
  <#return blueprintFreemarkerFacade.getPageContext(page).content />
</#function>

<#function getPlacementPropertyName placement>
  <#return blueprintFreemarkerFacade.getPlacementPropertyName(placement) />
</#function>

<#--
 * Returns the metadata that was determined for the container, either as list or as plain object
 *
 * @param container The container the metadata should be determined for
 -->
<#function getContainerMetadata container>
  <#return blueprintFreemarkerFacade.getContainerMetadata(container) />
</#function>

<#--
 * Utility function to allow rendering of containers with custom items, e.g. partial containers with a subset of
 * the items the original container had.
 *
 * @param items The items to be put inside the new container
 * @return a new container
 -->
<#function getContainer items=[]>
  <#return blueprintFreemarkerFacade.getContainer(items) />
</#function>

<#--
 * Utility function to allow rendering of containers with custom items, e.g. partial containers with a subset of
 * the items the original container had.
 *
 * @param baseContainer The base container the new container shall be created from
 * @param items The items to be put inside the new container
 -->
<#function getContainerFromBase baseContainer items=[]>
  <#return blueprintFreemarkerFacade.getContainer(baseContainer, items) />
</#function>

<#function getPageLanguageTag object>
  <#return blueprintFreemarkerFacade.getLanguageTag(object) />
</#function>

<#function getPageDirection object>
  <#return blueprintFreemarkerFacade.getDirection(object) />
</#function>
<#--
 * get offset for given element for bootstrap grid
 *
 * @param index Integer
 * @param numberOfItems Integer
 * @param itemsPerRow Integer
 * @param prefix (optional) String, put in front of offset class
 * @param force (optional) Boolean, force offset class, even if it's zero
-->
<#function getOffsetClass index numberOfItems itemsPerRow  prefix="" force=false>
  <#-- bootstrap default grid = 12 rows -->
  <#assign width=12/itemsPerRow />
  <#assign isFirstItemOfRow=(index % itemsPerRow == 0) />
  <#assign isLastRow=(numberOfItems - numberOfItems % itemsPerRow - index) <= 0 />
  <#-- define offset class to align items in rows containing less than 3 items centered -->
  <#assign offsetClass="" />
  <#-- offset only applies to first element of last row -->
  <#if (isLastRow && isFirstItemOfRow)>
    <#-- offset depends on the number of items in the last row -->
    <#assign offsetValue=(12-((numberOfItems % itemsPerRow)*width))/2 />
    <#assign offsetClass="${prefix}offset-${offsetValue}" />
  <#elseif (force)>
    <#assign offsetClass="${prefix}offset-0" />
  </#if>

  <#return offsetClass>
</#function>

<#--
 * render closing and opening div for bootstrap grid
 *
 * @param index Integer
 * @param itemsPerRow Integer
 * @param additionalClass (optional) String
-->
<#macro renderNewRow index itemsPerRow additionalClass="">
  <#-- bootstrap default grid = 12 rows -->
  <#assign width=12/itemsPerRow />
  <#assign isFirstItemOfRow=(index % itemsPerRow == 0) />
  <#-- offset only applies to first element of last row -->
  <#if (isFirstItemOfRow && index != 0)>
    </div>
    <div class="${additionalClass}row">
  </#if>
</#macro>

<#function getPlacementHighlightingMetaData placement>
  <#return blueprintFreemarkerFacade.getPlacementHighlightingMetaData(placement)>
</#function>


<#-- --- DEPRECATED ------------------------------------------------------------------------------------------------ -->


<#-- DEPRECATED -->
<#function getPlacementByName name pagegrid>
  <#return blueprintFreemarkerFacade.getPlacementByName(name, pagegrid) />
</#function>

<#-- DEPRECATED, use bp.getLanguageTag instead -->
<#function getPageLanguage object>
  <#return blueprintFreemarkerFacade.getLanguage(object) />
</#function>
