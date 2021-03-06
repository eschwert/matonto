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
describe('Everything Tree directive', function() {
    var $compile,
        scope,
        element,
        controller,
        ontologyStateSvc,
        ontologyManagerSvc;

    beforeEach(function() {
        module('templates');
        module('everythingTree');
        mockOntologyManager();
        mockOntologyState();

        inject(function(_$compile_, _$rootScope_, _ontologyManagerService_, _ontologyStateService_) {
            $compile = _$compile_;
            scope = _$rootScope_;
            ontologyManagerSvc = _ontologyManagerService_;
            ontologyStateSvc = _ontologyStateService_;
        });
        ontologyManagerSvc.getClasses.and.returnValue(['class1']);
        ontologyManagerSvc.getClassProperties.and.returnValue(['property1']);
        ontologyManagerSvc.getNoDomainProperties.and.returnValue(['property1']);
        ontologyManagerSvc.hasNoDomainProperties.and.returnValue(true);
        ontologyStateSvc.getOpened.and.returnValue(true);
        ontologyStateSvc.getNoDomainsOpened.and.returnValue(true);
    });

    describe('replaces the element with the correct html', function() {
        beforeEach(function() {
            element = $compile(angular.element('<everything-tree></everything-tree>'))(scope);
            scope.$digest();
        });
        it('for a DIV', function() {
            expect(element.prop('tagName')).toBe('DIV');
        });
        it('based on tree class', function() {
            expect(element.hasClass('tree')).toBe(true);
        });
        it('based on container class', function() {
            var container = element.querySelectorAll('.container');
            expect(container.length).toBe(2);
        });
        it('based on <ul>s', function() {
            var uls = element.find('ul');
            expect(uls.length).toBe(4);
        });
        it('based on container tree-items', function() {
            var treeItems = element.querySelectorAll('.container tree-item');
            expect(treeItems.length).toBe(2);
        });
        describe('based on tree-item length', function() {
            it('when noDomainProperties is empty', function() {
                ontologyManagerSvc.getNoDomainProperties.and.returnValue([]);
                element = $compile(angular.element('<everything-tree></everything-tree>'))(scope);
                scope.$digest();

                var treeItems = element.querySelectorAll('.container tree-item');
                expect(treeItems.length).toBe(1);
            });
            it('when getClassProperties returns an empty array', function() {
                ontologyManagerSvc.getClassProperties.and.returnValue([]);
                element = $compile(angular.element('<everything-tree></everything-tree>'))(scope);
                scope.$digest();

                var treeItems = element.querySelectorAll('.container tree-item');
                expect(treeItems.length).toBe(1);
            });
            it('when getClasses is empty', function() {
                ontologyManagerSvc.getClasses.and.returnValue([]);
                element = $compile(angular.element('<everything-tree></everything-tree>'))(scope);
                scope.$digest();

                var treeItems = element.querySelectorAll('.container tree-item');
                expect(treeItems.length).toBe(1);
            });
        });
    });
});