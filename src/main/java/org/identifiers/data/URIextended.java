package org.identifiers.data;

public class URIextended
{
	private final boolean obsolete;
	private final String urlPattern;

	public URIextended(String urlPattern, boolean obsolete)
	{
		super();
		this.obsolete = obsolete;
		this.urlPattern = urlPattern;
	}

	/**
	 * Getter
	 * @return the obsolete
	 */
	public Boolean isObsolete()
	{
		return this.obsolete;
	}

	public String getUri()
	{
		return urlPattern;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((urlPattern == null) ? 0 : urlPattern.hashCode());
		result = prime * result + (obsolete ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		URIextended other = (URIextended) obj;
		if (urlPattern == null)
		{
			if (other.urlPattern != null)
				return false;
		}
		else if (!urlPattern.equals(other.urlPattern))
			return false;
		if (obsolete != other.obsolete)
			return false;
		return true;
	}
}
