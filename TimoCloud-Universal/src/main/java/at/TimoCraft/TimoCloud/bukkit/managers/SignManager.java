package at.TimoCraft.TimoCloud.bukkit.managers;

import at.TimoCraft.TimoCloud.api.TimoCloudAPI;
import at.TimoCraft.TimoCloud.api.objects.GroupObject;
import at.TimoCraft.TimoCloud.api.objects.ServerObject;
import at.TimoCraft.TimoCloud.bukkit.TimoCloudBukkit;
import at.TimoCraft.TimoCloud.bukkit.helpers.JsonHelper;
import at.TimoCraft.TimoCloud.bukkit.objects.SignInstance;
import at.TimoCraft.TimoCloud.bukkit.objects.SignLayout;
import at.TimoCraft.TimoCloud.bukkit.objects.SignTemplate;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Timo on 28.12.16.
 */
public class SignManager {

    private List<SignTemplate> signTemplates;
    private List<SignInstance> signInstances;
    private int updates = 0;

    public SignManager() {
        load();
    }

    public void load() {
        loadSignTemplates();
        loadSignInstances();

        TimoCloudBukkit.log("Successfully loaded signs!");
    }

    private void loadSignTemplates() {
        signTemplates = new ArrayList<>();
        FileConfiguration config = TimoCloudBukkit.getInstance().getFileManager().getSignTemplates();
        for (String template : config.getKeys(false)) {
            String errorReason = "Unknown";
            Map<String, SignLayout> layouts = new HashMap<>();
            try {
                for (String layout : config.getConfigurationSection(template + ".layouts").getKeys(false)) {
                    List<String>[] lines = Arrays.copyOf(new Object[4], 4, List[].class);
                    for (int i = 0; i < 4; i++) {
                        String line = config.getString(template + ".layouts." + layout + ".lines." + (i + 1));
                        if (line == null)
                            errorReason = "Line " + (i + 1) + " (signTemplates.yml section '" + template + ".layouts.lines." + (i + 1) + ") is not defined.";
                        lines[i] = Arrays.asList(line.split(";"));
                    }
                    layouts.put(layout, new SignLayout(lines, config.getLong(template + ".layouts." + layout + ".updateSpeed")));
                }
                if (! layouts.containsKey("Default")) {
                    errorReason = "Template " + template + " does not have a default layout 'Default'. Please add it and type /signs reload";
                    throw new Exception();
                }
                signTemplates.add(new SignTemplate(template, layouts));
            } catch (Exception e) {
                TimoCloudBukkit.log("&cCould not parse sign template &e" + template + "&c. Please check your &esignTemplates.yml&c. Reason: &e" + errorReason);
            }
        }
    }

    private void loadSignInstances() {
        signInstances = new ArrayList<>();
        try {
            JSONArray jsonArray = TimoCloudBukkit.getInstance().getFileManager().getSignInstances();
            for (Object object : jsonArray) {
                JSONObject jsonObject = (JSONObject) object;
                signInstances.add(new SignInstance(
                        JsonHelper.locationFromJson((JSONObject) jsonObject.get("location")),
                        (String) jsonObject.get("target"),
                        (String) jsonObject.get("template"),
                        getSignTemplate((String) jsonObject.get("template")),
                        (boolean) jsonObject.get("dynamic"),
                        ((Long) jsonObject.get("priority")).intValue()
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveSignInstances() {
        try {
            JSONArray jsonArray = new JSONArray();

            for (SignInstance signInstance : signInstances) {
                Map<String, Object> properties = new HashMap<>();
                properties.put("location", JsonHelper.locationToJson(signInstance.getLocation()));
                properties.put("target", signInstance.getTarget());
                properties.put("template", signInstance.getTemplateName());
                properties.put("dynamic", signInstance.isDynamic());
                properties.put("priority", signInstance.getPriority());
                jsonArray.add(new JSONObject(properties));
            }
            TimoCloudBukkit.getInstance().getFileManager().saveSignInstances(jsonArray);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private SignTemplate getSignTemplate(String name) {
        for (SignTemplate signTemplate : signTemplates) if (signTemplate.getName().equals(name)) return signTemplate;
        for (SignTemplate signTemplate : signTemplates)
            if (signTemplate.getName().equalsIgnoreCase(name)) return signTemplate;
        return null;
    }

    private SignTemplate templateNotFound(String name) {
        List[] lines = {
                Arrays.asList("&cCould not find"),
                Arrays.asList("&ctemplate"),
                Arrays.asList(""),
                Arrays.asList(name)
        };
        Map<String, SignLayout> layouts = new HashMap<>();
        layouts.put("Default", new SignLayout(lines, -1));
        return new SignTemplate("TemplateNotFound", layouts);
    }

    public void updateSigns() {
        if (TimoCloudAPI.getUniversalInstance().getGroups() == null) return;
        List<SignInstance> dynamicInstances = new ArrayList<>();
        for (SignInstance signInstance : signInstances) {
            if (signInstance.isDynamic()) {
                dynamicInstances.add(signInstance);
            } else {
                processStaticSign(signInstance);
            }
        }
        processDynamicSigns(dynamicInstances);
        updates++;
    }

    private void processDynamicSigns(List<SignInstance> signInstances) {
        Map<GroupObject, List<SignInstance>> groups = new HashMap<>();
        for (SignInstance signInstance : signInstances) {
            GroupObject group = TimoCloudAPI.getUniversalInstance().getGroup(signInstance.getTarget());
            groups.computeIfAbsent(group, k -> new ArrayList<>());
            groups.get(group).add(signInstance);
        }
        for (GroupObject group : groups.keySet()) processDynamicSignsPerGroup(group, groups.get(group));
    }

    private boolean isSignActive(SignInstance signInstance) {
        return signInstance.isActive() && signInstance.getLocation().getBlock().getState() instanceof Sign;
    }

    private void processDynamicSignsPerGroup(GroupObject group, List<SignInstance> signInstances) {
        if (group == null) return;
        signInstances = signInstances.stream().filter(this::isSignActive).collect(Collectors.toList());
        List<SignInstance> withPriority = signInstances.stream().filter((signInstance) -> signInstance.getPriority() != 0).collect(Collectors.toList());
        List<SignInstance> withoutPriority = signInstances.stream().filter((signInstance) -> signInstance.getPriority() == 0).collect(Collectors.toList());
        withPriority.sort(Comparator.comparing(SignInstance::getPriority));
        Integer lastPriority = null;
        int step = -1;
        for (SignInstance signInstance : withPriority) {
            if (signInstance == null) return;
            if (lastPriority == null || signInstance.getPriority() != lastPriority) {
                step++;
                lastPriority = signInstance.getPriority();
            }
            writeSign(step < group.getAllServers().size() ? group.getAllServers().get(step) : null, step < group.getAllServers().size() ? signInstance.getTemplate() : getSignTemplate("NoFreeServerFound"), signInstance);
        }

        step = -1;
        for (SignInstance signInstance : withoutPriority) {
            if (signInstance == null) return;
            step++;
            writeSign(step < group.getAllServers().size() ? group.getAllServers().get(step) : null, step < group.getAllServers().size() ? signInstance.getTemplate() : getSignTemplate("NoFreeServerFound"), signInstance);
        }
    }

    private void processStaticSign(SignInstance signInstance) {
        if (signInstance.isDynamic()) {
            TimoCloudBukkit.log("&cFatal error: Wanted to process dynamic sign as static sign. This should never happen - please report this!");
            return;
        }
        writeSign(TimoCloudAPI.getUniversalInstance().getServer(signInstance.getTarget()), signInstance.getTemplate(), signInstance);
    }

    private void writeSign(ServerObject server, SignTemplate signTemplate, SignInstance signInstance) {
        if (! isSignActive(signInstance) || server == null) return;
        if (signTemplate == null) signTemplate = templateNotFound(signInstance.getTemplateName());
        signInstance.setTargetServer(server);
        SignLayout signLayout = signTemplate.getLayout(server.getState());
        Sign sign = (Sign) signInstance.getLocation().getBlock().getState();
        for (int i = 0; i < 4; i++) {
            sign.setLine(i, replace(signLayout.getLine(i).get(signInstance.getStep() % signLayout.getLine(i).size()), server));
        }
        sign.update();
        if (signLayout.getUpdateSpeed() > 0 && updates % signLayout.getUpdateSpeed() == 0)
            signInstance.setStep(signInstance.getStep() + 1);
    }

    private Block getBlockBehindSign(Block signBlock) {
        org.bukkit.material.Sign sign = (org.bukkit.material.Sign) signBlock.getState();
        if (!signBlock.getType().equals(org.bukkit.Material.WALL_SIGN)) return null;
        switch (sign.getFacing()) {
            case NORTH:
                return signBlock.getLocation().clone().add(0, 0, -1).getBlock();
            case SOUTH:
                return signBlock.getLocation().clone().add(0, 0, 1).getBlock();
            case EAST:
                return signBlock.getLocation().clone().add(1, 0, 0).getBlock();
            case WEST:
                return signBlock.getLocation().clone().add(-1, 0, 0).getBlock();
            default:
                return null;
        }
    }

    public String replace(String string, ServerObject server) {
        return ChatColor.translateAlternateColorCodes('&', string
                .replace("%name%", server.getName())
                .replace("%server_name%", server.getName())
                .replace("%current_players%", Integer.toString(server.getCurrentPlayers()))
                .replace("%max_players%", Integer.toString(server.getMaxPlayers()))
                .replace("%state%", server.getState())
                .replace("%extra%", server.getExtra())
                .replace("%motd%", server.getMotd())
                .replace("%map%", server.getMap()));
    }

    private SignInstance getSignInstanceByLocation(Location location) {
        for (SignInstance signInstance : signInstances) if (signInstance.getLocation().equals(location)) return signInstance;
        return null;
    }

    public boolean signExists(Location location) {
        return getSignInstanceByLocation(location) != null;
    }

    public void onSignClick(Player player, Location location) {
        SignInstance signInstance = getSignInstanceByLocation(location);
        if (signInstance == null) return;
        TimoCloudBukkit.getInstance().sendPlayerToServer(player, signInstance.getTargetServer().getName());
    }

    public void addSign(Location location, String target, String template, int priority, Player player) {
        SignInstance existing = getSignInstanceByLocation(location);
        if (existing != null) signInstances.remove(existing);
        if (template.equals("")) template = "Default";
        SignTemplate signTemplate = getSignTemplate(template);
        if (signTemplate == null) {
            BukkitMessageManager.sendMessage(player, "&cError while creating sign: Could not find template &e" + template + "&c.");
            return;
        }
        boolean dynamic;
        System.out.println(Arrays.toString(TimoCloudAPI.getUniversalInstance().getGroups().toArray()));
        if (TimoCloudAPI.getUniversalInstance().getGroup(target) != null) dynamic = true;
        else if (TimoCloudAPI.getUniversalInstance().getServer(target) != null) dynamic = false;
        else {
            BukkitMessageManager.sendMessage(player, "&cError while creating sign: Could not find group or server called &e" + target + "&c.");
            return;
        }
        signInstances.add(new SignInstance(location, target, template, signTemplate, dynamic, priority));
        BukkitMessageManager.sendMessage(player, "&aSuccessfully added sign. Please check the parsed data is correct: " +
                "&6[" +
                "&etarget&6: &3 " + target +
                "&6, &eisGroup&6: &3 " + dynamic +
                "&6, &etemplate&6: &3 " + signTemplate.getName() +
                "&6, &epriority&6: &3 " + priority +
                "&6]"
        );
        saveSignInstances();
    }

    public void lockSign(Location location) {
        SignInstance signInstance = getSignInstanceByLocation(location);
        if (signInstance == null) return;
        signInstance.setActive(false);
    }

    public void unlockSign(Location location) {
        SignInstance signInstance = getSignInstanceByLocation(location);
        if (signInstance == null) return;
        signInstance.setActive(true);
    }

}
