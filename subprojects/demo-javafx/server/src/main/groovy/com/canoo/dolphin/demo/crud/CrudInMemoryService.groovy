package com.canoo.dolphin.demo.crud

class CrudInMemoryService implements CrudService {
    List<Map> listPortfolios() {
        return [
            [name:'Balanced',total:100,fixed:false],
            [name:'Growth',  total: 40,fixed:false],
            [name:'Risky',   total: 30,fixed:false],
            [name:'Insane',  total: 20,fixed:false],
        ]
    }
    List<Map> listPositions() {
        return [
            [instrument:'ORCL',weight:10],
            [instrument:'APPL',weight:40],
            [instrument:'IBM', weight:30],
            [instrument:'UBSN',weight:20],
        ]
    }
}
