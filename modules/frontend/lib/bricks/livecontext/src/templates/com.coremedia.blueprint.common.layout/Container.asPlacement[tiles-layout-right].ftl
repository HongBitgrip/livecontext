<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.Container" -->

<@cm.include self=bp.getContainerFromBase(self,self.items[0..*5]) view="asGrid" params={"itemsPerRow": 3, "additionalClass": "cm-collection--tiles-right", "addRows": false, "center": false} />
<#if (self.items?size>5) >
  <@cm.include self=bp.getContainerFromBase(self,self.items[5..]) view="asGrid" params={"itemsPerRow": 3, "additionalClass": "cm-collection--tiles-landscape"} />
</#if>