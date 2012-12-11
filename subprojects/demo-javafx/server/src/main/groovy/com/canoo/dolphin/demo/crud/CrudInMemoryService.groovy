package com.canoo.dolphin.demo.crud

class CrudInMemoryService implements CrudService {
    List<Map> listPortfolios() {
        return [
            [domainId:1, name:'Balanced',total:100,fixed:false],
            [domainId:2, name:'Growth',  total:100,fixed:false],
            [domainId:3, name:'Risky',   total:100,fixed:false],
            [domainId:4, name:'Insane',  total:100,fixed:false],
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
