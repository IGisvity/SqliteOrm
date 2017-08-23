/**
 *
 */
package cn.com.igisvity.sqliteormlib;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.channels.FileChannel;
import java.util.ArrayList;


/**
 * @author zhouzhou
 * @Title: SqliteHelper
 * @Description:
 * @Company: www.cari.com.cn
 * @date 2015-9-24 下午5:03:18
 */
public class DataBaseUtil {
    public static String toJson(Object o) {
        Gson gson = new Gson();
        return gson.toJson(o);
    }

    /**
     * 反射机制，从数据库到数据表
     *
     * @param context
     * @param cls
     * @return
     */
    @SuppressWarnings("unchecked")
    public static ArrayList<Object> GetData(Context context, Class cls) {
        return GetData(context, cls, "");
    }

    public static Object GetOneData(Context context, Class cls, String sqlStr) {
        ArrayList<Object> datas = GetData(context, cls, sqlStr);
        if (datas.size() > 0) {
            return datas.get(0);
        } else {
            return null;
        }
    }

    /**
     * 反射机制，从数据库到数据表
     *
     * @param context
     * @param cls
     * @return
     */
    @SuppressWarnings("unchecked")
    public static ArrayList<Object> GetData(Context context, Class cls, String sqlStr) {
        ArrayList<Object> resultList = new ArrayList<Object>();
        try {
            DBHelper dbhelper = new DBHelper(context);
            SQLiteDatabase db = dbhelper.getReadableDatabase(); // 上传id大于多少的....
            String selectStr = cls.getDeclaredFields()[0].getName();
            for (int i = 1; i < cls.getDeclaredFields().length; i++) {
                selectStr = selectStr + "," + cls.getDeclaredFields()[i].getName();
            }
            Cursor cursor = db.rawQuery("SELECT " + selectStr + " FROM " + cls.getSimpleName() + " " + sqlStr, null);
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                Object obj = cls.newInstance();
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    if (cursor.getString(i) != null) {
                        ReflectionHelper.doMethod("set" + toUpperCaseFirstOne(cursor.getColumnName(i)), obj, cursor.getString(i), cls.getDeclaredFields()[i].getType());
                    }
                }
                resultList.add(obj);
            }
            if (cursor != null) {
                cursor.close();
            }
            db.close();
            dbhelper.close();
        } catch (Exception e) {
            // TODO: handle exception
            Log.e("SQLite ERROR", e.getMessage().toString());
        }
        return resultList;
    }

    /**
     * 向数据库中插入数据ArrayList
     *
     * @param context
     * @param cls
     * @param
     */
    public static void saveArrayListData(Context context, Class<?> cls, Object obj) {
        String JsonStr = toJson(obj);
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(JsonStr);
            saveJsonArrayData(context, cls, jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 向数据库中插入单个数据
     *
     * @param context
     * @param cls
     * @param
     */
    public static void saveObjectData(Context context, Class<?> cls, Object obj) {
        String JsonStr = toJson(obj);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(JsonStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        saveJsonObjct(context, cls, jsonObject);
    }

    /**
     * 插入JsonArray
     *
     * @param context
     * @param cls
     * @param jsonArray
     */
    public static void saveJsonArrayData(Context context, Class<?> cls, JSONArray jsonArray) {
        DBHelper db = new DBHelper(context);
        SQLiteDatabase dataBase = db.getWritableDatabase();
        try {
            dataBase.beginTransaction(); // 手动设置开始事务
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject itemdata = (JSONObject) jsonArray.get(i);
                dataBase.insert(cls.getSimpleName(), null, InSertContentValues(cls, itemdata));
            }
            dataBase.setTransactionSuccessful(); // 设置事务处理成功，不设置会自动回滚不提交
            Log.i("SQLite", "Success insert info into SqliteDatabase " + cls.getSimpleName());

        } catch (Exception e) {
            Log.e("SQLite ERROR " + cls.getSimpleName(), e.getMessage().toString());
        } finally {
            dataBase.endTransaction(); // 处理完成
            dataBase.close();
            db.close();
        }
    }

    /**
     * 插入JSONObject
     *
     * @param context
     * @param cls
     * @param jsonObject
     */
    public static void saveJsonObjct(Context context, Class<?> cls, JSONObject jsonObject) {
        DBHelper db = new DBHelper(context);
        SQLiteDatabase dataBase = db.getWritableDatabase();
        try {
            dataBase.insert(cls.getSimpleName(), null, InSertContentValues(cls, jsonObject));
            Log.i("SQLite", "Success insert info into SqliteDatabase " + cls.getSimpleName());
        } catch (Exception e) {
            Log.e("SQLite ERROR " + cls.getSimpleName(), e.getMessage().toString());
        } finally {
            dataBase.close();
            db.close();
        }
    }

    /**
     * 向ContentValues插入JSONObject
     *
     * @param cls
     * @param jsonObject
     * @return
     * @throws JSONException
     */
    public static ContentValues InSertContentValues(Class<?> cls, JSONObject jsonObject) throws JSONException {
        ContentValues cv = new ContentValues();
        JSONObject itemdata = jsonObject;
        Field[] fields = cls.getDeclaredFields();
        for (int j = 0; j < fields.length; j++) {
            if (itemdata.has(fields[j].getName().toString())) {
                cv.put(fields[j].getName().toString(), itemdata.getString(fields[j].getName().toString()));
            }
        }
        return cv;
    }

    /**
     * 清空指定数据表
     *
     * @param context
     * @param cls
     */
    public static void clearTable(Context context, Class<?> cls) {
        DBHelper db = new DBHelper(context);
        SQLiteDatabase dataBase = db.getWritableDatabase();
        try {
            dataBase.execSQL("delete from " + cls.getSimpleName());
        } catch (Exception e) {
            Log.e("SQLite ERROR " + cls.getSimpleName(), e.getMessage().toString());
        } finally {
            dataBase.close();
            db.close();
        }
    }

    public static void getOutDataBase() {
        File f = new File("/data/data/com.lihua.oa/databases/lhoa.db"); //比如  "/data/data/com.hello/databases/test.db"
        String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File o = new File(sdcardPath + "/cp.db"); //sdcard上的目标地址
        if (f.exists()) {
            FileChannel outF;
            try {
                outF = new FileOutputStream(o).getChannel();
                new FileInputStream(f).getChannel().transferTo(0, f.length(), outF);
                Log.e("sqlite", "数据库拷贝成功");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //首字母转大写
    public static String toUpperCaseFirstOne(String s) {
        if (Character.isUpperCase(s.charAt(0)))
            return s;
        else
            return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
    }

    public static JSONArray GetData(Context context, String sql) {
        JSONArray jsonArray = new JSONArray();
        try {
            DBHelper dbhelper = new DBHelper(context);
            SQLiteDatabase db = dbhelper.getReadableDatabase(); // 上传id大于多少的....
            Cursor cursor = db.rawQuery(sql, null);
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                JSONObject jsonObject = new JSONObject();
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    jsonObject.put(cursor.getColumnName(i), cursor.getString(i));
                }
                jsonArray.put(jsonObject);
            }
            if (cursor != null) {
                cursor.close();
            }
            db.close();
            dbhelper.close();
        } catch (Exception e) {
            // TODO: handle exception
            Log.e("SQLite ERROR", e.getMessage().toString());
        }
        return jsonArray;
    }

    public static void excute(Context context, String sql) {
        DBHelper dbhelper = new DBHelper(context);
        SQLiteDatabase db = dbhelper.getReadableDatabase(); // 上传id大于多少的....
        db.execSQL(sql);
        db.close();
        dbhelper.close();
    }
}
