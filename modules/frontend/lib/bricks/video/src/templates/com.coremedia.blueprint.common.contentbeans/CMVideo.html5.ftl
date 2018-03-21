<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMVideo" -->
<#-- @ftlvariable name="classVideo" type="java.lang.String" -->
<#-- @ftlvariable name="hideControls" type="java.lang.Boolean" -->
<#-- @ftlvariable name="autoplay" type="java.lang.Boolean" -->
<#-- @ftlvariable name="loop" type="java.lang.Boolean" -->
<#-- @ftlvariable name="muted" type="java.lang.Boolean" -->
<#-- @ftlvariable name="adaptive" type="java.lang.Boolean" -->

<#if self.data?has_content || self.dataUrl?has_content>
  <#assign videoLink=bp.getVideoLink(self) />

  <video
    src="${videoLink}"
    class="cm-video cm-video--html5 ${classVideo!""}"
    poster="" <#-- leave poster empty, first frame will be used as poster -->
    <#if !hideControls>controls="controls"</#if>
    <#if autoplay>autoplay="autoplay"</#if>
    <#if loop>loop="loop"</#if>
    <#if muted>muted="muted"</#if>
    <#if adaptive><@cm.dataAttribute name="data-cm-non-adaptive-content" data={"overflow": false} /></#if>
    <@cm.metadata "properties.data"/>>
      <@bp.notification type="warn" text=bp.getMessage("error_video_tag_not_supported") dismissable=true />
  </video>
</#if>
