(function() {
    'use strict';

    angular
        /**
         * @ngdoc overview
         * @name mappingPreview
         * @requires  ontologyManager
         * @requires  mappingManager
         * @requires  mapperState
         *
         * @description 
         * The `mappingPreview` module only provides the `mappingPreview` directive which creates
         * a "boxed" area with a preview of a mapping and a button to select it.
         */
        .module('mappingPreview', ['mappingManager', 'mapperState', 'ontologyManager'])
        /**
         * @ngdoc directive
         * @name mappingPreview.directive:mappingPreview
         * @scope
         * @restrict E
         * @requires  $q
         * @requires  ontologyManager.service:ontologyManagerService
         * @requires  mappingManager.service:mappingManagerService
         * @requires  mapperState.service:mapperStateService
         *
         * @description 
         * `mappingPreview` is a directive that creates a "boxed" div with a preview of a mapping with 
         * its source ontology and all its mapped classes and properties. It also provides a button to
         * select the mapping for mapping delimited data. The directive is replaced by the contents of 
         * its template.
         */
        .directive('mappingPreview', mappingPreview);

        mappingPreview.$inject = ['$q', 'mappingManagerService', 'mapperStateService', 'ontologyManagerService'];

        function mappingPreview($q, mappingManagerService, mapperStateService, ontologyManagerService) {
            return {
                restrict: 'E',
                controllerAs: 'dvm',
                replace: true,
                scope: {},
                controller: function() {
                    var dvm = this;
                    dvm.state = mapperStateService;
                    dvm.manager = mappingManagerService;
                    dvm.ontology = ontologyManagerService;

                    dvm.useMapping = function() {
                        var deferred = $q.defer();
                        dvm.state.editMapping = true;
                        dvm.state.newMapping = false;
                        var ontologyId = dvm.manager.getSourceOntologyId(dvm.manager.mapping.jsonld);
                        var ontology = _.find(dvm.ontology.getList(), {'@id': ontologyId});
                        if (ontology) {
                            deferred.resolve(ontology);
                        } else {
                            dvm.ontology.getThenRestructure(ontologyId).then(ontology => {
                                deferred.resolve(ontology);
                            });
                        }
                        deferred.promise.then(ontology => {
                            dvm.ontology.getImportedOntologies(ontology['@id']).then(imported => {
                                dvm.manager.sourceOntologies = _.concat(ontology, imported);
                                dvm.state.step = 1;
                            });
                        });
                    }
                    dvm.ontologyExists = function() {
                        var objs = angular.copy(dvm.ontology.getList());
                        var ids = _.union(dvm.ontology.getOntologyIds(), _.map(objs, '@id'));
                        return _.includes(ids, dvm.manager.getSourceOntologyId(dvm.manager.mapping.jsonld));
                    }

                    dvm.getClassName = function(classMapping) {
                        return dvm.ontology.getBeautifulIRI(dvm.manager.getClassIdByMapping(classMapping));
                    }
                    dvm.getPropName = function(propMapping) {
                        return dvm.ontology.getBeautifulIRI(dvm.manager.getPropIdByMapping(propMapping));
                    }
                },
                templateUrl: 'modules/mapper/directives/mappingPreview/mappingPreview.html'
            }
        }
})();
