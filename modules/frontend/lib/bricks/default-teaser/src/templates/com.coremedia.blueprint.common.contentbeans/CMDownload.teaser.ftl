<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMDownload" -->

<#-- same as CMTeasable.teaser.ftl but with a link to the file. -->
<#assign cssClasses = self.teaserText?has_content?then(" is-text", "") + cm.localParameter("islast", false)?then(" is-last", "") />
<#assign blockClass=cm.localParameters().blockClass!"cm-teasable" />
<#assign additionalClass=cm.localParameters().additionalClass!"" />
<#assign link=cm.getLink(self.target!cm.UNDEFINED) />

<#assign renderTeaserText=cm.localParameter("renderTeaserText", true) />
<#assign renderDimmer=cm.localParameter("renderDimmer", true) />
<#assign renderEmptyImage=cm.localParameter("renderEmptyImage", true) />

<div class="${blockClass} ${blockClass}--download ${cssClasses} ${additionalClass}"<@cm.metadata self.content />>
    <div class="${blockClass}__wrapper">
      <@bp.optionalLink href="${link}">
        <#-- picture -->
        <@bp.responsiveImage self=self.picture!cm.UNDEFINED classPrefix=blockClass displayEmptyImage=renderEmptyImage displayDimmer=renderDimmer limitAspectRatios=bp.setting(cmpage.navigation, "default_aspect_ratios_for_teaser", [])/>
      </@bp.optionalLink>
      <div class="${blockClass}__caption">
        <@bp.optionalLink href="${link}">
          <#-- teaser title -->
          <#if self.teaserTitle?has_content>
              <h3 class="${blockClass}__headline" <@cm.metadata "properties.teaserTitle" />>
          <span>
            <#if link?has_content>
                <i class="glyphicon glyphicon-download" aria-hidden="true"></i>
            </#if>
          ${self.teaserTitle!""}</span>
              </h3>
          </#if>
          <#-- teaser text -->
          <#if renderTeaserText && self.teaserText?has_content>
              <p class="${blockClass}__text" <@cm.metadata "properties.teaserText" />>
                <#if self.data?has_content>
                    <span<@cm.metadata "properties.data" />>(${cm.getLink(self)?keep_after_last(".")?keep_before("?") + ", "} ${bp.getDisplaySize(self.data.size)})</span>
                    <br>
                </#if>
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
