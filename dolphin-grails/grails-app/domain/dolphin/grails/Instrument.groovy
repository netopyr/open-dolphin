package dolphin.grails

class Instrument {

    String name

    static constraints = {
        name unique: true
    }

    String toString() { name }
}
