package org.eclipse.smarthome.binding.hue.internal.converter;

import org.eclipse.smarthome.binding.hue.HueBindingConstants;
import org.eclipse.smarthome.binding.hue.internal.LightState;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.State;

public class SwitchConverter extends BaseChannelCommandConverter {

    @Override
    public String getChannelId() {
        return HueBindingConstants.CHANNEL_SWITCH;
    }

    @Override
    public LightState convert(Command command) {
        LightState lightState = null;
        if (command instanceof OnOffType) {
            lightState = toOnOffLightState((OnOffType) command);
        }

        return lightState;
    }

    @Override
    public State convert(LightState lightState) {
        return lightState.isOn() ? OnOffType.ON : OnOffType.OFF;
    }

}
