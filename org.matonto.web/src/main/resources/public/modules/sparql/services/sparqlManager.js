(function() {
    'use strict';

    angular
        .module('sparqlManager', [])
        .service('sparqlManagerService', sparqlManagerService);

        sparqlManagerService.$inject = ['$rootScope', '$http'];

        function sparqlManagerService($rootScope, $http) {
            var prefix = '/matontorest/query';
            var self = this;

            self.prefixes = [];
            self.queryString = '';
            self.data = {};

            self.errorMessage = '';
            self.infoMessage = 'Please submit a query to see results here.';

            function getMessage(response, defaultMessage) {
                return _.get(response, 'statusText') || defaultMessage;
            }

            self.queryRdf = function() {
                $rootScope.showSpinner = true;

                self.data = {};
                self.errorMessage = '';
                self.infoMessage = '';

                var prefixes = self.prefixes.length ? 'PREFIX ' + _.join(self.prefixes, ' PREFIX ') : '';
                var config = {
                    params: {
                        query: prefixes + self.queryString
                    }
                }

                return $http.get(prefix, config)
                    .then(function(response) {
                        if(_.get(response, 'status') === 200) {
                            self.data = response.data;
                        } else {
                            self.infoMessage = getMessage(response, 'There was a problem getting the results.');
                        }
                    }, function(response) {
                        self.errorMessage = getMessage(response, 'A server error has occurred. Please try again later.');
                    })
                    .then(function() {
                        $rootScope.showSpinner = false;
                    });
            }
        }
})();