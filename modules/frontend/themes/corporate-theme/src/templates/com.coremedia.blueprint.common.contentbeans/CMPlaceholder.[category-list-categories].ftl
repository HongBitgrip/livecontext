<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMPlaceholder" -->
<#-- @ftlvariable name="cmpage.content" type="com.coremedia.blueprint.ecommerce.contentbeans.CMCategory" -->

<#assign categories=cmpage.content.subcategories![]/>
<#assign numberOfItems=categories?size />
<#assign itemsPerRow=4 />

<#if (numberOfItems > 0)>
<div class="cm-placeholder cm-placeholder--category"<@cm.metadata self.content />>
  <#-- headline -->
  <h2 class="cm-placeholder__headline"<@cm.metadata "properties.title" />>${self.title}</h2>
  <#-- list of products -->
  <div class="row">
    <#list categories as item>
      <#-- add new row -->
      <@bp.renderNewRow item_index itemsPerRow />
      <#-- render the items as claim teaser -->
      <div class="col-xs-6 col-sm-3">
        <@cm.include self=item view="asSquarelist" />
      </div>
    </#list>
  </div>
</div>
</#if>
