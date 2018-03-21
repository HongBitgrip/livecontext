<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMVideo" -->
<#-- @ftlvariable name="classBox" type="java.lang.String" -->
<#-- @ftlvariable name="classImage" type="java.lang.String" -->

<#assign hasVideo=self.data?has_content || self.dataUrl?has_content />

<#if hasVideo>
  <div class="${classBox!""}"<@cm.metadata self.content />>
    <@cm.include self=self view="video" params={
      "classVideo": "cm-details__video",
      "youtube": "&rel=0&showinfo=0&autohide=1&iv_load_policy=3",
      "vimeo": "&title=0&byline=0&portrait=0&color=ffffff"
    } />
  </div>
</#if>