<#ftl encoding="UTF-8">
<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMImageMap" -->
<#-- @ftlvariable name="imageMapId" type="java.lang.String" -->
<#-- @ftlvariable name="quickInfoMainId" type="java.lang.String" -->
<#-- @ftlvariable name="quickInfoIdList" type="java.lang.String" -->

<#-- if imageMapId is not given, generate new id -->
<#assign imageMapId=imageMapId!(bp.generateId("cm-map-")) />
<#assign quickInfoMainId=quickInfoMainId!(imageMapId + "-quickinfo--main") />

<#assign quickInfoIdList=quickInfoIdList?split(",") />

<#assign imageMapAreas=bp.responsiveImageMapAreas(self) />
<#assign overlay=bp.setting(self, "overlay", {}) />

<#--imagemap with areas -->
<#if imageMapAreas?has_content>
  <#list imageMapAreas![] as imageMapArea>
    <#if imageMapArea?has_content>

      <#assign index=imageMapArea?index/>
      <#assign quickInfoId=quickInfoIdList[index]/>

      <#-- ids for next/previous button -->
      <#assign quickInfoNextId=quickInfoIdList[index + 1]!""/>
      <#assign quickInfoPreviousId=quickInfoIdList[index - 1]!""/>
      <#if (quickInfoIdList?size > 1 ) >
        <#if (index == 0)>
          <#assign quickInfoPreviousId=quickInfoIdList?last />
        <#elseif (index == (imageMapAreas?size - 1))>
          <#assign quickInfoNextId=quickInfoIdList?first />
        </#if>
      </#if>

      <#if imageMapArea.linkedContent?has_content>
        <#assign linkedContent=imageMapArea.linkedContent />
      <#-- include quickinfo popup -->
        <@cm.include self=linkedContent view="asQuickInfo" params={
        "classQuickInfo": "cm-imagemap__quickinfo cm-quickinfo--imagemap",
        "metadata": ["properties.localSettings"],
        "quickInfoId": quickInfoId,
        "quickInfoNextId": quickInfoNextId,
        "quickInfoPreviousId": quickInfoPreviousId,
        "quickInfoGroup": imageMapId,
        "overlay": overlay
        } />
      </#if><#-- imageMapArea.linkedContent?has_content -->
    </#if>
  </#list>
</#if>
