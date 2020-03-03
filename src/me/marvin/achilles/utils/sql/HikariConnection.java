package me.marvin.achilles.utils.sql;

import com.google.common.base.Preconditions;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.IntStream;

/*
 * Copyright (c) 2019 marvintheskid (Kovács Márton)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
 * OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */

@Getter
@NoArgsConstructor
public class HikariConnection {
    private String host, user, db, pw;
    private int port, poolSize;
    private HikariDataSource connection;
    private ExecutorService service;

    public HikariConnection(String host, int port, String user, String pw, String db, boolean async, int poolSize) {
        this.poolSize = poolSize;
        this.host = host;
        this.user = user;
        this.port = port;
        this.db = db;
        this.pw = pw;
        if (async) {
            this.service = Executors.newCachedThreadPool();
        }
    }

    public void query(String query, Consumer<ResultSet> result, Object... params) {
        this.query(true, query, result, params);
    }

    public void update(String query, Consumer<Integer> result, BatchContainer container) {
        this.update(true, query, result, container.getObjects());
    }

    public void update(String query, Consumer<Integer> result, Object... params) {
        this.update(true, query, result, params);
    }

    public void batchUpdate(String query, Consumer<Integer[]> result, BatchContainer... params) {
        this.batchUpdate(true, query, result, params);
    }

    public void query(boolean async, String query, Consumer<ResultSet> result, Object... params) {
        if (service != null && async) {
            service.execute(() -> queryInternal(query, result, params));
        } else {
            queryInternal(query, result, params);
        }
    }

    public void update(boolean async, String query, Consumer<Integer> result, Object... params) {
        if (service != null && async) {
            service.execute(() -> updateInternal(query, result, params));
        } else {
            updateInternal(query, result, params);
        }
    }

    public void batchUpdate(boolean async, String query, Consumer<Integer[]> result, BatchContainer... params) {
        if (service != null && async) {
            service.execute(() -> batchUpdate(query, result, params));
        } else {
            batchUpdate(query, result, params);
        }
    }

    private void queryInternal(String query, Consumer<ResultSet> result, Object... params) {
        Preconditions.checkNotNull(connection, "connection is null");
        try (Connection conn = connection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            int index = 1;
            for (Object param : params) {
                stmt.setObject(index, param);
                index++;
            }
            try (ResultSet rs = stmt.executeQuery()) {
                result.accept(rs);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            result.accept(null);
        }
    }

    private void updateInternal(String query, Consumer<Integer> result, Object... params) {
        Preconditions.checkNotNull(connection, "connection is null");
        try (Connection conn = connection.getConnection(); PreparedStatement stmt = conn.prepareStatement(query)) {
            int index = 1;
            for (Object param : params) {
                stmt.setObject(index, param);
                index++;
            }
            result.accept(stmt.executeUpdate());
        } catch (Exception ex) {
            ex.printStackTrace();
            result.accept(-1);
        }
    }

    private void updateBatchInternal(String query, Consumer<Integer[]> result, BatchContainer... params) {
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
            ex.printStackTrace();
            result.accept(new Integer[]{-1});
        }
    }

    public void connect() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + db + "?useUnicode=true&characterEncoding=UTF-8");
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
        config.setLeakDetectionThreshold(60 * 1000);

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
