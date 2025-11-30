package pl.edu.p.lodz.wiarygodnik.cas.service.comparator

import pl.edu.p.lodz.wiarygodnik.cas.model.dto.ContentComparison
import pl.edu.p.lodz.wiarygodnik.cas.model.dto.ScrapedWebContent

interface ContentComparator {
    fun compare(mainSourceDescription: String, similarSourceContents: List<ScrapedWebContent>): ContentComparison
}