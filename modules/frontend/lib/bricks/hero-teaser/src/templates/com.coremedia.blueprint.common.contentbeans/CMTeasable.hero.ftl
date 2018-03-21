<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->

<#assign blockClass=cm.localParameters().blockClass!"cm-hero" />
<#assign link=cm.getLink(self.target!cm.UNDEFINED) />
<#assign target=(self.target?has_content && self.target.openInNewTab)?then("_blank", "") />

<#assign renderTeaserText=cm.localParameter("renderTeaserText", true) />
<#assign renderDimmer=cm.localParameter("renderDimmer", true) />
<#assign renderEmptyImage=cm.localParameter("renderEmptyImage", true) />

<div class="${blockClass}"<@cm.metadata self.content />>
  <@bp.optionalLink href="${link}" attr={"target":target}>
    <#-- picture -->
    <@bp.responsiveImage self=self.picture!cm.UNDEFINED classPrefix=blockClass displayDimmer=renderDimmer displayEmptyImage=renderEmptyImage limitAspectRatios=bp.setting(cmpage.navigation, "default_aspect_ratios_for_hero_teaser", [])/>
  </@bp.optionalLink>
  <#if !self.teaserOverlaySettings.enabled>
    <#-- with caption -->
    <div class="${blockClass}__caption">
      <#-- headline -->
      <#if self.teaserTitle?has_content>
        <@bp.optionalLink href="${link}" attr={"target":target}>
          <h1 class="${blockClass}__headline"<@cm.metadata "properties.teaserTitle" />>${self.teaserTitle!""}</h1>
        </@bp.optionalLink>
      </#if>
      <#-- teaser text -->
      <#if renderTeaserText && self.teaserText?has_content>
        <p class="${blockClass}__text"<@cm.metadata "properties.teaserText" />>
          <@bp.renderWithLineBreaks bp.truncateText(self.teaserText!"", bp.setting(cmpage, blockClass+"-max-length", 140)) />
        </p>
      </#if>
      <#-- custom call-to-action button -->
      <@cm.include self=self view="_callToAction" params={
        "additionalClass": "${blockClass}__cta"
      }/>
    </div>
  <#else>
    <@cm.include self=self view="_teaserOverlay" />
  </#if>
</div>
