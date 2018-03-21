<#ftl strip_whitespace=true>

<#-- -------------------------------------------------------------------------------------------------------------------
 *
 * Please check the section "Freemarker API" in chapter "Reference" in the frontend manual for details and examples
 * for the following directives.
 * Any changes, additions or removals need to be documented in the manual.
 *
 ------------------------------------------------------------------------------------------------------------------- -->


<#--
 * Renders a map of attributes as html attribute, e.g.:
 * map: {"class": "button", "id": "button1"} will be rendered as: class="button" id="button1"
 *
 * There are reserved keys in attr:
 * "metadata": if metadata shall be rendered it contains the value of param data for cm.metadata
 * "classes": contains a sequence of classes attached to class attribute. If there are classes set in "class" attribute
 * these classes will be merged with classes delivered in "classes"
 *
 * @param attr the map of attributes
 * @param ignore a list of keys that will be ignored
 -->
<#macro renderAttr attr={} ignore=[]>
  <#if attr?keys?seq_contains("classes") && !ignore?seq_contains("classes")>
    <#local classes=attr["classes"] />
    <#if attr?keys?seq_contains("class")>
      <#local classes=classes + attr["class"]?replace("  ", " ")?split(" ") />
    </#if>
    <#local attr=attr + {"class": classes?join(" ")} />
  </#if>
  <#local ignore=ignore + ["classes"] />
  <#list attr?keys as key><#if !ignore?seq_contains(key)><#assign value=attr[key]/><#if key=="metadata"><@cm.metadata data=value /><#elseif key?contains("data-")><@cm.dataAttribute name=key data=value/><#elseif value?has_content> ${key}="${value}"</#if></#if></#list>
</#macro>

<#--
 * Renders a <time/> element  in locale format
 * See http://freemarker.org/docs/ref_builtins_date.html#ref_builtin_string_for_date
 *
 * @param contentDate @see java.util.Date
 * @param cssClass (optional) string
 -->
<#macro renderDate contentDate cssClass="">
  <#if contentDate?has_content>
    <time class="${cssClass}" datetime="${contentDate?datetime?string.iso}"<@cm.metadata "properties.externallyDisplayedDate" />>${contentDate?date?string.medium}</time>
  </#if>
</#macro>

<#--
 * Translates a message key into a localized message
 * Use message instead of spring.message/spring.messageArgs to avoid rendering of exceptions
 *
 * @param key @see spring.message#key
 * @param (optional) args @see spring.messageArgs#args
 * @param (optional) highlightErrors specifies if errors should be highlighted, default: true (for macro variant)
 -->
<#outputformat "plainText">
<#macro message key args=[] highlightErrors=true>
<#compress>
  <#if key?has_content>
    <#local messageText><@spring.messageText key key/></#local>
    <#if messageText?has_content && messageText != key>
      <#if args?has_content>
        <#local messageText><@spring.messageArgs key args/></#local>
      </#if>
      ${messageText}
    <#elseif ((cmpage.developerMode)!false) && highlightErrors>
      <span title="key '${key}' is missing" class="cm-preview-missing-key">[---${key}---]</span>
    <#else>
      [---${key}---]
    </#if>
  </#if>
</#compress>
</#macro>
</#outputformat>

<#--
 * Translates a message key into a localized message
 * Use getMessage instead of spring.message/spring.messageArgs to avoid rendering of exceptions
 *
 * @param key @see spring.message#key
 * @param args @see spring.messageArgs#args
 * @param highlightErrors specifies if errors should be highlighted, default: false (function variant)
 -->
<#outputformat "plainText">
<#function getMessage key args=[] highlightErrors=false>
  <#local result><@message key=key args=args highlightErrors=highlightErrors /></#local>
  <#return result />
</#function>
</#outputformat>

<#--
  * Checks if a translation for the given key exists
  *
  * @param key @see spring.message#key
  -->
<#function hasMessage key>
  <#local messageText><@spring.messageText key ""/></#local>
  <#return messageText?has_content />
</#function>

<#--
 * Renders nested content inside a link if href is not empty.
 *
 * @param href The href attribute of the link
 * @param attr (optional) additional attributes for link tag
 * @param render (optional) Setting this parameter to false will skip rendering the link. DEPRECATED: provide an empty href instead.
 * @nested (optional) nested content will be rendered inside the link
 -->
<#macro optionalLink href attr={} render=true>
  <#if render && href?has_content><a href="${href}"<@renderAttr attr />></#if>
  <#nested>
  <#if href?has_content></a></#if>
</#macro>

<#--
 * Renders nested content inside a frame if its title is not empty.
 *
 * @param (optional) title The title of the frame
 * @param (optional) classFrame additional CSS class name for the container representing the frame
 * @param (optional) attr additional attributes for the container representing the frame
 * @param (optional) attrTitle additional attributes for the headline representing the frame title
 * @nested (optional) nested content will be rendered inside the frame
 -->
<#macro optionalFrame title="" classFrame="" attr={} attrTitle={}>
  <#if title?has_content>
    <div class="cm-frame ${classFrame}"<@renderAttr attr />>
      <h2 class="cm-frame__title"<@renderAttr attrTitle />>${title}</h2>
  </#if>
    <#nested>
  <#if title?has_content>
    </div>
  </#if>
</#macro>

<#--
 * Renders given text with line breaks as <br>
 *
 * @param text String with line breaks
 -->
<#macro renderWithLineBreaks text>
  <#noautoesc>
    ${text?trim?replace("\n\n", "<br>")?replace("\n", "<br>")}
  </#noautoesc>
</#macro>

<#--
 * returns a given identifier as css class name
 * - Illegal characters will be removed
 * - camel case will be replaced with dashes
 *
 * @param identifier the identifier to use as css class name
 todo: CMS-11143 move to download portal (am)
 -->
<#function asCSSClassName identifier>
  <#local cleanedUpIdentifier=identifier?replace("[!\"#$%&'\\(\\)\\*\\+,\\.\\/:;<=>\\?@\\[\\\\\\]\\^`\\{\\|}~\\s]", "", "r") />) />
  <#return cleanedUpIdentifier?replace("([A-Z][a-z])|([A-Z]$)", " $0", "r")?lower_case?replace(" ", "-") />
</#function>


<#-- --- PRIVATE --------------------------------------------------------------------------------------------------- -->


<#-- PRIVATE -->
<#function _extendSequenceInMap map={} key="" extendBy=[]>
  <#local newSequence=extendBy />
  <#if map?keys?seq_contains(key) && map[key]?is_sequence>
    <#local newSequence=map[key] + extendBy />
  </#if>
  <#return map + {key: newSequence} />
</#function>


<#-- --- DEPRECATED ------------------------------------------------------------------------------------------------ -->


<#-- DEPRECATED, UNUSED -->
<#macro optionalFrameForObject self="" classFrame="" attr={}>
  <#if self.teaserTitle?has_content  || self.teaserText?has_content>
  <div class="cm-frame ${classFrame}"<@renderAttr attr />>
    <#if self.teaserTitle?has_content>
        <h2 class="cm-frame__title"<@cm.metadata ["properties.teaserTitle", self.content] />>${self.teaserTitle}</h2>
    </#if>
  </#if>
  <#nested>
  <#if self.teaserTitle?has_content  || self.teaserText?has_content>
    <#if self.teaserText?has_content>
        <div class="cm-frame__text"<@cm.metadata ["properties.teaserTitle", self.content] />><@cm.include self=self.teaserText/></div>
    </#if>
  </div>
  </#if>
</#macro>
