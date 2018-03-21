<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->

<#assign blockClass=cm.localParameters().blockClass!"cm-teasable" />
<#assign additionalClass=cm.localParameters().additionalClass!"" />
<#assign cssClasses=self.teaserText?has_content?then(" is-text", "") + cm.localParameter("islast", false)?then(" is-last", "") />
<#assign link=cm.getLink(self.target!cm.UNDEFINED) />
<#assign target=(self.target?has_content && self.target.openInNewTab)?then("_blank", "_self") />

<#assign renderTeaserTitle=cm.localParameter("renderTeaserTitle", true) />
<#assign renderTeaserText=cm.localParameter("renderTeaserText", true) />
<#assign renderDimmer=cm.localParameter("renderDimmer", true) />
<#assign renderEmptyImage=cm.localParameter("renderEmptyImage", true) />
<#assign limitAspectRatiosKey="default_aspect_ratios_for_" + cm.localParameter("limitAspectRatiosKey", "teaser") />
<#assign limitAspectRatios=cm.localParameter("limitAspectRatios", bp.setting(cmpage.navigation, limitAspectRatiosKey, [])) />

<div class="${blockClass} ${cssClasses} ${additionalClass}"<@cm.metadata self.content />>
  <div class="${blockClass}__wrapper">
    <@bp.optionalLink href="${link}" attr={"target":target}>
    <#-- picture -->
      <@bp.responsiveImage self=self.picture!cm.UNDEFINED classPrefix=blockClass displayEmptyImage=renderEmptyImage displayDimmer=renderDimmer limitAspectRatios=limitAspectRatios/>
    </@bp.optionalLink>
    <#if !self.teaserOverlaySettings.enabled>
      <#if renderTeaserTitle || renderTeaserText>
        <div class="${blockClass}__caption">

          <#-- teaser title -->
          <#if self.teaserTitle?has_content>
            <@bp.optionalLink href="${link}" attr={"target":target}>
              <h3 class="${blockClass}__headline" <@cm.metadata "properties.teaserTitle" />>
                <span>${self.teaserTitle!""}</span>
              </h3>
            </@bp.optionalLink>
          </#if>

          <#-- teaser text -->
          <#if renderTeaserText &&  self.teaserText?has_content>
            <p class="${blockClass}__text" <@cm.metadata "properties.teaserText" />>
              <@bp.renderWithLineBreaks bp.truncateText(self.teaserText!"", bp.setting(cmpage, "square.max.length", 115)) />
            </p>
          </#if>

         <#-- custom call-to-action button -->
         <@cm.include self=self view="_callToAction" params={
           "additionalClass": "${blockClass}__cta"
         }/>
        </div>
      </#if>
    <#else>
      <@cm.include self=self view="_teaserOverlay"/>
    </#if>
  </div>

  <#-- extensions -->
  <@cm.hook id=bp.viewHookEventNames.VIEW_HOOK_TEASER />
</div>
