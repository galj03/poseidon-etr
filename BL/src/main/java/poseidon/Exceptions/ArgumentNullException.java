package poseidon.Exceptions;

public class ArgumentNullException extends IllegalArgumentException {
    public ArgumentNullException(String s) {
        super("Argument cannot be null: " + s);
    }
}
