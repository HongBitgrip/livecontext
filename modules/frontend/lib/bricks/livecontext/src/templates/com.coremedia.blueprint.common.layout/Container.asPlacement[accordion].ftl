<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.Container" -->

<ul class="cm-collection cm-collection--accordion js-accordion"<@cm.metadata data=bp.getContainerMetadata(self) />>
<#list self.items![] as item>
  <@cm.include self=item view="asAccordionItem" />
</#list>
</ul>