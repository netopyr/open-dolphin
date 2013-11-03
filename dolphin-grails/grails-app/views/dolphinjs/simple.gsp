<%--
  A simple page that works together with dolphin.js for showcasing the use of OpenDolphin in a
  single-page javascript application.
  author: dierk.koenig
--%>

<%@ page import="org.opendolphin.demo.TutorialAction" contentType="text/html;charset=UTF-8" %>

<%
  dolphinUrl      = createLink(controller: 'dolphin', absolute: true) - 'index'

  // page-wide constants to share as HTML ids and JS lookups
  textInputId     = "textInput"
  label           = "label"
  logActionButton = "logActionButton"
  addButton       = "addButton"
  list            = "list"
  showCode        = "showCode"
  dolphinCode     = "dolphinCode"
  range           = "range"
  rangeLabel      = "rangeLabel"
%>

<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Dolphin.js Simple Page</title>
  <script src="${resource(dir: 'libs',    file: 'require.js')}"></script>
  <script src="${resource(dir: 'dolphin', file: 'config.js')}"></script>
  <link  href="${resource(dir: 'css',     file: 'bootstrap.min.css')}" rel="stylesheet">
</head>

<body>
<div class="container">
  <div class="row" style="margin-top: 4em;">
    <div class="offset2 span4">
      <h1>Dolphin.js</h1>
      <p class="lead">A simple page with bindings</p>
    </div>
    <div class="span3">
      <a href="http://open-dolphin.org">
        <img src="${resource(dir: 'img', file: 'open_dolphin_logo.png')}" alt="Open Dolphin">
      </a>
    </div>
  </div>

  <div class="row">
    <div class="offset1 span8">
    <fieldset>
      <legend>Step 1 - Some simple bindings</legend>
      <span class="help-block">
        Type some text in the input field to test the binding with the label.<br>
        Click the button to let it update with server-side content.
      </span>
      <input id="${textInputId}" type="text" placeholder="Type some text..."/>
      <p>Label: <span id="${label}"></span></p>
      <button id="${logActionButton}" class="btn btn-primary">Server Modification</button>

      <span class="help-block">
        Drag the slider to see the label being updated.
      </span>
      <input id="${range}" type="range" value="10" min="0" max="100">
      <p>Label: <span id="${rangeLabel}"></span></p>

      <span class="help-block">
        Click to get new content from the server side, bound to a list.
      </span>
      <button id="${addButton}"       class="btn btn-primary">Add Server Data</button>
      <div id="${list}"></div>

    </fieldset>
    </div>
  </div>
  <hr>

  <!-- Collapsible area to show the code snippet, this is only for illustration. Dolphin does not depend on angular. -->
  <div ng-app="dolphinJsTutorial">
    <div ng-controller="CollapseController">
      <div class="row">
        <div class="offset1"><button class="btn" ng-click="isCollapsed = !isCollapsed">{{caption}} Code</button></div>
        <div class ="offset8 span2"><a href="http://www.canoo.com" >
          <img  src="${resource(dir: 'img', file: 'canoo_logo.png')}" alt="Canoo Engineering AG">
        </a></div>
      </div>

      <div class="row" collapse="isCollapsed">
        <pre id="${showCode}" class="prettyprint" data-script-id="${dolphinCode}">
          <!-- The code snippet will be shown here -->
        </pre>
      </div>
    </div>
  </div>
</div>

<script id="${dolphinCode}">
  require([
    'Dolphin',
    'comm/ClientAttribute'
  ], function (Dolphin, ClientAttribute) {

    var dolphin = new Dolphin("${dolphinUrl}");

    // create named PM with attribute on the client side
    var textAttribute  = new ClientAttribute("${TutorialAction.ATTR_ID}");
    var rangeAttribute = new ClientAttribute("${range}");
    console.log("INIT PM");
    dolphin.getClientDolphin().presentationModel(
      "${TutorialAction.PM_ID_MODEL}", undefined,
      textAttribute, rangeAttribute
    );

    // send echo command on button click
    var logActionButton = document.getElementById("${logActionButton}");
    logActionButton.addEventListener("click", function () {
      dolphin.getClientDolphin().send("${TutorialAction.CMD_ECHO}");
    });

    // bind text input field to pm textAttribute bidirectionally
    var textInput = document.getElementById("${textInputId}");
    textInput.addEventListener("input", function () {
      textAttribute.setValue(textInput.value);
    });
    textAttribute.on("valueChange", function (data) {
      textInput.value = data.newValue;
    });

    // bind label to textAttribute
    var label = document.getElementById("${label}");
    textAttribute.on("valueChange", function (data) {
      label.innerHTML = data.newValue;
    });

    // bind range input field to pm rangeAttribute and label to pm
    var rangeInput  = document.getElementById("${range}");
    var rangeOutput = document.getElementById("${rangeLabel}");
    rangeInput.addEventListener("input", function () {
      rangeAttribute.setValue(rangeInput.value);
    });
    rangeAttribute.on("valueChange", function (data) {
      rangeOutput.innerHTML = data.newValue;
    });

    // send add command on button click and add a div for each received model
    var addButton = document.getElementById("${addButton}");
    var list      = document.getElementById("${list}");
    addButton.addEventListener("click", function () {
      dolphin.getClientDolphin().send("${TutorialAction.CMD_ADD}", function (models) {
        console.log("NEW models", models);
        models.forEach(function (model) {
          var element = document.createElement("div");
          element.innerHTML = model.presentationModelType + ": " + model.attributes[0].value;
          list.appendChild(element);
        })
      });
    });

  });
</script>

<!-- Copy the content of the application script into an html container and Use prettify to show the code snippet -->
<script>
  var showCode = document.getElementById("${showCode}");
  var script   = document.getElementById("${dolphinCode}");
  showCode.innerHTML = script.innerHTML;
</script>
<script src="https://google-code-prettify.googlecode.com/svn/loader/run_prettify.js?skin=sunburst"></script>

<!-- Use AngularJS directives for Bootstrap to make the code snippet collapsible -->
<script src="${resource(dir: 'libs', file: 'angular-1.0.5.min.js')}"></script>
<script src="${resource(dir: 'libs', file: 'ui-bootstrap-tpls-0.2.0.min.js')}"></script>
<script>
  angular.module('dolphinJsTutorial', ['ui.bootstrap']);

  function CollapseController($scope) {
    $scope.isCollapsed = true;
    $scope.caption = "Show";

    $scope.$watch("isCollapsed", function (newValue) {
      $scope.caption = newValue ? "Show" : "Hide";
    });
  }
</script>
</body>
</html>