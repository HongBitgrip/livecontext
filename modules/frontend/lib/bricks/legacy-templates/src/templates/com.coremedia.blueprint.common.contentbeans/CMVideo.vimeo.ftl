<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMVideo" -->
<#-- @ftlvariable name="classVideo" type="java.lang.String" -->
<#-- @ftlvariable name="hideControls" type="java.lang.Boolean" -->
<#-- @ftlvariable name="autoplay" type="java.lang.Boolean" -->
<#-- @ftlvariable name="loop" type="java.lang.Boolean" -->
<#-- @ftlvariable name="muted" type="java.lang.Boolean" -->
<#-- @ftlvariable name="vimeo" type="java.lang.String" -->

<#if self.dataUrl?has_content>
  <#assign playerId=bp.generateId("cm-video-") />
  <#-- get vimeo video id -->
  <#assign dataUrl=self.dataUrl?split("/") />
  <#assign length=dataUrl?size />
  <#assign videoId=dataUrl[length - 1] />

  <#-- add supported video settings to url -->
  <#assign video = videoId + "?" +
    autoplay?then('&autoplay=1','') +
    loop?then('&loop=1','') +
    vimeo!"" <#-- see https://developer.vimeo.com/player/embedding for more parameters -->
  />

  <iframe
    id="${playerId}"
    src="//player.vimeo.com/video/${video}&amp;api=1&amp;player_id=${playerId}"
    class="cm-video cm-video--vimeo ${classVideo}"
    frameborder="0" width="100%" height="100%" webkitAllowFullScreen="" mozallowfullscreen="" allowFullScreen=""
    ${muted?then('data-muted="true"','')}
    <@cm.metadata "properties.dataUrl" />>
    <@bp.notification type="warn" text=bp.getMessage("error_iframe_not_available") dismissable=true />
  </iframe>
</#if>
