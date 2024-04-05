package poseidon.DAO._Interfaces;

import poseidon.DTO._Interfaces.IPoszt;
import poseidon.Exceptions.QueryException;

public interface IPosztDAO {
    Iterable<IPoszt> getAll() throws QueryException;

    IPoszt getById(Integer id) throws QueryException;

    IPoszt save(IPoszt poszt) throws QueryException;

    void remove(IPoszt poszt) throws IllegalArgumentException, QueryException;
}
