<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMVideo" -->
<#-- @ftlvariable name="islast" type="java.lang.Boolean" -->

<#assign cssClasses = cm.localParameter("islast", false)?then(" is-last", "") />
<#assign hasVideo=self.data?has_content || self.dataUrl?has_content />
<#assign videoLink = bp.getVideoLink(self) />

<div class="cm-text thumbnail ${cssClasses}"<@cm.metadata self.content />>
  <#-- headline -->
  <@bp.optionalLink href="${videoLink}" attr={"data-cm-popup": "", "class": "cm-popup-opener"}>
    <h3 class="cm-text__headline"<@cm.metadata "properties.teaserTitle" />>
      <span>${self.teaserTitle!""}</span>
    </h3>
  </@bp.optionalLink>
  <#-- teaser text, 9 lines ~ 600 chars -->
  <p class="cm-text__text"<@cm.metadata "properties.teaserText" />>
    <@bp.renderWithLineBreaks bp.truncateText(self.teaserText!"", bp.setting(cmpage, "text.max.length", 600)) />
  </p>
  <#-- play button -->
  <#if videoLink?has_content>
    <a class="cm-text__cta cm-button cm-button--primary btn" data-cm-popup="" href="${videoLink}">
      ${bp.getMessage("button_video")}
    </a>
  </#if>
</div>
