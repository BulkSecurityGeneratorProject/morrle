'use strict';

angular.module('morrleApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('login', {
                url: '/login',
                data: {
                    roles: [], 
                    pageTitle: 'login.title'
                },
                views: {
                    'nav@' :{
                      template : ''
                    },
                    'body@': {
                        templateUrl: 'scripts/app/account/account.html',
                        controller: 'LoginController'
                    },
                    'form@login' :{
                        templateUrl: 'scripts/app/account/login/login.html'
                    }
                }
            });
    });
