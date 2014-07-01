package deliverycube.common.atg.componentresolver.jndi;

import javax.naming.Context;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import atg.core.util.StringUtils;
import deliverycube.common.atg.componentresolver.ComponentResolver;
import deliverycube.common.atg.componentresolver.UnknownComponentException;

/**
 * Implementation of the {@link ComponentResolver} interface that uses the JNDI to resolve components.
 * 
 * This is a stateful object - the <code>componentName</code> property must be set.
 * 
 * @author Vihung Marathe
 * 
 */
public class JndiComponentResolver implements ComponentResolver {
    private static final Logger log = Logger.getLogger(JndiComponentResolver.class);

    /**
     * 
     */
    public JndiComponentResolver() {
        super();
    }

    /**
     * Uses JNDI to resolve components in any scope at invocation time
     * 
     * @throws IllegalArgumentException
     *             if no componentName has been set
     * @throws UnknownComponentException
     *             if the component cannot be resolved
     * @see deliverycube.deliverycube.common.atg.componentresolver.ComponentResolver#resolveComponent()
     */
    @Override
    public Object resolveComponent() throws UnknownComponentException {
        if (StringUtils.isEmpty(mComponentName)) throw new IllegalArgumentException("No componentName set");

        if (log.isDebugEnabled()) log.debug("componentName=" + mComponentName);
        final String jndiName = "dynamo:" + mComponentName;
        if (log.isDebugEnabled()) log.debug("jndiName=" + jndiName);

        try {
            final Context ctx = new javax.naming.InitialContext();

            final Object component = ctx.lookup(jndiName);
            if (log.isDebugEnabled()) log.debug("resolveComponent(): component=" + component);
            if (log.isDebugEnabled()) log.debug("resolveComponent(): component.class=" + component.getClass().getName());

            if (component == null) {
                throw new UnknownComponentException("Component: " + jndiName + " could not be resolved");
            }

            return component;
        } catch (final NamingException e) {
            throw new UnknownComponentException("Component: " + jndiName + " could not be resolved", e);
        }
    }

    /** Handle to the Component Name */
    private String mComponentName;

    /**
     * Accessor for the <code>componentName</code> property
     * 
     * @return the componentName
     */
    public String getComponentName() {
        return mComponentName;
    }

    /**
     * Modifier for the <code>componentName</code> property
     * 
     * @param pComponentName
     *            the componentName to set
     */
    public void setComponentName(final String pComponentName) {
        mComponentName = pComponentName;
    }

}
