package poseidon.DAO._Interfaces;

import poseidon.Exceptions.QueryException;
import poseidon.DTO._Interfaces.IUser;
import poseidon.DTO._Interfaces.IWorld;

import java.util.List;

/**
 * Data access object for the user model.
 */
public interface IUserDAO {
    /**
     * Get user by their id.
     * @param id The user's id.
     * @return The user with the given id, or null.
     * @throws QueryException If query was unsuccessful.
     */
    IUser getById(int id) throws QueryException;
    /**
     * Get user by their exact username or e-mail.
     * @param searchText Value to search for username or e-mail with.
     * @return The user with the given username or e-mail address.
     * @throws QueryException If query was unsuccessful.
     */
    IUser getBySearchText(String searchText) throws QueryException;
    /**
     * Get all users who were invited to given world.
     * @param world The world where the users have been invited.
     * @return All the users who were invited to given world.
     * @throws IllegalArgumentException If the world is null or has no id.
     * @throws QueryException If query was unsuccessful.
     */
    List<IUser> getByInvitedWorld(IWorld world) throws IllegalArgumentException, QueryException;
    /**
     * Get all users who have joined to the given world.
     * @param world The world where the users have joined.
     * @return All the users who have joined to the given world.
     * @throws IllegalArgumentException If the world is null or has no id.
     * @throws QueryException If query was unsuccessful.
     */
    List<IUser> getByWorldJoined(IWorld world) throws IllegalArgumentException, QueryException;
    /**
     * Accept invite to world.
     * @param user The user who is accepting the invite.
     * @param world The world where the user has been invited.
     * @throws IllegalArgumentException If user or world is null or has no id or user has not been invited to the given world.
     * @throws QueryException If query was unsuccessful.
     */
    void acceptInvite(IUser user, IWorld world) throws IllegalArgumentException, QueryException;
    /**
     * Decline invite to world.
     * @param user The user who is declining the invite.
     * @param world The world where the user has been invited.
     * @throws IllegalArgumentException If user or world is null or has no id, or if invite has been already accepted.
     * @throws QueryException If query was unsuccessful.
     */
    void declineInvite(IUser user, IWorld world) throws IllegalArgumentException, QueryException;
    /**
     * Save given user to datasource.
     * @param user User to save.
     * @return The saved user.
     * @throws QueryException If query was unsuccessful.
     */
    IUser save(IUser user) throws QueryException;
    /**
     * Remove user from datasource with their profile picture, all worlds and invites.
     * @param user User to remove.
     * @throws IllegalArgumentException If user is null or has no id
     * @throws QueryException If query was unsuccessful.
     */
    void remove(IUser user) throws IllegalArgumentException, QueryException;
}
