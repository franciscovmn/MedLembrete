package br.edu.ifpb.pdm.medlembrete.ui.util

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

private val FORMATO_DATA_BR = SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("pt-BR"))

fun Timestamp.toDataFormatada(): String = FORMATO_DATA_BR.format(toDate())
