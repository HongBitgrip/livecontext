<#ftl strip_whitespace=true>
<#-- @ftlvariable name="blueprintFreemarkerFacade" type="com.coremedia.blueprint.cae.web.taglib.BlueprintFreemarkerFacade" -->

<#-- -------------------------------------------------------------------------------------------------------------------
 *
 * Please check the section "Freemarker API" in chapter "Reference" in the frontend manual for details and examples
 * for the following directives.
 * Any changes, additions or removals need to be documented in the manual.
 *
 ------------------------------------------------------------------------------------------------------------------- -->


<#function responsiveImageLinksData picture aspectRatios=[]>
  <#return blueprintFreemarkerFacade.responsiveImageLinksData(picture, cmpage, aspectRatios)>
</#function>

<#function getBiggestImageLink picture aspectRatio="">
  <#return blueprintFreemarkerFacade.getLinkForBiggestImageWithRatio(picture, cmpage, aspectRatio)>
</#function>


<#-- --- DEPRECATED ------------------------------------------------------------------------------------------------ -->


<#-- DEPRECATED use cm.getLink(picture.data) instead -->
<#function uncroppedImageLink picture>
  <#return blueprintFreemarkerFacade.uncroppedImageLink(picture)>
</#function>

<#--
 * Renders responsive images.
 *
 * @param self The item to render an image for
 * @param view (optional) the view to render the image with
 * @param limitAspectRatios (optional) A list of allowed aspect ratios.
 * @param classPrefix Set the classPrefix.
 * @param metadata (optional) if metadata shall be rendered it contains the value of param data for cm.metadata, default to ["properties.pictures"]
 * @param displayEmptyImage (optional) if set to true, a special div is rendered as a placeholder for a non existing image
 * @param displayDimmer (optional) render a dimmer on top of the image
 * @param background (optional) renders the image as a background attribute of a div instead of a standard img tag
 * @param additionalAttr (optional) additional attributes to be handed over to the responsive image renderer
 *
 * DEPRECATED
 -->
<#macro responsiveImage self classPrefix view="" limitAspectRatios=[]  metadata=["properties.pictures"], displayEmptyImage=true, displayDimmer=true, background=false, additionalAttr={} classSuffix="picture">
  <#local cssClassBox>${classPrefix}__${classSuffix}-box</#local>
  <#local cssClassImage>${classPrefix}__${classSuffix}</#local>
  <#local cssClassDimmer>${classPrefix}__dimmer</#local>
  <#if self?has_content && (self != cm.UNDEFINED)>
    <@cm.include self=self view=view params={
    "limitAspectRatios": limitAspectRatios,
    "classBox": cssClassBox,
    "classImage": cssClassImage,
    "metadata": metadata,
    "additionalAttr": additionalAttr,
    "background": background
    }/>
    <#if displayDimmer>
    <div class="${cssClassDimmer}"></div>
    </#if>
  <#elseif displayEmptyImage>
  <div class="${cssClassBox}" <@cm.metadata "properties.pictures" />>
    <div class="${cssClassImage} cm-image--missing"></div>
  </div>
  </#if>
</#macro>
