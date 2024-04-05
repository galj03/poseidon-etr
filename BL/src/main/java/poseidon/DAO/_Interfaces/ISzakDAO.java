package poseidon.DAO._Interfaces;

import poseidon.DTO._Interfaces.ISzak;
import poseidon.Exceptions.QueryException;

public interface ISzakDAO {
    Iterable<ISzak> getAll() throws QueryException;

    ISzak getById(Integer id) throws QueryException;

    ISzak save(ISzak szak) throws QueryException;

    void remove(ISzak szak) throws IllegalArgumentException, QueryException;
}
