import $ from "jquery";
import * as basic from "./basic";

/**
 * Accordion functionality
 */
const $document = $(document);

// class name definitions
const classAccordionItem = "cm-accordion-item";
const classAccordionItemHeader = classAccordionItem + "__header";
const classAccordionItemContent = classAccordionItem + "__content";
const classAccordionItemHeaderActive = classAccordionItemHeader + "--active";
const classAccordionItemContentActive = classAccordionItemContent + "--active";

// prefix/namespace for events in this module
const EVENT_PREFIX = "coremedia.blueprint.basic.accordion.";

/**
 * @type {string} Name for the event to be triggered if accordion has changed
 */
export const EVENT_ACCORDION_CHANGED = EVENT_PREFIX + "accordionChanged";

/**
 * Changes the active item of the given accordion to the given item
 * @param {jQuery} $accordion the accordion to change
 * @param {jQuery} $activeItem the item to be active
 */
export function change($accordion, $activeItem) {
  $accordion.find(".cm-accordion-item").not($activeItem).each(function () {
    const $item = $(this);
    $item.find("." + classAccordionItemHeader).first().removeClass(classAccordionItemHeaderActive);
    $item.find("." + classAccordionItemContent).first().removeClass(classAccordionItemContentActive);
  });
  $activeItem.find("." + classAccordionItemHeader).first().addClass(classAccordionItemHeaderActive);
  $activeItem.find("." + classAccordionItemContent).first().addClass(classAccordionItemContentActive);
  $accordion.trigger(EVENT_ACCORDION_CHANGED, [$activeItem]);
  $document.trigger(basic.EVENT_LAYOUT_CHANGED);
}
