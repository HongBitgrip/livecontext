<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMAction" -->
<#-- this template is called by IBM's CheckoutLogon.jsp -->

<#assign substitution=bp.substitute(self.id, self)!cm.UNDEFINED />
<@cm.include self=substitution view="asFragment" />
