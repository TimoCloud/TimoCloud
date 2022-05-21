package cloud.timo.TimoCloud.bukkit.signs;

import cloud.timo.TimoCloud.api.objects.ServerObject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Location;

@RequiredArgsConstructor
public class SignInstance {
    @Getter
    private final Location location;
    @Getter
    private final String target;
    @Getter
    private final String templateName;
    @Getter
    private final SignTemplate template;
    @Getter
    private final boolean dynamic;
    @Getter
    private final int priority;
    @Getter
    @Setter
    private int step = 0;
    @Getter
    @Setter
    private ServerObject targetServer;
    @Getter
    @Setter
    private boolean active = true;
}
