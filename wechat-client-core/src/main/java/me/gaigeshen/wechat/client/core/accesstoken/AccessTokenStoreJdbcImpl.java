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
 * 此访问令牌存储器器基于数据库的数据源实现
 *
 * @author gaigeshen
 */
public class AccessTokenStoreJdbcImpl implements AccessTokenStore {

  private static final String TABLE = "wechat_access_token";

  private static final String INSERT = "insert into " + TABLE + " (appid, access_token, expires_in, expires_timestamp, update_time) values (?, ?, ?, ?, ?)";

  private static final String UPDATE = "update " + TABLE + " set access_token = ?, expires_in = ?, expires_timestamp = ?, update_time = ? where appid = ?";

  private static final String DELETE = "delete from " + TABLE + " where appid = ?";

  private static final String FIND = "select access_token, expires_in, expires_timestamp, update_time from " + TABLE + " where appid = ?";

  private static final String FIND_ALL = "select access_token, expires_in, expires_timestamp, update_time from " + TABLE;

  // 用于转换单条访问令牌数据库记录
  private static final ResultSetHandler<Map<String, Object>> DATABASE_RESULT_HANDLER = new MapHandler();

  // 用于转换多条访问令牌数据库记录
  private static final ResultSetHandler<List<Map<String, Object>>> DATABASE_RESULTS_HANDLER = new MapListHandler();

  private final DataSource dataSource;

  private final QueryRunner queryRunner;

  private AccessTokenStoreJdbcImpl(DataSource dataSource) {
    this.dataSource = Asserts.notNull(dataSource, "dataSource");
    this.queryRunner = new QueryRunner(dataSource);
  }

  /**
   * 创建访问令牌存储器
   *
   * @param dataSource 数据源
   * @return 访问令牌存储器
   */
  public static AccessTokenStoreJdbcImpl create(DataSource dataSource) {
    return new AccessTokenStoreJdbcImpl(dataSource);
  }

  @Override
  public boolean saveOrUpdate(AccessToken accessToken) throws AccessTokenStoreException {
    Connection connection = prepareTransactionalConnection();
    try {
      // 先执行更新操作，如果更新成功则说明该应用已经存在访问令牌
      int result = queryRunner.update(connection, UPDATE, accessToken.getAccessToken(), accessToken.getExpiresIn(), accessToken.getExpiresTimestamp(), new Date(), accessToken.getAppid());
      if (result > 0) {
        connection.commit();
        return false;
      }
      // 为新应用增加访问令牌
      queryRunner.update(connection, INSERT, accessToken.getAppid(), accessToken.getAccessToken(), accessToken.getExpiresIn(), accessToken.getExpiresTimestamp(), new Date());
      connection.commit();
    } catch (SQLException e) {
      // 操作数据库过程中发生异常，本次所有操作失败，抛出异常
      DbUtils.rollbackAndCloseQuietly(connection);
      throw new AccessTokenStoreException("Could not save or update access token:: " + accessToken.getAppid(), e);
    } finally {
      // 确保数据库连接被关闭
      DbUtils.closeQuietly(connection);
    }
    // 所有数据库的操作成功，增加了新应用的访问令牌
    return true;
  }

  @Override
  public void deleteByAppid(String appid) throws AccessTokenStoreException {
    try {
      queryRunner.update(DELETE, Asserts.notBlank(appid, "appid"));
    } catch (SQLException e) {
      throw new AccessTokenStoreException("Could not delete access token:: " + appid, e);
    }
  }

  @Override
  public AccessToken findByAppid(String appid) throws AccessTokenStoreException {
    Map<String, Object> result;
    try {
      result = queryRunner.query(FIND, DATABASE_RESULT_HANDLER, Asserts.notBlank(appid, "appid"));
    } catch (SQLException e) {
      throw new AccessTokenStoreException("Could not find access token:: " + appid, e);
    }
    if (Objects.isNull(result)) {
      return null;
    }
    return parseAccessToken(result);
  }

  @Override
  public Collection<AccessToken> findAll() throws AccessTokenStoreException {
    List<AccessToken> accessTokens = new ArrayList<>();
    List<Map<String, Object>> results;
    try {
      results = queryRunner.query(FIND_ALL, DATABASE_RESULTS_HANDLER);
    } catch (SQLException e) {
      throw new AccessTokenStoreException("Could not find all access tokens:: ", e);
    }
    if (results.isEmpty()) {
      return accessTokens;
    }
    for (Map<String, Object> result : results) {
      accessTokens.add(parseAccessToken(result));
    }
    return accessTokens;
  }

  private AccessToken parseAccessToken(Map<String, Object> databaseResult) throws AccessTokenStoreException {
    try {
      return AccessToken.builder()
              .setAppid((String) databaseResult.get("appid"))
              .setAccessToken((String) databaseResult.get("access_token"))
              .setExpiresIn((Long) databaseResult.get("expires_in"))
              .setExpiresTimestamp((Long) databaseResult.get("expires_timestamp"))
              .setUpdateTime((Date) databaseResult.get("update_time"))
              .build();
    } catch (Exception e) {
      throw new AccessTokenStoreException("Could not parse to access token object:: database result " + databaseResult);
    }
  }

  private Connection prepareTransactionalConnection() throws AccessTokenStoreException {
    Connection connection = null;
    try {
      connection = dataSource.getConnection();
      connection.setAutoCommit(false);
      return connection;
    } catch (SQLException e) {
      DbUtils.closeQuietly(connection);
      throw new AccessTokenStoreException("Could not get database connection:: data source " + dataSource);
    }
  }

}
