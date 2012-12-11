package com.canoo.dolphin.demo.crud

class CrudConstants {

    public static final String CMD_PULL_PORTFOLIOS      = 'pullPortfolios'
    public static final String CMD_UPDATE_TOTAL         = 'updateTotal'
    public static final String CMD_PULL_POSITIONS       = 'pullPositions'

    public static final String TYPE_PORTFOLIO           = 'Portfolio'
    public static final String TYPE_POSITION            = 'Position'

    public static final String PM_SELECTED_PORTFOLIO    = 'selectedPortfolio'


    static pmId(String type, int index) { type + "-" + index}


}
