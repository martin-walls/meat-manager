package com.martinwalls.meatmanager.data.api;

/**
 * Stores the possible errors that can occur when accessing the API.
 */
enum RequestStatus {
    /**
     * The request was successful.
     */
    OK(0),
    /**
     * No API access key was specified with the request. The access key should
     * be sent with every request to authenticate with the API.
     */
    MissingKey(101),
    /**
     * The monthly quota of API requests has been exceeded. The maximum number
     * of 1000 shouldn't be exceeded as data is cached for an hour.
     */
    MaxRequests(104),
    /**
     * The requested feature isn't supported by the current API subscription
     * plan.
     */
    NotSupported(105),
    /**
     * The API has no results matching the request.
     */
    NoResults(106),
    /**
     * The currency codes specified in the request are invalid. For example,
     * sending a request with {@code ...&symbols=ABC} will result in this error.
     */
    InvalidSymbols(202),
    /**
     * A fallback value for any error that doesn't match any other values.
     */
    UnknownError(-1);

    private final boolean success;
    private final int code;

    RequestStatus(int code) {
        this.success = code == 0;
        this.code = code;
    }

    public boolean isSuccess() {
        return success;
    }

    public int getCode() {
        return code;
    }

    /**
     * Returns the {@link RequestStatus} with the specified code.
     */
    static RequestStatus getStatusByCode(int code) {
        for (RequestStatus status : values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return UnknownError;
    }
}
