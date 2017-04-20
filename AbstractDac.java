/**
 * AbstractDac.java ver. 0.7
 */

package hashimotonet.sql;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

/**
 * @author 橋本 修一
 *
 */
public abstract class AbstractDac {

    /**
     * JDBC接続マネージャ
     */
    Connection conn;

    /**
     * JDBCドライバクラス名
     */
    private String className = "com.mysql.cj.jdbc.Driver";

    /**
     * JDBCのURL
     */
    private String url = "jdbc:mysql://localhost/test?useUnicode=true&charcterEncoding=UTF-8&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";

    /**
     * JDBCユーザ名
     */
    private String userName = "root";

    /**
     * JDBCパスワード
     */
    private String password = "root";

    /**
     * コンストラクタ。
     */
    public AbstractDac()
            throws ClassNotFoundException,
            SQLException,
            IllegalAccessException,
            InstantiationException {
        Class.forName(className).newInstance();
        conn = DriverManager.getConnection(url, userName, password);
    }

    /**
     *
     * @param bean
     * @param rs
     * @return
     * @throws SQLException
     */
    public Object setData(Object bean, ResultSet rs) throws SQLException {
        ResultSetMetaData rsMeta = rs.getMetaData();
        int count = rsMeta.getColumnCount();
        String columnNames[] = new String[count];
        int columnTypes[] = new int[count];

        System.out.println("count = " + count);

        // カラム名取得。
        for (int i = 1; i <= columnNames.length; i++) {
            System.out.println("i = " + i);
            columnNames[i - 1] = rsMeta.getColumnName(i);
            System.out.println(columnNames[i - 1]);
            columnTypes[i - 1] = rsMeta.getColumnType(i);
            System.out.println(columnTypes[i - 1]);
        }

        // Beanのセッターメソッド取得。
        Class beanClass = bean.getClass();
        Method methods[] = beanClass.getDeclaredMethods();
        //        String setterNames[] = getSetterNames(bean);

        //        while (rs.next()) {
        int i = 0;
        for (String columnName : columnNames) {
            for (Method method : methods) {
                String methodName = method.getName();
                if (columnName.equalsIgnoreCase(methodName.substring(3))
                        && methodName.startsWith("set")) {
                    // beanにプロパティセット
                    if (Types.CHAR == columnTypes[i]) {
                        // String型をセット。

                    } else if (Types.INTEGER == columnTypes[i]) {
                        // Int型をセット。

                    }
                }
            }
            i++;
        }
        //        }

        return bean;
    }

    /**
     * Int型をBeanにセットする。
     *
     * @param param
     * @param bean
     * @param setterMethodName
     * @return
     */
    public Object invoke(int param, Object bean, Method method)
            throws InvocationTargetException, IllegalAccessException {
        method.invoke(bean, param);
        return bean;
    }

    /**
     * String型をBeanにセットする。
     *
     * @param param
     * @param bean
     * @param method
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public Object invoke(String param, Object bean, Method method)
            throws InvocationTargetException, IllegalAccessException {
        method.invoke(bean, param);
        return bean;
    }

    /**
     * デストラクタ。
     */
    public void finalize() throws SQLException {
        conn.close();
    }

}
