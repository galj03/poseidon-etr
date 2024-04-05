package poseidon.DAO._Interfaces;

import poseidon.DTO._Interfaces.ITerem;
import poseidon.Exceptions.QueryException;

public interface ITeremDAO {
    Iterable<ITerem> getAll() throws QueryException;

    ITerem getById(Integer id) throws QueryException;

    ITerem save(ITerem terem) throws QueryException;

    void remove(ITerem terem) throws IllegalArgumentException, QueryException;
}
