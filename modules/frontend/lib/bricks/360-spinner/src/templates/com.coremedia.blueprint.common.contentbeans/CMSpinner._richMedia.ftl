<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMSpinner" -->

<#assign classPrefix=cm.localParameters().classPrefix!"cm-details" />
<#assign classSuffix=cm.localParameters().classSuffix!"media" />
<#assign limitAspectRatios=cm.localParameters().limitAspectRatios![] />

<@cm.include self=self view="_spinner" params={
  "spinnerCssClass": "cm-spinner__images " + classPrefix+"__"+classSuffix+"-box",
  "limitAspectRatios": limitAspectRatios,
  "imagesCssClass": classPrefix,
  "imagesCssClassSuffix": classSuffix
}/>


