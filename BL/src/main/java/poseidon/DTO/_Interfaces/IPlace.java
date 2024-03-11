package poseidon.DTO._Interfaces;

import poseidon.Exceptions.ArgumentNullException;
import poseidon.Exceptions.IllegalOperationException;

/**
 * Interface to represent the place model.
 */
public interface IPlace {
    //region Getters
    /**
     * Getter for id.
     * @return The id of the place. Null if it wasn't yet saved.
     */
    Integer getId();
    /**
     * Getter for the id of the world.
     * @return The id of the world where the place is in.
     */
    int getWorldId();
    /**
     * Getter for parent.
     * @return The parent of the place, or null if it's the root place.
     */
    IPlace getParent();
    /**
     * Getter for discovered.
     * @return If the place is discovered or not.
     */
    boolean isDiscovered();
    /**
     * Getter for the name.
     * @return The name of the place.
     */
    String getName();
    /**
     * Getter for the type.
     * @return The type of the place.
     */
    String getType();
    /**
     * Getter for notes.
     * @return The private notes by and for the game master.
     */
    String getNotes();
    /**
     * Getter for description.
     * @return The description of the place for the players.
     */
    String getDescription();
    /**
     * Getter for if the description is public for the users.
     * @return If the description is public for the users.
     */
    boolean isDescriptionShown();

    /**
     * Getter for the path of the map.
     * @return The path of the map.
     */
    String getMapPath();
    //endregion

    //region Setters
    /**
     * Setter for the id, only allow if id is null.
     * @param id New id.
     * @return Self for chaining.
     * @throws IllegalOperationException If id has been already set.
     */
    IPlace setId(int id) throws IllegalOperationException;
    /**
     * Setter for the id of the world, only allow if it's null.
     * @param worldId The id of the world where the place is.
     * @return Self for chaining.
     * @throws IllegalOperationException If world has been already set.
     */
    IPlace setWorldId(int worldId) throws IllegalOperationException;
    /**
     * Setter for parent.
     * @param parent The parent of the place, or null if it's the root place.
     * @return Self for chaining.
     * @throws IllegalArgumentException If parent has no id.
     */
    IPlace setParent(IPlace parent) throws IllegalArgumentException;
    /**
     * Setter for discovered.
     * @param discovered If the place is discovered or not.
     * @return Self for chaining.
     */
    IPlace setDiscovered(boolean discovered);
    /**
     * Setter for the name.
     * @param name The name of the place.
     * @return Self for chaining.
     * @throws ArgumentNullException If name is null or empty.
     */
    IPlace setName(String name) throws ArgumentNullException;
    /**
     * Setter for type.
     * @param type The type of the place.
     * @return Self for chaining.
     * @throws ArgumentNullException If type is null or empty.
     */
    IPlace setType(String type) throws ArgumentNullException;
    /**
     * Setter for the notes.
     * @param notes The private notes by and for the game master.
     * @return Self for chaining.
     */
    IPlace setNotes(String notes);
    /**
     * Setter for the description.
     * @param description The description of the place for the players.
     * @return Self for chaining.
     */
    IPlace setDescription(String description);
    /**
     * Setter for if the description is public for the users.
     * @param descriptionShown If the description is public for the users.
     * @return Self for chaining.
     */
    IPlace setDescriptionShown(boolean descriptionShown);
    /**
     * Setter for the path of the map.
     * @param path The path of the map.
     * @return Self for chaining.
     */
    IPlace setMapPath(String path);
    //endregion
}
