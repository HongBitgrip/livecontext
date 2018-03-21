<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeaser" -->
<#-- @ftlvariable name="additionalClass" type="java.lang.String" -->
<#-- @ftlvariable name="additionalButtonClass" type="java.lang.String" -->
<#-- @ftlvariable name="metadata" type="java.util.List" -->

<#assign additionalClass=cm.localParameter("additionalClass", "") />
<#assign additionalButtonClass=cm.localParameter("additionalButtonClass", "") />
<#assign metadata=cm.localParameter("metadata", []) />

<#assign struct=self.targets!cm.UNDEFINED />

<#if struct?has_content>
  <#assign structList=struct.links!cm.UNDEFINED />

  <#if structList?has_content>
    <div class="cm-cta ${additionalClass}"<@preview.metadata data=metadata+["properties.targets"]/>>
      <#list structList as structListEntry>
        <#assign target=structListEntry.target!cm.UNDEFINED />
        <#assign enabled=structListEntry.callToActionEnabled!false />
        <#assign text=structListEntry.callToActionCustomText!"" />
          <@cm.include self=target view="_callToActionButton" params={
            "additionalClass": "cm-cta__button ${additionalButtonClass}",
            "enabled": enabled,
            "text": text
          } />
      </#list>
    </div>
  </#if>
</#if>