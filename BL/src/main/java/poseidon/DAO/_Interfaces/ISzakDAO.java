package poseidon.DAO._Interfaces;

import org.springframework.dao.DataIntegrityViolationException;
import poseidon.DTO._Interfaces.ISzak;
import poseidon.DTO._Interfaces.ITantargy;
import poseidon.DTO._Interfaces.IUser;
import poseidon.Exceptions.QueryException;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface ISzakDAO {
    Iterable<ISzak> getAll() throws QueryException;

    ISzak getById(Integer id) throws QueryException;

    ISzak save(ISzak szak) throws QueryException;

    void remove(ISzak szak) throws IllegalArgumentException, QueryException, DataIntegrityViolationException;

    Integer getRequiredClassesCount(ISzak szak) throws QueryException;

    Map<String, Float> getAveragesForAll(ISzak szak) throws QueryException;
}
