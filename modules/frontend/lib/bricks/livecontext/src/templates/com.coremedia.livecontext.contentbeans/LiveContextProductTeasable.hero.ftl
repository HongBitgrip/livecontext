<#-- @ftlvariable name="self" type="com.coremedia.livecontext.contentbeans.LiveContextProductTeasable" -->

<#assign blockClass=cm.localParameters().blockClass!"cm-hero" />

<#assign renderTeaserText=cm.localParameter("renderTeaserText", true) />
<#assign renderDimmer=cm.localParameter("renderDimmer", true) />
<#assign renderEmptyImage=cm.localParameter("renderEmptyImage", true) />

<#if self.externalId?has_content>
  <#assign link=cm.getLink(self.productInSite!cm.UNDEFINED) />

  <div class="${blockClass} ${blockClass}--product"<@preview.metadata self.content />>
    <@bp.optionalLink href="${link}">
      <#-- picture -->
      <@cm.include self=self view="_picture" params={"blockClass": blockClass, "aspectRatiosSuffix": "hero_teaser", "renderDimmer": renderDimmer, "renderEmptyImage": renderEmptyImage}/>
    </@bp.optionalLink>
    <#-- with caption -->
    <div class="${blockClass}__caption">
      <#-- headline -->
      <#if self.teaserTitle?has_content>
        <@bp.optionalLink href="${link}">
          <h1 class="${blockClass}__headline"<@preview.metadata "properties.teaserTitle" />>${self.teaserTitle!""}</h1>
        </@bp.optionalLink>
      </#if>
      <#-- teaser text -->
      <#if renderTeaserText && self.teaserText?has_content>
        <p class="${blockClass}__text"<@preview.metadata "properties.teaserText" />>
          <@bp.renderWithLineBreaks bp.truncateText(self.teaserText!"", bp.setting(cmpage, blockClass+"-max-length", 140)) />
        </p>
      </#if>
      <@bp.optionalLink href="${link}">
        <#-- product price (and title) -->
        <@cm.include self=self.product!cm.UNDEFINED view="info" params={
          "classBox": "${blockClass}__info",
          "classPrice": "cm-price--teaser"
        } />
      </@bp.optionalLink>

      <#-- custom call-to-action button -->
      <#-- shop now button and quickinfo -->
      <#if self.isShopNowEnabled(cmpage.context)>
        <#assign quickInfoId=bp.generateId("cm-quickinfo-") />
        <#-- button -->
        <div class="cm-button-group--shopnow cm-button-group cm-button-group--overlay">
          <@bp.button text=bp.getMessage("button_shop_now") attr={
          "classes": ["cm-button-group__button", "cm-button--primary", "cm-button--shadow"],
          "data-cm-button--quickinfo": '{"target": "${quickInfoId!""}"}'
          } />
        </div>
        <#-- quickinfo -->
        <@cm.include self=self view="asQuickInfo" params={
          "quickInfoId": quickInfoId!"",
          "quickInfoGroup": "product-teasers",
          "quickInfoModal": true,
          "classQuickInfo": "cm-quickinfo--shop-now",
          "metadata": ["properties.target"],
          "overlay": {
            "displayTitle": true,
            "displayShortText": true,
            "displayPicture": true,
            "displayDefaultPrice": true,
            "displayDiscountedPrice": true,
            "displayOutOfStockLink": true
          }
        } />
      </#if>
    </div>
  </div>
</#if>
