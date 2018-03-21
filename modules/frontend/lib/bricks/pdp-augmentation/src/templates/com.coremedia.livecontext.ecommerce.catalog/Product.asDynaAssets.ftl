<@cm.responseHeader name="Content-Type" value="text/html; charset=UTF-8"/><#-- could be used as fragment -->
<#-- @ftlvariable name="self" type="com.coremedia.livecontext.ecommerce.catalog.Product" -->
<#-- @ftlvariable name="orientation" type="java.lang.String" -->
<#-- @ftlvariable name="types" type="java.lang.String" -->

<div class="cm-product-assets" data-cm-refreshable-fragment='{"url": "${cm.getLink(self, 'asAssets')}"}'>
  <#if (types == 'all' || types == 'visuals')>
    <#assign visuals=bp.createBeansFor(self.visuals) />
    <#if !visuals?has_content>
      <#-- use catalog image if no visuals assigned -->
      <#if self.catalogPicture.picture?has_content>
        <#-- managed asset found -->
        <@cm.include self=self.catalogPicture!cm.UNDEFINED params={"overflow":false, "classBox": "cm-product-assets__picture-box"}/>
      <#else>
        <#-- use original image url from catalog-->
        <@cm.include self=self.catalogPicture!cm.UNDEFINED params={"overflow":false}/>
      </#if>
    <#else>
      <#if orientation?has_content && visuals?has_content>

      <#-- set image aspect ratio -->
        <#assign classLightbox="" />
        <#assign aspectRatio="portrait_ratio1x1" />
        <#if (orientation == 'landscape') >
          <#assign classLightbox="cm-lightbox--landscape" />
          <#assign aspectRatio="landscape_ratio4x3" />
        <#elseif (orientation == 'portrait') >
          <#assign classLightbox="cm-lightbox--portrait" />
          <#assign aspectRatio="portrait_ratio3x4" />
        </#if>
        <#assign limitAspectRatios=[aspectRatio] />
      <#-- slideshow with large images -->
          <div class="cm-product-assets__slideshow cm-collection--slideshow cm-slideshow--carousel cm-lightbox--gallery">
          <#-- large image with link to lightBoxed image -->
            <#list visuals![] as visual>
              <@cm.include self=visual!cm.UNDEFINED view="asLightBox" params={
                "classBox": classLightbox,
                "limitAspectRatios": limitAspectRatios
              } />
            </#list>
          </div>
      <#-- this is the selector slideshow -->
        <#if (visuals?size > 1)>
            <div class="cm-product-assets__carousel cm-collection--slideshow cm-slideshow--carousel-chooser">
              <#list visuals![] as visual>
                  <div class="cycle-slide">
                    <@cm.include self=visual!cm.UNDEFINED view="teaser" params={
                      "renderTeaserTitle": false,
                      "renderTeaserText": false,
                      "renderDimmer": false,
                      "renderType": "plain",
                      "limitAspectRatios": ["portrait_ratio1x1"]
                    } />
                  </div>
              </#list>
              <#-- add empty slides to get a responsive layout for 4 slides -->
              <#if (visuals?size == 2 )>
                  <div class="cycle-slide cycle-slide-disabled"></div>
                  <div class="cycle-slide cycle-slide-disabled"></div>
              <#elseif (visuals?size == 3 )>
                  <div class="cycle-slide cycle-slide-disabled"></div>
              </#if>

              <#-- controls to navigate thru images -->
              <#if (visuals?size > 4)>
                <div class="cm-collection--slideshow__prev cm-direction-arrow cm-direction-arrow--left"></div>
                <div class="cm-collection--slideshow__next cm-direction-arrow cm-direction-arrow--right"></div>
              </#if>
            </div>
        </#if>
      </#if>
    </#if>
  </#if>

  <#-- render download list -->
  <#if (types == 'all' || types == 'downloads') >
    <#assign downloads=bp.createBeansFor(self.downloads) />
    <#if (downloads?size > 0)>
      <div class="cm-product-assets__downloads cm-product-assets-downloads">
        <h3 class="cm-product-assets-downloads__title cm-heading3">${bp.getMessage("product_assets_downloads")}</h3>
        <ul class="cm-product-assets-downloads__list">
          <#list downloads![] as download>
            <#if download.data?has_content>
              <li class="cm-product-assets-downloads__item"<@cm.metadata download.content />><@cm.include self=download view="asLink" /></li>
            </#if>
          </#list>
        </ul>
      </div>
    </#if>
  </#if>

</div>
