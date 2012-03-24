The Dolphin Project
===================

Team: Dieter Holz, Andres Almiray, Dierk Koenig

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
- a server may "push" commands to the client

Project layout
--------------
The multi-project build consists of these subprojects

- shared (code that is needed on both client and server)
- client (visualization)
- server (domain model and control logic)
- combined (for testing the client-server combination)
- demo   (again devided in client - server - shared)

General approach
----------------
PMs are used on both, client and server.
PMs consists of Attributes that capture only one datatype,
which is "String".

On the client side, the visualization code (JavaFX) binds
against the PM, i.e. its Attributes.
On the server side, the domain model binds against the PM.

Commands are sent between client and server. They are 
concerned with create, read, update, and delete actions
for PMs. (Future: they may be undoable)

Unlike GRASP there is neiter a PM- nor an AttributeSwitch.
In contrast, switches are ordinary attributes that
happen to have the same id as the source attributes that there are "pointing"
to. They do *not* maintain a reference to the source attribute.

When "switching" all attribute properties get updated for all attributes
that share the same id. The same is true when any value changes.

How to build
------------
gradlew clean install

Prerequisites
-------------
JAVAFX_HOME must be set to a 2.1.0 version
