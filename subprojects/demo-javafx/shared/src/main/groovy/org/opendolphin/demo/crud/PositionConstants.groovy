package org.opendolphin.demo.crud

class PositionConstants {

    static class TYPE {
        public static final String POSITION         = PositionConstants.unique 'type'
    }

    static class ATT {
        public static final String PORTFOLIO_ID     = 'portfolioId'
        public static final String INSTRUMENT       = 'instrument'
        public static final String WEIGHT           = 'weight'
    }

    static class CMD {
        public static final String PULL             = PositionConstants.unique 'pull'
    }

    static String unique(String s) { PositionConstants.class.name + '.'+ s }


}
