package cloud.timo.TimoCloud.core.managers;

import cloud.timo.TimoCloud.api.core.commands.CommandSender;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class) // We need that for every test in order to mock static methods
@PrepareForTest(TimoCloudCore.class) // We want to mock TimoCloudCore statically
public class CommandManagerTest {

    @Mock
    private TimoCloudCore timoCloudCore; // Our TimoCloudCore mock
    @Mock
    private CoreInstanceManager serverManager; // Our ServerManager mock

    private CommandManager commandManager; // Our CommandManager instance we want to test

    @Before
    public void setUp() {
        commandManager = new CommandManager(); // Create instance
        PowerMockito.mockStatic(TimoCloudCore.class); // Mock TimoCloudCore statically
        when(TimoCloudCore.getInstance()).thenReturn(timoCloudCore); // Return our mocked instance
        when(timoCloudCore.getInstanceManager()).thenReturn(serverManager); // Return mocked ServerManager
    }

    @Test
    public void onCommandReload() { // Test "reload" command
        CoreFileManager fileManager = mock(CoreFileManager.class); // Mock FileManager
        when(timoCloudCore.getFileManager()).thenReturn(fileManager); // Return FileManager mock in TimoCloudCore mock
        CommandSender commandSender = mock(CommandSender.class); // Mocked Consumer to check if protocol have been sent
        commandManager.onCommand("reload", commandSender); // Run the method we want to test
        verify(fileManager, times(1)).load(); // Check if 'load' method has been called in FileManager
        verify(serverManager, times(1)).loadEverything(); // Check if 'loadEverything' has been called in ServerManager
        verify(commandSender, times(1)).sendMessage(anyString()); // Check if message has been sent to user
    }
}