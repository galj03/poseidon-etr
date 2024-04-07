package poseidon.DTO._Interfaces;

import poseidon.Exceptions.ArgumentNullException;

/**
 * Interface for representing the Szak model.
 */
public interface ISzak {
    /**
     * Getter for the szak id.
     *
     * @return The szak id.
     */
    Integer getSzakId();

    /**
     * Getter for the username.
     *
     * @return The username.
     */
    String getName();


    /**
     * Setter for the szak id.
     *
     * @param szakId The szak id.
     * @return Self for chaining.
     * @throws IllegalArgumentException If id is null or invalid.
     */
    ISzak setSzakId(Integer szakId) throws IllegalArgumentException;

    /**
     * Setter for the name.
     *
     * @param name The name.
     * @return Self for chaining.
     * @throws ArgumentNullException If name is null or empty.
     */
    ISzak setName(String name) throws ArgumentNullException;
}
