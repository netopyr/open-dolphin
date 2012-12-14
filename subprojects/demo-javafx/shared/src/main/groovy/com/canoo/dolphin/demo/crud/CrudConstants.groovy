package com.canoo.dolphin.demo.crud

class CrudConstants {

    public static final String CMD_PULL_PORTFOLIOS      = 'pullPortfolios'
    public static final String CMD_UPDATE_TOTAL         = 'updateTotal'
    public static final String CMD_PULL_POSITIONS       = 'pullPositions'

    public static final String TYPE_PORTFOLIO           = 'Portfolio'
    public static final String TYPE_POSITION            = 'Position'

    public static final String PM_SELECTED_PORTFOLIO    = 'selectedPortfolio'

    // portfolios
    public static final String ATT_NAME                 = 'name'
    public static final String ATT_FIXED                = 'fixed'
    public static final String ATT_TOTAL                = 'total'
    public static final String ATT_DOMAIN_ID            = 'domainId'

    // positions
    public static final String ATT_PORTFOLIO_ID         = 'portfolioId'
    public static final String ATT_INSTRUMENT           = 'instrument'
    public static final String ATT_WEIGHT               = 'weight'


    static String pmId(String type, int index) { type + "-" + index}


}
