<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->

<#assign fragmentViews=[
{
"viewName": "asTeaser",
"titleKey": "preview_label_teaser"
}] />

<#assign fragmentedPreviews=bp.previewTypes(cmpage, self, fragmentViews)/>

<@cm.include self=self view="multiViewPreview" params={
  "fragmentViews": fragmentedPreviews
}/>
