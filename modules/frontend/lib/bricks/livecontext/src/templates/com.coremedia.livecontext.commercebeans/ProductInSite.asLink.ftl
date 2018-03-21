<#-- @ftlvariable name="self" type="com.coremedia.livecontext.commercebeans.ProductInSite" -->

<@bp.optionalLink href="${cm.getLink(self)}">${(self.product.name)!""}</@bp.optionalLink>