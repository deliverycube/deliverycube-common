package deliverycube.common.atg.componentresolver.dynamo;

import org.apache.log4j.Logger;

import atg.core.util.StringUtils;
import atg.nucleus.naming.ComponentName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.ServletUtil;
import deliverycube.common.atg.componentresolver.ComponentResolver;
import deliverycube.common.atg.componentresolver.UnknownComponentException;

/**
 * Implementation of the {@link ComponentResolver} interface that uses the Dynamo Request to resolve components.
 * 
 * This is a stateful object - the <code>componentName</code> property must be set.
 * 
 * @author Vihung Marathe
 * 
 */
public class DynamoComponentResolver implements ComponentResolver {
    private static final Logger log = Logger.getLogger(DynamoComponentResolver.class);

    /**
     * 
     */
    public DynamoComponentResolver() {
        super();
    }

    /**
     * Uses the Current Dynamo Request to resolve components in any scope
     * 
     * @throws IllegalArgumentException
     *             if no componentName has been set
     * @throws NullPointerException
     *             if there is no current Dynamo request
     * @throws UnknownComponentException
     *             if there is a current Dynamo request, but the component cannot be resolved
     * @see deliverycube.deliverycube.common.atg.componentresolver.ComponentResolver#resolveComponent()
     */
    @Override
    public Object resolveComponent() throws UnknownComponentException {
        if (StringUtils.isEmpty(mComponentName)) throw new IllegalArgumentException("No componentName set");

        final DynamoHttpServletRequest dynamoRequest = ServletUtil.getCurrentRequest();
        if (log.isDebugEnabled()) log.debug("resolveComponent(): dynamoRequest=" + dynamoRequest.getRequestURIWithQueryString());
        if (dynamoRequest == null) throw new NullPointerException("No valid Dynamo Request found");

        final ComponentName componentName = ComponentName.getComponentName(mComponentName);
        if (log.isDebugEnabled()) log.debug("resolveComponent(): componentName=" + componentName);

        final Object component = dynamoRequest.resolveName(componentName);
        if (log.isDebugEnabled()) log.debug("resolveComponent(): component=" + component);
        if (log.isDebugEnabled()) log.debug("resolveComponent(): component.class=" + component.getClass().getName());

        if (component == null) {
            throw new UnknownComponentException("Component: " + componentName + " could not be resolved");
        }

        return component;
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
