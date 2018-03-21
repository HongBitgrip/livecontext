<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.layout.Container" -->
<#assign isInSidebar=cm.localParameter("isInSidebar", false) />

<@cm.include self=self view="asPlacement[claim]" params={"viewItemCssClass": "cm-claim--circle", "isInSidebar":isInSidebar} />
