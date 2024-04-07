package poseidon.DTO;

import poseidon.DTO._Interfaces.IPoszt;
import poseidon.Exceptions.ArgumentNullException;
import poseidon.Exceptions.IllegalOperationException;

public class Poszt implements IPoszt {
    private Integer _posztId;
    private String _psCode;
    private String _tartalom;

    public Poszt(Integer posztId, String psCode, String tartalom) {
        this._posztId = posztId;
        this._psCode = psCode;
        this._tartalom = tartalom;
    }

    public Poszt() {
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
    public IPoszt setPosztId(Integer id) throws IllegalOperationException {
        if (_posztId != null) throw new IllegalOperationException("Id cannot be changed");
        _posztId = id;
        return this;
    }

    @Override
    public IPoszt setPsCode(String psCode) throws ArgumentNullException {
        if (psCode == null || psCode.isEmpty()) throw new ArgumentNullException("psCode");

        _psCode = psCode.trim();
        return this;
    }

    @Override
    public IPoszt setTartalom(String tartalom) throws ArgumentNullException {
        if (tartalom == null || tartalom.isEmpty()) throw new ArgumentNullException("tartalom");

        _tartalom = tartalom;
        return this;
    }
}
