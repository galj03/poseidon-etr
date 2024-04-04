package poseidon.DTO;

import poseidon.DTO._Interfaces.IKomment;
import poseidon.Exceptions.ArgumentNullException;
import poseidon.Exceptions.IllegalOperationException;

public class Komment implements IKomment {
    private Integer _kommentId;
    private Integer _posztId;
    private String _psCode;
    private String _tartalom;

    public Komment(Integer kommentId, Integer posztId, String psCode, String tartalom) {
        this._kommentId = kommentId;
        this._posztId = posztId;
        this._psCode = psCode;
        this._tartalom = tartalom;
    }

    public Komment() {
    }

    @Override
    public Integer getKommentId() {
        return _kommentId;
    }

    @Override
    public Integer getPosztId() {
        return _posztId;
    }

    @Override
    public String getPsCode() {
        return _psCode;
    }

    @Override
    public String getTartalom() {
        return _tartalom;
    }

    @Override
    public IKomment setKommentId(Integer id) throws IllegalOperationException {
        if (_kommentId != null) throw new IllegalOperationException("Id cannot be changed");
        _kommentId = id;
        return this;
    }

    @Override
    public IKomment setPosztId(Integer id) throws IllegalOperationException {
        if (_posztId != null) throw new IllegalOperationException("Foreign key id cannot be changed");
        _posztId = id;
        return this;
    }

    @Override
    public IKomment setPsCode(String psCode) throws ArgumentNullException {
        if (psCode == null || psCode.isEmpty()) throw new ArgumentNullException("psCode");

        _psCode = psCode.trim();
        return this;
    }

    @Override
    public IKomment setTartalom(String tartalom) throws ArgumentNullException {
        if (tartalom == null || tartalom.isEmpty()) throw new ArgumentNullException("tartalom");

        _tartalom = tartalom;
        return this;
    }
}
