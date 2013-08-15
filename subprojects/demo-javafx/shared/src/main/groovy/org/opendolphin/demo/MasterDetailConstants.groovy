package org.opendolphin.demo

class MasterDetailConstants {
    static final String CMD_PULL            = unique 'pullSoccerPlayers'

    static final String TYPE_SOCCERPLAYER   = unique('soccerPlayer')

    static final String ATT_NAME            = 'Name'
    static final String ATT_RANK            = 'Rank'
    static final String ATT_YEAROFBIRTH     = 'YearOfBirth'
    static final String ATT_COUNTRY         = 'Country'
    static final String ATT_MATCHESFIFA     = 'Matches(FIFA)'
    static final String ATT_MATCHESRSSSF    = 'Matches(RSSSF)'


    static String unique(String part) {
        MasterDetailConstants.name + '-' + part
    }

    static String qualify(String id, String attributeName) {
        unique id + '.' + attributeName
    }
}
