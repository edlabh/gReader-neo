package com.greader.neo.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        FeedEntity::class,
        TagEntity::class,
        Tag2SubEntity::class,
        ArticleEntity::class,
        Tag2ArticleEntity::class,
        SyncOperationEntity::class,
    ],
    version = 2,
    exportSchema = false,
)
abstract class NeoDatabase : RoomDatabase() {
    abstract fun feedDao(): FeedDao
    abstract fun articleDao(): ArticleDao
    abstract fun tagDao(): TagDao
    abstract fun tag2SubDao(): Tag2SubDao
    abstract fun syncOperationDao(): SyncOperationDao
}
