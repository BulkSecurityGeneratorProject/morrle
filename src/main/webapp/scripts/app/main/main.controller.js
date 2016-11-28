'use strict';

angular.module('morrleApp')
    .controller('MainController', function ($scope,ParseLinks,Resource) {
        $scope.resources = [];
        $scope.page = 1;
        $scope.loadAll = function() {
            Resource.query({page: $scope.page, per_page: 20}, function(result, headers) {
                // $scope.links = ParseLinks.parse(headers('link'));
                $scope.resources = result;
            });
        };
        $scope.loadPage = function(page) {
            $scope.page = page;
            $scope.loadAll();
        };
         $scope.loadAll();

    });

// angular.module('morrleApp')
//     .controller('MainController', function ($scope, Principal) {
//         Principal.identity().then(function(account) {
//             $scope.account = account;
//             $scope.isAuthenticated = Principal.isAuthenticated;
//         });
//     });
