<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->

<#assign blockClass=cm.localParameters().blockClass!"cm-hero" />
<#assign videoLink = bp.getVideoLink(self) />

<#assign renderTeaserText=cm.localParameter("renderTeaserText", true) />
<#assign renderDimmer=cm.localParameter("renderDimmer", true) />
<#assign renderEmptyImage=cm.localParameter("renderEmptyImage", true) />

<div class="${blockClass} ${blockClass}--video"<@cm.metadata self.content />>
  <@bp.optionalLink href="${videoLink}" attr={"data-cm-popup": "", "class":"cm-claim__popup-opener"}>
    <#-- picture -->
    <@bp.responsiveImage self=self.picture!cm.UNDEFINED classPrefix=blockClass displayDimmer=renderDimmer displayEmptyImage=renderEmptyImage limitAspectRatios=bp.setting(cmpage.navigation, "default_aspect_ratios_for_hero_teaser", [])/>

    <#-- play overlay icon-->
    <@cm.include self=self view="_playButton" params={"blockClass": "${blockClass}"}/>

    <div class="${blockClass}__dimmer"></div>
  </@bp.optionalLink>

  <#if self.teaserTitle?has_content>
    <#-- with caption -->
    <div class="${blockClass}__caption">
      <#-- headline -->
      <h1 class="${blockClass}__headline"<@cm.metadata "properties.teaserTitle" />>${self.teaserTitle!""}</h1>
      <#-- teaser text -->
      <#if renderTeaserText && self.teaserText?has_content>
        <p class="${blockClass}__text"<@cm.metadata "properties.teaserText" />>
          <@bp.renderWithLineBreaks bp.truncateText(self.teaserText!"", bp.setting(cmpage, "hero.max.length", 140)) />
        </p>
      </#if>
    </div>
  </#if>
</div>
