
# Android Sqlite ���ݿ��ݲ���

ʹ�ã�

1.  ��Ҫ�����ı��Ӧ��Class

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

2. ��ʼ�����ݿ�

```
        DBHelper.initDBHelper("test.db",TestTable.class);
```

3. ������

- �����ڳ�ʼ�����ݿ�ʱ������
- ����ʱ�䴴��(����ʱ�䴴����ʱ�����ݿ�汾���Զ�+1)��
```
    DBHelper.addTable(TestTable.class);
```

4. ���±�

    �ĵ�������....

5. ��������

```
                JSONArray flows = jsonObject.getJSONArray("flows");
                                DataBaseUtil.clearTable(context, FlowLiuChengTable.class);
                DataBaseUtil.saveJsonArrayData(context, FlowLiuChengTable.class, flows);

```

6. ��ȡ����

```
DataBaseUtil.GetData(context, FlowLiuChengTable.class);

yaopinkuJson = DataBaseUtil.GetData(ChuFangActivity.this, "select CANGKU,CANGKUID from Medicine group by CANGKU,CANGKUID");

Object object = DataBaseUtil.GetOneData(ChuFangActivity.this, Medicine.class, " WHERE fmatid LIKE '" + MedicineId + "' and CANGKUID = '" + yaoPinKuTextView.getTag().toString() + "'"); //��ȡ����ũ��

```

���ã�

```
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}

```

```
    compile 'com.github.IGisvity:SqliteOrm:v1.0.4'
```