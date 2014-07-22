/*
 * Copyright 2012 Delivery Cube
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package deliverycube.common.atg.transaction.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.TransactionManager;

import org.apache.log4j.Logger;

import atg.dtm.TransactionDemarcation;
import atg.dtm.TransactionDemarcationException;
import deliverycube.common.atg.componentresolver.UnknownComponentException;
import deliverycube.common.atg.componentresolver.jndi.JndiComponentResolver;
import deliverycube.common.atg.transaction.annotation.PropagationLevel;

/**
 * Servlet Filter for wrapping every HTTP Request with an ATG Transaction.
 * 
 * @author Vihung Marathe
 * 
 */
public class ATGTransactionFilter implements Filter {
    // List of HTTP Methods for which a Transaction is required
    private final static Set<String> REQUIRED_METHODS = new HashSet<String>(Arrays.asList("PUT", "POST", "DELETE"));

    /** Logger for ATGTransactionFilter */
    private static final Logger log = Logger.getLogger(ATGTransactionFilter.class);

    private int mPropagationLevel;

    private JndiComponentResolver mTransactionManagerResolver;

    /**
     * Default constructor. Delegates to <code>super()</code>.
     */
    public ATGTransactionFilter() {
        super();
    }

    /**
     * @see javax.servlet.Filter#destroy()
     */
    public void destroy() {
        if (log.isDebugEnabled()) log.debug("destroy(): Destroyed");
    }

    /**
     * 
     * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
     */
    public void doFilter(final ServletRequest pRequest, final ServletResponse pResponse, final FilterChain pFilterChain) throws IOException, ServletException {
        final HttpServletRequest request = (HttpServletRequest) pRequest;
        final String method = request.getMethod();

        if (log.isDebugEnabled()) log.debug("doFilter(): Invoked. method=" + method + ", requestURI=" + request.getRequestURI());

        if (!REQUIRED_METHODS.contains(method)) {
            if (log.isDebugEnabled()) log.debug("doFilter(): Transaction not required");
            // Send the request down the chain
            pFilterChain.doFilter(pRequest, pResponse);
        } else {
            if (log.isDebugEnabled()) log.debug("doFilter(): Transaction required");

            // assume failure
            boolean success = false;

            TransactionDemarcation td = new TransactionDemarcation();

            try {
                // Resolve the Transaction Manager component
                TransactionManager transactionManager = (TransactionManager) mTransactionManagerResolver.resolveComponent();

                // Begin a transaction with the selected propagation level
                td.begin(transactionManager, mPropagationLevel);

                // Send the request down the chain
                pFilterChain.doFilter(pRequest, pResponse);
                success = true;
            } catch (TransactionDemarcationException e) {
                log.error(e.getMessage(), e);
                success = false;
            } catch (UnknownComponentException e) {
                log.error(e.getMessage(), e);
                success = false;
            } finally {
                try {
                    if (log.isDebugEnabled()) log.debug("doFilter(): Ending transaction with success=" + success);
                    td.end(!success);
                } catch (TransactionDemarcationException e) {
                    throw new ServletException(e.getMessage(), e);
                }
            }
        }
    }

    /**
     * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
     */
    public void init(final FilterConfig pFilterConfig) throws ServletException {
        if (log.isDebugEnabled()) log.debug("init(): Initialised");

        String progatationLevelParam = pFilterConfig.getInitParameter("propagationLevel");
        if (log.isDebugEnabled()) log.debug("init(): progatationLevelParam=" + progatationLevelParam);

        PropagationLevel propagationLevel = PropagationLevel.valueOf(progatationLevelParam);

        switch (propagationLevel) {
        case REQUIRES_NEW:
            mPropagationLevel = TransactionDemarcation.REQUIRES_NEW;
            break;
        case MANDATORY:
            mPropagationLevel = TransactionDemarcation.MANDATORY;
            break;
        case NEVER:
            mPropagationLevel = TransactionDemarcation.NEVER;
            break;
        case NOT_SUPPORTED:
            mPropagationLevel = TransactionDemarcation.NOT_SUPPORTED;
            break;
        case SUPPORTS:
            mPropagationLevel = TransactionDemarcation.SUPPORTS;
            break;
        default: // case REQUIRED
            mPropagationLevel = TransactionDemarcation.REQUIRED;
            break;
        }

        mTransactionManagerResolver = new JndiComponentResolver();
        mTransactionManagerResolver.setComponentName("/atg/dynamo/transaction/TransactionManager");
    }

}
