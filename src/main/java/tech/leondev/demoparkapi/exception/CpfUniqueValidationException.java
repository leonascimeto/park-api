package tech.leondev.demoparkapi.exception;

public class CpfUniqueValidationException extends RuntimeException {
    public CpfUniqueValidationException(String message) {
        super(message);
    }
}
