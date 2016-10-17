/**
 *Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.binding.hue.handler;

import static org.eclipse.smarthome.binding.hue.HueBindingConstants.*;

import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.smarthome.binding.hue.internal.HueCache;
import org.eclipse.smarthome.binding.hue.internal.HueConfigStatusMessage;
import org.eclipse.smarthome.binding.hue.internal.LightState;
import org.eclipse.smarthome.binding.hue.internal.discovery.HueLightDiscoveryService;
import org.eclipse.smarthome.config.core.status.ConfigStatusMessage;
import org.eclipse.smarthome.config.discovery.DiscoveryService;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.ConfigStatusBridgeHandler;
import org.eclipse.smarthome.core.types.Command;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.philips.lighting.hue.listener.PHLightListener;
import com.philips.lighting.hue.sdk.PHAccessPoint;
import com.philips.lighting.hue.sdk.PHHueSDK;
import com.philips.lighting.hue.sdk.PHSDKListener;
import com.philips.lighting.model.PHBridge;
import com.philips.lighting.model.PHBridgeConfiguration;
import com.philips.lighting.model.PHGroup;
import com.philips.lighting.model.PHHueParsingError;
import com.philips.lighting.model.PHLight;

/**
 * {@link HueBridgeHandler} is the handler for a hue bridge and connects it to
 * the framework. All {@link HueLightHandler}s use the {@link HueBridgeHandler} to execute the actual commands.
 *
 * @author Dennis Nobel - Initial contribution of hue binding
 * @author Oliver Libutzki
 * @author Kai Kreuzer - improved state handling
 * @author Andre Fuechsel - implemented getFullLights(), startSearch()
 * @author Thomas Höfer - added thing properties
 * @author Stefan Bußweiler - Added new thing status handling
 * @author Jochen Hiller - fixed status updates, use reachable=true/false for state compare
 */
public class HueBridgeHandler extends ConfigStatusBridgeHandler implements PHSDKListener {

    private static final int DEFAULT_POLLING_INTERVAL = 10; // in seconds

    private static final String DEVICE_TYPE = "EclipseSmartHome";

    private Logger logger = LoggerFactory.getLogger(HueBridgeHandler.class);

    private HueCache lastCache = new HueCache(null);

    private List<CacheUpdatedListener> cacheUpdatedListeners = new CopyOnWriteArrayList<>();

    private ServiceRegistration<?> discoveryService;

    private PHBridge hueBridge = null;
    private PHHueSDK hueSDK = null;

    private PHHueSDK getSDK() {
        if (hueSDK == null) {
            hueSDK = PHHueSDK.getInstance();
            hueSDK.setAppName(DEVICE_TYPE);
            hueSDK.setDeviceName(DEVICE_TYPE);
            hueSDK.getNotificationManager().registerSDKListener(this);
        }

        return hueSDK;
    }

    public HueBridgeHandler(Bridge hueBridge) {
        super(hueBridge);
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {
        // not needed
    }

    public void updateGroupState(String groupId, LightState lightState) {
        if (hueBridge != null) {
            hueBridge.setLightStateForGroup(groupId, lightState.getPHLightState());
        }
    }

    public void updateLightState(String id, LightState lightState) {
        if (hueBridge != null) {
            // hueBridge.updateLightState(light, stateUpdate);
            hueBridge.updateLightState(id, lightState.getPHLightState(), null);
        } else {
            logger.warn("No bridge connected or selected. Cannot set light state.");
        }
    }

    @Override
    public void dispose() {
        unregisterDiscovery();

        if (hueBridge != null) {
            getSDK().disableHeartbeat(hueBridge);
            getSDK().getNotificationManager().unregisterSDKListener(this);
            getSDK().disconnect(hueBridge);

            // TODO This may not be appropriate if there is more than one bridge
            getSDK().destroySDK();
        }
    }

    @Override
    public void initialize() {
        logger.debug("Initializing hue bridge handler.");

        registerDiscovery();

        if (getConfig().get(HOST) != null) {
            if (hueBridge == null) {
                logger.debug("Connecting to access point:{}", getConfig().get(HOST));

                PHAccessPoint accessPoint = new PHAccessPoint();
                accessPoint.setIpAddress((String) getConfig().get(HOST));
                accessPoint.setUsername((String) getConfig().get(USER_NAME));
                getSDK().connect(accessPoint);
            }
        } else {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.OFFLINE.CONFIGURATION_ERROR,
                    "Cannot connect to hue bridge. IP address not set.");
        }
    }

    private void registerDiscovery() {
        HueLightDiscoveryService discoveryService = new HueLightDiscoveryService(this);
        discoveryService.activate();
        this.discoveryService = bundleContext.registerService(DiscoveryService.class.getName(), discoveryService,
                new Hashtable<String, Object>());
    }

    private void unregisterDiscovery() {
        ServiceRegistration<?> serviceReg = this.discoveryService;
        if (serviceReg != null) {
            // remove discovery service, if bridge handler is removed
            HueLightDiscoveryService service = (HueLightDiscoveryService) bundleContext
                    .getService(serviceReg.getReference());
            service.deactivate();
            serviceReg.unregister();
        }
    }

    private int getPollingInterval() {
        int pollingInterval = DEFAULT_POLLING_INTERVAL;
        Object pollingIntervalConfig = getConfig().get(POLLING_INTERVAL);

        if (pollingIntervalConfig != null) {
            pollingInterval = ((Number) pollingIntervalConfig).intValue();
        } else {
            logger.info("Polling interval not configured for this hue bridge. Using default value: {}s",
                    pollingInterval);
        }

        // logger.info("Wrong configuration value for polling interval. Using default value: {}s",
        // pollingInterval);

        return pollingInterval * 1000;

    }

    // private void updateBridgeThingConfiguration(String userName) {
    // Configuration config = editConfiguration();
    // config.put(USER_NAME, userName);
    // try {
    // updateConfiguration(config);
    // logger.debug("Updated configuration parameter {} to '{}'", USER_NAME, userName);
    // } catch (IllegalStateException e) {
    // logger.trace("Configuration update failed.", e);
    // logger.warn("Unable to update configuration of Hue bridge.");
    // logger.warn("Please configure the following user name manually: {}", userName);
    // }
    // }
    //
    // private void handleAuthenticationFailure(Exception ex, String userName) {
    // logger.warn("User {} is not authenticated on Hue bridge {}", userName, getConfig().get(HOST));
    // logger.warn("Please configure a valid user or remove user from configuration to generate a new one.");
    // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.OFFLINE.CONFIGURATION_ERROR,
    // "Authentication failed - remove user name from configuration to generate a new one.");
    // }
    //
    // private void handleLinkButtonNotPressed(LinkButtonException ex) {
    // logger.debug("Failed creating new user on Hue bridge: {}", ex.getMessage());
    // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.OFFLINE.CONFIGURATION_ERROR,
    // "Not authenticated - press pairing button on the bridge.");
    // }
    //
    // private void handleExceptionWhileCreatingUser(Exception ex) {
    // logger.warn("Failed creating new user on Hue bridge", ex);
    // updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.OFFLINE.CONFIGURATION_ERROR,
    // "Failed to create new user on bridge: " + ex.getMessage());
    // }

    public boolean registerCacheUpdatedListener(CacheUpdatedListener cacheUpdatedListener) {
        if (cacheUpdatedListener == null) {
            throw new NullPointerException("It's not allowed to pass a null CacheUpdatedListener.");
        }
        boolean result = cacheUpdatedListeners.add(cacheUpdatedListener);
        if (result) {
            cacheUpdatedListener.cacheUpdated(lastCache);
        }
        return result;
    }

    public boolean unregisterCacheUpdatedListener(CacheUpdatedListener lightStatusListener) {
        boolean result = cacheUpdatedListeners.remove(lightStatusListener);
        return result;
    }

    public PHLight getLightById(String lightId) {
        return lastCache.getLight(lightId);
    }

    public Collection<PHGroup> getFullGroups() {
        if (hueBridge != null) {
            return hueBridge.getResourceCache().getAllGroups();
        }

        return Collections.emptyList();
    }

    public Collection<PHLight> getFullLights() {
        if (hueBridge != null) {
            return hueBridge.getResourceCache().getAllLights();
        }

        return Collections.emptyList();
    }

    public void startSearch(PHLightListener listener) {
        hueBridge.findNewLights(listener);
    }

    public void startSearch(List<String> serialNumbers, PHLightListener listener) {
        hueBridge.findNewLightsWithSerials(serialNumbers, listener);
    }

    @Override
    public Collection<ConfigStatusMessage> getConfigStatus() {
        // The bridge IP address to be used for checks
        final String bridgeIpAddress = (String) getThing().getConfiguration().get(HOST);
        Collection<ConfigStatusMessage> configStatusMessages;

        // Check whether an IP address is provided
        if (bridgeIpAddress == null || bridgeIpAddress.isEmpty()) {
            configStatusMessages = Collections.singletonList(ConfigStatusMessage.Builder.error(HOST)
                    .withMessageKeySuffix(HueConfigStatusMessage.IP_ADDRESS_MISSING.getMessageKey()).withArguments(HOST)
                    .build());
        } else {
            configStatusMessages = Collections.emptyList();
        }

        return configStatusMessages;
    }

    @Override
    public void onAccessPointsFound(List<PHAccessPoint> accessPoints) {
    }

    @Override
    public void onAuthenticationRequired(PHAccessPoint accessPoint) {
        logger.debug("Push link button within 30 seconds.");
        getSDK().startPushlinkAuthentication(accessPoint);
    }

    @Override
    public void onBridgeConnected(PHBridge b, String username) {
        logger.debug("Hue bridge connected: {}", b);

        hueBridge = b;
        getConfig().put(USER_NAME, username);
        getConfig().put(HOST, b.getResourceCache().getBridgeConfiguration().getIpAddress());

        logger.debug("Turning on heartbeat for {}s.", getPollingInterval());
        getSDK().enableHeartbeat(hueBridge, getPollingInterval());
        updateStatus(ThingStatus.ONLINE);

        updateConfiguration(getConfig());
        logger.debug("Updated configuration parameter {} to '{}'", USER_NAME, username);

    }

    @Override
    public void onCacheUpdated(List<Integer> cacheNotificationsList, PHBridge bridge) {

        logger.debug("Hue cache updated");

        lastCache = new HueCache(bridge.getResourceCache());
        for (CacheUpdatedListener listener : cacheUpdatedListeners) {
            listener.cacheUpdated(lastCache);
        }

        final PHBridgeConfiguration config = bridge.getResourceCache().getBridgeConfiguration();
        if (config != null) {
            Map<String, String> properties = editProperties();
            properties.put(Thing.PROPERTY_SERIAL_NUMBER, config.getMacAddress());
            properties.put(Thing.PROPERTY_FIRMWARE_VERSION, config.getSoftwareVersion());
            properties.put(Thing.PROPERTY_MODEL_ID, config.getModelId());
            updateProperties(properties);
        }
    }

    @Override
    public void onConnectionLost(PHAccessPoint accessPoints) {
        logger.debug("Bridge connection lost. Updating thing status to OFFLINE.");
        updateStatus(ThingStatus.OFFLINE);
    }

    @Override
    public void onConnectionResumed(PHBridge bridge) {
        logger.debug("Bridge connection resumed. Updating thing status to ONLINE.");
        updateStatus(ThingStatus.ONLINE);
    }

    @Override
    public void onError(int code, String message) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onParsingErrors(List<PHHueParsingError> parsingErrors) {
        // TODO Auto-generated method stub

    }
}
