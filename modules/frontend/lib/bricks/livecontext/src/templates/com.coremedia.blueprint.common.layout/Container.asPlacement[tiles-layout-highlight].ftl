<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.Container" -->

<@cm.include self=bp.getContainerFromBase(self,self.items[0..*2]) view="asGrid" params={"itemsPerRow": 2, "additionalClass": "cm-collection--tiles-highlight"} />
<#if (self.items?size>2) >
  <@cm.include self=bp.getContainerFromBase(self,self.items[2..]) view="asGrid" params={"itemsPerRow": 3, "additionalClass": "cm-collection--tiles-landscape"} />
</#if>
