import dol = require('../../js/dolphin/OpenDolphin');
import cd  = require('../../js/dolphin/ClientDolphin');
import cms = require('../../js/dolphin/ClientModelStore');
import cpm = require('../../js/dolphin/ClientPresentationModel');

// html elements
var postings        = <HTMLTableElement>    document.getElementById('postings');
var name            = <HTMLInputElement>    document.getElementById('name');
var message         = <HTMLTextAreaElement> document.getElementById('message');
var postMessage     = <HTMLButtonElement>   document.getElementById('post-message');

// dolphin setup
var SERVER_URL      = "http://localhost:8080/dolphin-grails/dolphin/";
var dolphin         = <cd.dolphin.ClientDolphin> dol.dolphin(SERVER_URL, true, 0); // slack

// main entry pm
var nameAtt         = dolphin.attribute("name",     null, '',  'VALUE');
var messageAtt      = dolphin.attribute("message",  null, '',  'VALUE');
var myChat          = dolphin.presentationModel("chatter.input", null, nameAtt, messageAtt);
var channelBlocked  = false;

function release() {
    if (!channelBlocked) return; // avoid too many unblocks
    channelBlocked = false;
    var http = new XMLHttpRequest();
    http.open('GET', "http://localhost:8080/dolphin-grails/chatter/release", true);
    http.send();
}

// bind input form bidirectionally
name.oninput     = (event) => { release();    nameAtt.setValue(   name.value)};
message.oninput  = (event) => { release(); messageAtt.setValue(message.value)};

nameAtt.onValueChange(   (event) => name.value    = event.newValue);
messageAtt.onValueChange((event) => message.value = event.newValue);

// bind collection of posts
function onPostAdded(pm) {
    var nameDt = document.createElement("dt");
    var msgDd  = document.createElement("dd");
    nameDt.id  = pm.getAt("name").getQualifier();
    msgDd.id   = pm.getAt("message").getQualifier();
    postings.appendChild(nameDt);
    postings.appendChild(msgDd);
    pm.getAt("name").onValueChange((evt)    => nameDt.innerHTML = evt.newValue);
    pm.getAt("message").onValueChange((evt) => msgDd.innerHTML  = evt.newValue);
}
function onPostRemoved(pm) {
    var holder = document.getElementById(pm.getAt("name").getQualifier());
    postings.removeChild(holder);
    holder = document.getElementById(pm.getAt("message").getQualifier());
    postings.removeChild(holder);
}

dolphin.getClientModelStore().onModelStoreChange((event) => {
    if (event.clientPresentationModel.presentationModelType != "chatter.type.post") return;
    if (event.eventType == cms.dolphin.Type.ADDED) {
        onPostAdded(event.clientPresentationModel);
    }
    if (event.eventType == cms.dolphin.Type.REMOVED) {
        onPostRemoved(event.clientPresentationModel);
    }
});

// handle the button click
postMessage.onclick = (event) => {
    postMessage.disabled = true; // double-click protection
    release();
    dolphin.send('chatter.post', { onFinished : () => postMessage.disabled = false, onFinishedData : null });
};

var longPollCallback = (pms) => {
    channelBlocked = true;
    dolphin.send("chatter.poll", { onFinished : longPollCallback, onFinishedData : null });
}

dolphin.send("chatter.init", { onFinished : () => longPollCallback([]), onFinishedData : null });

