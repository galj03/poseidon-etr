package poseidon.DAO._Interfaces;

import org.springframework.dao.DataIntegrityViolationException;
import poseidon.DTO._Interfaces.ISzak;
import poseidon.DTO._Interfaces.IUser;
import poseidon.DTO._Interfaces.ITantargyData;
import poseidon.Exceptions.QueryException;

import java.util.List;
import java.util.Map;

public interface ISzakDAO {
    Iterable<ISzak> getAll() throws QueryException;

    List<ITantargyData> kotelezokGetAll(String psCode, Integer szakId) throws QueryException;

    ISzak getById(Integer id) throws QueryException;

    ISzak save(ISzak szak) throws QueryException;

    void remove(ISzak szak) throws IllegalArgumentException, QueryException, DataIntegrityViolationException;

    List<IUser> getAllUsersForSzak(ISzak szak) throws QueryException;

    Integer getRequiredClassesCount(ISzak szak) throws QueryException;

    Map<String, Float> getAveragesForAll(ISzak szak) throws QueryException;

    Integer finishedCoursesCountForEvfolyam(ISzak szak, Integer kezdEv) throws QueryException;
}
