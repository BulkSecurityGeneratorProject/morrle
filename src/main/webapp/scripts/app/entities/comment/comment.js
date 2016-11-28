"use strict";
angular.module('morrleApp')
    .config(function ($stateProvider) {
        $stateProvider
            .state('resource.comments', {
                views: {
                    'comment@resource':{
                        templateUrl: 'scripts/app/entities/comment/comment.html'
                        // controller: 'ResourceDetailController'
                    }

                }
            });
    })