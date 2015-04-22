/**
 * 
 */
package it.geosolutions.opensdi2.wps.rest.plugin;

import it.geosolutions.opensdi2.config.OpenSDIManagerConfigExtensions;
import it.geosolutions.opensdi2.configurations.model.OSDIConfigurationKVP;
import it.geosolutions.opensdi2.rest.RestPlugin;
import it.geosolutions.opensdi2.rest.RestService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author alessio.fabiani
 * 
 */
public class RestWPSPlugin implements RestPlugin {

    Set<RestService> wpsProcesses = Collections
            .newSetFromMap(new ConcurrentHashMap<RestService, Boolean>());

    OSDIConfigurationKVP configuration;

    /*
     * (non-Javadoc)
     * 
     * @see it.geosolutions.opensdi2.rest.RestPlugin#getPluginName()
     */
    @Override
    public String getPluginName() {
        return "wps";
    }

    /*
     * (non-Javadoc)
     * 
     * @see it.geosolutions.opensdi2.rest.RestPlugin#getDescription()
     */
    @Override
    public String getDescription() {
        return "WPS REST Plugin for OpenSDI2-Manager V.1.0";
    }

    /*
     * (non-Javadoc)
     * 
     * @see it.geosolutions.opensdi2.rest.RestPlugin#getVersion()
     */
    @Override
    public String getVersion() {
        return "1.0";
    }

    /*
     * (non-Javadoc)
     * 
     * @see it.geosolutions.opensdi2.rest.RestPlugin#getServices()
     */
    @Override
    public Set<RestService> getServices() throws Exception {

        if (wpsProcesses.isEmpty()) {
            List<RestWPSProcess> wpsAvailableProcesses = OpenSDIManagerConfigExtensions
                    .extensions(RestWPSProcess.class);

            if (wpsAvailableProcesses != null) {
                for (RestWPSProcess process : wpsAvailableProcesses) {
                    if ("ENABLED".equals(process.getActiveStatus())) {
                        wpsProcesses.add(process);
                    }
                }
            }
        }

        return wpsProcesses;
    }

    @Override
    public Set<RestService> getAllServices() throws Exception {
        List<RestWPSProcess> wpsAvailableProcesses = OpenSDIManagerConfigExtensions
                .extensions(RestWPSProcess.class);

        Set<RestService> wpsProcesses = Collections
                .newSetFromMap(new ConcurrentHashMap<RestService, Boolean>());
        wpsProcesses.addAll(wpsAvailableProcesses);
        return wpsProcesses;
    }

    @Override
    public boolean supportsQueries() {
        return false;
    }

    @Override
    public int countServices() {
        return 0;
    }

    @Override
    public List<RestService> findServices(String serviceId, String name, String activeStatus,
            Map<String, String> params, int page, int pageSize) {
        return null;
    }

    @Override
    public RestService getService(String serviceId) {
        return null;
    }

    @Override
    public void setConfiguration(OSDIConfigurationKVP config) {
        this.configuration = config;
    }

    @Override
    public OSDIConfigurationKVP getConfiguration() {
        return this.configuration;
    }

}
