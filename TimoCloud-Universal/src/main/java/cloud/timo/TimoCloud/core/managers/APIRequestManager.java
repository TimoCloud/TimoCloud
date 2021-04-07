package cloud.timo.TimoCloud.core.managers;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.async.APIRequest;
import cloud.timo.TimoCloud.api.async.APIRequestError;
import cloud.timo.TimoCloud.api.implementations.async.APIRequestImplementation;
import cloud.timo.TimoCloud.api.implementations.async.APIResponse;
import cloud.timo.TimoCloud.api.messages.listeners.MessageListener;
import cloud.timo.TimoCloud.api.messages.objects.AddressedPluginMessage;
import cloud.timo.TimoCloud.api.objects.ProxyChooseStrategy;
import cloud.timo.TimoCloud.api.objects.properties.ProxyGroupProperties;
import cloud.timo.TimoCloud.api.objects.properties.ServerGroupProperties;
import cloud.timo.TimoCloud.common.datatypes.TypeMap;
import cloud.timo.TimoCloud.common.encryption.RSAKeyUtil;
import cloud.timo.TimoCloud.common.json.JsonConverter;
import cloud.timo.TimoCloud.common.log.LogEntry;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.objects.*;

import java.security.PublicKey;
import java.util.*;

// Next free error code: 16
public class APIRequestManager implements MessageListener {

    @Override
    public void onPluginMessage(AddressedPluginMessage message) {
        APIRequest request = APIRequestImplementation.fromMap(message.getMessage().getData());
        APIResponse response = processRequest(request);
        TimoCloudAPI.getMessageAPI().sendMessage(new AddressedPluginMessage(message.getSender(), response.toPluginMessage()));
    }

    public <T> APIResponse<T> processRequest(APIRequest<T> request) {
        TypeMap data = new TypeMap(request.getData());
        T responseData = null;
        try {
            switch (request.getType().getTargetType()) {
                case GENERAL: {
                    switch (request.getType()) {
                        case G_CREATE_SERVER_GROUP: {
                            ServerGroupProperties serverGroupProperties;
                            try {
                                serverGroupProperties = JsonConverter.convertMapIfNecessary(data.get("value"), ServerGroupProperties.class);
                            } catch (Exception e) {
                                throw new APIRequestError("Could not deserialize ServerGroupProperties", 10, Arrays.asList(data.get("value")));
                            }
                            String name = serverGroupProperties.getName();
                            validateNotNull(name, "Name");
                            Integer onlineAmount = serverGroupProperties.getOnlineAmount();
                            validateMinimum(onlineAmount, 0, "OnlineAmount");
                            Integer maxAmount = serverGroupProperties.getMaxAmount();
                            validateMinimum(maxAmount, -1, "MaxAmount");
                            Integer ram = serverGroupProperties.getRam();
                            validateMinimum(ram, 1, "Ram");
                            Boolean isStatic = serverGroupProperties.isStatic();
                            validateNotNull(isStatic, "Static");
                            Integer priority = serverGroupProperties.getPriority();
                            validateNotNull(priority, "Priority");
                            String baseIdentifier = serverGroupProperties.getBaseIdentifier();
                            if (baseIdentifier != null && TimoCloudCore.getInstance().getInstanceManager().getBaseByIdentifier(baseIdentifier) == null) {
                                throw new APIRequestError("Base does not exist", 14);
                            }
                            Collection<String> sortOutStates = serverGroupProperties.getSortOutStates();
                            validateNotNull(sortOutStates, "SortOutStates");
                            List<String> javaParameters = serverGroupProperties.getJavaParameters();
                            validateNotNull(javaParameters, "JavaParameters");
                            List<String> spigotParameters = serverGroupProperties.getSpigotParameters();
                            validateNotNull(spigotParameters, "SpigotParameters");

                            String jrePath = serverGroupProperties.getJrePath();
                            validateNotNull(jrePath, "jrePath");

                            if (TimoCloudCore.getInstance().getInstanceManager().getGroupByName(name) != null) {
                                throw new APIRequestError("A group with this name already exists", 12, Arrays.asList(name));
                            }

                            ServerGroup serverGroup = new ServerGroup(
                                    ServerGroupProperties.generateId(),
                                    name,
                                    onlineAmount,
                                    maxAmount,
                                    ram,
                                    isStatic,
                                    priority,
                                    baseIdentifier,
                                    sortOutStates,
                                    javaParameters,
                                    spigotParameters,
                                    jrePath
                            );

                            TimoCloudCore.getInstance().getInstanceManager().createGroup(serverGroup);
                            break;
                        }
                        case G_CREATE_PROXY_GROUP: {
                            ProxyGroupProperties proxyGroupProperties;
                            try {
                                proxyGroupProperties = JsonConverter.convertMapIfNecessary(data.get("value"), ProxyGroupProperties.class);
                            } catch (Exception e) {
                                throw new APIRequestError("Could not deserialize ProxyGroupProperties", 11, Arrays.asList(data.get("value")));
                            }
                            String name = proxyGroupProperties.getName();
                            validateNotNull(name, "Name");
                            Integer maxPlayerCountPerProxy = proxyGroupProperties.getMaxPlayerCountPerProxy();
                            validateMinimum(maxPlayerCountPerProxy, 0, "MaxPlayerCountPerProxy");
                            Integer maxPlayerCount = proxyGroupProperties.getMaxPlayerCount();
                            validateMinimum(maxPlayerCount, 0, "MaxPlayerCount");
                            Integer keepFreeSlots = proxyGroupProperties.getKeepFreeSlots();
                            validateMinimum(keepFreeSlots, 0, "KeepFreeSlots");
                            Integer minAmount = proxyGroupProperties.getMinAmount();
                            validateMinimum(minAmount, 0, "MinAmount");
                            Integer maxAmount = proxyGroupProperties.getMaxAmount();
                            validateMinimum(maxAmount, -1, "MaxAmount");
                            Integer ram = proxyGroupProperties.getRam();
                            validateMinimum(ram, 1, "Ram");
                            String motd = proxyGroupProperties.getMotd();
                            validateNotNull(motd, "MOTD");
                            Boolean isStatic = proxyGroupProperties.isStatic();
                            validateNotNull(isStatic, "Static");
                            Integer priority = proxyGroupProperties.getPriority();
                            validateNotNull(priority, "Priority");
                            Collection<String> serverGroups = proxyGroupProperties.getServerGroups();
                            validateNotNull(serverGroups, "ServerGroups");
                            List<String> javaParameters = proxyGroupProperties.getJavaParameters();
                            validateNotNull(javaParameters, "JavaParameters");
                            String jrePath = proxyGroupProperties.getJrePath();
                            validateNotNull(jrePath, "jrePath");
                            if (serverGroups.isEmpty()) serverGroups = Collections.singleton("*");
                            String baseIdentifier = proxyGroupProperties.getBaseIdentifier();
                            if (baseIdentifier != null && TimoCloudCore.getInstance().getInstanceManager().getBaseByIdentifier(baseIdentifier) == null) {
                                throw new APIRequestError("Base does not exist", 14);
                            }
                            ProxyChooseStrategy proxyChooseStrategy = proxyGroupProperties.getProxyChooseStrategy();
                            validateNotNull(proxyChooseStrategy, "ProxyChooseStrategy");
                            Collection<String> hostNames = proxyGroupProperties.getHostNames();
                            validateNotNull(hostNames, "HostNames");

                            if (TimoCloudCore.getInstance().getInstanceManager().getGroupByName(name) != null) {
                                throw new APIRequestError("A group with this name already exists", 13, Arrays.asList(name));
                            }

                            ProxyGroup proxyGroup = new ProxyGroup(
                                    ProxyGroupProperties.generateId(),
                                    name,
                                    maxPlayerCountPerProxy,
                                    maxPlayerCount,
                                    keepFreeSlots,
                                    minAmount,
                                    maxAmount,
                                    ram,
                                    motd,
                                    isStatic,
                                    priority,
                                    serverGroups,
                                    baseIdentifier,
                                    proxyChooseStrategy.name(),
                                    hostNames,
                                    javaParameters,
                                    jrePath
                            );

                            TimoCloudCore.getInstance().getInstanceManager().createGroup(proxyGroup);
                            break;
                        }
                        case G_REGISTER_PUBLICKEY: {
                            String publicKeyString = data.getString("value");
                            try {
                                PublicKey publicKey = RSAKeyUtil.publicKeyFromBase64(publicKeyString);
                                TimoCloudCore.getInstance().getCorePublicKeyManager().addPermittedBaseKey(publicKey);
                            } catch (Exception e) {
                                throw new APIRequestError("Invalid public key", 15, Collections.singleton(publicKeyString));
                            }
                            break;
                        }
                    }
                    break;
                }
                case PROXY_GROUP: {
                    String proxyGroupName = request.getTarget();
                    validateNotNull(proxyGroupName, "ProxyGroupName");
                    ProxyGroup proxyGroup = TimoCloudCore.getInstance().getInstanceManager().getProxyGroupByIdentifier(proxyGroupName);
                    if (proxyGroup == null) {
                        throw new APIRequestError(String.format("ProxyGroup '%s' could not be found", proxyGroupName), 6, Collections.singleton(proxyGroupName));
                    }
                    switch (request.getType()) {
                        case PG_SET_MAX_PLAYER_COUNT: {
                            Integer amount = data.getInteger("value");
                            validateMinimum(amount, 0, "MaxPlayerCount");
                            proxyGroup.setMaxPlayerCount(amount);
                            break;
                        }
                        case PG_SET_MAX_PLAYER_COUNT_PER_PROXY: {
                            Integer amount = data.getInteger("value");
                            validateMinimum(amount, 0, "MaxPlayerCountPerProxy");
                            proxyGroup.setMaxPlayerCountPerProxy(amount);
                            break;
                        }
                        case PG_SET_KEEP_FREE_SLOTS: {
                            Integer amount = data.getInteger("value");
                            validateMinimum(amount, 0, "KeepFreeSlots");
                            proxyGroup.setKeepFreeSlots(amount);
                            break;
                        }
                        case PG_SET_MIN_AMOUNT: {
                            Integer amount = data.getInteger("value");
                            validateMinimum(amount, 0, "MaxAmount");
                            proxyGroup.setMinAmount(amount);
                            break;
                        }
                        case PG_SET_MAX_AMOUNT: {
                            Integer amount = data.getInteger("value");
                            validateMinimum(amount, 0, "MaxPlayerCount");
                            proxyGroup.setMaxAmount(amount);
                            break;
                        }
                        case PG_SET_RAM: {
                            Integer amount = data.getInteger("value");
                            validateMinimum(amount, 1, "Ram");
                            proxyGroup.setRam(amount);
                            break;
                        }
                        case PG_SET_MOTD: {
                            String value = data.getString("value");
                            validateNotNull(value, "MOTD");
                            proxyGroup.setMotd(value);
                            break;
                        }
                        case PG_SET_STATIC: {
                            Boolean value = data.getBoolean("value");
                            validateNotNull(value, "Static");
                            proxyGroup.setStatic(value);
                            break;
                        }
                        case PG_SET_PRIORITY: {
                            Integer value = data.getInteger("value");
                            validateNotNull(value, "Priority");
                            proxyGroup.setPriority(value);
                            break;
                        }
                        case PG_SET_BASE: {
                            if (!data.containsKey("value")) {
                                throw new APIRequestError("Missing value for base", 5);
                            }
                            String value = data.getString("value");
                            if (value == null) {
                                proxyGroup.setBase(null);
                                break;
                            }
                            Base base = TimoCloudCore.getInstance().getInstanceManager().getBaseByIdentifier(value);
                            if (base == null) {
                                throw new APIRequestError("Base does not exist", 14);
                            }
                            proxyGroup.setBase(base);
                            break;
                        }
                        case PG_SET_PROXY_CHOOSE_STRATEGY: {
                            String value = data.getString("value");
                            validateNotNull(value, "ProxyChooseStrategy");
                            try {
                                ProxyChooseStrategy proxyChooseStrategy = ProxyChooseStrategy.valueOf(value.toUpperCase());
                                proxyGroup.setProxyChooseStrategy(proxyChooseStrategy);
                            } catch (IllegalArgumentException e) {
                                throw new APIRequestError(String.format("Unknown ProxyChooseStrategy: %s", value));
                            }
                            break;
                        }
                        case PG_SET_HOST_NAMES: {
                            Collection<String> value = (Collection<String>) data.get("value");
                            validateNotNull(value, "HostNames");
                            proxyGroup.setHostNames(new LinkedHashSet<>(value));
                            break;
                        }
                        case PG_DELETE: {
                            TimoCloudCore.getInstance().getInstanceManager().deleteGroup(proxyGroup);
                            break;
                        }
                        case PG_SET_JAVA_START_PARAMETERS: {
                            Collection<String> value = (Collection<String>) data.get("value");
                            validateNotNull(value, "JavaParameters");
                            break;
                        }
                    }
                    TimoCloudCore.getInstance().getInstanceManager().saveProxyGroups();
                    break;
                }
                case SERVER_GROUP: {
                    String serverGroupName = request.getTarget();
                    validateNotNull(serverGroupName, "ServerGroupName");
                    ServerGroup serverGroup = TimoCloudCore.getInstance().getInstanceManager().getServerGroupByIdentifier(serverGroupName);
                    if (serverGroup == null) {
                        throw new APIRequestError(String.format("ServerGroup '%s' could not be found", serverGroupName), 7, Collections.singleton(serverGroupName));
                    }
                    switch (request.getType()) {
                        case SG_SET_MAX_AMOUNT: {
                            Integer value = data.getInteger("value");
                            validateMinimum(value, 0, "MaxAmount");
                            serverGroup.setMaxAmount(value);
                            break;
                        }
                        case SG_SET_RAM: {
                            Integer value = data.getInteger("value");
                            validateMinimum(value, 1, "Ram");
                            serverGroup.setRam(value);
                            break;
                        }
                        case SG_SET_STATIC: {
                            Boolean value = data.getBoolean("value");
                            validateNotNull(value, "Static");
                            serverGroup.setStatic(value);
                            break;
                        }
                        case SG_SET_PRIORITY: {
                            Integer value = data.getInteger("value");
                            validateNotNull(value, "Priority");
                            serverGroup.setPriority(value);
                            break;
                        }
                        case SG_SET_BASE: {
                            if (!data.containsKey("value")) {
                                throw new APIRequestError("Missing value for base", 5);
                            }
                            String value = data.getString("value");
                            if (value == null) {
                                serverGroup.setBase(null);
                                break;
                            }
                            Base base = TimoCloudCore.getInstance().getInstanceManager().getBaseByIdentifier(value);
                            if (base == null) {
                                throw new APIRequestError("Base does not exist", 14);
                            }
                            serverGroup.setBase(base);
                            break;
                        }
                        case SG_SET_ONLINE_AMOUNT: {
                            Integer value = data.getInteger("value");
                            validateMinimum(value, 0, "OnlineAmount");
                            serverGroup.setOnlineAmount(value);
                            break;
                        }
                        case SG_SET_SORT_OUT_STATES: {
                            Collection<String> value = (Collection<String>) data.get("value");
                            validateNotNull(value, "SortOutStates");
                            serverGroup.setSortOutStates(value);
                        }
                        case SG_SET_JAVA_START_PARAMETERS: {
                            Collection<String> value = (Collection<String>) data.get("value");
                            validateNotNull(value, "JavaParameters");
                        }
                        case SG_SET_SPIGOT_START_PARAMETERS: {
                            Collection<String> value = (Collection<String>) data.get("value");
                            validateNotNull(value, "SpigotParameters");
                        }
                        case SG_DELETE: {
                            TimoCloudCore.getInstance().getInstanceManager().deleteGroup(serverGroup);
                            break;
                        }
                    }
                    TimoCloudCore.getInstance().getInstanceManager().saveServerGroups();
                    break;
                }
                case SERVER: {
                    String serverIdentifier = request.getTarget();
                    validateNotNull(serverIdentifier, "ServerIdentifier");
                    Server server = TimoCloudCore.getInstance().getInstanceManager().getServerByIdentifier(serverIdentifier);
                    if (server == null) {
                        throw new APIRequestError(String.format("Server '%s' could not be found", serverIdentifier), 8, Collections.singleton(serverIdentifier));
                    }
                    switch (request.getType()) {
                        case S_EXECUTE_COMMAND: {
                            String value = data.getString("value");
                            validateNotNull(value, "Command");
                            server.executeCommand(value);
                            break;
                        }
                        case S_STOP: {
                            server.stop();
                            break;
                        }
                        case S_SET_STATE: {
                            String value = data.getString("value");
                            validateNotNull(value, "State");
                            server.setState(value);
                            break;
                        }
                        case S_SET_EXTRA: {
                            String value = data.getString("value");
                            validateNotNull(value, "Extra");
                            server.setExtra(value);
                            break;
                        }
                        case S_GET_LOG_FRACTION: {
                            Long startTime = data.getLong("startTime");
                            Long endTime = data.getLong("endTime");
                            Collection<LogEntry> entries = server.getLogStorage().queryEntries(startTime, endTime);
                        }
                    }
                    break;
                }
                case PROXY: {
                    String proxyIdentifier = request.getTarget();
                    validateNotNull(proxyIdentifier, "ProxyIdentifier");
                    Proxy proxy = TimoCloudCore.getInstance().getInstanceManager().getProxyByIdentifier(proxyIdentifier);
                    if (proxy == null) {
                        throw new APIRequestError(String.format("Proxy '%s' could not be found", proxyIdentifier), 9, Collections.singleton(proxyIdentifier));
                    }
                    switch (request.getType()) {
                        case P_EXECUTE_COMMAND: {
                            String value = data.getString("value");
                            validateNotNull(value, "Command");
                            proxy.executeCommand(value);
                            break;
                        }
                        case P_SEND_PLAYER: {
                            String playerUUID = data.getString("playerUUID");
                            String targetServer = data.getString("targetServer");
                            validateNotNull(playerUUID, "UUID");
                            validateNotNull(targetServer, "targetServer");
                            Boolean isOnGivenProxy = Boolean.valueOf(proxy.getOnlinePlayers().stream().anyMatch(playerObject -> playerObject.getUuid().toString().equals(playerUUID)));
                            responseData = (T) isOnGivenProxy;
                            if (isOnGivenProxy) {
                                proxy.sendPlayer(playerUUID, TimoCloudCore.getInstance().getInstanceManager().getServerByIdentifier(targetServer));
                            }
                            break;
                        }
                        case P_STOP: {
                            break;
                        }
                    }
                    break;
                }
            }
        } catch (APIRequestError error) {
            return new APIResponse<T>(request, error);
        } catch (Exception e) {
            return new APIResponse<T>(request, new APIRequestError(String.format("An unknown error occurred: %s", e.getMessage()), 1));
        }
        return new APIResponse<>(request, responseData);
    }

    private static void validateNotNull(Object o) throws APIRequestError {
        validateNotNull(o, null);
    }

    private static void validateNotNull(Object o, String name) throws APIRequestError {
        if (name == null) name = "Value";
        validateNotNull(o, name, String.format("%s must not be null", name));
    }

    private static void validateNotNull(Object o, String name, String message) throws APIRequestError {
        if (o == null) {
            throw new APIRequestError(message,
                    1,
                    Arrays.asList(name));
        }
    }

    private static void validateMinimum(Number number, Number minimum) throws APIRequestError {
        validateMinimum(number, minimum, null);
    }

    private static void validateMinimum(Number number, Number minimum, String name) throws APIRequestError {
        if (name == null) name = "Value";
        validateNotNull(number, name);
        validateNotNull(minimum, String.format("Minimum for %s", name));
        if (number.doubleValue() < minimum.doubleValue()) {
            throw new APIRequestError(String.format("%s must not be smaller than %s (actual value: %s)", name, minimum, number),
                    2,
                    Arrays.asList(number, minimum));
        }
    }

    private static void validateMaximum(Number number, Number maximum) throws APIRequestError {
        validateMinimum(number, maximum, null);
    }

    private static void validateMaximum(Number number, Number maximum, String name) throws APIRequestError {
        if (name == null) name = "Value";
        validateNotNull(number, name);
        validateNotNull(maximum, String.format("Maximum for %s", name));
        if (number.doubleValue() > maximum.doubleValue()) {
            throw new APIRequestError(String.format("%s must not be greater than %s (actual value: %s)", name, maximum, number),
                    3,
                    Arrays.asList(number, maximum));
        }
    }

    private static void validateRange(Number number, Number minimum, Number maximum) throws APIRequestError {
        validateRange(number, minimum, maximum, null);
    }

    private static void validateRange(Number number, Number minimum, Number maximum, String name) throws APIRequestError {
        if (name == null) name = "Value";
        validateNotNull(number, name);
        validateNotNull(minimum, String.format("Minimum for %s", name));
        validateNotNull(maximum, String.format("Maximum for %s", name));
        if (number.doubleValue() < minimum.doubleValue() || number.doubleValue() > maximum.doubleValue()) {
            throw new APIRequestError(String.format("%s must be in range [%s;%s] %s (actual value: %s)", name, minimum, maximum, number),
                    4,
                    Arrays.asList(number, minimum, maximum));
        }
    }

}
