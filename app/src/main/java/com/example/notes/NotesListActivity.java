package com.example.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
//import androidx.recyclerview.widget.ItemTouchHelper.SimpleCallback;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.notes.adapters.NotesRecyclerAdapter;
import com.example.notes.models.Note;
import com.example.notes.persistence.NoteRepository;
import com.example.notes.util.VerticalSpacingItemDecorator;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class NotesListActivity extends AppCompatActivity implements  NotesRecyclerAdapter.OnNoteListener
    , View.OnClickListener
{
    private static final String TAG = "NotesListActivity";
    //UI COMPONENTS
    private RecyclerView mrecyclerview;
    private FloatingActionButton mFab;

    //VARIABLE SECTION
    private ArrayList<Note> mNotes=new ArrayList<>();
    private NotesRecyclerAdapter mNotesRecylerAdapter;
    private VerticalSpacingItemDecorator itemDecorator;
    private NoteRepository mNoteRepository;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Toast.makeText(this, "Swipe right to Delete", Toast.LENGTH_SHORT).show();
        setContentView(R.layout.activity_notes_list);
        mrecyclerview=findViewById(R.id.recyclerView);

        mFab=findViewById(R.id.fab);
        mFab.setOnClickListener(this);
        /*OR
            findViewById(fab).setOnClickListener(this);*/

        mNoteRepository=new NoteRepository(this);


        initRecyclerView();
        retreiveNotes();
//        fakeNotes();
        setSupportActionBar((Toolbar)findViewById(R.id.notes_toolbar));
        setTitle("NOTES");


    }

    //Retreive note by fetching when the app opens
    private void    retreiveNotes()
    {
        mNoteRepository.retrieveNotesTask().observe(this, new Observer<List<Note>>()
        {
            @Override
            public void onChanged(List<Note> notes)
            {
                if(mNotes.size()>0)
                {
                    mNotes.clear();
                }
                if(notes!=null)
                {
                    mNotes.addAll(notes);

                }
                mNotesRecylerAdapter.notifyDataSetChanged();
            }
        });
    }

    private void fakeNotes()
    {
        for(int i=0;i<1000;i++)
        {
            Note note=new Note();
            note.setTitle("Title#"+i);
            note.setContent("content #"+i);
            note.setTimestamp(" jan 2019");
            mNotes.add(note);
        }
        mNotesRecylerAdapter.notifyDataSetChanged();
    }
    private  void initRecyclerView()
    {
        LinearLayoutManager linerlayoutmanager=new LinearLayoutManager(this);
        mrecyclerview.setLayoutManager(linerlayoutmanager);
        mNotesRecylerAdapter=new NotesRecyclerAdapter(mNotes,this);
        //attaching the gesture to recyclerview for delete operation
        itemDecorator=new VerticalSpacingItemDecorator(14);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mrecyclerview);
        mrecyclerview.addItemDecoration(itemDecorator);
        mrecyclerview.setAdapter(mNotesRecylerAdapter);


    }

    @Override
    public void onNoteClick(int position)
    {
        //Log.d(TAG, "onNoteClick: Clicked" + position);
        Intent intent=new Intent(this,NoteActivity.class);
        intent.putExtra("Selected_note",mNotes.get(position));
        startActivity(intent);
    }

    @Override
    public void onClick(View view)
    {
        Intent intent=new Intent(this,NoteActivity.class);
        startActivity(intent);
    }

    //simulated method for Delete note
    private void delNotes(Note notes)
    {
        mNotes.remove(notes);
        mNotesRecylerAdapter.notifyDataSetChanged();
        //deleting
        mNoteRepository.delNotes(notes);


    }

    //method for swiping right gesture on recycler view
    private ItemTouchHelper.SimpleCallback itemTouchHelperCallback=new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT)
    {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction)
        {
            delNotes(mNotes.get(viewHolder.getAdapterPosition()));

        }
    };
}

