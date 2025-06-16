package pl.edu.p.lodz.wiarygodnik.report.amqp

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component
import pl.edu.p.lodz.wiarygodnik.report.amqp.RabbitMQConfig.Companion.CREDIBILITY_SERVICE_RESULTS_QUEUE
import pl.edu.p.lodz.wiarygodnik.report.service.ReportService
import pl.edu.p.lodz.wiarygodnik.report.service.dto.ReportContent

@Component
class RabbitMQConsumer(private val reportService: ReportService) {

    private val log = KotlinLogging.logger {}

    @RabbitListener(queues = [CREDIBILITY_SERVICE_RESULTS_QUEUE])
    fun credibilityServiceResultsQueue(message: ReportContent) {
        reportService.updateReportContent(message)
        log.info { "Received message: $message" }
    }

}