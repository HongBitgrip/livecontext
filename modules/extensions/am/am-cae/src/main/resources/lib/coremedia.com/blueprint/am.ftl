<#ftl strip_whitespace=true>
<#-- @ftlvariable name="amFreemarkerFacade" type="com.coremedia.blueprint.assets.cae.tags.AMFreemarkerFacade" -->

<#-- -------------------------------------------------------------------------------------------------------------------
 *
 * Please check the section "Freemarker API" in chapter "Reference" in the frontend manual for details and examples
 * for the following directives.
 * Any changes, additions or removals need to be documented in the manual.
 *
 ------------------------------------------------------------------------------------------------------------------- -->


<#function getDownloadPortal>
  <#return amFreemarkerFacade.getDownloadPortal() />
</#function>

<#function hasDownloadPortal>
  <#return amFreemarkerFacade.hasDownloadPortal() />
</#function>


<#-- --- DEPRECATED ------------------------------------------------------------------------------------------------ -->


<#-- DEPRECATED, UNUSED -->
<#function getAssetType asset>
  <#return (asset.content.type.name)!"" />
</#function>
