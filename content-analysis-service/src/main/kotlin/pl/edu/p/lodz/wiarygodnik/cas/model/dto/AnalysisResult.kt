package pl.edu.p.lodz.wiarygodnik.cas.model.dto

data class AnalysisResult(
    val analysisId: Long,
    val contentAnalysis: ContentAnalysis,
    val contentComparison: ContentComparison
)
