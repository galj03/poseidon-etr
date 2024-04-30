package poseidon.DAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;
import poseidon.Exceptions.QueryException;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Repository
public class BaseDAO extends JdbcDaoSupport {
    //region Properties
    private final DataSource _dataSource;
    //endregion

    //region Constructor
    @Autowired
    public BaseDAO(DataSource dataSource) {
        _dataSource = dataSource;
        setDataSource(_dataSource);
    }
    //endregion

    public Map<String, Object> getCustomRow(String sql, Object... args) throws QueryException {
        try {
            List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql, args);

            if (rows.isEmpty()) return null;

            return rows.get(0);

        } catch (DataAccessException exception) {
            throw new QueryException("Could not get values from database", exception);
        }
    }

    public List<Map<String, Object>> getCustomRows(String sql, Object... args) throws QueryException {
        try {
            List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql, args);

            if (rows.isEmpty()) return null;

            return rows;

        } catch (DataAccessException exception) {
            throw new QueryException("Could not get values from database", exception);
        }
    }
}
