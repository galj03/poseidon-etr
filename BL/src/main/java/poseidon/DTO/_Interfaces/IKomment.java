package poseidon.DTO._Interfaces;

import poseidon.Exceptions.ArgumentNullException;
import poseidon.Exceptions.IllegalOperationException;

public interface IKomment {
    Integer getKommentId();

    Integer getPosztId();

    String getPsCode();

    String getTartalom();

    IKomment setKommentId(Integer id) throws IllegalOperationException;

    IKomment setPosztId(Integer id) throws IllegalOperationException;

    IKomment setPsCode(String psCode) throws ArgumentNullException;

    IKomment setTartalom(String tartalom) throws ArgumentNullException;
}
