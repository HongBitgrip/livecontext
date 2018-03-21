<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.Container" -->
<#-- @ftlvariable name="index" type="java.lang.Integer" -->

<#-- check setting (linklist) on page, if this placement should display the first element as header (gap) -->
<#assign withGap=bp.setting(self, "firstItemAsHeader", false) />
<#assign index=cm.localParameters().index!0 />

<#-- display first element as Gap -->
<#assign items=self.flattenedItems />
<#if withGap && items?has_content>
  <@cm.include self=bp.getContainerFromBase(self, [items?first]) view="asGap" />
  <@cm.include self=bp.getContainerFromBase(self, items[1..]) view="asPlacement" params={"index": index} />
<#else>
  <@cm.include self=self view="asPlacement" params={"index": index} />
</#if>
