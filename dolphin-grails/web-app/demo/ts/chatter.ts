import dol = require('../../js/dolphin/OpenDolphin');
import cd  = require('../../js/dolphin/ClientDolphin');
import cms = require('../../js/dolphin/ClientModelStore');
import cpm = require('../../js/dolphin/ClientPresentationModel');

// html elements
var postings        = <HTMLUListElement>    document.getElementById('postings');
var name            = <HTMLInputElement>    document.getElementById('name');
var message         = <HTMLTextAreaElement> document.getElementById('message');
var postMessage     = <HTMLButtonElement>   document.getElementById('post-message');

// dolphin setup
var SERVER_URL      = "http://localhost:8080/dolphin-grails/dolphin/";
var dolphin         = <cd.dolphin.ClientDolphin> dol.dolphin(SERVER_URL, true, 0); // slack

// main entry pm
var nameAtt         = dolphin.attribute("name",     null, '',  'VALUE');
var messageAtt      = dolphin.attribute("message",  null, '',  'VALUE');
var idAtt           = dolphin.attribute("id",       null, -1,  'VALUE');
var myChat          = dolphin.presentationModel("chatter.input", null, nameAtt, messageAtt, idAtt);

// bind input form bidirectionally
name.oninput     = (event) =>    nameAtt.setValue(   name.value);
name.onchange    = (event) =>    nameAtt.setValue(   name.value);
message.oninput  = (event) => messageAtt.setValue(message.value);
message.onchange = (event) => messageAtt.setValue(message.value);

nameAtt.onValueChange(   (event) => name.value    = event.newValue);
messageAtt.onValueChange((event) => message.value = event.newValue);

// bind collection of posts
dolphin.getClientModelStore().onModelStoreChange((event) => {
    var pm = event.clientPresentationModel;
    if (pm.presentationModelType != "chatter.type.post") return;
    if (event.eventType == cms.dolphin.Type.ADDED) {
        var li = document.createElement("li");
        li.id = pm.id;
        postings.appendChild(li);
        var update = (evt) => li.innerHTML = "<b>"+ pm.getAt("name").getValue()+": </b>" + pm.getAt("message").getValue();
        event.clientPresentationModel.getAt("name").onValueChange(update);
        event.clientPresentationModel.getAt("message").onValueChange(update);
    }
    if (event.eventType == cms.dolphin.Type.REMOVED) {
        li = <HTMLLIElement> document.getElementById(pm.id);
        postings.removeChild(li);
    }
});

// handle the button click
postMessage.onclick = (event) => dolphin.send('chatter.post', null);

dolphin.send("chatter.init", null);

var longPollCallback = (pms) => {
    dolphin.send("chatter.poll", { onFinished : longPollCallback, onFinishedData : null });
}
longPollCallback([]);

