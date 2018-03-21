<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMVideo" -->

<#assign hasVideo=self.data?has_content || self.dataUrl?has_content />
<#assign blockClass=cm.localParameters().blockClass!"cm-teasable" />
<#if hasVideo>
  <div class="${blockClass}__play cm-play-button"></div>
</#if>

