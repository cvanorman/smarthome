package org.eclipse.smarthome.binding.hue.internal.converter;

import org.eclipse.smarthome.binding.hue.internal.LightState;
import org.eclipse.smarthome.core.library.types.OnOffType;

public abstract class BaseChannelCommandConverter implements ChannelCommandConverter {

    protected static final int DIM_STEPSIZE = 30;
    protected static final int HUE_FACTOR = 182;
    protected static final double SATURATION_FACTOR = 2.54;
    protected static final double BRIGHTNESS_FACTOR = 2.54;

    private boolean osramPar16;

    /**
     * Transforms the given {@link OnOffType} into a light state containing the
     * 'on' value.
     *
     * @param onOffType
     *            on or off state
     * @return light state containing the 'on' value
     */
    protected LightState toOnOffLightState(OnOffType onOffType) {
        LightState state = new LightState();
        state.setOn(OnOffType.ON.equals(onOffType));

        if (osramPar16) {
            addOsramSpecificCommands(state, onOffType);
        }

        return state;
    }

    /*
     * Applies additional {@link StateUpdate} commands as a workaround for Osram
     * Lightify PAR16 TW firmware bug. Also see
     * http://www.everyhue.com/vanilla/discussion
     * /1756/solved-lightify-turning-off
     */
    private LightState addOsramSpecificCommands(LightState lightState, OnOffType actionType) {
        if (actionType.equals(OnOffType.ON)) {
            lightState.setBrightness(254);
        } else {
            lightState.setTransitionTime(0);
        }

        return lightState;
    }

    protected static int restrictToBounds(int percentValue) {
        if (percentValue < 0) {
            return 0;
        } else if (percentValue > 100) {
            return 100;
        }
        return percentValue;
    }

}
