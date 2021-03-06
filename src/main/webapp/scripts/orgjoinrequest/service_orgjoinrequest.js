'use strict';

respondecoApp.factory('OrgJoinRequest', function ($resource) {
        return $resource('app/rest/orgjoinrequests/:id', {}, {
            'query': { method: 'GET', isArray: true},
            'get': { method: 'GET', isArray: true},
            'accept': { method: 'POST', url: '/app/rest/orgjoinrequests/accept/:id' },
            'decline': { method: 'POST', url: '/app/rest/orgjoinrequests/decline/:id' },
            'current': { method: 'GET', url: '/app/rest/account/orgjoinrequests', isArray: true },
            'delete': { method: 'DELETE', url: '/app/rest/orgjoinrequests/:id' }
        });
    });
