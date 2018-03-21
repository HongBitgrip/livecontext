<@cm.responseHeader name="Content-Type" value="text/html; charset=UTF-8"/><#-- could be used as fragment -->
<#-- @ftlvariable name="self" type="com.coremedia.livecontext.ecommerce.order.Cart" -->

<#assign labelCart><@bp.message "cart" "" false /></#assign>

<div class="cm-icon cm-icon--cart"
     data-cm-popup-control='{ "button": ".cm-popup-button--cart", "popup": ".cm-popup--cart" }'
     data-cm-cart-control='{ "symbol": ".cm-icon__symbol--badged", "badge": ".cm-icon__symbol--badged__badge", "cart": ".cm-cart" }'
     data-cm-refreshable-fragment='{"url": "${cm.getLink(self, "fragment", {"targetView": "asHeader"})}"}'>
  <a href="${cm.getLink(self)}" title="${labelCart}" class="cm-popup-button cm-popup-button--cart">
    <i class="cm-icon__symbol cm-icon__symbol--badged icon-cart-<#if self.orderItems?has_content>full<#else>empty</#if>"><span class="cm-icon__symbol--badged__badge"><#-- filled automatically by js #--></span></i>
    <span class="cm-icon__info cm-visuallyhidden">${labelCart}</span>
  </a>
  <div class="cm-popup cm-popup--cart">
    <@cm.include self=self />
  </div>
</div>
