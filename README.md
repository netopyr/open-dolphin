The Dolphin Project
===================

Team: Dieter Holz, Andres Almiray, Dierk Koenig

How to build
------------
 gradlew clean install

All generated jar files are copied to the dist directory.

To zip all the demo sources into the dist directory:
 gradlew demoZip

Update the logs for distribution
 updateLog

To run all tests including coverage analysis
(report is in build/coverage):
 gradlew -Pcoverage=true cleanTest test

Running the push demo with the grails server integration
 gradlew build
 cd dolphin-grails
 grails run-app
 // startGrailsClientDemo.groovy e.g. from inside idea

Prerequisites
-------------
JAVAFX_HOME must be set to a 2.1.0 version

Purpose
-------
Providing a ULC-like facility with visualization in JavaFX 2.0.

Just like ULC, all application logic resides on the server.

Unlike ULC

- the client/server split is not through widgets but through
  application-specific presentation models (PM)
- the split is not based on the half-object-plus-protocol pattern
  but on the command pattern
- visualization code resides on the client (View);
  models and controllers remain on the server, 
  presentation models are shared between client and server
- a server may "push" commands to the client by means of a long poll

Project layout
--------------
The multi-project build consists of these subprojects

- shared (code that is needed on both client and server)
- client (visualization)
- server (domain model and control logic)
- combined (for testing the client-server combination)
- demo-javafx   (again divided in client - server - shared - combined)

General approach
----------------
PMs are used on both, client and server.
PMs consist of Attributes that capture only simple data types.

On the client side, the visualization code (JavaFX) binds
against the PM, i.e. its Attributes.
On the server side, the domain model binds against the PM.

Commands are sent between client and server. They are 
concerned with create, read, update, and delete actions
for PMs. (Future: they may be undoable)

For PM/attributes to be synchronized between client and server developers
must make use of the respective ModelStore. Here's for example how to create
a PM on the client side and sync it with it's server counterpart

Old:
    def pm = new ClientPresentationModel('myPmId', [
        new ClientAttribute(propertyName: 'name', 'Dolphin')
    ])
    Dolphin.clientModelStore.add(pm)

New since 17. Aug 2012: use the facade:
    clientDolphin.presentationModel 'myPmId', name:'Dolphin'

Unlike GRASP there is neither a PM- nor an AttributeSwitch.
In contrast, switches are ordinary attributes that
happen to have the same qualifier as the source attributes that there are "pointing"
to. They do *not* maintain a reference to the source attribute.

When "switching" all attribute properties get updated for all attributes
that share the same qualifier. The same is true when any value changes.

Use
 dolphin.apply sourcePm to targetPm

== Dirty state ==

Attributes have a dirty flag that can be used to detect when the attribute
contains a value different that its original one. This flag is observable trough
standard POJO bindings (i.e, PropertyChangeListener).

PresentationModels also have a dirty flag which is dependent on the dirty status
of any of the attributes the model holds. This flag too is observable trough
standard POJO bindings (i.e, PropertyChangeListener).

== Thread Safety ==

Dolphin makes the assumption that changes to PresentationModel and Attributes will
occur inside the UI thread when in the client side of the application. Changes on
the server must occur in the same thread without enforcing one in particular.

== Programming model ==

The client side runs under an JavaSE event model, whereas the server side follows
a JavaEE request-response model. An important part of dolphin is to bridge these
two worlds.

The communication between these worlds happens solely through command objects
that are sent between them.

ClientModelStore (CMS) and ServerModelStore (SMS) are automatically kept in sync.
Changes to the CMS automatically trigger a respective command being sent to the
server, which in turn updates the SMS accordingly.

The server does never change the SMS on its own. Instead, the server may send
commands to the client, which make the client update the CMS (which in turn automatically
syncs with the server as mentioned above).
The reason for this approach is the communication delay between client and server.
We need to make sure that one side is the master and always ahead of the other
side and we have chosen the client to be that master.

Design decisions
----------------
- Any PresentationModel id must be unique inside the ModelStore.
- Any Attribute id must be unique inside the ModelStore.
- All commands from client to server are sent asynchronously.
- Commands are always sent in strict sequence such that we can rely on all
  value changes being synced to the SMS before a command is processed on the
  server that depends on these values.
