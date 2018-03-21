<#-- @ftlvariable name="blueprintFreemarkerFacade" type="com.coremedia.blueprint.cae.web.taglib.BlueprintFreemarkerFacade" -->

<#-- -------------------------------------------------------------------------------------------------------------------
 *
 * Please check the section "Freemarker API" in chapter "Reference" in the frontend manual for details and examples
 * for the following directives.
 * Use the brick "image-maps" instead of direct accessing the following imagemap functions.
 *
 * Any changes, additions or removals need to be documented in the manual.
 *
 ------------------------------------------------------------------------------------------------------------------- -->


<#--
 * The width all image transformations are based on.
 * PRIVATE
 -->
<#assign IMAGE_TRANSFORMATION_BASE_WIDTH=blueprintFreemarkerFacade.imageTransformationBaseWidth />

<#--
 * initializes a map of image map parameters
 *
 * @param quickInfoModal (optional) Boolean
 * @param quickInfoGroup (optional) String
 * @param imageMapIdPrefix (optional) String
 * @param quickInfoMainIdSuffix (optional) String
 * @param quickInfoIdPrefix (optional) String
 * PRIVATE
-->
<#function initializeImageMap quickInfoModal=false quickInfoGroup="" imageMapIdPrefix="cm-map-" quickInfoMainIdSuffix="-quickinfo--main" quickInfoIdPrefix="cm-quickinfo-">
  <#-- generate unique id for imagemap -->
  <#assign imageMapId=bp.generateId(imageMapIdPrefix)/>

  <#-- generate quick info id for main teaser -->
  <#assign quickInfoMainId=imageMapId + quickInfoMainIdSuffix />

  <#-- generate quick info ids list and forward the list to the templates areas map and areas quickinfo-->
  <#assign quickInfoData={} />
  <#if quickInfoModal?has_content && quickInfoModal?is_boolean>
    <#assign quickInfoData=quickInfoData + {"modal": quickInfoModal!false} />
  </#if>
  <#if quickInfoGroup?has_content>
    <#assign quickInfoData=quickInfoData + {"group": quickInfoGroup!""} />
  </#if>

  <#-- generate quick info ids list and forward the list to the templates areas map and areas quickinfo-->
  <#assign imageMapAreas=bp.responsiveImageMapAreas(self) />
  <#assign quickInfoIdList = [] />
  <#if imageMapAreas?has_content>
    <#list imageMapAreas![] as imageMapArea>
      <#if imageMapArea?has_content>
        <#assign quickInfoIdList = quickInfoIdList + [bp.generateId(quickInfoIdPrefix)]/>
      </#if>
    </#list>
  </#if>
  <#assign quickInfoIdList = quickInfoIdList?join(",") />


  <#assign imageMapParams={
  "imageMapId": imageMapId,
  "quickInfoMainId": quickInfoMainId,
  "quickInfoIdList": quickInfoIdList
  }/>

  <#return imageMapParams/>
</#function>

<#--
 * Return list of area configurations with the 'coords' attribute being transformed according to the image map's
 * picture transformations. If cropping is disabled, an empty list is returned.
 *
 * @param imageMap CMImageMap to retrieve areas from
 * @param limitAspectRatios List of aspect ratios to be calculated. If empty, all aspect ratios will be calculated
 * PRIVATE
 -->
<#function responsiveImageMapAreas imageMap limitAspectRatios=[]>
  <#return blueprintFreemarkerFacade.responsiveImageMapAreas(imageMap, limitAspectRatios)>
</#function>

<#--
 * Returns Map containing information to be rendered as data attribute delivering informationen about the ImageMap
 * areas to JavaScript.
 *
 * @param coords map of transformation => points key/value pairs
 * PRIVATE
 -->
<#function responsiveImageMapAreaData coords>
  <#return blueprintFreemarkerFacade.responsiveImageMapAreaData(coords) />
</#function>
