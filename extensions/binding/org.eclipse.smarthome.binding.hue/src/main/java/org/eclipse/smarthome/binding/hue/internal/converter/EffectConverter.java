package org.eclipse.smarthome.binding.hue.internal.converter;

import org.eclipse.smarthome.binding.hue.HueBindingConstants;
import org.eclipse.smarthome.binding.hue.internal.LightState;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.State;

import com.philips.lighting.model.PHLight.PHLightEffectMode;

public class EffectConverter extends BaseChannelCommandConverter {

    @Override
    public String getChannelId() {
        return HueBindingConstants.CHANNEL_EFFECT;
    }

    @Override
    public LightState convert(Command command) {
        LightState lightState = null;

        if (command instanceof OnOffType) {
            lightState = toOnOffEffectState((OnOffType) command);
        }

        return lightState;
    }

    @Override
    public State convert(LightState lightState) {
        return null;
    }

    /**
     * Transforms the given {@link OnOffType} into a light state containing the {@link Effect} value.
     * {@link OnOffType#ON} will result in {@link Effect#COLORLOOP}. {@link OnOffType#OFF} will result in
     * {@link Effect#NONE}.
     *
     * @param onOffType
     *            on or off state
     * @return light state containing the {@link Effect} value
     */
    protected LightState toOnOffEffectState(OnOffType onOffType) {
        LightState lightState = new LightState();

        if (OnOffType.ON.equals(onOffType)) {
            lightState.setEffectMode(PHLightEffectMode.EFFECT_COLORLOOP);
        } else {
            lightState.setEffectMode(PHLightEffectMode.EFFECT_NONE);
        }

        return lightState;
    }

}
