package org.eclipse.smarthome.binding.hue.internal.converter;

import org.eclipse.smarthome.binding.hue.HueBindingConstants;
import org.eclipse.smarthome.binding.hue.internal.LightState;
import org.eclipse.smarthome.core.library.types.IncreaseDecreaseType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.PercentType;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.State;

public class ColorTemperatureConverter extends BaseChannelCommandConverter {

    private static final int MIN_COLOR_TEMPERATURE = 153;
    private static final int MAX_COLOR_TEMPERATURE = 500;
    private static final int COLOR_TEMPERATURE_RANGE = MAX_COLOR_TEMPERATURE - MIN_COLOR_TEMPERATURE;

    @Override
    public String getChannelId() {
        return HueBindingConstants.CHANNEL_COLORTEMPERATURE;
    }

    @Override
    public LightState convert(Command command) {
        LightState lightState = null;
        if (command instanceof PercentType) {
            lightState = toColorTemperatureLightState((PercentType) command);
        } else if (command instanceof OnOffType) {
            lightState = toOnOffLightState((OnOffType) command);
        } else if (command instanceof IncreaseDecreaseType) {
            lightState = toColorTemperatureLightState((IncreaseDecreaseType) command);
        }

        return lightState;
    }

    @Override
    public State convert(LightState lightState) {
        int percent = (int) Math
                .round(((lightState.getCt() - MIN_COLOR_TEMPERATURE) * 100.0) / COLOR_TEMPERATURE_RANGE);
        return new PercentType(restrictToBounds(percent));
    }

    /**
     * Transforms the given {@link PercentType} into a light state containing
     * the color temperature represented by {@link PercentType}.
     *
     * @param percentType
     *            color temperature represented as {@link PercentType}
     * @return light state containing the color temperature
     */
    private LightState toColorTemperatureLightState(PercentType percentType) {
        int colorTemperature = MIN_COLOR_TEMPERATURE
                + Math.round((COLOR_TEMPERATURE_RANGE * percentType.floatValue()) / 100);
        LightState stateUpdate = new LightState();
        stateUpdate.setCt(colorTemperature);
        return stateUpdate;
    }

    /**
     * Transforms the given {@link PercentType} into a light state containing
     * the color temperature represented by {@link PercentType}.
     *
     * @param percentType
     *            color temperature represented as {@link PercentType}
     * @return light state containing the color temperature
     */
    private LightState toColorTemperatureLightState(IncreaseDecreaseType increaseDecreaseType) {
        LightState lightState = new LightState();
        if (increaseDecreaseType == IncreaseDecreaseType.INCREASE) {
            lightState.setIncrementCt(DIM_STEPSIZE);
        } else {
            lightState.setIncrementCt(-DIM_STEPSIZE);
        }

        return lightState;
    }
}
