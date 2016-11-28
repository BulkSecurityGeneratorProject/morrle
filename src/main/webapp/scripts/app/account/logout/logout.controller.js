'use strict';

angular.module('morrleApp')
    .controller('LogoutController', function (Auth) {
        Auth.logout();
    });
