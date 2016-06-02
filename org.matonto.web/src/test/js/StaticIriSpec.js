describe('Static IRI directive', function() {
    var $compile,
        scope,
        element;

    injectRegexConstant();
    injectSplitIRIFilter();

    beforeEach(function() {
        module('staticIri');

        inject(function(_$compile_, _$rootScope_) {
            $compile = _$compile_;
            scope = _$rootScope_;
        });
    });

    injectDirectiveTemplate('modules/ontology-editor/directives/staticIri/staticIri.html');

    beforeEach(function() {
        scope.onEdit = jasmine.createSpy('onEdit');
        scope.iri = 'iri';
        scope.ontologyIriBegin = 'begin';
        scope.ontologyIriThen = 'then';

        element = $compile(angular.element('<static-iri on-edit="onEdit()" iri="iri" ontology-iri-begin="ontologyIriBegin" ontology-iri-then="ontologyIriThen"></static-iri>'))(scope);
        scope.$digest();
    });

    describe('in isolated scope', function() {
        var isolatedScope;
        beforeEach(function() {
            isolatedScope = element.isolateScope();
        });
        it('selectItem should be called in parent scope', function() {
            isolatedScope.onEdit();
            scope.$digest();
            expect(scope.onEdit).toHaveBeenCalled();
        });
    });
    describe('controller bound variables', function() {
        var controller;

        beforeEach(function() {
            controller = element.controller('staticIri');
        });
        it('iri should be two way bound', function() {
            controller.iri = 'new';
            scope.$digest();
            expect(scope.iri).toBe('new');
        });
        it('ontologyIriBegin should be two way bound', function() {
            controller.ontologyIriBegin = 'new';
            scope.$digest();
            expect(scope.ontologyIriBegin).toBe('new');
        });
        it('ontologyIriThen should be two way bound', function() {
            controller.ontologyIriThen = 'new';
            scope.$digest();
            expect(scope.ontologyIriThen).toBe('new');
        });
    });
    describe('replaces the element with the correct html', function() {
        beforeEach(function() {
            element.controller('staticIri').showIriOverlay = true;
            scope.$digest();
        });
        it('for a STATIC-IRI', function() {
            expect(element.prop('tagName')).toBe('STATIC-IRI');
        });
        _.forEach(['.static-iri', 'form', 'h6', '.btn-container'], function(item) {
            it('based on ' + item, function() {
                var items = element.querySelectorAll(item);
                expect(items.length).toBe(1);
            });
        });
        it('based on .form-control', function() {
            var uls = element.querySelectorAll('.form-control');
            expect(uls.length).toBe(3);
        });
    });
    describe('controller methods', function() {
        var controller;

        beforeEach(function() {
            controller = element.controller('staticIri');
        });
        it('setVariables changes the passed in variable', function() {
            var obj = {
                iriBegin: 'begin',
                iriThen: 'then',
                iriEnd: 'end'
            }
            controller.setVariables(obj);
            expect(obj.iriBegin).toBe('');
            expect(obj.iriThen).toBe('');
            expect(obj.iriEnd).toBe('');
        });
        it('resetVariables updates iriBegin, iriThen, and iriEnd', function() {
            controller.refresh = {
                iriBegin: 'new',
                iriThen: 'new',
                iriEnd: 'new'
            }
            controller.resetVariables();
            expect(controller.iriBegin).toBe('new');
            expect(controller.iriThen).toBe('new');
            expect(controller.iriEnd).toBe('new');
        });
        it('afterEdit update ontologyIriBegin and ontologyIriThen and sets showIriOverlay to false', function() {
            controller.showIriOverlay = true;
            controller.iriBegin = 'new';
            controller.iriThen = 'new';
            controller.afterEdit();
            expect(controller.ontologyIriBegin).toBe('new');
            expect(controller.ontologyIriThen).toBe('new');
            expect(controller.showIriOverlay).toBe(false);
        });
        it('check $watch', function() {
            controller.setVariables = jasmine.createSpy('setVariables');
            controller.iri = 'new';
            scope.$digest();
            expect(controller.setVariables).toHaveBeenCalled();
        });
    });
});
