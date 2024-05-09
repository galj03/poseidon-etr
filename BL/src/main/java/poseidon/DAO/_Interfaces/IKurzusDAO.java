package poseidon.DAO._Interfaces;

import poseidon.DTO._Interfaces.IKurzus;
import poseidon.DTO._Interfaces.IUser;
import poseidon.DTO._Interfaces.IKurzusData;
import poseidon.Exceptions.QueryException;
import java.util.List;
import java.util.Map;

import java.util.List;
import java.util.Set;

public interface IKurzusDAO {
    Iterable<IKurzus> getAll() throws QueryException;

    IKurzus getById(Integer id) throws QueryException;

    List<IKurzus> getKurzusokByTantargyId(Integer tantargyId);

    Integer getTantargyIdByKurzusId(Integer kurzusId) throws QueryException;

    Integer getSumOfEnrolledStudents(Integer kurzusId);

    List<IKurzusData> getAllCoursesOfSubject(Integer tantargyId, String PsCode);

    Set<Integer> getAllPrerequisities(Integer tantargyId);

    Set<Integer> getAllCompletedSubjectsByUser(String PsCode);

    List<Integer> checkPrerequisitesCompleted(Set<Integer> prerequisiteSubjects, Set<Integer> completedSubjects);

    void enrollCourse(Integer kurzusId, String PsCode);

    void removeFromCourse(String PsCode, Integer kurzusId);

    IKurzus save(IKurzus kurzus) throws QueryException;

    void remove(IKurzus kurzus) throws IllegalArgumentException, QueryException;

    void saveGrade(String psCode, Integer tantargyId, Integer grade);

    List<Map<IKurzus, Map<IUser, Integer>>> getTeachingCourses(String teacher_ps_kod);
}
