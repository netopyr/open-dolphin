package dolphin.grails

class Position {

    Instrument  instrument
    int         weight

    static belongsTo = [portfolio: Portfolio]

    static constraints = {
        instrument nullable:    false
        weight     min:         0
    }
}
