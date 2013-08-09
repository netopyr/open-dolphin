package org.opendolphin.demo

import org.opendolphin.core.comm.Command

import org.opendolphin.core.comm.NamedCommand
import org.opendolphin.core.server.DTO
import org.opendolphin.core.server.Slot
import org.opendolphin.core.server.action.DolphinServerAction
import org.opendolphin.core.server.comm.ActionRegistry

import static org.opendolphin.demo.MasterDetailConstants.*
import static com.xlson.groovycsv.CsvParser.parseCsv

public class MasterDetailActions extends DolphinServerAction {

    @Override
    public void registerIn(ActionRegistry registry) {

        def soccerPlayersData = getSoccerPlayerData()
        registry.register CMD_PULL, { NamedCommand command, List<Command> response ->
            soccerPlayersData.eachWithIndex { SoccerPlayer soccerPlayer, index ->
                def pmId = index.toString()
                presentationModel(pmId, TYPE_SOCCERPLAYER, new DTO(
                        new Slot(ATT_RANK, soccerPlayer.rank, qualify(pmId, ATT_RANK)),
                        new Slot(ATT_NAME, soccerPlayer.name, qualify(pmId, ATT_NAME))
                ))
            }
        }
    }


    private List<SoccerPlayer> getSoccerPlayerData() {
        def soccerPlayerList = new ArrayList<SoccerPlayer>()
        def csv = '''Name:Rank
Player1:1
Player2:2'''

        def data = parseCsv(csv, autoDetect: true)
        for (line in data) {
            soccerPlayerList.add new SoccerPlayer(name: line.Name, rank: line.Rank)
        }
        return soccerPlayerList
    }

    class SoccerPlayer {
        def name
        def rank
    }
}
