<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.Page" -->

<body id="top" <@cm.metadata data=bp.setting(cmpage, "sliderMetaData", "")/>>

  <#-- show pagegrid -->
  <@cm.include self=self.pageGrid />

  <#-- include javascript files at the end -->
  <@cm.include self=self view="_bodyEnd" />
</body>
