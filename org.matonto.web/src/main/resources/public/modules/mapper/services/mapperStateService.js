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
        /**
         * @ngdoc overview
         * @name mapperState
         * @requires delimitedManager
         *
         * @description 
         * The `mapperState` module only provides the `mapperStateService` service which
         * contains various variables to hold the state of the mapping tool page and 
         * utility functions to update those variables.
         */
        .module('mapperState', ['delimitedManager'])
        /**
         * @ngdoc service
         * @name mapperState.service:mapperStateService
         * @requires prefixes.service:prefixes
         * @requires mappingManager.service:mappingManagerService
         * @requires ontologyManager.service:ontologyManagerService
         * @requires delimitedManager.service:delimitedManagerService
         *
         * @description 
         * `mapperStateService` is a service which contains various variables to hold the 
         * state of the mapping tool page and utility functions to update those variables.
         */
        .service('mapperStateService', mapperStateService);

        mapperStateService.$inject = ['prefixes', 'mappingManagerService', 'ontologyManagerService', 'delimitedManagerService'];

        function mapperStateService(prefixes, mappingManagerService, ontologyManagerService, delimitedManagerService) {
            var self = this;
            var cachedOntologyId = '';
            var cachedSourceOntologies = undefined;
            var originalMappingName = '';
            var mm = mappingManagerService,
                om = ontologyManagerService,
                dm = delimitedManagerService;

            // Static step indexes
            self.selectMappingStep = 0;
            self.fileUploadStep = 1;
            self.editMappingStep = 2;

            self.mappingSearchString = '';
            self.displayCreateMapping = false;
            self.displayDownloadMapping = false;
            self.displayMappingConfig = false;
            self.displayPropMappingOverlay = false;
            self.displayDeletePropConfirm = false;
            self.displayDeleteClassConfirm = false;

            /**
             * @ngdoc property
             * @name editMapping
             * @propertyOf mapperState.service:mapperStateService
             * @type {boolean}
             *
             * @description 
             * `editMapping` holds a boolean indicating whether or not the mapping page is
             * editing a mapping
             */
            self.editMapping = false;
            /**
             * @ngdoc property
             * @name newMapping
             * @propertyOf mapperState.service:mapperStateService
             * @type {boolean}
             *
             * @description 
             * `newMapping` holds a boolean indicating whether or not the mapping page is
             * creating a new mapping
             */
            self.newMapping = false;
            /**
             * @ngdoc property
             * @name step
             * @propertyOf mapperState.service:mapperStateService
             * @type {number}
             *
             * @description 
             * `step` holds a number indicating what step in the mapping process the mapping
             * page is currently on
             */
            self.step = 0;
            /**
             * @ngdoc property
             * @name invalidProps
             * @propertyOf mapperState.service:mapperStateService
             * @type {Object[]}
             *
             * @description 
             * `invalidProps` holds an array of property objects from 
             * {@link ontologyManager.service:ontologyManagerService ontologyManagerService}
             * that are mapped to non-existent column indexes in the currently selected 
             * {@link mappingManager.service:mappingManagerService#mapping mapping}.
             */
            self.invalidProps = [];
            /**
             * @ngdoc property
             * @name availableColumns
             * @propertyOf mapperState.service:mapperStateService
             * @type {string[]}
             *
             * @description 
             * `availableColumns` holds an array of the header strings for all the columns
             * that haven't been mapped yet in the currently selected 
             * {@link mappingManager.service:mappingManagerService#mapping mapping}.
             */
            self.availableColumns = [];
            /**
             * @ngdoc property
             * @name availableProps
             * @propertyOf mapperState.service:mapperStateService
             * @type {Object[]}
             *
             * @description 
             * `availableProps` holds an array of property objects from 
             * {@link ontologyManager.service:ontologyManagerService ontologyManagerService}
             * that haven't been mapped yet in the currently selected 
             * {@link mappingManager.service:mappingManagerService#mapping mapping} for the current selected
             * {@link mappingManager.service:mappingManagerService#selectedClassMappingId class mapping}.
             */
            self.availableProps = [];
            /**
             * @ngdoc property
             * @name availablePropsByClass
             * @propertyOf mapperState.service:mapperStateService
             * @type {Object}
             *
             * @description 
             * `availablePropsByClass` holds a object with keys for the class mappings in the currently selected
             * {@link mappingManager.service:mappingManagerService#mapping mapping} and values indicating whether
             * the class mapping still has properties available to map.
             */
            self.availablePropsByClass = {};
            /**
             * @ngdoc property
             * @name openedClasses
             * @propertyOf mapperState.service:mapperStateService
             * @type {string[]}
             *
             * @description 
             * `openedClasses` holds an array of class mapping ids indicating which ones should be 
             * opened in the {@link classList.directive:classList classList}
             */
            self.openedClasses = [];
            /**
             * @ngdoc property
             * @name invalidOntology
             * @propertyOf mapperState.service:mapperStateService
             * @type {boolean}
             *
             * @description 
             * `invalidOntology` holds a boolean indicating whether or not the source ontology for the 
             * currently selected {@link mappingManager.service:mappingManagerService#mapping mapping} is 
             * incompatible.
             */
            self.invalidOntology = false;
            /**
             * @ngdoc property
             * @name editMappingName
             * @propertyOf mapperState.service:mapperStateService
             * @type {boolean}
             *
             * @description 
             * `editMappingName` holds a boolean indicating whether or not the mapping page is
             * editing the mapping name
             */
            self.editMappingName = false;
            /**
             * @ngdoc property
             * @name displayCancelConfirm
             * @propertyOf mapperState.service:mapperStateService
             * @type {boolean}
             *
             * @description 
             * `displayCancelConfirm` holds a boolean indicating whether or not the cancel confirm 
             * overlay should be shown.
             */
            self.displayCancelConfirm = false;
            /**
             * @ngdoc property
             * @name displayNewMappingConfirm
             * @propertyOf mapperState.service:mapperStateService
             * @type {boolean}
             *
             * @description 
             * `displayNewMappingConfirm` holds a boolean indicating whether or not the create new 
             * mapping confirm overlay should be shown.
             */
            self.displayNewMappingConfirm = false;
            /**
             * @ngdoc property
             * @name changeOntology
             * @propertyOf mapperState.service:mapperStateService
             * @type {boolean}
             *
             * @description 
             * `changeOntology` holds a boolean indicating whether or not the mapping page is
             * changing the source ontology for the currently selected 
             * {@link mappingManager.service:mappingManagerService#mapping mapping}.
             */
            self.changeOntology = false;
            /**
             * @ngdoc property
             * @name displayDeleteEntityConfirm
             * @propertyOf mapperState.service:mapperStateService
             * @type {boolean}
             *
             * @description 
             * `displayDeleteEntityConfirm` holds a boolean indicating whether or not the delete 
             * entity confirm overlay should be shown.
             */
            self.displayDeleteEntityConfirm = false;
            /**
             * @ngdoc property
             * @name displayDeleteMappingConfirm
             * @propertyOf mapperState.service:mapperStateService
             * @type {boolean}
             *
             * @description 
             * `displayDeleteMappingConfirm` holds a boolean indicating whether or not the delete 
             * mapping confirm overlay should be shown.
             */
            self.displayDeleteMappingConfirm = false;
            /**
             * @ngdoc property
             * @name previewOntology
             * @propertyOf mapperState.service:mapperStateService
             * @type {boolean}
             *
             * @description 
             * `previewOntology` holds a boolean indicating whether or not the 
             * {@link ontologyPreviewOverlay.directive:ontologyPreviewOverlay ontologyPreviewOverlay}
             * should be shown.
             */
            self.previewOntology = false;
            /**
             * @ngdoc property
             * @name editIriTemplate
             * @propertyOf mapperState.service:mapperStateService
             * @type {boolean}
             *
             * @description 
             * `editIriTemplate` holds a boolean indicating whether or not the
             * {@link iriTemplateOverlay.directive:iriTemplateOverlay iriTemplateOverlay} should be
             * shown.
             */
            self.editIriTemplate = false;

            /**
             * @ngdoc property
             * @name selectedClassMappingId
             * @propertyOf mapperState.service:mapperStateService
             * @type {string}
             *
             * @description 
             * `selectedClassMappingId` holds a string with the IRI of the currently selected
             * class mapping.
             */
            self.selectedClassMappingId = '';
            /**
             * @ngdoc property
             * @name selectedPropMappingId
             * @propertyOf mapperState.service:mapperStateService
             * @type {string}
             *
             * @description 
             * `selectedPropMappingId` holds a string with the IRI of the currently selected
             * property mapping.
             */
            self.selectedPropMappingId = '';
            /**
             * @ngdoc property
             * @name selectedProp
             * @propertyOf mapperState.service:mapperStateService
             * @type {Object}
             *
             * @description 
             * `selectedProp` holds the property object from {@link ontologyManager.service:ontologyManagerService ontologyManagerService}
             * of the currently selected property from the currently selected class 
             */
            self.selectedProp = undefined;
            /**
             * @ngdoc property
             * @name selectedColumn
             * @propertyOf mapperState.service:mapperStateService
             * @type {string}
             *
             * @description 
             * `selectedColumn` holds a string with the header of the currently selected column
             */
            self.selectedColumn = '';
            /**
             * @ngdoc property
             * @name newProp
             * @propertyOf mapperState.service:mapperStateService
             * @type {boolean}
             *
             * @description 
             * `newProp` holds a boolean indicating whether or not the a new property is being mapped
             */
            self.newProp = false;
            /**
             * @ngdoc property
             * @name deleteId
             * @propertyOf mapperState.service:mapperStateService
             * @type {string}
             *
             * @description 
             * `deleteId` holds a string with the IRI of the mapping entity to be deleted.
             */
            self.deleteId = '';

            /**
             * @ngdoc method
             * @name initialize
             * @methodOf mapperState.service:mapperStateService
             * 
             * @description 
             * Sets the main state variables back to their default values.
             */
            self.initialize = function() {
                self.editMapping = false;
                self.newMapping = false;
                self.step = 0;
                self.invalidProps = [];
                self.availableColumns = [];
                self.availableProps = [];
                originalMappingName = '';
                self.openedClasses = [];
                self.availablePropsByClass = {};
            }
            /**
             * @ngdoc method
             * @name resetEdit
             * @methodOf mapperState.service:mapperStateService
             * 
             * @description 
             * Sets the edit related state variables back to their default values.
             */
            self.resetEdit = function() {
                self.selectedClassMappingId = '';
                self.selectedPropMappingId = '';
                self.selectedProp = undefined;
                self.selectedColumn = '';
                self.newProp = false;
            }
            /**
             * @ngdoc method
             * @name createMapping
             * @methodOf mapperState.service:mapperStateService
             *
             * @description 
             * Sets the state variables, {@link mappingManager.service:mappingManagerService#mapping mapping}, and
             * {@link mappingManager.service:mappingManagerService#sourceOntologies sourceOntologies} to indicate creating
             * a new mapping.
             */
            self.createMapping = function() {
                self.editMapping = true;
                self.newMapping = true;
                // self.step = 0;
                mm.mapping = {
                    name: '',
                    jsonld: []
                };
                mm.sourceOntologies = [];
                // self.editMappingName = true;
                self.resetEdit();
                self.availablePropsByClass = {};
            }
            /**
             * @ngdoc method
             * @name cacheSourceOntologies
             * @methodOf mapperState.service:mapperStateService
             *
             * @description 
             * Saves the current values of the source ontology id from 
             * {@link mappingManager.service:mappingManagerService#mapping mapping} and 
             * {@link mappingManager.service:mappingManagerService#sourceOntologies sourceOntologies}.
             */
            self.cacheSourceOntologies = function() {
                cachedOntologyId = mm.getSourceOntologyId(mm.mapping.jsonld);
                cachedSourceOntologies = angular.copy(mm.sourceOntologies);
            }
            /**
             * @ngdoc method
             * @name clearCachedSourceOntologies
             * @methodOf mapperState.service:mapperStateService
             *
             * @description 
             * Clears the saved values of the source ontology id and source ontologies.
             */
            self.clearCachedSourceOntologies = function() {
                cachedOntologyId = '';
                cachedSourceOntologies = undefined;
            }
            /**
             * @ngdoc method
             * @name restoreCachedSourceOntologies
             * @methodOf mapperState.service:mapperStateService
             *
             * @description 
             * Sets the saved values of the source ontology id and source ontologies back to
             * {@link mappingManager.service:mappingManagerService#mapping mapping} and 
             * {@link mappingManager.service:mappingManagerService#sourceOntologies sourceOntologies}.
             */
            self.restoreCachedSourceOntologies = function() {
                mm.sourceOntologies = angular.copy(cachedSourceOntologies);
                mm.setSourceOntology(mm.mapping.jsonld, cachedOntologyId);
                self.clearCachedSourceOntologies();
            }
            /**
             * @ngdoc method
             * @name getCachedSourceOntologyId
             * @methodOf mapperState.service:mapperStateService
             *
             * @description 
             * Gets the saved value of the source ontology id.
             */
            self.getCachedSourceOntologyId = function() {
                return cachedOntologyId;
            }
            /**
             * @ngdoc method
             * @name getCachedSourceOntologies
             * @methodOf mapperState.service:mapperStateService
             *
             * @description 
             * Gets the saved source ontologies.
             */
            self.getCachedSourceOntologies = function() {
                return cachedSourceOntologies;
            }
            /**
             * @ngdoc method
             * @name getMappedColumns
             * @methodOf mapperState.service:mapperStateService
             *
             * @description 
             * Finds the column headers matching all of the column indexes that haven't
             * been mapped to data mappings yet in the currently selected {@link mappingManager.service:mappingManagerService#mapping mapping}.
             * 
             * @return {string[]} an array of header names of columns that haven't been 
             * mapped yet
             */
            self.getMappedColumns = function() {
                return _.chain(mm.getAllDataMappings(mm.mapping.jsonld))
                    .map(dataMapping => parseInt(_.get(dataMapping, "['" + prefixes.delim + "columnIndex'][0]['@value']", '0'), 10))
                    .map(index => _.get(dm.filePreview.headers, index))
                    .value();
            }
            /**
             * @ngdoc method
             * @name updateAvailableColumns
             * @methodOf mapperState.service:mapperStateService
             *
             * @description 
             * Updates the list of {@link mapperState.service:mapperStateService#availableColumns "available columns"}
             * for the currently selected {@link mappingManager.service:mappingManagerService#mapping mapping} 
             * and saved file preview. If a data property mapping has been selected, adds the header 
             * corresponding to its mapped column index from the available columns.
             */
            self.updateAvailableColumns = function() {
                var mappedColumns = self.getMappedColumns();
                if (self.selectedPropMappingId) {
                    var propMapping = _.find(mm.mapping.jsonld, {'@id': self.selectedPropMappingId});
                    var index = parseInt(_.get(propMapping, "['" + prefixes.delim + "columnIndex'][0]['@value']", '0'), 10);
                    _.pull(mappedColumns, dm.filePreview.headers[index]);
                }
                self.availableColumns = _.difference(dm.filePreview.headers, mappedColumns);
            }
            /**
             * @ngdoc method
             * @name updateAvailableProps
             * @methodOf mapperState.service:mapperStateService
             *
             * @description 
             * Updates the list of {@link mapperState.service:mapperStateService#availableProps available properties}
             * for the currently selected {@link mappingManager.service:mappingManagerService#mapping mapping} and
             * class mapping with the passed id.
             *
             * @param {string} classMappingId The id of the class mapping to retrieve the avilable properties of
             */
            self.updateAvailableProps = function(classMappingId) {
                self.availableProps = self.getAvailableProps(classMappingId);
            }

            /**
             * @ngdoc method
             * @name hasAvailableProps
             * @methodOf mapperState.service:mapperStateService
             * 
             * @description 
             * Returns the boolean indicating whether a class mapping has available properties to map.
             * 
             * @param {string} classMappingId The id of the class mapping to check
             * @return {boolean} True if there are available properties to map for the class mapping;
             * false otherwise.
             */
            self.hasAvailableProps = function(classMappingId) {
                return _.get(self.availablePropsByClass, encodeURIComponent(classMappingId), false);
            }

            /**
             * @ngdoc method
             * @name setAvailableProps
             * @methodOf mapperState.service:mapperStateService
             * 
             * @description 
             * Sets a boolean value for a class mapping in {@link mapperState.service:mapperStateService#availablePropsByClass availablePropsByClass} 
             * indicating whether the class mapping with the passed id has available properties to map.
             * 
             * @param {string} classMappingId The id of the class mapping to set the boolean value of
             */
            self.setAvailableProps = function(classMappingId) {
                _.set(self.availablePropsByClass, encodeURIComponent(classMappingId), self.getAvailableProps(classMappingId).length > 0);
            }

            /**
             * @ngdoc method
             * @name getAvailableProps
             * @methodOf mapperState.service:mapperStateService
             *
             * @description 
             * Retrieves an array of property objects from the current {@link mappingManager.service:mappingManagerService#mapping mapping}
             * representing the properties that the class mapping with the passed id hasn't used yet.
             * 
             * @param {string} classMappingId The id of the class mapping to retrieve available properties of
             * @return {Object[]} An array of property objects for the properties that haven't been mapped yet 
             * for the class mapping.
             */
            self.getAvailableProps = function(classMappingId) {
                var mappedProps = _.map(mm.getPropMappingsByClass(mm.mapping.jsonld, classMappingId), "['" + prefixes.delim + "hasProperty'][0]['@id']");
                var classId = mm.getClassIdByMappingId(mm.mapping.jsonld, classMappingId);
                var props = self.getClassProps(mm.sourceOntologies, classId);
                return _.filter(props, prop => mappedProps.indexOf(prop['@id']) < 0);
            }
            
            self.getClassProps = function(ontologies, classId) {
                var props = [];
                _.forEach(ontologies, ontology => {
                    var classProps = om.getClassProperties(ontology.entities, classId);
                    var noDomainProps = om.getNoDomainProperties(ontology.entities);
                    props = _.union(props, classProps, noDomainProps);
                });
                return props;
            }
            /**
             * @ngdoc method
             * @name changedMapping
             * @methodOf mapperState.service:mapperStateService
             *
             * @description 
             * Tests whether the currently selected {@link mappingManager.service:mappingManagerService#mapping mapping}, 
             * that has just been updated, is a saved mapping and if so, adds a timestamp to the end of the name.
             */
            self.changedMapping = function() {
                if (!self.newMapping && !originalMappingName) {
                    originalMappingName = mm.mapping.name;
                    mm.mapping.name = originalMappingName + '_' + Math.floor(Date.now() / 1000);
                }
            }
        }
})();