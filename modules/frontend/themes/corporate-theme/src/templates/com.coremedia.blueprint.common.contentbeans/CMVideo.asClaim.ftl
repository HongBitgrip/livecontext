<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMVideo" -->
<#-- @ftlvariable name="cssClass" type="java.lang.String" -->
<#-- @ftlvariable name="islast" type="java.lang.Boolean" -->

<#assign cssClasses = cm.localParameter("cssClass", "") + cm.localParameter("islast", false)?then(" is-last", "") />
<#assign videoLink = bp.getVideoLink(self) />

<div class="cm-claim cm-claim--video thumbnail ${cssClasses}"<@cm.metadata self.content />>
  <@bp.optionalLink href="${videoLink}" attr={"data-cm-popup": "", "class":"cm-claim__popup-opener"}>
    <#-- picture -->
    <#if self.picture?has_content>
      <@cm.include self=self.picture params={
      "limitAspectRatios": [ "portrait_ratio1x1" ],
      "classBox": "cm-claim__picture-box",
      "classImage": "cm-claim__picture",
      "metadata": ["properties.pictures"]
      }/>
    <#else>
      <div class="cm-claim__picture-box">
        <div class="cm-claim__picture cm-image--blank"></div>
      </div>
    </#if>

    <#-- play overlay icon-->
    <@cm.include self=self view="_playButton" params={"blockClass": "cm-claim"}/>
  </@bp.optionalLink>

  <div class="caption">
    <#-- headline -->
    <h3 class="cm-claim__headline thumbnail-label"<@cm.metadata "properties.teaserTitle" />>
      <span>
        ${self.teaserTitle!""}
      </span>
    </h3>
    <#-- teaser text, 3 lines ~ 120 chars -->
    <p class="cm-claim__text"<@cm.metadata "properties.teaserText" />>
      <@bp.renderWithLineBreaks bp.truncateText(self.teaserText!"", bp.setting(cmpage, "claim.max.length", 115)) />
    </p>
  </div>
</div>
