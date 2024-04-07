package poseidon.DTO;

import poseidon.DTO._Interfaces.ITerem;
import poseidon.Exceptions.ArgumentNullException;
import poseidon.Exceptions.IllegalOperationException;

public class Terem implements ITerem {
    private Integer _teremId;
    private Integer _ferohely;

    public Terem(Integer _teremId, Integer _ferohely) {
        this._teremId = _teremId;
        this._ferohely = _ferohely;
    }

    public Terem() {
    }

    @Override
    public Integer getTeremId() {
        return _teremId;
    }

    @Override
    public Integer getFerohely() {
        return _ferohely;
    }

    @Override
    public ITerem setTeremId(Integer id) throws IllegalOperationException {
        if (_teremId != null) throw new IllegalOperationException("Id cannot be changed");
        _teremId = id;
        return this;
    }

    @Override
    public ITerem setFerohely(Integer ferohely) throws ArgumentNullException {
        if (ferohely == null) throw new ArgumentNullException("ferohely");
        _ferohely = ferohely;
        return this;
    }
}
