<#-- @ftlvariable name="self" type="com.coremedia.livecontext.contentbeans.LiveContextProductTeasable" -->

<#assign cssClasses = self.teaserText?has_content?then(" is-text", "") + cm.localParameter("islast", false)?then(" is-last", "") />
<#assign blockClass=cm.localParameters().blockClass!"cm-teasable" />
<#assign additionalClass=cm.localParameters().additionalClass!"" />
<#assign link=cm.getLink(self.productInSite!cm.UNDEFINED) />
<#assign renderTeaserText=cm.localParameter("renderTeaserText", true) />
<#assign renderDimmer=cm.localParameter("renderDimmer", true) />
<#assign renderEmptyImage=cm.localParameter("renderEmptyImage", true) />
<div class="${blockClass} ${blockClass}--product ${cssClasses} ${additionalClass}"<@cm.metadata self.content />>
    <div class="${blockClass}__wrapper">
    <@bp.optionalLink href="${link}">
      <#-- picture -->
      <@cm.include self=self view="_picture" params={"blockClass": blockClass, "renderDimmer": renderDimmer, "renderEmptyImage": renderEmptyImage}/>

        <div class="${blockClass}__caption">
          <#-- teaser title -->
          <#if self.teaserTitle?has_content>
              <h3 class="${blockClass}__headline" <@cm.metadata "properties.teaserTitle" />>
                  <span>${self.teaserTitle!""}</span>
              </h3>
          </#if>
          <#-- teaser text or short description -->
          <#if renderTeaserText>
            <#if self.teaserText?has_content>
              <#-- teaser text -->
              <p class="${blockClass}__text" <@cm.metadata "properties.teaserText" />>
                <@bp.renderWithLineBreaks bp.truncateText(self.teaserText!"", bp.setting(cmpage, blockClass + "-max-length", 115)) />
              </p>
            <#else>
              <#-- if no teaser text exists, render the product's short description-->
              <div class="${blockClass}__text"><@cm.include self=self.product.shortDescription/></div>
            </#if>
          </#if>
          <@cm.include self=self.product!cm.UNDEFINED view="info" params={
          "classBox": "${blockClass}__info",
          "classPrice": "cm-price--teaser"
          } />
         <#--custom call-to-action button-->
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
            }
            />
          </#if>
        </div>
    </@bp.optionalLink>
    </div>

  <@cm.hook id=bp.viewHookEventNames.VIEW_HOOK_TEASER />
</div>

