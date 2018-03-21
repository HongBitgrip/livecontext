<#-- @ftlvariable name="self" type="com.coremedia.blueprint.common.contentbeans.CMPicture" -->

<div<@cm.metadata self.content />>
<#if self.data?has_content>
  <#-- display the image uncropped in original size -->
  <#if self.disableCropping>
    <#-- id may not be generated using bp.generateId, as persisting toggle state in local storage will not work -->
    <#assign toggleId="toggle-0-details" />
    <div class="toggle-item cm-preview-item" data-id="${toggleId}">
      <a href="#" class="toggle-button cm-preview-item__headline">
        <@bp.message "preview_image_originalSize" />
      </a>
      <div class="toggle-container cm-preview-item__container">
        <div class="cm-image-box cm-image-box--details"<@cm.metadata self.content/>>
        <#-- image -->
          <@cm.include self=self params={
          "limitAspectRatios": limitAspectRatios![],
          "classBox": "cm-image-box__image"
          }/>

          <div class="cm-image-box__infos">
          <#-- copyright -->
            <#if self.copyright?has_content>
              <div<@cm.metadata "properties.copyright"/> class="cm-image-box__copyright">${self.copyright}</div>
            </#if>
          <#-- description -->
            <#if self.detailText?has_content>
              <div<@cm.metadata "properties.detailText"/> class="cm-image-box__description"><@cm.include self=self.detailText /></div>
            </#if>
          </div>
        </div>
      </div>
    </div>

  <#else>
    <#-- image as teaser -->
    <#-- id may not be generated using bp.generateId, as persisting toggle state in local storage will not work -->
    <#assign toggleId="toggle-1-asTeaser" />
    <div class="toggle-item cm-preview-item" data-id="${toggleId}">
      <a href="#" class="toggle-button cm-preview-item__headline">
        <@bp.message "preview_image_teaser" />
      </a>
      <div class="toggle-container cm-preview-item__container">
        <div class="cm-preview-content cm-clearfix">
          <div class="content">
            <@cm.include self=self view="asTeaser" />
          </div>
        </div>
      </div>
    </div>
    <#-- display every aspect ratio as preview item -->
    <#assign allAspectRatios=bp.setting(cmpage, "responsiveImageSettings") />
    <#list allAspectRatios?keys as ratio>
      <#-- id may not be generated using bp.generateId, as persisting toggle state in local storage will not work -->
      <#assign toggleId="toggle-" + (ratio_index + 2) + "-crop-" + ratio />
      <div class="toggle-item cm-preview-item" data-id="${toggleId}">
        <a href="#" class="toggle-button cm-preview-item__headline toggle-off">
          <@bp.message "preview_image_"+ratio />
        </a>
        <div class="toggle-container cm-preview-item__container toggle-container-off">
          <@cm.include self=self params={
            "limitAspectRatios": [ratio],
            "crop": ratio,
            "classBox": "cm-preview__image"
          }/>
        </div>
      </div>
    </#list>
  </#if>
</#if>
</div>
