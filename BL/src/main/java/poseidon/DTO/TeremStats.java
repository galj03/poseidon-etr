package poseidon.DTO;

import poseidon.DTO._Interfaces.ITeremStats;

public class TeremStats implements ITeremStats {
    private Integer Id;
    private Integer ferohely;
    private String nap;
    private Integer kezdesIdo;
    private Integer letszam;

    public TeremStats(Integer Id, Integer ferohely, String nap, Integer kezdesIdo, Integer letszam) {
        this.Id = Id;
        this.ferohely = ferohely;
        this.nap = nap;
        this.kezdesIdo = kezdesIdo;
        this.letszam = letszam;
    }

    public TeremStats() {
    }

    public Integer getId() {
        return Id;
    }

    public Integer getFerohely() {
        return ferohely;
    }

    public String getNap() {
        return nap;
    }

    public Integer getKezdesIdo() {
        return kezdesIdo;
    }

    public Integer getLetszam() {
        return letszam;
    }

    public TeremStats setId(Integer ID) {
        this.Id = ID;
        return this;
    }

    public TeremStats setFerohely(Integer ferohely) {
        this.ferohely = ferohely;
        return this;
    }

    public TeremStats setNap(String nap) {
        this.nap = nap;
        return this;
    }

    public TeremStats setKezdesIdo(Integer kezdesIdo) {
        this.kezdesIdo = kezdesIdo;
        return this;
    }

    public TeremStats setLetszam(Integer letszam) {
        this.letszam = letszam;
        return this;
    }
}
