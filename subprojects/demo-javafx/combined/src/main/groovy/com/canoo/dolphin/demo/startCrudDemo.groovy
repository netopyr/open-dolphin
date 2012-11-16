/*
 * Copyright 2012 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.canoo.dolphin.demo

import com.canoo.dolphin.core.comm.CreatePresentationModelCommand

def config = new JavaFxInMemoryConfig()
def serverDolphin = config.serverDolphin

serverDolphin.action 'pullPortfolios', { cmd, response ->
    def portfolios = [
            [name:'Balanced',total:100,fixed:false],
            [name:'Growth',  total: 40,fixed:false],
            [name:'Risky',   total: 30,fixed:false],
            [name:'Insane',  total: 20,fixed:false],
    ]
    portfolios.eachWithIndex{ portfolio, index ->
        def pm = serverDolphin.presentationModel(portfolio, "Portfolio-$index", 'Portfolio')
        response << CreatePresentationModelCommand.makeFrom(pm)
    }
}

serverDolphin.action 'pullPositions', { cmd, response ->
    def selectedPortfolio = serverDolphin.findPresentationModelById('selectedPortfolio')
    def positions = [
            [instrument:'ORCL',weight:10],
            [instrument:'APPL',weight:40],
            [instrument:'IBM', weight:30],
            [instrument:'UBSN',weight:20],
    ]
    positions.eachWithIndex{ position, index ->
        position.portfolioId = selectedPortfolio.domainId.value
        def pm = serverDolphin.presentationModel(position, "position-$index", 'Position')
        response << CreatePresentationModelCommand.makeFrom(pm)
    }
}

serverDolphin.action 'updateTotal', { cmd, response ->
    def selectedPortfolio = serverDolphin.findPresentationModelById('selectedPortfolio')
    def portfolioDomainId = selectedPortfolio.domainId.value
    def allPositions = serverDolphin.findAllPresentationModelsByType('Position')
    def positions = allPositions.findAll{ it.portfolioId.value == portfolioDomainId }
    def sum = positions.sum { it.weight.value }
    response << selectedPortfolio.total.changeValueCommand(sum) // todo: serverDolphin.setValue(attribute, newValue)
}


CrudView.show(config.clientDolphin)