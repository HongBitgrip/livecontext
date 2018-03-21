<#-- @ftlvariable name="self" type="com.coremedia.livecontext.contentbeans.LiveContextExternalChannel" -->
<#assign blockClass=cm.localParameters().blockClass!"cm-teasable" />
<#assign aspectRatiosSuffix=cm.localParameters().aspectRatiosSuffix!"teaser"/>
<#assign renderDimmer=cm.localParameter("renderDimmer", true) />

<#assign pictureParams={
"limitAspectRatios": bp.setting(cmpage.navigation, "default_aspect_ratios_for_"+aspectRatiosSuffix, []),
"classBox": "${blockClass}__picture-box",
"classImage": "${blockClass}__picture"
} />
<#if self.picture?has_content>
  <@cm.include self=self.picture params=pictureParams + {"metadata": ["properties.pictures"]}/>
<#elseif self.category.catalogPicture?has_content>
  <@cm.include self=(self.category.catalogPicture)!cm.UNDEFINED params=pictureParams />
<#else>
<div class="${blockClass}__picture-box" <@cm.metadata "properties.pictures" />>
    <div class="${blockClass}__picture"></div>
</div>
</#if>
<#if renderDimmer>
  <div class="${blockClass}__dimmer"></div>
</#if>