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
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.identifiers.data.URIextended;
import org.identifiers.db.Dao;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.JsonPath;

public class ApiDao implements Dao {
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
	List<PrefixPatterns> prefixPatterns = new ArrayList<>();
	var document = Configuration.defaultConfiguration().jsonProvider().parse(json);
	int length = JsonPath.read(document, "$.payload.namespaces.length()");
	for (int i = 0; i < length; i++) {
	    Map<String, ?> namespace = JsonPath.read(document, "$.payload.namespaces[" + i + "]");

	    final String prefix = (String) namespace.get("prefix");
	    final String idPattern = (String) namespace.get("pattern");
	    PrefixPatterns prefixPattern = new PrefixPatterns(idPattern);
	    @SuppressWarnings("unchecked")
	    List<Map<String, ?>> resources = (List<Map<String, ?>>) namespace.get("resources");
	    for (Map<String, ?> resource : resources) {
		String urlPattern = (String) resource.get("urlPattern");
		add(prefixPattern, prefix, urlPattern, (Boolean) resource.get("deprecated"), idPattern);

	    }
	    add(prefixPattern, prefix, "https://identifiers.org/" + prefix + ":{$id}", false, idPattern);
	    add(prefixPattern, prefix, "http://identifiers.org/" + prefix + ":{$id}", true, idPattern);
	    add(prefixPattern, prefix, "https://identifiers.org/" + prefix + "/{$id}", true, idPattern);
	    add(prefixPattern, prefix, "http://identifiers.org/" + prefix + "/{$id}", true, idPattern);
	    prefixPatterns.add(prefixPattern);
	}
	return prefixPatterns;
    }

    private void add(PrefixPatterns prefixPattern, final String prefix, String urlPattern, boolean deprecated,
	    String idPattern) {

	final String idString = "{$id}";
	final int indexOf = urlPattern.indexOf(idString);
	String beforeId = urlPattern.substring(0, indexOf);
	String afterId = urlPattern.substring(indexOf + idString.length());
	prefixPattern.beforeAndAfterId.add(new BeforeAfterActive(beforeId, afterId, !deprecated));
    }

    @Override
    public List<URIextended> getSameAsURIs(String uri, Boolean activeflag) {
	List<URIextended> extended = new ArrayList<>();
	for (PrefixPatterns patterns : prefixPatterns) {
	    for (BeforeAfterActive beforeAndAfterId : patterns.beforeAndAfterId) {
		final String beforeId = beforeAndAfterId.beforeId;
		final String afterId = beforeAndAfterId.afterId;
		if (uri.startsWith(beforeId) && uri.endsWith(afterId)) {
		    String id = uri.substring(beforeId.length(), uri.length() - afterId.length());
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
	    if (!uri.equals(newUrl) && (activeflag == null || activeflag == beforeAndAfterId.active))
		extended.add(new URIextended(newUrl, !activeflag));
	}
    }

    class PrefixPatterns {
	private final Pattern idPattern;
	private final List<BeforeAfterActive> beforeAndAfterId = new ArrayList<>();

	public PrefixPatterns(String idPattern) {
	    if (idPattern != null && !idPattern.isBlank())
		this.idPattern = Pattern.compile(idPattern);
	    else
		this.idPattern = null;
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
    }
}
