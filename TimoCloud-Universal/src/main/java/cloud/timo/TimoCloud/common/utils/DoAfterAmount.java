package cloud.timo.TimoCloud.common.utils;

import lombok.Setter;

public class DoAfterAmount {

    private int current;
    @Setter
    private int amount;
    private final Runnable runnable;

    public DoAfterAmount(int amount, Runnable runnable) {
        current = 0;
        this.amount = amount;
        this.runnable = runnable;
    }

    public void addOne() {
        current++;
        if (current == amount) runnable.run();
    }
}
