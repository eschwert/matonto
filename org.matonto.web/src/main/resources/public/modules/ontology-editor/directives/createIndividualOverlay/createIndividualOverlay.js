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
        .module('createIndividualOverlay', [])
        .directive('createIndividualOverlay', createIndividualOverlay);

        createIndividualOverlay.$inject = ['$filter', 'ontologyManagerService', 'ontologyStateService', 'responseObj', 'prefixes'];

        function createIndividualOverlay($filter, ontologyManagerService, ontologyStateService, responseObj, prefixes) {
            return {
                restrict: 'E',
                replace: true,
                templateUrl: 'modules/ontology-editor/directives/createIndividualOverlay/createIndividualOverlay.html',
                scope: {},
                controllerAs: 'dvm',
                controller: function() {
                    var dvm = this;

                    dvm.prefixes = prefixes;
                    dvm.ro = responseObj;
                    dvm.om = ontologyManagerService;
                    dvm.sm = ontologyStateService;

                    dvm.prefix = _.get(dvm.om.getListItemById(dvm.sm.state.ontologyId), 'iriBegin',
                        dvm.om.getOntologyIRI(dvm.sm.ontology)) + _.get(dvm.om.getListItemById(dvm.sm.state.ontologyId),
                        'iriThen', '#');

                    dvm.individual = {
                        '@id': dvm.prefix,
                        '@type': [],
                        matonto: {
                            created: true
                        }
                    };

                    dvm.subClasses = _.map(dvm.sm.state.subClasses, obj => dvm.ro.getItemIri(obj));

                    dvm.nameChanged = function() {
                        if (!dvm.iriHasChanged) {
                            dvm.individual['@id'] = dvm.prefix + $filter('camelCase')(dvm.name, 'class');
                        }
                    }

                    dvm.onEdit = function(iriBegin, iriThen, iriEnd) {
                        dvm.iriHasChanged = true;
                        dvm.individual['@id'] = iriBegin + iriThen + iriEnd;
                    }

                    dvm.getItemOntologyIri = function(item) {
                        return _.get(item, 'ontologyId', dvm.sm.state.ontologyId);
                    }

                    dvm.create = function() {
                        _.set(dvm.individual, 'matonto.originalIRI', dvm.individual['@id']);
                        // update relevant lists
                        var split = $filter('splitIRI')(dvm.individual['@id']);
                        var listItem = dvm.om.getListItemById(dvm.sm.state.ontologyId);
                        _.get(listItem, 'individuals').push({namespace:split.begin + split.then, localName: split.end});
                        var classesWithIndividuals = _.get(listItem, 'classesWithIndividuals');
                        _.forEach(dvm.individual['@type'], type => {
                            _.set(listItem, 'classesWithIndividuals', _.union(classesWithIndividuals,
                                [{ entityIRI: type }]));
                        });
                        // add the entity to the ontology
                        dvm.individual['@type'].push(prefixes.owl + 'NamedIndividual');
                        dvm.om.addEntity(dvm.sm.ontology, dvm.individual);
                        _.set(_.get(listItem, 'index'), dvm.individual['@id'], dvm.sm.ontology.length - 1);
                        // select the new individual
                        dvm.sm.selectItem(dvm.individual['@id']);
                        // hide the overlay
                        dvm.sm.showCreateIndividualOverlay = false;
                    }
                }
            }
        }
})();
