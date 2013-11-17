<%--
  A simple page that works together with dolphin.js for showcasing the use of OpenDolphin in a
  single-page javascript application.
  author: dierk.koenig
--%>

<%
  velocityUrl = createLink(controller: 'setVelocity', action: 'set')
  range       = "range"
%>

<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Dolphin.js Simple Page</title>
  <script src="${resource(dir: 'libs', file: 'require.js')}"></script>
  <script src="${resource(dir: 'dolphin', file: 'config.js')}"></script>
</head>

<body>

<p>Use the slider or <br>
  tilt your device if it supports device orientation.
</p>

<input id="${range}" type="range" value="0" min="0" max="100">
<div id="value"></div>

<script id="${dolphinCode}">
  require([ ], function () {

    var request = new XMLHttpRequest();
    var url = "${velocityUrl}";

    console.log("INIT with url " + url);

    // bind range input field to pm rangeAttribute and label to pm
    var rangeInput = document.getElementById("${range}");
    rangeInput.addEventListener("input", function () {
      request.open('GET', url + "?value=" + rangeInput.value, true);
      request.send();
    });

    if (window.DeviceOrientationEvent) {
      var lastOrientation = 0;
      console.log("DeviceOrientation is supported");
      window.addEventListener('deviceorientation', function (eventData) {
        // gamma is the left-to-right tilt in degrees, where right is positive
        var newOrientation = (Math.floor(Number(eventData.gamma))) % 100;
        if (newOrientation !== lastOrientation) {
          lastOrientation = newOrientation;
          request.open('GET', url + "?value=" + newOrientation, true);
          document.getElementById("value").innerHTML = "value: "+newOrientation;
          request.send();
        }
      }, true);
    }

  });
</script>

</body>
</html>