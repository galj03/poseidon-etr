package poseidon.DAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import poseidon.DAO._Interfaces.IKommentDAO;
import poseidon.DTO.Komment;
import poseidon.DTO._Interfaces.IKomment;
import poseidon.Exceptions.ArgumentNullException;
import poseidon.Exceptions.QueryException;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class OracleDBKommentDAO extends JdbcDaoSupport implements IKommentDAO {
    //region Properties
    private final DataSource _dataSource;
    //endregion

    //region Constructor
    @Autowired
    public OracleDBKommentDAO(DataSource dataSource) {
        _dataSource = dataSource;
        setDataSource(_dataSource);
    }
    //endregion

    @Override
    public Iterable<IKomment> getAll() throws QueryException {
        return getRows("select * from komment");
    }

    @Override
    public IKomment getById(Integer id) throws QueryException {
        return getRow("select * from komment where id=?", id);
    }

    @Override
    public IKomment save(IKomment komment) throws QueryException {
        if (komment.getKommentId() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();

            try {
                String sql = "INSERT INTO komment(poszt_id, PS_kod, tartalom) VALUES (?, ?, ?)";
                getJdbcTemplate().update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                    ps.setString(1, komment.getPosztId().toString());
                    ps.setString(2, komment.getPsCode());
                    ps.setString(3, komment.getTartalom());
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
            String sql = "UPDATE komment SET poszt_id=?, PS_kod=?, tartalom=? WHERE id=?";
            getJdbcTemplate().update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setString(1, komment.getPosztId().toString());
                ps.setString(2, komment.getPsCode());
                ps.setString(3, komment.getTartalom());
                ps.setString(4, komment.getKommentId().toString());
                return ps;
            });
        } catch (DataAccessException exception) {
            throw new QueryException("Could not insert value into database", exception);
        }

        return getById(komment.getKommentId());
    }

    @Override
    public void remove(IKomment komment) throws IllegalArgumentException, QueryException {
        if (komment == null) throw new ArgumentNullException("komment");
        if (komment.getKommentId() == null) throw new ArgumentNullException("Komment must be saved first.");

        String sql = "DELETE FROM komment WHERE id=?";
        getJdbcTemplate().update(sql, komment.getKommentId());
    }

    //region Private members
    private IKomment getRow(String sql, Object... args) throws QueryException {
        try {
            List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql, args);

            if (rows.isEmpty()) return null;

            return new Komment()
                    .setKommentId(((BigDecimal) rows.get(0).get("id")).intValue())
                    .setPosztId(((BigDecimal) rows.get(0).get("poszt_id")).intValue())
                    .setPsCode((String) rows.get(0).get("PS_kod"))
                    .setTartalom((String) rows.get(0).get("tartalom"));

        } catch (DataAccessException exception) {
            throw new QueryException("Could not get values from database", exception);
        }
    }

    private List<IKomment> getRows(String sql, Object... args) throws QueryException {
        try {
            List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql, args);

            List<IKomment> result = new ArrayList<>();

            for (Map<String, Object> row : rows) {
                result.add(
                        new Komment()
                                .setKommentId(((BigDecimal) row.get("id")).intValue())
                                .setPosztId(((BigDecimal) row.get("poszt_id")).intValue())
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
