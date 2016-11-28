"use strict";
angular.module('morrleApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('header',{
                parent: 'site',
                url: '/',
                data: {
                    roles: []
                },
                views: {
                    'header@': {
                        templateUrl: 'scripts/app/header/header.html',
                        controller: 'HeaderController'
                    }
                }

        });
    });
