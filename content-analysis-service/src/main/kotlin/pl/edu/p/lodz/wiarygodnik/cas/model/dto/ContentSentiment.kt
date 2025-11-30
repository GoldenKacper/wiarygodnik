package pl.edu.p.lodz.wiarygodnik.cas.model.dto

enum class Sentiment {
    NEUTRAL, POSITIVE, NEGATIVE, ALARMIST, IRONIC, PERSUASIVE, AGGRESSIVE, FORMAL
}

data class ContentSentiment(val description: String, val examples: List<SentimentExample>)
data class SentimentExample(val sentiment: Sentiment, val quotes: List<CommentedQuote>)
data class CommentedQuote(val quote: String, val comment: String)
