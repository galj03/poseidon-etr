package poseidon.DTO._Interfaces;

/**
 * Interface to represent the appearance model.
 */
public interface IAppearance {
    //region Getters
    /**
     * Getter for the place.
     * @return The place which appears on the map.
     */
    IPlace getPlace();
    /**
     * Getter for the location.
     * @return The place on whose map it appears.
     */
    IPlace getLocation();
    /**
     * Getter for the coordinates.
     * @return The coordinates where the area of the place is in a string form
     * ("{topLeftX},{topLeftY},{bottomRightX},{bottomRightY}", where all values are numbers).
     */
    String getCoordinates();
    /**
     * Getter for the z-axis.
     * @return The z-axis for the location.
     */
    int getZAxis();
    //endregion

    //region Setters
    /**
     * Setter for place.
     * @param place The place which appears on the map.
     * @return Self for chaining.
     * @throws IllegalArgumentException If place is null or has no id.
     */
    IAppearance setPlace(IPlace place) throws IllegalArgumentException;
    /**
     * Setter for location.
     * @param location The place on whose map it appears.
     * @return Self for chaining.
     * @throws IllegalArgumentException If location is null or has no id.
     */
    IAppearance setLocation(IPlace location) throws IllegalArgumentException;
    /**
     * Setter for coordinates.
     * @param coordinates The coordinates where the area of the place is in a string form
     * ("{topLeftX},{topLeftY},{bottomRightX},{bottomRightY}", where all values are numbers).
     * @return Self for chaining.
     * @throws IllegalArgumentException If string is not in the correct format.
     */
    IAppearance setCoordinates(String coordinates) throws IllegalArgumentException;
    /**
     * Setter for z-axis.
     * @param zAxis The z-axis for the location.
     * @return Self for chaining.
     */
    IAppearance setZAxis(int zAxis);
    //endregion
}
