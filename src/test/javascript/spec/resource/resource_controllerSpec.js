'use strict';

describe('Resource Controller Tests ', function () {
    beforeEach(module('respondecoApp'));

    describe('ResourceController', function () {
        var $scope, ResourceService, location;

        beforeEach(inject(function ($rootScope, $controller, $location, $routeParams, Resource) {
            $scope = $rootScope.$new();
            location = $location;
            ResourceService = Resource;
            $routeParams.id = 'new';

            location.path('ownresource');

            $controller('ResourceController', {$scope: $scope, $routeParams: $routeParams, $location: location, Resource: ResourceService});
        }));

        it('should create a new resource',function() {
            $scope.resource = {
              "name": "Resource",
              "description": "Desc",
              "id": 1,
              "endDate": "2015-12-12",
              "startDate": "2015-12-12",
              "resourceTags": [
                "Computer", "Test"
              ],
              "amount": 5,
              "isRecurrent": false,
              "isCommercial": false,
              "organizationId": 1
            }

            spyOn(ResourceService, "save");

            $scope.create();

            expect(ResourceService.save).toHaveBeenCalled();
        });


        it('should show an error message', function() {
            $scope.resource = {
              "name": "Resource",
              "description": "Desc",
              "id": 1,
              "endDate": "2015-12-12",
              "startDate": "2015-12-12",
              "resourceTags": [
                "Computer", "Test"
              ],
              "amount": 5,
              "isRecurrent": false,
              "isCommercial": false,
              "organizationId": 1
            }

            spyOn(ResourceService, "save");

            $scope.create();

            expect(ResourceService.save).toHaveBeenCalled();
            expect(ResourceService.save).toHaveBeenCalledWith($scope.resource, jasmine.any(Function), jasmine.any(Function));

            //Simulate error callback
            ResourceService.save.calls.mostRecent().args[2]({
                data: {
                    error: "Error"
                }
            });

            expect($scope.formSaveError).toBe(true);
        });

        it('should update a resource', function() {
            $scope.resource.resourceTags = ['Technik', 'Computer'];
            spyOn(ResourceService, "get");

            $scope.isNew = false;
            $scope.update('1');

            expect(ResourceService.get).toHaveBeenCalledWith({id:'1'}, jasmine.any(Function), jasmine.any(Function));
        });


        it('should redirect to given id', function() {
            $scope.redirectToResource('1');

            expect(location.path()).toBe('/resource/1');
        });

        it('should clear the resource', function() {
            $scope.resource.id = "1";
            $scope.resource.name = "Resource";
            $scope.resource.amount = 1;
            $scope.resource.dateStart = "11.11.2014";
            $scope.resource.dateEnd = "12.12.2014";
            $scope.resource.isCommercial = true;
            $scope.resource.isRecurrent = true;

            $scope.clear();

            expect($scope.resource).toEqual({'id': null, 'name': null, 'description': null,
                'resourceTags': [], 'amount': null, 'startDate': null, 'endDate': null,
                'isCommercial': false, 'isRecurrent': false});
        });

        it('should search for resources', function() {
            spyOn(ResourceService,"query");
            $scope.search("filter");
            expect(ResourceService.query).toHaveBeenCalledWith({filter:'filter'},
                jasmine.any(Function));

            ResourceService.query.calls.mostRecent().args[1]({
                res: 'resources'
            });
        });

        /*it('should call update if isNew is false', function() {
            spyOn($scope,"update");

            $scope.isNew = false;

            expect($scope.update).toHaveBeenCalled();
        });*/

        it('should delete a resource', function() {
            spyOn(ResourceService, "delete");
            spyOn(ResourceService, "query");

            $scope.delete('1');

            expect(ResourceService.delete).toHaveBeenCalledWith({id:'1'}, jasmine.any(Function));

            ResourceService.delete.calls.mostRecent().args[1]();
            expect(ResourceService.query).toHaveBeenCalled();
        });

        it('should update the model', function() {
            spyOn(ResourceService, "get");
            $scope.update(1);
            expect(ResourceService.get).toHaveBeenCalledWith({
              id: 1
            }, jasmine.any(Function), jasmine.any(Function));

            ResourceService.get.calls.mostRecent().args[1]({
              resourceTags: []
            });

            ResourceService.get.calls.mostRecent().args[2]();
            expect(location.path()).toEqual("/resource/new");
        });
    });
});
