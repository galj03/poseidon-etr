package poseidon.DAO;

import poseidon.Exceptions.ArgumentNullException;
import poseidon.Exceptions.QueryException;
import poseidon.DAO._Interfaces.IAppearanceDAO;
import poseidon.DAO._Interfaces.IPlaceDAO;
import poseidon.DTO.Appearance;
import poseidon.DTO._Interfaces.IAppearance;
import poseidon.DTO._Interfaces.IPlace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Data access object for the appearance model in a PostgreSQL database.
 */
@Repository
public class PostgreSQLAppearanceDAO extends JdbcDaoSupport implements IAppearanceDAO {
    //region Properties
    private final DataSource _dataSource;
    private final IPlaceDAO _placeDAO;
    //endregion

    //region Constructor
    @Autowired
    public PostgreSQLAppearanceDAO(DataSource dataSource, IPlaceDAO placeDAO) {
        _dataSource = dataSource;
        setDataSource(_dataSource);

        _placeDAO = placeDAO;
    }

    //region Public members
    @Override
    public List<IAppearance> getAllByLocation(IPlace location, boolean publicOnly) throws IllegalArgumentException, QueryException {
        if (location == null) throw new ArgumentNullException("location");
        if (location.getId() == null) throw new IllegalArgumentException("Location must be saved first.");

        String sql;
        if (publicOnly) {
            sql = "SELECT appearance.* FROM appearance, place " +
                    "WHERE appearance.place_id=place.id AND location_id=? AND place.discovered=TRUE";
        } else {
            sql = "SELECT * FROM appearance WHERE location_id=?";
        }

        try {
            List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql, location.getId());
            if (rows.isEmpty()) return null;

            List<IAppearance> result = new ArrayList<>();

            for (Map<String,Object> row: rows) {
                result.add(new Appearance(_placeDAO)
                        .setPlace(_placeDAO.getById((int) row.get("place_id"), false))
                        .setLocation(_placeDAO.getById((int) row.get("location_id"), false))
                        .setZAxis((int) row.get("z_axis"))
                        .setCoordinates((String) row.get("coordinates"))
                );
            }

            return result;

        } catch (DataAccessException exception) {
            throw new QueryException("Could not get values from database", exception);
        } catch (QueryException exception) {
            throw new QueryException("Failed to query a nested value", exception);
        }
    }

    @Override
    public IAppearance save(IAppearance appearance) throws ArgumentNullException, QueryException {
        remove(appearance);

        try {
            String insertSql = "INSERT INTO appearance(place_id, location_id, coordinates, z_axis) VALUES (?, ?, ?, ?)";
            getJdbcTemplate().update(connection -> {
                        PreparedStatement ps = connection.prepareStatement(insertSql);
                        ps.setInt(1, appearance.getPlace().getId());
                        ps.setInt(2, appearance.getLocation().getId());
                        ps.setString(3, appearance.getCoordinates());
                        ps.setInt(4, appearance.getZAxis());
                        return ps;
                    });

            String selectSql = "SELECT * FROM appearance WHERE place_id=? AND location_id=?";
            List<Map<String, Object>> rows = getJdbcTemplate().queryForList(selectSql, appearance.getPlace().getId(), appearance.getLocation().getId());

            if (rows.isEmpty()) throw new QueryException("Could not get value from database");

            return new Appearance(_placeDAO)
                    .setPlace(_placeDAO.getById((Integer) rows.get(0).get("place_id")))
                    .setLocation(_placeDAO.getById((Integer) rows.get(0).get("location_id")))
                    .setCoordinates((String) rows.get(0).get("coordinates"))
                    .setZAxis((Integer) rows.get(0).get("z_axis"));

        } catch (DataAccessException exception) {
            throw new QueryException("Could not insert value into database", exception);
        }
    }

    @Override
    public void remove(IAppearance appearance) throws ArgumentNullException, QueryException {
        if (appearance == null) throw new ArgumentNullException("appearance");

        String sql = "DELETE FROM appearance WHERE location_id=? AND place_id=?";
        getJdbcTemplate().update(sql, appearance.getLocation().getId(), appearance.getPlace().getId());
    }
    //endregion
}
