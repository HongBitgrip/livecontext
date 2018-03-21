<#-- @ftlvariable name="self" type="com.coremedia.livecontext.contentbeans.LiveContextProductTeasable" -->
<#-- @ftlvariable name="overlay" type="java.util.Map" -->
<#-- @ftlvariable name="classOverlay" type="java.lang.String" -->
<#-- @ftlvariable name="metadata" type="java.util.List" -->

<#-- if overlay configuration is not set explicitly assert false for each key not set -->
<#assign overlay={
  "displayTitle": false,
  "displayDefaultPrice": false,
  "displayDiscountedPrice": false
} + overlay!{} />

<div class="cm-overlay ${classOverlay}"<@cm.metadata data=(metadata![]) + [self.content] />>
  <@bp.optionalLink href=cm.getLink(self.productInSite!(cm.UNDEFINED)) attr={"class":"cm-overlay__link"}>
    <#if overlay.displayTitle && self.teaserTitle?has_content>
      <div class="cm-overlay__item cm-overlay__item--title"<@cm.metadata "properties.teaserTitle" />>${self.teaserTitle}</div>
    <#else>
      <div class="cm-overlay__item cm-overlay__item--title"><@bp.message "button_quickinfo" /></div>
    </#if>
    <div class="cm-overlay__item"<@cm.metadata "properties.externalId" />>
      <@cm.include self=self.product!cm.UNDEFINED view="pricing" params={"showListPrice": overlay.displayDefaultPrice, "showOfferPrice": overlay.displayDiscountedPrice, "classListPrice": "cm-price--overlay", "classOfferPrice": "cm-price--overlay"} />
    </div>
  </@bp.optionalLink>
</div>