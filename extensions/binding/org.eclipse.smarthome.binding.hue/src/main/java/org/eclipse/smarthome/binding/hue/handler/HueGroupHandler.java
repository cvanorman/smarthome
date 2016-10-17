package org.eclipse.smarthome.binding.hue.handler;

import static org.eclipse.smarthome.binding.hue.HueBindingConstants.*;

import org.eclipse.smarthome.binding.hue.internal.HueCache;
import org.eclipse.smarthome.binding.hue.internal.LightState;
import org.eclipse.smarthome.binding.hue.internal.converter.LightStateConverter;
import org.eclipse.smarthome.core.thing.Bridge;
import org.eclipse.smarthome.core.thing.ChannelUID;
import org.eclipse.smarthome.core.thing.Thing;
import org.eclipse.smarthome.core.thing.ThingStatus;
import org.eclipse.smarthome.core.thing.ThingStatusDetail;
import org.eclipse.smarthome.core.thing.binding.BaseThingHandler;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HueGroupHandler extends BaseThingHandler implements CacheUpdatedListener {

    private HueBridgeHandler bridgeHandler;
    private LightStateConverter lightStateConverter = new LightStateConverter();

    private Logger logger = LoggerFactory.getLogger(HueGroupHandler.class);

    public HueGroupHandler(Thing thing) {
        super(thing);
    }

    @Override
    public void initialize() {
        super.initialize();

        getHueBridgeHandler();
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
            hueBridge.updateGroupState(getThingId(), lightState);
        }
    }

    @Override
    public void cacheUpdated(HueCache cache) {
        LightState newState = cache.getGroupState(getThing().getUID().getId());

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
        String[] channels = { CHANNEL_COLOR, CHANNEL_COLORTEMPERATURE, CHANNEL_BRIGHTNESS, CHANNEL_ALERT };

        for (String channel : channels) {
            State state = lightStateConverter.convert(channel, newState);

            if (state != null) {
                updateState(channel, state);
            }
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

    private String getThingId() {
        return getThing().getUID().getId();
    }
}
