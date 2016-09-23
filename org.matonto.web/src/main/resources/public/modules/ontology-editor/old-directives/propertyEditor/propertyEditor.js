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
        .module('propertyEditor', [])
        .directive('propertyEditor', propertyEditor);

        propertyEditor.$inject = ['$filter', 'stateManagerService', 'ontologyManagerService', 'prefixes'];

        function propertyEditor($filter, stateManagerService, ontologyManagerService, prefixes) {
            return {
                restrict: 'E',
                replace: true,
                templateUrl: 'modules/ontology-editor/old-directives/propertyEditor/propertyEditor.html',
                scope: {},
                controllerAs: 'dvm',
                controller: ['$scope', function($scope) {
                    var dvm = this;

                    dvm.sm = stateManagerService;
                    dvm.om = ontologyManagerService;
                    dvm.prefixes = prefixes;

                    dvm.checkDomain = function() {
                        if (dvm.sm.selected[prefixes.rdfs + 'domain'].length === 0) {
                            _.unset(dvm.sm.selected, prefixes.rdfs + 'domain');
                        }
                    }

                    function getLists() {
                        dvm.subClasses = $filter('removeIriFromArray')(dvm.sm.state.subClasses, dvm.sm.state.entityIRI);
                        dvm.subObjectProperties = $filter('removeIriFromArray')(dvm.sm.state.subObjectProperties,
                            dvm.sm.state.entityIRI);
                    }

                    $scope.$watch('dvm.sm.selected', getLists);
                    getLists();
                }]
            }
        }
})();