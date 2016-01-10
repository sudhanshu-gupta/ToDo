package com.amigo.todo;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.amigo.todo.contentprovider.ToDoContentProvider;
import com.amigo.todo.databases.ToDoTable;

public class ToDoActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = ToDoActivity.class.getSimpleName();

    private static final int ACTIVITY_CREATE = 0;
    private static final int ACTIVITY_EDIT = 1;
    private static final int DELETE_ID = Menu.FIRST + 2;
    private static final int MARK_AS_DONE_ID = Menu.FIRST + 1;
    // private Cursor cursor;
    private SimpleCursorAdapter adapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.header);
        getSupportActionBar().setHomeButtonEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                Intent i = new Intent(getApplicationContext(), ToDoEditActivity.class);
                startActivity(i);
            }
        });

        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.w(TAG, "CLICKED_CALLED");
                Intent i = new Intent(getApplicationContext(), ToDoEditActivity.class);
                Uri todoUri = Uri.parse(ToDoContentProvider.CONTENT_URI + "/" + id);
                i.putExtra(ToDoContentProvider.CONTENT_ITEM_TYPE, todoUri);

                startActivity(i);
            }
        });

        listView.setDividerHeight(2);
        fillData();
        registerForContextMenu(listView);


    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case DELETE_ID:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                        .getMenuInfo();
                Uri uri = Uri.parse(ToDoContentProvider.CONTENT_URI + "/"
                        + info.id);
                getContentResolver().delete(uri, null, null);
                fillData();
                return true;
            case MARK_AS_DONE_ID:
                AdapterView.AdapterContextMenuInfo infoC = (AdapterView.AdapterContextMenuInfo) item
                        .getMenuInfo();
                Uri uriC = Uri.parse(ToDoContentProvider.CONTENT_URI + "/"
                        + infoC.id);
                ContentValues values = new ContentValues();
                values.put(ToDoTable.COLUMN_IS_DONE, 1);
                Log.i(TAG, "ID => "+String.valueOf(infoC.id));
                getContentResolver().update(uriC, values, "_id=?", new String[]{String.valueOf(infoC.id)});
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void fillData() {

        // Fields from the database (projection)
        // Must include the _id column for the adapter to work
        String[] from = new String[] { ToDoTable.COLUMN_TASK };
        // Fields on the UI to which we map
        int[] to = new int[] { R.id.toDoTextView };

        getLoaderManager().initLoader(0, null, this);
        adapter = new SimpleCursorAdapter(this, R.layout.todo_row, null, from,
                to, 0);
        listView.setAdapter(adapter);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, MARK_AS_DONE_ID, 0, "Mark as Done");
        menu.add(0, DELETE_ID, 1, R.string.menu_delete);
    }

    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = { ToDoTable.COLUMN_ID, ToDoTable.COLUMN_TASK };
        CursorLoader cursorLoader = new CursorLoader(this,
                ToDoContentProvider.CONTENT_URI, projection, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
