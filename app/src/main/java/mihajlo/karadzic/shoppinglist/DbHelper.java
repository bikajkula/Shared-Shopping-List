package mihajlo.karadzic.shoppinglist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.UUID;

public class DbHelper extends SQLiteOpenHelper {

    public static final String users_table_name = "USERS";
    public static final String users_column_username = "username";
    public static final String users_column_email = "email";
    public static final String users_column_password = "password";

    public static final String lists_table_name = "LISTS";
    public static final String lists_column_name = "name";
    public static final String lists_column_creator_name = "creatorName";
    public static final String lists_column_shared = "shared";

    public static final String items_table_name = "ITEMS";
    public static final String items_column_name = "name";
    public static final String items_column_list_name = "listName";
    public static final String items_column_checked = "checked";
    public static final String items_column_id = "id";

    public DbHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE " + users_table_name +
                " (" + users_column_username + " TEXT, " +
                users_column_email + " TEXT, " +
                users_column_password + " TEXT);");

        sqLiteDatabase.execSQL("CREATE TABLE " + lists_table_name +
                " (" + lists_column_name + " TEXT, " +
                lists_column_creator_name + " TEXT, " +
                lists_column_shared + " TEXT);");
        sqLiteDatabase.execSQL("CREATE TABLE " + items_table_name +
                " (" + items_column_name + " TEXT, " +
                items_column_list_name + " TEXT, " +
                items_column_checked + " TEXT," +
                items_column_id + " TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


    public void insertUser(String username, String email, String password){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(users_column_username, username);
        values.put(users_column_email, email);
        values.put(users_column_password, password);

        db.insert(users_table_name, null, values);
        close();
    }

    public void insertList(ListRowModel rm, String user){
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(lists_column_name, rm.getmTitle());
        values.put(lists_column_creator_name,user);
        values.put(lists_column_shared,rm.getmShared());

        db.insert(lists_table_name,null, values);
        close();
    }

    public void insertItem (ListTaskModel tm,String uID, String list) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(items_column_name, tm.getmTitle());
        values.put(items_column_list_name, list);
        values.put(items_column_checked, Boolean.toString(tm.ismChecked()));
        values.put(items_column_id, uID);

        db.insert(items_table_name, null, values);
        close();
    }

    public boolean checkUser(String username, String password){
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(users_table_name, null, users_column_username + "=? AND " + users_column_password + "=?", new String[] { username, password }, null, null, null);
        if(cursor.getCount() <= 0) {
            return false;
        }

        close();
        return true;
    }

    public boolean doesUserExist(String username){
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(users_table_name, null, users_column_username + "=?", new String[] { username }, null, null, null);
        if(cursor.getCount() <= 0) {
            return false;
        }

        close();
        return true;
    }

    public boolean doesListExist(String listname){
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(lists_table_name, null, lists_column_name + "=?", new String[] { listname }, null, null, null);
        if(cursor.getCount() <= 0) {
            return false;
        }

        close();
        return true;
    }

    public boolean doesTaskExist(String taskname, String uID){
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(items_table_name, null, items_column_name + "=?", new String[] { taskname }, null, null, null);
        if(cursor.getCount() <= 0) {
            return false;
        }

        close();
        return true;
    }

    public ListRowModel[] readLists(String user) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(lists_table_name, null, lists_column_creator_name + "=? OR " + lists_column_shared + "=?", new String[] {user, "Yes"}, null, null, null);
        if(cursor.getCount() <= 0) {
            return null;
        }
        ListRowModel[] lists = new ListRowModel[cursor.getCount()];
        int i = 0;
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
            lists[i++] = createList(cursor);
        }

        close();
        return lists;

    }

    public ListRowModel[] readMyLists(String user) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(lists_table_name, null, lists_column_creator_name + "=?", new String[] {user}, null, null, null);
        if(cursor.getCount() <= 0) {
            return null;
        }
        ListRowModel[] lists = new ListRowModel[cursor.getCount()];
        int i = 0;
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
            lists[i++] = createList(cursor);
        }

        close();
        return lists;
    }

    public ListTaskModel[] readItems (String list) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(items_table_name, null, items_column_list_name + "=?", new String[] {list}, null, null, null);
        if(cursor.getCount() <= 0) {
            return null;
        }
        ListTaskModel[] lists = new ListTaskModel[cursor.getCount()];
        int i = 0;
        for(cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()){
            lists[i++] = createItem(cursor);
        }

        close();
        return lists;
    }

    private ListRowModel createList(Cursor cursor) {
        String mTitle = cursor.getString(cursor.getColumnIndexOrThrow(lists_column_name));
        String mShared = cursor.getString(cursor.getColumnIndexOrThrow(lists_column_shared));

        return new ListRowModel(mTitle, mShared);
    }

    private ListTaskModel createItem (Cursor cursor) {
        String mTitle = cursor.getString(cursor.getColumnIndexOrThrow(items_column_name));
        String mChecked = cursor.getString(cursor.getColumnIndexOrThrow(items_column_checked));
        String uID = cursor.getString(cursor.getColumnIndexOrThrow(items_column_id));
        return new ListTaskModel(mTitle, Boolean.valueOf(mChecked), uID);
    }

    public void deleteList (String name) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(lists_table_name, lists_column_name + "=?", new String[] { name });
        db.delete(items_table_name, items_column_name + "=?", new String[] { name });
        close();
    }

    public void deleteItem (String uID) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(items_table_name, items_column_id + " =?", new String[] { uID });
        close();
    }


    public void updateItem(String uID, String mChecked){
        SQLiteDatabase db = getWritableDatabase();

        Cursor cursor = db.query(items_table_name, null, items_column_id + "=?", new String[] {uID}, null, null, null);
        cursor.moveToFirst();
        String title = cursor.getString(cursor.getColumnIndexOrThrow(items_column_name));
        String listName =  cursor.getString(cursor.getColumnIndexOrThrow(items_column_list_name));

        ContentValues cv = new ContentValues();
        cv.put(items_column_name, title);
        cv.put(items_column_list_name, listName);
        cv.put(items_column_checked, mChecked);
        cv.put(items_column_id, uID);

        db.update(items_table_name, cv, items_column_id + "=?", new String[] {uID});
        close();
    }
}
