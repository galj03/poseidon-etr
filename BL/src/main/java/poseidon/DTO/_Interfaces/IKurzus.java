package poseidon.DTO._Interfaces;

import poseidon.Exceptions.ArgumentNullException;
import poseidon.Exceptions.IllegalOperationException;

import java.sql.Timestamp;

public interface IKurzus {
    Integer getKurzusId();
    String getNev();
    String getOktato();
    String getKezdesNapja();
    Integer getKezdesIdopontja();
    Integer getTantargyId();
    Integer getTeremId();
    Boolean isFelveheto();
    Boolean isVizsga();
    IKurzus setKurzusId(Integer id) throws IllegalOperationException;
    IKurzus setNev(String name) throws ArgumentNullException;
    IKurzus setOktato(String oktato) throws ArgumentNullException;
    IKurzus setKezdesNapja(String nap) throws ArgumentNullException;
    IKurzus setKezdesIdopontja(Integer idopont) throws ArgumentNullException;
    IKurzus setTantargyId(Integer id) throws ArgumentNullException;
    IKurzus setTeremId(Integer id) throws ArgumentNullException;
    IKurzus setIsFelveheto(Boolean felvehetoE) throws ArgumentNullException;
    IKurzus setIsVizsga(Boolean vizsgaE) throws ArgumentNullException;
}
