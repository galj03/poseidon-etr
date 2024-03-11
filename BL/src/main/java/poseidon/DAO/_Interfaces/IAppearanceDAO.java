package poseidon.DAO._Interfaces;

import poseidon.Exceptions.ArgumentNullException;
import poseidon.Exceptions.QueryException;
import poseidon.DTO._Interfaces.IAppearance;
import poseidon.DTO._Interfaces.IPlace;

import java.util.List;

/**
 * Data access object for the appearance model.
 */
public interface IAppearanceDAO {
    /**
     * Get all places appearing on the map of the given location.
     * @param location The location.
     * @param publicOnly Only return discovered places.
     * @return All places appearing on the map of the given location.
     * @throws QueryException If query was unsuccessful.
     * @throws IllegalArgumentException If location is null or has no id.
     */
    List<IAppearance> getAllByLocation(IPlace location, boolean publicOnly) throws IllegalArgumentException, QueryException;
    /**
     * Save given appearance to datasource.
     * @param appearance Place to save.
     * @return The saved place.
     * @throws ArgumentNullException If appearance is null.
     * @throws QueryException If query was unsuccessful.
     */
    IAppearance save(IAppearance appearance) throws ArgumentNullException, QueryException;
    /**
     * Remove appearance from datasource.
     * @param appearance Place to remove.
     * @throws ArgumentNullException If appearance is null.
     * @throws QueryException If query was unsuccessful.
     */
    void remove(IAppearance appearance) throws ArgumentNullException, QueryException;
}
