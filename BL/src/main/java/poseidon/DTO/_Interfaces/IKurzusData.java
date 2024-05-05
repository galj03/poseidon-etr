package poseidon.DTO._Interfaces;

import poseidon.DTO.KurzusData;

public interface IKurzusData {
    Integer getKurzusId();
    String getNev();
    String getOktatoNeve();
    String getOktatoPsKod();
    String getNap();
    Integer getKezdesIdeje();
    Boolean getFelveheto();
    Boolean getVizsga();
    Integer getFerohely();
    Integer getAktualisLetszam();
    Boolean getFelvette();
    Boolean getTeljesitette();
    KurzusData setKurzusId(Integer kurzusId);
    KurzusData setNev(String nev);
    KurzusData setOktatoNeve(String oktatoNeve);
    KurzusData setOktatoPsKod(String oktatoPsKod);
    KurzusData setNap(String nap);
    KurzusData setKezdesIdeje(Integer kezdesIdeje);
    KurzusData setFelveheto(Boolean felveheto);
    KurzusData setVizsga(Boolean vizsga);
    KurzusData setFerohely(Integer ferohely);
    KurzusData setAktualisLetszam(Integer aktualisLetszam);
    KurzusData setFelvette(Boolean felvette);
    KurzusData setTeljesitette(Boolean teljesitette);
}
