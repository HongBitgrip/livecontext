<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->

<#--
This template is reusing the generic templates brick. Therefore all rendering is taking place in e.g. CMTeasable.hero.ftl.
More specific templates exist for:
 - CMDownload: Adding a info about the download type
 - CMGallery
 - CMHTML
 - CMVideo: Adding a play icon
 - CMProductTeaser: Adding price
 - LiveContextExternalChannel

The CoreMedia ViewDispatcher will automatically select the proper template for you.

The following parameters can be set:
 - additionalClass (String): to set a specific CSS class. It automatically defaults to "cm-hero"
-->

<@cm.include self=self view="hero"/>