<#-- @ftlvariable name="self" type="com.coremedia.livecontext.commercebeans.ProductInSite" -->
<#-- @ftlvariable name="metadata" type="java.util.List" -->

<#if self.product?has_content && (cmpage.navigation.rootNavigation)?has_content>
  <#assign cssClasses = cm.localParameter("islast", false)?then(" is-last", "") />
  <#assign blockClass=cm.localParameters().blockClass!"cm-teasable" />
  <#assign additionalClass=cm.localParameters().additionalClass!"" />
  <#assign link=cm.getLink(self) />

  <#assign renderTeaserText=cm.localParameter("renderTeaserText", true) />
  <#assign renderDimmer=cm.localParameter("renderDimmer", true) />
  <#assign renderEmptyImage=cm.localParameter("renderEmptyImage", true) />

  <div class="${blockClass} ${blockClass}--product ${cssClasses} ${additionalClass}">
    <div class="${blockClass}__wrapper">
    <@bp.optionalLink href="${link}">
      <#-- picture -->
      <@bp.responsiveImage self=(self.product.catalogPicture)!cm.UNDEFINED classPrefix=blockClass displayEmptyImage=renderEmptyImage displayDimmer=renderDimmer limitAspectRatios=bp.setting(cmpage.navigation, "default_aspect_ratios_for_teaser", [])/>
      <div class="${blockClass}__caption">
        <@cm.include self=self.product!cm.UNDEFINED view="info" params={
        "classBox": "${blockClass}__info",
        "classPrice": "cm-price--teaser"
        } />
      </div>
    </@bp.optionalLink>
    </div>
  </div>
</#if>