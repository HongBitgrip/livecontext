<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMVideo" -->
<#-- @ftlvariable name="islast" type="java.lang.Boolean" -->

<#assign cssClasses = self.teaserText?has_content?then(" is-text", "") + cm.localParameter("islast", false)?then(" is-last", "") />
<#assign blockClass=cm.localParameters().blockClass!"cm-teasable" />
<#assign additionalClass=cm.localParameters().additionalClass!"" />
<#assign link=bp.getVideoLink(self) />

<#assign renderTeaserTitle=cm.localParameter("renderTeaserTitle", true) />
<#assign renderTeaserText=cm.localParameter("renderTeaserText", true) />
<#assign renderDimmer=cm.localParameter("renderDimmer", false) />
<#assign renderEmptyImage=cm.localParameter("renderEmptyImage", true) />
<#assign limitAspectRatiosKey="default_aspect_ratios_for_" + cm.localParameter("limitAspectRatiosKey", "teaser") />
<#assign limitAspectRatios=cm.localParameter("limitAspectRatios", bp.setting(cmpage.navigation, limitAspectRatiosKey, [])) />

<div class="${blockClass} ${blockClass}--video  ${cssClasses} ${additionalClass}"<@cm.metadata self.content />>
    <div class="${blockClass}__wrapper">
    <@bp.optionalLink href="${link}" attr={"data-cm-popup": "", "class":"${blockClass}__popup-opener"}>
      <#-- picture -->
      <div class="${blockClass}__video-wrapper">
        <@bp.responsiveImage self=self.picture!cm.UNDEFINED classPrefix=blockClass displayEmptyImage=renderEmptyImage displayDimmer=renderDimmer limitAspectRatios=limitAspectRatios/>
        <#-- play overlay icon-->
        <@cm.include self=self view="_playButton" params={"blockClass": "${blockClass}"}/>
      </div>
      <#if renderTeaserTitle || renderTeaserText>
        <div class="${blockClass}__caption">
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
        </div>
      </#if>
    </@bp.optionalLink>
    </div>
  
  <@cm.hook id=bp.viewHookEventNames.VIEW_HOOK_TEASER />
</div>
