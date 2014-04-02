package org.opendolphin.demo.team;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import groovy.lang.Closure;
import groovyx.gpars.agent.Agent;
import groovyx.gpars.dataflow.DataflowQueue;
import org.opendolphin.core.Attribute;
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.comm.*;
import org.opendolphin.core.server.*;
import org.opendolphin.core.server.action.DolphinServerAction;
import org.opendolphin.core.server.comm.ActionRegistry;
import org.opendolphin.core.server.comm.CommandHandler;

import static org.opendolphin.demo.team.TeamMemberConstants.*;

public class TeamMemberActions extends DolphinServerAction {

    /** thread safe shared state to keep around for new user sessions */
    private final Agent currentMembers ;

    /** thread safe unique user count across all sessions */
    static final AtomicInteger userCount = new AtomicInteger(0);

    /** thread safe unique member count across all sessions */
    static final AtomicInteger memberCount = new AtomicInteger(0);

    /** unique identification of the current user session. */
    final int userId = userCount.getAndIncrement();

    private EventBus teamBus;
    private final DataflowQueue<TeamEvent> memberQueue = new DataflowQueue<TeamEvent>();

    public TeamMemberActions(EventBus teamBus, Agent<List<DTO>> history) {
        this.teamBus = teamBus;
        this.teamBus.subscribe(memberQueue);
        currentMembers = history;
    }

    @Override
    public void registerIn(ActionRegistry actionRegistry) {

        actionRegistry.register(CMD_INIT, new CommandHandler<NamedCommand>() {
            @Override
            public void handleCommand(NamedCommand command, List<Command> response) {
                // display the shared state
                try {
                    currentMembers.sendAndWait(new Closure(this) {
                        void doCall(List<DTO> members) {
                            for (DTO member : members) {
                                presentationModel(null, TYPE_TEAM_MEMBER, member);
                            }
                        }
                    });
                } catch (InterruptedException e) { /* do nothing */ }
                // we do not necessarily select at start
            }
        });

        actionRegistry.register(CMD_ADD, new CommandHandler<NamedCommand>() {
            @Override
            public void handleCommand(NamedCommand command, List<Command> response) {
                // prepare a DTO to add
                int memberId = memberCount.getAndIncrement();
                final DTO dto = new DTO(
                    new Slot(ATT_FIRSTNAME, "", qualifier(memberId, ATT_FIRSTNAME)),
                    new Slot(ATT_LASTNAME, "", qualifier(memberId, ATT_LASTNAME)),
                    new Slot(ATT_FUNCTION, "", qualifier(memberId, ATT_FUNCTION)),
                    new Slot(ATT_AVAILABLE,  false, qualifier(memberId, ATT_AVAILABLE)),
                    new Slot(ATT_CONTRACTOR, false, qualifier(memberId, ATT_CONTRACTOR)),
                    new Slot(ATT_WORKLOAD,   0,     qualifier(memberId, ATT_WORKLOAD))
                );
                // add the dto to shared state
                try {
                    currentMembers.sendAndWait(new Closure(this) {
                        void doCall(List<DTO> members) {
                            members.add(dto);
                            return;
                        }
                    });
                } catch (InterruptedException e) { /* do nothing */ }
                // notify all others about the new team member
                teamBus.publish(memberQueue, new TeamEvent("new", dto));
                // create the pm
                String addedId = uniqueId(memberId);
                presentationModel(addedId, TYPE_TEAM_MEMBER, dto);
                // it is a server-side decision that after creating a new team member,
                // it should be immediately selected
                changeValue(findSelectedPmAttribute(), addedId);
            }
        });

        // whenever we delete a pm, we must publish it to others and update the shared state
        actionRegistry.register(CMD_REMOVE, new CommandHandler<NamedCommand>() {
            @Override
            public void handleCommand(NamedCommand command, List<Command> response) {
                String selPmId = getServerDolphin().getAt(PM_ID_SELECTED).getAt(ATT_SEL_PM_ID).getValue().toString();
                PresentationModel pm = getServerDolphin().getAt(selPmId);
                if (pm == null) {
                    System.out.println("cannot find pm to delete with id "+selPmId);
                    return;
                }
                if (!TYPE_TEAM_MEMBER.equals(pm.getPresentationModelType())) return; // sanity check
                // take the qualifier of the first attribute as the indication which pm to remove
                Attribute indicator = pm.getAttributes().get(0);
                removeFromHistory(indicator);
                teamBus.publish(memberQueue, new TeamEvent("remove", indicator.getQualifier(), null));
                response.add(new DeletePresentationModelCommand(selPmId));
            }
        });

        // whenever a value changes, we must publish it to others and update the shared state
        actionRegistry.register(ValueChangedCommand.class, new CommandHandler<ValueChangedCommand>() {
            @Override
            public void handleCommand(ValueChangedCommand command, List<Command> response) {
                Attribute attr = getServerDolphin().findAttributeById(command.getAttributeId());
                if (attr == null ||
                    attr.getQualifier() == null ||
                    !TYPE_TEAM_MEMBER.equals(attr.getPresentationModel().getPresentationModelType()))
                    return;
                boolean updated = updateHistory(attr);
                if (updated) teamBus.publish(memberQueue, new TeamEvent("change", attr.getQualifier(), attr.getValue()));
            }
        });

        actionRegistry.register(CMD_SAVE, new CommandHandler<NamedCommand>() {
            @Override
            public void handleCommand(NamedCommand command, List<Command> response) {
                String pmIdToSave = (String) findSelectedPmAttribute().getValue();
                // saving the model to the database here. We assume all was ok:
                response.add(new SavedPresentationModelNotification(pmIdToSave));
                PresentationModel pm = getServerDolphin().getAt(pmIdToSave);
                for (Attribute attribute : pm.getAttributes()) {
                    rebaseInHistory(attribute);
                    teamBus.publish(memberQueue, new TeamEvent("rebase", attribute.getQualifier(), null));
                }
            }
        });

        actionRegistry.register(ACTION_ON_PUSH, new CommandHandler<NamedCommand>() {
            @Override
            public void handleCommand(NamedCommand command, List<Command> response) {
                try {
                    processEventsFromQueue(response, 60, TimeUnit.SECONDS);
                } catch (InterruptedException e) { /* do nothing */ }
            }
        });

        // the release action is in the TeamBusRelease

    }

    private void processEventsFromQueue(List<Command> response, int timeoutValue, TimeUnit timeoutUnit) throws InterruptedException {
        TeamEvent event = memberQueue.getVal(timeoutValue, timeoutUnit);
        while (null != event) {
            if ("new".equals(event.type)) { // todo : make enum
                presentationModel(null, TYPE_TEAM_MEMBER, event.dto);
            }
            if ("change".equals(event.type)) {
                List<Attribute> attributes = getServerDolphin().findAllAttributesByQualifier(event.qualifier);
                for (Attribute attribute : attributes) {
                    PresentationModel pm = attribute.getPresentationModel();
                    if (TYPE_TEAM_MEMBER.equals(pm.getPresentationModelType())) {
                        changeValue((ServerAttribute) attribute, event.value);
                    }
                }
            }
            if ("rebase".equals(event.type)) {
                List<Attribute> attributes = getServerDolphin().findAllAttributesByQualifier(event.qualifier);
                for (Attribute attribute : attributes) {
                    response.add(new BaseValueChangedCommand(attribute.getId()));
                }
            }
            if ("remove".equals(event.type)) {
                List<Attribute> attributes = getServerDolphin().findAllAttributesByQualifier(event.qualifier);
                for (Attribute attribute : attributes) {
                    PresentationModel pm = attribute.getPresentationModel();
                    if (TYPE_TEAM_MEMBER.equals(pm.getPresentationModelType())) {
                        response.add(new DeletePresentationModelCommand(pm.getId()));
                    }
                }
            }
            event = memberQueue.getVal(20, TimeUnit.MILLISECONDS);
        }
    }

    private Slot findSlotByQualifier(List<DTO> dtos, String qualifier) {
        if (qualifier == null) return null;
        Slot result = null;
        for (DTO dto : dtos) {
            for (Slot slot : dto.getSlots()) {
                if (qualifier.equals(slot.getQualifier())) {
                    return slot;
                }
            }
        }
        return result;
    }

    private boolean updateHistory(final Attribute attr) {
        final AtomicBoolean updated = new AtomicBoolean(false);
        try {
            currentMembers.sendAndWait(new Closure(this) {
                void doCall(List<DTO> members) {
                    Slot slot = findSlotByQualifier(members, attr.getQualifier());
                    if (null == slot) return;
                    if (! slot.getValue().equals(attr.getValue())) {
                        slot.setValue(attr.getValue());
                        updated.set(true);
                    }
                }
            });
        } catch (InterruptedException e) { /* do nothing */ }
        return updated.get();
    }

    private boolean rebaseInHistory(final Attribute attr) {
        final AtomicBoolean updated = new AtomicBoolean(false);
        try {
            currentMembers.sendAndWait(new Closure(this) {
                void doCall(List<DTO> members) {
                    Slot slot = findSlotByQualifier(members, attr.getQualifier());
                    if (null == slot) return;
                    slot.setBaseValue(slot.getValue());
                    updated.set(true);
                }
            });
        } catch (InterruptedException e) { /* do nothing */ }
        return updated.get();
    }

    private void removeFromHistory(final Attribute attr) {
        try {
            currentMembers.sendAndWait(new Closure(this) {
                void doCall(List<DTO> members) {
                    DTO toRemove = null;
                    out:
                    for (DTO member : members) {
                        for (Slot slot : member.getSlots()) {
                            if (slot.getQualifier().equals(attr.getQualifier())) {
                                toRemove = member;
                                break out;
                            }
                        }
                    }
                    if (null != toRemove) members.remove(toRemove);
                }
            });
        } catch (InterruptedException e) { /* do nothing */ }
    }

    private ServerAttribute findSelectedPmAttribute() {
        return getServerDolphin().getAt(PM_ID_SELECTED).getAt(ATT_SEL_PM_ID);
    }
}


