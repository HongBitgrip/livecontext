// Set current time... Enables remote control to find out when studio was started
window.document.openTimestamp = (new Date()).getTime();
// Set window name if necessary. Makes window accessible via remote control.
if (window.name !== "CoreMediaStudio") {
  window.name = "CoreMediaStudio";
}

joo = {
  localization: {
    localeCookieName: "com.coremedia.cms.editor.locale"
  }
};

(function () {
  // This disables touch events as soon as possible (prevents Ext.supports.TouchEvents=true later on).
  // CMS-11086
  delete window.ontouchend;

  // http://stackoverflow.com/questions/984510/what-is-my-script-src-url
  var configLocationPath = (function() {
    var scripts = document.getElementsByTagName('script'),
      script = scripts[scripts.length - 1];
    var scriptSrc;
    if (script.getAttribute.length !== undefined) {
      scriptSrc = script.getAttribute('src')
    } else {
      scriptSrc = script.getAttribute('src', 2);
    }
    return scriptSrc + "/..";
  }());

  function probeRestApiThenLoadScripts(coremediaRemoteServiceUri, failure) {
    var xhr = new XMLHttpRequest();

    xhr.onload = function() {
      if (xhr.status === 200) {
        window.coremediaRemoteServiceUri = coremediaRemoteServiceUri;
        loadScripts()
      } else {
        failure()
      }
    };

    xhr.open('HEAD', coremediaRemoteServiceUri + 'supported-locales.js');
    xhr.send();
  }

  function loadScripts() {
    // create and append script elements
    function loadScript(attributes) {
      var script = document.createElement('script');
      script.async = false;
      script.type = 'text/javascript';
      Object.keys(attributes).forEach(function(a) {
        script[a] = attributes[a];
      });

      document.body.appendChild(script);
    }

    var attributeList = [
      {src: window.coremediaRemoteServiceUri + 'supported-locales.js'},
      {src: window.coremediaRemoteServiceUri + 'accept-language-header.js'},
      {src: 'resources/before-ext-load.js'},
      {id: 'microloader', 'data-app': '906bf4bf-9a7d-42cc-b7a5-6ef30df325e9', src: 'bootstrap.js'}
    ];

    attributeList.forEach(loadScript);
  }

  var pathname = window.location.pathname;
  var isDevelopmentMode = pathname.indexOf("/target/app/") >= 0;
  var uriPrefix = isDevelopmentMode ? '/' : '';

  probeRestApiThenLoadScripts(uriPrefix + 'api/',
      function () {
        // fallback: try new "rest/" URL prefix
        probeRestApiThenLoadScripts(uriPrefix + 'rest/api/',
            function () {
              console.error("No response from Studio REST API at " + uriPrefix + 'api/ or ' + uriPrefix + 'rest/api/');
            }
        );
      }
  );

})();
