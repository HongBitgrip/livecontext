<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.Page" -->

<#-- info icon for developer mode -->
<#if cmpage.developerMode>
  <div class="cm-preview-developer-mode" data-cm-developer-mode="true" aria-label="Developer Mode">
    <i class="cm-preview-developer-mode__icon" title="CoreMedia Developer Mode" aria-hidden="true">
      <span>Developer Mode</span>
    </i>
  </div>
  <#-- this js is used for a automatic reload of webrources changes, triggered by the grunt watch task -->
  <script src="//localhost:35729/livereload.js"></script>
</#if>
