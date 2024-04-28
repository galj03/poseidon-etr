package poseidon.DAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import poseidon.Constants;
import poseidon.DAO._Interfaces.IKurzusDAO;
import poseidon.DTO.Kurzus;
import poseidon.DTO.User;
import poseidon.DTO._Interfaces.IKurzus;
import poseidon.DTO._Interfaces.IUser;
import poseidon.Exceptions.ArgumentNullException;
import poseidon.Exceptions.QueryException;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class OracleDBKurzusDAO extends BaseDAO implements IKurzusDAO {
    //region Constructor
    @Autowired
    public OracleDBKurzusDAO(DataSource dataSource) {
        super(dataSource);
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
                String sql = "INSERT INTO kurzus(nev, oktato_PS_kod, kezdes_ideje_nap, kezdes_ideje_idopont, tantargy_id, terem_id, felveheto, vizsga) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                getJdbcTemplate().update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                    ps.setString(1, kurzus.getNev());
                    ps.setString(2, kurzus.getOktato());
                    ps.setString(3, kurzus.getKezdesNapja());
                    ps.setString(4, kurzus.getKezdesIdopontja().toString());
                    ps.setString(5, kurzus.getTantargyId().toString());
                    ps.setString(6, kurzus.getTeremId().toString());
                    ps.setString(7, kurzus.isFelveheto() ? "I" : "N");
                    ps.setString(8, kurzus.isVizsga() ? "I" : "N");
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
            String sql = "UPDATE kurzus SET nev=?, oktato_PS_kod=?, kezdes_ideje_nap=?, kezdes_ideje_idopont=?, tantargy_id=?, terem_id=?, felveheto=?, vizsga=? WHERE id=?";
            getJdbcTemplate().update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setString(1, kurzus.getNev());
                ps.setString(2, kurzus.getOktato());
                ps.setString(3, kurzus.getKezdesNapja());
                ps.setString(4, kurzus.getKezdesIdopontja().toString());
                ps.setString(5, kurzus.getTantargyId().toString());
                ps.setString(6, kurzus.getTeremId().toString());
                ps.setString(7, kurzus.isFelveheto() ? "I" : "N");
                ps.setString(8, kurzus.isVizsga() ? "I" : "N");
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

            Boolean isFelveheto = rows.get(0).get("felveheto").equals(Constants.TRUE);
            Boolean isVizsga = rows.get(0).get("vizsga").equals(Constants.TRUE);

            return new Kurzus()
                    .setKurzusId(((BigDecimal) rows.get(0).get("id")).intValue())
                    .setNev((String) rows.get(0).get("nev"))
                    .setOktato((String) rows.get(0).get("oktato_PS_kod"))
                    .setKezdesNapja((String) rows.get(0).get("kezdes_ideje_nap"))
                    .setKezdesIdopontja(((BigDecimal) rows.get(0).get("kezdes_ideje_idopont")).intValue())
                    .setTantargyId(((BigDecimal) rows.get(0).get("tantargy_id")).intValue())
                    .setTeremId(((BigDecimal) rows.get(0).get("terem_id")).intValue())
                    .setIsFelveheto(isFelveheto)
                    .setIsVizsga(isVizsga);

        } catch (DataAccessException exception) {
            throw new QueryException("Could not get values from database", exception);
        }
    }

    @Override
    public Map<IKurzus, Map<IUser, Integer>> getTeachingCourses(String teacher_ps_kod) {
        String sql = "SELECT kurzus.nev, ps_kod, kurzus_id, jegy FROM kurzus " +
                "INNER JOIN felvette ON kurzus.id = felvette.kurzus_id " +
                "WHERE kurzus.oktato_ps_kod =?";
        var queryResultKurzusok = super.getCustomRows(sql, teacher_ps_kod);
        Map<IKurzus, Map<IUser, Integer>> hallgatokJegyeKurzusonkent = new HashMap<>();
        Map<IUser, Integer> hallgatokJegyei = new HashMap<>();
        IKurzus lastKurzus = null;
        IKurzus tmpKurzus = null;
        for (var item : queryResultKurzusok) {
            tmpKurzus = new Kurzus()
                    .setKurzusId(((BigDecimal) item.get("kurzus_id")).intValue())
                    .setNev((String) item.get("nev"));

            if (lastKurzus == null) {
                lastKurzus = tmpKurzus;
            }

            IUser tmpUser = new User()
                    .setPsCode((String)item.get("ps_kod"));

            if (lastKurzus.getKurzusId() != tmpKurzus.getKurzusId()) {
                hallgatokJegyeKurzusonkent.put(tmpKurzus, new HashMap<>(hallgatokJegyei));
                lastKurzus = tmpKurzus;
                hallgatokJegyei.clear();
                hallgatokJegyei.put(tmpUser, item.get("jegy") == null ? 0 : ((BigDecimal)item.get("jegy")).intValue());
            } else {
                hallgatokJegyei.put(tmpUser, item.get("jegy") == null ? 0 : ((BigDecimal)item.get("jegy")).intValue());
            }
        }
        if (lastKurzus == tmpKurzus) {
            hallgatokJegyeKurzusonkent.put(lastKurzus, new HashMap<>(hallgatokJegyei));
        }
        return hallgatokJegyeKurzusonkent;
    }

    private List<IKurzus> getRows(String sql, Object... args) throws QueryException {
        try {
            List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql, args);

            List<IKurzus> result = new ArrayList<>();

            for (Map<String, Object> row : rows) {
                Boolean isFelveheto = row.get("felveheto").equals(Constants.TRUE);
                Boolean isVizsga = row.get("vizsga").equals(Constants.TRUE);

                result.add(
                        new Kurzus()
                                .setKurzusId(((BigDecimal) row.get("id")).intValue())
                                .setNev((String) row.get("nev"))
                                .setOktato((String) row.get("oktato_PS_kod"))
                                .setKezdesNapja((String) row.get("kezdes_ideje_nap"))
                                .setKezdesIdopontja(((BigDecimal) row.get("kezdes_ideje_idopont")).intValue())
                                .setTantargyId(((BigDecimal) row.get("tantargy_id")).intValue())
                                .setTeremId(row.get("terem_id") == null ? null : ((BigDecimal) row.get("terem_id")).intValue())
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
