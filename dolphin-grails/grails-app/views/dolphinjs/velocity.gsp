<%--
  A simple page that works together with dolphin.js for showcasing the use of OpenDolphin in a
  single-page javascript application.
  author: dierk.koenig
--%>

<%@ page import="org.opendolphin.demo.TutorialAction" contentType="text/html;charset=UTF-8" %>

<%
  dolphinUrl      = createLink(controller: 'dolphin', absolute: true) - 'index'

  // page-wide constants to share as HTML ids and JS lookups
  pmId            = "Train"
  range           = "range"
%>

<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Dolphin.js Simple Page</title>
  <script src="${resource(dir: 'libs',    file: 'require.js')}"></script>
  <script src="${resource(dir: 'dolphin', file: 'config.js')}"></script>
</head>

<body>

<input id="${range}" type="range" value="0" min="0" max="100">

<script id="${dolphinCode}">
    require([
        'Dolphin',
        'comm/ClientAttribute'
    ], function (Dolphin, ClientAttribute) {

        var sender  = new Dolphin("${dolphinUrl}");

        console.log("INIT PM");

        var speedInput = new ClientAttribute("speed");
        speedInput.setValue("0");
        speedInput.qualifier = "train.speed.input";
        sender.getClientDolphin().presentationModel("${pmId}", undefined, speedInput)

        // bind range input field to pm rangeAttribute and label to pm
        var rangeInput  = document.getElementById("${range}");
        rangeInput.addEventListener("input", function () {
          console.log(rangeInput.value);
          speedInput.setValue(Number(rangeInput.value));
        });

      if (window.DeviceOrientationEvent) {
        console.log("DeviceOrientation is supported");
        window.addEventListener('deviceorientation', function(eventData) {
          // gamma is the left-to-right tilt in degrees, where right is positive
          speedInput.setValue((Math.floor(Number(eventData.gamma)) + 90) % 100);
          }, true);
      }

    });
</script>

</body>
</html>