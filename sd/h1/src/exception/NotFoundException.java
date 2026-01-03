package exception;

public class NotFoundException extends IllegalArgumentException {
	public NotFoundException(String msg) {
		super(msg);
	}
}
