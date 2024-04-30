package poseidon.DAO._Interfaces;

import poseidon.DTO._Interfaces.IKurzus;
import poseidon.DTO._Interfaces.ITantargy;
import poseidon.DTO._Interfaces.IUser;
import poseidon.Exceptions.QueryException;

import java.util.List;
import java.util.Map;

public interface ITantargyDAO {
    Iterable<ITantargy> getAll() throws QueryException;

    ITantargy getById(Integer id) throws QueryException;

    ITantargy save(ITantargy tantargy) throws QueryException;

    void remove(ITantargy tantargy) throws IllegalArgumentException, QueryException;

    List<Map<ITantargy, List<IKurzus>>> getTeachingSubjects(String ps_kod);

    boolean saveRequiredSubjects(ITantargy tantargy, List<ITantargy> feltetelek);

    void removeAllRequiredSubjects(ITantargy tantargy);

    void approveStudents(List<String> students, int tantargyId);

    void removeStudentFromSubject(String ps_kod, Integer tantargyId);
}
