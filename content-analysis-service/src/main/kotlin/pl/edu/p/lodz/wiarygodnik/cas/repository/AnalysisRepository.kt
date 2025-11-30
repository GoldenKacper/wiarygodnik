package pl.edu.p.lodz.wiarygodnik.cas.repository

import org.springframework.data.jpa.repository.JpaRepository
import pl.edu.p.lodz.wiarygodnik.cas.model.AnalysisEntity

interface AnalysisRepository : JpaRepository<AnalysisEntity, Long>