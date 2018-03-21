<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->

<div class="cm-gap--mini"<@cm.metadata self.content />>
  <div class="cm-gap--mini__wrapper">
    <#-- picture -->
    <#if self.picture?has_content>
      <@cm.include self=self.picture params={
      "limitAspectRatios": ["landscape_ratio4x1","landscape_ratio5x2","landscape_ratio16x9"],
      "classBox": "cm-gap--mini__picture-box",
      "classImage": "cm-gap--mini__picture",
      "metadata": ["properties.pictures"]
      }/>
      <div class="cm-gap--mini__dimmer"></div>
    <#else>
      <div class="cm-gap--mini__picture-box" <@cm.metadata "properties.pictures" />>
        <div class="cm-gap--mini__picture"></div>
      </div>
    </#if>

    <#-- headline -->
    <#if self.teaserTitle?has_content>
      <#-- with caption -->
      <div class="cm-gap--mini__caption row">
        <div class="col-xs-10 col-xs-push-1">
          <h1 class="cm-gap--mini__headline"<@cm.metadata "properties.teaserTitle" />>${self.teaserTitle!""}</h1>
        </div>
      </div>
    </#if>
  </div>

  <#-- teaser text -->
  <#if self.teaserText?has_content>
    <div class="cm-gap--mini__text cm-richtext"<@cm.metadata "properties.teaserText"/>>
      <@cm.include self=self.teaserText!cm.UNDEFINED />
    </div>
  </#if>
</div>
