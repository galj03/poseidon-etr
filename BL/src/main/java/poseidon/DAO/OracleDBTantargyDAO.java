package poseidon.DAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import poseidon.Constants;
import poseidon.DAO._Interfaces.ITantargyDAO;
import poseidon.DTO.Kurzus;
import poseidon.DTO.Tantargy;
import poseidon.DTO._Interfaces.IKurzus;
import poseidon.DTO._Interfaces.ITantargy;
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
public class OracleDBTantargyDAO extends BaseDAO implements ITantargyDAO {
    //region Constructor
    @Autowired
    public OracleDBTantargyDAO(DataSource dataSource) {
        super(dataSource);
    }
    //endregion

    @Override
    public Iterable<ITantargy> getAll() throws QueryException {
        return getRows("select * from tantargy");
    }

    @Override
    public ITantargy getById(Integer id) throws QueryException {
        return getRow("select * from tantargy where id=?", id);
    }

    @Override
    public ITantargy save(ITantargy tantargy) throws QueryException {
        if (tantargy.getTantargyId() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();

            try {
                String sql = "INSERT INTO tantargy(nev, targyfelelos) VALUES (?, ?)";
                getJdbcTemplate().update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                    ps.setString(1, tantargy.getNev());
                    ps.setString(2, tantargy.getFelelos());
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
            String sql = "UPDATE tantargy SET nev=?, targyfelelos=? WHERE id=?";
            getJdbcTemplate().update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setString(1, tantargy.getNev());
                ps.setString(2, tantargy.getFelelos());
                ps.setString(3, tantargy.getTantargyId().toString());
                return ps;
            });
        } catch (DataAccessException exception) {
            throw new QueryException("Could not insert value into database", exception);
        }

        return getById(tantargy.getTantargyId());
    }

    @Override
    public void remove(ITantargy tantargy) throws IllegalArgumentException, QueryException {
        if (tantargy == null) throw new ArgumentNullException("tantargy");
        if (tantargy.getTantargyId() == null) throw new ArgumentNullException("Tantargy must be saved first.");

        String sql = "DELETE FROM tantargy WHERE id=?";
        getJdbcTemplate().update(sql, tantargy.getTantargyId());
    }

    @Override
    public Map<ITantargy, List<IKurzus>> getTeachingSubjects(String ps_kod) {
        String sql = "SELECT tantargy.id as tantargy_id, tantargy.nev as tantargy_nev, " +
                "tantargy.targyfelelos, " +
                "kurzus.id as kurzus_id, kurzus.nev as kurzus_nev, " +
                "kurzus.oktato_ps_kod, kurzus.kezdes_ideje_nap, kurzus.kezdes_ideje_idopont, " +
                "kurzus.terem_id, kurzus.felveheto, kurzus.vizsga " +
                "from tantargy " +
                "INNER JOIN kurzus ON tantargy.id = kurzus.tantargy_id " +
                "WHERE targyfelelos=?";
        var list = super.getCustomRows(sql, ps_kod);
        Map<ITantargy, List<IKurzus>> tanitottTargyak = new HashMap<>();
        ITantargy lastTantargy = null;
        ITantargy tmpTargy = null;
        IKurzus tmpKurzus = null;
        List<IKurzus> targyonBeluliKurzusok = new ArrayList<>();
        for (var item : list) {
            tmpTargy = new Tantargy()
                    .setTantargyId(((BigDecimal) item.get("tantargy_id")).intValue())
                    .setNev((String) item.get("tantargy_nev"))
                    .setFelelos((String) item.get("targyfelelos"));

            if (lastTantargy == null) {
                lastTantargy = tmpTargy;
            }

            Boolean isFelveheto = item.get("felveheto").equals(Constants.TRUE);
            Boolean isVizsga = item.get("vizsga").equals(Constants.TRUE);

            tmpKurzus = new Kurzus()
                    .setKurzusId(((BigDecimal) item.get("kurzus_id")).intValue())
                    .setNev((String) item.get("kurzus_nev"))
                    .setOktato((String) item.get("oktato_PS_kod"))
                    .setKezdesNapja((String) item.get("kezdes_ideje_nap"))
                    .setKezdesIdopontja(((BigDecimal) item.get("kezdes_ideje_idopont")).intValue())
                    .setTantargyId(((BigDecimal) item.get("tantargy_id")).intValue())
                    .setTeremId(item.get("terem_id") == null ? null : ((BigDecimal) item.get("terem_id")).intValue())
                    .setIsFelveheto(isFelveheto)
                    .setIsVizsga(isVizsga);

            if (tmpTargy.getTantargyId() != lastTantargy.getTantargyId()) {
                tanitottTargyak.put(lastTantargy,new ArrayList<>(targyonBeluliKurzusok));
                lastTantargy = tmpTargy;
                targyonBeluliKurzusok.clear();
                targyonBeluliKurzusok.add(tmpKurzus);
            } else {
                targyonBeluliKurzusok.add(tmpKurzus);
            }
        }
        if (tmpTargy == lastTantargy) {
            tanitottTargyak.put(lastTantargy, targyonBeluliKurzusok);
        }
        return tanitottTargyak;
    }

    //region Private members
    private ITantargy getRow(String sql, Object... args) throws QueryException {
        try {
            List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql, args);

            if (rows.isEmpty()) return null;

            return new Tantargy()
                    .setTantargyId(((BigDecimal) rows.get(0).get("id")).intValue())
                    .setNev((String) rows.get(0).get("nev"))
                    .setFelelos((String) rows.get(0).get("targyfelelos"));

        } catch (DataAccessException exception) {
            throw new QueryException("Could not get values from database", exception);
        }
    }

    private List<ITantargy> getRows(String sql, Object... args) throws QueryException {
        try {
            List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql, args);

            List<ITantargy> result = new ArrayList<>();

            for (Map<String, Object> row : rows) {
                result.add(
                        new Tantargy()
                                .setTantargyId(((BigDecimal) row.get("id")).intValue())
                                .setNev((String) row.get("nev"))
                                .setFelelos((String) row.get("targyfelelos"))
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
