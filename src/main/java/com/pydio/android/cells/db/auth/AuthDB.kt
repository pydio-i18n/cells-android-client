package com.pydio.android.cells.db.auth

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        RToken::class,
        RLegacyCredentials::class,
        ROAuthState::class,
    ],
    version = 1,
    // FIXME
//     exportSchema = true,
    exportSchema = false,
)

abstract class AuthDB : RoomDatabase() {

    abstract fun tokenDao(): TokenDao

    abstract fun legacyCredentialsDao(): LegacyCredentialsDao

    abstract fun authStateDao(): OAuthStateDao

}
