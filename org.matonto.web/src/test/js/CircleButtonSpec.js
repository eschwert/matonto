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
describe('Circle Button directive', function() {
    var $compile,
        scope;

    beforeEach(function() {
        module('templates');
        module('circleButton');

        // To test out a directive, you need to inject $compile and $rootScope
        // and save them to use
        inject(function(_$compile_, _$rootScope_) {
            $compile = _$compile_;
            scope = _$rootScope_;
        });
    });

    describe('in isolated scope', function() {
        beforeEach(function() {
            scope.btnIcon = 'fa-square';
            scope.btnSmall = false;
            scope.isEnabled = true;
            scope.onClick = jasmine.createSpy('onClick');

            // To create a copy of the directive, use the $compile(angular.element())($rootScope) 
            // syntax
            this.element = $compile(angular.element('<circle-button btn-icon="btnIcon" btn-small="btnSmall" is-enabled="isEnabled" on-click="onClick()"></circle-button>'))(scope);
            // This needs to be called explicitly if you change anything with the directive,
            // being either a variable change or a function call
            scope.$digest();
        });

        it('btnIcon should be two way bound', function() {
            var isolatedScope = this.element.isolateScope();
            isolatedScope.btnIcon = 'fa-square-o';
            scope.$digest();
            expect(scope.btnIcon).toEqual('fa-square-o');
        });
        it('btnSmall should be two way bound', function() {
            var isolatedScope = this.element.isolateScope();
            isolatedScope.btnSmall = true;
            scope.$digest();
            expect(scope.btnSmall).toEqual(true);
        });
        it('isEnabled should be two way bound', function() {
            var isolatedScope = this.element.isolateScope();
            isolatedScope.isEnabled = false;
            scope.$digest();
            expect(scope.isEnabled).toEqual(false);
        });
        it('onClick should be called in parent scope when invoked', function() {
            var isolatedScope = this.element.isolateScope();
            isolatedScope.onClick();

            expect(scope.onClick).toHaveBeenCalled();
        });
    });
    describe('replaces the element with the correct html', function() {
        it('for a button', function() {
            var element = $compile(angular.element('<circle-button btn-icon="btnIcon" btn-small="btnSmall" is-enabled="isEnabled" on-click="onClick()"></circle-button>'))(scope);
            scope.$digest();

            expect(element.prop('tagName')).toBe('BUTTON');
        });
        it('based on btnIcon', function() {
            scope.btnIcon = 'fa-square';
            var element = $compile(angular.element('<circle-button btn-icon="btnIcon" btn-small="btnSmall" is-enabled="isEnabled" on-click="onClick()"></circle-button>'))(scope);
            scope.$digest();

            var iconList = element.querySelectorAll('.' + scope.btnIcon);
            expect(iconList.length).toBe(1);
        });
        it('based on btnSmall', function() {
            scope.btnSmall = false;
            var element = $compile(angular.element('<circle-button btn-icon="btnIcon" btn-small="btnSmall" is-enabled="isEnabled" on-click="onClick()"></circle-button>'))(scope);
            scope.$digest();

            expect(element.hasClass('small')).toBe(false);
            var isolatedScope = element.isolateScope();
            isolatedScope.btnSmall = true;
            scope.$digest();
            expect(element.hasClass('small')).toBe(true);
        });
    });
    it('calls onClick if button is clicked', function() {
        scope.onClick = jasmine.createSpy('onClick');
        var element = $compile(angular.element('<circle-button btn-icon="btnIcon" btn-small="btnSmall" is-enabled="isEnabled" on-click="onClick()"></circle-button>'))(scope);
        scope.$digest();

        // Testing out DOM events is extremely easy with jqLite which is 
        // shipped with angular
        element.triggerHandler('click');
        scope.$digest();
        expect(scope.onClick).toHaveBeenCalled();
    });
});