package poseidon.DTO._Interfaces;

import poseidon.Exceptions.ArgumentNullException;
import poseidon.Exceptions.IllegalOperationException;

public interface IPoszt {
    Integer getPosztId();

    String getPsCode();

    String getTartalom();

    Boolean isForBoard();

    IPoszt setPosztId(Integer id) throws IllegalOperationException;

    IPoszt setPsCode(String psCode) throws ArgumentNullException;

    IPoszt setTartalom(String tartalom) throws ArgumentNullException;

    IPoszt setIsForBoard(Boolean isForBoard) throws ArgumentNullException;
}
