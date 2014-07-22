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
