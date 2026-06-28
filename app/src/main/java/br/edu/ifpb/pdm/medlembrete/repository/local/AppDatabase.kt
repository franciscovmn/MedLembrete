package br.edu.ifpb.pdm.medlembrete.repository.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Banco de dados local do app (Room).
 * Por enquanto guarda apenas o cache offline de medicamentos.
 */
@Database(entities = [MedicamentoEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun medicamentoDao(): MedicamentoDao

    companion object {
        private const val DATABASE_NAME = "medlembrete.db"

        fun build(context: Context): AppDatabase =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            ).build()
    }
}
