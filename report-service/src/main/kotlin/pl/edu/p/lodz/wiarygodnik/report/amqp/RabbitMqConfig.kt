package pl.edu.p.lodz.wiarygodnik.report.amqp

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.TopicExchange
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class RabbitMQConfig {

    companion object {
        const val EXCHANGE_NAME: String = "wiarygodnik.exchange"
        const val REPORT_SERVICE_ORDER_QUEUE: String = "report.service.order.queue"
        const val CREDIBILITY_SERVICE_RESULTS_QUEUE: String = "credibility.service.results.queue"
    }

    @Bean
    fun exchange(): TopicExchange {
        return TopicExchange(EXCHANGE_NAME)
    }

    @Bean
    fun reportServiceOrderQueue(): Queue {
        return Queue(REPORT_SERVICE_ORDER_QUEUE)
    }

    @Bean
    fun credibilityServiceResultsQueue(): Queue {
        return Queue(CREDIBILITY_SERVICE_RESULTS_QUEUE)
    }

    @Bean
    fun reportCreationOrderBinding(): Binding {
        return BindingBuilder.bind(reportServiceOrderQueue()).to(exchange()).with("report.creation.order.key")
    }

    @Bean
    fun reportCreatedBinding(): Binding {
        return BindingBuilder.bind(credibilityServiceResultsQueue()).to(exchange()).with("report.created.key")
    }

    @Bean
    fun jsonMessageConverter(): MessageConverter {
        return Jackson2JsonMessageConverter()
    }

    @Bean
    fun rabbitTemplate(connectionFactory: ConnectionFactory): RabbitTemplate {
        val template = RabbitTemplate(connectionFactory)
        template.messageConverter = jsonMessageConverter()
        return template
    }

}