package poseidon.DAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import poseidon.DAO._Interfaces.ITeremDAO;
import poseidon.DTO.Terem;
import poseidon.DTO._Interfaces.ITerem;
import poseidon.Exceptions.ArgumentNullException;
import poseidon.Exceptions.QueryException;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class OracleDBTeremDAO extends JdbcDaoSupport implements ITeremDAO {
    //region Properties
    private final DataSource _dataSource;
    //endregion

    //region Constructor
    @Autowired
    public OracleDBTeremDAO(DataSource dataSource) {
        _dataSource = dataSource;
        setDataSource(_dataSource);
    }
    //endregion

    @Override
    public Iterable<ITerem> getAll() throws QueryException {
        return getRows("select * from terem");
    }

    @Override
    public ITerem getById(Integer id) throws QueryException {
        return getRow("select * from terem where id=?", id);
    }

    @Override
    public ITerem save(ITerem terem) throws QueryException {
        if (terem.getTeremId() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();

            try {
                String sql = "INSERT INTO terem(id, ferohely) VALUES (?, ?)";
                getJdbcTemplate().update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                    ps.setString(1, terem.getFerohely().toString());
                    return ps;
                }, keyHolder);
            } catch (DataAccessException exception) {
                throw new QueryException("Could not insert value into database", exception);
            }

            Number key = keyHolder.getKey();
            if (key == null) throw new QueryException("Failed to get inserted record's id");

            return getById(key.intValue());
        }

        try {
            String sql = "UPDATE terem SET ferohely=? WHERE id=?";
            getJdbcTemplate().update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setString(1, terem.getFerohely().toString());
                ps.setString(2, terem.getTeremId().toString());
                return ps;
            });
        } catch (DataAccessException exception) {
            throw new QueryException("Could not insert value into database", exception);
        }

        return getById(terem.getTeremId());
    }

    @Override
    public void remove(ITerem terem) throws IllegalArgumentException, QueryException {
        if (terem == null) throw new ArgumentNullException("terem");
        if (terem.getTeremId() == null) throw new ArgumentNullException("Terem must be saved first.");

        String sql = "DELETE FROM terem WHERE id=?";
        getJdbcTemplate().update(sql, terem.getTeremId());
    }

    //region Private members
    private ITerem getRow(String sql, Object... args) throws QueryException {
        try {
            List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql, args);

            if (rows.isEmpty()) return null;

            return new Terem()
                    .setTeremId(((BigDecimal) rows.get(0).get("id")).intValue())
                    .setFerohely(((BigDecimal) rows.get(0).get("ferohely")).intValue());

        } catch (DataAccessException exception) {
            throw new QueryException("Could not get values from database", exception);
        }
    }

    private List<ITerem> getRows(String sql, Object... args) throws QueryException {
        try {
            List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql, args);

            List<ITerem> result = new ArrayList<>();

            for (Map<String, Object> row : rows) {
                result.add(
                        new Terem()
                                .setTeremId(((BigDecimal) row.get("id")).intValue())
                                .setFerohely(((BigDecimal) row.get("ferohely")).intValue())
                );
            }

            return result;

        } catch (DataAccessException exception) {
            throw new QueryException("Could not get values from database", exception);
        } catch (QueryException exception) {
            throw new QueryException("Failed to query a nested value", exception);
        }
    }
    //endregion
}
