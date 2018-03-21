<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMCollection" -->

<li<@cm.metadata self.content />>
  <a<@cm.metadata "properties.teaserTitle" />>${self.teaserTitle!""}</a>
  <ul<@cm.metadata "properties.items" />>
    <#list self.items as item>
      <@cm.include self=item view="asLinkListItem" />
    </#list>
  </ul>
</li>