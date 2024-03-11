package poseidon.DTO;

import poseidon.DAO._Interfaces.IPlaceDAO;
import poseidon.DTO._Interfaces.IPlace;
import poseidon.Exceptions.ArgumentNullException;
import poseidon.Exceptions.DataAccessException;
import poseidon.Exceptions.IllegalOperationException;
import poseidon.Exceptions.QueryException;

/**
 * Data transfer object to represent the place model.
 */
public class Place implements IPlace {
    //region Properties
    private final IPlaceDAO _placeDAO;
    private Integer _id;
    private Integer _worldId;
    private Integer _parentId;
    private boolean _isDiscovered;
    private String _name;
    private String _type;
    private String _notes;
    private String _description;
    private boolean _isDescriptionShown;
    private String _mapPath;
    //endregion

    //region Constructors
    public Place(IPlaceDAO placeDAO) throws ArgumentNullException {
        if (placeDAO == null) throw new ArgumentNullException("placeDAO");

        _placeDAO = placeDAO;
    }
    //endregion

    //region Getters
    @Override
    public Integer getId() {
        return _id;
    }

    @Override
    public int getWorldId() {
        return _worldId;
    }

    @Override
    public IPlace getParent() throws DataAccessException {
        try {
            return _placeDAO.getById(_parentId);
        } catch (QueryException e) {
            throw new DataAccessException("Could not get parent by id: " + _parentId, e);
        }
    }

    @Override
    public boolean isDiscovered() {
        return _isDiscovered;
    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public String getType() {
        return _type;
    }

    @Override
    public String getNotes() {
        return _notes;
    }

    @Override
    public String getDescription() {
        return _description;
    }

    @Override
    public boolean isDescriptionShown() {
        return _isDescriptionShown;
    }

    @Override
    public String getMapPath() {
        return _mapPath;
    }
    //endregion

    //region Setters
    @Override
    public IPlace setId(int id) throws IllegalOperationException {
        if (_id != null) throw new IllegalOperationException("Id cannot be changed.");

        _id = id;
        return this;
    }

    @Override
    public IPlace setWorldId(int worldId) throws IllegalOperationException {
        if (_worldId != null) throw new IllegalOperationException("World cannot be changed.");

        _worldId = worldId;
        return this;
    }

    @Override
    public IPlace setParent(IPlace parent) throws IllegalArgumentException  {
        if (parent == null) {
            _parentId = null;
            return this;
        }
        if (parent.getId() == null) throw new IllegalArgumentException("Parent must be saved first.");

        //TODO: validate to ensure there is one root place only

        _parentId = parent.getId();
        return this;
    }

    @Override
    public IPlace setDiscovered(boolean discovered) {
        _isDiscovered = discovered;
        return this;
    }

    @Override
    public IPlace setName(String name) throws ArgumentNullException {
        if (name == null || name.isEmpty()) throw new ArgumentNullException("name");

        _name = name.trim();
        return this;
    }

    @Override
    public IPlace setType(String type) throws ArgumentNullException {
        if (type == null || type.isEmpty()) throw new ArgumentNullException("type");

        _type = type.trim();
        return this;
    }

    @Override
    public IPlace setNotes(String notes) {
        _notes = (notes == null) ? "" : notes.trim();
        return this;
    }

    @Override
    public IPlace setDescription(String description) {
        _description = (description == null) ? "" : description.trim();
        return this;
    }

    @Override
    public IPlace setDescriptionShown(boolean descriptionShown) {
        _isDescriptionShown = descriptionShown;
        return this;
    }

    @Override
    public IPlace setMapPath(String path) {
        _mapPath = path == null || path.isEmpty() ? null : path.trim();
        return this;
    }
    //endregion
}
