package dolphin.grails

class MoreTimeController {

    static counter = 0

    def index() {

//        sleep 1000
        render text:"Hi, Kunal! " + (counter++)
    }
}
