import $ from "jquery";

const classMain = "cm-dropdown";
const classMenu = "cm-dropdown-menu";
const classMenuOpened = "cm-dropdown-menu--active";
const classMenuSubOpened = "cm-dropdown-menu--opened";
const classItem = "cm-dropdown-item";
const classItemLeaf = "cm-dropdown-item--leaf"; // new: defines, that the menu item is a leaf (has no submenus)
const classButton = "cm-dropdown-button";
const classButtonOpen = "cm-dropdown-button--open";
const classButtonClose = "cm-dropdown-button--close";
const classMenuLevel = "cm-dropdown-menu--level";
const classMenuMinLevel = "cm-dropdown-menu--min-level";
const classItemLevel = "cm-dropdown-item--level";
const classItemMinLevel = "cm-dropdown-item--min-level";
const classButtonLevel = "cm-dropdown-button--level";
const classButtonMinLevel = "cm-dropdown-button--min-level";

const EVENT_PREFIX = "coremedia.blueprint.basic.dropdown.";
export const EVENT_DROPDOWN_CHANGED = EVENT_PREFIX + "dropdownChanged";

/**
 * Sets the state of an menu or menu item
 *
 * @param {object} item menu or menu item
 * @param {string} state "opened", "sub-opened" or ""
 */
export function setState(item, state) {
  const $item = $(item);
  if (state == "opened" || state === "sub-opened") {
    $item.addClass(classMenuSubOpened);
  }
  if (state === "sub-opened") {
    $item.removeClass(classMenuOpened);
  }
  if (state === "opened") {
    $item.addClass(classMenuOpened);
  }
  if (state === "") {
    $item.removeClass(classMenuOpened);
    $item.removeClass(classMenuSubOpened);
  }
}

/**
 * Opens the delivered menu.
 *
 * @param menu The menu to be opened
 */
export function open(menu) {
  const $menu = $(menu);
  const $root = $(menu).closest("." + classMain);

  let additionalClassButtonOpen = $root.data("dropdown-class-button-open");
  if (additionalClassButtonOpen === undefined) {
    additionalClassButtonOpen = "";
  }
  let additionalClassButtonClose = $root.data("dropdown-class-button-close");
  if (additionalClassButtonClose === undefined) {
    additionalClassButtonClose = "";
  }

  // Full reset

  // remove open or sub-open from all menus
  $root.find("." + classMenu).each(function () {
    setState(this, "");
  });

  const $items = $root.find("." + classItem);

  // remove open or sub-open from all menu items
  $items.each(function () {
    setState(this, "");
  });

  // add is-leaf to all items
  $items.addClass(classItemLeaf);

  // set open for all openclose buttons having submenu (there can be more than one dropdown-menu-openclose per menu)
  $items.has("." + classMenu).find("." + classButton + ":first").each(function () {
    const $item = $(this).parent(":first");

    // indicate that item is no leaf
    $item.removeClass(classItemLeaf);

    $item.children("." + classButton).each(function () {
      const $this = $(this);
      $this.removeClass(additionalClassButtonClose);
      $this.removeClass(classButtonClose);
      $this.addClass(classButtonOpen);
      $this.addClass(additionalClassButtonOpen);
    });
  });

  setState(menu, "opened");

  // set sub-opened to all parent menus
  $menu.parents("." + classMenu).each(function () {
    setState(this, "sub-opened");
  });

  // set sub-opened to all parent menu items
  // set close to openclose buttons of menu item
  $menu.parents("." + classItem).each(function () {
    setState(this, "sub-opened");
    $(this).find("." + classButton + ":first").each(function () {
      $(this).parent(":first").children("." + classButton).each(function () {
        const $this = $(this);
        $this.removeClass(additionalClassButtonOpen);
        $this.removeClass(classButtonOpen);
        $this.addClass(classButtonClose);
        $this.addClass(additionalClassButtonClose);
      });
    });
  });

  // set opened to parent menu item if menu is not the root menu
  if (!$menu.hasClass(classMain)) {
    setState($menu.parent(":first"), "opened");
  }

  $root.trigger(EVENT_DROPDOWN_CHANGED, [menu]);
}

/**
 * Closes the delivered menu.
 *
 * @param {object} menu The menu to be closed
 */
export function close(menu) {
  const parent = menu.parents("." + classMenu + ":first");
  // closing a menu is the same as opening the parent menu
  open(parent);
}

/**
 * Initializes a dropdown menu
 *
 * @param {object} menu The menu to be initialized
 */
export function init(menu) {
  const $menu = $(menu);

  // the root menu itsself is a dropdown-menu
  $menu.addClass(classMenu);

  // add classes for menu and items if selectors are specified
  const selectorMenus = $menu.data("dropdown-menus");
  if (typeof selectorMenus !== "undefined") {
    $menu.find(selectorMenus).addClass(classMenu);
  }
  const selectorItems = $menu.data("dropdown-items");
  if (typeof selectorItems !== "undefined") {
    $menu.find(selectorItems).addClass(classItem);
  }

  // every menu items get an openclose button (initialized with no action to be performed)
  $menu.find("." + classItem).prepend("<button class=\"" + classButton + "\"></button>");

  // recursively add levels
  const addLevel = function (menu, level) {
    const $menu = $(menu);
    $menu.addClass(classMenuLevel + level);
    for (let i = 1; i <= level; i++) {
      $menu.addClass(classMenuMinLevel + i);
    }
    const $items = $menu.children("." + classItem);
    $items.each(function () {
      const $item = $(this);
      $item.addClass(classItemLevel + level);
      for (let i = 1; i <= level; i++) {
        $item.addClass(classItemMinLevel + i);
      }

      // min 0, max 1
      $item.children("." + classButton).each(function () {
        const $button = $(this);
        $button.addClass(classButtonLevel + level);
        for (let i = 1; i <= level; i++) {
          $button.addClass(classButtonMinLevel + i);
        }
      });
      $item.children("." + classMenu).each(function () {
        addLevel(this, level + 1);
      });
    });
  };
  addLevel(menu, 1);

  // open the menu to be initialized
  open(menu);

  // bind click-listener to openclose button
  $menu.find("." + classButton).bind("click", function () {
    const $this = $(this);
    const $parent = $(this).closest("." + classItem).find("." + classMenu + ":first");
    if ($this.hasClass(classButtonOpen)) {
      open($parent);
    } else if ($this.hasClass(classButtonClose)) {
      close($parent);
    }
    return true;
  });

  // bind delegation from empty link to openclose button
  $menu.find("." + classItem + " > a").each(function () {
    const $this = $(this);
    if (!$this.attr("href")) {
      $this.bind("click", function () {
        $this.closest("." + classItem).find("." + classButton + ":first").trigger("click");
        return false;
      });
    }
  });
}