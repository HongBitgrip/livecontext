<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMAction" -->

<#-- delegate to SearchActionState -->
<#assign substitution=bp.substitute(self.id!"", self)!cm.UNDEFINED />
<@cm.include self=substitution view="asSearchField" />
