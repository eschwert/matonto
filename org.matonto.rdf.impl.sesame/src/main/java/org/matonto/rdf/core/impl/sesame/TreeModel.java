package org.matonto.rdf.core.impl.sesame;

/*-
 * #%L
 * org.matonto.rdf.impl.sesame
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
import org.matonto.rdf.api.Namespace;
import org.matonto.rdf.api.Statement;

import java.util.Collection;
import java.util.Set;

public class TreeModel extends SesameModelWrapper {

    public TreeModel() {
        setDelegate(new org.openrdf.model.impl.TreeModel());
    }

    public TreeModel(Model model) {
        this(model.getNamespaces());
        addAll(model);
    }

    public TreeModel(Collection<? extends Statement> c) {
        this();
        addAll(c);
    }

    public TreeModel(Set<Namespace> namespaces) {
        this();
        namespaces.forEach(this::setNamespace);
    }

    public TreeModel(Set<Namespace> namespaces, Collection<? extends Statement> c) {
        this(c);
        namespaces.forEach(this::setNamespace);
    }
}
