package deliverycube.common.atg.featuretoggles;

/**
 * Interface describing a service to determine whether a feature is enabled or
 * not
 * 
 * @author Vihung Marathe
 */
public interface FeatureToggleService {

    /**
     * Determine the value of the feature toggle for the given feature.
     * 
     * @param pFeature
     *            the feature
     * @return the feature toggle - <code>true</code> if on, <code>false</code>
     *         if off or unspecified.
     */
    public abstract boolean isFeatureToggled(final String pFeature);

}