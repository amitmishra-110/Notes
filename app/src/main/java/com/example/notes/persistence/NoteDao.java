package com.example.notes.persistence;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.notes.models.Note;

import java.util.List;

@Dao
public interface NoteDao
{
    @Insert
    long[] insertNotes(Note...notes);

    @Query("SELECT * from notes")
    LiveData<List<Note>> getNotes();

    @Query("SELECT * from notes where title like:title")
    List<Note> getNoteWithCustomQuery(String title);

    @Delete
    int delete(Note...note);

    @Update
    int update(Note...note);


}
