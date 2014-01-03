package org.opendolphin.demo.teammember;

import java.util.List;

import org.opendolphin.core.comm.Command;
import org.opendolphin.core.comm.SavedPresentationModelNotification;
import org.opendolphin.core.server.DTO;
import org.opendolphin.core.server.ServerAttribute;
import org.opendolphin.core.server.Slot;
import org.opendolphin.core.server.action.DolphinServerAction;
import org.opendolphin.core.server.comm.ActionRegistry;
import org.opendolphin.core.server.comm.CommandHandler;

import static org.opendolphin.demo.teammember.TeamMemberConstants.*;

public class TeamMemberActions extends DolphinServerAction {

    private int count = 0;
    @Override
    public void registerIn(ActionRegistry actionRegistry) {
        actionRegistry.register(CMD_ADD, new CommandHandler<Command>() {
            @Override
            public void handleCommand(Command command, List<Command> response) {
                count++;
                final DTO dto = new DTO(
                        new Slot(ATT_FIRSTNAME,  "",    qualifier(count, ATT_FIRSTNAME)),
                        new Slot(ATT_LASTNAME,   "",    qualifier(count, ATT_LASTNAME)),
                        new Slot(ATT_FUNCTION,   "",    qualifier(count, ATT_FUNCTION)),
                        new Slot(ATT_AVAILABLE,  false, qualifier(count, ATT_AVAILABLE)),
                        new Slot(ATT_CONTRACTOR, false, qualifier(count, ATT_CONTRACTOR)),
                        new Slot(ATT_WORKLOAD,   0,     qualifier(count, ATT_WORKLOAD))
                        );
                String addedId = uniqueId(count);
                presentationModel(addedId, TYPE_TEAM_MEMBER, dto);

                // it is a server-side decision that after creating a new team member,
                // it should be immediately selected
                changeValue(findSelectedPmAttribute(), addedId);
            }
        });
        
        actionRegistry.register(CMD_SAVE, new CommandHandler<Command>() {
            @Override
            public void handleCommand(Command command, List<Command> response) {
                String pmIdToSave = (String) findSelectedPmAttribute().getValue();
                // saving the model to the database here. We assume all was ok:
                response.add(new SavedPresentationModelNotification(pmIdToSave) );
            }
        });
        
    }

    private ServerAttribute findSelectedPmAttribute() {
        return getServerDolphin().getAt(PM_ID_SELECTED).getAt(ATT_SEL_PM_ID);
    }
}
