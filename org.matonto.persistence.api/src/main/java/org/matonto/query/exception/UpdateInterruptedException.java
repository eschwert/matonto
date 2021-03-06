package org.matonto.query.exception;

/*-
 * #%L
 * org.matonto.persistence.api
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

import org.matonto.exception.MatOntoException;

public class UpdateInterruptedException extends MatOntoException {

    public UpdateInterruptedException() {
        super();
    }

    public UpdateInterruptedException(String message) {
        super(message);
    }

    public UpdateInterruptedException(Throwable exception) {
        super(exception);
    }

    public UpdateInterruptedException(String message, Throwable exception) {
        super(message, exception);
    }

}
