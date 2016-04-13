(function() {
    'use strict';

    angular
        .module('propSelect', ['ontologyManager'])
        .directive('propSelect', propSelect);

        propSelect.$inject = ['ontologyManagerService'];

        function propSelect(ontologyManagerService) {
            return {
                restrict: 'E',
                controllerAs: 'dvm',
                replace: true,
                scope: {
                    props: '=',
                    onChange: '&'
                },
                bindToController: {
                    selectedProp: '='
                },
                controller: function() {
                    var dvm = this;

                    dvm.getName = function(propObj) {
                        return ontologyManagerService.getEntityName(propObj);
                    }
                },
                templateUrl: 'modules/mapper/directives/propSelect/propSelect.html'
            }
        }
})();