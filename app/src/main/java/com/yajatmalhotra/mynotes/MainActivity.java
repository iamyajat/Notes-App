package com.yajatmalhotra.mynotes;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    String[] listItem;
    String[] listItem2;
    FloatingActionButton fab;
    DatabaseHelper notesDb;
    TextInputEditText title;
    TextInputEditText summary;
    Button confirm, delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notesDb = new DatabaseHelper(this);

        listView = findViewById(R.id.listView);
        fab = findViewById(R.id.fab);

        listItem = this.viewAll()[2];
        listItem2 = this.viewAll()[0];
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.notes_card, R.id.note, listItem);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((adapterView, view, position, l) -> {
//            String value = adapter2.getItem(position);
//            Toast.makeText(getApplicationContext(), value, Toast.LENGTH_SHORT).show();
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            ViewGroup viewGroup = findViewById(android.R.id.content);
            View dialogView = LayoutInflater.from(view.getContext()).inflate(R.layout.add_note, viewGroup, false);
            TextView nt = dialogView.findViewById(R.id.note_title);
            title = dialogView.findViewById(R.id.titleTextField);
            summary = dialogView.findViewById(R.id.summaryTextField);
            confirm = dialogView.findViewById(R.id.addNote);
            delete = dialogView.findViewById(R.id.delNote);
            builder.setView(dialogView);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            nt.setText("Edit Note");
            summary.setText(adapter.getItem(position));

            confirm.setOnClickListener(v -> {
                        boolean isUpdate = notesDb.updateData(listItem2[position],
                                title.getText().toString(),
                                summary.getText().toString());
                        if (isUpdate == true) {
                            Toast.makeText(MainActivity.this, "Note Updated", Toast.LENGTH_LONG).show();
                            Intent intent = getIntent();
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(intent);
                            alertDialog.dismiss();
                        } else
                            Toast.makeText(MainActivity.this, "Note not Updated", Toast.LENGTH_LONG).show();
                    }
            );

            delete.setOnClickListener(v -> {
                Integer deletedRows = notesDb.deleteData(listItem2[position]);
                if(deletedRows > 0) {
                    Toast.makeText(MainActivity.this, "Note Deleted", Toast.LENGTH_LONG).show();
                    Intent intent = getIntent();
                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(intent);
                    alertDialog.dismiss();
                }
                else
                    Toast.makeText(MainActivity.this,"Note not Deleted",Toast.LENGTH_LONG).show();
            });

        });

        fab.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            ViewGroup viewGroup = findViewById(android.R.id.content);
            View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.add_note, viewGroup, false);
            title = dialogView.findViewById(R.id.titleTextField);
            summary = dialogView.findViewById(R.id.summaryTextField);
            confirm = dialogView.findViewById(R.id.addNote);
            Button cancel = dialogView.findViewById(R.id.delNote);
            builder.setView(dialogView);
            AlertDialog alertDialog = builder.create();
            alertDialog.setCancelable(false);
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();

            confirm.setOnClickListener(v1 -> {
                boolean suc = notesDb.insertData(title.getText().toString(), summary.getText().toString());
                if (suc == true) {
                    Toast.makeText(MainActivity.this, "Note Added", Toast.LENGTH_LONG).show();
                    Intent intent = getIntent();
                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(intent);
                    alertDialog.dismiss();
                } else
                    Toast.makeText(MainActivity.this, "Note not Added", Toast.LENGTH_LONG).show();
            });
            cancel.setText("Cancel");
            cancel.setOnClickListener(v2 -> {
                alertDialog.dismiss();
            });
        });
    }

    public String[][] viewAll() {

        Cursor res = notesDb.getAllData();
        if (res.getCount() == 0) {
            // show message
            return new String[3][0];
        }
        String arr[][] = new String[3][res.getCount()];
        int i = 0;
        while (res.moveToNext()) {
            arr[0][i] = res.getString(0);
            arr[1][i] = res.getString(1);
            arr[2][i] = res.getString(2);
            i++;
        }
        return arr;

    }
}