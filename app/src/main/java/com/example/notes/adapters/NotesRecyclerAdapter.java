package com.example.notes.adapters;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notes.models.Note;
import com.example.notes.R;
import com.example.notes.util.UtilityTimestamp;

import java.util.ArrayList;

public class NotesRecyclerAdapter extends RecyclerView.Adapter<NotesRecyclerAdapter.ViewHolder>
{
    private static final String TAG = "NotesRecyclerAdapter";
     private ArrayList<Note> mNotes=new ArrayList<>();
     private  OnNoteListener mOnNoteListener;

     public NotesRecyclerAdapter(ArrayList<Note> Notes,OnNoteListener onNoteListener)
     {
         this.mNotes = Notes;
          this.mOnNoteListener=onNoteListener;
     }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view=
        LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_notes_list_item,parent,false);
        return new ViewHolder(view,mOnNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        try
        {
            String month=mNotes.get(position).getTimestamp().substring(0,2);
            month= UtilityTimestamp.getMonthFromNumber(month);
            String year=mNotes.get(position).getTimestamp().substring(3);
            String timestamp=month+" "+year;

            holder.timestamp.setText(timestamp);
            holder.title.setText(mNotes.get(position).getTitle());
        }catch (Exception e)
        {
            Log.d(TAG, "onBindViewHolder: NullPointerException"+e.getMessage());
        }

        

    }

    @Override
    public int getItemCount()
    {
        return mNotes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        TextView title, timestamp;
        OnNoteListener onNoteListener;

        public ViewHolder(@NonNull View itemView,OnNoteListener onNoteListener)
        {
            super(itemView);
            title =itemView.findViewById(R.id.note_title);
            timestamp=itemView.findViewById(R.id.note_timestamp);
            this.onNoteListener=onNoteListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view)
        {
            onNoteListener.onNoteClick(getAdapterPosition());
        }
    }
    public interface OnNoteListener
    {
        void onNoteClick(int position);
    }
}
