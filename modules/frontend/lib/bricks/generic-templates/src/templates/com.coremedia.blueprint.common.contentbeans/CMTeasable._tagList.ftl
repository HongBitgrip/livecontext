<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMTeasable" -->

<#assign tags=self.subjectTaxonomy![] />

<#if (tags?size > 0)>
  <div class="cm-tag">
    <#-- headline -->
    <h3 class="cm-tag__title"><@bp.message key="tags_label"/></h3>

    <#--tags -->
    <ul class="cm-tag__items">
      <#list tags as taxonomy>
        <li class="cm-tag__item">
          <@cm.include self=taxonomy view="asLink"/>
        </li>
      </#list>
    </ul>
  </div>
</#if>
