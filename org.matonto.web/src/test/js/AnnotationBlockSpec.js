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
describe('Annotation Block directive', function() {
    var $compile,
        scope,
        element,
        stateManagerSvc,
        ontologyManagerSvc,
        controller;


    beforeEach(function() {
        module('templates');
        module('annotationBlock');
        injectBeautifyFilter();
        injectSplitIRIFilter();
        injectShowPropertiesFilter();
        mockStateManager();
        mockOntologyManager();
        mockResponseObj();

        inject(function(_$compile_, _$rootScope_, _stateManagerService_, _responseObj_, _ontologyManagerService_) {
            $compile = _$compile_;
            scope = _$rootScope_;
            stateManagerSvc = _stateManagerService_;
            ontologyManagerSvc = _ontologyManagerService_;
        });
    });

    describe('replaces the element with the correct html', function() {
        beforeEach(function() {
            stateManagerSvc.selected = {
                'prop1': [{'@id': 'value1'}],
                'prop2': [{'@value': 'value2'}]
            };
            ontologyManagerSvc.getAnnotationIRIs.and.returnValue(['prop1', 'prop2']);
            element = $compile(angular.element('<annotation-block></annotation-block>'))(scope);
            scope.$digest();
        });
        it('for a DIV', function() {
            expect(element.prop('tagName')).toBe('DIV');
        });
        it('based on annotation button', function() {
            var icon = element.querySelectorAll('.fa-plus');
            expect(icon.length).toBe(1);
        });
        it('based on listed anotations', function() {
            var annotations = element.find('property-values');
            expect(annotations.length).toBe(2);
            stateManagerSvc.selected = undefined;
            scope.$digest();
            annotations = element.find('property-values');
            expect(annotations.length).toBe(0);
        });
    });
    describe('controller methods', function() {
        beforeEach(function() {
            element = $compile(angular.element('<annotation-block></annotation-block>'))(scope);
            scope.$digest();
            controller = element.controller('annotationBlock');
        });
        it('openAddOverlay sets the correct manager values', function() {
            controller.openAddOverlay();
            expect(stateManagerSvc.editingAnnotation).toBe(false);
            expect(stateManagerSvc.annotationSelect).toEqual(undefined);
            expect(stateManagerSvc.annotationValue).toBe('');
            expect(stateManagerSvc.annotationIndex).toBe(0);
            expect(stateManagerSvc.showAnnotationOverlay).toBe(true);
        });
        it('openRemoveOverlay sets the correct manager values', function() {
            controller.openRemoveOverlay('key', 1);
            expect(stateManagerSvc.key).toBe('key');
            expect(stateManagerSvc.index).toBe(1);
            expect(stateManagerSvc.showRemoveOverlay).toBe(true);
        });
        it('editClicked sets the correct manager values', function() {
            var annotationIRI = 'prop1';
            stateManagerSvc.selected = {
                'prop1': [{'@value': 'value', '@type': 'type'}]
            };
            stateManagerSvc.listItem.dataPropertyRange = ['type'];
            controller.editClicked(annotationIRI, 0);
            expect(stateManagerSvc.editingAnnotation).toBe(true);
            expect(stateManagerSvc.annotationSelect).toEqual(annotationIRI);
            expect(stateManagerSvc.annotationValue).toBe(stateManagerSvc.selected[annotationIRI][0]['@value']);
            expect(stateManagerSvc.annotationIndex).toBe(0);
            expect(stateManagerSvc.annotationType).toBe(stateManagerSvc.selected[annotationIRI][0]['@type']);
            expect(stateManagerSvc.showAnnotationOverlay).toBe(true);
        });
    });
});