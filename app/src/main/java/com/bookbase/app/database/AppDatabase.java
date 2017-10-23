package com.bookbase.app.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.bookbase.app.utils.Converters;
import com.bookbase.app.model.dao.BookDao;
import com.bookbase.app.model.entity.Author;
import com.bookbase.app.model.entity.Book;

@Database(entities = {Book.class, Author.class}, version = 1, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase{

    public abstract BookDao bookDao();

}