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
package deliverycube.common.atg.featuretoggles;

import java.io.IOException;

import javax.servlet.ServletException;

import atg.core.util.StringUtils;
import atg.nucleus.naming.ParameterName;
import atg.servlet.DynamoHttpServletRequest;
import atg.servlet.DynamoHttpServletResponse;
import atg.servlet.DynamoServlet;

/**
 * Feature Toggle Droplet. Selectively renders an <code>oparam</code> depending
 * on whether the given feature toggle is on or off.
 * 
 * This droplet uses the {@link FeatureToggleService} to determine whether a
 * feature toggle is on.
 * 
 * Expected usage is
 * 
 * <pre>
 * &lt;dsp:droplet name="/deliverycube/common/featuretoggles/FeatureToggleDroplet"&gt;
 *   &lt;dsp:param name="feature" value="feature2.capability1"/&gt;
 *   &lt;dsp:oparam name="on"&gt;
 *     &lt;!-- Content to render if the feature toggle is on --&gt;
 *   &lt;/dsp:oparam&gt;
 *   &lt;dsp:oparam name="off"&gt;
 *     &lt;!-- Content to render if the feature toggle is off --&gt;
 *   &lt;/dsp:oparam&gt;
 * &lt;/dsp:droplet&gt;
 * </pre>
 * 
 * @author Vihung Marathe
 */
public class FeatureToggleDroplet extends DynamoServlet {
    /** The <code>featureName</code> parameter name **/
    private static final ParameterName PARAM_NAME__FEATURE = ParameterName.getParameterName("feature");

    /** The <code>error</code> OPARAM name **/
    private static final ParameterName OPARAM_NAME__ERROR = ParameterName.getParameterName("error");

    /** The <code>enabled</code> OPARAM name **/
    private static final ParameterName OPARAM_NAME__ON = ParameterName.getParameterName("on");

    /** The <code>error</code> OPARAM name **/
    private static final ParameterName OPARAM_NAME__OFF = ParameterName.getParameterName("off");

    /**
     * Default constructor. Defers to <code>super()</code>
     */
    public FeatureToggleDroplet() {
        super();
    }

    /**
     * Determine whether the specified feature (<code>featureName</code> input
     * parameter) is enabled or not, and selectively render the appropriate
     * <code>OPARAM</code>. (<code>enabled</code> if true, <code>disabled</code>
     * if not).
     */
    @Override
    public void service(DynamoHttpServletRequest pRequest, DynamoHttpServletResponse pResponse) throws ServletException, IOException {
        // Read the input parameter
        final String feature = pRequest.getParameter(PARAM_NAME__FEATURE);
        if (isLoggingDebug()) logDebug("service(): feature=" + feature);

        if (StringUtils.isEmpty(feature)) {
            final String errorMessage = "No feature specified";
            logError(errorMessage);
            pRequest.setParameter("message", errorMessage);
            pRequest.serviceLocalParameter(OPARAM_NAME__ERROR, pRequest, pResponse);
        } else {
            boolean featureToggled = getFeatureToggleService().isFeatureToggled(feature);
            if (isLoggingDebug()) logDebug("service(): featureToggled=" + featureToggled);

            if (featureToggled) {
                if (isLoggingDebug()) logDebug("service(): Rendering OPARAM " + OPARAM_NAME__ON);
                pRequest.serviceLocalParameter(OPARAM_NAME__ON, pRequest, pResponse);
            } else {
                if (isLoggingDebug()) logDebug("service(): Rendering OPARAM " + OPARAM_NAME__OFF);
                pRequest.serviceLocalParameter(OPARAM_NAME__OFF, pRequest, pResponse);
            }
        }
    }

    /**
     * The Feature Service
     */
    private FeatureToggleService mFeatureToggleService;

    public FeatureToggleService getFeatureToggleService() {
        return mFeatureToggleService;
    }

    public void setFeatureToggleService(FeatureToggleService pFeatureToggleService) {
        this.mFeatureToggleService = pFeatureToggleService;
    }

}
