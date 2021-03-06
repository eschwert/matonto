package org.matonto.ontology.core.impl.owlapi.datarange;

/*-
 * #%L
 * org.matonto.ontology.core.impl.owlapi
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2016 iNovex Information Systems, Inc.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import org.matonto.ontology.core.api.datarange.DataOneOf;
import org.matonto.ontology.core.api.types.DataRangeType;
import org.matonto.rdf.api.Literal;


public class SimpleDataOneOf implements DataOneOf {	
	
	private Set<Literal> values;
	
	public SimpleDataOneOf(@Nonnull Set<Literal> values)	
	{
		this.values = new HashSet<Literal>(values);
	}
	
	
	@Override
	public boolean isDatatype() 
	{
		return false;
	}

	
	@Override
	public DataRangeType getDataRangeType()
	{
		return DataRangeType.DATA_ONE_OF;
	}

	
	@Override
	public Set<Literal> getValues() 
	{
		return new HashSet<Literal>(values);
	}
	
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) 
		    return true;
		
		if ((obj instanceof DataOneOf)) {
			DataOneOf other = (DataOneOf)obj;
			return getValues().equals(other.getValues());
		}
		
		return false;
	}

}
