<@cm.responseHeader name="Content-Type" value="text/html; charset=UTF-8"/>
<#-- @ftlvariable name="self" type="java.lang.Object" -->
<#assign sliderMetadata=(cmpage?has_content)?then(bp.setting(cmpage, "sliderMetaData", ""), '')/>

<!DOCTYPE html>
<html class="no-js" lang="${bp.getPageLanguageTag(cmpage!self)}" dir="${bp.getPageDirection(cmpage!self)!'ltr'}" <@cm.metadata data=bp.getPageMetadata(cmpage!self)!"" />>

<#if cmpage?has_content>
  <@cm.include self=cmpage view="head"/>
<#else>
  <head>
    <meta charset="UTF-8">
    <title>CoreMedia Studio Preview</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <@preview.previewScripts />
  </head>
</#if>

<body id="top" class="cm-page-preview"<@cm.metadata sliderMetadata!"" />>

<#-- include fragmented preview -->
  <@cm.include self=self view="asPreview"/>

  <#if cmpage?has_content>
    <@cm.include self=cmpage view="_bodyEnd"/>
  </#if>

</body>
</html>
