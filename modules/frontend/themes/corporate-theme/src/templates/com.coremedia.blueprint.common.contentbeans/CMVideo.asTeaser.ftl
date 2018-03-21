<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMVideo" -->
<#-- @ftlvariable name="index" type="java.lang.Integer" -->
<#-- @ftlvariable name="additionalClass" type="java.lang.String" -->

<#-- todo: simplify variables with ?then() -->
<#assign index=cm.localParameters().index!0 />
<#assign hasImage=self.picture?has_content />
<#assign hasEvenIndex=(index % 2 == 0) />
<#assign additionalVariantCssClass="" />
<#assign additionalButtonCssClass="cm-button " />
<#assign additionalNoImageCssClass="" />
<#assign additionalImgCssClass=""/>
<#assign additionalTextCssClass=""/>
<#assign videoLink = bp.getVideoLink(self) />

<#if !hasEvenIndex>
  <#assign additionalVariantCssClass="cm-teasable--alternative" />
<#else>
  <#assign additionalButtonCssClass="cm-button--white" />
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

<div class="cm-teasable cm-teasable--video ${additionalVariantCssClass} ${additionalNoImageCssClass} row ${additionalClass!""}"<@cm.metadata self.content />>
<#if hasImage>
  <div class="col-xs-12 ${additionalImgCssClass}">
    <@bp.optionalLink href="${videoLink}" attr={"data-cm-popup": "", "class":"cm-teasable__popup-opener"}>
      <#-- picture -->
      <@cm.include self=self.picture params={
        "limitAspectRatios": [ "portrait_ratio1x1", "landscape_ratio16x9" ],
        "classBox": "cm-teasable__picture-box",
        "classImage": "cm-teasable__picture",
        "metadata": ["properties.pictures"]
      }/>

      <#-- play overlay icon-->
      <@cm.include self=self view="_playButton"/>
    </@bp.optionalLink>
  </div>
</#if>

  <div class="col-xs-12 ${additionalTextCssClass}">
    <div class="cm-teasable__text-content-box">
      <div class="cm-teasable__text-content">
        <#-- add overlay play icon here, if no image is set -->
        <#if !hasImage>
          <@bp.optionalLink href="${videoLink}" attr={"data-cm-popup": "", "class":"cm-teasable__popup-opener"}>
          <#-- play overlay icon-->
          <@cm.include self=self view="_playButton"/>
          </@bp.optionalLink>
        </#if>

        <#-- headline -->
        <h3 class="cm-teasable__headline"<@cm.metadata "properties.teaserTitle" />>
          <span>${self.teaserTitle!""}</span>
        </h3>
        <#-- teaser text -->
        <#assign truncatedTeaserText=bp.truncateText(self.teaserText!"", bp.setting(cmpage, "teaser.max.length", 140)) />
        <#if truncatedTeaserText?has_content>
          <p class="cm-teasable__text"<@cm.metadata "properties.teaserText" />>
            <@bp.renderWithLineBreaks truncatedTeaserText />
          </p>
        </#if>
      </div>
    </div>
  </div>
</div>
