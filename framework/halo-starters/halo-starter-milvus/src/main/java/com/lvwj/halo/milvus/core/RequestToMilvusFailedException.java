package com.lvwj.halo.milvus.core;

class RequestToMilvusFailedException extends RuntimeException {

    public RequestToMilvusFailedException() {
        super();
    }

    public RequestToMilvusFailedException(String message) {
        super(message);
    }

    public RequestToMilvusFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
