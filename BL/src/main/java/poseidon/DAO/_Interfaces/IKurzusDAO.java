package poseidon.DAO._Interfaces;

import poseidon.DTO._Interfaces.IKurzus;
import poseidon.Exceptions.QueryException;

public interface IKurzusDAO {
    Iterable<IKurzus> getAll() throws QueryException;

    IKurzus getById(Integer id) throws QueryException;

    IKurzus save(IKurzus kurzus) throws QueryException;

    void remove(IKurzus kurzus) throws IllegalArgumentException, QueryException;
}
