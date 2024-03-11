package poseidon.DAO._Interfaces;

import poseidon.Exceptions.ArgumentNullException;
import poseidon.Exceptions.QueryException;
import poseidon.DTO._Interfaces.IPlace;
import poseidon.DTO._Interfaces.IWorld;

import java.util.List;

/**
 * Data access object for the place model.
 */
public interface IPlaceDAO {
    /**
     * Get place by its id.
     * @param id The id of the place.
     * @param queryParents Get world and parent values too or set them to null.
     * @return The place with the given id, or null.
     * @throws QueryException If query was unsuccessful.
     */
    IPlace getById(int id, boolean queryParents) throws QueryException;
    /**
     * Get place by its id.
     * @param id The id of the place.
     * @return The place with the given id, or null.
     * @throws QueryException If query was unsuccessful.
     */
    IPlace getById(Integer id) throws QueryException;
    /**
     * Get the root place of the given world.
     * @param world The world.
     * @return The root place of the given world.
     * @throws IllegalArgumentException If world is null or has no id.
     * @throws QueryException If query was unsuccessful.
     */
    IPlace getRootByWorld(IWorld world) throws IllegalArgumentException, QueryException;
    /**
     * Get all places in the given world.
     * @param world The world.
     * @param publicOnly Only return discovered places.
     * @return All places in the given world.
     * @throws IllegalArgumentException If world is null or has no id.
     * @throws QueryException If query was unsuccessful.
     */
    List<IPlace> getAllByWorld(IWorld world, boolean publicOnly) throws IllegalArgumentException, QueryException;
    /**
     * Get all children of the given place.
     * @param parent The parent place.
     * @param publicOnly Only return discovered places.
     * @return All children of the given place.
     * @throws IllegalArgumentException If parent is null or has no id.
     * @throws QueryException If query was unsuccessful.
     */
    List<IPlace> getAllByParent(IPlace parent, boolean publicOnly) throws IllegalArgumentException, QueryException;
    /**
     * Save given place to datasource.
     * @param place Place to save.
     * @return The saved place.
     * @throws QueryException If query was unsuccessful.
     */
    IPlace save(IPlace place) throws QueryException;
    /**
     * Remove place from datasource, with map file and all appearances. If the given place has any children, or it's the
     * root place it throws error.
     * @param place Place to remove.
     * @throws ArgumentNullException If place is null or has no id.
     * @throws QueryException If query was unsuccessful.
     */
    void remove(IPlace place) throws ArgumentNullException, QueryException;
}
