package org.matonto.rdf.orm.impl;

/*-
 * #%L
 * RDF ORM
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

import org.matonto.rdf.api.Model;
import org.matonto.rdf.api.Resource;
import org.matonto.rdf.api.ValueFactory;
import org.matonto.rdf.orm.AbstractOrmFactory;
import org.matonto.rdf.orm.OrmFactory;
import org.matonto.rdf.orm.Thing;
import org.matonto.rdf.orm.conversion.ValueConverterRegistry;

import aQute.bnd.annotation.component.Component;

/**
 * This is the core {@link OrmFactory} for {@link Thing} instances. It provides
 * a useful pattern for working with the {@link Thing} class. It is a OSGi
 * service for {@link OrmFactory} instances that return {@link Thing} typed ORM
 * objects.
 * 
 * @author bdgould
 *
 */
@Component
public class ThingFactory extends AbstractOrmFactory<Thing> {

	/**
	 * Construct a new {@link ThingFactory}.
	 */
	public ThingFactory() {
		super(Thing.class, ThingImpl.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Thing getExisting(Resource resource, Model model, ValueFactory valueFactory,
			ValueConverterRegistry valueConverterRegistry) {
		return new ThingImpl(resource, model, valueFactory, valueConverterRegistry);
	}

}
