package pl.edu.p.lodz.wiarygodnik.cas.service.comparator

import org.springframework.stereotype.Component
import pl.edu.p.lodz.wiarygodnik.cas.model.dto.ContentComparison
import pl.edu.p.lodz.wiarygodnik.cas.model.dto.ContentSummarization
import pl.edu.p.lodz.wiarygodnik.cas.model.dto.ScrapedWebContent
import pl.edu.p.lodz.wiarygodnik.cas.service.agent.ContentComparisonAgent
import pl.edu.p.lodz.wiarygodnik.cas.service.agent.ContentSummarizationAgent

@Component
class AiContentComparator(
    private val contentSummarizationAgent: ContentSummarizationAgent,
    private val contentComparisonAgent: ContentComparisonAgent
) : ContentComparator {

    override fun compare(
        mainSourceDescription: String,
        similarSourceContents: List<ScrapedWebContent>
    ): ContentComparison {
        var input: String = """
            GŁÓWNE ŹRÓDŁO:
            ${mainSourceDescription}
            
            PODOBNE ŹRÓDŁA:
            
        """.trimIndent()
        for (content in similarSourceContents) {
            val summarization: ContentSummarization = contentSummarizationAgent.work(content.text)
            input += """
                
                URL: ${content.url}
                ${summarization.description}
                
            """.trimIndent()
        }
        return contentComparisonAgent.work(input)
    }

}