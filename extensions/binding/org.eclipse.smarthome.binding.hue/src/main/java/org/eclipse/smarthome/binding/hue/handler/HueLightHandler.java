/**
 * Copyright (c) 2014-2016 by the respective copyright holders.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.smarthome.binding.hue.handler;

import static org.eclipse.smarthome.binding.hue.HueBindingConstants.*;

import java.util.Set;

import org.eclipse.smarthome.binding.hue.internal.HueCache;
import org.eclipse.smarthome.binding.hue.internal.HueLight;
import org.eclipse.smarthome.binding.hue.internal.LightState;
import org.eclipse.smarthome.binding.hue.internal.converter.LightStateConverter;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.ThingStatusInfo;
import org.eclipse.smarthome.core.thing.ThingTypeUID;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;
import com.philips.lighting.model.PHLight;

/**
 * {@link HueLightHandler} is the handler for a hue light. It uses the {@link HueBridgeHandler} to execute the actual
 * command.
 *
 * @author Dennis Nobel - Initial contribution of hue binding
 * @author Oliver Libutzki
 * @author Kai Kreuzer - stabilized code
 * @author Andre Fuechsel - implemented switch off when brightness == 0, changed to support generic thing types
 * @author Thomas Höfer - added thing properties
 * @author Jochen Hiller - fixed status updates for reachable=true/false
 * @author Markus Mazurczak - added code for command handling of OSRAM PAR16 50
 *         bulbs
 * @author Yordan Zhelev - added alert and effect functions
 *
 */
public class HueLightHandler extends BaseThingHandler implements CacheUpdatedListener {

    public final static Set<ThingTypeUID> SUPPORTED_THING_TYPES = Sets.newHashSet(THING_TYPE_COLOR_LIGHT,
            THING_TYPE_COLOR_TEMPERATURE_LIGHT, THING_TYPE_DIMMABLE_LIGHT, THING_TYPE_EXTENDED_COLOR_LIGHT,
            THING_TYPE_ON_OFF_LIGHT, THING_TYPE_GROUP);

    private Logger logger = LoggerFactory.getLogger(HueLightHandler.class);
    private boolean propertiesInitializedSuccessfully = false;
    private HueBridgeHandler bridgeHandler;
    private LightStateConverter lightStateConverter = new LightStateConverter();

    public HueLightHandler(Thing hueLight) {
        super(hueLight);
    }

    @Override
    public void initialize() {
        logger.debug("Initializing hue light handler.");
        initializeThing((getBridge() == null) ? null : getBridge().getStatus());
    }

    @Override
    public void bridgeStatusChanged(ThingStatusInfo bridgeStatusInfo) {
        logger.debug("bridgeStatusChanged {}", bridgeStatusInfo);
        initializeThing(bridgeStatusInfo.getStatus());
    }

    private void initializeThing(ThingStatus bridgeStatus) {
        logger.debug("initializeThing thing {} bridge status {}", getThing().getUID(), bridgeStatus);
        // note: this call implicitly registers our handler as a listener on
        // the bridge
        if (getHueBridgeHandler() != null) {
            if (bridgeStatus == ThingStatus.ONLINE) {
                updateStatus(ThingStatus.ONLINE);
                initializeProperties();
            } else {
                updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.BRIDGE_OFFLINE);
            }
        } else {
            updateStatus(ThingStatus.OFFLINE);
        }
    }

    private synchronized void initializeProperties() {
        if (!propertiesInitializedSuccessfully) {
            PHLight fullLight = getLight();
            if (fullLight != null) {
                HueLight light = new HueLight(fullLight);
                updateProperties(light.getProperties());
                lightStateConverter.setOsramPar16(light.isOsramPar16());
                propertiesInitializedSuccessfully = true;
            }
        }
    }

    @Override
    public void dispose() {
        logger.debug("Handler disposes. Unregistering listener.");
        if (getThingId() != null) {
            HueBridgeHandler bridgeHandler = getHueBridgeHandler();
            if (bridgeHandler != null) {
                bridgeHandler.unregisterCacheUpdatedListener(this);
                this.bridgeHandler = null;
            }
        }
    }

    private PHLight getLight() {
        HueBridgeHandler bridgeHandler = getHueBridgeHandler();
        if (bridgeHandler != null) {
            return bridgeHandler.getLightById(getThingId());
        }
        return null;
    }

    @Override
    public void handleCommand(ChannelUID channelUID, Command command) {

        HueBridgeHandler hueBridge = getHueBridgeHandler();
        if (hueBridge == null) {
            logger.warn("hue bridge handler not found. Cannot handle command without bridge.");
            return;
        }

        LightState lightState = lightStateConverter.convert(channelUID.getId(), command);

        if (lightState == null) {
            logger.warn("Unsupported command: {}, for channel: {}.  ", command, channelUID.getId());
        } else {
            hueBridge.updateLightState(getThingId(), lightState);
        }
    }

    private synchronized HueBridgeHandler getHueBridgeHandler() {
        if (this.bridgeHandler == null) {
            Bridge bridge = getBridge();
            if (bridge == null) {
                return null;
            }
            ThingHandler handler = bridge.getHandler();
            if (handler instanceof HueBridgeHandler) {
                this.bridgeHandler = (HueBridgeHandler) handler;
                this.bridgeHandler.registerCacheUpdatedListener(this);
            } else {
                return null;
            }
        }
        return this.bridgeHandler;
    }

    @Override
    public void cacheUpdated(HueCache cache) {
        LightState newState = cache.getLightState(getThing().getUID().getId());

        if (newState == null) {
            updateStatus(ThingStatus.OFFLINE);
        } else if (!newState.isReachable()) {
            updateStatus(ThingStatus.OFFLINE, ThingStatusDetail.NONE, "Bridge reports light as not reachable");
        } else {
            updateStatus(ThingStatus.ONLINE);
            updateLightState(newState);
        }
    }

    private void updateLightState(LightState newState) {
        initializeProperties();

        String[] channels = { CHANNEL_COLOR, CHANNEL_COLORTEMPERATURE, CHANNEL_BRIGHTNESS, CHANNEL_ALERT };

        for (String channel : channels) {
            State state = lightStateConverter.convert(channel, newState);

            if (state != null) {
                updateState(channel, state);
            }
        }
    }

    /**
     * @return the lightId
     */
    private String getThingId() {
        return getThing().getUID().getId();
    }
}