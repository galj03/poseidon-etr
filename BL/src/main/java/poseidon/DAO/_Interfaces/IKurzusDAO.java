package poseidon.DAO._Interfaces;

import poseidon.DTO._Interfaces.IKurzus;
import poseidon.DTO._Interfaces.IUser;
import poseidon.Exceptions.QueryException;

import java.util.Map;

public interface IKurzusDAO {
    Iterable<IKurzus> getAll() throws QueryException;

    IKurzus getById(Integer id) throws QueryException;

    IKurzus save(IKurzus kurzus) throws QueryException;

    void remove(IKurzus kurzus) throws IllegalArgumentException, QueryException;

    void saveGrade(String psCode, Integer tantargyId, Integer grade);

    Map<IKurzus, Map<IUser, Integer>> getTeachingCourses(String teacher_ps_kod);
}
