<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMVideo" -->
<#-- @ftlvariable name="additionalClass" type="java.lang.String" -->

<#assign hasPicture=self.picture?has_content />
<#assign additionalClasses=hasPicture?then("cm-squarelist--dimmer", '') />
<#assign videoLink = bp.getVideoLink(self) />

<div class="cm-squarelist ${additionalClasses}"<@cm.metadata self.content />>
  <@bp.optionalLink href="${videoLink}" attr={"data-cm-popup": ""}>
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
        <div class="cm-squarelist__picture cm-image--blank"></div>
      </div>
    </#if>

    <#-- play overlay icon-->
    <@cm.include self=self view="_playButton" params={"blockClass": "cm-squarelist"}/>

    <#-- headline -->
    <#if self.teaserTitle?has_content>
      <h3 class="cm-squarelist__headline"<@cm.metadata "properties.teaserTitle" />>${self.teaserTitle!""}</h3>
    </#if>

    <@cm.hook id=bp.viewHookEventNames.VIEW_HOOK_TEASER />
  </@bp.optionalLink>
</div>
