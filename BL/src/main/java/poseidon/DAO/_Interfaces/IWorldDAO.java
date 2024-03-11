package poseidon.DAO._Interfaces;

import poseidon.Exceptions.QueryException;
import poseidon.DTO._Interfaces.IUser;
import poseidon.DTO._Interfaces.IWorld;

import java.util.List;

/**
 * Data access object for the world model.
 */
public interface IWorldDAO {
    /**
     * Get world by its id.
     * @param id The world's id.
     * @return The world with the given id, or null.
     * @throws QueryException If query was unsuccessful.
     */
    IWorld getById(int id) throws QueryException;
    /**
     * Get all worlds where the given user is the owner.
     * @param owner The user whose worlds to return.
     * @return All the worlds owned by the given user.
     * @throws IllegalArgumentException If owner is null or has no id.
     * @throws QueryException If query was unsuccessful.
     */
    List<IWorld> getAllByOwner(IUser owner) throws IllegalArgumentException, QueryException;
    /**
     * Get all worlds where the given user is joined.
     * @param joinedUser The user.
     * @return All worlds where the given user is joined.
     * @throws IllegalArgumentException If user is null or has no id.
     * @throws QueryException If query was unsuccessful.
     */
    List<IWorld> getAllByJoined(IUser joinedUser) throws IllegalArgumentException, QueryException;
    /**
     * Get all worlds where the given user is invited.
     * @param invitedUser The user.
     * @return All worlds where the given user is invited.
     * @throws IllegalArgumentException If user is null or has no id.
     * @throws QueryException If query was unsuccessful.
     */
    List<IWorld> getAllByInvited(IUser invitedUser) throws IllegalArgumentException, QueryException;
    /**
     * Invite user to world.
     * @param world World to invite to.
     * @param user User to invite.
     * @throws IllegalArgumentException If user or world is null or has no id or if the user has already been invited.
     * @throws QueryException If query was unsuccessful.
     */
    void inviteUser(IWorld world, IUser user) throws IllegalArgumentException, QueryException;
    /**
     * Remove user from world.
     * @param world World to remove from.
     * @param user User to remove.
     * @throws IllegalArgumentException If user or world is null or has no id or if user is the owner of the world.
     * @throws QueryException If query was unsuccessful.
     */
    void removeUser(IWorld world, IUser user) throws IllegalArgumentException, QueryException;
    /**
     * Save given world to datasource.
     * @param world The world to save.
     * @return The saved world.
     * @throws QueryException If query was unsuccessful.
     */
    IWorld save(IWorld world) throws QueryException;
    /**
     * Remove given world from the datasource, with all the places, their maps, appearances and invites/joins.
     * @param world The world to remove.
     * @throws IllegalArgumentException If world is null or has no id.
     * @throws QueryException If query was unsuccessful.
     */
    void remove(IWorld world) throws IllegalArgumentException, QueryException;
}
