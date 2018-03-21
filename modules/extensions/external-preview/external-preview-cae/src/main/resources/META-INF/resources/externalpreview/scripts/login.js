(function () {
  // create coremedia namespace if it doesn't exist
  this.coremedia = window.coremedia || {};

  // create coremedia.blueprint namespace if it doesn't exist
  this.coremedia.blueprint = coremedia.blueprint || {};

  // extend coremedia.blueprint with externalpreview object
  this.coremedia.blueprint.externalpreview = {
    login: function(params) {
      params.token = encodeURI(params.token);
      $.ajax({
        url: "/blueprint/servlet/service/externalpreview?token=" + encodeURIComponent(params.token) + "&method=login",
        dataType: "json",
        cache: false,
        data:[],
        success: function (data, textStatus, jqXHR) {
          params.callback(data.status == "ok");
        },
        error: function(jdXHR, textStatus, errorThrown) {
          params.callback(false);
        }
      });
    }
  };
})();

$(function() {
  $("#loginButton").click(function() {
    var token = $("#previewToken").val();
    var errorElement = $("#loginError");
    coremedia.blueprint.externalpreview.login({
      token: token,
      callback: function(result) {
        if (result) {
          errorElement.hide();
          window.location = "preview.html#" + token;
        } else {
          errorElement.show();
        }
      }
    });
  });

  $(window).click(function() {
    $("#loginError").hide();
    });

  $("#previewToken").keypress(function(event) {
    // look for window.event in case event isn't passed in
    if (event == undefined && window.event !== undefined) {
      event = window.event;
    }
    if (event !== undefined && event.keyCode == 13) {
      $("#loginButton").click();
    }
  });

  $("#previewToken").focus();
});
