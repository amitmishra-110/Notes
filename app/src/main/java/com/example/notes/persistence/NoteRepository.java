package com.example.notes.persistence;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.notes.async.DeleteAsyncTask;
import com.example.notes.async.InsertAsyncTask;
import com.example.notes.async.UpdateAsyncTask;
import com.example.notes.models.Note;

import java.util.List;

public class NoteRepository
{
    private NoteDatabase mNoteDatabase;

    public NoteRepository(Context context)
    {
        mNoteDatabase = NoteDatabase.getInstance(context);
    }
    public void insertNoteTask(Note notes)
    {
        new InsertAsyncTask(mNoteDatabase.getNoteDao()).execute(notes);
        
    }
    public void updateNote(Note note )
    {
        new UpdateAsyncTask(mNoteDatabase.getNoteDao()).execute(note);

    }
    public LiveData<List<Note>> retrieveNotesTask()
    {
        return mNoteDatabase.getNoteDao().getNotes();
    }
    public void delNotes(Note note)
    {
        new DeleteAsyncTask(mNoteDatabase.getNoteDao()).execute(note);
        
    }
}
