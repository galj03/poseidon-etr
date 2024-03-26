package poseidon.DTO._Interfaces;

import poseidon.Exceptions.ArgumentNullException;
import poseidon.Exceptions.IllegalOperationException;
import org.springframework.security.core.userdetails.UserDetails;
import poseidon.UserRoles;

/**
 * Interface to represent the user model.
 */
public interface IUser extends UserDetails {
    //region Getters

    /**
     * Getter for PS code.
     *
     * @return The PS code of the user. Null if it wasn't yet saved.
     */
    String getPsCode();

    /**
     * Getter for the username.
     *
     * @return The username.
     */
    String getName();

    /**
     * Getter for the e-mail.
     *
     * @return The e-mail address.
     */
    String getEmail();

    /**
     * Getter for the szak id.
     *
     * @return The szak id.
     */
    Integer getSzakId();

    /**
     * Getter for user role.
     *
     * @return The user's role.
     */
    UserRoles getRole();

    /**
     * Getter for kezdes eve.
     *
     * @return The kezdes eve.
     */
    Integer getKezdesEve();

    /**
     * Getter for vegzes eve.
     *
     * @return The vegzes eve.
     */
    Integer getVegzesEve();
    //endregion

    //region Setters

    /**
     * Setter for the PS code, only allow if PS code is null.
     *
     * @param id New PS code.
     * @return Self for chaining.
     * @throws IllegalOperationException If PS code has been already set.
     */
    IUser setPsCode(String id) throws IllegalOperationException;

    /**
     * Setter for the name.
     *
     * @param name The name.
     * @return Self for chaining.
     * @throws ArgumentNullException If name is null or empty.
     */
    IUser setName(String name) throws ArgumentNullException;

    /**
     * Setter for the email.
     *
     * @param email The email.
     * @return Self for chaining.
     * @throws IllegalArgumentException If email is null or invalid.
     */
    IUser setEmail(String email) throws IllegalArgumentException;

    /**
     * Setter for the password hash.
     *
     * @param password The password hash.
     * @return Self for chaining.
     * @throws ArgumentNullException If hash is null or empty.
     */
    IUser setPassword(String password) throws ArgumentNullException;

    /**
     * Setter for the szak id.
     *
     * @param szakId The szak id.
     * @return Self for chaining.
     * @throws IllegalArgumentException If id is null or invalid.
     */
    IUser setSzakId(Integer szakId) throws IllegalArgumentException;

    //TODO: should this be handled elsewhere?
    /**
     * Setter for the role.
     *
     * @param role The role.
     * @return Self for chaining.
     */
    IUser setRole(UserRoles role);

    /**
     * Setter for kezdes eve.
     *
     * @param kezdesEve The kezdes eve.
     * @return Self for chaining.
     * @throws IllegalArgumentException If number is null or invalid.
     */
    IUser setKezdesEve(Integer kezdesEve) throws IllegalArgumentException;

    /**
     * Setter for vegzes eve.
     *
     * @param vegzesEve The vegzes eve.
     * @return Self for chaining.
     * @throws IllegalArgumentException If number is null or invalid.
     */
    IUser setVegzesEve(Integer vegzesEve) throws IllegalArgumentException;
    //endregion
}
