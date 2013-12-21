package org.opendolphin.demo.teammember;

import java.util.List;

import org.opendolphin.core.Attribute;
import org.opendolphin.core.comm.Command;
import org.opendolphin.core.server.DTO;
import org.opendolphin.core.server.ServerAttribute;
import org.opendolphin.core.server.ServerDolphin;
import org.opendolphin.core.server.ServerPresentationModel;
import org.opendolphin.core.server.Slot;
import org.opendolphin.core.server.action.DolphinServerAction;
import org.opendolphin.core.server.comm.ActionRegistry;
import org.opendolphin.core.server.comm.CommandHandler;

public class TeamMemberActions extends DolphinServerAction {

    private int count = 0;
    @Override
    public void registerIn(ActionRegistry actionRegistry) {
        actionRegistry.register("add", new CommandHandler<Command>() {
            @Override
            public void handleCommand(Command command, List<Command> response) {
                count++;
                final DTO dto = new DTO(
                        new Slot(TeamMemberConstants.FIRSTNAME, "", "teamMember." + count + "."+TeamMemberConstants.FIRSTNAME),
                        new Slot(TeamMemberConstants.LASTNAME, "", "teamMember." + count + "."+TeamMemberConstants.LASTNAME),
                        new Slot(TeamMemberConstants.FUNCTION, "", "teamMember." + count + "."+TeamMemberConstants.FUNCTION),
                        new Slot(TeamMemberConstants.AVAILABLE, "", "teamMember." + count + "."+TeamMemberConstants.AVAILABLE),
                        new Slot(TeamMemberConstants.CONTRACTOR, "", "teamMember." + count + "."+TeamMemberConstants.CONTRACTOR),
                        new Slot(TeamMemberConstants.WORKLOAD, "", "teamMember." + count + "."+TeamMemberConstants.WORKLOAD)
                        );
                presentationModel("teamMember." + count, "teamMember", dto);
            }
        });
        
        actionRegistry.register("save", new CommandHandler<Command>() {
            @Override
            public void handleCommand(Command command, List<Command> response) {
                ServerPresentationModel model = getServerDolphin().getAt("teamMemberMold");
                for(Attribute attribute:model.getAttributes()){
                    ServerDolphin.rebase(response,(ServerAttribute)attribute);
                }
               
            }
        });
        
    }
}
