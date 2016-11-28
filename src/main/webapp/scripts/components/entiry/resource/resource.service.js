"use strict";
angular.module('morrleApp')
    .factory('Resource',function ($resource,DateUtils) {
        return $resource('scripts/app/resource:id.json',{},{
            'query': {method:'GET', isArray: true},
            'get': {
                method: 'GET'
                // transformResponse: function (data) {
                //     data = angular.fromJson(data);
                //     data.publicationDate = DateUtils.convertLocaleDateFromServer(data.publicationDate);
                // }
            }
        })
    });