package poseidon.DTO;

import poseidon.DAO._Interfaces.IPlaceDAO;
import poseidon.DAO._Interfaces.IUserDAO;
import poseidon.DTO._Interfaces.IPlace;
import poseidon.DTO._Interfaces.IUser;
import poseidon.DTO._Interfaces.IWorld;
import poseidon.Exceptions.ArgumentNullException;
import poseidon.Exceptions.DataAccessException;
import poseidon.Exceptions.IllegalOperationException;
import poseidon.Exceptions.QueryException;

import java.util.List;
import java.util.Objects;

/**
 * Data transfer object to represent the world model.
 */
public class World implements IWorld {
    //region Properties
    private final IUserDAO _userDAO;
    private final IPlaceDAO _placeDAO;
    private Integer _id;
    private String _name;
    private String _description;
    private Integer _ownerId;
    //endregion

    //region Constructors
    public World(IUserDAO userDAO, IPlaceDAO placeDAO) throws ArgumentNullException {
        if (userDAO == null) throw new ArgumentNullException("userDAO");
        if (placeDAO == null) throw new ArgumentNullException("placeDAO");
        _userDAO = userDAO;
        _placeDAO = placeDAO;
    }
    //endregion

    //region Getters
    @Override
    public Integer getId() {
        return _id;
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public String getDescription() {
        return _description;
    }

    @Override
    public IUser getOwner() throws DataAccessException {
        try {
            return _userDAO.getById(_ownerId);
        } catch (QueryException e) {
            throw new DataAccessException("Could not get owner by id: " + _ownerId, e);
        }
    }

    @Override
    public List<IUser> getInvitedUsers() throws DataAccessException {
        try {
            return _userDAO.getByInvitedWorld(this);
        } catch (QueryException e) {
            throw new DataAccessException("Could not get invited users.", e);
        } catch (IllegalArgumentException e) {
            throw new DataAccessException("World is not saved to the database: " + _name, e);
        }
    }

    @Override
    public List<IUser> getJoinedUsers() throws DataAccessException {
        try {
            return _userDAO.getByWorldJoined(this);
        } catch (QueryException e) {
            throw new DataAccessException("Could not get joined users.", e);
        } catch (IllegalArgumentException e) {
            throw new DataAccessException("World is not saved to the database: " + _name, e);
        }
    }

    @Override
    public IPlace getRootPlace() throws DataAccessException {
        try {
            return _placeDAO.getRootByWorld(this);
        } catch (QueryException e) {
            throw new DataAccessException("Could not get root place.", e);
        } catch (IllegalArgumentException e) {
            throw new DataAccessException("World is not saved to the database: " + _name, e);
        }
    }
    //endregion

    //region Setters
    @Override
    public IWorld setId(int id) {
        if (_id == null) _id = id;
        return this;
    }

    @Override
    public IWorld setName(String name) throws ArgumentNullException {
        if (name == null || name.isEmpty()) throw new ArgumentNullException("name");

        _name = name.trim();
        return this;
    }

    @Override
    public IWorld setDescription(String description) {
        _description = description == null || description.isEmpty() ? null : description.trim();
        return this;
    }

    @Override
    public IWorld setOwner(IUser owner) throws IllegalOperationException, IllegalArgumentException {
        if (_ownerId != null) throw new IllegalOperationException("Owner cannot be changed.");
        if (owner.getId() == null) throw new IllegalArgumentException("Owner must be saved first.");

        if (_ownerId == null) _ownerId = owner.getId();
        return this;
    }
    //endregion

    //region Public members
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof IWorld)) return false;
        return Objects.equals(_id, ((IWorld) obj).getId());
    }
    //endregion
}
