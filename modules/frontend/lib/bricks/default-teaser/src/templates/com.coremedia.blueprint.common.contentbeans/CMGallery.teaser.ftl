<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMGallery" -->

<#assign cssClasses = self.teaserText?has_content?then(" is-text", "") + cm.localParameter("islast", false)?then(" is-last", "") />
<#assign blockClass=cm.localParameters().blockClass!"cm-teasable" />
<#assign additionalClass=cm.localParameters().additionalClass!"" />
<#assign link=cm.getLink(self.target!cm.UNDEFINED) />

<#assign renderTeaserText=cm.localParameter("renderTeaserText", true) />
<#assign renderDimmer=cm.localParameter("renderDimmer", true) />
<#assign renderEmptyImage=cm.localParameter("renderEmptyImage", true) />

<div class="${blockClass}  ${blockClass}--gallery ${cssClasses} ${additionalClass}"<@cm.metadata self.content />>
    <div class="${blockClass}__wrapper">
      <#-- picture -->
      <#assign picture=cm.UNDEFINED />
      <#assign metadata=[] />
      <#if self.picture?has_content>
        <#assign picture=self.picture />
        <#assign metadata=["properties.pictures"]/>
      <#elseif self.items?has_content>
        <#assign picture=self.items[0] />
        <#assign metadata=["properties.items"]/>
      </#if>
      <#if picture?has_content>
        <@bp.optionalLink href="${link}">
          <@cm.include self=picture params={
            "limitAspectRatios": bp.setting(cmpage.navigation, "default_aspect_ratios_for_square_teaser", []),
            "classBox": "${blockClass}__picture-box",
            "classImage": "${blockClass}__picture",
            "metadata": metadata
          }/>
        </@bp.optionalLink>
        <#if renderDimmer>
          <div class="${blockClass}__dimmer"></div>
        </#if>
      <#elseif renderEmptyImage>
        <@bp.optionalLink href="${link}">
          <div class="${blockClass}__picture-box" <@cm.metadata "properties.pictures" />>
              <div class="${blockClass}__picture"></div>
          </div>
        </@bp.optionalLink>
      </#if>

      <div class="${blockClass}__caption">
        <@bp.optionalLink href="${link}">
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
        </@bp.optionalLink>
        <#-- custom call-to-action button -->
        <@cm.include self=self view="_callToAction" params={
          "additionalClass": "${blockClass}__cta"
        }/>
      </div>
    </div>

  <@cm.hook id=bp.viewHookEventNames.VIEW_HOOK_TEASER />
</div>
