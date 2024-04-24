package poseidon.DAO;

import poseidon.Constants;
import poseidon.DTO.Kurzus;
import poseidon.DTO._Interfaces.IKurzus;
import poseidon.Exceptions.ArgumentNullException;
import poseidon.Exceptions.QueryException;
import poseidon.DAO._Interfaces.IUserDAO;
import poseidon.DTO.User;
import poseidon.DTO._Interfaces.IUser;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import poseidon.UserRoles;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static poseidon.Constants.JOVAHAGYOTT;

/**
 * Data access object for the user model in an Oracle database.
 */
@Repository
public class OracleDBUserDAO extends BaseDAO implements IUserDAO {
    //region Constructor
    @Autowired
    public OracleDBUserDAO(DataSource dataSource) {
        super(dataSource);
    }
    //endregion

    //region Public members
    @Override
    public Iterable<IUser> getAllUsers() throws QueryException {
        return getRows("SELECT * FROM felhasznalo");
    }

    @Override
    public IUser getByPsCode(String id) throws QueryException {
        return getRow("SELECT * FROM felhasznalo WHERE PS_kod=?", id);
    }

    @Override
    public IUser getByEmail(String searchText) throws QueryException {
        return getRow("SELECT * FROM felhasznalo WHERE email=?", searchText);
    }

    @Override
    public IUser save(IUser user) throws QueryException {
        if (getByPsCode(user.getPsCode()) == null) {
            try {
                String sql = "INSERT INTO felhasznalo(PS_kod, nev, email, jelszo, szak_id, jogosultsag, kezdes_eve, vegzes_ideje) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                getJdbcTemplate().update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql);
                    ps.setString(1, user.getPsCode());
                    ps.setString(2, user.getName());
                    ps.setString(3, user.getEmail());
                    ps.setString(4, user.getPassword());
                    ps.setString(5, user.getSzakId().toString());
                    ps.setString(6, user.getRole().toString());
                    ps.setString(7, user.getKezdesEve().toString());
                    ps.setString(8, user.getVegzesEve().toString());
                    return ps;
                });
            } catch (DataAccessException exception) {
                throw new QueryException("Could not insert value into database", exception);
            }

            return getByPsCode(user.getPsCode());
        }

        try {
            String sql = "UPDATE felhasznalo SET nev=?, email=?, jelszo=?, szak_id=?, jogosultsag=?, kezdes_eve=?, vegzes_ideje=? WHERE PS_kod=?";
            getJdbcTemplate().update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setString(1, user.getName());
                ps.setString(2, user.getEmail());
                ps.setString(3, user.getPassword());
                ps.setString(4, user.getSzakId().toString());
                ps.setString(5, user.getRole().toString());
                ps.setString(6, user.getKezdesEve().toString());
                ps.setString(7, user.getVegzesEve().toString());
                ps.setString(8, user.getPsCode());
                return ps;
            });
        } catch (DataAccessException exception) {
            throw new QueryException("Could not insert value into database", exception);
        }

        return getByPsCode(user.getPsCode());
    }

    @Override
    public void remove(IUser user) throws IllegalArgumentException, QueryException, SQLException {
        if (user == null) throw new ArgumentNullException("user");
        if (user.getPsCode() == null) throw new ArgumentNullException("User must be saved first.");

        String sql = "DELETE FROM felhasznalo WHERE PS_kod=?";
        getJdbcTemplate().update(sql, user.getPsCode());
    }

    public List<IKurzus> currentCourses(IUser user) throws QueryException {
        String sql = "select * from kurzus, felvette " +
                String.format("where felvette.kurzus_id=kurzus.id and felvette.PS_kod=? and allapot='%s'", JOVAHAGYOTT);
        var courseData = super.getCustomRows(sql, user.getPsCode());
        if (courseData == null) {
            return new ArrayList<>();
        }

        var results = new ArrayList<IKurzus>(courseData.size());
        for (var course : courseData) {
            Boolean isFelveheto = course.get("felveheto").equals(Constants.TRUE);
            Boolean isVizsga = course.get("vizsga").equals(Constants.TRUE);

            var courseObj = new Kurzus()
                    .setKurzusId(((BigDecimal) course.get("id")).intValue())
                    .setNev((String) course.get("nev"))
                    .setOktato((String) course.get("oktato_PS_kod"))
                    .setKezdesNapja((String) course.get("kezdes_ideje_nap"))
                    .setKezdesIdopontja(((BigDecimal) course.get("kezdes_ideje_idopont")).intValue())
                    .setTantargyId(((BigDecimal) course.get("tantargy_id")).intValue())
                    .setTeremId(((BigDecimal) course.get("terem_id")).intValue())
                    .setIsFelveheto(isFelveheto)
                    .setIsVizsga(isVizsga);

            results.add(courseObj);
        }

        return results;
    }
    //endregion

    //region Private members
    private IUser getRow(String sql, Object... args) throws QueryException {
        try {
            List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql, args);

            if (rows.isEmpty()) return null;

            UserRoles role = Objects.equals(rows.get(0).get("jogosultsag").toString(), "ROLE_USER") ? UserRoles.ROLE_USER : UserRoles.ROLE_ADMIN;

            return new User()
                    .setPsCode((String) rows.get(0).get("PS_kod"))
                    .setName((String) rows.get(0).get("nev"))
                    .setEmail((String) rows.get(0).get("email"))
                    .setPassword((String) rows.get(0).get("jelszo"))
                    .setSzakId(((BigDecimal) rows.get(0).get("szak_id")).intValue())
                    .setRole(role)
                    .setKezdesEve(((BigDecimal) rows.get(0).get("kezdes_eve")).intValue())
                    .setVegzesEve(((BigDecimal) rows.get(0).get("vegzes_ideje")).intValue());

        } catch (DataAccessException exception) {
            throw new QueryException("Could not get values from database", exception);
        }
    }

    private List<IUser> getRows(String sql, Object... args) throws QueryException {
        try {
            List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql, args);

            List<IUser> result = new ArrayList<>();

            for (Map<String, Object> row : rows) {
                UserRoles role = Objects.equals(row.get("jogosultsag"), "ROLE_USER") ? UserRoles.ROLE_USER : UserRoles.ROLE_ADMIN;

                result.add(new User()
                        .setPsCode((String) row.get("PS_kod"))
                        .setName((String) row.get("nev"))
                        .setEmail((String) row.get("email"))
                        .setPassword((String) row.get("jelszo"))
                        .setSzakId(((BigDecimal) row.get("szak_id")).intValue())
                        .setRole(role)
                        .setKezdesEve(((BigDecimal) row.get("kezdes_eve")).intValue())
                        .setVegzesEve(((BigDecimal) row.get("vegzes_ideje")).intValue())
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
