package com.canoo.dolphin.demo.crud

import com.canoo.dolphin.core.server.DTO

interface CrudService {
    List<DTO> listPortfolios()
    List<DTO> listPositions()
}
