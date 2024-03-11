package poseidon.DTO;

import poseidon.DAO._Interfaces.IPlaceDAO;
import poseidon.DTO._Interfaces.IAppearance;
import poseidon.DTO._Interfaces.IPlace;
import poseidon.Exceptions.ArgumentNullException;
import poseidon.Exceptions.DataAccessException;
import poseidon.Exceptions.QueryException;

import java.util.regex.Pattern;

/**
 * Data transfer object to represent the appearance model.
 */
public class Appearance implements IAppearance {
    //region Properties
    private final IPlaceDAO _placeDAO;
    private Integer _placeId;
    private Integer _locationId;
    private String _coordinates;
    private int _zAxis;
    //endregion

    //region Constructor
    public Appearance(IPlaceDAO placeDAO) throws ArgumentNullException {
        if (placeDAO == null) throw new ArgumentNullException("placeDAO");
        _placeDAO = placeDAO;
    }
    //endregion

    //region Getters
    @Override
    public IPlace getPlace() throws DataAccessException {
        try {
            return _placeDAO.getById(_placeId);
        } catch (QueryException e) {
            throw new DataAccessException("Couldn't get place by id: " + _placeId, e);
        }
    }

    @Override
    public IPlace getLocation() throws DataAccessException {
        try {
            return _placeDAO.getById(_locationId);
        } catch (QueryException e) {
            throw new DataAccessException("Couldn't get location by id: " + _locationId, e);
        }
    }

    @Override
    public String getCoordinates() {
        return _coordinates;
    }

    @Override
    public int getZAxis() {
        return _zAxis;
    }
    //endregion

    //region Setters
    @Override
    public IAppearance setPlace(IPlace place) throws IllegalArgumentException {
        if (place == null) throw new ArgumentNullException("place");
        if (place.getId() == null) throw new IllegalArgumentException("Place must be saved first.");

        _placeId = place.getId();
        return this;
    }

    @Override
    public IAppearance setLocation(IPlace location) throws IllegalArgumentException {
        if (location == null) throw new ArgumentNullException("location");
        if (location.getId() == null) throw new IllegalArgumentException("Location must be saved first.");

        _locationId = location.getId();
        return this;
    }

    @Override
    public IAppearance setCoordinates(String coordinates) throws IllegalArgumentException {
        if (coordinates == null) throw new ArgumentNullException("coordinates");
        if (!Pattern.compile("\\d+,\\d+,\\d+,\\d+").matcher(coordinates.trim()).matches())
            throw new IllegalArgumentException("Coordinates are not in '{topLeftX},{topLeftY},{bottomRightX},{bottomRightY}' format: " + coordinates);

        _coordinates = coordinates.trim();
        return this;
    }

    @Override
    public IAppearance setZAxis(int zAxis) {
        _zAxis = zAxis;
        return this;
    }
    //endregion
}
