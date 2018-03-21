<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMSpinner" -->
<#-- @ftlvariable name="limitAspectRatios" type="java.util.List" -->
<#-- @ftlvariable name="classBox" type="java.lang.String" -->

<#assign ownPictureCssClass="" />
<#assign spinnerId = bp.generateId("spinner") />

<#assign limitAspectRatiosKey="default_aspect_ratios_for_" + cm.localParameter("limitAspectRatiosKey", "teaser") />
<#assign limitAspectRatios=cm.localParameter("limitAspectRatios", bp.setting(cmpage.navigation, limitAspectRatiosKey, [])) />

<div class="cm-lightbox cm-lightbox--inline ${classBox}" data-cm-popup-class="cm-spinner--popup ${classBox}" <@cm.metadata self.content />>
  <#-- inline -->
  <#if (self.sequence![])?size gt 2>
    <div class="cm-teaser cm-teaser--spinner cm-spinner">
      <a href="#${spinnerId}" title="${self.title!""}">
        <@cm.include self=self view="_spinner" params={"id": spinnerId, "imagesCssClass": "cm-product-assets", "spinnerCssClass": "cm-spinner__images cm-product-assets__picture-box", "limitAspectRatios": limitAspectRatios}/>
      </a>
    </div>
  </#if>
</div>
