package poseidon.DAO;

import poseidon.DAO._Interfaces.IPlaceDAO;
import poseidon.Exceptions.ArgumentNullException;
import poseidon.Exceptions.QueryException;
import poseidon.DAO._Interfaces.IUserDAO;
import poseidon.DAO._Interfaces.IWorldDAO;
import poseidon.DTO.World;
import poseidon.DTO._Interfaces.IUser;
import poseidon.DTO._Interfaces.IWorld;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Data access object for the world model in a PostgreSQL database.
 */
@Repository
public class PostgreSQLWorldDAO extends JdbcDaoSupport implements IWorldDAO {
    //region Properties
    private final DataSource _dataSource;
    private final IUserDAO _userDAO;
    private final IPlaceDAO _placeDAO;
    //endregion

    //region Constructor
    @Autowired
    public PostgreSQLWorldDAO(DataSource dataSource, IUserDAO userDAO, IPlaceDAO placeDAO) {
        _dataSource = dataSource;
        setDataSource(_dataSource);

        _userDAO = userDAO;
        _placeDAO = placeDAO;
    }
    //endregion

    //region Public members
    @Override
    public IWorld getById(int id) throws QueryException {
        List<IWorld> rows = getRows("SELECT * FROM world WHERE id=?", id);
        return rows.isEmpty() ? null : rows.get(0);
    }

    @Override
    public List<IWorld> getAllByOwner(IUser owner) throws IllegalArgumentException, QueryException {
        if (owner == null) throw new ArgumentNullException("owner");
        if (owner.getId() == null) throw new IllegalArgumentException("Owner must be saved first.");

        return getRows("SELECT * FROM world WHERE owner_id=?", owner.getId());
    }

    @Override
    public List<IWorld> getAllByJoined(IUser joinedUser) throws IllegalArgumentException, QueryException {
        if (joinedUser == null) throw new ArgumentNullException("joinedUser");
        if (joinedUser.getId() == null) throw new IllegalArgumentException("User must be saved first.");

        return getRows("SELECT world.* FROM world, player " +
                "WHERE world.id=player.world_id AND player.user_id=? AND player.accepted_invite=TRUE",
                joinedUser.getId());
    }

    @Override
    public List<IWorld> getAllByInvited(IUser invitedUser) throws IllegalArgumentException, QueryException {
        if (invitedUser == null) throw new ArgumentNullException("invitedUser");
        if (invitedUser.getId() == null) throw new IllegalArgumentException("User must be saved first.");

        return getRows("SELECT world.* FROM world, player " +
                        "WHERE world.id=player.world_id AND player.user_id=? AND player.accepted_invite=FALSE",
                invitedUser.getId());
    }

    @Override
    public void inviteUser(IWorld world, IUser user) throws IllegalArgumentException, QueryException {
        if (user == null) throw new ArgumentNullException("user");
        if (world == null) throw new ArgumentNullException("world");
        if (user.getId() == null) throw new ArgumentNullException("User must be saved first.");
        if (world.getId() == null) throw new ArgumentNullException("World must be saved first.");
        if (world.getInvitedUsers().contains(user)) throw new ArgumentNullException("User has been already invited to given world");

        String sql = "INSERT INTO player(user_id, world_id) VALUES (?, ?)";
        getJdbcTemplate().update(sql, user.getId(), world.getId());
    }

    @Override
    public void removeUser(IWorld world, IUser user) throws IllegalArgumentException, QueryException {
        if (user == null) throw new ArgumentNullException("user");
        if (world == null) throw new ArgumentNullException("world");
        if (user.getId() == null) throw new ArgumentNullException("User must be saved first.");
        if (world.getId() == null) throw new ArgumentNullException("World must be saved first.");
        if (Objects.equals(world.getOwner().getId(), user.getId())) throw new IllegalArgumentException("User cannot be removed since they're the owner of the world.");

        String sql = "DELETE FROM player WHERE user_id=? AND world_id=?";
        getJdbcTemplate().update(sql, user.getId(), world.getId());
    }

    @Override
    public IWorld save(IWorld world) throws QueryException {
        if (world.getId() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();

            try {
                String sql = "INSERT INTO world(name, description, owner_id) VALUES (?, ?, ?)";
                getJdbcTemplate().update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                    ps.setString(1, world.getName());
                    ps.setString(2, world.getDescription());
                    ps.setInt(3, world.getOwner().getId());
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
            String sql = "UPDATE world SET name=?, description=?, owner_id=? WHERE id=?";
            getJdbcTemplate().update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setString(1, world.getName());
                ps.setString(2, world.getDescription());
                ps.setInt(3, world.getOwner().getId());
                ps.setInt(4, world.getId());
                return ps;
            });
        } catch (DataAccessException exception) {
            throw new QueryException("Could not update record in database", exception);
        }

        return getById(world.getId());
    }

    @Override
    public void remove(IWorld world) throws IllegalArgumentException, QueryException {
        if (world == null) throw new ArgumentNullException("world");
        if (world.getId() == null) throw new ArgumentNullException("World must be saved first.");

        String sql = "DELETE FROM world WHERE id=?";
        getJdbcTemplate().update(sql, world.getId());
    }
    //endregion

    //region Private members
    private List<IWorld> getRows(String sql, Object... args) throws QueryException {
        try {
            List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql, args);

            List<IWorld> result = new ArrayList<>();

            for (Map<String,Object> row: rows) {
                result.add(new World(_userDAO, _placeDAO)
                                .setId((Integer) row.get("id"))
                                .setName((String) row.get("name"))
                                .setDescription((String) row.get("description"))
                                .setOwner(_userDAO.getById((int) row.get("owner_id")))
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
