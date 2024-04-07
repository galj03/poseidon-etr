package poseidon.DTO._Interfaces;

import poseidon.Exceptions.ArgumentNullException;
import poseidon.Exceptions.IllegalOperationException;

public interface ITantargy {
    Integer getTantargyId();

    String getNev();

    String getFelelos();

    ITantargy setTantargyId(Integer id) throws IllegalOperationException;

    ITantargy setNev(String name) throws ArgumentNullException;

    ITantargy setFelelos(String felelos) throws ArgumentNullException;
}
