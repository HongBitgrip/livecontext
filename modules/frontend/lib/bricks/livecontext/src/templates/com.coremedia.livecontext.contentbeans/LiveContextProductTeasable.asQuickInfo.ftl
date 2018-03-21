<#-- @ftlvariable name="self" type="com.coremedia.livecontext.contentbeans.LiveContextProductTeasable" -->
<#-- @ftlvariable name="classQuickInfo" type="java.lang.String" -->
<#-- @ftlvariable name="metadata" type="java.util.List" -->
<#-- @ftlvariable name="quickInfoId" type="java.lang.String" -->
<#-- @ftlvariable name="quickInfoNextId" type="java.lang.String" -->
<#-- @ftlvariable name="quickInfoPreviousId" type="java.lang.String" -->
<#-- @ftlvariable name="quickInfoModal" type="java.lang.Boolean" -->
<#-- @ftlvariable name="quickInfoGroup" type="java.lang.String" -->
<#-- @ftlvariable name="overlay" type="java.util.Map" -->

<#-- if overlay configuration is not set explicitly assert false for each key not set -->
<#assign overlay={
  "displayTitle": false,
  "displayShortText": false,
  "displayPicture": false,
  "displayDefaultPrice": false,
  "displayDiscountedPrice": false,
  "displayOutOfStockLink": false
} + overlay!{} />
<#assign quickInfoData={} />
<#if quickInfoModal?has_content && quickInfoModal?is_boolean>
  <#assign quickInfoData=quickInfoData + {"modal": quickInfoModal!false} />
</#if>
<#if quickInfoGroup?has_content>
  <#assign quickInfoData=quickInfoData + {"group": quickInfoGroup!""} />
</#if>

<div id="${quickInfoId!bp.generateId("quickinfo")}" class="cm-quickinfo <#if !overlay.displayPicture>cm-quickinfo--no-image</#if> ${classQuickInfo!""}" <@cm.dataAttribute name="data-cm-quickinfo" data=quickInfoData /><@cm.metadata (metadata![]) + [self.content] />>

  <#-- image -->
  <#if overlay.displayPicture>
    <div class="cm-quickinfo__container">
      <a href="${cm.getLink(self.productInSite!(cm.UNDEFINED))}" class="cm-quickinfo__picture-link">
        <#assign pictureParams={
          "classBox": "cm-quickinfo__picture-box",
          "classImage": "cm-quickinfo__picture"
        } />
        <#if self.picture?has_content>
          <@cm.include self=self.picture!cm.UNDEFINED params=pictureParams + {"metadata": ["properties.pictures"]} />
        <#else>
          <@cm.include self=(self.product.catalogPicture)!cm.UNDEFINED params=pictureParams />
        </#if>
      </a>
    </div>
  </#if>
  <div class="cm-quickinfo__container content-container">
    <div class="cm-quickinfo__content">
      <#-- title -->
      <#assign showTitle=self.teaserTitle?has_content && overlay.displayTitle />
      <#assign showTeaserText=self.teaserText?has_content && overlay.displayShortText />
      <#-- teaserTitle -->
      <div class="cm-quickinfo__header">
        <#if showTitle>
          <h5 class="cm-quickinfo__title cm-heading5"<@cm.metadata "properties.teaserTitle" />>${self.teaserTitle}</h5>
        </#if>
        <#-- close button -->
        <@bp.button baseClass="" iconClass="cm-icon__symbol icon-close" iconText=bp.getMessage("button_close") attr={"class": "cm-quickinfo__close cm-icon"}/>
      </div>

      <#-- price -->
      <#if overlay.displayDefaultPrice || overlay.displayDiscountedPrice>
        <div class="cm-quickinfo__price">
          <@cm.include self=self.product!cm.UNDEFINED view="pricing" params={"showListPrice": overlay.displayDefaultPrice, "showOfferPrice": overlay.displayDiscountedPrice, "classListPrice": "cm-price--quickinfo cm-quickinfo__listprice", "classOfferPrice": "cm-price--quickinfo cm-quickinfo__offerprice"} />
        </div>
      </#if>
      <#-- teaserText -->
      <#if showTeaserText>
        <div class="cm-quickinfo__text"<@cm.metadata "properties.teaserText" />>
          <#-- strip wrong <p/> tags from ecommerce, happens in hybris -->
          <@bp.renderWithLineBreaks bp.truncateText(self.teaserText!"", 175)?replace("&lt;p&gt;", "")?replace("&lt;/p&gt;", "") />
        </div>
      <#else>
        <div class="cm-quickinfo__text"></div>
      </#if>

      <#if (self.product?has_content && self.product.isAvailable()) || overlay.displayOutOfStockLink>
          <#-- add-to-cart button -->
          <div class="cm-quickinfo__controls cm-button-group cm-button-group--linked-large">
            <@lc.addToCartButton product=self.product!cm.UNDEFINED withLink=cm.getLink(self.productInSite!(cm.UNDEFINED)) enableShopNow=self.isShopNowEnabled(cmpage.context) attr={"classes": ["cm-button-group__button", "cm-button--linked-large", (lc.getVendorName() == 'SAP Hybris')?then('btn btn-default', '')]} />
          </div>
      </#if>
    </div>
  </div>
  <#-- next/previous buttons -->
  <#if (quickInfoNextId?? && quickInfoPreviousId??)>
    <#if (quickInfoNextId?length > 0 && quickInfoPreviousId?length > 0)>
      <@bp.button baseClass="" iconClass="cm-icon__symbol icon-next" iconText=bp.getMessage("button_next") attr={"class": "cm-quickinfo__switch cm-quickinfo__switch--next", "data-cm-target": quickInfoNextId}/>
      <@bp.button baseClass="" iconClass="cm-icon__symbol icon-prev" iconText=bp.getMessage("button_prev") attr={"class": "cm-quickinfo__switch cm-quickinfo__switch--prev", "data-cm-target": quickInfoPreviousId}/>
    </#if>
  </#if>
</div>
