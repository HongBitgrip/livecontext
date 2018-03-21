<#ftl strip_whitespace=true>
<#-- @ftlvariable name="liveContextFreemarkerFacade" type="com.coremedia.livecontext.web.taglib.LiveContextFreemarkerFacade" -->
<#-- @ftlvariable name="liveContextLoginFreemarkerFacade" type="com.coremedia.livecontext.web.taglib.LiveContextLoginFreemarkerFacade" -->
<#-- @ftlvariable name="blueprintFreemarkerFacade" type="com.coremedia.blueprint.cae.web.taglib.BlueprintFreemarkerFacade" -->

<#-- -------------------------------------------------------------------------------------------------------------------
 *
 * Please check the section "Freemarker API" in chapter "Reference" in the frontend manual for details and examples
 * for the following directives.
 * Any changes, additions or removals need to be documented in the manual.
 *
 ------------------------------------------------------------------------------------------------------------------- -->


<#--
 * Renders an addToCart button
 * If product has only one variant the button has add to cart functionality
 * otherwise its just a link to the product detail page
 *
 * @param product the product that needs to be added to cart
 * @param alwaysShow (optional) if true, forces the button to be shown even if product is not available
 * @param alwaysClickable (optional) always attached add to cart functionality even if there is more than one variant
 * @param withLink (optional) link to product detail page
 * @param enableShopNow (optional, default:true) enable "addToCart" functionality. If "false", only show the "Details" button
 -->
<#macro addToCartButton product alwaysShow=false alwaysClickable=false enableShopNow=true withLink="" attr={}>
  <#local numberOfVariants=(product.variants?size)!0 />
  <#local hasSingleSKU=(numberOfVariants == 1) />
  <#local isProductAvailable=(product.isAvailable())!false />

  <#-- variant 1) unavailable -->
  <#local buttonLabel=bp.getMessage("cart_unavailable") />
  <#local buttonData={} />
  <#local buttonClasses=[] />
  <#local iconClass="" />

  <#-- variant 2) available -->
  <#if (alwaysShow || isProductAvailable)>
    <#local buttonLabel=bp.getMessage("cart_view_variants") />
    <#local buttonClasses=buttonClasses + ["cm-button--primary"] />

  <#-- variant 3) available with one sku -->
    <#if (enableShopNow && (alwaysClickable || hasSingleSKU))>
      <#local cart=bp.substitute("cart", product)!cm.UNDEFINED />
      <#local buttonLabel=bp.getMessage("cart_add_item") />
      <#local externalTechId=hasSingleSKU?then(product.variants[0].externalTechId, product.externalTechId)/>
      <#local buttonData={"data-cm-cart-add-item": '{"id": "${externalTechId!""}", "link": "${cm.getLink(cart, "ajax")}", "cart": ".cm-cart" }'} />
      <#local iconClass="icon-none" />
    </#if>
  </#if>

  <#local attr=bp._extendSequenceInMap(attr, "classes", buttonClasses) />

  <#local link="" />
  <#if (withLink?has_content && ((!alwaysClickable && !hasSingleSKU) || !enableShopNow))>
    <#local link=withLink>
  </#if>
  <@bp.button text=buttonLabel href=link baseClass="cm-button" iconClass=iconClass attr=(attr + buttonData) />
</#macro>

<#--Format Utils-->
<#function formatPrice amount currency locale>
  <#return liveContextFreemarkerFacade.formatPrice(amount, currency, locale)>
</#function>

<#function createProductInSite product>
  <#return liveContextFreemarkerFacade.createProductInSite(product)/>
</#function>

<#function previewMetaData>
  <#return liveContextFreemarkerFacade.getPreviewMetadata()>
</#function>

<#function augmentedContent>
  <#return liveContextFreemarkerFacade.isAugmentedContent()>
</#function>


<#-- Returns name of eCommerce Vendor like IBM or SAP Hybris -->
<#function getVendorName>
  <#return liveContextFreemarkerFacade.getVendorName()>
</#function>

<#--
 * Builds the url for the status handler to retrieve the actual state (logged in/logged out) of the user.
 -->
<#function getStatusUrl>
  <#return liveContextLoginFreemarkerFacade.getStatusUrl()>
</#function>

<#--
 * Builds the absolute url to the login formular of a commerce system.
 -->
<#function getLoginFormUrl>
  <#return liveContextLoginFreemarkerFacade.getLoginFormUrl()>
</#function>

<#--
 * Builds a logout url of a commerce system to logout the current user.
 -->
<#function getLogoutUrl>
  <#return liveContextLoginFreemarkerFacade.getLogoutUrl()>
</#function>



<#-- --- DEPRECATED ------------------------------------------------------------------------------------------------ -->

<#-- DEPRECATED, use bp.getPlacementHighlightingMetaData instead -->
<#function fragmentHighlightingMetaData placement>
  <#return blueprintFreemarkerFacade.getPlacementHighlightingMetaData(placement)>
</#function>

<#-- UNUSED -->
<#function fragmentContext>
  <#return liveContextFreemarkerFacade.fragmentContext()>
</#function>

<#-- DEPRECATED -->
<#function getSecureScheme>
  <#return liveContextFreemarkerFacade.getSecureScheme() />
</#function>

<#-- DEPRECATED, UNUSED -->
<#macro getLoginLink linkClass="">
  <#local cssClass=""/>
  <#if linkClass?has_content>
    <#local cssClass="class=\"" + linkClass + "\""/>
  </#if>
  <a id="cm-login" ${cssClass} data-loginstatus="${liveContextLoginFreemarkerFacade.getStatusUrl()}" href="${liveContextLoginFreemarkerFacade.getLoginFormUrl()}" style="display: none" title="Login">Login</a>
  <a id="cm-logout" ${cssClass} data-loginstatus="${liveContextLoginFreemarkerFacade.getStatusUrl()}" href="${liveContextLoginFreemarkerFacade.getLogoutUrl()}" style="display: none" title="Logout">Logout</a>
</#macro>
