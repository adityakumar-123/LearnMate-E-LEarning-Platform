package com.learnify.adapter;

import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.learnify.Model.NoteItem;
import com.learnify.R;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    private final List<NoteItem> noteList;

    public NoteAdapter(List<NoteItem> noteList) {
        this.noteList = noteList;
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView timestamp, note;

        public NoteViewHolder(View itemView) {
            super(itemView);
            timestamp = itemView.findViewById(R.id.tvTimestamp);
            note = itemView.findViewById(R.id.tvNoteText);
        }
    }

    @NonNull
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        NoteItem item = noteList.get(position);
        holder.timestamp.setText(String.format("At %.0f sec", item.getTimestamp()));
        holder.note.setText(item.getText());
    }

    public int getItemCount() {
        return noteList.size();
    }
}
