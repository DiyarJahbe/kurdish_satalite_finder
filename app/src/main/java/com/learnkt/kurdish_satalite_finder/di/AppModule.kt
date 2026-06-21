package com.learnkt.kurdish_satalite_finder.di

import android.content.Context
import androidx.room.Room
import com.learnkt.kurdish_satalite_finder.data.local.SatelliteDatabase
import com.learnkt.kurdish_satalite_finder.data.local.dao.SatelliteDao
import com.learnkt.kurdish_satalite_finder.data.preferences.OnboardingPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSatelliteDatabase(@ApplicationContext context: Context): SatelliteDatabase {
        return Room.databaseBuilder(
            context,
            SatelliteDatabase::class.java,
            SatelliteDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideSatelliteDao(db: SatelliteDatabase): SatelliteDao {
        return db.satelliteDao
    }

    @Provides
    @Singleton
    fun provideOnboardingPreferences(@ApplicationContext context: Context): OnboardingPreferences {
        return OnboardingPreferences(context)
    }
}
