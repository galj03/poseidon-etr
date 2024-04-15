package poseidon.DAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import poseidon.DAO._Interfaces.IPosztDAO;
import poseidon.DTO.Poszt;
import poseidon.DTO._Interfaces.IPoszt;
import poseidon.Exceptions.ArgumentNullException;
import poseidon.Exceptions.QueryException;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class OracleDBPosztDAO extends JdbcDaoSupport implements IPosztDAO {
    //region Properties
    private final DataSource _dataSource;
    //endregion

    //region Constructor
    @Autowired
    public OracleDBPosztDAO(DataSource dataSource) {
        _dataSource = dataSource;
        setDataSource(_dataSource);
    }
    //endregion

    @Override
    public Iterable<IPoszt> getAll() throws QueryException {
        return getRows("select * from poszt");
    }

    @Override
    public IPoszt getById(Integer id) throws QueryException {
        return getRow("select * from poszt where id=?", id);
    }

    @Override
    public IPoszt save(IPoszt poszt) throws QueryException {
        if (poszt.getPosztId() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();

            try {
                String sql = "INSERT INTO poszt(PS_kod, tartalom) VALUES (?, ?)";
                getJdbcTemplate().update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                    ps.setString(1, poszt.getPsCode());
                    ps.setString(2, poszt.getTartalom());
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
            String sql = "UPDATE poszt SET PS_kod=?, tartalom=? WHERE id=?";
            getJdbcTemplate().update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setString(1, poszt.getPsCode());
                ps.setString(2, poszt.getTartalom());
                ps.setString(3, poszt.getPosztId().toString());
                return ps;
            });
        } catch (DataAccessException exception) {
            throw new QueryException("Could not insert value into database", exception);
        }

        return getById(poszt.getPosztId());
    }

    @Override
    public void remove(IPoszt poszt) throws IllegalArgumentException, QueryException {
        if (poszt == null) throw new ArgumentNullException("poszt");
        if (poszt.getPosztId() == null) throw new ArgumentNullException("Poszt must be saved first.");

        String sql = "DELETE FROM poszt WHERE id=?";
        getJdbcTemplate().update(sql, poszt.getPosztId());
    }

    //region Private members
    private IPoszt getRow(String sql, Object... args) throws QueryException {
        try {
            List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql, args);

            if (rows.isEmpty()) return null;

            return new Poszt()
                    .setPosztId(((BigDecimal) rows.get(0).get("id")).intValue())
                    .setPsCode((String) rows.get(0).get("PS_kod"))
                    .setTartalom((String) rows.get(0).get("tartalom"));

        } catch (DataAccessException exception) {
            throw new QueryException("Could not get values from database", exception);
        }
    }

    private List<IPoszt> getRows(String sql, Object... args) throws QueryException {
        try {
            List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql, args);

            List<IPoszt> result = new ArrayList<>();

            for (Map<String, Object> row : rows) {
                result.add(
                        new Poszt()
                                .setPosztId(((BigDecimal) row.get("id")).intValue())
                                .setPsCode((String) row.get("PS_kod"))
                                .setTartalom((String) row.get("tartalom"))
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
