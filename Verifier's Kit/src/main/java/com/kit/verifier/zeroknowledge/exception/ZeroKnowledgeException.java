package com.kit.verifier.zeroknowledge.exception;

public class ZeroKnowledgeException extends RuntimeException {
    public ZeroKnowledgeException(String message) {
        super(message);
    }

    public ZeroKnowledgeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ZeroKnowledgeException(Throwable cause) {
        super(cause);
    }
}
