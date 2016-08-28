package org.matonto.rdf.orm.conversion.impl;

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

import org.matonto.rdf.api.Value;
import org.matonto.rdf.orm.Thing;
import org.matonto.rdf.orm.conversion.AbstractValueConverter;
import org.matonto.rdf.orm.conversion.ValueConversionException;
import org.matonto.rdf.orm.conversion.ValueConverter;

import aQute.bnd.annotation.component.Component;

/**
 * {@link ValueConverter} for {@link Short}s.
 * 
 * @author bdgould
 *
 */
@Component(provide = ValueConverter.class)
public class ShortValueConverter extends AbstractValueConverter<Short> {

	/**
	 * Construct a new {@link ShortValueConverter}.
	 */
	public ShortValueConverter() {
		super(Short.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Short convertValue(Value value, Thing thing, Class<? extends Short> desiredType)
			throws ValueConversionException {
		try {
			return Short.parseShort(value.stringValue());
		} catch (NumberFormatException e) {
			throw new ValueConversionException("Issue getting short value from statement", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Value convertType(Short type, Thing thing) throws ValueConversionException {
		return getValueFactory(thing).createLiteral(type);
	}

}