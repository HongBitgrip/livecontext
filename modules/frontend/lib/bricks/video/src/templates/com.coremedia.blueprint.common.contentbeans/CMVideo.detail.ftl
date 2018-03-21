<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMVideo" -->
<#assign blockClass=cm.localParameters().blockClass!"cm-details" />
<#assign relatedView=cm.localParameters().relatedView!"related" />
<#assign hasVideo=self.data?has_content || self.dataUrl?has_content />

<#assign renderDate=cm.localParameter("renderDate", true) />
<#assign renderTags=cm.localParameter("renderTags", true) />
<#assign renderRelated=cm.localParameter("renderRelated", true) />

<article class="${blockClass} ${blockClass}--video"<@cm.metadata self.content />>

  <#-- title -->
  <h1 class="${blockClass}__headline"<@cm.metadata "properties.title"/>>${self.title!""}</h1>

  <#-- video or picture -->
  <div class="${blockClass}__medias">
    <#if hasVideo>
      <div class="${blockClass}__video-box">
        <@cm.include self=self view="video" params={
          "classVideo": "${blockClass}__video",
          "hideControls": false,
          "autoplay": false,
          "loop": false,
          "muted": false
        } />
      </div>
    <#elseif self.picture?has_content>
      <div class="alert alert-warning alert-dismissible" role="alert">
        <button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        ${bp.getMessage("error_video_not_available")}
      </div>
      <@cm.include self=self.picture params={
        "limitAspectRatios": ["landscape_ratio16x9"],
        "classBox": "${blockClass}__video-box",
        "classImage": "${blockClass}__video"
      }/>
    </#if>
  </div>

  <#-- text -->
  <#if self.detailText?has_content>
    <div class="${blockClass}__text cm-richtext"<@cm.metadata "properties.detailText"/>>
      <@cm.include self=self.detailText!cm.UNDEFINED />
    </div>
  </#if>

  <#-- date -->
  <#if renderDate && self.externallyDisplayedDate?has_content>
    <div class="${blockClass}__date"<@cm.metadata "properties.externallyDisplayedDate"/>>
      <@bp.renderDate self.externallyDisplayedDate.time "${blockClass}__time" />
    </div>
  </#if>

  <#-- tags -->
  <#if renderTags>
    <@cm.include self=self view="_tagList"/>
  </#if>
</article>

<#-- related -->
<#if renderRelated>
  <@cm.include self=self view="_related" params={"relatedView": relatedView}/>
</#if>

<#-- extensions -->
<@cm.hook id=bp.viewHookEventNames.VIEW_HOOK_END />
