package poseidon.DTO;

import poseidon.DTO._Interfaces.ISzak;
import poseidon.Exceptions.ArgumentNullException;

public class Szak implements ISzak {
    private Integer _szakId;
    private String _name;

    public Szak(Integer _szakId, String _name) {
        this._szakId = _szakId;
        this._name = _name;
    }

    //Getters
    @Override
    public Integer getSzakId() {
        return _szakId;
    }

    @Override
    public String getName() {
        return _name;
    }
    //end Getters

    //Setters
    @Override
    public ISzak setSzakId(Integer szakId) throws IllegalArgumentException {
        if (szakId == null || szakId < 0) throw new IllegalArgumentException("szakId");

        _szakId = szakId;
        return this;
    }

    @Override
    public ISzak setName(String name) throws ArgumentNullException {
        if (name == null || name.isEmpty()) throw new ArgumentNullException("name");

        _name = name.trim();
        return this;
    }
    //end Setters
}
