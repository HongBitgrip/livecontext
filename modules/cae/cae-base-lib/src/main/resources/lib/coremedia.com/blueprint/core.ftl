<#ftl strip_whitespace=true>
<#-- @ftlvariable name="blueprintFreemarkerFacade" type="com.coremedia.blueprint.cae.web.taglib.BlueprintFreemarkerFacade" -->
<#-- @ftlvariable name="cmFacade" type="com.coremedia.objectserver.view.freemarker.CAEFreemarkerFacade" -->

<#-- -------------------------------------------------------------------------------------------------------------------
 *
 * Please check the section "Freemarker API" in chapter "Reference" in the frontend manual for details and examples
 * for the following directives.
 * Any changes, additions or removals need to be documented in the manual.
 *
 ------------------------------------------------------------------------------------------------------------------- -->


<#assign viewHookEventNames=blueprintFreemarkerFacade.getViewHookEventNames()/>

<#-- NAVIGATION -->
<#function isActiveNavigation navigation navigationPathList>
    <#return blueprintFreemarkerFacade.isActiveNavigation(navigation, navigationPathList)>
</#function>

<#-- SETTINGSFUNCTION -->
<#function setting self key default=cm.UNDEFINED>
  <#return blueprintFreemarkerFacade.setting(self, key, default)>
</#function>

<#-- GENERATEUNIQUEID -->
<#function generateId prefix="">
  <#return blueprintFreemarkerFacade.generateId(prefix)>
</#function>

<#-- TRUNCATE TEXT -->
<#function truncateText text maxLength>
  <#return blueprintFreemarkerFacade.truncateText(text, maxLength)>
</#function>

<#-- Truncate Text and closes last opened but not closed <b> tag -->
<#function truncateHighlightedText text maxLength>
  <#return blueprintFreemarkerFacade.truncateHighlightedText(text, maxLength)>
</#function>

<#-- GET FRAGMENTS FOR PREVIEW -->
<#function previewTypes page self defaultFragmentViews=[]>
  <#return blueprintFreemarkerFacade.getPreviewViews(self, page, defaultFragmentViews)>
</#function>

<#--
 * todo: CMS-11124 move to bean
 * Builds a link to Video.
 * dataUrl is default, if dataUrl is empty blob-data is used
 * @param CMVideo
 -->
<#function getVideoLink video>
  <#assign videoLink=video.dataUrl!"" />
  <#if !videoLink?has_content && video.data?has_content>
    <#assign videoLink=cm.getLink(video.data)!"" />
  </#if>
  <#return videoLink />
</#function>

<#-- todo: CMS-11122 move to cm -->
<#function substitute id original>
  <#return cmFacade.substitute(id, original)>
</#function>

<#-- ErrorReporterHelper -->
<#function getStackTraceAsString exception>
  <#return blueprintFreemarkerFacade.getStackTraceAsString(exception)>
</#function>

<#function isWebflowRequest>
  <#return blueprintFreemarkerFacade.isWebflowRequest()!false>
</#function>

<#function getDisplaySize size>
  <#return blueprintFreemarkerFacade.getDisplaySize(size) />
</#function>

<#function getDisplayFileFormat mimeType>
  <#return blueprintFreemarkerFacade.getDisplayFileFormat(mimeType) />
</#function>

<#function isDisplayableImage blob>
  <#return blueprintFreemarkerFacade.isDisplayableImage(blob) />
</#function>

<#function isDisplayableVideo blob>
  <#return blueprintFreemarkerFacade.isDisplayableVideo(blob) />
</#function>

<#--
 * Retrieves the URL path that belongs to a theme resource (image, webfont, etc.) defined by its path within the
 * theme folder. The path must not contain any <strong>..</strong>
 * descending path segments.
 *
 * @param path to the resource within the theme folder
 -->
<#function getLinkToThemeResource path>
  <#return blueprintFreemarkerFacade.getLinkToThemeResource(path)>
</#function>


<#-- --- DEPRECATED ------------------------------------------------------------------------------------------------ -->


<#-- DEPRECATED, UNUSED -->
<#function transformations self>
  <#return blueprintFreemarkerFacade.getTransformations(self)>
</#function>

<#-- DEPRECATED -->
<#function createBeanFor content>
  <#return blueprintFreemarkerFacade.createBeanFor(content)>
</#function>

<#-- DEPRECATED -->
<#function createBeansFor contents>
  <#return blueprintFreemarkerFacade.createBeansFor(contents)>
</#function>
