package lt.fintech.api.adapter;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseCode {
    OK(200),
    BAD_REQUEST(400),
    METHOD_NOT_ALLOWED(405);

    private final int code;
}