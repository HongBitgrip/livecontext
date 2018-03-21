<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMVideo" -->
<#-- @ftlvariable name="index" type="java.lang.Integer" -->
<#-- @ftlvariable name="additionalClass" type="java.lang.String" -->

<#assign index=cm.localParameters().index!0 />
<#assign hasVideo=self.data?has_content || self.dataUrl?has_content />
<#assign additionalClasses=(cm.localParameters().additionalClass!"") + hasVideo?then(" cm-superhero--video", "") />

<div class="cm-superhero ${additionalClasses!""}" data-cm-module="superhero" <@cm.metadata self.content />>

  <#-- video -->
  <#if hasVideo>
      <@cm.include self=self view="video" params={
        "classVideo": "cm-superhero__video",
        "autoplay":true,
        "loop":true,
        "hideControls":true,
        "muted":true,
        "youtube": "&fs=0&modestbranding=1&rel=0&showinfo=0&autohide=1&iv_load_policy=3",
        "vimeo": "&title=0&byline=0&portrait=0&color=ffffff"
      } />
  </#if>
  <#-- picture -->
  <@bp.responsiveImage self=self.picture!cm.UNDEFINED classPrefix="cm-superhero" background=true/>

  <#if (self.teaserTitle?has_content || self.teaserText?has_content)>
  <#-- with caption -->
    <div class="cm-superhero__caption row">
      <div class="col-xs-10 col-xs-push-1 col-md-8 col-md-push-2">
        <#-- headline -->
        <h1 class="cm-superhero__headline"<@cm.metadata "properties.teaserTitle" />>${self.teaserTitle!""}</h1>
        <#-- teaser text -->
        <p class="cm-superhero__text"<@cm.metadata "properties.teaserText" />>
          <@bp.renderWithLineBreaks bp.truncateText(self.teaserText!"", bp.setting(cmpage, "superhero.max.length", 140)) />
        </p>
      </div>
    </div>
  </#if>
</div>
