package poseidon.DTO._Interfaces;

import poseidon.Exceptions.ArgumentNullException;
import poseidon.Exceptions.IllegalOperationException;

import java.util.List;

/**
 * Interface to represent the world model.
 */
public interface IWorld {
    //region Getters
    /**
     * Getter for id.
     * @return The id of the world. Null if it wasn't yet saved.
     */
    Integer getId();
    /**
     * Getter for the name.
     * @return The name of the world.
     */
    String getName();
    /**
     * Getter for the description.
     * @return The description of the world.
     */
    String getDescription();
    /**
     * Getter for the owner.
     * @return The owner of the world.
     */
    IUser getOwner();
    /**
     * Getter for invited users.
     * @return All the users who have been invited.
     */
    List<IUser> getInvitedUsers();
    /**
     * Getter for joined users.
     * @return All the users who accepted the invite.
     */
    List<IUser> getJoinedUsers();
    /**
     * Getter for the root place of the world.
     * @return The root place of the world.
     */
    IPlace getRootPlace();
    //endregion

    //region Setters
    /**
     * Setter for the id, only allow if id is null.
     * @param id New id.
     * @return Self for chaining.
     * @throws IllegalOperationException If id has been already set.
     */
    IWorld setId(int id) throws IllegalOperationException;
    /**
     * Setter for the name.
     * @param name The name of the world.
     * @return Self for chaining.
     * @throws ArgumentNullException If name is null or empty.
     */
    IWorld setName(String name) throws ArgumentNullException;
    /**
     * Setter for the description.
     * @param description The description of the world.
     * @return Self for chaining.
     */
    IWorld setDescription(String description);
    /**
     * Setter for the owner, only allow if it's null.
     * @param owner The owner.
     * @return Self for chaining.
     * @throws IllegalOperationException If owner has been already set.
     * @throws IllegalArgumentException If owner has no id.
     */
    IWorld setOwner(IUser owner) throws IllegalOperationException, IllegalArgumentException;
    //endregion
}
