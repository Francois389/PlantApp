package com.fsp.plantapp.exportation

import com.fsp.plantapp.export.ExportViewModel.NameFormat.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class NameFormatTest {

    @Nested
    inner class Formatteur {
        @Test
        fun `test de Default`() {
            val nomOriginale = "ce nom a était Originale !"

            val nomFormatte = Default.formatteur(nomOriginale)

            assertEquals(nomOriginale, nomFormatte, "Default ne dois rien changé")
        }

        @Test
        fun `test de SansAccent`() {
            val nomAccentue = "nom avec é des accent àê"
            val nomSansAccent = "nom avec e des accent ae"

            val nomFormatte = SansAccent.formatteur(nomAccentue)

            assertEquals(nomSansAccent, nomFormatte)
        }

        @Test
        fun `test de SansAccent avec que des accents`() {
            val nomAccentue = "éâàèÉ"
            val nomSansAccent = "eaaeE"

            val nomFormatte = SansAccent.formatteur(nomAccentue)

            assertEquals(nomSansAccent, nomFormatte)
        }

        @Test
        fun `test de CamelCase`() {
            val nom = "nom pas camel case !"
            val nomCamelCase = "NomPasCamelCase"

            val nomFormatte = CamelCase.formatteur(nom)

            assertEquals(nomCamelCase, nomFormatte)
        }

        @Test
        fun `test de SnakeCase`() {
            val nom = "nom snake CASE !"
            val nomSnakeCase = "nom_snake_case"

            val nomFormatte = SnakeCase.formatteur(nom)

            assertEquals(nomSnakeCase, nomFormatte)
        }
    }
}