<#-- @ftlvariable name="self" type="com.coremedia.blueprint.assets.contentbeans.AMAssetRendition" -->

<tr class="am-renditions__item am-rendition"<@cm.metadata data="properties." + self.name />>
  <td class="am-rendition__name">${bp.getMessage("am_rendition_${self.name}")}</td>
  <td class="am-rendition__size">${bp.getDisplaySize(self.size)}</td>
  <td class="am-rendition__type">
    <#if self.mimeType?has_content>
      <span class="am-rendition-type">${(bp.getDisplayFileFormat(self.mimeType!"")!"bin")?upper_case}</span>
    </#if>
  </td>
  <td class="am-rendition__download">
    <@cm.include self=self view="asDownloadLink" />
  </td>
  <td class="am-rendition__control">
    <@cm.include self=self view="asCollectionControl" />
  </td>
</tr>
