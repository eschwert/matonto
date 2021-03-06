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
        .module('ontologyCloseOverlay', [])
        .directive('ontologyCloseOverlay', ontologyCloseOverlay);

        ontologyCloseOverlay.$inject = ['ontologyManagerService', 'ontologyStateService'];

        function ontologyCloseOverlay(ontologyManagerService, ontologyStateService) {
            return {
                restrict: 'E',
                replace: true,
                templateUrl: 'modules/ontology-editor/directives/ontologyCloseOverlay/ontologyCloseOverlay.html',
                scope: {},
                controllerAs: 'dvm',
                controller: function() {
                    var dvm = this;

                    dvm.om = ontologyManagerService;
                    dvm.sm = ontologyStateService;

                    dvm.saveThenClose = function() {
                        var ontology = dvm.om.getOntologyById(dvm.sm.ontologyIdToClose);
                        dvm.om.saveChanges(dvm.sm.ontologyIdToClose, dvm.sm.getUnsavedEntities(ontology),
                            dvm.sm.getCreatedEntities(ontology), dvm.sm.getState(dvm.sm.ontologyIdToClose).deletedEntities)
                            .then(newId => {
                                dvm.sm.afterSave(newId);
                                dvm.close();
                            }, errorMessage => {
                                dvm.error = errorMessage;
                            });
                    }

                    dvm.close = function() {
                        dvm.sm.deleteState(dvm.sm.ontologyIdToClose);
                        dvm.om.closeOntology(dvm.sm.ontologyIdToClose);
                        dvm.sm.showCloseOverlay = false;
                    }
                }
            }
        }
})();
