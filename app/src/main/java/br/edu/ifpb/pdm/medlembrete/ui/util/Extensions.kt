package br.edu.ifpb.pdm.medlembrete.ui.util

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

private val FORMATO_DATA_BR = SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("pt-BR"))

fun Timestamp.toDataFormatada(): String = FORMATO_DATA_BR.format(toDate())

val mapeamentoPtEn = mapOf(
    "dipirona" to "dipyrone",
    "ibuprofeno" to "ibuprofen",
    "paracetamol" to "acetaminophen",
    "amoxicilina" to "amoxicillin",
    "losartana" to "losartan",
    "metformina" to "metformin",
    "atorvastatina" to "atorvastatin",
    "omeprazol" to "omeprazole",
    "sinvastatina" to "simvastatin",
    "captopril" to "captopril",
    "amlodipina" to "amlodipine",
    "hidroxicloroquina" to "hydroxychloroquine",
    "azitromicina" to "azithromycin",
    "prednisona" to "prednisone",
    "dexametasona" to "dexamethasone",
    "clonazepam" to "clonazepam",
    "fluoxetina" to "fluoxetine",
    "sertralina" to "sertraline",
    "enalapril" to "enalapril",
    "furosemida" to "furosemide"
)

fun resolverNomeEn(nomePt: String, nomeEnManual: String?): String? {
    if (!nomeEnManual.isNullOrBlank()) return nomeEnManual.trim()
    return mapeamentoPtEn[nomePt.trim().lowercase()]
}
