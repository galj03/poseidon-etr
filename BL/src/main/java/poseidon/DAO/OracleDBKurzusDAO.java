package poseidon.DAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import poseidon.Constants;
import poseidon.DAO._Interfaces.IKurzusDAO;
import poseidon.DTO.Kurzus;
import poseidon.DTO._Interfaces.IKurzus;
import poseidon.Exceptions.ArgumentNullException;
import poseidon.Exceptions.QueryException;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class OracleDBKurzusDAO extends JdbcDaoSupport implements IKurzusDAO {
    //region Properties
    private final DataSource _dataSource;
    //endregion

    //region Constructor
    @Autowired
    public OracleDBKurzusDAO(DataSource dataSource) {
        _dataSource = dataSource;
        setDataSource(_dataSource);
    }
    //endregion

    @Override
    public Iterable<IKurzus> getAll() throws QueryException {
        return getRows("select * from kurzus");
    }

    @Override
    public IKurzus getById(Integer id) throws QueryException {
        return getRow("select * from kurzus where id=?", id);
    }

    @Override
    public IKurzus save(IKurzus kurzus) throws QueryException {
        if (kurzus.getKurzusId() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();

            try {
                String sql = "INSERT INTO kurzus(id, nev, oktato_PS_kod, kezdes_ideje_nap, kezdes_ideje_idopont, tantargy_id, terem_id, felveheto, vizsga) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                getJdbcTemplate().update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                    ps.setString(1, kurzus.getNev());
                    ps.setString(2, kurzus.getOktato());
                    ps.setString(3, kurzus.getKezdesNapja());
                    ps.setString(4, kurzus.getKezdesIdopontja().toString());
                    ps.setString(5, kurzus.getTantargyId().toString());
                    ps.setString(6, kurzus.getTeremId().toString());
                    ps.setString(7, kurzus.isFelveheto().toString());
                    ps.setString(8, kurzus.isVizsga().toString());
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
            String sql = "UPDATE komment SET nev=?, oktato_PS_kod=?, kezdes_ideje_nap=?, kezdes_ideje_idopont=?, tantargy_id=?, terem_id=?, felveheto=?, vizsga=? WHERE id=?";
            getJdbcTemplate().update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setString(1, kurzus.getNev());
                ps.setString(2, kurzus.getOktato());
                ps.setString(3, kurzus.getKezdesNapja());
                ps.setString(4, kurzus.getKezdesIdopontja().toString());
                ps.setString(5, kurzus.getTantargyId().toString());
                ps.setString(6, kurzus.getTeremId().toString());
                ps.setString(7, kurzus.isFelveheto().toString());
                ps.setString(8, kurzus.isVizsga().toString());
                ps.setString(9, kurzus.getKurzusId().toString());
                return ps;
            });
        } catch (DataAccessException exception) {
            throw new QueryException("Could not insert value into database", exception);
        }

        return getById(kurzus.getKurzusId());
    }

    @Override
    public void remove(IKurzus kurzus) throws IllegalArgumentException, QueryException {
        if (kurzus == null) throw new ArgumentNullException("kurzus");
        if (kurzus.getKurzusId() == null) throw new ArgumentNullException("Kurzus must be saved first.");

        String sql = "DELETE FROM kurzus WHERE id=?";
        getJdbcTemplate().update(sql, kurzus.getKurzusId());
    }

    //region Private members
    private IKurzus getRow(String sql, Object... args) throws QueryException {
        try {
            List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql, args);

            if (rows.isEmpty()) return null;

            Boolean isFelveheto = rows.get(0).get("felveheto") == Constants.TRUE;
            Boolean isVizsga = rows.get(0).get("vizsga") == Constants.TRUE;

            return new Kurzus()
                    .setKurzusId(((BigDecimal) rows.get(0).get("id")).intValue())
                    .setNev((String) rows.get(0).get("nev"))
                    .setOktato((String) rows.get(0).get("oktato_PS_kod"))
                    .setKezdesNapja((String) rows.get(0).get("kezdes_ideje_nap"))
                    .setKezdesIdopontja((Timestamp) rows.get(0).get("kezdes_ideje_idopont"))
                    .setTantargyId(((BigDecimal) rows.get(0).get("tantargy_id")).intValue())
                    .setTeremId(((BigDecimal) rows.get(0).get("terem_id")).intValue())
                    .setIsFelveheto(isFelveheto)
                    .setIsVizsga(isVizsga);

        } catch (DataAccessException exception) {
            throw new QueryException("Could not get values from database", exception);
        }
    }

    private List<IKurzus> getRows(String sql, Object... args) throws QueryException {
        try {
            List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql, args);

            List<IKurzus> result = new ArrayList<>();

            for (Map<String, Object> row : rows) {
                Boolean isFelveheto = row.get("felveheto") == Constants.TRUE;
                Boolean isVizsga = row.get("vizsga") == Constants.TRUE;

                result.add(
                        new Kurzus()
                                .setKurzusId(((BigDecimal) row.get("id")).intValue())
                                .setNev((String) row.get("nev"))
                                .setOktato((String) row.get("oktato_PS_kod"))
                                .setKezdesNapja((String) row.get("kezdes_ideje_nap"))
                                .setKezdesIdopontja((Timestamp) row.get("kezdes_ideje_idopont"))
                                .setTantargyId(((BigDecimal) row.get("tantargy_id")).intValue())
                                .setTeremId(((BigDecimal) row.get("terem_id")).intValue())
                                .setIsFelveheto(isFelveheto)
                                .setIsVizsga(isVizsga)
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
