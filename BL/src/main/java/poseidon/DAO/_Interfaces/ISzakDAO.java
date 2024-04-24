package poseidon.DAO._Interfaces;

import org.springframework.dao.DataIntegrityViolationException;
import poseidon.DTO._Interfaces.ISzak;
import poseidon.DTO._Interfaces.ITantargy;
import poseidon.Exceptions.QueryException;

import java.sql.SQLException;
import java.util.List;

public interface ISzakDAO {
    Iterable<ISzak> getAll() throws QueryException;

    ISzak getById(Integer id) throws QueryException;

    ISzak save(ISzak szak) throws QueryException;

    void remove(ISzak szak) throws IllegalArgumentException, QueryException, DataIntegrityViolationException;

    List<ITantargy> getRequiredClasses(ISzak szak) throws QueryException;
}
