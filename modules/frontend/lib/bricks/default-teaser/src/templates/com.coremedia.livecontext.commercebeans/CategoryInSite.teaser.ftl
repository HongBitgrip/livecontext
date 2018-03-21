<#-- @ftlvariable name="self" type="com.coremedia.livecontext.commercebeans.CategoryInSite" -->
<#-- @ftlvariable name="metadata" type="java.util.List" -->

<#assign cssClasses = cm.localParameter("islast", false)?then(" is-last", "") />
<#assign blockClass=cm.localParameters().blockClass!"cm-teasable" />
<#assign additionalClass=cm.localParameters().additionalClass!"" />
<#assign link=cm.getLink(self) />

<div class="${blockClass} ${blockClass}--category ${additionalClass}"<@cm.metadata metadata![] />>
  <div class="${blockClass}__wrapper">
    <@bp.optionalLink href="${link}">
      <#-- picture -->
      <div class="${blockClass}__caption">
        <@cm.include self=self.category view="_picture" params={"blockClass":blockClass}/>
          <h3 class="${blockClass}__headline">
            <span>${(self.category.name)!""}</span>
          </h3>
      </div>
    </@bp.optionalLink>
  </div>
</div>
