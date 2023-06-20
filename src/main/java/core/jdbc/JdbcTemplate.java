package core.jdbc;

import core.jdbc.ConnectionManager;
import next.model.User;
import org.h2.result.Row;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcTemplate {

    public void update(String sql, Object... parameters) throws DataAccessException {
        try(Connection conn = ConnectionManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < parameters.length; i++) {
                pstmt.setObject(i + 1, parameters[i]);

            }
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    @SuppressWarnings("rawtypes")
    public <T> List<T> query(String sql, RowMapper<T> rm, PreparedStatementSetter pss) throws DataAccessException {
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            pss.setValues(pstmt);
            List<T> result = new ArrayList<>();
            if (rs.next()) {
                result.add(rm.mapRow(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    @SuppressWarnings("rawtypes")
    public <T> T queryForObject(String sql, RowMapper<T> rm, PreparedStatementSetter pss) throws DataAccessException {
        List<T> result = query(sql, rm, pss);
        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }

}
