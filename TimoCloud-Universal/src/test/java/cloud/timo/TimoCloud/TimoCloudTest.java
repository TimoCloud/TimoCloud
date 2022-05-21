package cloud.timo.TimoCloud;

import cloud.timo.TimoCloud.base.TimoCloudBase;
import cloud.timo.TimoCloud.bukkit.TimoCloudBukkit;
import cloud.timo.TimoCloud.bungeecord.TimoCloudBungee;
import cloud.timo.TimoCloud.cord.TimoCloudCord;
import cloud.timo.TimoCloud.core.TimoCloudCore;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collection;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({
        TimoCloudCore.class,
        TimoCloudBase.class,
        TimoCloudBukkit.class,
        TimoCloudBungee.class,
        TimoCloudCord.class
})
@Ignore
public class TimoCloudTest {

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private TimoCloudCore core;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private TimoCloudBase base;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private TimoCloudBukkit bukkit;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private TimoCloudBungee bungee;
    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private TimoCloudCord cord;

    @Before
    public void setUpBasics() throws Exception {
        MockitoAnnotations.initMocks(this);

        mockStatic(TimoCloudCore.class);
        mockStatic(TimoCloudBase.class);
        mockStatic(TimoCloudBukkit.class);
        mockStatic(TimoCloudBungee.class);
        mockStatic(TimoCloudCord.class);

        when(TimoCloudCore.getInstance()).thenReturn(core);
        when(TimoCloudBase.getInstance()).thenReturn(base);
        when(TimoCloudBukkit.getInstance()).thenReturn(bukkit);
        when(TimoCloudBungee.getInstance()).thenReturn(bungee);
        when(TimoCloudCord.getInstance()).thenReturn(cord);
    }

    public void expectCoreExeption() {
        verify(getCore(), atLeastOnce()).severe(any(Throwable.class));
    }

    public void expectNoException() {
        verify(getCore(), never()).severe(any(Throwable.class));
        verify(getCore(), never()).severe(anyString());

        verify(getBase(), never()).severe(any(Throwable.class));
        verify(getBase(), never()).severe(anyString());

        verify(getBukkit(), never()).severe(any(Throwable.class));
        verify(getBukkit(), never()).severe(anyString());

        verify(getBungee(), never()).severe(any(Throwable.class));
        verify(getBungee(), never()).severe(anyString());

        verify(getCord(), never()).severe(any(Throwable.class));
        verify(getCord(), never()).severe(anyString());
    }

    public void assertCollectionEqualsInAnyOrder(Collection a, Collection b) {
        assertTrue("First collection does not contain all elements of second collection", a.containsAll(b));
        assertTrue("Second collection does not contain all elements of first collection", b.containsAll(a));
    }

    public TimoCloudCore getCore() {
        return core;
    }

    public TimoCloudBase getBase() {
        return base;
    }

    public TimoCloudBukkit getBukkit() {
        return bukkit;
    }

    public TimoCloudBungee getBungee() {
        return bungee;
    }

    public TimoCloudCord getCord() {
        return cord;
    }

}
