<#-- @ftlvariable name="self" type="com.coremedia.livecontext.ecommerce.p13n.MarketingImage" -->
<#-- @ftlvariable name="metadata" type="java.util.List" -->

<div class="cm-teasable cm-teasable--image"<@cm.metadata metadata![] />>
  <div class="cm-teasable__content cm-aspect-ratio-box">
    <img class="cm-aspect-ratio-box__content" <@cm.dataAttribute name="data-cm-non-adaptive-content" data={"overflow": false} /> src="${(self.thumbnailUrl)!""}" alt="${(self.shortText)!""}">
  </div>
</div>
