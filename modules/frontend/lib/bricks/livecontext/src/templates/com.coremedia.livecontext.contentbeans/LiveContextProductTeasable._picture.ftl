<#-- @ftlvariable name="self" type="com.coremedia.livecontext.contentbeans.LiveContextProductTeasable" -->
<#assign blockClass=cm.localParameters().blockClass!"cm-teasable" />
<#assign aspectRatiosSuffix=cm.localParameters().aspectRatiosSuffix!"teaser"/>
<#assign renderDimmer=cm.localParameter("renderDimmer", true) />
<#assign renderEmptyImage=cm.localParameter("renderEmptyImage", true) />
<#assign pictureParams={
  "limitAspectRatios": bp.setting(cmpage.navigation, "default_aspect_ratios_for_"+aspectRatiosSuffix, []),
  "classBox": "${blockClass}__picture-box",
  "classImage": "${blockClass}__picture"
} />

<#if self.picture?has_content>
  <@cm.include self=self.picture params=pictureParams + {"metadata": ["properties.pictures"]}/>
<#elseif (self.product?has_content && self.product.catalogPicture?has_content)>
  <@cm.include self=(self.product.catalogPicture)!cm.UNDEFINED params=pictureParams />
<#elseif renderEmptyImage>
  <div class="${blockClass}__picture-box" <@cm.metadata "properties.pictures" />>
      <div class="${blockClass}__picture"></div>
  </div>
</#if>
<#if renderDimmer>
  <div class="${blockClass}__dimmer"></div>
</#if>
