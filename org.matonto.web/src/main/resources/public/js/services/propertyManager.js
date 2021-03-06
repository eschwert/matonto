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
        .module('propertyManager', [])
        .service('propertyManagerService', propertyManagerService);

        propertyManagerService.$inject = ['$rootScope', '$filter', '$q', '$http', 'prefixes'];

        function propertyManagerService($rootScope, $filter, $q, $http, prefixes) {
            var self = this;
            var prefix = '/matontorest/ontologies/';

            var rdfsAnnotations = _.map(['comment', 'label', 'seeAlso', 'isDefinedBy'], item => {
                return {
                    namespace: prefixes.rdfs,
                    localName: item
                }
            });
            var dcAnnotations = _.map(['description', 'title'], item => {
                return {
                    namespace: prefixes.dcterms,
                    localName: item
                }
            });
            self.defaultAnnotations = _.concat(rdfsAnnotations, dcAnnotations);

            self.skosAnnotations = _.map(['altLabel', 'changeNote', 'definition', 'editorialNote', 'example',
                'hiddenLabel', 'historyNote', 'note', 'prefLabel', 'scopeNote'], item => {
                return {
                    namespace: prefixes.skos,
                    localName: item
                }
            });

            self.classAxiomList = [
                {
                    namespace: prefixes.rdfs,
                    localName: 'subClassOf',
                    valuesKey: 'subClasses'
                },
                {
                    namespace: prefixes.owl,
                    localName: 'disjointWith',
                    valuesKey: 'subClasses'
                },
                {
                    namespace: prefixes.owl,
                    localName: 'equivalentClass',
                    valuesKey: 'subClasses'
                }
            ];


            self.datatypeAxiomList = [
                {
                    namespace: prefixes.rdfs,
                    localName: 'domain',
                    valuesKey: 'subClasses'
                },
                {
                    namespace: prefixes.rdfs,
                    localName: 'range',
                    valuesKey: 'dataPropertyRange'
                },
                {
                    namespace: prefixes.owl,
                    localName: 'equivalentProperty',
                    valuesKey: 'subDataProperties'
                },
                {
                    namespace: prefixes.rdfs,
                    localName: 'subPropertyOf',
                    valuesKey: 'subDataProperties'
                },
                {
                    namespace: prefixes.owl,
                    localName: 'disjointWith',
                    valuesKey: 'subDataProperties'
                }
            ];

            self.objectAxiomList = [
                {
                    namespace: prefixes.rdfs,
                    localName: 'domain',
                    valuesKey: 'subClasses'
                },
                {
                    namespace: prefixes.rdfs,
                    localName: 'range',
                    valuesKey: 'subClasses'
                },
                {
                    namespace: prefixes.owl,
                    localName: 'equivalentProperty',
                    valuesKey: 'subObjectProperties'
                },
                {
                    namespace: prefixes.rdfs,
                    localName: 'subPropertyOf',
                    valuesKey: 'subObjectProperties'
                },
                {
                    namespace: prefixes.owl,
                    localName: 'inverseOf',
                    valuesKey: 'subObjectProperties'
                },
                {
                    namespace: prefixes.owl,
                    localName: 'disjointWith',
                    valuesKey: 'subObjectProperties'
                }
            ];

            self.remove = function(entity, key, index) {
                _.pullAt(entity[key], index);
                if (!entity[key].length) {
                    delete entity[key];
                }
            }

            self.add = function(entity, prop, value, type) {
                if (prop) {
                    var annotation = {'@value': value};
                    if (type) {
                        annotation['@type'] = type;
                    }
                    if (_.has(entity, prop)) {
                        entity[prop].push(annotation);
                    } else {
                        entity[prop] = [annotation];
                    }
                }
            }

            self.edit = function(entity, prop, value, index, type) {
                if (prop) {
                    var annotation = entity[prop][index];
                    annotation['@value'] = value;

                    if (type) {
                        annotation['@type'] = type;
                    }
                }
            }

            self.create = function(ontologyId, annotationIRIs, iri) {
                $rootScope.showSpinner = true;
                var deferred = $q.defer();
                var annotationJSON = {'@id': iri, '@type': [prefixes.owl + 'AnnotationProperty']};
                if (_.indexOf(annotationIRIs, iri) === -1) {
                    var config = {
                        params: {
                            annotationjson: annotationJSON
                        }
                    }
                    $http.post(prefix + encodeURIComponent(ontologyId) + '/annotations', null, config)
                        .then(response => {
                            if (_.get(response, 'status') === 200) {
                                deferred.resolve(annotationJSON);
                            } else {
                                deferred.reject(_.get(response, 'statusText'));
                            }
                        }, response => {
                            deferred.reject(_.get(response, 'statusText'));
                        })
                        .then(() => {
                            $rootScope.showSpinner = false;
                        });
                } else {
                    deferred.reject('This ontology already has an OWL Annotation declared with that IRI.');
                    $rootScope.showSpinner = false;
                }
                return deferred.promise;
            }
        }
})();