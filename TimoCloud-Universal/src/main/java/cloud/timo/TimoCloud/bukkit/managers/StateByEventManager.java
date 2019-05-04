package cloud.timo.TimoCloud.bukkit.managers;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.bukkit.TimoCloudBukkit;

import java.text.NumberFormat;
import java.util.*;

public class StateByEventManager {

    private String stateBefore;
    private int setByPlayer = 0;
    private String lastStateSet;

    private void setState(String state) {
        TimoCloudAPI.getBukkitAPI().getThisServer().setState(state);
    }

    public void setStateByMotd(String motd) {
        Object state = TimoCloudBukkit.getInstance().getFileManager().getConfig().get("MotdToState." + motd.trim());
        if (state != null && state instanceof String) {
            setState((String) state);
        }
    }

    public void onPlayerJoin() {
        TimoCloudBukkit.getInstance().getServer().getScheduler().callSyncMethod(TimoCloudBukkit.getInstance(), () -> {
            setStateByPlayerCount();
            return null;
        });
    }

    public void onPlayerQuit() {
        TimoCloudBukkit.getInstance().getServer().getScheduler().callSyncMethod(TimoCloudBukkit.getInstance(), () -> {
            setStateByPlayerCount();
            return null;
        });
    }

    public void setStateByPlayerCount() {
        int cur = TimoCloudBukkit.getInstance().getOnlinePlayersAmount();
        String currentState = TimoCloudAPI.getBukkitAPI().getThisServer().getState();
        if (!currentState.equals(lastStateSet)) stateBefore = currentState;
        if (!TimoCloudBukkit.getInstance().getFileManager().getConfig().getStringList("PlayersToState.enabledWhileStates").contains(stateBefore))
            return;
        double percentage = (double) cur / (double) TimoCloudBukkit.getInstance().getMaxPlayersAmount() * 100;
        String state = null;
        List<Double> steps = new ArrayList<>();
        Map<Double, String> states = new HashMap<>();
        for (String step : TimoCloudBukkit.getInstance().getFileManager().getConfig().getConfigurationSection("PlayersToState.percentages").getKeys(false)) {
            try {
                Double d = NumberFormat.getInstance(Locale.GERMAN).parse(step).doubleValue();
                steps.add(d);
                states.put(d, TimoCloudBukkit.getInstance().getFileManager().getConfig().getString("PlayersToState.percentages." + step));
            } catch (Exception e) {
            }
        }
        Collections.sort(steps);
        for (double step : steps) {
            if (percentage >= step) state = states.get(step);
        }
        if (state == null) {
            setState(stateBefore);
        } else {
            setState(state);
            lastStateSet = state;
        }
    }

}
