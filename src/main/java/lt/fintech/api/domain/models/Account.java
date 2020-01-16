package lt.fintech.api.domain.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Account {
    String accountNumber;
    float amount;

    public boolean canDeposit(float amount) {
        return this.amount - amount >= 0;
    }

    public void deposit(float amount) {
        this.amount -= amount;
    }

    public void credit(float amount) {
        this.amount += amount;
    }
}