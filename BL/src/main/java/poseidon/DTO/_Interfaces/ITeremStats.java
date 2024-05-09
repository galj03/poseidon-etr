package poseidon.DTO._Interfaces;

import poseidon.DTO.TeremStats;

public interface ITeremStats {
    Integer getId();
    Integer getFerohely();
    String getNap();
    Integer getKezdesIdo();
    Integer getLetszam();
    TeremStats setId(Integer Id);
    TeremStats setFerohely(Integer ferohely);
    TeremStats setNap(String nap);
    TeremStats setKezdesIdo(Integer kezdesIdo);
    TeremStats setLetszam(Integer letszam);
}
