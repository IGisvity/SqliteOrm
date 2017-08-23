# Android Sqlite 数据库便捷操作

使用：

1.  需要创建的表对应的Class

```
public class TestTable {
    private int sex;
    private double show;
    private String name;

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public double getShow() {
        return show;
    }

    public void setShow(double show) {
        this.show = show;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```

2. 初始化数据库

```
        DBHelper.initDBHelper("test.db",TestTable.class);
```

3. 创建表

- 可以在初始化数据库时创建。
- 其它时间创建(其他时间创建表时，数据库版本号自动+1)。
```
    DBHelper.addTable(TestTable.class);
```

4. 更新表

    文档待完善....

5. 插入数据

```
                JSONArray flows = jsonObject.getJSONArray("flows");
                                DataBaseUtil.clearTable(context, FlowLiuChengTable.class);
                DataBaseUtil.saveJsonArrayData(context, FlowLiuChengTable.class, flows);

```

6. 获取数据

```
DataBaseUtil.GetData(context, FlowLiuChengTable.class);

yaopinkuJson = DataBaseUtil.GetData(ChuFangActivity.this, "select CANGKU,CANGKUID from Medicine group by CANGKU,CANGKUID");

Object object = DataBaseUtil.GetOneData(ChuFangActivity.this, Medicine.class, " WHERE fmatid LIKE '" + MedicineId + "' and CANGKUID = '" + yaoPinKuTextView.getTag().toString() + "'"); //获取所有农户

```
