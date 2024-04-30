package poseidon.DAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import poseidon.DAO._Interfaces.ISzakDAO;
import poseidon.DAO._Interfaces.IUserDAO;
import poseidon.DTO.Kurzus;
import poseidon.DTO.Szak;
import poseidon.DTO.Tantargy;
import poseidon.DTO.User;
import poseidon.DTO._Interfaces.ISzak;
import poseidon.DTO._Interfaces.ITantargy;
import poseidon.DTO._Interfaces.IUser;
import poseidon.Exceptions.ArgumentNullException;
import poseidon.Exceptions.QueryException;
import poseidon.UserRoles;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.util.*;

import static poseidon.Constants.TELJESITETT;

@Repository
public class OracleDBSzakDAO extends BaseDAO implements ISzakDAO {
    private final IUserDAO _userDAO;

    //region Constructor
    @Autowired
    public OracleDBSzakDAO(DataSource dataSource, IUserDAO userDAO) {
        super(dataSource);
        _userDAO = userDAO;
    }
    //endregion

    @Override
    public Iterable<ISzak> getAll() throws QueryException {
        return getRows("select * from szak");
    }

    @Override
    public ISzak getById(Integer id) throws QueryException {
        return getRow("select * from szak where id=?", id);
    }

    @Override
    public ISzak save(ISzak szak) throws QueryException {
        if (szak.getSzakId() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();

            try {
                String sql = "INSERT INTO szak(nev) VALUES (?)";
                getJdbcTemplate().update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                    ps.setString(1, szak.getName());
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
            String sql = "UPDATE szak SET nev=? WHERE id=?";
            getJdbcTemplate().update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setString(1, szak.getName());
                ps.setString(2, szak.getSzakId().toString());
                return ps;
            });
        } catch (DataAccessException exception) {
            throw new QueryException("Could not insert value into database", exception);
        }

        return getById(szak.getSzakId());
    }

    @Override
    public void remove(ISzak szak) throws IllegalArgumentException, QueryException, DataIntegrityViolationException {
        if (szak == null) throw new ArgumentNullException("szak");
        if (szak.getSzakId() == null) throw new ArgumentNullException("Szak must be saved first.");

        String sql = "DELETE FROM szak WHERE id=?";
        getJdbcTemplate().update(sql, szak.getSzakId());
    }

    @Override
    public Integer getRequiredClassesCount(ISzak szak) throws QueryException {
        String sql = "select count(*) as num, kotelezo.szak_id from kotelezo, tantargy " +
                "where kotelezo.tantargy_id=tantargy.id " +
                "group by kotelezo.szak_id having kotelezo.szak_id=?";

        var requiredSubjectsData = super.getCustomRows(sql, szak.getSzakId());
        if (requiredSubjectsData == null) {
            return 0;
        }

        return ((BigDecimal) requiredSubjectsData.get(0).get("num")).intValue();
    }

    @Override
    public Map<String, Float> getAveragesForAll(ISzak szak) throws QueryException {
        String sql = "select * from get_students_for_szak (?)";
        var userData = super.getCustomRows(sql, szak.getSzakId());
        if (userData == null) {
            return new HashMap<>();
        }

        sql = "select avg(jegy) as avg_jegy, felhasznalo.PS_kod as PS_kod from felhasznalo, felvette " +
                String.format("where felhasznalo.szak_id=? and felvette.PS_kod=felhasznalo.PS_kod and allapot='%s' ", TELJESITETT)
                + "group by felhasznalo.PS_kod";
        var avgData = super.getCustomRows(sql, szak.getSzakId());
        if (avgData == null) {
            return new HashMap<>();
        }

        var results = new HashMap<String, Float>(userData.size());
        for (var user : userData) {
            results.put((String) user.get("PS_kod"), 0f);
        }

        for (var avgRecord : avgData) {
            var avg = ((BigDecimal) avgRecord.get("avg_jegy")).floatValue();

            results.put((String) avgRecord.get("PS_kod"), avg);
        }

        return results;
    }

    //region Private members
    private ISzak getRow(String sql, Object... args) throws QueryException {
        try {
            List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql, args);

            if (rows.isEmpty()) return null;

            return new Szak()
                    .setSzakId(((BigDecimal) rows.get(0).get("id")).intValue())
                    .setName((String) rows.get(0).get("nev"));

        } catch (DataAccessException exception) {
            throw new QueryException("Could not get values from database", exception);
        }
    }

    private List<ISzak> getRows(String sql, Object... args) throws QueryException {
        try {
            List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql, args);

            List<ISzak> result = new ArrayList<>();

            for (Map<String, Object> row : rows) {
                result.add(
                        new Szak()
                                .setSzakId(((BigDecimal) row.get("id")).intValue())
                                .setName((String) row.get("nev"))
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
