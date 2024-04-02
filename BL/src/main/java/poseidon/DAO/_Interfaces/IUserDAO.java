package poseidon.DAO._Interfaces;

import poseidon.Exceptions.QueryException;
import poseidon.DTO._Interfaces.IUser;

/**
 * Data access object for the user model.
 */
public interface IUserDAO {
    /**
     * Get all users.
     * @return The users, or null.
     * @throws QueryException If query was unsuccessful.
     */
    Iterable<IUser> getAllUsers() throws QueryException;

    /**
     * Get user by their id.
     * @param id The user's id.
     * @return The user with the given id, or null.
     * @throws QueryException If query was unsuccessful.
     */
    IUser getByPsCode(String id) throws QueryException;

    /**
     * Get user by their exact username or e-mail.
     * @param searchText Value to search for username or e-mail with.
     * @return The user with the given username or e-mail address.
     * @throws QueryException If query was unsuccessful.
     */
    IUser getByEmail(String searchText) throws QueryException;

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
