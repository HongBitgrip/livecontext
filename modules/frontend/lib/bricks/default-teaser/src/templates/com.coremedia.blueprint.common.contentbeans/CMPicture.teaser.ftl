<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMPicture" -->

<#-- same as CMTeasable.teaser.ftl but without a link. -->
<#assign cssClasses = self.teaserText?has_content?then(" is-text", "") + cm.localParameter("islast", false)?then(" is-last", "") />
<#assign blockClass=cm.localParameters().blockClass!"cm-teasable" />
<#assign additionalClass=cm.localParameters().additionalClass!"" />

<#assign renderTeaserTitle=cm.localParameter("renderTeaserTitle", true) />
<#assign renderTeaserText=cm.localParameter("renderTeaserText", true) />
<#assign renderDimmer=cm.localParameter("renderDimmer", true) />
<#assign renderEmptyImage=cm.localParameter("renderEmptyImage", true) />
<#assign limitAspectRatiosKey="default_aspect_ratios_for_" + cm.localParameter("limitAspectRatiosKey", "teaser") />
<#assign limitAspectRatios=cm.localParameter("limitAspectRatios", bp.setting(cmpage.navigation, limitAspectRatiosKey, [])) />

<div class="${blockClass} ${cssClasses} ${additionalClass}"<@cm.metadata self.content />>
    <div class="${blockClass}__wrapper">
      <#-- picture -->
      <@bp.responsiveImage self=self.picture!cm.UNDEFINED classPrefix=blockClass displayEmptyImage=renderEmptyImage displayDimmer=renderDimmer limitAspectRatios=limitAspectRatios/>
      <#if renderTeaserTitle || renderTeaserText>
        <div class="${blockClass}__caption">
        <#-- teaser title -->
          <#if self.teaserTitle?has_content>
            <h3 class="${blockClass}__headline" <@cm.metadata "properties.teaserTitle" />>
              <span>${self.teaserTitle!""}</span>
            </h3>
          </#if>
          <#-- teaser text -->
          <#if renderTeaserText &&  self.teaserText?has_content>
            <p class="${blockClass}__text" <@cm.metadata "properties.teaserText" />>
              <@bp.renderWithLineBreaks bp.truncateText(self.teaserText!"", bp.setting(cmpage, "square.max.length", 115)) />
            </p>
          </#if>
        </div>
      </#if>
    </div>

  <@cm.hook id=bp.viewHookEventNames.VIEW_HOOK_TEASER />
</div>
