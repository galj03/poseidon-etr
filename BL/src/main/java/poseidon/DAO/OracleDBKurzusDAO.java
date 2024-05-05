package poseidon.DAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import poseidon.Constants;
import poseidon.DAO._Interfaces.IKurzusDAO;
import poseidon.DTO.Kurzus;
import poseidon.DTO.KurzusData;
import poseidon.DTO.Tantargy;
import poseidon.DTO.TantargyData;
import poseidon.DTO._Interfaces.IKurzus;
import poseidon.DTO._Interfaces.IKurzusData;
import poseidon.DTO._Interfaces.ITantargyData;
import poseidon.DTO.User;
import poseidon.DTO._Interfaces.IUser;
import poseidon.Exceptions.ArgumentNullException;
import poseidon.Exceptions.QueryException;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.security.Principal;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.*;

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

    public List<IKurzus> getKurzusokByTantargyId(Integer tantargyId) throws QueryException {
        return getRows("select * from kurzus where tantargy_id=?", tantargyId);
    }

    public Integer getTantargyIdByKurzusId(Integer kurzusId) throws QueryException {
        try {
            List<Map<String, Object>> rows = getJdbcTemplate().queryForList(
                    "SELECT tantargy.id AS tantargy_id " +
                            "FROM tantargy, kurzus " +
                            "WHERE tantargy.id = kurzus.tantargy_id " +
                            "AND tantargy.id = ?", kurzusId);



            return ((BigDecimal) rows.get(0).get("tantargy_id")).intValue();

        } catch (DataAccessException exception) {
            throw new QueryException("Could not get values from database", exception);
        } catch (QueryException exception) {
            throw new QueryException("Failed to query a nested value", exception);
        }
    }

    public Set<Integer> getAllPrerequisities(Integer tantargyId) {
        try {
            List<Map<String, Object>> rows = getJdbcTemplate().queryForList(
                    "SELECT feltetel_id AS elofeltetel " +
                        "FROM elofeltetel " +
                        "WHERE elofeltetel.tantargy_id = ?", tantargyId);

            Set<Integer> prerequsiteSubjects = new HashSet<>();

            for (Map<String, Object> row : rows) {
                prerequsiteSubjects.add(((BigDecimal) row.get("elofeltetel")).intValue());
            }

            return prerequsiteSubjects;

        } catch (DataAccessException exception) {
            throw new QueryException("Could not get values from database", exception);
        } catch (QueryException exception) {
            throw new QueryException("Failed to query a nested value", exception);
        }
    }

    public Set<Integer> getAllCompletedSubjectsByUser(String PsCode) {
        try {
            List<Map<String, Object>> rows = getJdbcTemplate().queryForList(
                    "SELECT tantargy_id AS teljesitett_targy " +
                        "FROM felvette " +
                        "WHERE felvette.PS_kod = '" + PsCode + "' " +
                        "AND allapot = 'TELJESITETT'");

            Set<Integer> completedSubjects = new HashSet<>();

            // debug
            if (!rows.isEmpty()) {
                Set<String> keys = rows.get(0).keySet();

                for (String key : keys) {
                    System.err.println(key);
                }
            }

            for (Map<String, Object> row : rows) {
                completedSubjects.add(((BigDecimal) row.get("teljesitett_targy")).intValue());
            }

            return completedSubjects;

        } catch (DataAccessException exception) {
            throw new QueryException("Could not get values from database", exception);
        } catch (QueryException exception) {
            throw new QueryException("Failed to query a nested value", exception);
        }
    }

    public List<Integer> checkPrerequisitesCompleted(Set<Integer> prerequisiteSubjects, Set<Integer> completedSubjects) {
        List<Integer> notCompletedSubjects = new ArrayList<>();

        for (Integer pSubject : prerequisiteSubjects) {
            if (!completedSubjects.contains(pSubject)) {
                notCompletedSubjects.add(pSubject);
            }
        }

        return notCompletedSubjects;
    }

    public Integer getSumOfEnrolledStudents(Integer kurzusId) {
        try {
            List<Map<String, Object>> rows = getJdbcTemplate().queryForList("SELECT * FROM felvette WHERE kurzus_id = ? AND allapot = " + "'" + Constants.JOVAHAGYOTT + "'", kurzusId);

            return rows.size();

        } catch (DataAccessException exception) {
            throw new QueryException("Could not get values from database", exception);
        } catch (QueryException exception) {
            throw new QueryException("Failed to query a nested value", exception);
        }
    }

    public List<IKurzusData> getAllCoursesOfSubject(Integer tantargyId, String PsCode) {
        try {
            List<Map<String, Object>> rows = getJdbcTemplate().queryForList("" +
                    "SELECT " +
                    "kurzus.id, " +
                    "nev, " +
                    "oktato_ps_kod, " +
                    "kezdes_ideje_nap, " +
                    "kezdes_ideje_idopont, " +
                    "felveheto, " +
                    "vizsga, " +
                    "ferohely, " +
                    "(SELECT nev FROM felhasznalo WHERE PS_kod = oktato_ps_kod) AS OKTATO_NEV, " +
                    "(SELECT COUNT(*) FROM felvette WHERE kurzus_id=kurzus.id AND PS_kod='" + PsCode + "') AS felvette,\n" +
                    "(SELECT COUNT(*) FROM felvette WHERE kurzus_id=kurzus.id AND PS_kod='" + PsCode + "' AND allapot='TELJESITETT') AS teljesitette\n" +
                    "FROM kurzus, terem\n" +
                    "WHERE kurzus.terem_id = terem.id\n" +
                    "AND kurzus.tantargy_id = ?", tantargyId);

            List<IKurzusData> result = new ArrayList<>();

            // debug
//            if (!rows.isEmpty()) {
//                Set<String> keys = rows.get(0).keySet();
//
//                for (String key : keys) {
//                    System.err.println(key);
//                }
//                System.err.println("-------------------");
//            }

            for (Map<String, Object> row : rows) {
                IKurzusData kurzusData = new KurzusData();
                kurzusData
                        .setKurzusId(((BigDecimal) row.get("id")).intValue())
                        .setNev((String) row.get("nev"))
                        .setOktatoPsKod((String) row.get("oktato_ps_kod"))
                        .setOktatoNeve((String) row.get("oktato_nev"))
                        .setNap((String) row.get("kezdes_ideje_nap"))
                        .setKezdesIdeje(((BigDecimal) row.get("kezdes_ideje_idopont")).intValue())
                        .setFelveheto("I".equals(row.get("felveheto")))
                        .setVizsga("I".equals(row.get("vizsga")))
                        .setFerohely(((BigDecimal) row.get("ferohely")).intValue())
                        .setAktualisLetszam(getSumOfEnrolledStudents(kurzusData.getKurzusId()))
                        .setFelvette(((BigDecimal) row.get("felvette")).intValue() > 0)
                        .setTeljesitette(((BigDecimal) row.get("teljesitette")).intValue() > 0);
                result.add(kurzusData);
            }

            return result;

        } catch (DataAccessException exception) {
            throw new QueryException("Could not get values from database", exception);
        } catch (QueryException exception) {
            throw new QueryException("Failed to query a nested value", exception);
        }
    }

    public void enrollCourse(Integer kurzusId, String PsCode) {
        try {
            String sql = "INSERT INTO felvette VALUES (?, ?, ?, " + "'" + Constants.JOVAHAGYASRA_VAR + "'" + ", NULL)";
            getJdbcTemplate().update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setString(1, PsCode);
                ps.setString(2, String.valueOf(kurzusId));
                ps.setString(3, String.valueOf(getById(kurzusId).getTantargyId()));
                return ps;
            });
        } catch (DataAccessException exception) {
            throw new QueryException("Could not insert value into database", exception);
        }
    }

    public void removeFromCourse(String PsCode, Integer kurzusId) {
        String sql = "DELETE FROM felvette WHERE kurzus_id=? AND PS_kod=?";
        getJdbcTemplate().update(sql, kurzusId, PsCode);
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

    @Override
    public void saveGrade(String psCode, Integer tantargyId, Integer grade) {
        try {
            String sql = "UPDATE felvette SET jegy = ?, allapot = ? WHERE ps_kod = ? AND tantargy_id = ?";
            getJdbcTemplate().update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql);
                if (grade == null) {
                    ps.setNull(1, Types.NUMERIC);
                } else {
                    ps.setInt(1, grade);
                }
                ps.setString(2, grade == null ? Constants.JOVAHAGYOTT : Constants.TELJESITETT);
                ps.setString(3, psCode);
                ps.setInt(4, tantargyId);
                return ps;
            });
        } catch (DataAccessException exception) {
            throw new QueryException("Could not insert value into database", exception);
        }
    }

    @Override
    public List<Map<IKurzus, Map<IUser, Integer>>> getTeachingCourses(String teacher_ps_kod) {
        String sql = "SELECT kurzus.nev, ps_kod, felvette.tantargy_id, kurzus_id, jegy FROM kurzus " +
                "INNER JOIN felvette ON kurzus.id = felvette.kurzus_id " +
                "WHERE kurzus.oktato_ps_kod =?";
        var queryResultKurzusok = super.getCustomRows(sql, teacher_ps_kod);

        if (queryResultKurzusok == null) {
            return null;
        }

        List<Map<IKurzus, Map<IUser, Integer>>> hallgatokJegyeKurzusonkentList = new ArrayList<>();
        Map<IKurzus, Map<IUser, Integer>> hallgatokJegyeKurzusonkent = new HashMap<>();
        Map<IUser, Integer> hallgatokJegyei = new HashMap<>();
        IKurzus lastKurzus = null;
        IKurzus tmpKurzus = null;
        for (var item : queryResultKurzusok) {
            tmpKurzus = new Kurzus()
                    .setKurzusId(((BigDecimal) item.get("kurzus_id")).intValue())
                    .setNev((String) item.get("nev"))
                    .setTantargyId(((BigDecimal) item.get("tantargy_id")).intValue());

            if (lastKurzus == null) {
                lastKurzus = tmpKurzus;
            }

            IUser tmpUser = new User()
                    .setPsCode((String) item.get("ps_kod"));

            if (lastKurzus.getKurzusId() != tmpKurzus.getKurzusId()) {
                hallgatokJegyeKurzusonkent.put(lastKurzus, new HashMap<>(hallgatokJegyei));
                hallgatokJegyeKurzusonkentList.add(new HashMap<>(hallgatokJegyeKurzusonkent));
                lastKurzus = tmpKurzus;
                hallgatokJegyei.clear();
                hallgatokJegyeKurzusonkent.clear();
                hallgatokJegyei.put(tmpUser, item.get("jegy") == null ? 0 : ((BigDecimal) item.get("jegy")).intValue());
            } else {
                hallgatokJegyei.put(tmpUser, item.get("jegy") == null ? 0 : ((BigDecimal) item.get("jegy")).intValue());
            }
        }
        if (lastKurzus.getKurzusId() == tmpKurzus.getKurzusId()) {
            hallgatokJegyeKurzusonkent.put(lastKurzus, new HashMap<>(hallgatokJegyei));
            hallgatokJegyeKurzusonkentList.add(new HashMap<>(hallgatokJegyeKurzusonkent));
        }
        return hallgatokJegyeKurzusonkentList;
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

    private List<IKurzus> getRows(String sql, Object... args) throws QueryException {
        try {
            List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql, args);

            List<IKurzus> result = new ArrayList<>();

            for (Map<String, Object> row : rows) {

                // debug
//                if (!rows.isEmpty()) {
//                    Set<String> keys = rows.get(0).keySet();
//
//                    for (String key : keys) {
//                        System.err.println(key);
//                    }
//                }

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
