package poseidon.DTO._Interfaces;

import poseidon.Exceptions.ArgumentNullException;
import poseidon.Exceptions.IllegalOperationException;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Interface to represent the user model.
 */
public interface IUser extends UserDetails {
    //region Getters
    /**
     * Getter for PS code.
     * @return The PS code of the user. Null if it wasn't yet saved.
     */
    String getPsCode();
    /**
     * Getter for the username.
     * @return The username.
     */
    String getUsername();
    /**
     * Getter for the e-mail.
     * @return The e-mail address.
     */
    String getEmail();
    /**
     * Getter for the path of the profile picture.
     * @return The path of the profile picture.
     */
    String getPfpPath();
    //endregion

    //region Setters
    /**
     * Setter for the PS code, only allow if PS code is null.
     * @param id New PS code.
     * @return Self for chaining.
     * @throws IllegalOperationException If PS code has been already set.
     */
    IUser setPsCode(String id) throws IllegalOperationException;

    /**
     * Setter for the username.
     * @param username The username.
     * @return Self for chaining.
     * @throws ArgumentNullException If username is null or empty.
     */
    IUser setUsername(String username) throws ArgumentNullException;
    /**
     * Setter for the password hash.
     * @param email The password hash.
     * @return Self for chaining.
     * @throws IllegalArgumentException If email is null or invalid.
     */
    IUser setEmail(String email) throws IllegalArgumentException;
    /**
     * Setter for the password hash.
     * @param password The password hash.
     * @return Self for chaining.
     * @throws ArgumentNullException If hash is null or empty.
     */
    IUser setPassword(String password) throws ArgumentNullException;
    /**
     * Setter for the path of the profile picture.
     * @param path The path of the profile picture.
     * @return Self for chaining.
     */
    IUser setPfpPath(String path);
    //endregion
}
