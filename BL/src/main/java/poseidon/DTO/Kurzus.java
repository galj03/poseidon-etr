package poseidon.DTO;

import poseidon.DTO._Interfaces.IKurzus;
import poseidon.Exceptions.ArgumentNullException;
import poseidon.Exceptions.IllegalOperationException;

import java.sql.Timestamp;

public class Kurzus implements IKurzus {
    private Integer _kurzusId;
    private String _nev;
    private String _oktato;
    private String _kezdesNapja;
    private Timestamp _kezdesIdopontja;
    private Integer _tantargyId;
    private Integer _teremId;
    private Boolean _isFelveheto;
    private Boolean _isVizsga;

    public Kurzus(Integer _kurzusId, String _nev, String _oktato, String _kezdesNapja, Timestamp _kezdesIdopontja, Integer _tantargyId, Integer _teremId, Boolean _isFelveheto, Boolean _isVizsga) {
        this._kurzusId = _kurzusId;
        this._nev = _nev;
        this._oktato = _oktato;
        this._kezdesNapja = _kezdesNapja;
        this._kezdesIdopontja = _kezdesIdopontja;
        this._tantargyId = _tantargyId;
        this._teremId = _teremId;
        this._isFelveheto = _isFelveheto;
        this._isVizsga = _isVizsga;
    }

    public Kurzus() {
    }

    @Override
    public Integer getKurzusId() {
        return _kurzusId;
    }

    @Override
    public String getNev() {
        return _nev;
    }

    @Override
    public String getOktato() {
        return _oktato;
    }

    @Override
    public String getKezdesNapja() {
        return _kezdesNapja;
    }

    @Override
    public Timestamp getKezdesIdopontja() {
        return _kezdesIdopontja;
    }

    @Override
    public Integer getTantargyId() {
        return _tantargyId;
    }

    @Override
    public Integer getTeremId() {
        return _teremId;
    }

    @Override
    public Boolean isFelveheto() {
        return _isFelveheto;
    }

    @Override
    public Boolean isVizsga() {
        return _isVizsga;
    }

    @Override
    public IKurzus setKurzusId(Integer id) throws IllegalOperationException {
        if (_kurzusId != null) throw new IllegalOperationException("Id cannot be changed");
        _kurzusId = id;
        return this;
    }

    @Override
    public IKurzus setNev(String name) throws ArgumentNullException {
        if (name == null || name.isEmpty()) throw new ArgumentNullException("name");

        _nev = name.trim();
        return this;
    }

    @Override
    public IKurzus setOktato(String oktato) throws ArgumentNullException {
        if (oktato == null || oktato.isEmpty()) throw new ArgumentNullException("oktato");

        _oktato = oktato.trim();
        return this;
    }

    @Override
    public IKurzus setKezdesNapja(String nap) throws ArgumentNullException {
        if (nap == null || nap.isEmpty()) throw new ArgumentNullException("nap");

        _kezdesNapja = nap.trim();
        return this;
    }

    @Override
    public IKurzus setKezdesIdopontja(Timestamp idopont) throws ArgumentNullException {
        if (idopont == null) throw new ArgumentNullException("idopont");

        _kezdesIdopontja = idopont;
        return this;
    }

    @Override
    public IKurzus setTantargyId(Integer id) throws ArgumentNullException {
        if (id == null) throw new ArgumentNullException("tantargyId");
        _tantargyId = id;
        return this;
    }

    @Override
    public IKurzus setTeremId(Integer id) throws ArgumentNullException {
        if (id == null) throw new ArgumentNullException("teremId");
        _teremId = id;
        return this;
    }

    @Override
    public IKurzus setIsFelveheto(Boolean felvehetoE) throws ArgumentNullException {
        if (felvehetoE == null) throw new ArgumentNullException("felvehetoE");
        _isFelveheto = felvehetoE;
        return this;
    }

    @Override
    public IKurzus setIsVizsga(Boolean vizsgaE) throws ArgumentNullException {
        if (vizsgaE == null) throw new ArgumentNullException("vizsgaE");
        _isVizsga = vizsgaE;
        return this;
    }
}
