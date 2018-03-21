<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.PageGridPlacement" -->
<#assign renderDiv=cm.localParameters().renderDiv!true />

<#if self.items?has_content || self.name == "main" || self.name == "header">
  <#if renderDiv>
  <div id="cm-placement-${self.name!""}"
       class="cm-placement cm-placement-${self.name!""} col-xs-12 col-md-${self.colspan!1}"<@cm.metadata data=[bp.getPlacementPropertyName(self)!"", lc.fragmentHighlightingMetaData(self)!""]/>>
  </#if>

  <#-- replace main section with the main content to render -->
  <#if self.name! == "main" && cmpage.detailView>
    <@cm.include self=cmpage.content/>
  <#-- do not display the above section if in detailView -->
  <#elseif self.name! == "above" && cmpage.detailView>
    <#-- do nothing -->
  <#-- render the placement items -->
  <#-- sidebar -->
  <#elseif self.name! == "sidebar">
    <#list self.items![] as item>
      <@cm.include self=item view="asTeaser" />
    </#list>
  <#-- default -->
  <#else>
    <@cm.include self=self view="asPlacement" />
  </#if>

  <#if renderDiv!true>
  </div>
  </#if>
</#if>
