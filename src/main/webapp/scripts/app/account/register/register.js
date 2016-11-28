'use strict';

angular.module('morrleApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('register', {
                url: '/register',
                data: {
                    roles: [],
                },
                views: {
                    'nav@':{
                        template:''
                    },
                    'body@': {
                        templateUrl: 'scripts/app/account/account.html',
                        controller: 'RegisterController'
                    },
                    'form@register' : {
                        templateUrl: 'scripts/app/account/register/register.html'
                    }
                }
            });
    });
