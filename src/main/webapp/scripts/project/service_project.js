'use strict';

respondecoApp.factory('Project', function($resource, $http) {
    return $resource('app/rest/projects/:id', {}, {
        'update': {method: 'PUT', url: 'app/rest/projects/:id'},
        'getProjectsByOrgId': {method: 'GET', url: 'app/rest/organizations/:organizationId/projects'},
        'getPostingsByProjectId': {method: 'GET', url: 'app/rest/projects/:id/postings'},
        'addPostingForProject': {method: 'POST', url: '/app/rest/projects/:id/postings'},
        'getProjectRequirements' : {method: 'GET', isArray:true, url: 'app/rest/projects/:id/resourcerequirements'},
        'getResourceMatchesByProjectId' : {method: 'GET', isArray:true, url: 'app/rest/projects/:id/resourcematches'},
        'getAggregatedRating' : {method: 'GET', url: 'app/rest/projects/:pid/ratings'},
        'rateProject' : {method: 'POST', url: 'app/rest/projects/:pid/ratings'},
        'apply': { method: 'POST', url: '/app/rest/projects/apply' },
        'checkIfRatingPossible' : {method: 'GET', isArray: true, url: 'app/rest/projects/:pid/ratings'},
        'editable' : {method: 'GET', url: 'app/rest/projects/:id/editable', ignoreAuthModule: true},
        'getNearProjects': {method: 'GET', isArray:true, url: 'app/rest/nearprojects'},
        'follow' : {method: 'POST', url: 'app/rest/projects/:id/follow'},
        'unfollow' : {method: 'DELETE', url: 'app/rest/projects/:id/unfollow'},
        'followingState' : {method: 'GET', url: 'app/rest/projects/:id/followingstate', ignoreAuthModule: true},
        'deletePosting' : {method: 'DELETE', url: 'app/rest/projects/:id/postings/:pid'},
        'query': {method: 'GET', url: 'app/rest/projects'}
    });
}).factory('ResourceRequirement', function($resource) {
    return $resource('app/rest/resourcerequirements', {})
});
