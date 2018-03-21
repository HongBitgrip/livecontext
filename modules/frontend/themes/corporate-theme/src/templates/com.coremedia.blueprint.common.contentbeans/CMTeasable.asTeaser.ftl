<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->
<#-- @ftlvariable name="index" type="java.lang.Integer" -->
<#-- @ftlvariable name="additionalClass" type="java.lang.String" -->

<#assign index=cm.localParameters().index!0 />
<#assign hasImage=self.picture?has_content />
<#assign hasEvenIndex=(index % 2 == 0) />
<#assign additionalVariantCssClass="" />
<#assign additionalButtonCssClass="cm-button " />
<#assign additionalNoImageCssClass="" />
<#assign additionalImgCssClass=""/>
<#assign additionalTextCssClass=""/>
<#assign link=cm.getLink(self.target!cm.UNDEFINED) />
<#assign target=(self.target?has_content && self.target.openInNewTab)?then("_blank", "") />

<#if !hasEvenIndex>
  <#assign additionalVariantCssClass="cm-teasable--alternative" />
<#else>
  <#assign additionalButtonCssClass=additionalButtonCssClass + "cm-button--white " />
</#if>
<#if !hasImage>
  <#assign additionalNoImageCssClass="cm-teasable--no-image" />
</#if>
<#if !hasEvenIndex && hasImage>
  <#assign additionalImgCssClass="col-sm-6"/>
  <#assign additionalTextCssClass="col-sm-6"/>
</#if>
<#if hasEvenIndex && hasImage>
  <#assign additionalImgCssClass="col-sm-6 col-sm-push-6"/>
  <#assign additionalTextCssClass="col-sm-6 col-sm-pull-6"/>
</#if>

<div class="cm-teasable ${additionalVariantCssClass} ${additionalNoImageCssClass} row ${additionalClass!""}"<@cm.metadata self.content />>
  <#if hasImage>
    <div class="col-xs-12 ${additionalImgCssClass}">
      <@bp.optionalLink href=link attr={"target":target}>
        <#-- picture -->
        <@cm.include self=self.picture params={
          "limitAspectRatios": [ "portrait_ratio1x1", "landscape_ratio16x9" ],
          "classBox": "cm-teasable__picture-box",
          "classImage": "cm-teasable__picture",
          "metadata": ["properties.pictures"]
        }/>
      </@bp.optionalLink>
    </div>
  </#if>
  <div class="col-xs-12 ${additionalTextCssClass}">
    <div class="cm-teasable__text-content-box">
      <div class="cm-teasable__text-content">
        <#-- headline -->
        <@bp.optionalLink href="${link}" attr={"target":target}>
          <h3 class="cm-teasable__headline"<@cm.metadata "properties.teaserTitle" />>
            <span>${self.teaserTitle!""}</span>
          </h3>
        </@bp.optionalLink>
        <#-- teaser text -->
        <#assign truncatedTeaserText=bp.truncateText(self.teaserText!"", bp.setting(cmpage, "teaser.max.length", 140)) />
        <#if truncatedTeaserText?has_content>
          <p class="cm-teasable__text"<@cm.metadata "properties.teaserText" />>
            <@bp.renderWithLineBreaks truncatedTeaserText />
          </p>
        </#if>
        <#-- custom call-to-action button -->
        <@cm.include self=self view="_callToAction" params={
          "additionalClass": "cm-teasable__cta",
          "additionalButtonClass": additionalButtonCssClass}
        />
      </div>
    </div>
  </div>
</div>
