import dolphin.grails.Instrument
import dolphin.grails.Portfolio
import dolphin.grails.Position
import dolphin.grails.User
import grails.util.Environment

class BootStrap {

    def init = { servletContext ->
        if (Environment.DEVELOPMENT != Environment.current) return

        def dierk = new User(firstName: 'Dierk', lastName: 'Koenig').save()

        def portfolios = ['Balanced', 'Growth', 'Lucky', 'Lunacy', 'Sheer Madness']
            .collect { new Portfolio(owner: dierk, name: it).save() }

        def instruments = ['UBSN', 'IBM', 'APPL', 'MSFT', 'CANO']
            .collect { new Instrument(name: it).save() }

        def weights = [
            [ 50, 20, 20, 10,  0 ],
            [ 50, 20, 30, 10, 10 ],
            [ 30, 30, 30, 20, 10 ],
            [ 10, 20, 30, 20, 20 ],
            [ 10, 10, 20, 10, 50 ],
        ]

        portfolios.eachWithIndex { portfolio, px ->
            instruments.eachWithIndex { instrument, ix ->
                new Position(portfolio: portfolio, instrument: instrument, weight: weights[px][ix]).save()
            }
        }

    }

}
