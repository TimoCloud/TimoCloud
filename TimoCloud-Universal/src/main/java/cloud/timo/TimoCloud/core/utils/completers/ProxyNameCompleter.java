package cloud.timo.TimoCloud.core.utils.completers;

import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.objects.Proxy;
import cloud.timo.TimoCloud.core.objects.ProxyGroup;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ProxyNameCompleter implements Completer {
    @Override
    public void complete(LineReader lineReader, ParsedLine parsedLine, List<Candidate> list) {
        list.addAll(TimoCloudCore.getInstance().getInstanceManager().getProxyGroups().stream().map(ProxyGroup::getProxies).flatMap(Collection::stream).map(Proxy::getName).map(Candidate::new).collect(Collectors.toList()));
    }
}
