import * as logger from "@coremedia/js-logger";
import $ from "jquery";
import * as nodeDecorationService from "@coremedia/js-node-decoration-service";

import {default as Handler, BASE_CONFIG as HANDLER_BASE_CONFIG} from "./hashBasedFragment.Handler";
import {default as Link, BASE_CONFIG as LINK_BASE_CONFIG} from "./hashBasedFragment.Link";
import {default as Form, BASE_CONFIG as FORM_BASE_CONFIG} from "./hashBasedFragment.Form";

export {Handler, Link, Form};

export function requestParamsToString(requestParams) {
  let result = "";
  for (let name in requestParams) {
    if (!requestParams.hasOwnProperty(name)) {
      continue;
    }
    if (result.length > 1) {
      result += "&";
    }
    result += encodeURIComponent(name);
    if (typeof requestParams[name] !== "undefined") {
      result += "=" + encodeURIComponent(requestParams[name]);
    }
  }
  return result;
}

export function stringToRequestParams(string, validParameters) {
  validParameters = validParameters || [];
  const requestParams = {};
  const hashParams = string.split("&");
  $.each(hashParams, function (_, parameter) {
    const keyValue = parameter.split("=", 2);
    const key = keyValue[0];
    const value = keyValue[1];
    if (key && validParameters.indexOf(key) > -1) {
      const decodedKey = decodeURIComponent(key);
      if (typeof value !== "undefined") {
        requestParams[decodedKey] = decodeURIComponent(value);
      } else {
        requestParams[decodedKey] = "";
      }
    }
  });

  return requestParams;
}

// --- DOCUMENT READY --------------------------------------------------------------------------------------------------
$(function () {

  // handle hashBasedFragmentHandler
  nodeDecorationService.addNodeDecoratorByData(
          HANDLER_BASE_CONFIG,
          "hash-based-fragment-handler",
          // decorate
          function ($handler, handlerConfig, state) {
            try {
              state.instance = new Handler($handler, handlerConfig);
            } catch (error) {
              logger.log(error);
            }
          },
          // undecorate
          function ($handler, handlerConfig, state) {
            state.instance && state.instance.destroy();
          }
  );

  // handle hashBasedFragmentLinks
  nodeDecorationService.addNodeDecoratorByData(
          LINK_BASE_CONFIG,
          "hash-based-fragment-link",
          function ($link, linkConfig) {
            new Link($link, linkConfig);
          }
  );

  // handle hashBasedFragmentForms
  nodeDecorationService.addNodeDecoratorByData(
          FORM_BASE_CONFIG,
          "hash-based-fragment-form",
          function ($form, formConfig) {
            new Form($form, formConfig);
          }
  );

});
