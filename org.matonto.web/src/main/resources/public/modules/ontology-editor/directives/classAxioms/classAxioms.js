/*-
 * #%L
 * org.matonto.web
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
(function() {
    'use strict';

    angular
        .module('classAxioms', [])
        .directive('classAxioms', classAxioms);

        classAxioms.$inject = ['stateManagerService', 'prefixes'];

        function classAxioms(stateManagerService, prefixes) {
            return {
                restrict: 'E',
                replace: true,
                templateUrl: 'modules/ontology-editor/directives/classAxioms/classAxioms.html',
                scope: {},
                controllerAs: 'dvm',
                controller: function() {
                    var dvm = this;
                    dvm.prefixes = prefixes;
                    dvm.sm = stateManagerService;
                    dvm.axiomList = [
                        {
                            namespace: prefixes.rdfs,
                            localName: 'subClassOf',
                            values: 'subClasses'
                        },
                        {
                            namespace: prefixes.owl,
                            localName: 'disjointWith',
                            values: 'subClasses'
                        },
                        {
                            namespace: prefixes.owl,
                            localName: 'equivalentClass',
                            values: 'subClasses'
                        }
                    ];

                    dvm.openRemoveOverlay = function(key, index) {
                        dvm.sm.key = key;
                        dvm.sm.index = index;
                        dvm.sm.showRemoveOverlay = true;
                    }
                }
            }
        }
})();