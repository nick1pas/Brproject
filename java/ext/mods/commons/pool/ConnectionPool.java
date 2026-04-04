/*
* Copyleft © 2024-2026 L2Brproject
* * This file is part of L2Brproject derived from aCis409/RusaCis3.8
* * L2Brproject is free software: you can redistribute it and/or modify it
* under the terms of the GNU General Public License as published by the
* Free Software Foundation, either version 3 of the License.
* * L2Brproject is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* General Public License for more details.
* * You should have received a copy of the GNU General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
* Our main Developers, Dhousefe-L2JBR, Agazes33, Ban-L2jDev, Warman, SrEli.
* Our special thanks, Nattan Felipe, Diego Fonseca, Junin, ColdPlay, Denky, MecBew, Localhost, MundvayneHELLBOY, SonecaL2, Eduardo.SilvaL2J, biLL, xpower, xTech, kakuzo
* as a contribution for the forum L2JBrasil.com
 */
package ext.mods.commons.pool;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicLong;
import ext.mods.commons.logging.CLogger;
import ext.mods.Config;

public final class ConnectionPool {
    private ConnectionPool() { throw new IllegalStateException("Utility class"); }
    
    private static final CLogger LOGGER = new CLogger(ConnectionPool.class.getName());
    private static final AtomicLong TOTAL_QUERIES = new AtomicLong(0);
    
    private static HikariDataSource _source;
    
    public static void init() {
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(Config.DATABASE_URL);
            config.setUsername(Config.DATABASE_LOGIN);
            config.setPassword(Config.DATABASE_PASSWORD);
            
            config.setMaximumPoolSize(50);
            config.setMinimumIdle(10);
            config.setIdleTimeout(300000);
            config.setMaxLifetime(1800000);
            config.setConnectionTestQuery("SELECT 1");
            config.setLeakDetectionThreshold(60000);
            config.setPoolName("GameServerPool");
            config.setRegisterMbeans(true);
            
            _source = new HikariDataSource(config);
            
            LOGGER.info("HikariCP Pool: 50/10 | Ready!");
            
        } catch (Exception e) {
            LOGGER.error("HikariCP failed!", e);
        }
    }
    
    public static void shutdown() {
        if (_source != null) {
            _source.close();
            _source = null;
        }
    }
    
    public static Connection getConnection() throws SQLException {
        Connection conn = _source.getConnection();
        TOTAL_QUERIES.incrementAndGet();
        return conn;
    }
    
    public static long getTotalQueries() {
        return TOTAL_QUERIES.get();
    }

    public static String getStats() {
        if (_source == null) return "HikariCP: not initialized";
        try {
            var mx = _source.getHikariPoolMXBean();
            if (mx != null) {
                return String.format("HikariCP: %d active | %d idle | %d total | %d queries",
                    mx.getActiveConnections(),
                    mx.getIdleConnections(),
                    mx.getTotalConnections(),
                    TOTAL_QUERIES.get());
            }
        } catch (Exception ignored) { }
        return String.format("HikariCP: %d queries total", TOTAL_QUERIES.get());
    }
}
