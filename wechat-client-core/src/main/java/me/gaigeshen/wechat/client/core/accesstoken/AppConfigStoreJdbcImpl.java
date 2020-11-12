package me.gaigeshen.wechat.client.core.accesstoken;

import me.gaigeshen.wechat.client.core.util.Asserts;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * 此应用配置存储器基于数据库的数据源实现
 *
 * @author gaigeshen
 */
public class AppConfigStoreJdbcImpl implements AppConfigStore {

  private static final String TABLE = "wechat_app_config";

  private static final String INSERT = "insert into " + TABLE + " (appid, secret) values (?, ?)";

  private static final String UPDATE = "update " + TABLE + " set secret = ? where appid = ?";

  private static final String DELETE = "delete from " + TABLE + " where appid = ?";

  private static final String FIND = "select appid, secret from " + TABLE + " where appid = ?";

  private static final String FIND_ALL = "select appid, secret from " + TABLE;

  // 用于转换单条访问令牌数据库记录
  private static final ResultSetHandler<Map<String, Object>> DATABASE_RESULT_HANDLER = new MapHandler();

  // 用于转换多条访问令牌数据库记录
  private static final ResultSetHandler<List<Map<String, Object>>> DATABASE_RESULTS_HANDLER = new MapListHandler();

  private final DataSource dataSource;

  private final QueryRunner queryRunner;

  private AppConfigStoreJdbcImpl(DataSource dataSource) {
    this.dataSource = Asserts.notNull(dataSource, "dataSource");
    this.queryRunner = new QueryRunner(dataSource);
  }

  /**
   * 创建应用配置存储器
   *
   * @param dataSource 数据源
   * @return 应用配置存储器
   */
  public static AppConfigStoreJdbcImpl create(DataSource dataSource) {
    return new AppConfigStoreJdbcImpl(dataSource);
  }

  @Override
  public boolean saveOrUpdate(AppConfig appConfig) throws AppConfigStoreException {
    Connection connection = prepareTransactionalConnection();
    try {
      // 先执行更新操作，如果更新成功则说明该应用已经存在应用配置
      int result = queryRunner.update(connection, UPDATE, appConfig.getSecret(), appConfig.getAppid());
      if (result > 0) {
        connection.commit();
        return false;
      }
      // 为新应用增加应用配置
      queryRunner.update(connection, INSERT, appConfig.getSecret(), appConfig.getAppid());
      connection.commit();
    } catch (SQLException e) {
      // 操作数据库过程中发生异常，本次所有操作失败，抛出异常
      DbUtils.rollbackAndCloseQuietly(connection);
      throw new AppConfigStoreException("Could not save or update app config:: " + appConfig.getAppid(), e);
    } finally {
      // 确保数据库连接被关闭
      DbUtils.closeQuietly(connection);
    }
    // 所有数据库的操作成功，增加了新应用的访问令牌
    return true;
  }

  @Override
  public void deleteByAppid(String appid) throws AppConfigStoreException {
    try {
      queryRunner.update(DELETE, Asserts.notBlank(appid, "appid"));
    } catch (SQLException e) {
      throw new AppConfigStoreException("Could not delete app config:: " + appid, e);
    }
  }

  @Override
  public AppConfig findByAppid(String appid) throws AppConfigStoreException {
    Map<String, Object> result;
    try {
      result = queryRunner.query(FIND, DATABASE_RESULT_HANDLER, Asserts.notBlank(appid, "appid"));
    } catch (SQLException e) {
      throw new AppConfigStoreException("Could not find app config:: " + appid, e);
    }
    if (Objects.isNull(result)) {
      return null;
    }
    return parseAppConfig(result);
  }

  @Override
  public Collection<AppConfig> findAll() throws AppConfigStoreException {
    List<AppConfig> accessTokens = new ArrayList<>();
    List<Map<String, Object>> results;
    try {
      results = queryRunner.query(FIND_ALL, DATABASE_RESULTS_HANDLER);
    } catch (SQLException e) {
      throw new AppConfigStoreException("Could not find all app configs:: ", e);
    }
    if (results.isEmpty()) {
      return accessTokens;
    }
    for (Map<String, Object> result : results) {
      accessTokens.add(parseAppConfig(result));
    }
    return accessTokens;
  }

  private AppConfig parseAppConfig(Map<String, Object> databaseResult) throws AppConfigStoreException {
    try {
      return AppConfig.builder()
              .setAppid((String) databaseResult.get("appid"))
              .setSecret((String) databaseResult.get("secret"))
              .build();
    } catch (Exception e) {
      throw new AppConfigStoreException("Could not parse to app config object:: database result " + databaseResult);
    }
  }

  private Connection prepareTransactionalConnection() throws AppConfigStoreException {
    Connection connection = null;
    try {
      connection = dataSource.getConnection();
      connection.setAutoCommit(false);
      return connection;
    } catch (SQLException e) {
      DbUtils.closeQuietly(connection);
      throw new AppConfigStoreException("Could not get database connection:: data source " + dataSource);
    }
  }

}
