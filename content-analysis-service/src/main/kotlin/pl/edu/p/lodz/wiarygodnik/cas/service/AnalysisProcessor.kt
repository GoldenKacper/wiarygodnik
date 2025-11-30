package pl.edu.p.lodz.wiarygodnik.cas.service

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.springframework.stereotype.Component
import pl.edu.p.lodz.wiarygodnik.cas.amqp.RabbitMQProducer
import pl.edu.p.lodz.wiarygodnik.cas.model.AnalysisEntity
import pl.edu.p.lodz.wiarygodnik.cas.model.AnalysisStatus
import pl.edu.p.lodz.wiarygodnik.cas.model.AnalysisStatus.*
import pl.edu.p.lodz.wiarygodnik.cas.model.dto.AnalysisResult
import pl.edu.p.lodz.wiarygodnik.cas.model.dto.ContentAnalysis
import pl.edu.p.lodz.wiarygodnik.cas.model.dto.ContentComparison
import pl.edu.p.lodz.wiarygodnik.cas.model.dto.ScrapedWebContent
import pl.edu.p.lodz.wiarygodnik.cas.repository.AnalysisRepository
import pl.edu.p.lodz.wiarygodnik.cas.service.analyser.ContentAnalyser
import pl.edu.p.lodz.wiarygodnik.cas.service.comparator.ContentComparator
import pl.edu.p.lodz.wiarygodnik.cas.service.scraper.WebScraper
import pl.edu.p.lodz.wiarygodnik.cas.service.searcher.KeywordWebSearcher
import kotlin.coroutines.CoroutineContext

@Component
class AnalysisProcessor(
    private val webScraper: WebScraper,
    private val contentAnalyzer: ContentAnalyser,
    private val keywordWebSearcher: KeywordWebSearcher,
    private val contentComparator: ContentComparator,
    private val analysisRepository: AnalysisRepository,
    private val producer: RabbitMQProducer
) : CoroutineScope {

    override val coroutineContext: CoroutineContext = SupervisorJob() + Dispatchers.IO

    private val log = KotlinLogging.logger {}

    fun analyse(url: String): AnalysisEntity {
        val analysisEntity = prepareNewAnalysis(url)
        val persistedAnalysis = analysisRepository.save(analysisEntity)

        asyncProcessAnalysis(url, persistedAnalysis)

        return persistedAnalysis
    }

    private fun prepareNewAnalysis(url: String) = AnalysisEntity(sourceUrl = url, status = ANALYSING_CONTENT)

    private fun asyncProcessAnalysis(url: String, analysisEntity: AnalysisEntity) = launch {
        try {
            log.info { "Analysis [id: ${analysisEntity.id}] for url: $url process started." }

            val scrapedWebContent: ScrapedWebContent = webScraper.scrape(url)
            val analysis: ContentAnalysis = contentAnalyzer.analyse(scrapedWebContent.text)

            switchAnalysisStatus(analysisEntity, COMPARING_SIMILAR_SOURCES)
            val urls: List<String> = keywordWebSearcher.searchTopUrls(analysis.summarization.keywords)
            val scrapedSimilarWebContents: List<ScrapedWebContent> = urls.map { webScraper.scrape(it) }
            val comparison: ContentComparison =
                contentComparator.compare(analysis.summarization.description, scrapedSimilarWebContents)
            switchAnalysisStatus(analysisEntity, COMPLETED)

            val result = AnalysisResult(analysisEntity.id, analysis, comparison)
            log.info { "Analysis [id: ${analysisEntity.id}] for url: $url process finished. Sending result for report generation." }
            producer.sendAnalysis(result)
        } catch (e: Exception) {
            log.error { "Error while analysing analysis: $e" }
            switchAnalysisStatus(analysisEntity, FAILED)
        }
    }

    private fun switchAnalysisStatus(analysis: AnalysisEntity, status: AnalysisStatus) {
        analysis.status = status
        analysisRepository.save(analysis)
    }

}