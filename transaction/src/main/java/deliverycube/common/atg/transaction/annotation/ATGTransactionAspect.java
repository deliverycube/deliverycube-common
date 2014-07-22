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
package deliverycube.common.atg.transaction.annotation;

import javax.transaction.TransactionManager;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import atg.dtm.TransactionDemarcation;
import deliverycube.common.atg.componentresolver.jndi.JndiComponentResolver;

/**
 * Aspect for wrapping an ATG transaction around a method call
 * 
 * Requires aspectjweaver-1.6.11.jar provided with javaagent at runtime
 * 
 * @author Akash Motwani
 */
@Aspect
public class ATGTransactionAspect {

    @Pointcut("execution(public * *(..)) && @annotation(pATGTransaction)")
    private void myTransactionMethod(final ProceedingJoinPoint pJoinPoint, final ATGTransaction pATGTransaction) {
        // No code needed here, just empty method
    }

    @Around("myTransactionMethod(pJoinPoint,pATGTransaction)")
    public Object doStartTransaction(final ProceedingJoinPoint pJoinPoint, final ATGTransaction pATGTransaction) throws Throwable {
        JndiComponentResolver transactionManagerResolver = new JndiComponentResolver();
        transactionManagerResolver.setComponentName("/atg/dynamo/transaction/TransactionManager");

        final int lPropagationLevel;

        switch (pATGTransaction.propagationLevel()) {
        case REQUIRES_NEW:
            lPropagationLevel = TransactionDemarcation.REQUIRES_NEW;
            break;
        case MANDATORY:
            lPropagationLevel = TransactionDemarcation.MANDATORY;
            break;
        case NEVER:
            lPropagationLevel = TransactionDemarcation.NEVER;
            break;
        case NOT_SUPPORTED:
            lPropagationLevel = TransactionDemarcation.NOT_SUPPORTED;
            break;
        case SUPPORTS:
            lPropagationLevel = TransactionDemarcation.SUPPORTS;
            break;
        default: // case REQUIRED
            lPropagationLevel = TransactionDemarcation.REQUIRED;
            break;
        }

        // Assume the method invocation fails
        boolean success = false;

        // the object to return.
        Object result = null;

        final TransactionDemarcation td = new TransactionDemarcation();

        try {
            // Resolve the Transaction Manager component
            TransactionManager transactionManager = (TransactionManager) transactionManagerResolver.resolveComponent();

            // Begin a transaction with the selected propagation level
            td.begin(transactionManager, lPropagationLevel);

            // Invoke the wrapped method
            result = pJoinPoint.proceed();

            success = true;
        } finally {
            td.end(!success);
        }

        return result;
    }
}
