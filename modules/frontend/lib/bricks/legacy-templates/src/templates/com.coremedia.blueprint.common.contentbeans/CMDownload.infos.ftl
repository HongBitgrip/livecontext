<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMDownload" -->

<#-- display file extension and size of download data -->
<#if self.data?has_content>
  <span<@cm.metadata "properties.data" />>(${cm.getLink(self.target)?keep_after_last(".")?keep_before("?") + ", "} ${bp.getDisplaySize(self.data.size)})</span>
</#if>



