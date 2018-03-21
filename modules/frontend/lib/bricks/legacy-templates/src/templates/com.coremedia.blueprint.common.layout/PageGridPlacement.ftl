<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.PageGridPlacement" -->

<div id="cm-placement-${self.name!""}" class="cm-placement-${self.name!""} width${self.width}"<@cm.metadata data=bp.getPlacementPropertyName(self)!""/>>
  <#-- replace main section with the main content to render -->
  <#if self.name! == "main" && cmpage.detailView>
    <@cm.include self=cmpage.content/>
  <#-- render the placement items -->
  <#else>
    <#list self.items![] as item>
      <@cm.include self=item view="asTeaser"/>
    </#list>
  </#if>
</div>