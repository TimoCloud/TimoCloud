package cloud.timo.TimoCloud.core.utils.completers;

import cloud.timo.TimoCloud.core.TimoCloudCore;
import cloud.timo.TimoCloud.core.objects.Server;
import cloud.timo.TimoCloud.core.objects.ServerGroup;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ServerNameCompleter implements Completer {

    @Override
    public void complete(LineReader lineReader, ParsedLine parsedLine, List<Candidate> list) {
        list.addAll(TimoCloudCore.getInstance().getInstanceManager().getServerGroups().stream().map(ServerGroup::getServers).flatMap(Collection::stream).map(Server::getName).map(Candidate::new).collect(Collectors.toList()));
    }
}
