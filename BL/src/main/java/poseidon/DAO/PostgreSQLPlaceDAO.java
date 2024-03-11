package poseidon.DAO;

import poseidon.Exceptions.ArgumentNullException;
import poseidon.Exceptions.QueryException;
import poseidon.DAO._Interfaces.IPlaceDAO;
import poseidon.DTO.Place;
import poseidon.DTO._Interfaces.IPlace;
import poseidon.DTO._Interfaces.IWorld;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Data access object for the world model in a PostgreSQL database.
 */
@Repository
public class PostgreSQLPlaceDAO extends JdbcDaoSupport implements IPlaceDAO {
    //region Properties
    private final DataSource _dataSource;
    //endregion

    //region Constructor
    @Autowired
    public PostgreSQLPlaceDAO(DataSource dataSource) {
        _dataSource = dataSource;
        setDataSource(_dataSource);
    }
    //endregion

    //region Public members
    @Override
    public IPlace getById(int id, boolean queryParents) throws QueryException {
        return getRow(queryParents, "SELECT * FROM place WHERE id=?", id);
    }

    @Override
    public IPlace getById(Integer id) throws QueryException {
        if (id == null) return null;
        return getById(id, true);
    }

    @Override
    public IPlace getRootByWorld(IWorld world) throws IllegalArgumentException, QueryException {
        if (world == null) throw new ArgumentNullException("world");
        if (world.getId() == null) throw new IllegalArgumentException("World must be saved first.");

        return getRow(false, "SELECT * FROM place WHERE parent_id IS NULL AND world_id=?", world.getId());
    }

    @Override
    public List<IPlace> getAllByWorld(IWorld world, boolean publicOnly) throws IllegalArgumentException, QueryException {
        if (world == null) throw new ArgumentNullException("world");
        if (world.getId() == null) throw new IllegalArgumentException("World must be saved first.");

        String sql = "SELECT * FROM place WHERE world_id=?";
        if (publicOnly) sql += " AND discovered=TRUE";

        return getRows(null, sql, world.getId());
    }

    @Override
    public List<IPlace> getAllByParent(IPlace parent, boolean publicOnly) throws IllegalArgumentException, QueryException {
        if (parent == null) throw new ArgumentNullException("parent");
        if (parent.getId() == null) throw new IllegalArgumentException("Parent must be saved first.");

        String sql = "SELECT * FROM place WHERE parent_id=?";
        if (publicOnly) sql += " AND discovered=TRUE";

        return getRows(parent, sql, parent.getId());
    }

    @Override
    public IPlace save(IPlace place) throws QueryException {
        if (place.getId() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();

            IPlace parent = place.getParent();

            try {
                String sql = "INSERT INTO place(name, notes, discovered, type, show_description, description, map, " +
                        "world_id, parent_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                getJdbcTemplate().update(connection -> {
                            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                            ps.setString(1, place.getName());
                            ps.setString(2, place.getNotes());
                            ps.setBoolean(3, place.isDiscovered());
                            ps.setString(4, place.getType());
                            ps.setBoolean(5, place.isDescriptionShown());
                            ps.setString(6, place.getDescription());
                            ps.setString(7, place.getMapPath());
                            ps.setInt(8, place.getWorldId());
                            if (parent == null) ps.setNull(9, Types.INTEGER);
                            else ps.setInt(9, parent.getId());
                            return ps;
                        },
                        keyHolder);
            } catch (DataAccessException exception) {
                throw new QueryException("Could not insert value into database", exception);
            }

            Number key = keyHolder.getKey();
            if (key == null) throw new QueryException("Failed to get inserted record's id");

            return getById(key.intValue());
        }

        try {
            String sql = "UPDATE place SET name=?, notes=?, discovered=?, type=?, show_description=?, description=?, " +
                    "map=?, world_id=?, parent_id=? WHERE id=?";
            IPlace parent = place.getParent();
            getJdbcTemplate().update(connection -> {
                        PreparedStatement ps = connection.prepareStatement(sql);
                        ps.setString(1, place.getName());
                        ps.setString(2, place.getNotes());
                        ps.setBoolean(3, place.isDiscovered());
                        ps.setString(4, place.getType());
                        ps.setBoolean(5, place.isDescriptionShown());
                        ps.setString(6, place.getDescription());
                        ps.setString(7, place.getMapPath());
                        ps.setInt(8, place.getWorldId());
                        //ps.setInt(9, place.getParent().getId());
                        if (parent == null) ps.setNull(9, Types.INTEGER);
                        else ps.setInt(9, parent.getId());
                        ps.setInt(10, place.getId());
                        return ps;
                    });
        } catch (DataAccessException exception) {
            throw new QueryException("Could not update record in database", exception);
        }

        return getById(place.getId());
    }

    @Override
    public void remove(IPlace place) throws ArgumentNullException, QueryException {
        if (place == null) throw new ArgumentNullException("place");
        if (place.getId() == null) throw new ArgumentNullException("Place must be saved first.");

        String sql = "DELETE FROM place WHERE id=?";
        getJdbcTemplate().update(sql, place.getId());
    }
    //endregion

    //region Private members
    private List<IPlace> getRows(IPlace parent, String sql, Object... args) throws QueryException {
        try {
            List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql, args);
            List<IPlace> result = new ArrayList<>();

            if (rows.isEmpty()) return result;

            for (Map<String,Object> row: rows) {
                result.add(new Place(this)
                        .setId((Integer) row.get("id"))
                        .setWorldId((Integer) row.get("world_id"))
                        .setParent((parent != null) ? parent : getRow(false,
                                                                "SELECT * FROM place WHERE id=?",
                                                                row.get("parent_id")))
                        .setDiscovered((boolean) row.get("discovered"))
                        .setName((String) row.get("name"))
                        .setType((String) row.get("type"))
                        .setNotes((String) row.get("notes"))
                        .setDescription((String) row.get("description"))
                        .setDescriptionShown((boolean) row.get("show_description"))
                        .setMapPath((String) row.get("map"))
                );
            }

            return result;

        } catch (DataAccessException exception) {
            throw new QueryException("Could not get values from database", exception);
        } catch (QueryException exception) {
            throw new QueryException("Failed to query a nested value", exception);
        }
    }

    private IPlace getRow(boolean getParents, String sql, Object... args) throws QueryException {
        try {
            List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql, args);

            if (rows.isEmpty()) return null;

            return new Place(this)
                    .setId((Integer) rows.get(0).get("id"))
                    .setWorldId((Integer)  rows.get(0).get("world_id"))
                    .setParent(getParents ? getRow(false, "SELECT * FROM place WHERE id=?",
                            rows.get(0).get("parent_id")) : null)
                    .setDiscovered((boolean) rows.get(0).get("discovered"))
                    .setName((String) rows.get(0).get("name"))
                    .setType((String) rows.get(0).get("type"))
                    .setNotes((String) rows.get(0).get("notes"))
                    .setDescription((String) rows.get(0).get("description"))
                    .setDescriptionShown((boolean) rows.get(0).get("show_description"))
                    .setMapPath((String) rows.get(0).get("map"));

        } catch (DataAccessException exception) {
            throw new QueryException("Could not get values from database", exception);
        } catch (QueryException exception) {
            throw new QueryException("Failed to query a nested value", exception);
        }
    }
    //endregion
}
