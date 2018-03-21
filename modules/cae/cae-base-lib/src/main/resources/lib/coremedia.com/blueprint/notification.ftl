<#ftl strip_whitespace=true>
<#import "util.ftl" as util>

<#-- -------------------------------------------------------------------------------------------------------------------
 *
 * Please check the section "Freemarker API" in chapter "Reference" in the frontend manual for details and examples
 * for the following directives.
 * Any changes, additions or removals need to be documented in the manual.
 *
 ------------------------------------------------------------------------------------------------------------------- -->


<#--
 * Renders a notification element
 *
 * @param type the type of the notification
 * @param baseClass (optional) base class of the notification, the type will be added to the notification as an additional
 *                         class relying on the base class (e.g. baseclass--error)
 * @param additionalClasses (optional) sequence of additional css classes to be attached to container element
 * @param title (optional) title to display
 * @param text (optional) plain text to show (html will be escaped)
 * @param attr (optional) additional attributes for notification tag
 * @param dismissable (optional) specifies if the dismiss button is shown, default is false
 * @param iconClass (optional) set icon with given css class
 * @nested (optional) nested content will be rendered between the text and dismiss output
 -->
<#macro notification type baseClass="cm-notification" additionalClasses=[] title="" text="" dismissable=false iconClass="" attr={}>
  <#local classes=[baseClass, baseClass + "--" + type] + additionalClasses />
  <#local attr=util._extendSequenceInMap(attr, "classes", classes) />
  <div<@util.renderAttr attr=attr />>
    <#if iconClass?has_content>
      <i class="${iconClass}" aria-hidden="true"></i>
    </#if>
    <#if title?has_content>
      <span class="${baseClass}__headline">${title}</span>
    </#if>
    <span class="${baseClass}__text">
    <#if text?has_content>${text}</#if>
    <#nested />
    </span>
    <#if dismissable?is_boolean && dismissable>
      <div class="${baseClass}__dismiss"></div>
    </#if>
  </div>
</#macro>

<#--
 * Renders a notification element associated to spring forms.
 * The text will automatically be determinated.
 *
 * @param path the name of the field to bind to
 * @param dismissable (optional) @see notification#dismissable
 * @param class @see notification#baseClass
 * @param class @see notification#additionalClasses
 * @param ignoreIfEmpty (optional) specifies if the notification will not be rendered if spring error messages are empty
 * @param type (optional) @see notification#type, for spring notifications the default is "error"
 * @param title (optional) @see notification#title
 * @param bindPath (optional) false prevents the rebinding of the path, e.g. if you already know that the path is bound
 * @param attr (optional) @see notification#attr
 * @nested (optional) nested content will be rendered between the text and dismiss output
 * todo: CMS-11137 move to elasticsocial
 -->
<#outputformat "plainText">
<#macro notificationFromSpring path dismissable=false baseClass="cm-notification" additionalClasses=[] ignoreIfEmpty=true type="error" title="" bindPath=true attr={}>
  <#if bindPath><@spring.bind path=path /></#if>
  <#local text="" />
  <#if spring.status.error>
    <#local text=spring.status.getErrorMessagesAsString("\n") />
  </#if>
  <#if !ignoreIfEmpty?is_boolean || !ignoreIfEmpty || text?has_content>
    <@notification type=type dismissable=dismissable baseClass=baseClass additionalClasses=additionalClasses title=title attr=attr>${text?replace("\n", "<br>")}<#nested /></@notification>
  </#if>
</#macro>
</#outputformat>
