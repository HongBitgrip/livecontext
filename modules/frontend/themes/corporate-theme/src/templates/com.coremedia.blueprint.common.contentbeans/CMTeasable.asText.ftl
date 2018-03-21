<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->
<#-- @ftlvariable name="islast" type="java.lang.Boolean" -->

<#assign cssClasses = cm.localParameter("islast", false)?then(" is-last", "") />
<#assign link=cm.getLink(self.target!cm.UNDEFINED) />
<#assign target=(self.target?has_content && self.target.openInNewTab)?then("_blank", "") />

<div class="cm-text thumbnail ${cssClasses}"<@cm.metadata self.content />>
  <#-- headline -->
  <@bp.optionalLink href="${link}" attr={"target":target}>
    <h3 class="cm-text__headline"<@cm.metadata "properties.teaserTitle" />>
      <span>${self.teaserTitle!""}</span>
    </h3>
  </@bp.optionalLink>
  <#-- teaser text, 9 lines ~ 600 chars -->
  <p class="cm-text__text"<@cm.metadata "properties.teaserText" />>
    <@bp.renderWithLineBreaks bp.truncateText(self.teaserText!"", bp.setting(cmpage, "text.max.length", 600)) />
  </p>
  <#-- custom call-to-action button -->
  <@cm.include self=self view="_callToAction" params={
    "additionalClass": "cm-text__cta",
    "additionalButtonClass": "cm-button cm-button--primary"
  }/>
</div>
