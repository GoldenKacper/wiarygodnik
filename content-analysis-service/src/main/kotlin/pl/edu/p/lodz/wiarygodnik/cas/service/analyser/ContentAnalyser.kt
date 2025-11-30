package pl.edu.p.lodz.wiarygodnik.cas.service.analyser

import pl.edu.p.lodz.wiarygodnik.cas.model.dto.ContentAnalysis

interface ContentAnalyser {
    fun analyse(content: String): ContentAnalysis
}