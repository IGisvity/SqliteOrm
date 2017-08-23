package cn.com.igisvity.sqliteormlib;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {
    // 默认本数据库名称

    /**
     * 数据库版本升级
     */
    public static int DATABASE_VERSION = 1;

    public static ArrayList<Class> ClassList = new ArrayList<>();

    private String Tag = this.getClass().getSimpleName();
    // 表集合
    public static String DATABASE_NAME = "database.db";

    public static void initDBHelper(String dbName,Class... cls) {
        DATABASE_NAME = dbName;
        if(cls!=null&&cls.length>0){
            for (int i = 0; i < cls.length; i++) {
                ClassList.add(cls[i]);
            }
        }
    }

    public static void addTable(Class c){
        ClassList.add(c);
        DATABASE_VERSION++;
    }

    /**
     * @param context
     */
    public DBHelper(Context context) {
        //
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * @param context
     * @param dataName
     */
    public DBHelper(Context context, String dataName) {
        //
        super(context, dataName, null, DATABASE_VERSION);
    }

    /**
     * @param context
     * @param dataName
     * @param version
     */
    public DBHelper(Context context, String dataName, int version) {
        //
        super(context, dataName, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            Log.i(Tag, "Create dataBase**********************");
            if (ClassList == null || ClassList.size() == 0) {
                Log.e(Tag, "当前数据库无初始化");
                return;
            }
            for (int i = 0; i < ClassList.size(); i++) {
                String tableName = ClassList.get(i).getSimpleName();
                Field[] declaredFields = ClassList.get(i).getDeclaredFields(); // 加局部变量
                // 如果类里面的变量数目大于0,新建基础表
                if (declaredFields == null || declaredFields.length == 0) {
                    Log.e(Tag, "类：" + tableName + " 未检测到字段");
                    return;
                }
                db.execSQL("DROP TABLE IF EXISTS " + tableName);//如果存在，则删除
                db.execSQL("CREATE TABLE IF NOT EXISTS " + tableName + " (_id INTEGER PRIMARY KEY AUTOINCREMENT )");
                for (int m = 0; m < declaredFields.length; m++) { //针对类里每个属性增加表的各个字段
                    String fieldname = declaredFields[m].getName();
                    String fieldtype = " VARCHAR"; //默认字段类型为 VARCHAR
                    if (declaredFields[m].getType() == Integer.class || declaredFields[m].getType() == Double.class || declaredFields[m].getType() == Float.class) {
                        fieldtype = " INTEGER";
                    }
                    db.execSQL("ALTER TABLE " + tableName + " ADD '" + fieldname + "' " + fieldtype);
                    Log.i(Tag, "table:" + tableName + " +field:" + fieldname + " type : " + fieldtype);
                }
            }

        } catch (Exception e) {
            // TODO: handle exception
            Log.i("Set_ZeroError", e.getClass().toString());
        }
    }

    //
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("Sqlite", "ALTER dataBase *******************************");
        if (oldVersion < DATABASE_VERSION) {
            try {
                onCreate(db);
                //  UpdateDB(db);
            } catch (Exception e) {
                // TODO: handle exception
                Log.e("Sql_error", e.getMessage().toString());
            }
        }
    }

    /**
     * 重置数据
     */
    public void SetZero(String TableName) {
    }

    public static void DeleteDataBase(Context context) {
        context.deleteDatabase(DATABASE_NAME);
    }

    /**
     * 判断表是否存在，若不存在，则创建
     *
     * @param context
     * @param cls
     */
    public static void CreateTableWithJudge(Context context, Class<?> cls) {
        if (!isHave(context, cls)) {
            CreateTable(context, cls);
        }
    }

    public static boolean isHave(Context context, Class<?> userClass) {
        boolean result = false;
        try {
            DBHelper db = new DBHelper(context);
            SQLiteDatabase database = db.getWritableDatabase();
            String tableName = userClass.getSimpleName();
            String sql = "select * from sqlite_sequence where name like  '" + tableName + "'";
            Cursor cursor = database.rawQuery(sql, null);
            if (cursor.getCount() > 0) {
                result = true;
            } else {
                result = false;
            }
            cursor.close();
            database.close();
        } catch (Exception ex) {
            ex.fillInStackTrace();
        }
        return result;
    }

    /**
     * 快捷建表 表名 Equip
     * 例：DBHelper.CreateTable(CariAndroidOrmPronActivity.this,Equip.class);
     *
     * @param userClass
     */
    public static void CreateTable(Context context, Class<?> userClass) {
        try {
            DBHelper db = new DBHelper(context);
            SQLiteDatabase database = db.getWritableDatabase();
            String tableName = userClass.getSimpleName();
            Field[] methods = userClass.getDeclaredFields(); // 加载变量属性
            // 遍历方法集合
            Log.i("OrmHelper", "=== start traversing getXX methods====" + tableName);
            // 如果类里面的变量数目大于0,新建基础表
            if (methods.length > 0) {
                database.execSQL("CREATE TABLE IF NOT EXISTS " + tableName + " (_id INTEGER PRIMARY KEY AUTOINCREMENT )");
                Log.i("OrmHelper", "CREATE TABLE IF NOT EXISTS " + tableName + " (_id INTEGER PRIMARY KEY AUTOINCREMENT )");
            }
            for (int i = 0; i < methods.length; i++) {
                Class<?> cl = methods[i].getType();
                String TypeName = cl.getSimpleName();
                if (TypeName.equals("int")) {
                    Log.i("OrmHelper", "ALTER TABLE " + tableName + " ADD '" + methods[i].getName() + "' int"); //把所有字段名改成小写
                    database.execSQL("ALTER TABLE " + tableName + " ADD '" + methods[i].getName() + "' int");//把所有字段名改成小写
                } else {
                    Log.i("OrmHelper", "ALTER TABLE " + tableName + " ADD '" + methods[i].getName() + "' VARCHAR");//把所有字段名改成小写
                    database.execSQL("ALTER TABLE " + tableName + " ADD '" + methods[i].getName() + "' VARCHAR");//把所有字段名改成小写
                }
            }
            database.close();
            db.close();
            Log.i("OrmHelper", "=== end ====");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 升级数据库
     */
    public void UpdateDB(SQLiteDatabase db) {
        if (ClassList.size() > 0) {
            for (int i = 0; i < ClassList.size(); i++) {
                String tableName = ClassList.get(i).getSimpleName();
                Field[] methods = ClassList.get(i).getDeclaredFields(); // 加载变量属性,加载所有字段
                CompareTableAndCreate(db, tableName, methods);
            }
        }
    }

    /**
     * 对比需要生成的数据库表与设备中当前存在的数据库表的差别，并对有差别的表进行删除和重建
     */
    private static void CompareTableAndCreate(SQLiteDatabase db, String table, Field[] methods) {
        // if(clear)
        // db.execSQL("drop table if exists "+table);
        String sql = "_id INTEGER PRIMARY KEY AUTOINCREMENT";
        for (int i = 0; i < methods.length; i++) {
            Class<?> cl = methods[i].getType();
            String TypeName = cl.getSimpleName();
            if (TypeName.equals("int"))
                sql = sql + "," + methods[i].getName() + " int";
            else
                sql = sql + "," + methods[i].getName() + " VARCHAR";
        }

        // 判断新数据库的字段是否与旧数据库一致，不一致则删除重建
        boolean a = true;
        for (int j = 0; j < methods.length; j++) {
            a = checkColumnExist(db, table, methods[j].getName());
            if (a == false)
                break;
        }
        if (a == false) {
            Log.i("CompareTableAndCreate", table);
            db.execSQL("drop table if exists " + table);
            db.execSQL("create table if not exists " + table + "(" + sql + ")");
        } else
            db.execSQL("create table if not exists " + table + "(" + sql + ")");

    }

    /**
     * 判断需要生成的表中的字段与数据库中已有的表的字段是否一致
     */
    static public boolean checkColumnExist(SQLiteDatabase db, String tableName, String columnName) {
        boolean result = true;
        Cursor cursor = null;
        int index = -1;
        try {
            // 查询一行
            cursor = db.rawQuery("SELECT * FROM " + tableName + " LIMIT 0", null);
            if (cursor != null) {
                index = cursor.getColumnIndex(columnName);
                if (index == -1)
                    result = false;
            } else
                result = false;
        } catch (Exception e) {
            Log.i("checkColumnExistError", e.getMessage());
            result = false;
        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return result;
    }

}
