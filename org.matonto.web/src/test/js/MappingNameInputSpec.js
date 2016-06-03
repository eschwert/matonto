describe('Mapping Name Input directive', function() {
    var $compile,
        scope,
        mappingManagerSvc;

    injectRegexConstant();
    beforeEach(function() {
        module('mappingNameInput');
        mockMappingManager();

        inject(function(_mappingManagerService_) {
            mappingManagerSvc = _mappingManagerService_;
        });

        inject(function(_$compile_, _$rootScope_) {
            $compile = _$compile_;
            scope = _$rootScope_;
        });
    });

    injectDirectiveTemplate('modules/mapper/directives/mappingNameInput/mappingNameInput.html');

    describe('in isolated scope', function() {
        beforeEach(function() {
            scope.name = '';
            scope.required = true;
            scope.isActive = true;
            scope.focusEvent = jasmine.createSpy('focusEvent');

            var form = $compile('<form></form>')(scope);
            this.element = angular.element('<mapping-name-input name="name" required="required" is-active="isActive" focus-event="focusEvent()"></mapping-name-input>');
            form.append(this.element);
            this.element = $compile(this.element)(scope);
            scope.$digest();
        });

        it('name should be two way bound', function() {
            var isolatedScope = this.element.isolateScope();
            isolatedScope.name = 'test';
            scope.$digest();
            expect(scope.name).toBe('test');
        });
        it('required should be called in the parent scope', function() {
            var isolatedScope = this.element.isolateScope();
            isolatedScope.required = false;
            scope.$digest();
            expect(scope.required).toBe(false);
        });
        it('isActive should be called in the parent scope', function() {
            var isolatedScope = this.element.isolateScope();
            isolatedScope.isActive = false;
            scope.$digest();
            expect(scope.isActive).toBe(false);
        });
        it('focusEvent should be called in the parent scope', function() {
            var isolatedScope = this.element.isolateScope();
            isolatedScope.focusEvent();

            expect(scope.focusEvent).toHaveBeenCalled();
        });
    });
    describe('replaces the element with the correct html', function() {
        beforeEach(function() {
            mappingManagerSvc.previousMappingNames = ['test'];
            scope.name = '';
            scope.required = true;
            scope.isActive = true;
            scope.focusEvent = jasmine.createSpy('focusEvent');

            var form = $compile('<form></form>')(scope);
            this.element = angular.element('<mapping-name-input name="name" required="required" is-active="isActive" focus-event="focusEvent()"></mapping-name-input>');
            form.append(this.element);
            this.element = $compile(this.element)(scope);
            scope.$digest();
        });
        it('for wrapping containers', function() {
            expect(this.element.hasClass('mapping-name-input')).toBe(true);
            expect(this.element.hasClass('form-group')).toBe(true);
        });
        it('with the correct classes based on the input field validity and active state', function() {
            expect(this.element.hasClass('has-error')).toBe(true);
            var isolatedScope = this.element.isolateScope();

            isolatedScope.name = 'a';
            scope.$digest();
            expect(this.element.hasClass('has-success')).toBe(true);

            isolatedScope.isActive = false;
            scope.$digest();
            expect(this.element.hasClass('has-success')).toBe(false);
            expect(this.element.hasClass('has-error')).toBe(false);
        });
        it('with an error for invalid characters', function() {
            var isolatedScope = this.element.isolateScope();
            isolatedScope.name = '$';
            scope.$digest();
            expect(isolatedScope.form.name.$error.pattern).toBe(true);
        });
        it('with an error if the input is a previous mapping name', function() {
            var isolatedScope = this.element.isolateScope();
            isolatedScope.name = mappingManagerSvc.previousMappingNames[0];
            scope.$digest();
            expect(isolatedScope.form.name.$error.uniqueName).toBe(true);
        });
        it('with an error if the input is longer than 50 characters', function() {
            var isolatedScope = this.element.isolateScope();
            isolatedScope.name = 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa';
            scope.$digest();
            expect(isolatedScope.form.name.$error.maxlength).toBe(true);
        });
    });
    it('should not show an error if first name passed is a previous mapping name', function() {
        mappingManagerSvc.previousMappingNames = ['test'];
        scope.name = 'test';
        var form = $compile('<form></form>')(scope);
        var element = angular.element('<mapping-name-input name="name" required="required" is-active="isActive" focus-event="focusEvent()"></mapping-name-input>');
        form.append(element);
        element = $compile(element)(scope);
        scope.$digest();

        var isolatedScope = element.isolateScope();
        expect(isolatedScope.form.$valid).toBe(true);
    });
    it('should have the correct default values for isActive and required', function() {
        var form = $compile('<form></form>')(scope);
        var element = angular.element('<mapping-name-input name="name" required="required" is-active="isActive" focus-event="focusEvent()"></mapping-name-input>');
        form.append(element);
        element = $compile(element)(scope);
        scope.$digest();

        var isolatedScope = element.isolateScope();
        expect(isolatedScope.isActive).toBe(true);
        expect(isolatedScope.required).toBe(true);
    });
});