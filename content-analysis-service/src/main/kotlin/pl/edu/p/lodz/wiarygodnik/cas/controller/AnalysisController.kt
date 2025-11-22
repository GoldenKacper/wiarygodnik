package pl.edu.p.lodz.wiarygodnik.cas.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pl.edu.p.lodz.wiarygodnik.cas.controller.dto.AnalyseRequest
import pl.edu.p.lodz.wiarygodnik.cas.service.ContentAnalyser
import pl.edu.p.lodz.wiarygodnik.cas.service.KeywordWebSearcher
import pl.edu.p.lodz.wiarygodnik.cas.service.WebScraper
import pl.edu.p.lodz.wiarygodnik.cas.service.dto.ScrapedWebContent
import pl.edu.p.lodz.wiarygodnik.cas.service.dto.ContentAnalysis

@RestController
@RequestMapping("/api")
class AnalysisController(
    private val webScraper: WebScraper,
    private val contentAnalyser: ContentAnalyser,
    private val keywordWebSearcher: KeywordWebSearcher
) {

    @PostMapping("/analyse")
    fun analyze(@RequestBody request: AnalyseRequest): ResponseEntity<List<String>> {
        val scrapedWebContent: ScrapedWebContent = webScraper.scrape(request.url)
        val analysis: ContentAnalysis = contentAnalyser.analyse(scrapedWebContent.text)
        val urls: List<String> = keywordWebSearcher.searchTopUrls(analysis.keywords)
        return ResponseEntity.ok(urls)
    }

}