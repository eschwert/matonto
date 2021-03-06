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

        classAxioms.$inject = ['ontologyStateService', 'propertyManagerService', 'responseObj', 'prefixes'];

        function classAxioms(ontologyStateService, propertyManagerService, responseObj, prefixes) {
            return {
                restrict: 'E',
                replace: true,
                templateUrl: 'modules/ontology-editor/directives/classAxioms/classAxioms.html',
                scope: {},
                controllerAs: 'dvm',
                controller: function() {
                    var dvm = this;
                    dvm.sm = ontologyStateService;
                    dvm.pm = propertyManagerService;
                    dvm.ro = responseObj;

                    dvm.openRemoveOverlay = function(key, index) {
                        dvm.key = key;
                        dvm.index = index;
                        dvm.showRemoveOverlay = true;
                    }

                    dvm.updateHierarchy = function(axiom, values) {
                        if (_.get(axiom, 'localName') === 'subClassOf') {
                            _.forEach(values, value => {
                                dvm.sm.addEntityToHierarchy(dvm.sm.listItem.classHierarchy,
                                    dvm.sm.selected.matonto.originalIRI, dvm.sm.listItem.classIndex,
                                    dvm.ro.getItemIri(value));
                            });
                        }
                    }

                    dvm.removeFromHierarchy = function(axiomObject) {
                        if (prefixes.rdfs + 'subClassOf' === dvm.key) {
                            dvm.sm.deleteEntityFromParentInHierarchy(dvm.sm.listItem.classHierarchy,
                                dvm.sm.selected.matonto.originalIRI, axiomObject['@id'], dvm.sm.listItem.classIndex);
                        }
                    }
                }
            }
        }
})();
