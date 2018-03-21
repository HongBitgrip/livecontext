<#-- @ftlvariable name="self" type="com.coremedia.livecontext.ecommerce.order.Cart" -->
<#-- @ftlvariable name="_CSRFToken" type="java.lang.String" -->

<div class="cm-cart" data-cm-cart='{"token": "${_CSRFToken!""}", "itemCount": ${self.totalQuantity}}'>
  <div class="cm-cart__arrow"></div>
  <div class="cm-cart__header cm-button-group cm-button-group--equal">
    <#-- close cart preview -->
    <button class="cm-cart__close cm-icon cm-popup-button cm-popup-button--cart" type="button"
            data-cm-popup-control='{ "button": ".cm-popup-button--cart", "popup": ".cm-popup--cart" }'>
      <i class="cm-icon__symbol icon-close"><span class="cm-visuallyhidden"><@bp.message "cart_close"/></span></i>
    </button>
  </div>

<#-- list all items in cart -->
<#if self.orderItems?size gt 0>
  <div class="cm-cart__list">
  <#list self.orderItems![] as item>
      <#assign productInSite=lc.createProductInSite(item.product)/>
      <div class="cm-cart__item cm-cart-item">
        <#if item.product?has_content>
          <#if item.product.currency?has_content && item.product.locale?has_content>
            <#if item.unitPrice?has_content>
              <#assign unitPriceFormatted=lc.formatPrice(item.unitPrice, item.product.currency, item.product.locale)/>
            </#if>
            <#if item.price?has_content>
              <#assign totalPriceFormatted=lc.formatPrice(item.price, item.product.currency, item.product.locale)/>
            </#if>
          </#if>

          <#-- product image -->
          <#if item.product.defaultImageUrl?has_content>
              <div class="cm-cart-item__image cm-aspect-ratio-box">
                  <a href="${cm.getLink(productInSite)}">
                      <img class="cm-aspect-ratio-box__content"
                        <@cm.dataAttribute name="data-cm-non-adaptive-content" data={"overflow": true} />
                           src="${item.product.defaultImageUrl}" alt="${item.product.defaultImageAlt!""}">
                  </a>
              </div>
          </#if>

          <#-- product details -->
          <div class="cm-cart-item__properties">
              <#-- remove from cart button -->
              <div class="cm-icon cm-icon--cart-remove-item cm-icon--nospace"
                   title="<@bp.message key="cart_remove_item" highlightErrors=false />"
                   data-cm-cart-remove-item='{
                      "id": "${item.externalId!""}",
                      "link": "${cm.getLink(self, "ajax")}",
                      "cart": ".cm-cart",
                      "item": ".cm-cart__item",
                      "quantity": "${item.quantity}"}'>
                  <i class="cm-icon__symbol icon-cancel-circle"></i>
                  <span class="cm-icon__info cm-visuallyhidden"><@bp.message "cart_remove_item" /></span>
              </div>

              <div class="cm-property cm-property--title">
                  <div class="cm-property__name cm-visuallyhidden"><@bp.message "cart_product" />:</div>
                  <div class="cm-property__value">
                      <a href="${cm.getLink(productInSite)}">${item.product.name!""}</a>
                  </div>
              </div>
              <div class="cm-property cm-property--quantity">
                  <div class="cm-property__name"><@bp.message "cart_quantity" />:</div>
                  <div class="cm-property__value">${item.quantity!0}</div>
              </div>
              <div class="cm-property cm-property--price">
                  <div class="cm-property__name"><@bp.message "cart_price" />:</div>
                  <div class="cm-property__value">${totalPriceFormatted!""}</div>
              </div>
              <div class="cm-property cm-property--description">
                  <div class="cm-property__name"><@bp.message "cart_description" />:</div>
                  <div class="cm-property__value"><@cm.include self=item.product.shortDescription/></div>
              </div>
          </div>
        </#if>
      </div>
  </#list>
  </div>

<#-- cart is empty -->
<#else>
    <div class="cm-cart__empty">
      <@bp.message "cart_empty" />
    </div>
</#if>

    <div class="cm-cart__footer cm-button-group cm-button-group--equal">
    <@bp.button href=cm.getLink(self)
                text=bp.getMessage("cart_go_to_cart")
                attr={"classes": ["cm-button-group__button cm-button--primary"]} />
    </div>
</div>
