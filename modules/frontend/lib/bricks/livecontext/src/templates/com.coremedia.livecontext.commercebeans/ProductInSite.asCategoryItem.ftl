<#-- @ftlvariable name="self" type="com.coremedia.livecontext.commercebeans.ProductInSite" -->

<#assign limitAspectRatios=bp.setting(cmpage.navigation, "default_aspect_ratios_for_teaser", [])/>

<#if self.product?has_content && (cmpage.navigation.rootNavigation)?has_content>
  <#if self.product.listPrice?has_content && self.product.currency?has_content && self.product.locale?has_content>
    <#assign listPriceFormatted=lc.formatPrice(self.product.listPrice, self.product.currency, self.product.locale)/>
  </#if>
  <div class="cm-category-item">
    <@bp.optionalLink href="${cm.getLink(self)}">
      <@cm.include self=self.product.catalogPicture!cm.UNDEFINED params={
        "limitAspectRatios": limitAspectRatios,
        "classBox": "cm-category-item__image cm-aspect-ratio-box",
        "classImage": "cm-aspect-ratio-box__content"
      } />

      <div class="cm-category-item__info">
        <#-- headline -->
        <h4 class="cm-category-item__title">${self.product.name!""}</h4>
        <#-- price -->
        <div class="cm-category-item__pricing">
          <@cm.include self=self.product view="pricing" params={
            "classListPrice": "cm-category-item__list-price cm-price--category-item",
            "classOfferPrice": "cm-category-item__offer-price cm-price--category-item"
          } />
        </div>
        <#-- add to cart button -->
        <div class="cm-category-item__cart">
          <div class="cm-button-group cm-button-group--linked">
            <@lc.addToCartButton product=self.product attr={"classes": ["cm-button-group__button", "cm-button--linked"]} />
          </div>
        </div>
      </div>
    </@bp.optionalLink>
  </div>
</#if>
