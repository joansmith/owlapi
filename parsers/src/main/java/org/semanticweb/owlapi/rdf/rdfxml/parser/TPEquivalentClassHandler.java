/* This file is part of the OWL API.
 * The contents of this file are subject to the LGPL License, Version 3.0.
 * Copyright 2014, The University of Manchester
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program.  If not, see http://www.gnu.org/licenses/.
 *
 * Alternatively, the contents of this file may be used under the terms of the Apache License, Version 2.0 in which case, the provisions of the Apache License Version 2.0 are applicable instead of those above.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License. */
package org.semanticweb.owlapi.rdf.rdfxml.parser;

import java.util.HashSet;
import java.util.Set;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.UnloadableImportException;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

/** @author Matthew Horridge, The University Of Manchester, Bio-Health Informatics
 *         Group
 * @since 2.0.0 */
public class TPEquivalentClassHandler extends TriplePredicateHandler {
    /** @param consumer
     *            consumer */
    public TPEquivalentClassHandler(OWLRDFConsumer consumer) {
        super(consumer, OWLRDFVocabulary.OWL_EQUIVALENT_CLASS.getIRI());
    }

    @Override
    public boolean canHandle(IRI subject, IRI predicate, IRI object) {
        inferTypes(subject, object);
        return super.canHandle(subject, predicate, object)
                && isSubjectAndObjectMatchingClassExpressionOrMatchingDataRange(
                        subject, object);
    }

    @Override
    public boolean canHandleStreaming(IRI subject, IRI predicate, IRI object) {
        inferTypes(subject, object);
        return !isStrict()
                && !isSubjectOrObjectAnonymous(subject, object)
                && isSubjectAndObjectMatchingClassExpressionOrMatchingDataRange(
                        subject, object);
    }

    @Override
    public void handleTriple(IRI subject, IRI predicate, IRI object)
            throws UnloadableImportException {
        if (isStrict()) {
            if (isClassExpressionStrict(subject)
                    && isClassExpressionStrict(object)) {
                translateEquivalentClasses(subject, predicate, object);
            } else if (isDataRangeStrict(subject) && isDataRangeStrict(object)) {
                translateEquivalentDataRanges(subject, predicate, object);
            }
        } else {
            if (isClassExpressionLax(subject) && isClassExpressionLax(object)) {
                translateEquivalentClasses(subject, predicate, object);
            } else if (isDataRangeLax(subject) || isDataRangeLax(object)) {
                translateEquivalentDataRanges(subject, predicate, object);
            }
        }
    }

    private void translateEquivalentDataRanges(IRI subject, IRI predicate,
            IRI object) {
        OWLDatatype datatype = getDataFactory().getOWLDatatype(subject);
        OWLDataRange dataRange = getConsumer().translateDataRange(object);
        addAxiom(getDataFactory().getOWLDatatypeDefinitionAxiom(datatype,
                dataRange, getPendingAnnotations()));
        consumeTriple(subject, predicate, object);
    }

    private void translateEquivalentClasses(IRI subject, IRI predicate,
            IRI object) {
        Set<OWLClassExpression> operands = new HashSet<OWLClassExpression>();
        operands.add(translateClassExpression(subject));
        operands.add(translateClassExpression(object));
        addAxiom(getDataFactory().getOWLEquivalentClassesAxiom(operands,
                getPendingAnnotations()));
        consumeTriple(subject, predicate, object);
    }
}