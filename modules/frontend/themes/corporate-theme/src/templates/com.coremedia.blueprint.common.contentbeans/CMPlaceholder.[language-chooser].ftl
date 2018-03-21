<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMPlaceholder" -->

<#if (cmpage.content.localizations)?has_content>
<div class="cm-placeholder cm-language-chooser"<@cm.metadata self.content />>
  <label class="sr-only" <@cm.metadata "properties.title"/>>${self.title!""}</label>
  <#-- selected language -->
  <#assign localizations=cmpage.content.localizations![] />
  <#if (localizations?size > 1)>
  <a class="cm-language-chooser__button" title="${cmpage.locale.displayName}" data-toggle="collapse" data-target=".cm-language-chooser__items">
    ${cmpage.locale.language}
    <span class="cm-language-chooser__icon glyphicon glyphicon-triangle-bottom" aria-hidden="true"></span>
  </a>
  <#-- list all others languages -->
  <ul class="cm-language-chooser__items collapse">
    <#list localizations as localization>
      <#-- list all localized variants without self -->
      <#if localization.locale != cmpage.content.locale>
        <li class="cm-language-chooser__item">
          <a href="${cm.getLink(localization)}" title="${localization.locale.displayName}">${localization.locale.language}</a>
        </li>
      </#if>
    </#list>
  </ul>
  <#else>
  <span class="cm-language-chooser__title" title="${cmpage.locale.displayName}">${cmpage.locale.language}</span>
  </#if>
</div>
</#if>
