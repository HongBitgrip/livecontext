<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMPlaceholder" -->

<#assign substitution=bp.substitute(self.id!"", cmpage)!cm.UNDEFINED />
<@cm.include self=substitution view="listing" />
