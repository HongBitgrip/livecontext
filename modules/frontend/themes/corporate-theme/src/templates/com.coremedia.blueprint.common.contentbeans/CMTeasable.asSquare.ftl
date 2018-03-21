<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->

<#--
This template is reusing the generic templates brick. Therefore all rendering is taking place in e.g. CMTeasable.teaser.ftl.
More specific templates exist for:
 - CMDownload: Adding a info about the download type
 - CMGallery
 - CMHTML
 - CMVideo: Adding a play icon
 - CMProductTeaser: Adding price
 - LiveContextExternalChannel

The CoreMedia ViewDispatcher will automatically select the proper template for you.

The following parameters can be set:
 - blockClass (String): to set a specific CSS class. It automatically defaults to "cm-teasable"
 - additionalClass (String): to add a specific class to the most outer element rendered by the template
 - isLast (boolean): to set the "is-last" CSS class for the last item. It automatically defaults to ""
-->

<#assign isLast=cm.localParameter("islast", false)/>
<@cm.include self=self view="teaser" params={
  "isLast": isLast,
  "blockClass": "cm-square"
}/>
