<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->

<#assign link=cm.getLink(self.target!cm.UNDEFINED) />
<#assign hasPicture=self.picture?has_content />
<#assign additionalClasses="" />
<#if hasPicture>
  <#assign additionalClasses="cm-squarelist--dimmer" />
</#if>
<#assign target=(self.target?has_content && self.target.openInNewTab)?then("_blank", "") />

<div class="cm-squarelist ${additionalClasses}"<@cm.metadata self.content />>
  <@bp.optionalLink href="${link}" attr={"target":target}>
    <#-- picture -->
    <#if hasPicture>
      <@cm.include self=self.picture params={
        "limitAspectRatios": [ "portrait_ratio1x1" ],
        "classBox": "cm-squarelist__picture-box",
        "classImage": "cm-squarelist__picture",
        "metadata": ["properties.pictures"]
      }/>
    <#else>
      <div class="cm-squarelist__picture-box"<@cm.metadata "properties.pictures" />>
        <div class="cm-squarelist__picture cm-image--missing"></div>
      </div>
    </#if>

    <#-- headline -->
    <#if self.teaserTitle?has_content>
      <h3 class="cm-squarelist__headline"<@cm.metadata "properties.teaserTitle" />>${self.teaserTitle!""}</h3>
    </#if>

    <@cm.hook id=bp.viewHookEventNames.VIEW_HOOK_TEASER />
  </@bp.optionalLink>
</div>
