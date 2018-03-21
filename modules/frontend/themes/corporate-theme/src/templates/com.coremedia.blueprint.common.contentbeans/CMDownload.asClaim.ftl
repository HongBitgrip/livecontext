<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMDownload" -->
<#-- @ftlvariable name="cssClass" type="java.lang.String" -->
<#-- @ftlvariable name="islast" type="java.lang.Boolean" -->

<#assign cssClasses = cm.localParameter("cssClass", "") + cm.localParameter("islast", false)?then(" is-last", "") />
<#assign link=cm.getLink(self.target!cm.UNDEFINED) />

<div class="cm-claim thumbnail ${cssClasses}"<@cm.metadata self.content />>
  <#-- picture -->
  <@bp.optionalLink href="${link}">
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
    <@bp.optionalLink href="${link}">
      <h3 class="cm-claim__headline thumbnail-label"<@cm.metadata "properties.teaserTitle" />>
        <span>
          <#if link?has_content>
              <i class="glyphicon glyphicon-download" aria-hidden="true"></i>
          </#if>
          ${self.teaserTitle!""}
        </span>
      </h3>
    </@bp.optionalLink>
    <#-- teaser text, 3 lines ~ 120 chars -->
    <p class="cm-claim__text"<@cm.metadata "properties.teaserText" />>
      <@cm.include self=self view="infos" /><br>
      <@bp.renderWithLineBreaks bp.truncateText(self.teaserText!"", bp.setting(cmpage, "claim.max.length", 115)) />
    </p>
  </div>
</div>
