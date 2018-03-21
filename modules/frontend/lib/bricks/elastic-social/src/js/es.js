import * as timezone from "./es.timezone";
import $ from "jquery";
import { findAndSelf } from "@coremedia/js-jquery-utils";
import * as basic from "@coremedia/js-basic";
import * as nodeDecorationService from "@coremedia/js-node-decoration-service";

const $document = $(document);

const EVENT_PREFIX = "coremedia.es.";
export const EVENT_FORM_CLOSE = EVENT_PREFIX + "formClose";
export const EVENT_FORM_SUBMIT = EVENT_PREFIX + "formSubmit";
export const EVENT_MODEL_INFO = EVENT_PREFIX + "modelInfo";
export const EVENT_TOGGLE_AVERAGE_RATING = EVENT_PREFIX + "toggleAverageRating";

const NOTIFICATION_TYPES = ["info", "error", "warning", "success"];
const NOTIFICATION_IDENTIFIER = "cm-notification";

/**
 * Decorates given container with notifications based on list of messages given.
 * Messages need to have the following structure:
 * {type: {String}, path: {undefined|String}, text: {string}}
 *
 * @param container the node to be decorated
 * @param messages the messages to apply.
 */
export function addNotifications(container, messages) {
  const $container = $(container);
  const $notificationByPath = {};

  // create a list of notification hooks by path
  const selector = "[data-" + NOTIFICATION_IDENTIFIER + "]";
  findAndSelf($container, selector).each(function () {
    const $this = $(this);
    const config = $.extend({path: ""}, $this.data(NOTIFICATION_IDENTIFIER));
    $notificationByPath[config.path] = $this;
  });

  // iterate over all given messages
  for (let i = 0; i < messages.length; i++) {
    const message = $.extend({type: "info", path: undefined, text: ""}, messages[i]);
    if (message.path === undefined) {
      message.path = "";
    }
    // find notification in map
    const $notification = $notificationByPath[message.path];

    if ($notification !== undefined) {
      // assign information to notification and make it visible
      $notification.find("." + NOTIFICATION_IDENTIFIER + "__text").html(message.text);
      if (NOTIFICATION_TYPES.indexOf(message.type) > -1) {
        $notification.addClass(NOTIFICATION_IDENTIFIER + "--" + message.type);
      }
      $notification.removeClass(NOTIFICATION_IDENTIFIER + "--inactive");
    }
  }
}

/**
 * Clears all notifications from the given container
 *
 * @param container the node to be cleared
 */
export function clearNotifications(container) {
  const $container = $(container);
  const $notifications = $container.find("[data-" + NOTIFICATION_IDENTIFIER + "]");
  for (let i = 0; i < NOTIFICATION_TYPES.length - 1; i++) {
    $notifications.removeClass(NOTIFICATION_IDENTIFIER + "--" + NOTIFICATION_TYPES[i]);
  }
  $notifications.addClass(NOTIFICATION_IDENTIFIER + "--inactive");
}

// apply confirm functionality to all elements rendered with necessary information
nodeDecorationService.addNodeDecoratorByData({message: undefined}, "cm-button--confirm", function ($target, config) {
  if (config.message !== undefined) {
    $target.bind("click", function () {
      return confirm(config.message);
    });
  }
});

const FORM_IDENTIFIER = "cm-form";

/**
 * Starts a form submit (prevent double submitting)
 * If submitting is done without page reload (e.g. ajax) formSubmitEnd has to be called once finished.
 *
 * @returns {boolean} TRUE if start was successfull
 */
export function formSubmitStart(form) {
  const $form = $(form);
  const result = $form.hasClass(FORM_IDENTIFIER + "--progress");
  $form.addClass(FORM_IDENTIFIER + "--progress");
  return !result;
}

/**
 * Ends a form submit (prevent double submitting)
 * Only used if submitting is done without page reload (e.g. ajax)
 *
 * @returns {boolean} TRUE if end was successfull
 */
export function formSubmitEnd(form) {
  const $form = $(form);
  const result = $form.hasClass(FORM_IDENTIFIER + "--progress");
  $form.removeClass(FORM_IDENTIFIER + "--progress");
  return result;
}

$("#timezone").val(timezone.determine_timezone().name());

const ES_AJAX_FORM_IDENTIFIER = "cm-es-ajax-form";

// activate es ajax forms
nodeDecorationService.addNodeDecoratorByData({}, ES_AJAX_FORM_IDENTIFIER, function ($form) {
  $form.on("submit", function (ev) {
    ev.preventDefault();
    if (formSubmitStart($form)) {
      clearNotifications($form);
      $form.trigger(EVENT_FORM_SUBMIT);
      $.ajax({
        type: $form.attr("method"),
        url: $form.attr("action"),
        data: $form.serialize(),
        headers: {'X-Requested-With': 'XMLHttpRequest'},
        xhrFields: {withCredentials: true},
        dataType: "json"
      }).done(function (result) {
        result = $.extend({success: false, messages: [], id: undefined}, result);
        if (result.success) {
          $form.trigger(EVENT_MODEL_INFO, [result]);
        } else {
          addNotifications($form, result.messages);
          /*global grecaptcha*/
          if (typeof grecaptcha !== "undefined") {
            //reset recaptcha if recaptcha is enabled and an error is found
            grecaptcha.reset();
          }
        }
        $document.trigger(basic.EVENT_LAYOUT_CHANGED);
      }).fail(function () {
        addNotifications($form, [{type: "error", "text": "Due to an internal error, comment could not be posted."}]);
      }).always(function () {
        formSubmitEnd($form);
      });
      $document.trigger(basic.EVENT_LAYOUT_CHANGED);
    }
  });
});

// activate cancel functionality for es forms
nodeDecorationService.addNodeDecoratorByData({}, "cm-button--cancel", function ($button) {
  $button.on("click", function () {
    $button.trigger(EVENT_FORM_CLOSE);
  });
});

const COMMENTS_IDENTIFIER = "cm-comments";
const NEW_COMMENT_IDENTIFIER = "cm-new-comment";

// activate write a comment functionality for buttons (not the submit button, just for displaying the form)
nodeDecorationService.addNodeDecoratorByData({
  replyTo: undefined,
  quote: {author: undefined, date: undefined, text: undefined}
}, "cm-button--comment", function ($commentButton, config) {
  $commentButton.on("click", function () {
    const $comments = $commentButton.closest("." + COMMENTS_IDENTIFIER);
    // deactivate all active buttons due to form element being reused
    $comments.find(".cm-toolbar--comments").removeClass("cm-toolbar--inactive");
    const $toolbar = $commentButton.closest(".cm-toolbar--comments");
    $toolbar.addClass("cm-toolbar--inactive");
    const $container = $comments.find("." + COMMENTS_IDENTIFIER + "__new-comment");
    // reset form
    $container.find("." + NEW_COMMENT_IDENTIFIER + "__form").each(function () {
      this.reset();
      clearNotifications(this);
    });
    $container.addClass(NEW_COMMENT_IDENTIFIER + "--active");

    const $replyToField = $container.find("[name='replyTo']");
    const $commentField = $container.find("[name='comment']");
    const commentField = $commentField[0];

    $replyToField.val(config.replyTo || "");
    if (config.quote.text !== undefined) {
      $commentField.val("[quote author='" + config.quote.author.replace("'", "\\''") + "' date='" + config.quote.date.replace("'", "\\''") + "']" + config.quote.text + "[/quote]\n");
    }
    $toolbar.after($container);
    $commentField.focus();
    // function exists in non IE browsers
    if (commentField.setSelectionRange) {
      // non IE
      const len = $commentField.val().length;
      commentField.setSelectionRange(len, len);
    } else {
      // IE
      $commentField.val($commentField.val());
    }
    commentField.scrollTop = commentField.scrollHeight;
    $document.trigger(basic.EVENT_LAYOUT_CHANGED);
  });
});

// activate functionality for new comment form
nodeDecorationService.addNodeDecoratorBySelector("." + NEW_COMMENT_IDENTIFIER, function ($newCommentWidget) {

  // catch es ajax form events
  findAndSelf($newCommentWidget, "form." + NEW_COMMENT_IDENTIFIER + "__form").each(function () {
    const $newCommentForm = $(this);
    const $commentsWidget = $newCommentForm.closest("." + COMMENTS_IDENTIFIER);
    $newCommentForm.on(EVENT_FORM_SUBMIT, function () {
      clearNotifications($commentsWidget);
      $document.trigger(basic.EVENT_LAYOUT_CHANGED);
    });
    $newCommentForm.on(EVENT_MODEL_INFO, function (event, handlerInfo) {
      if (handlerInfo.success) {
        basic.refreshFragment($commentsWidget, function (_, $commentsWidgetRefreshed) {
          if (handlerInfo.id !== undefined) {
            const $comment = $commentsWidgetRefreshed.find("[data-cm-comment-id='" + handlerInfo.id + "']");
            addNotifications($comment, handlerInfo.messages);
            $("html, body").animate({
              scrollTop: $comment.offset().top
            }, 500);
          } else {
            // fallback if no id is provided
            addNotifications($commentsWidgetRefreshed, handlerInfo.messages);
          }
          $document.trigger(basic.EVENT_LAYOUT_CHANGED);
        });
      }
    });
  });

  // activate cancel functionality for comment form
  $newCommentWidget.on(EVENT_FORM_CLOSE, function () {
    $newCommentWidget.removeClass(NEW_COMMENT_IDENTIFIER + "--active");
    $newCommentWidget.closest("." + COMMENTS_IDENTIFIER).find(".cm-toolbar--comments").removeClass("cm-toolbar--inactive");
    $document.trigger(basic.EVENT_LAYOUT_CHANGED);
  });
});

const REVIEWS_IDENTIFIER = "cm-reviews";
const NEW_REVIEW_IDENTIFIER = "cm-new-review";

// activate write a comment functionality for buttons (not the submit button, just for displaying the form)
nodeDecorationService.addNodeDecoratorByData({disabled: false}, "cm-button--review", function ($reviewButton, config) {
  if (!config.disabled) {
    $reviewButton.on("click", function () {
      const $reviews = $reviewButton.closest("." + REVIEWS_IDENTIFIER);
      // deactivate all active buttons due to form element being reused
      $reviews.find(".cm-toolbar--reviews").removeClass("cm-toolbar--inactive");
      const $toolbar = $reviewButton.closest(".cm-toolbar--reviews");
      $toolbar.addClass("cm-toolbar--inactive");
      const $container = $reviews.find("." + REVIEWS_IDENTIFIER + "__new-review");
      // reset form
      $container.find("." + NEW_REVIEW_IDENTIFIER + "__form").each(function () {
        this.reset();
        clearNotifications(this);
      });
      $container.addClass(NEW_REVIEW_IDENTIFIER + "--active");

      const $reviewField = $container.find("[name='review']");

      $toolbar.after($container);
      $reviewField.focus();
      $document.trigger(basic.EVENT_LAYOUT_CHANGED);
    });
  }
});

// activate functionality for new review form
nodeDecorationService.addNodeDecoratorBySelector("." + NEW_REVIEW_IDENTIFIER, function ($newReviewWidget) {

  // catch form submit for review functionality and replace it with ajax call
  findAndSelf($newReviewWidget, "form." + NEW_REVIEW_IDENTIFIER + "__form").each(function () {
    const $newReviewForm = $(this);
    const $reviewsWidget = $newReviewForm.closest("." + REVIEWS_IDENTIFIER);
    $newReviewForm.on(EVENT_FORM_SUBMIT, function () {
      clearNotifications($reviewsWidget);
      $document.trigger(basic.EVENT_LAYOUT_CHANGED);
    });
    $newReviewForm.on(EVENT_MODEL_INFO, function (event, modelInfo) {
      if (modelInfo.success) {
        basic.refreshFragment($reviewsWidget, function (_, $reviewsWidgetRefreshed) {
          if (modelInfo.id !== undefined) {
            const $review = $reviewsWidgetRefreshed.find("[data-cm-review-id='" + modelInfo.id + "']");
            addNotifications($review, modelInfo.messages);
            $("html, body").animate({
              scrollTop: $review.offset().top
            }, 500);
          } else {
            // fallback if no id is provided
            addNotifications($reviewsWidgetRefreshed, modelInfo.messages);
          }
          $document.trigger(basic.EVENT_LAYOUT_CHANGED);
        });
      }
    });
  });

  $newReviewWidget.on(EVENT_FORM_CLOSE, function () {
    $newReviewWidget.removeClass(NEW_REVIEW_IDENTIFIER + "--active");
    $newReviewWidget.closest("." + REVIEWS_IDENTIFIER).find(".cm-toolbar--reviews").removeClass("cm-toolbar--inactive");
    $document.trigger(basic.EVENT_LAYOUT_CHANGED);
  });

});


// initialize reviews widget
nodeDecorationService.addNodeDecoratorBySelector(".cm-ratings-average", function ($target) {
  $target.on(EVENT_TOGGLE_AVERAGE_RATING, function () {
    $target.toggleClass("cm-ratings-average--active");
  });
});