<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->
<#-- @ftlvariable name="additionalClass" type="java.lang.String" -->
<#-- @ftlvariable name="additionalButtonClass" type="java.lang.String" -->
<#-- @ftlvariable name="metadata" type="java.util.List" -->

<#assign additionalClass=cm.localParameter("additionalClass", "") />
<#assign additionalButtonClass=cm.localParameter("additionalButtonClass", "") />
<#assign metadata=cm.localParameter("metadata", []) />

<#assign disabled=bp.setting(self, "callToActionDisabled", false) />
<#assign text=bp.setting(self, "callToActionCustomText", "") />

<div class="cm-cta ${additionalClass}"<@preview.metadata data=metadata+["properties.localSettings"]/>>
  <@cm.include self=self.target!cm.UNDEFINED view="_callToActionButton" params={
    "additionalClass": "cm-cta__button ${additionalButtonClass}",
    "enabled": !disabled,
    "text": text
  } />
</div>
