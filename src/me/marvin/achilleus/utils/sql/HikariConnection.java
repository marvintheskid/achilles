package me.marvin.achilleus.utils.sql;

import com.google.common.base.Preconditions;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class HikariConnection {
    private String host, user, db, pw;
    private int port, poolSize;
    private HikariDataSource connection;

    public HikariConnection(String host, int port, int poolSize, String user, String pw, String db) {
        this.poolSize = poolSize;
        this.host = host;
        this.user = user;
        this.port = port;
        this.db = db;
        this.pw = pw;
    }

    public void query(String query, Consumer<ResultSet> result, Object... params) {
        Preconditions.checkNotNull(connection, "connection is null");
        try (Connection conn = connection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            int index = 1;
            for (Object param : params) {
                stmt.setObject(index, param);
                index++;
            }
            result.accept(stmt.executeQuery());
        } catch (Exception ex) {
            result.accept(null);
        }
    }

    public void update(String query, Consumer<Integer> result, Object... params) {
        Preconditions.checkNotNull(connection, "connection is null");
        try (Connection conn = connection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            int index = 1;
            for (Object param : params) {
                stmt.setObject(index, param);
                index++;
            }
            result.accept(stmt.executeUpdate());
        } catch (Exception ex) {
            result.accept(-1);
        }
    }

    public void batchUpdate(String query, Consumer<Integer[]> result, BatchContainer... params) {
        Preconditions.checkNotNull(connection, "connection is null");
        try (Connection conn = connection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            for (BatchContainer container : params) {
                int index = 1;
                for (Object param : container.getObjects()) {
                    stmt.setObject(index, param);
                    index++;
                }
                stmt.addBatch();
            }
            result.accept(IntStream.of(stmt.executeBatch()).boxed().toArray(Integer[]::new));
        } catch (Exception ex) {
            result.accept(new Integer[]{-1});
        }
    }

    public void connect() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + db + "?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8");
        config.setUsername(user);
        config.setPassword(pw);
        config.setDriverClassName("com.mysql.jdbc.Driver");

        config.setMaximumPoolSize(poolSize);
        config.addDataSourceProperty("cachePrepStmts", true);
        config.addDataSourceProperty("prepStmtCacheSize", 250);
        config.addDataSourceProperty("prepStmtCacheSqlLimit", 2048);
        config.addDataSourceProperty("useServerPrepStmts", true);
        config.addDataSourceProperty("useLocalSessionState", true);
        config.addDataSourceProperty("rewriteBatchedStatements", true);
        config.addDataSourceProperty("cacheResultSetMetadata", true);
        config.addDataSourceProperty("cacheServerConfiguration", true);
        config.addDataSourceProperty("elideSetAutoCommits", true);
        config.addDataSourceProperty("maintainTimeStats", false);

        try {
            connection = new HikariDataSource(config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public HikariDataSource getInstance() {
        return connection;
    }
}
