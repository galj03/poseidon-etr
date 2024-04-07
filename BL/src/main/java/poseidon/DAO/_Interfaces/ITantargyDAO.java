package poseidon.DAO._Interfaces;

import poseidon.DTO._Interfaces.ITantargy;
import poseidon.Exceptions.QueryException;

public interface ITantargyDAO {
    Iterable<ITantargy> getAll() throws QueryException;

    ITantargy getById(Integer id) throws QueryException;

    ITantargy save(ITantargy tantargy) throws QueryException;

    void remove(ITantargy tantargy) throws IllegalArgumentException, QueryException;
}
