package org.identifiers.api;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.identifiers.data.URIextended;
import org.identifiers.db.Dao;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import static java.util.concurrent.TimeUnit.HOURS;

public final class ApiDao implements Dao {

    private final HttpClient hc = HttpClient.newBuilder().followRedirects(Redirect.NORMAL).build();
    private final URI uri;
    private volatile List<PrefixPatterns> prefixPatterns;

    public ApiDao() {
        super();
        try {
            uri = new URI("https://registry.api.identifiers.org/resolutionApi/getResolverDataset");
        } catch (URISyntaxException e) {
            throw new IllegalStateException(e);
        }
        update();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(new Updater(), 1, 1, HOURS);
    }

    private class Updater implements Runnable {

        @Override
        public void run() {
            update();
        }

    }

    public void update() {
        HttpRequest get = HttpRequest.newBuilder(uri).header("User-Agent", "sparql.identifiers.org cloud update").GET()
                .build();

        try {
            BodyHandler<String> bh = BodyHandlers.ofString();
            final HttpResponse<String> send = hc.send(get, bh);
            if (send.statusCode() == 200) {
                String json = send.body();
                prefixPatterns = parse(json);
            }

        } catch (IOException | InterruptedException e) {
            // Do nothing
        }

    }

    private List<PrefixPatterns> parse(String json) {
        List<PrefixPatterns> parsedPrefixPatterns = new ArrayList<>();
        var document = Configuration.defaultConfiguration().jsonProvider().parse(json);
        int length = JsonPath.read(document, "$.payload.namespaces.length()");
        for (int i = 0; i < length; i++) {
            Map<String, ?> namespace = JsonPath.read(document, "$.payload.namespaces[" + i + "]");

            final String prefix = (String) namespace.get("prefix");
            final String idPattern = (String) namespace.get("pattern");
            final Boolean namespaceEmbeddedInLui = (boolean) namespace.get("namespaceEmbeddedInLui");
            PrefixPatterns prefixPattern = new PrefixPatterns(idPattern);
            @SuppressWarnings("unchecked")
            List<Map<String, ?>> resources = (List<Map<String, ?>>) namespace.get("resources");
            for (Map<String, ?> resource : resources) {
                String urlPattern = (String) resource.get("urlPattern");
                add(prefixPattern, prefix, urlPattern, (Boolean) resource.get("deprecated"), idPattern, namespaceEmbeddedInLui);

            }
            if (!namespaceEmbeddedInLui) {
                add(prefixPattern, prefix, "https://identifiers.org/" + prefix + ":{$id}", false, idPattern, namespaceEmbeddedInLui);
                add(prefixPattern, prefix, "http://identifiers.org/" + prefix + ":{$id}", true, idPattern, namespaceEmbeddedInLui);
                add(prefixPattern, prefix, "https://identifiers.org/" + prefix + "/{$id}", true, idPattern, namespaceEmbeddedInLui);
                add(prefixPattern, prefix, "http://identifiers.org/" + prefix + "/{$id}", true, idPattern, namespaceEmbeddedInLui);
            }
            parsedPrefixPatterns.add(prefixPattern);
        }
        return parsedPrefixPatterns;
    }

    private void add(PrefixPatterns prefixPattern, final String prefix, String urlPattern, Boolean deprecated, String idPattern, Boolean namespaceEmbeddedInLui) {

        final String idString = "{$id}";
        int startIndexOf = urlPattern.indexOf(idString);
        int endIndexOf = startIndexOf + idString.length();
        //This is because CHEBI etc are part of the ID but are not seen as such
        if (namespaceEmbeddedInLui) {
            startIndexOf = startIndexOf - (prefix.length() + 1); // +1 for 
        }
        String beforeId = urlPattern.substring(0, startIndexOf);

        String afterId = urlPattern.substring(endIndexOf);
        prefixPattern.beforeAndAfterId.add(new BeforeAfterActive(beforeId, afterId, !deprecated));
    }

    @Override
    public List<URIextended> getSameAsURIs(String uri, Boolean activeflag) {
        List<URIextended> extended = new ArrayList<>();
        for (PrefixPatterns patterns : prefixPatterns) {
            for (BeforeAfterActive beforeAndAfterId : patterns.beforeAndAfterId) {

                if (beforeAndAfterId.matches(uri)) {
                    String id = beforeAndAfterId.id(uri);
                    if (patterns.idPattern == null || patterns.idPattern.matcher(id).matches()) {
                        addAll(extended, id, patterns.beforeAndAfterId, uri, activeflag);
                    }
                }
            }
        }
        return extended;
    }

    private void addAll(List<URIextended> extended, String id, List<BeforeAfterActive> beforeAndAfterIds, String uri,
            Boolean activeflag) {
        for (BeforeAfterActive beforeAndAfterId : beforeAndAfterIds) {
            String newUrl = beforeAndAfterId.beforeId + id + beforeAndAfterId.afterId;
            if (!uri.equals(newUrl) && (activeflag == null || activeflag == beforeAndAfterId.active)) {
                extended.add(new URIextended(newUrl, activeflag == null ? false : !activeflag));
            }
        }
    }

    class PrefixPatterns {

        private final Pattern idPattern;
        private final List<BeforeAfterActive> beforeAndAfterId = new ArrayList<>();

        public PrefixPatterns(String idPattern) {
            if (idPattern != null && !idPattern.isBlank()) {
                this.idPattern = Pattern.compile(idPattern);
            } else {
                this.idPattern = null;
            }
        }

        public void add(String beforeId, String afterId, boolean active) {
            beforeAndAfterId.add(new BeforeAfterActive(beforeId, afterId, active));
        }
    }

    class BeforeAfterActive {

        public BeforeAfterActive(String beforeId2, String afterId2, boolean active2) {
            beforeId = beforeId2;
            afterId = afterId2;
            active = active2;
        }

        private String beforeId;
        private String afterId;
        private boolean active;

        boolean matches(String uri1) {
            return uri1.startsWith(beforeId) && uri1.endsWith(afterId);
        }

        private String id(String uri) {
            return uri.substring(beforeId.length(), uri.length() - afterId.length());
        }
    }

	public Iterator<String> iris() {
		return prefixPatterns.stream()
				.map(p -> p.beforeAndAfterId)
				.flatMap(List::stream)
				.map(baa -> baa.beforeId+"${id}"+baa.afterId)
				.iterator();
	}	
}
