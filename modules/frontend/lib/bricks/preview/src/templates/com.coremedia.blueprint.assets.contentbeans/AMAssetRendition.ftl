<#-- @ftlvariable name="self" type="com.coremedia.blueprint.assets.contentbeans.AMAssetRendition" -->

<#-- image -->
<#if self.blob?has_content && bp.isDisplayableImage(self.blob)>
  <img <@cm.metadata data=[self.asset.content, "properties." + self.name]/> src="${cm.getLink(self)}" style="max-width: 100%">

<#-- video -->
<#elseif self.blob?has_content && bp.isDisplayableVideo(self.blob)>
  <video <@cm.metadata data=[self.asset.content, "properties." + self.name]/>
          poster=""
          src="${cm.getLink(self.blob)}"
          style="max-width: 100%"
          controls></video>

<#-- error -->
<#else>
  <span<@cm.metadata data=[self.asset.content, "properties." + self.name] />>${bp.getMessage('preview_am_rendition')}</span>
</#if>
