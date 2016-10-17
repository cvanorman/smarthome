package org.eclipse.smarthome.binding.hue.internal.converter;

import org.eclipse.smarthome.binding.hue.HueBindingConstants;
import org.eclipse.smarthome.binding.hue.internal.LightState;
import org.eclipse.smarthome.core.library.types.IncreaseDecreaseType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.PercentType;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.State;

public class BrightnessConverter extends BaseChannelCommandConverter {

    @Override
    public String getChannelId() {
        return HueBindingConstants.CHANNEL_BRIGHTNESS;
    }

    @Override
    public LightState convert(Command command) {
        LightState lightState = null;

        if (command instanceof PercentType) {
            lightState = toBrightnessLightState((PercentType) command);
        } else if (command instanceof OnOffType) {
            lightState = toOnOffLightState((OnOffType) command);
        } else if (command instanceof IncreaseDecreaseType) {
            lightState = toBrightnessLightState((IncreaseDecreaseType) command);
        }

        return lightState;
    }

    @Override
    public State convert(LightState lightState) {
        int percent = (int) (lightState.getBrightness() / BRIGHTNESS_FACTOR);
        return new PercentType(restrictToBounds(percent));
    }

    /**
     * Transforms the given {@link PercentType} into a light state containing
     * the brightness and the 'on' value represented by {@link PercentType}.
     *
     * @param percentType
     *            brightness represented as {@link PercentType}
     * @return light state containing the brightness and the 'on' value
     */
    protected LightState toBrightnessLightState(PercentType percentType) {
        boolean on = percentType.equals(PercentType.ZERO) ? false : true;
        final LightState lightState = new LightState();
        lightState.setOn(on);

        int brightness = (int) Math.round(percentType.floatValue() * BRIGHTNESS_FACTOR);
        if (brightness > 0) {
            lightState.setBrightness(brightness);
        }
        return lightState;
    }

    /**
     * Transforms the given {@link IncreaseDecreaseType} into a light state.
     *
     * @param increaseDecreaseType
     *            whether to increase or decrease the light
     * @return light state containing the increment or decrement
     */
    protected LightState toBrightnessLightState(IncreaseDecreaseType increaseDecreaseType) {
        LightState lightState = new LightState();

        if (increaseDecreaseType == IncreaseDecreaseType.INCREASE) {
            lightState.setIncrementBrightness(DIM_STEPSIZE);
        } else {
            lightState.setIncrementBrightness(-DIM_STEPSIZE);
        }

        return lightState;
    }
}
