<%--
  A simple page that works together with dolphin.js for showcasing the use of OpenDolphin in a
  single-page javascript application.
  author: dierk.koenig
--%>

<%@ page import="org.opendolphin.demo.crud.PortfolioConstants; org.opendolphin.demo.TutorialAction" contentType="text/html;charset=UTF-8" %>

<%
  dolphinUrl      = createLink(controller: 'dolphin', absolute: true) - 'index'

  // page-wide constants to share as HTML ids and JS lookups
  gauge           = "gauge"
  startButton     = "startButton"
  pmId            = "Train"
  showCode        = "showCode"
  dolphinCode     = "dolphinCode"

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
      <p class="lead">Cross-channel instant updates</p>
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
      <legend>Step 2 - Instant updates across channels</legend>
      <span class="help-block">
        Change the velocity in a JavaFX view and see it updated instantly<br>
        across various channel like in this web page on your desktop or mobile device.
      </span>

      <canvas height="200" width="200" id="${gauge}"></canvas>

      <button id="${startButton}" class="btn btn-primary">Start</button>

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

<script src="${resource(dir: 'libs', file: 'steelseries-min.js')}"></script>
<script src="${resource(dir: 'libs', file: 'tween-min.js')}"></script>
<script>
    document.gauge = new steelseries.Radial('gauge', {
        pointerType: steelseries.PointerType.TYPE4,
        titleString: "km/h",
        threshold: 50,
        lcdVisible: true
    });
</script>

<script id="${dolphinCode}">
    require([
        'Dolphin',
        'comm/ClientAttribute'
    ], function (Dolphin, ClientAttribute) {

        var dolphin = new Dolphin("${dolphinUrl}");

        var speedAttr = new ClientAttribute("speed");
        speedAttr.setValue("0");
        speedAttr.qualifier = "train.speed";

        console.log("INIT PM");
        dolphin.getClientDolphin().presentationModel(
                "${pmId}",
                undefined,
                speedAttr
        );

        var startButton = document.getElementById("${startButton}")
        startButton.addEventListener("click", function () {
            function longPoll() {
                dolphin.getClientDolphin().send("poll.train.speed", longPoll);
            }
            longPoll();
        });

        speedAttr.on("valueChange", function (data) {
          document.gauge.setValue(Number(data.newValue));
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