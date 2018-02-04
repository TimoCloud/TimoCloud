package cloud.timo.TimoCloud.utils;

public class DoAfterAmount {

    private int current;
    private int amount;
    private Runnable runnable;

    public DoAfterAmount(int amount, Runnable runnable) {
        current = 0;
        this.amount = amount;
        this.runnable = runnable;
    }

    public void addOne() {
        current++;
        if (current == amount) runnable.run();
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
