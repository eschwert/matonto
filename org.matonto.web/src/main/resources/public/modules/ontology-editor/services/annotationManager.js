(function() {
    'use strict';

    angular
        .module('annotationManager', ['removeNamespace'])
        .service('annotationManagerService', annotationManagerService)
        .filter('showAnnotations', showAnnotations)
        .filter('hideAnnotations', hideAnnotations);

        annotationManagerService.$inject = ['$filter'];

        function annotationManagerService($filter) {
            var self = this,
                reg = /^([^:\/?#]+):\/\/([^\/?#]*)([^?#]*)\\?([^#]*)(?:[#\/:](.+))+/;

            self.getPattern = function() {
                return reg;
            }

            self.remove = function(obj, key) {
                delete obj[key];
            }

            self.add = function(obj) {
                var prop, temp, stripped,
                    found = false,
                    matonto = obj.matonto,
                    key = matonto.currentAnnotationKey,
                    value = matonto.currentAnnotationValue,
                    select = matonto.currentAnnotationSelect,
                    item = [{'@value': value}];

                if(select === 'other') {
                    temp = key;
                } else {
                    temp = select;
                }
                obj[temp] = item;
                stripped = $filter('removeNamespace')(temp);

                for(prop in matonto.annotations) {
                    if(temp.indexOf(prop) !== -1) {
                        found = true;
                        break;
                    }
                }

                if(!found) {
                    matonto.annotations[temp.replace(stripped, '')] = [stripped];
                }
            }

            self.edit = function(obj, key, value) {
                obj[key][0]['@value'] = value;
            }

            self.searchList = function(annotations, query) {
                var i = 0,
                    arr = [];
                while(i < annotations.length) {
                    if(annotations[i].toLowerCase().indexOf(query.toLowerCase()) !== -1) {
                        arr.push({'@id': annotations[i]});
                    }
                    i++;
                }
                return arr;
            }
        }

        function showAnnotations() {
            return function(obj, annotations) {
                var i, prop, temp,
                    results = [];

                for(prop in annotations) {
                    i = 0;
                    while(i < annotations[prop].length) {
                        temp = prop + annotations[prop][i];
                        if(obj.hasOwnProperty(temp)) {
                            results.push(temp);
                        }
                        i++;
                    }
                }
                return results;
            }
        }

        function hideAnnotations() {
            return function(annotations, obj) {
                var prop, i, count,
                    result = [];

                for(prop in annotations) {
                    i = 0;
                    count = 0;
                    while(i < annotations[prop].length) {
                        if(obj.hasOwnProperty(prop + annotations[prop][i])) {
                            count++;
                        }
                        i++;
                    }

                    if(count < annotations[prop].length) {
                        result.push(prop);
                    }
                }
                return result;
            }
        }
})();