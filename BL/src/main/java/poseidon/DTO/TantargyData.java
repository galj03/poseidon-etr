package poseidon.DTO;

import poseidon.DTO._Interfaces.ITantargy;
import poseidon.DTO._Interfaces.ITantargyData;

public class TantargyData implements ITantargyData {
    private ITantargy tantargy;
    private String targyfelelosNev;
    private Boolean teljesitett;

    public TantargyData(ITantargy tantargy, String targyfelelosNev, Boolean teljesitett) {
        this.tantargy = tantargy;
        this.targyfelelosNev = targyfelelosNev;
        this.teljesitett = teljesitett;
    }

    public TantargyData() {
    }

    public ITantargy getTantargy() {
        return tantargy;
    }

    public String getTargyfelelosNev() {
        return targyfelelosNev;
    }

    public Boolean getTeljesitett() {
        return teljesitett;
    }

    public void setTantargy(ITantargy tantargy) {
        this.tantargy = tantargy;
    }

    public void setTargyfelelosNev(String targyfelelosNev) {
        this.targyfelelosNev = targyfelelosNev;
    }

    public void setTeljesitett(Boolean teljesitett) {
        this.teljesitett = teljesitett;
    }
}
