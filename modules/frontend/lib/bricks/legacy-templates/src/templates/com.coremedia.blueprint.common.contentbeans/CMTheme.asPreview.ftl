<@cm.responseHeader name="Content-Type" value="text/html; charset=UTF-8"/>
<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTheme" -->

<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>CoreMedia Theme Preview</title>
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <#-- include css -->
  <#list self.css![] as css>
    <@cm.include self=css view="asCSSLink"/>
  </#list>
  <@preview.previewScripts />
</head>

<body id="top" class="cm-page-preview">

  <#-- simple preview of a theme -->
  <div class="cm-theme"<@cm.metadata self.content />>
    <h1 class="cm-theme__title">${self.content.name!""}</h1>
    <#if self.icon?has_content>
     <img class="cm-theme__screenshot" src="${cm.getLink(self.icon)}"<@cm.metadata "properties.icon" />>
    </#if>
    <div class="cm-theme__description"<@cm.metadata "properties.detailText" />><@cm.include self=self.detailText!cm.UNDEFINED /></div>
  </div>

  <#-- include javascript at bottom for performance -->
  <#list self.javaScriptLibraries![] as js>
    <@cm.include self=js view="asJSLink"/>
  </#list>
  <#list self.javaScripts![] as js>
    <@cm.include self=js view="asJSLink"/>
  </#list>

</body>
</html>
