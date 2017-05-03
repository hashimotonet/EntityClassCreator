/**
 * AbstractDac.java ver. 1.0
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
     * 結果セットのカーソル位置の
     *
     * @param bean
     * @param rs
     * @return
     * @throws SQLException
     * @throws IllegalAccessException
     */
    public Object setData(Object bean, ResultSet rs)
            throws SQLException,
            IllegalAccessException,
            InvocationTargetException {
        // 結果セットよりメタデータ取得。
        ResultSetMetaData rsMeta = rs.getMetaData();

        // 結果セットのカラム数を取得。
        int count = rsMeta.getColumnCount();

        // カラム名のString配列
        String columnNames[] = new String[count];

        // カラムの型
        int columnTypes[] = new int[count];

        System.out.println("count = " + count);

        // カラム名取得。
        for (int i = 1; i <= columnNames.length; i++) {
            System.out.println("i = " + i);

            // カラム名を取得。
            columnNames[i - 1] = rsMeta.getColumnName(i);
            System.out.println(columnNames[i - 1]);

            // カラムの型を取得。
            columnTypes[i - 1] = rsMeta.getColumnType(i);
            System.out.println(columnTypes[i - 1]);
        }

        // Beanのセッターメソッド取得。
        Class beanClass = bean.getClass(); // クラスの取得
        Method methods[] = beanClass.getDeclaredMethods(); // クラスの宣言メソッドを取得

        // ループカウンタ
        int i = 0;

        // カラム名数分だけループする。
        for (String columnName : columnNames) {

            // メソッド名分だけループする。
            for (Method method : methods) {

                // メソッド名取得。
                String methodName = method.getName();

                // カラム名よりアンダースコアを削除した名称とメソッド名が紐づくか
                if (deleteUnderscore(columnName).equalsIgnoreCase(methodName.substring(3))
                        && methodName.startsWith("set")) { // カラム名と同一でsetterメソッドの場合
                    // beanにプロパティセット
                    if (Types.CHAR == columnTypes[i]) { // カラムがCHAR型である
                        // String型をセット。
                        invoke(rs.getString(columnName), bean, method);

                    } else if (Types.INTEGER == columnTypes[i]) { //　カラムがINT型である
                        // Int型をセット。
                        invoke(rs.getInt(columnName), bean, method);
                    }
                }
            }
            // カウンタをインクリメント。
            i++;
        }

        // プロパティセットされたBeanを返却する。
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

        // Beanのセッターメソッドにパラメータをセット。
        method.invoke(bean, param);

        // パラメータのセットされたBeanを返却する。
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

        // Beanのセッターメソッドにパラメータをセット。
        method.invoke(bean, param);

        // パラメータのセットされたBeanを返却する。
        return bean;
    }

    /**
     * カラム名よりアンダースコアを削除する。
     *
     * @param columnName
     * @return
     */
    protected String deleteUnderscore(String columnName) {
        StringBuilder column = new StringBuilder(); // 文字列バッファ

        // カラム名をアンダースコアをデリミタにして分解。
        String split[] = columnName.split("_");

        // デリミタで分解された文字列を結合。
        for (String part : split) {
            column.append(part);
        }

        // アンダースコアを削除したカラム名を返却する。
        return column.toString();

    }

    /**
     * デストラクタ。
     * GCによる当クラスの具象クラスアンロード時にコールされる。
     */
    public void finalize() throws SQLException {
        //　JDBC接続マネージャをクローズ。
        conn.close();
    }

}
