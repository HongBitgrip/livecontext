<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMMedia" -->
<#assign classPrefix=cm.localParameters().classPrefix!"cm-details" />
<#assign classSuffix=cm.localParameters().classSuffix!"media" />
<#assign limitAspectRatios=cm.localParameters().limitAspectRatios![] />

<@bp.responsiveImage self=self classPrefix=classPrefix limitAspectRatios=limitAspectRatios classSuffix=classSuffix/>