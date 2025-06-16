package pl.edu.p.lodz.wiarygodnik.report.service

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import pl.edu.p.lodz.wiarygodnik.report.amqp.RabbitMQProducer
import pl.edu.p.lodz.wiarygodnik.report.controller.dto.UrlRequest
import pl.edu.p.lodz.wiarygodnik.report.model.Report
import pl.edu.p.lodz.wiarygodnik.report.repo.ReportRepository
import pl.edu.p.lodz.wiarygodnik.report.service.dto.ReportContent
import pl.edu.p.lodz.wiarygodnik.report.service.dto.ReportCreationOrder

@Service
class ReportService(
    private val producer: RabbitMQProducer,
    private val reportRepository: ReportRepository
) {

    private val log = KotlinLogging.logger {}

    fun orderReportCreation(urlRequest: UrlRequest): Long {
        val newReport = Report(sourceUrl = urlRequest.url)
        val persistedReport = reportRepository.save(newReport)
        log.info { "Initial report persisted to database, reportId: ${persistedReport.id}" }

        val reportCreationOrder = ReportCreationOrder(persistedReport.id, persistedReport.sourceUrl)
        producer.sendReportCreationOrder(reportCreationOrder)
        return persistedReport.id
    }

    fun updateReportContent(reportContent: ReportContent) {
        reportRepository.findByIdOrNull(reportContent.reportId)?.let {
            it.content = reportContent.urls.joinToString(separator = "\n")
            val updatedReport = reportRepository.save(it)
            log.info { "Report content updated, reportId: ${updatedReport.id}" }
        }
    }

    fun getReportContent(reportId: Long): String {
        val report = reportRepository.findByIdOrNull(reportId) ?: throw NoSuchElementException("Report not found")
        return report.content
    }

}