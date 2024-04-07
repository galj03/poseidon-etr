package poseidon.DTO._Interfaces;

import poseidon.Exceptions.ArgumentNullException;
import poseidon.Exceptions.IllegalOperationException;

public interface ITerem {
    Integer getTeremId();
    Integer getFerohely();

    ITerem setTeremId(Integer id) throws IllegalOperationException;
    ITerem setFerohely(Integer ferohely) throws ArgumentNullException;
}
