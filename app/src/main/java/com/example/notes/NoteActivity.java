package com.example.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.notes.models.*;
import com.example.notes.persistence.NoteRepository;
import com.example.notes.util.UtilityTimestamp;

public class NoteActivity extends AppCompatActivity implements
        View.OnTouchListener,
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener,View.OnClickListener,
        TextWatcher
{
    private static final String TAG = "NoteActivity";

    //To Track the current state of the mode
    private static final int EDIT_MODE_ENABLED=1;
    private static final int EDIT_MODE_DISABLE=0;




    //UI components
    private  LineEditText mLineEditText;
    private EditText  mEditTitle;
    private TextView mViewTitle;
    private RelativeLayout mCheckMarkContainer;
    private RelativeLayout mArrowContainer;
    private ImageButton mCheckMarkImage;
    private ImageButton mArrowImage;




    //Variables
    private boolean mIsNewNote;
    private Note  mInitialNote;
    //object to detect double tap gestures
    private GestureDetector  mGestureDetector;
    private int mMode;
    private NoteRepository mNoteRepository;
    private Note mFinalNote;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        mLineEditText=findViewById(R.id.note_text);
        mEditTitle=findViewById(R.id.note_edit_title);
        mViewTitle=findViewById(R.id.note_text_title);
        mCheckMarkContainer=findViewById(R.id.Check_arrow_container);
        mArrowContainer=findViewById(R.id.black_arrow_container);
        mCheckMarkImage=findViewById(R.id.toolbar_check_arrow);
        mArrowImage=findViewById(R.id.toolbar_back_arrow);

        mNoteRepository=new NoteRepository(this);

        setListeners();

        if(getIncomingIntent())
        {

            setNewNoteProperties();
            //this is Edit mode
            // for new note the UI should change for writing and having check mark enabled.
            enabledEditMode();
        }
        else
        {
            //this is A View mode property
            setNoteProperties();
            disableContentInteraction();
        }

    }

    //Setting Listeners for double tap on EDITLINES
    private  void setListeners()
    {
        //setting listner on EditText for edit mode when double clicked for this using on
        //for passing it to doubleTap event using GestureDetector
        mGestureDetector=new GestureDetector(this,this );
        mLineEditText.setOnTouchListener(this);

        mViewTitle.setOnClickListener(this);//for edit mode enabling
        mCheckMarkImage.setOnClickListener(this); // for disabling edit mode

        //Setting it on Toolbar backarrow
        mArrowImage.setOnClickListener(this);

        // It will monitor is EditText title is changed or not
        mEditTitle.addTextChangedListener(this);
    }

    //To catch incoming intent as a bundle
    private  boolean getIncomingIntent()
    {
        if(getIntent().hasExtra("Selected_note"))
        {
            Toast.makeText(this, "Double Tap to Edit", Toast.LENGTH_SHORT).show();
            //we did not instantiate here coz were getting it as a bunble from previous activity
            mInitialNote=getIntent().getParcelableExtra("Selected_note");

            mFinalNote=new Note();
            mFinalNote.setTitle(mInitialNote.getTitle());
            mFinalNote.setContent(mInitialNote.getContent());
            mFinalNote.setTimestamp(mInitialNote.getTimestamp());
            mFinalNote.setId(mInitialNote.getId());


            Log.d(TAG, "getIncomingNote "+mInitialNote.toString());
            mMode=EDIT_MODE_DISABLE;
            mIsNewNote=false;
            return false;
        }
        mMode=EDIT_MODE_ENABLED;
        mIsNewNote=true;
        return  true;
    }

    private void saveChange()
    {
        if(mIsNewNote)
        {
                saveNewNotes();
        }
        else
        {
            updateNote();

        }
    }
    private void updateNote()
    {
        mNoteRepository.updateNote(mFinalNote);
    }
    private void saveNewNotes( )
    {
        mNoteRepository.insertNoteTask(mFinalNote);

    }



    //Enabling EDIT MODE UI
    private void enabledEditMode()
    {
        mArrowContainer.setVisibility(View.GONE);
        mCheckMarkContainer.setVisibility(View.VISIBLE);
        mViewTitle.setVisibility(View.GONE);
        mEditTitle.setVisibility(View.VISIBLE);

        mMode=EDIT_MODE_ENABLED;
       enableContentInteraction();

    }

    //Disabling EDIT mode UI (View mode)
    private void disableEditMode()
    {
        mArrowContainer.setVisibility(View.VISIBLE);
        mCheckMarkContainer.setVisibility(View.GONE);
        mViewTitle.setVisibility(View.VISIBLE);
        mEditTitle.setVisibility(View.GONE);
        mMode=EDIT_MODE_DISABLE;
        disableContentInteraction();


        //Code to set property if there is a + icon selected/existing recyclerview and new note is
        // to be saved
        // only when there is a content inside EditText (mLinedEditText)
        String temp=mLineEditText.getText().toString();
        temp.replace("/n","");
        temp.replace(" ","");
        if(temp.length()>0)
        {
            mFinalNote.setTitle(mEditTitle.getText().toString());
            mFinalNote.setContent(mLineEditText.getText().toString());
            String timestamp= UtilityTimestamp.getCurrentTimeStamp(); //getting date ex: 10 2019
            mFinalNote.setTimestamp(timestamp);

        }
        //Now we will check it the final note is different from inital we are going to save it
        //accordingly
        if(!mFinalNote.getContent().equals(mInitialNote.getContent())
                ||!mFinalNote.getTitle().equals(mInitialNote.getTitle()))
        {
            saveChange();

        }

    }


    //Setting properties if there is an existing note
    private  void setNoteProperties()
    {
        mEditTitle.setText(mInitialNote.getTitle());
        mViewTitle.setText(mInitialNote.getTitle());
        mLineEditText.setText(mInitialNote.getContent());
    }

    //Setting properties if there is a new note to create
    private void setNewNoteProperties()
    {
        mViewTitle.setText("Note Title");
        mEditTitle.setText("Note Title");

        //Creating new mIntialNote object coz when the plus icon is set we need to
        //to get all the getters and setters of initial note as well as final note
        //and then set default values to it
        mInitialNote=new Note();
        mFinalNote=new Note();
        mInitialNote.setTitle("Note Title");
        mFinalNote.setTitle("Note Title");


    }


    private void disableContentInteraction()
    {
        mLineEditText.setKeyListener(null);
        mLineEditText.setFocusable(false);
        mLineEditText.setFocusableInTouchMode(false);
        mLineEditText.setCursorVisible(false);
        mLineEditText.clearFocus();
    }
    private void enableContentInteraction()
    {
        mLineEditText.setKeyListener(new EditText(this).getKeyListener());
        mLineEditText.setFocusable(true);
        mLineEditText.setFocusableInTouchMode(true);
        mLineEditText.setCursorVisible(true);
        mLineEditText.requestFocus();

    }

    private void hideSoftKeyBoard()
    {
        InputMethodManager imm=(InputMethodManager)this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view=this.getCurrentFocus();
        if(view==null)
        {
            view=new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);

    }

    //will intercept touch event and pass it through doubleTap using GestureDector object
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent)
    {
        return  mGestureDetector.onTouchEvent(motionEvent);
    }

    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) { }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) { }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent motionEvent)
    {    Log.d(TAG, "onDoubleTap: On Double is tapped");
        enabledEditMode();
            return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent motionEvent)
    {
        return false;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId())
        {
            case R.id.toolbar_check_arrow: {
                hideSoftKeyBoard();
                disableEditMode();
                break;
            }
            case R.id.note_text_title: {
                enabledEditMode();
                mEditTitle.requestFocus();
                mEditTitle.setSelection(mEditTitle.length());
                break;
            }
            //to close the NoteActivity when Back Arrow toolbar is clicked
            case R.id.toolbar_back_arrow: {
                finish();
                break;
            }
        }

    }

    @Override
    public void onBackPressed()
    {
        if(mMode==EDIT_MODE_ENABLED)
        {
            onClick(mCheckMarkImage);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("Mode",mMode);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mMode=savedInstanceState.getInt("Mode");
        if(mMode==EDIT_MODE_ENABLED)
        {enabledEditMode();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
    {
        mViewTitle.setText(charSequence.toString());

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
