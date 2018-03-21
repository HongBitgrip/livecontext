<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMImageMap" -->

<#--
    Template Description:

    This template extends the brick "hero-teaser".
-->

<#assign blockClass=cm.localParameters().blockClass!"cm-hero" />
<#assign link=cm.getLink(self.target!cm.UNDEFINED) />

<#assign renderTeaserText=cm.localParameter("renderTeaserText", true) />
<#assign renderDimmer=cm.localParameter("renderDimmer", true) />
<#assign renderEmptyImage=cm.localParameter("renderEmptyImage", true) />
<#assign imageMapParams=bp.initializeImageMap()/>
<#assign useQuickinfo=cm.localParameter("imagemap_use_quickinfo", true)/>

<div class="${blockClass} ${blockClass}--imagemap cm-imagemap"<@preview.metadata self.content />
     data-cm-imagemap='{"coordsBaseWidth": "${bp.IMAGE_TRANSFORMATION_BASE_WIDTH}", "defaultLink": "${cm.getLink(self.target!cm.UNDEFINED)}"}'>

  <#-- picture + hot zones -->
  <@cm.include self=self view="_picture" params={
    "blockClass": blockClass,
    "renderDimmer": renderDimmer,
    "renderEmptyImage": renderEmptyImage,
    "limitAspectRatios": bp.setting(cmpage.navigation, "default_aspect_ratios_for_hero_teaser", []),
    "useQuickinfo": useQuickinfo} +
    imageMapParams
  />

  <#if !self.teaserOverlaySettings.enabled>
    <#-- with caption -->
    <div class="${blockClass}__caption cm-imagemap__caption row">
      <#-- headline -->
      <#if self.teaserTitle?has_content>
        <@bp.optionalLink href="${link}">
          <h1 class="${blockClass}__headline"<@preview.metadata "properties.teaserTitle" />>${self.teaserTitle!""}</h1>
        </@bp.optionalLink>
      </#if>
      <#-- teaser text -->
      <#if renderTeaserText && self.teaserText?has_content>
        <p class="${blockClass}__text"<@preview.metadata "properties.teaserText" />>
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

  <#--include imagemap quick icons-->
  <#if useQuickinfo>
    <@cm.include self=self view="_areasQuickInfo" params=imageMapParams/>
  </#if>
</div>
