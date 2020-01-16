package lt.fintech.api.adapter;

import lombok.Value;

@Value
public class StandardResponse<T> {
    private final T responseObject;
    private final ResponseCode responseCode;
    private final String errorMessage;
}