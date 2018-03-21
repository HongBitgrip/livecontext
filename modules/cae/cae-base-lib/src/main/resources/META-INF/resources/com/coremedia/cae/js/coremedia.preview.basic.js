/**
 *  CoreMedia Namespace
 */
var coremedia = (function (module) {
  return module;
}(coremedia || {}));
coremedia.preview = (function (module) {
  return module;
}(coremedia.preview || {}));

/**
 * fragment preview javascript
 */
coremedia.preview.basic = (function (module) {
  module.EVENT_LAYOUT_CHANGED = "coremedia.preview.layoutChanged";
  return module;
}(coremedia.preview.basic || {}));

/**
 * Toggle (open/close) an element with class "toggle-container" by clicking on an element
 * with class "toggle-button" inside a container. The state will be stored in sessionStorage,
 * if available in browser and unique data-id is set.
 *
 * By default, this function is bind to all dom elements with class "toggle-item" and is used in fragmented preview.
 *
 * Example:
 *
 * <div id="example" data-id="example">
 *   <a href="#" class="toggle-button">Headline</a>
 *   <div class="toggle-container">Content</div>
 * </div>
 *
 * <script>
 *   coremedia.preview.basic.toggle.init("#example");
 * </script>
 */
coremedia.preview.basic.toggle = function (module) {

  var $ = coremedia.preview.$;
  var $document = $(document);

  /* Defines that the toggle is on */
  module.STATE_ON = "on";
  /* Defines that the toggle is off */
  module.STATE_OFF = "off";

  /**
   * Returns the state of a toggleItem base on the visibility of the
   * toggleContainer element.
   *
   * @param {object} toggleItem
   * @returns {string} "on" or "off"
   */
  module.getState = function (toggleItem) {
    // if toggle-container is visible state is on otherwise off
    return $(toggleItem).find(".toggle-container:first").hasClass("toggle-container-off")
            ? module.STATE_OFF
            : module.STATE_ON;
  };

  /**
   *  Sets the toggle on
   *
   * @param {object} toggleItem
   */
  module.on = function (toggleItem) {
    var $toggleItem = $(toggleItem);
    $toggleItem.find(".toggle-button:first").removeClass("toggle-off");
    $toggleItem.find(".toggle-container:first").removeClass("toggle-container-off");
    $toggleItem.trigger("toggleStateChanged", [module.STATE_ON]);
  };

  /**
   * Sets the toggle off
   *
   * @param {object} toggleItem
   */
  module.off = function (toggleItem) {
    var $toggleItem = $(toggleItem);
    $toggleItem.find(".toggle-button:first").addClass("toggle-off");
    $toggleItem.find(".toggle-container:first").addClass("toggle-container-off");
    $toggleItem.trigger("toggleStateChanged", [module.STATE_OFF]);
  };

  /**
   * If the toggle is on set the toggle off otherwise on.
   *
   * @param {object} toggleItem
   */
  module.toggle = function (toggleItem) {
    if (module.getState(toggleItem) === module.STATE_ON) {
      module.off(toggleItem);
    } else {
      module.on(toggleItem);
    }
  };

  /**
   * Initializes the toggleItem, binds handlers and sets its state base on the session.
   *
   * @param {object} toggleItem
   */
  module.init = function (toggleItem) {
    // check if browser supported sessionStorage
    var storageEnabled = typeof(Storage) !== "undefined";
    var $toggleItem = $(toggleItem);
    // only safe state if toggleItem has an id and storage is supported
    var useStorage = storageEnabled && $toggleItem.data("id") !== undefined;

    if (useStorage) {
      var state = sessionStorage.getItem($toggleItem.data("id"));
      if (state === module.STATE_ON) {
        module.on(toggleItem);
      }
      if (state === module.STATE_OFF) {
        module.off(toggleItem);
      }
    }

    // bind click-listener
    $toggleItem.find(".toggle-button").bind("click", function () {
      module.toggle(toggleItem);
      return false;
    });
    // bind toggleState-listener
    $toggleItem.bind("toggleStateChanged", function (event, newState) {
      if (useStorage) {
        sessionStorage.setItem($toggleItem.data("id"), newState);
      }
      $document.trigger(coremedia.preview.basic.EVENT_LAYOUT_CHANGED);
    });
  };
  return module;
}(coremedia.preview.basic.toggle || {});

// --- DOCUMENT READY --------------------------------------------------------------------------------------------------
coremedia.preview.$(function () {

  var $ = coremedia.preview.$;

  $(".toggle-item").each(function (index, toggleItem) {
    coremedia.preview.basic.toggle.init(toggleItem);
  });
});
