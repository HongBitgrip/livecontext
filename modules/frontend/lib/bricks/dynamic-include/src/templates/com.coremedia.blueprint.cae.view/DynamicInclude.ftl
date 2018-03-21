<@cm.responseHeader name="Content-Type" value="text/html; charset=UTF-8"/><#-- could be used as fragment -->
<#-- @ftlvariable name="self" type="com.coremedia.blueprint.cae.view.DynamicInclude" -->

<#assign isWebflowRequest=bp.isWebflowRequest()/>
<#assign fragmentLink=cm.getLink(self.delegate, "fragment", {
  "targetView": self.view!cm.UNDEFINED,
  "webflow": isWebflowRequest
})/>

<#if (cm.getRequestHeader("Surrogate-Capability")?seq_contains("ESI/1.0"))!false>
  <#-- include ESI fragment -->
  <${'esi'}:include src="${fragmentLink}" onerror="continue"/>
<#else>
  <#-- include AHAH fragment via AJAX -->
  <div class="cm-fragment" data-cm-fragment="${fragmentLink}"></div>
</#if>
