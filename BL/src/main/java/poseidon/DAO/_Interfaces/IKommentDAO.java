package poseidon.DAO._Interfaces;

import poseidon.DTO._Interfaces.IKomment;
import poseidon.Exceptions.QueryException;

public interface IKommentDAO {
    Iterable<IKomment> getAll() throws QueryException;

    IKomment getById(Integer id) throws QueryException;

    IKomment save(IKomment komment) throws QueryException;

    void remove(IKomment komment) throws IllegalArgumentException, QueryException;
}
