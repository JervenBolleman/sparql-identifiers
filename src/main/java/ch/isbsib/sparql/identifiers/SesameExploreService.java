package ch.isbsib.sparql.identifiers;

import java.net.URI;
import java.util.Collection;
import java.util.Set;

import uk.ac.ebi.fgpt.lode.exception.LodeException;
import uk.ac.ebi.fgpt.lode.model.RelatedResourceDescription;
import uk.ac.ebi.fgpt.lode.model.ShortResourceDescription;
import uk.ac.ebi.fgpt.lode.service.ExploreService;

public class SesameExploreService implements ExploreService {

	@Override
	public Collection<RelatedResourceDescription> getRelatedResourceByProperty(
			URI resourceUri, Set<URI> propertyUris, Set<URI> excludeTypes,
			boolean ignoreBnodes) throws LodeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<RelatedResourceDescription> getRelatedToObjects(
			URI resourceUri, Set<URI> excludePropertyUris,
			Set<URI> excludeTypes, boolean ignoreBnodes) throws LodeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<RelatedResourceDescription> getRelatedFromSubjects(
			URI resourceUri, Set<URI> excludePropertyUris,
			Set<URI> excludeTypes, boolean ignoreBnodes) throws LodeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<RelatedResourceDescription> getTypes(URI resourceUri,
			Set<URI> excludeTypes, boolean ignoreBnodes) throws LodeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<RelatedResourceDescription> getAllTypes(URI resourceUri,
			Set<URI> excludeTypes, boolean ignoreBnodes) throws LodeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ShortResourceDescription getShortResourceDescription(
			URI resourceUri, Set<URI> labelUris, Set<URI> descriptionUris)
			throws LodeException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<String> getResourceDepiction(URI uri, URI depictRelation) {
		// TODO Auto-generated method stub
		return null;
	}

}
