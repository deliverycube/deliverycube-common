/**
 * 
 */
package deliverycube.common.atg.featuretoggles;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import atg.core.util.StringUtils;
import atg.nucleus.GenericService;

/**
 * <p>
 * Implementation of {@link FeatureToggleService} that uses a configured
 * {@link Properties} property to determine whether a feature toggle is on or
 * not.
 * </p>
 * 
 * <p>
 * This is intended to be a globally scoped Nucleus component. Consuming
 * components should refer to it by the {@link FeatureToggleService} interface,
 * not by this class
 * </p>
 * 
 * <p>
 * The {@link Properties} are expected to be configured something along the
 * lines of
 * 
 * <pre>
 *     feature1=true,\
 *     feature2.capability1=false,\
 *     feature2.capability2=true
 * </pre>
 * 
 * </p>
 * 
 * <p>
 * This implementation loads values from the {@link Properties} as needed and
 * caches the results. To aid development, it also stores a local list of all
 * feature queries
 * </p>
 * 
 * @author Vihung Marathe
 */
public class FeatureToggleServiceImpl extends GenericService implements FeatureToggleService {
    /** The local cache of (featureName --&gt; featureToggle) */
    private final Map<String, Boolean> mFeatureCache = new HashMap<String, Boolean>();

    /**
     * A local cache of all queries made - useful for determining whether any
     * features need to be added to the list
     */
    private final Set<String> mQueries = new HashSet<String>();

    /**
     * Default Constructor. Delegates to <code>super()</code>.
     */
    public FeatureToggleServiceImpl() {
        super();
    }

    /**
     * @see FeatureToggleService#isFeatureToggled(java.lang.String)
     */
    public boolean isFeatureToggled(final String pFeature) {
        if (isLoggingDebug()) logDebug("isFeatureToggled(): Invoked. pFeature=" + pFeature);
        logFeatureToggleQuery(pFeature);

        // The value to return
        final boolean featureToggle;

        // see if we have cached the result of this query before?

        final Boolean cachedValue = getCachedFeatureToggle(pFeature);
        if (isLoggingDebug()) logDebug("isFeatureToggled(): cachedValue=" + cachedValue);

        if (cachedValue != null) {
            // get the cached value
            featureToggle = cachedValue.booleanValue();
            if (isLoggingDebug()) logDebug("isFeatureToggled(): Cached featureToggle=" + featureToggle);
        } else {
            // determine from the configured values
            featureToggle = loadFeatureToggle(pFeature);
            if (isLoggingDebug()) logDebug("isFeatureToggled(): Loaded featureToggle=" + featureToggle);
            // cache the value
            saveCachedFeatureToggle(pFeature, Boolean.valueOf(featureToggle));
        }

        if (isLoggingDebug()) logDebug("isFeatureToggled(): Returning featureToggle=" + featureToggle);
        return featureToggle;
    }

    /**
     * Load the feature toggle value for a given feature from the configured
     * properties
     * 
     * @param pFeature
     *            the feature
     * @return <code>true</code> if on, <code>false</code> if explicitly off, or
     *         if not specified
     */
    private boolean loadFeatureToggle(final String pFeature) {
        if (isLoggingDebug()) logDebug("loadFeatureToggle(): Invoked. pFeature=" + pFeature);
        final boolean featureToggle;

        final String featureToggleValue = mFeatureToggles.getProperty(pFeature);
        if (isLoggingDebug()) logDebug("loadFeatureToggle(): featureToggleValue=" + featureToggleValue);

        // is a value defined?
        if (StringUtils.isEmpty(featureToggleValue)) {
            // not defined, so default to false
            featureToggle = false;
            if (isLoggingDebug()) logDebug("loadFeatureToggle(): Value not defined. featureToggle=" + featureToggle);
        } else {
            // it is defined, so convert to a boolean
            featureToggle = Boolean.parseBoolean(featureToggleValue);
            if (isLoggingDebug()) logDebug("loadFeatureToggle(): Value defined. Parsed featureToggle=" + featureToggle);
        }

        if (isLoggingDebug()) logDebug("loadFeatureToggle(): Returning featureToggle=" + featureToggle);
        return featureToggle;
    }

    /**
     * Log the query to the local list of all queries
     * 
     * @param pFeature
     *            the feature name
     */
    private void logFeatureToggleQuery(final String pFeature) {
        if (isLoggingDebug()) logDebug("logFeatureToggleQuery(): Invoked. Adding pFeature=" + pFeature);
        mQueries.add(pFeature);
    }

    /**
     * Get the cached feature toggle value
     * 
     * @param pFeature
     *            the feature name
     * @return Boolean representing the toggle, or <code>null</code> if not
     *         cached
     */
    private Boolean getCachedFeatureToggle(String pFeature) {
        return mFeatureCache.get(pFeature);
    }

    /**
     * Save the loaded value to a local cache
     * 
     * @param pFeature
     *            the feature name
     * @param pFeatureToggleValue
     */
    private void saveCachedFeatureToggle(String pFeature, Boolean pFeatureToggleValue) {
        mFeatureCache.put(pFeature, pFeatureToggleValue);
    }

    /**
     * {@link Properties} for holding the configured status of the features
     */
    private Properties mFeatureToggles;

    /**
     * Accessor method for the <code>featureToggles</code> property
     * 
     * @return the <code>featureToggles</code> property
     */
    public Properties getFeatureToggles() {
        return mFeatureToggles;
    }

    /**
     * Modifier method for the <code>featureToggles</code> property
     * 
     * @param pFeatureToggles
     *            - the <code>featureToggles</code> property to set
     */
    public void setFeatureToggles(Properties pFeatureToggles) {
        mFeatureToggles = pFeatureToggles;
    }

    /**
     * Accessor method for the local list of queries. This should be used during
     * development to query. The resulting {@link Set} should not be modified.
     * 
     * @return the local set of queries.
     */
    public Set<String> getQueries() {
        return mQueries;
    }

    /**
     * Clear the cache
     */
    public void clearCache() {
        mFeatureCache.clear();
    }
}
