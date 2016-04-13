(function() {
    'use strict';

    angular
        .module('mappingSelectOverlay', ['mappingManager'])
        .directive('mappingSelectOverlay', mappingSelectOverlay);

        mappingSelectOverlay.$inject = ['mappingManagerService'];

        function mappingSelectOverlay(mappingManagerService) {
            return {
                restrict: 'E',
                controllerAs: 'dvm',
                replace: true,
                scope: {
                    onClickBack: '&',
                    onClickContinue: '&'
                },
                controller: ['$scope', function($scope) {
                    var dvm = this;
                    dvm.previousMappings = mappingManagerService.previousMappingNames;

                    dvm.updateMappingName = function() {
                        dvm.mappingName = (dvm.mappingType === 'new') ? '' : dvm.previousMappings[0];
                    }
                    dvm.testUniqueName = function() {
                        if ($scope.mappingForm) {
                            $scope.mappingForm.mappingName.$setValidity('uniqueName', 
                                dvm.mappingType === 'previous' || 
                                (dvm.mappingType === 'new' && dvm.previousMappings.indexOf(dvm.mappingName) < 0));
                        }
                    }
                }],
                templateUrl: 'modules/mapper/directives/mappingSelectOverlay/mappingSelectOverlay.html'
            }
        }
})();