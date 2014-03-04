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
var SERVER_URL      = window.location.protocol + "//" + window.location.host + "/dolphin-grails/dolphin/";
var dolphin         = <cd.dolphin.ClientDolphin> dol.dolphin(SERVER_URL, true, 0); // slack

// main entry pm
var nameAtt         = dolphin.attribute("name",     null, '',  'VALUE');
var messageAtt      = dolphin.attribute("message",  null, '',  'VALUE');
var dateAtt         = dolphin.attribute("date",     null, '',  'VALUE');
var myChat          = dolphin.presentationModel("chatter.input", null, nameAtt, messageAtt, dateAtt);
var channelBlocked  = false;

function release() {
    if (!channelBlocked) return; // avoid too many unblocks
    channelBlocked = false;
    var http = new XMLHttpRequest();
    http.open('GET', window.location.protocol + "//" + window.location.host + "/dolphin-grails/chatter/release", true);
    http.send();
}

// bind input form bidirectionally
name.oninput     = (event) => {    nameAtt.setValue(   name.value); release() };
message.oninput  = (event) => { messageAtt.setValue(message.value); release() };

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
    pm.getAt("name").onValueChange((evt)    => nameDt.innerHTML = evt.newValue + "<p class='date'>" + pm.getAt("date").getValue() + "</p>");
    pm.getAt("message").onValueChange((evt) => msgDd.innerHTML  = "<pre>"+evt.newValue+"</pre>");

    var postUserId = pm.getAt("name").getQualifier().split("-")[0];
    msgDd.onclick = (evt) => {
        var userId = myChat.getAt("name").getQualifier().split("-")[0];
        if (userId == postUserId) {  // our post, we can select
            myChat.syncWith(pm);
            release();
        }
        message.focus();
    }
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
    dolphin.send('chatter.post', { onFinished : () => postMessage.disabled = false, onFinishedData : null });
    release();
    message.focus();
};

var longPollCallback = (pms) => {
    channelBlocked = true;
    dolphin.send("chatter.poll", { onFinished : longPollCallback, onFinishedData : null });
}

dolphin.send("chatter.init", { onFinished : () => longPollCallback([]), onFinishedData : null });

