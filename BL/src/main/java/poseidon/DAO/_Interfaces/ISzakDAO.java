package poseidon.DAO._Interfaces;

import org.springframework.dao.DataIntegrityViolationException;
import poseidon.DTO._Interfaces.ISzak;
import poseidon.Exceptions.QueryException;

import java.sql.SQLException;

public interface ISzakDAO {
    Iterable<ISzak> getAll() throws QueryException;

    ISzak getById(Integer id) throws QueryException;

    ISzak save(ISzak szak) throws QueryException;

    void remove(ISzak szak) throws IllegalArgumentException, QueryException, DataIntegrityViolationException;
}
