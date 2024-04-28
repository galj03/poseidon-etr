package poseidon.DAO._Interfaces;

import poseidon.DTO._Interfaces.IKurzus;
import poseidon.DTO._Interfaces.ITantargy;
import poseidon.Exceptions.QueryException;

import java.util.List;
import java.util.Map;

public interface ITantargyDAO {
    Iterable<ITantargy> getAll() throws QueryException;

    ITantargy getById(Integer id) throws QueryException;

    ITantargy save(ITantargy tantargy) throws QueryException;

    void remove(ITantargy tantargy) throws IllegalArgumentException, QueryException;

    Map<ITantargy, List<IKurzus>> getTeachingSubjects(String ps_kod);
}
