package org.eclipse.smarthome.binding.hue.internal.converter;

import org.eclipse.smarthome.binding.hue.HueBindingConstants;
import org.eclipse.smarthome.binding.hue.internal.LightState;
import org.eclipse.smarthome.core.library.types.DecimalType;
import org.eclipse.smarthome.core.library.types.HSBType;
import org.eclipse.smarthome.core.library.types.IncreaseDecreaseType;
import org.eclipse.smarthome.core.library.types.OnOffType;
import org.eclipse.smarthome.core.library.types.PercentType;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.State;

public class ColorConverter extends BrightnessConverter {

    @Override
    public String getChannelId() {
        return HueBindingConstants.CHANNEL_COLOR;
    }

    @Override
    public LightState convert(Command command) {
        LightState lightState = null;

        if (command instanceof HSBType) {
            HSBType hsbCommand = (HSBType) command;
            if (hsbCommand.getBrightness().intValue() == 0) {
                lightState = toOnOffLightState(OnOffType.OFF);
            } else {
                lightState = toColorLightState(hsbCommand);
            }
        } else if (command instanceof PercentType) {
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
        int hue = lightState.getHue();

        int saturationInPercent = (int) (lightState.getSaturation() / SATURATION_FACTOR);
        int brightnessInPercent = (int) (lightState.getBrightness() / BRIGHTNESS_FACTOR);

        saturationInPercent = restrictToBounds(saturationInPercent);
        brightnessInPercent = restrictToBounds(brightnessInPercent);

        HSBType hsbType = new HSBType(new DecimalType(hue / HUE_FACTOR), new PercentType(saturationInPercent),
                new PercentType(brightnessInPercent));

        return hsbType;
    }

    /**
     * Transforms the given {@link HSBType} into a light state.
     *
     * @param hsbType
     *            HSB type
     * @return light state representing the {@link HSBType}.
     */
    protected LightState toColorLightState(HSBType hsbType) {
        int hue = (int) Math.round(hsbType.getHue().doubleValue() * HUE_FACTOR);
        int saturation = (int) Math.round(hsbType.getSaturation().doubleValue() * SATURATION_FACTOR);
        int brightness = (int) Math.round(hsbType.getBrightness().doubleValue() * BRIGHTNESS_FACTOR);

        LightState lightState = new LightState();
        lightState.setHue(hue);
        lightState.setSaturation(saturation);
        if (brightness > 0) {
            lightState.setBrightness(brightness);
        }
        return lightState;
    }

}
