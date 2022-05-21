package cloud.timo.TimoCloud.common.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class HttpRequestProperty {
    private final String key;
    private final String value;
}
