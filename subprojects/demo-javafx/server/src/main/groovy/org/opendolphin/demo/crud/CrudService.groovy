package org.opendolphin.demo.crud

import org.opendolphin.core.server.DTO

interface CrudService {
    List<DTO> listPortfolios(long ownerId)
    List<DTO> listPositions(long portfolioId)
}
