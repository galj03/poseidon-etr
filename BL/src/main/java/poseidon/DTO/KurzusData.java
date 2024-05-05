package poseidon.DTO;

import poseidon.DTO._Interfaces.IKurzusData;


public class KurzusData implements IKurzusData {
    private Integer kurzusId;
    private String nev;
    private String oktatoNeve;
    private String oktatoPsKod;
    private String nap;
    private Integer kezdesIdeje;
    private Boolean felveheto;
    private Boolean vizsga;
    private Integer ferohely;
    private Integer aktualisLetszam;
    private Boolean felvette;
    private Boolean teljesitette;

    public KurzusData(Integer kurzusId, String nev, String oktatoNeve, String oktatoPsKod, String nap, Integer kezdesIdeje, Boolean felveheto, Boolean vizsga, Integer ferohely, Integer aktualisLetszam, Boolean felvette, Boolean teljesitette) {
        this.kurzusId = kurzusId;
        this.nev = nev;
        this.oktatoNeve = oktatoNeve;
        this.oktatoPsKod = oktatoPsKod;
        this.nap = nap;
        this.kezdesIdeje = kezdesIdeje;
        this.felveheto = felveheto;
        this.vizsga = vizsga;
        this.ferohely = ferohely;
        this.aktualisLetszam = aktualisLetszam;
        this.felvette = felvette;
        this.teljesitette = teljesitette;
    }

    public KurzusData() {
    }

    public Integer getKurzusId() {
        return kurzusId;
    }

    @Override
    public String getNev() {
        return nev;
    }

    @Override
    public String getOktatoNeve() {
        return oktatoNeve;
    }

    public String getOktatoPsKod() {
        return oktatoPsKod;
    }

    @Override
    public String getNap() {
        return nap;
    }

    @Override
    public Integer getKezdesIdeje() {
        return kezdesIdeje;
    }

    public Boolean getFelveheto() {
        return felveheto;
    }

    @Override
    public Boolean getVizsga() {
        return vizsga;
    }

    public Integer getFerohely() {
        return ferohely;
    }

    @Override
    public Integer getAktualisLetszam() {
        return aktualisLetszam;
    }

    public Boolean getFelvette() {
        return felvette;
    }

    public Boolean getTeljesitette() {
        return teljesitette;
    }

    public KurzusData setKurzusId(Integer kurzusId) {
        this.kurzusId = kurzusId;
        return this;
    }

    @Override
    public KurzusData setNev(String nev) {
        this.nev = nev;
        return this;
    }

    @Override
    public KurzusData setOktatoNeve(String oktatoNeve) {
        this.oktatoNeve = oktatoNeve;
        return this;
    }

    public KurzusData setOktatoPsKod(String oktatoPsKod) {
        this.oktatoPsKod = oktatoPsKod;
        return this;
    }

    @Override
    public KurzusData setNap(String nap) {
        this.nap = nap;
        return this;
    }

    @Override
    public KurzusData setKezdesIdeje(Integer kezdesIdeje) {
        this.kezdesIdeje = kezdesIdeje;
        return this;
    }

    public KurzusData setFelveheto(Boolean felveheto) {
        this.felveheto = felveheto;
        return this;
    }

    @Override
    public KurzusData setVizsga(Boolean vizsga) {
        this.vizsga = vizsga;
        return this;
    }

    public KurzusData setFerohely(Integer ferohely) {
        this.ferohely = ferohely;
        return this;
    }

    @Override
    public KurzusData setAktualisLetszam(Integer aktualisLetszam) {
        this.aktualisLetszam = aktualisLetszam;
        return this;
    }

    public KurzusData setFelvette(Boolean felvette) {
        this.felvette = felvette;
        return this;
    }

    public KurzusData setTeljesitette(Boolean teljesitette) {
        this.teljesitette = teljesitette;
        return this;
    }
}
