/*-
 * #%L
 * org.matonto.web
 * $Id:$
 * $HeadURL:$
 * %%
 * Copyright (C) 2016 iNovex Information Systems, Inc.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
describe('Create Annotation Overlay directive', function() {
    var $compile,
        scope,
        element,
        controller,
        ontologyStateSvc,
        propertyManagerSvc,
        deferred,
        ontologyManagerSvc;

    beforeEach(function() {
        module('templates');
        module('createAnnotationOverlay');
        injectRegexConstant();
        mockPropertyManager();
        mockOntologyState();
        mockOntologyManager();

        inject(function(_$q_, _$compile_, _$rootScope_, _propertyManagerService_, _ontologyStateService_,
            _ontologyManagerService_) {
            $q = _$q_;
            $compile = _$compile_;
            scope = _$rootScope_;
            propertyManagerSvc = _propertyManagerService_;
            ontologyStateSvc = _ontologyStateService_;
            deferred = _$q_.defer();
            ontologyManagerSvc = _ontologyManagerService_;
        });
    });

    beforeEach(function() {
        element = $compile(angular.element('<create-annotation-overlay></create-annotation-overlay>'))(scope);
        scope.$digest();
    });

    describe('replaces the element with the correct html', function() {
        it('for a div', function() {
            expect(element.prop('tagName')).toBe('DIV');
        });
        it('based on .content', function() {
            var items = element.querySelectorAll('.content');
            expect(items.length).toBe(1);
        });
        it('based on h6', function() {
            var items = element.find('h6');
            expect(items.length).toBe(1);
        });
        it('based on .form-group', function() {
            var items = element.querySelectorAll('.form-group');
            expect(items.length).toBe(1);
        });
        it('based on .btn-container', function() {
            var items = element.querySelectorAll('.btn-container');
            expect(items.length).toBe(1);
        });
        it('based on .error-msg', function() {
            var items = element.querySelectorAll('.error-msg');
            expect(items.length).toBe(1);
        });
        describe('and has-error class', function() {
            it('is not there when form.iri is valid', function() {
                var formGroup = element.querySelectorAll('.form-group');
                expect(angular.element(formGroup[0]).hasClass('has-error')).toBe(false);
            });
            it('is there when form.iri is invalid', function() {
                controller = element.controller('createAnnotationOverlay');
                controller.form = {
                    iri: {
                        '$error': {
                            pattern: true
                        }
                    }
                }
                scope.$digest();

                var formGroup = element.querySelectorAll('.form-group');
                expect(angular.element(formGroup[0]).hasClass('has-error')).toBe(true);
            });
        });
        describe('and error-display', function() {
            beforeEach(function() {
                controller = element.controller('createAnnotationOverlay');
            });
            it('is visible when error is true', function() {
                controller.error = true;
                scope.$digest();
                var errors = element.querySelectorAll('error-display');
                expect(errors.length).toBe(1);
            });
            it('is not visible when error is false', function() {
                controller.error = false;
                scope.$digest();
                var errors = element.querySelectorAll('error-display');
                expect(errors.length).toBe(0);
            });
        });
    });
    describe('controller methods', function() {
        beforeEach(function() {
            controller = element.controller('createAnnotationOverlay');
        });
        describe('create', function() {
            beforeEach(function() {
                propertyManagerSvc.create.and.returnValue(deferred.promise);
                controller.iri = 'iri';
                controller.create();
            });
            it('calls the correct manager function', function() {
                expect(ontologyManagerSvc.getAnnotationIRIs).toHaveBeenCalledWith(ontologyStateSvc.ontology);
                expect(propertyManagerSvc.create).toHaveBeenCalledWith(ontologyStateSvc.state.ontologyId,
                    ontologyManagerSvc.getAnnotationIRIs(ontologyStateSvc.ontology), controller.iri);
            });
            it('when resolved, sets the correct variables', function() {
                deferred.resolve({'@id': 'id'});
                scope.$apply();
                expect(ontologyManagerSvc.addEntity).toHaveBeenCalledWith(ontologyStateSvc.ontology,
                    {'@id': 'id', matonto: {originalIRI: 'id'}});
                expect(ontologyStateSvc.showCreateAnnotationOverlay).toBe(false);
            });
            it('when rejected, sets the correct variable', function() {
                deferred.reject('error');
                scope.$apply();
                expect(controller.error).toBe('error');
            });
        });

    });
});