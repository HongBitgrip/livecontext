<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->

<#assign link=cm.getLink(self.target!cm.UNDEFINED) />
<#assign target=(self.target?has_content && self.target.openInNewTab)?then("_blank", "") />

<div class="cm-gap" data-cm-module="gap"<@cm.metadata self.content />>
  <#-- picture -->
  <@bp.optionalLink href="${link}" attr={"target":target}>
    <#if self.picture?has_content>
      <div class="cm-gap__embed">
        <div class="cm-gap__embed-item">
          <@cm.include self=self.picture params={
            "limitAspectRatios": [],
            "classBox": "cm-gap__picture-box",
            "classImage": "cm-gap__picture",
            "metadata": ["properties.pictures"]
          }/>
        </div>  
      </div>
    <#else>
      <div class="cm-gap__embed-item">
        <div class="cm-gap__picture-box" <@cm.metadata "properties.pictures" />>
          <div class="cm-gap__picture cm-image--missing"></div>
        </div>
      </div>
    </#if>
    <div class="cm-gap__dimmer"></div>
    <#if (self.teaserTitle?has_content || self.teaserText?has_content)>
      <#-- with caption -->
      <div class="cm-gap__caption row">
        <div class="col-xs-10 col-xs-push-1 col-md-8 col-md-push-2">
          <#-- headline -->
          <h2 class="cm-gap__headline"<@cm.metadata "properties.teaserTitle" />>
            <span>${self.teaserTitle!""} 
            <#if link?has_content><i class="cm-gap__arrow"></i></#if>
            </span>
          </h2>
          <#-- teaser text -->
          <p class="cm-gap__text"<@cm.metadata "properties.teaserText" />>
            <@bp.renderWithLineBreaks bp.truncateText(self.teaserText!"", bp.setting(cmpage, "gap.max.length", 140)) />
          </p>
        </div>
      </div>
    </#if>
  </@bp.optionalLink>
</div>
