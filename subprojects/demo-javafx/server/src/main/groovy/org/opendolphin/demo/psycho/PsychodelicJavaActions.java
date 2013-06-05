package org.opendolphin.demo.psycho;

import org.opendolphin.core.comm.Command;
import org.opendolphin.core.comm.DataCommand;
import org.opendolphin.core.server.*;
import org.opendolphin.core.server.action.DolphinServerAction;
import org.opendolphin.core.server.comm.ActionRegistry;
import org.opendolphin.core.server.comm.CommandHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.opendolphin.demo.psycho.RectangleConstants.*;

public class PsychodelicJavaActions extends DolphinServerAction {


    @Override
   	public void registerIn(ActionRegistry actionRegistry) {

   		// create presentation models

   		actionRegistry.register(CommandConstants.COMMAND_DIAGRAM_PULL, new CommandHandler<Command>() {
   			@Override
   			public void handleCommand(Command command, List<Command> response) {
   				// System.out.println("Server got command: " + command);
   				long startTime = System.currentTimeMillis();
   				int maxX = 33;
   				int maxY = 33;
   				int count = 0;
   				for (int x = 0; x < maxX; x++) {
   					for (int y = 0; y < maxY; y++) {
   						count++;
   						ServerDolphin.clientSideModel(response, "Rect_"+x+"_"+y, RectangleConstants.PM, new DTO(
                                new Slot(ATTR_X, x * 10),
                                new Slot(ATTR_Y, y * 10),
                                new Slot(ATTR_WIDTH, 10),
                                new Slot(ATTR_HEIGHT, 10),
                                new Slot(ATTR_STROKE_COLOR, "black"),
                                new Slot(ATTR_FILL_COLOR, "red")));
   					}
   				}
   				System.out.println(count + " presentation models created in "
   						+ (System.currentTimeMillis() - startTime) + " ms.");
   			}
   		});

   		actionRegistry.register(CommandConstants.COMMAND_DIAGRAM_UPDATE, new CommandHandler<Command>() {
   			@Override
   			public void handleCommand(Command command, List<Command> response) {
//   				System.out.println("Server got command: " + command);
//   				List<PresentationModel> presentationModels = getServerDolphin()
//   						.findAllPresentationModelsByType(RectangleConstants.PM);
//                if (presentationModels.size() < 1) return;
//                for (int i = 0; i < 10; i++) {
//   					changeValue(
//   							(ServerAttribute) presentationModels.get((int) (Math.random() * presentationModels.size()))
//   									.getAt(RectangleConstants.ATTR_FILL_COLOR), getRandomColor());
//   				}

                for (int i = 0; i < 10; i++) {
                    Map data = new HashMap(10);
                    data.put(ATTR_X, (int) (Math.random() * 33));
                    data.put(ATTR_Y, (int) (Math.random() * 33));
                    data.put(ATTR_FILL_COLOR, getRandomColor());
                    response.add(new DataCommand(data));
                }


   				// System.out.println("update send");
   			}
   		});

   	}

   	private String getRandomColor() {
   		String[] letters = "0123456789ABCDEF".split("");
   		String color = "#";
   		for (int i = 0; i < 6; i++) {
   			color += letters[(int) Math.round(Math.random() * 15) + 1];
   		}
   		return color;
   	}

}
