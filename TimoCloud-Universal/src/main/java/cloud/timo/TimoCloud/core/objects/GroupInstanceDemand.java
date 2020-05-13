package cloud.timo.TimoCloud.core.objects;

public class GroupInstanceDemand implements Comparable<GroupInstanceDemand> {

    private Group group;
    private int amount;

    public GroupInstanceDemand(Group group, int amount) {
        this.group = group;
        this.amount = amount;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void changeAmount(int change) {
        this.amount += change;
    }

    public int getScore() {
        return getAmount()*getGroup().getPriority();
    }

    @Override
    public int compareTo(GroupInstanceDemand o) {
        return o.getScore()-getScore();
    }
}
