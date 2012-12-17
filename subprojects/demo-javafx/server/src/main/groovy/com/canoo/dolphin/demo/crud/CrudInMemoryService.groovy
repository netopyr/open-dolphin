package com.canoo.dolphin.demo.crud

import com.canoo.dolphin.core.server.DTO
import com.canoo.dolphin.core.server.Slot

import static com.canoo.dolphin.demo.crud.CrudConstants.*

class CrudInMemoryService implements CrudService {

    // Java-like variant
    List<DTO> listPortfolios(long ownerId) {
        List<DTO> result = new LinkedList<DTO>();
        result.add(new DTO(
            new Slot(ATT_DOMAIN_ID, 1),
            new Slot(ATT_NAME, 'Balanced'),
            new Slot(ATT_TOTAL, 100),
            new Slot(ATT_FIXED, false)
        ));
        result.add(new DTO(
            new Slot(ATT_DOMAIN_ID, 2),
            new Slot(ATT_NAME, 'Growth'),
            new Slot(ATT_TOTAL, 100),
            new Slot(ATT_FIXED, false)
        ));
        result.add(new DTO(
            new Slot(ATT_DOMAIN_ID, 3),
            new Slot(ATT_NAME, 'Risky'),
            new Slot(ATT_TOTAL, 100),
            new Slot(ATT_FIXED, false)
        ));
        result.add(new DTO(
            new Slot(ATT_DOMAIN_ID, 4),
            new Slot(ATT_NAME, 'Insane'),
            new Slot(ATT_TOTAL, 100),
            new Slot(ATT_FIXED, false)
        ));
        return result;
    }

    // Groovy-like variant
    List<DTO> listPositions(long portfolioId) {
        [
            [(ATT_INSTRUMENT): 'ORCL', (ATT_WEIGHT): 10],
            [(ATT_INSTRUMENT): 'APPL', (ATT_WEIGHT): 40],
            [(ATT_INSTRUMENT): 'IBM',  (ATT_WEIGHT): 30],
            [(ATT_INSTRUMENT): 'UBSN', (ATT_WEIGHT): 20],
        ].collect { new DTO(Slot.slots(it)) }
    }
}
