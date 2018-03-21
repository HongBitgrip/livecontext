<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->
<#assign relatedView=cm.localParameters().relatedView!"related" />

<#if self.related?has_content>
  <div class="cm-related">
    <#-- headline -->
    <h3 class="cm-related__title"><@bp.message key="related_label"/></h3>
    <#--items -->
    <@cm.include self=bp.getContainer(self.related) view=relatedView />
  </div>
</#if>
