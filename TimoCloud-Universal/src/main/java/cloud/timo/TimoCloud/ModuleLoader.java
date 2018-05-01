package cloud.timo.TimoCloud;

import cloud.timo.TimoCloud.base.TimoCloudBase;
import cloud.timo.TimoCloud.cord.TimoCloudCord;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.lib.modules.ModuleType;
import cloud.timo.TimoCloud.lib.modules.TimoCloudModule;
import cloud.timo.TimoCloud.lib.utils.options.OptionParser;
import cloud.timo.TimoCloud.lib.utils.options.OptionSet;

import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ModuleLoader {

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";

    private static final String asciiArt =
            "  _____ _                  ____ _                 _ \n" +
            " \033[48;5;117m|_   _(_)\u001B[0m_ __ ___   ___  \033[48;5;188m/ ___| |\u001B[0m ___  _   _  __\033[48;5;188m| |\u001B[0m\n" +
            "   \033[48;5;117m| |\u001B[0m \033[48;5;117m| | '_ ` _ \\\u001B[0m \033[48;5;117m/ _ \\\033[48;5;188m| |\u001B[0m   \033[48;5;188m| |/ _ \\| |\u001B[0m \033[48;5;188m| |/ _` |\u001B[0m\n" +
            "   \033[48;5;117m| |\u001B[0m \033[48;5;117m| | |\u001B[0m \033[48;5;117m| |\u001B[0m \033[48;5;117m| | (\u001B[0m_\033[48;5;117m) \033[48;5;188m| |\u001B[0m___\033[48;5;188m| | (\u001B[0m_\033[48;5;188m) | |\u001B[0m_\033[48;5;188m| | (\u001B[0m_\033[48;5;188m| |\u001B[0m\n" +
            "   \033[48;5;117m|_|\u001B[0m \033[48;5;117m|_|_|\u001B[0m \033[48;5;117m|_|\u001B[0m \033[48;5;117m|_|\\___/\u001B[0m \033[48;5;188m\\____|_|\033[48;5;188m\\___/ \033[48;5;188m\\__,_|\\__,_|\u001B[0m\n" +
            "                                                    ";

    private static final String modulePropertyName="timocloud-module";

    private static TimoCloudModule module;
    private static boolean moduleInParam = false;
    private static OptionSet options;

    private static void info(String message) {
        System.out.println(message + ANSI_RESET);
    }

    private static void severe(String message) {
        System.err.println(ANSI_RED + message + ANSI_RESET);
    }

    private static void requestInput() {
        System.out.print("> ");
    }

    public static void main(String ... args) {
        info(asciiArt);
        info("\033[1;38;5;117mTimo\u001B[1;38;5;188mCloud\u001B[0m version \u001B[1;33m" + getVersion() + "\u001B[0m by \u001B[1;32mTimoCrafter\u001B[0m.");

        parseOptions(args);

        ModuleType moduleType = getModuleType();
        info("Loading module " + moduleType + "...");
        if (! moduleInParam) info("To automatically load this module, start TimoCloud with the parameter '--module=" + moduleType + "' at the end of your java -jar command.");
        loadModule(moduleType);
    }

    private static void parseOptions(String[] args) {
        OptionParser parser = new OptionParser();
        parser.addTemplate("module", "m");
        options = parser.parse(args);
    }

    private static ModuleType getModuleType() {
        if (options.has("module")) {
            ModuleType parsed = parseFromParameter(options.get("module").getValue());
            if (parsed != null) return parsed;
        }

        String type = System.getProperty(modulePropertyName);
        if (type != null) {
            ModuleType parsed = parseFromParameter(type);
            if (parsed != null) return parsed;
        }
        info("Please choose the TimoCloud module you want to load.");
        for (int i = 1; i<=ModuleType.values().length; i++) {
            info("    [" + i + "] " + ModuleType.values()[i-1]);
        }
        info("Please enter the number or the name of the module you want to load.");
        ModuleType moduleType = null;
        Scanner scanner = new Scanner(System.in);
        requestInput();
        String read = null;
        while (true) {
            read = scanner.nextLine().trim();
            if (read.isEmpty()) {
                requestInput();
                continue;
            }
            moduleType = parseTypeFromInput(read);
            if (moduleType == null) {
                severe("Could not parse module type. Please try again and see above for a list of available modules.");
                requestInput();
                continue;
            }
            return moduleType;
        }
    }

    private static ModuleType parseFromParameter(String parameter) {
        try {
            ModuleType moduleType = ModuleType.valueOf(parameter.toUpperCase());
            moduleInParam = true;
            return moduleType;
        } catch (IllegalArgumentException e) {
            severe("Unknown module type: '" + parameter + "'");
            info("Available module types: " + getAvailableModules());
            return null;
        }
    }

    private static ModuleType parseTypeFromInput(String input) {
        try {
            return ModuleType.values()[Integer.parseInt(input)-1];
        } catch (Exception e) {}
        try {
            return ModuleType.valueOf(input.toUpperCase());
        } catch (Exception e) {
            return null;
        }
    }

    private static void loadModule(ModuleType moduleType) {
        switch (moduleType) {
            case CORE:
                module = new TimoCloudCore();
                break;
            case BASE:
                module = new TimoCloudBase();
                break;
            case CORD:
                module = new TimoCloudCord();
                break;
        }
        try {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    module.unload();
                } catch (Exception e) {
                    severe("Error while unloading module " + moduleType.name() + ": ");
                    e.printStackTrace();
                }
            }));
            module.load(options);
        } catch (Exception e) {
            severe("Error while loading module " + moduleType.name() + ": ");
            e.printStackTrace();
        }
    }

    private static String getAvailableModules() {
        return Arrays.stream(ModuleType.values()).map(ModuleType::name).collect(Collectors.joining(", "));
    }

    private static String getVersion() {
        return ModuleLoader.class.getPackage().getImplementationVersion();
    }
}
