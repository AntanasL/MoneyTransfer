package lt.fintech.api.domain.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Transfer {
    String id;
    String accountFrom;
    String accountTo;
    float amount;
}