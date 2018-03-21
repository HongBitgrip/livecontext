<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->
<#-- @ftlvariable name="cssClass" type="java.lang.String" -->
<#-- @ftlvariable name="islast" type="java.lang.Boolean" -->

<#assign cssClasses = cm.localParameter("cssClass", "") + cm.localParameter("islast", false)?then(" is-last", "") />
<#assign link=cm.getLink(self.target!cm.UNDEFINED) />
<#assign target=(self.target?has_content && self.target.openInNewTab)?then("_blank", "") />

<div class="cm-claim thumbnail ${cssClasses}"<@cm.metadata self.content />>
  <#-- picture -->
  <@bp.optionalLink href="${link}" attr={"target":target}>
    <#if self.picture?has_content>
      <@cm.include self=self.picture params={
      "limitAspectRatios": [ "portrait_ratio1x1" ],
      "classBox": "cm-claim__picture-box",
      "classImage": "cm-claim__picture",
      "metadata": ["properties.pictures"]
      }/>
    <#else>
      <div class="cm-claim__picture-box">
        <div class="cm-claim__picture cm-image--missing"></div>
      </div>
    </#if>
  </@bp.optionalLink>
  <div class="caption">
    <#-- headline -->
    <@bp.optionalLink href="${link}" attr={"target":target}>
      <h3 class="cm-claim__headline thumbnail-label"<@cm.metadata "properties.teaserTitle" />>
        <span>
          <#if link?has_content>
            <#if link?contains("mailto")>
              <i class="glyphicon glyphicon-envelope" aria-hidden="true"></i>
            <#else>
              <i class="cm-claim__arrow" aria-hidden="true"></i>
            </#if>
          </#if>
          ${self.teaserTitle!""}
        </span>
      </h3>
    </@bp.optionalLink>
    <#-- teaser text, 3 lines ~ 120 chars -->
    <p class="cm-claim__text"<@cm.metadata "properties.teaserText" />>
      <@bp.renderWithLineBreaks bp.truncateText(self.teaserText!"", bp.setting(cmpage, "claim.max.length", 115)) />
    </p>
  </div>
</div>
