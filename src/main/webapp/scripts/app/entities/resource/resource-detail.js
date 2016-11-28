"use strict";
angular.module('morrleApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('resource', {
                parent: 'site',
                url: 'resource/:id',
                views: {
                    'body@': {
                        templateUrl: 'scripts/app/entities/resource/resource-detail.html',
                        controller: 'ResourceDetailController'
                    }

                }
            });
    })