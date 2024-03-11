package poseidon.DAO;

import poseidon.DTO._Interfaces.IWorld;
import poseidon.Exceptions.ArgumentNullException;
import poseidon.Exceptions.QueryException;
import poseidon.DAO._Interfaces.IUserDAO;
import poseidon.DTO.User;
import poseidon.DTO._Interfaces.IUser;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Data access object for the user model in a PostgreSQL database.
 */
@Repository
public class PostgreSQLUserDAO extends JdbcDaoSupport implements IUserDAO {
    //region Properties
    private final DataSource _dataSource;
    //endregion

    //region Constructor
    @Autowired
    public PostgreSQLUserDAO(DataSource dataSource) {
        _dataSource = dataSource;
        setDataSource(_dataSource);
    }
    //endregion

    //region Public members
    @Override
    public IUser getById(int id) throws QueryException {
        return getRow("SELECT * FROM \"user\" WHERE id=?", id);
    }

    @Override
    public IUser getBySearchText(String searchText) throws QueryException {
        return getRow("SELECT * FROM \"user\" WHERE username=? or email=?", searchText, searchText);
    }

    @Override
    public List<IUser> getByInvitedWorld(IWorld world) throws IllegalArgumentException, QueryException {
        if (world == null) throw new ArgumentNullException("world");
        if (world.getId() == null) throw new ArgumentNullException("World must be saved first.");

        return getRows("SELECT \"user\".* FROM \"user\", player " +
                        "WHERE player.user_id=\"user\".id AND player.accepted_invite=FALSE AND player.world_id=?",
                world.getId());
    }

    @Override
    public List<IUser> getByWorldJoined(IWorld world) throws IllegalArgumentException, QueryException {
        if (world == null) throw new ArgumentNullException("world");
        if (world.getId() == null) throw new ArgumentNullException("World must be saved first.");

        return getRows("SELECT \"user\".* FROM \"user\", player " +
                        "WHERE player.user_id=\"user\".id AND player.accepted_invite=TRUE AND player.world_id=?",
                world.getId());
    }

    @Override
    public void acceptInvite(IUser user, IWorld world) throws IllegalArgumentException, QueryException {
        if (user == null) throw new ArgumentNullException("user");
        if (world == null) throw new ArgumentNullException("world");
        if (user.getId() == null) throw new ArgumentNullException("User must be saved first.");
        if (world.getId() == null) throw new ArgumentNullException("World must be saved first.");


        try {
            String sql = "UPDATE player SET accepted_invite=TRUE WHERE user_id=? AND world_id=?";
            getJdbcTemplate().update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setInt(1, user.getId());
                ps.setInt(2, world.getId());
                return ps;
            });
        } catch (DataAccessException exception) {
            throw new QueryException("Could not update record in database", exception);
        }
    }

    @Override
    public void declineInvite(IUser user, IWorld world) throws IllegalArgumentException, QueryException {
        if (user == null) throw new ArgumentNullException("user");
        if (world == null) throw new ArgumentNullException("world");
        if (user.getId() == null) throw new ArgumentNullException("User must be saved first.");
        if (world.getId() == null) throw new ArgumentNullException("World must be saved first.");
        if (world.getInvitedUsers().contains(user)) throw new ArgumentNullException("User has already accepted the invited to the given world");

        String sql = "DELETE FROM player WHERE user_id=? AND world_id=? AND accepted_invite=FALSE";
        getJdbcTemplate().update(sql, user.getId(), world.getId());
    }

    @Override
    public IUser save(IUser user) throws QueryException {
        if (user.getId() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();

            try {
                String sql = "INSERT INTO \"user\"(username, email, password) VALUES (?, ?, ?)";
                getJdbcTemplate().update(connection -> {
                    PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                    ps.setString(1, user.getUsername());
                    ps.setString(2, user.getEmail());
                    ps.setString(3, user.getPassword());
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
            String sql = "UPDATE \"user\" SET username=?, email=?, password=?, pfp=? WHERE id=?";
            getJdbcTemplate().update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getEmail());
                ps.setString(3, user.getPassword());
                ps.setString(4, user.getPfpPath());
                ps.setInt(5, user.getId());
                return ps;
            });
        } catch (DataAccessException exception) {
            throw new QueryException("Could not insert value into database", exception);
        }

        return getById(user.getId());
    }

    @Override
    public void remove(IUser user) throws IllegalArgumentException, QueryException {
        if (user == null) throw new ArgumentNullException("user");
        if (user.getId() == null) throw new ArgumentNullException("User must be saved first.");

        String sql = "DELETE FROM \"user\" WHERE id=?";
        getJdbcTemplate().update(sql, user.getId());
    }
    //endregion

    //region Private members
    private IUser getRow(String sql, Object... args) throws QueryException {
        try {
            List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql, args);

            if (rows.isEmpty()) return null;

            return new User()
                    .setId((Integer) rows.get(0).get("id"))
                    .setUsername((String) rows.get(0).get("username"))
                    .setEmail((String) rows.get(0).get("email"))
                    .setPassword((String) rows.get(0).get("password"))
                    .setPfpPath((String) rows.get(0).get("pfp"));

        } catch (DataAccessException exception) {
            throw new QueryException("Could not get values from database", exception);
        }
    }

    private List<IUser> getRows(String sql, Object... args) throws QueryException {
        try {
            List<Map<String, Object>> rows = getJdbcTemplate().queryForList(sql, args);

            List<IUser> result = new ArrayList<>();

            for (Map<String,Object> row: rows) {
                result.add(new User()
                        .setId((Integer) row.get("id"))
                        .setUsername((String) row.get("username"))
                        .setEmail((String) row.get("email"))
                        .setPassword((String) row.get("password"))
                        .setPfpPath((String) row.get("pfp"))
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
