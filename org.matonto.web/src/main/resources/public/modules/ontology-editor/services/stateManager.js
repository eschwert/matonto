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
(function() {
    'use strict';

    angular
        .module('stateManager', ['ontologyManager'])
        .service('stateManagerService', stateManagerService);

        stateManagerService.$inject = ['ontologyManagerService'];

        function stateManagerService(ontologyManagerService) {
            var self = this;

            self.states = {
                current: 'everything',
                everything: {
                    tab: 'everything',
                    editor: 'default-tab',
                    editorTab: 'basic'
                },
                class: {
                    tab: 'class',
                    editor: 'default-tab',
                    editorTab: 'basic'
                },
                object: {
                    tab: 'object',
                    editor: 'default-tab',
                    editorTab: 'basic'
                },
                datatype: {
                    tab: 'datatype',
                    editor: 'default-tab',
                    editorTab: 'basic'
                },
                annotation: {
                    tab: 'annotation',
                    editor: 'default-tab',
                    editorTab: 'basic'
                }
            }

            self.om = ontologyManagerService;
            self.ontology = {};
            self.selected = {};
            self.ontologyIds = self.om.getOntologyIds();
            self.currentState = self.states[self.states.current];


            function setVariables(oi) {
                if(oi === undefined) {
                    self.selected = undefined;
                    self.ontology = undefined;
                } else {
                    self.selected = self.om.getObject(self.getState());
                    self.ontology = self.om.getOntology(oi);
                }
            }

            self.setTreeTab = function(tab) {
                self.states.current = tab;
                self.currentState = self.getState();
                if(tab !== 'annotation') {
                    self.selected = self.om.getObject(self.currentState);
                } else {
                    self.selected = _.get(self.om.getList(), '[' + self.currentState.oi + '].matonto.jsAnnotations[' + self.currentState.pi + ']');
                }
                self.ontology = self.om.getOntology(self.currentState.oi);
            }

            self.setEditorTab = function(tab) {
                self.states[self.states.current].editorTab = tab;
                self.currentState = self.getState();
            }

            self.getEditorTab = function() {
                return self.states[self.states.current].editorTab;
            }

            self.setState = function(editor, oi, ci, pi) {
                var state = self.states[self.states.current];
                if(editor !== state.editor) {
                    state.editorTab = 'basic';
                }
                state.oi = oi;
                state.ci = ci;
                state.pi = pi;
                state.editor = editor;
                self.currentState = self.getState();
            }

            self.getState = function() {
                return self.states[self.states.current];
            }

            self.setStateToNew = function(state, ontologies, type) {
                var editor,
                    oi = state.oi,
                    ci = state.ci,
                    pi = state.pi;
                if(type === 'ontology') {
                    oi = ontologies.length - 1;
                    ci = undefined;
                    pi = undefined;
                    editor = 'ontology-editor';
                } else if(type === 'class') {
                    ci = ontologies[oi].matonto.classes.length - 1;
                    pi = undefined;
                    editor = 'class-editor';
                } else if(type === 'property') {
                    if(ci !== undefined) {
                        pi = ontologies[oi].matonto.classes[ci].matonto.properties.length - 1;
                    } else {
                        pi = ontologies[oi].matonto.noDomains.length - 1;
                    }
                    editor = 'property-editor';
                }
                self.setState(editor, oi, ci, pi);
                setVariables(oi);
                return oi;
            }

            self.clearState = function(oi) {
                var prop, state;
                for(prop in self.states) {
                    if(self.states[prop].oi === oi) {
                        state = self.states[prop];
                        state.oi = undefined;
                        state.ci = undefined;
                        state.pi = undefined;
                        state.editor = 'default-tab';
                    }
                }
                self.currentState = self.getState();
            }

            self.selectItem = function(editor, oi, ci, pi) {
                self.setState(editor, oi, ci, pi);
                setVariables(oi);
            }

            setVariables(self.currentState.oi);
        }
})();