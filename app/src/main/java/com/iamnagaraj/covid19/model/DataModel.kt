package com.iamnagaraj.covid19.model

data class DataModel(
    val data: List<Data>
)

data class Data(
    val active_cases: String,
    val country_other: String,
    val new_cases: String,
    val new_deaths: String,
    val serious_critical: String,
    val tot_cases_1m_pop: String,
    val total_cases: String,
    val total_deaths: String,
    val total_recovered: String
)