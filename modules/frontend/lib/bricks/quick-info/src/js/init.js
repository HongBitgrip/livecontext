import * as quickInfo from "./quickInfo";
import $ from "jquery";
import * as nodeDecorationService from "@coremedia/js-node-decoration-service";

$(function () {
  // handle quickinfos
  nodeDecorationService.addNodeDecoratorBySelector(".cm-quickinfo", function ($target) {
    quickInfo.closeQuickInfo($target);
  });

  $('body').on("keydown", function (event) {
    // close quickinfo on Esc key
    if (event.keyCode === 27) {
      quickInfo.hide($('.cm-quickinfo--active'));
    }
    // previous quickinfo on LeftArrow key
    if (event.keyCode === 37) {
      const previousQuickInfoId = $('#' + $(".cm-quickinfo--active").find(".cm-quickinfo__switch--prev").data('cm-target'));
      if (previousQuickInfoId) {
        quickInfo.show(previousQuickInfoId);
      }
    }
    // next quickinfo on RightArrow key
    if (event.keyCode === 39) {
      const $nextQuickInfoId = $('#' + $(".cm-quickinfo--active").find(".cm-quickinfo__switch--next").data('cm-target'));
      if ($nextQuickInfoId) {
        quickInfo.show($nextQuickInfoId);
      }
    }
  });

  // handle quickinfo buttons
  nodeDecorationService.addNodeDecoratorByData({
    target: undefined,
    group: undefined
  }, "cm-button--quickinfo", function ($button, config) {
    quickInfo.toggleQuickInfo($button, config);
  });

  // switch through quickinfos
  nodeDecorationService.addNodeDecoratorBySelector(".cm-quickinfo__switch", function ($target, config) {
    quickInfo.switchQuickInfo($target, config);
  });

  nodeDecorationService.addNodeDecoratorBySelector(
  ".cm-quickinfo",
  function ($target) {
    $target.on("swipeleft", function(){
      const $prevButton = $target.find(".cm-quickinfo__switch--prev");
      const $nextQuickInfo = $('#' + $prevButton.data('cm-target'));
      $nextQuickInfo.length > 0 && quickInfo.show($nextQuickInfo);
    });
    $target.on("swiperight", function(){
      const $nextButton = $target.find(".cm-quickinfo__switch--next");
      const $nextQuickInfo = $('#' + $nextButton.data('cm-target'));
      $nextQuickInfo.length > 0 && quickInfo.show($nextQuickInfo);
    });
  },
  function ($target) {
    $target.off("swipeleft");
    $target.off("swiperight");
    $target.off("swipe");
  });

});