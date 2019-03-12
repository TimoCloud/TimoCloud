package cloud.timo.TimoCloud.common.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class HttpRequestProperty {
    @Getter
    private String key;
    @Getter
    private String value;
}
