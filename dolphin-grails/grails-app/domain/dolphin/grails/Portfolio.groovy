package dolphin.grails

class Portfolio {

    String name
    boolean fixed

    static belongsTo = [owner: User]

    String toString() { "$owner's $name portfolio" }
}
