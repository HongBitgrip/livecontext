<#-- @ftlvariable name="self" type="com.coremedia.livecontext.contentbeans.LiveContextExternalChannel" -->

<#assign cssClasses = self.teaserText?has_content?then(" is-text", "") + cm.localParameter("islast", false)?then(" is-last", "") />
<#assign blockClass=cm.localParameters().blockClass!"cm-teasable" />
<#assign additionalClass=cm.localParameters().additionalClass!"" />
<#assign link=cm.getLink(self.target!cm.UNDEFINED) />
<#assign renderDimmer=cm.localParameter("renderDimmer", true) />

<#assign renderTeaserText=cm.localParameter("renderTeaserText", true) />

<div class="${blockClass} ${blockClass}--livecontext-external-channel ${cssClasses} ${additionalClass}"<@cm.metadata self.content />>
    <div class="${blockClass}__wrapper">
      <@bp.optionalLink href="${link}">
        <#-- picture -->
        <@cm.include self=self view="_picture" params={"renderDimmer": renderDimmer}/>
      </@bp.optionalLink>
      <div class="${blockClass}__caption">
        <@bp.optionalLink href="${link}">
          <#-- teaser title -->
          <#if self.teaserTitle?has_content>
              <h3 class="${blockClass}__headline" <@cm.metadata "properties.teaserTitle" />>
                  <span>${self.teaserTitle!""}</span>
              </h3>
          </#if>
          <#-- teaser text -->
          <#if renderTeaserText && self.teaserText?has_content>
              <p class="${blockClass}__text" <@cm.metadata "properties.teaserText" />>
                <@bp.renderWithLineBreaks bp.truncateText(self.teaserText!"", bp.setting(cmpage, "square.max.length", 115)) />
              </p>
          </#if>
        </@bp.optionalLink>
        <#-- custom call-to-action button -->
        <@cm.include self=self view="_callToAction" params={
          "additionalClass": "${blockClass}__cta"
        }/>
      </div>
    </div>
  <@cm.hook id=bp.viewHookEventNames.VIEW_HOOK_TEASER />
</div>
