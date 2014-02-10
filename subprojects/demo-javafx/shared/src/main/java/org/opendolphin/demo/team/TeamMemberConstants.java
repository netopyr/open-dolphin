package org.opendolphin.demo.team;

public class TeamMemberConstants {

    public static final String PM_ID_MOLD       = "teamMemberMold";
    public static final String TYPE_TEAM_MEMBER = "teamMember";

    public static final String CMD_ADD        = TYPE_TEAM_MEMBER + ".add";
    public static final String CMD_REMOVE     = TYPE_TEAM_MEMBER + ".remove";
    public static final String CMD_SAVE       = TYPE_TEAM_MEMBER + ".save";
    public static final String CMD_INIT       = TYPE_TEAM_MEMBER + ".init";
    public static final String CMD_RELEASE    = TYPE_TEAM_MEMBER + ".release";
    public static final String ACTION_ON_PUSH = TYPE_TEAM_MEMBER + ".on.push";

    public static final String ATT_FIRSTNAME  = "firstName";
    public static final String ATT_LASTNAME   = "lastName";
    public static final String ATT_FUNCTION   = "function";
    public static final String ATT_AVAILABLE  = "available";
    public static final String ATT_CONTRACTOR = "contractor";
    public static final String ATT_WORKLOAD   = "workLoad";

    public static final String PM_ID_SELECTED = "selectedTeamMember";
    public static final String ATT_SEL_PM_ID  = "selectedPmId";
    public static final String QUAL_SEL_PM_ID = "teammember.selected.id";

    public static final String[] FUNCTION_NAMES = {"", "Engineer", "Architect", "Administrator", "Consultant", "CFO", "CTO", "CEO"};

    public static String uniqueId(int count) {
        return TYPE_TEAM_MEMBER + "." + count;
    }

    public static String qualifier(int count, String propertyName){
        return uniqueId(count) + "." + propertyName;
    }

}
