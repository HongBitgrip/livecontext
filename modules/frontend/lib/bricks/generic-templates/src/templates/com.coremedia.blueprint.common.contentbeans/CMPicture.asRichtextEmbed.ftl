<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMPicture" -->
<#-- @ftlvariable name="att_class" type="java.lang.String" -->

<#assign additionalCssClasses=att_class!""/>

<div class="cm-image-box cm-image-box--embedded ${additionalCssClasses}">
  <@cm.include self=self params={
    "limitAspectRatios": [ "landscape_ratio4x3", "landscape_ratio16x9" ],
    "classBox": "cm-image-box__image cm-aspect-ratio-box",
    "classImage": "cm-aspect-ratio-box__content"
  }/>
</div>
