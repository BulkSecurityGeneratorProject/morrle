"use strict";
angular.module('morrleApp')
    .controller('ResourceDetailController',function ($scope, $stateParams,Resource) {
        $scope.resource = {};
        $scope.load = function (id) {
            Resource.get({id:id},function (result) {
                $scope.resource = result;
            })
        }
        $scope.load($stateParams.id);

        
    })