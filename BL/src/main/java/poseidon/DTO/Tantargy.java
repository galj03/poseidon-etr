package poseidon.DTO;

import poseidon.DTO._Interfaces.ITantargy;
import poseidon.Exceptions.ArgumentNullException;
import poseidon.Exceptions.IllegalOperationException;

public class Tantargy implements ITantargy {
    private Integer _id;
    private String _nev;
    private String _felelos;

    public Tantargy(Integer _id, String _nev, String _felelos) {
        this._id = _id;
        this._nev = _nev;
        this._felelos = _felelos;
    }

    public Tantargy() {
    }

    @Override
    public Integer getTantargyId() {
        return _id;
    }

    @Override
    public String getNev() {
        return _nev;
    }

    @Override
    public String getFelelos() {
        return _felelos;
    }

    @Override
    public ITantargy setTantargyId(Integer id) throws IllegalOperationException {
        if (_id != null) throw new IllegalOperationException("Id cannot be changed");
        _id = id;
        return this;
    }

    @Override
    public ITantargy setNev(String name) throws ArgumentNullException {
        if (name == null || name.isEmpty()) throw new ArgumentNullException("name");

        _nev = name.trim();
        return this;
    }

    @Override
    public ITantargy setFelelos(String felelos) throws ArgumentNullException {
        if (felelos == null || felelos.isEmpty()) {
            _felelos = null;
            return this;
        }

        _felelos = felelos.trim();
        return this;
    }
}
