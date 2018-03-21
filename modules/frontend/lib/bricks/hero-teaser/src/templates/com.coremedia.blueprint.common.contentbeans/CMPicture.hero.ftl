<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->

<#-- same as CMTeasable.hero.ftl but without a link. -->
<#assign blockClass=cm.localParameters().blockClass!"cm-hero" />

<#assign renderTeaserText=cm.localParameter("renderTeaserText", true) />
<#assign renderDimmer=cm.localParameter("renderDimmer", true) />
<#assign renderEmptyImage=cm.localParameter("renderEmptyImage", true) />

<div class="${blockClass} ${blockClass}--picture"<@cm.metadata self.content />>
<#-- picture -->
<@bp.responsiveImage self=self.picture!cm.UNDEFINED classPrefix=blockClass limitAspectRatios=bp.setting(cmpage.navigation, "default_aspect_ratios_for_hero_teaser", [])/>

<#if self.teaserTitle?has_content>
  <#-- with caption -->
  <div class="${blockClass}__caption">
    <#-- headline -->
    <#if self.teaserTitle?has_content>
      <h1 class="${blockClass}__headline"<@cm.metadata "properties.teaserTitle" />>${self.teaserTitle!""}</h1>
    </#if>
    <#-- teaser text -->
    <#if renderTeaserText && self.teaserText?has_content>
      <p class="${blockClass}__text"<@cm.metadata "properties.teaserText" />>
        <@bp.renderWithLineBreaks bp.truncateText(self.teaserText!"", bp.setting(cmpage, blockClass+"-max-length", 140)) />
      </p>
    </#if>
  </div>
</#if>
</div>
