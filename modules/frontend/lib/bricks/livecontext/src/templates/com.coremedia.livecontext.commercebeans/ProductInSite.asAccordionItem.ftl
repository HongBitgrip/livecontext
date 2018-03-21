<#-- @ftlvariable name="self" type="com.coremedia.livecontext.commercebeans.ProductInSite" -->

<li class="cm-collection__item">
    <div class="cm-accordion-item">
        <div class="cm-accordion-item__header"><i class="icon-arrow-right"></i><i class="icon-arrow-down"></i><span>${(self.product.name)!""}</span></div>
        <div class="cm-accordion-item__content"><@cm.include self=self /></div>
    </div>
</li>
