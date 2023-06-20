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

    public void update(String sql, PreparedStatementSetter pss) throws DataAccessException {
        try(Connection conn = ConnectionManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pss.setValues(pstmt);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    @SuppressWarnings("rawtypes")
    public List query(String sql, PreparedStatementSetter pss, RowMapper rowMapper) throws SQLException {
        try(Connection conn = ConnectionManager.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery()) {
            pss.setValues(pstmt);
            List<Object> result = new ArrayList<>();
            if (rs.next()) {
                result.add(rowMapper.mapRow(rs));
            }
            return result;
        }
    }

    @SuppressWarnings("rawtypes")
    public Object queryForObject(String sql, PreparedStatementSetter pss, RowMapper rowMapper) throws SQLException {
        List result = query(sql, pss, rowMapper);
        if (result.isEmpty()) {
            return null;
        }
        return result.get(0);
    }

}
