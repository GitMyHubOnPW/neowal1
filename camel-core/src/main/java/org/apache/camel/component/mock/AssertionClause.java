/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.mock;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Expression;
import org.apache.camel.Predicate;
import org.apache.camel.builder.ExpressionClause;
import org.apache.camel.builder.ExpressionClauseSupport;
import org.apache.camel.builder.ValueBuilder;
import org.apache.camel.util.PredicateAssertHelper;

import static org.apache.camel.builder.ExpressionBuilder.bodyExpression;
import static org.apache.camel.builder.ExpressionBuilder.headerExpression;
import static org.apache.camel.builder.ExpressionBuilder.outBodyExpression;
import static org.apache.camel.builder.ExpressionBuilder.propertyExpression;

/**
 * A builder of assertions on message exchanges
 *
 * @version $Revision$
 */
public abstract class AssertionClause<T> extends ExpressionClauseSupport implements Runnable {

    private List<Predicate> predicates = new ArrayList<Predicate>();

    @SuppressWarnings("unchecked")
    public AssertionClause(Object result) {
        super(result);
    }

    // Builder methods
    // -------------------------------------------------------------------------

    /**
     * Adds the given predicate to this assertion clause
     */
    public AssertionClause predicate(Predicate predicate) {
        addPredicate(predicate);
        return this;
    }

    public ExpressionClause<AssertionClause> predicate() {
        ExpressionClause<AssertionClause> clause = new ExpressionClause<AssertionClause>(this);
        addPredicate(clause);
        return clause;
    }

    /**
     * Returns a predicate and value builder for headers on an exchange
     */
    public ValueBuilder header(String name) {
        Expression expression = headerExpression(name);
        return new PredicateValueBuilder(expression);
    }

    /**
     * Returns a predicate and value builder for property on an exchange
     */
    public ValueBuilder property(String name) {
        Expression expression = propertyExpression(name);
        return new PredicateValueBuilder(expression);
    }

    /**
     * Returns a predicate and value builder for the inbound body on an exchange
     */
    public PredicateValueBuilder body() {
        Expression expression = bodyExpression();
        return new PredicateValueBuilder(expression);
    }

    /**
     * Returns a predicate and value builder for the inbound message body as a
     * specific type
     */
    public <T> PredicateValueBuilder body(Class<T> type) {
        Expression expression = bodyExpression(type);
        return new PredicateValueBuilder(expression);
    }

    /**
     * Returns a predicate and value builder for the outbound body on an
     * exchange
     */
    public PredicateValueBuilder outBody() {
        Expression expression = outBodyExpression();
        return new PredicateValueBuilder(expression);
    }

    /**
     * Returns a predicate and value builder for the outbound message body as a
     * specific type
     */
    public <T> PredicateValueBuilder outBody(Class<T> type) {
        Expression expression = outBodyExpression(type);
        return new PredicateValueBuilder(expression);
    }

    /**
     * Performs any assertions on the given exchange
     */
    protected void applyAssertionOn(MockEndpoint endpoint, int index, Exchange exchange) {
        for (Predicate predicate : predicates) {
            PredicateAssertHelper.assertMatches(predicate, endpoint.getEndpointUri() + " ", exchange);
        }
    }

    protected void addPredicate(Predicate predicate) {
        predicates.add(predicate);
    }

    /**
     * Public class needed for fluent builders
     */
    public class PredicateValueBuilder extends ValueBuilder {

        public PredicateValueBuilder(Expression expression) {
            super(expression);
        }

        protected Predicate onNewPredicate(Predicate predicate) {
            addPredicate(predicate);
            return predicate;
        }
    }
}
