package org.eclipse.smarthome.binding.hue.internal.converter;

import org.eclipse.smarthome.binding.hue.HueBindingConstants;
import org.eclipse.smarthome.binding.hue.internal.LightState;
import org.eclipse.smarthome.core.library.types.StringType;
import org.eclipse.smarthome.core.types.Command;
import org.eclipse.smarthome.core.types.State;

import com.philips.lighting.model.PHLight.PHLightAlertMode;

public class AlertConverter extends BaseChannelCommandConverter {

    /**
     * {@value #ALERT_MODE_NONE}. The light is not performing an alert effect.
     */
    static final String ALERT_MODE_NONE = "NONE";
    /**
     * {@value #ALERT_MODE_SELECT}. The light is performing one breathe cycle.
     */
    static final String ALERT_MODE_SELECT = "SELECT";
    /**
     * {@value #ALERT_MODE_LONG_SELECT}. The light is performing breathe cycles
     * for 15 seconds or until an "alert": "none" command is received.
     */
    static final String ALERT_MODE_LONG_SELECT = "LSELECT";

    @Override
    public String getChannelId() {
        return HueBindingConstants.CHANNEL_ALERT;
    }

    @Override
    public LightState convert(Command command) {
        LightState lightState = null;
        if (command instanceof StringType) {
            lightState = toAlertState((StringType) command);
        } else {
            // scheduleAlertStateRestore(command);
        }

        return lightState;
    }

    @Override
    public State convert(LightState lightState) {
        return new StringType(lightState.getAlertMode().toString());
    }

    /**
     * Transforms the given {@link StringType} into a light state containing the {@link AlertMode} to be triggered.
     *
     * @param alertType
     *            {@link StringType} representing the required {@link AlertMode} . <br>
     *            Supported values are:
     *            <ul>
     *            <li>{@value #ALERT_MODE_NONE}.
     *            <li>{@value #ALERT_MODE_SELECT}.
     *            <li>{@value #ALERT_MODE_LONG_SELECT}.
     *            <ul>
     * @return light state containing the {@link AlertMode} or <b><code>null </code></b> if the provided
     *         {@link StringType} represents unsupported mode.
     */
    protected LightState toAlertState(StringType alertType) {
        PHLightAlertMode alertMode = null;

        switch (alertType.toString()) {
            case ALERT_MODE_NONE:
                alertMode = PHLightAlertMode.ALERT_NONE;
                break;
            case ALERT_MODE_SELECT:
                alertMode = PHLightAlertMode.ALERT_SELECT;
                break;
            case ALERT_MODE_LONG_SELECT:
                alertMode = PHLightAlertMode.ALERT_LSELECT;
                break;
            default:
                return null;
        }

        LightState state = new LightState();
        state.setAlertMode(alertMode);
        return state;
    }

    // /**
    // * Schedules restoration of the alert item state to {@link LightStateConverter#ALERT_MODE_NONE} after a given
    // time.
    // * <br>
    // * Based on the initial command:
    // * <ul>
    // * <li>For {@link LightStateConverter#ALERT_MODE_SELECT} restoration will be triggered after <strong>2
    // * seconds</strong>.
    // * <li>For {@link LightStateConverter#ALERT_MODE_LONG_SELECT} restoration will be triggered after <strong>15
    // * seconds</strong>.
    // * </ul>
    // * This method also cancels any previously scheduled restoration.
    // *
    // * @param command
    // * The {@link Command} sent to the item
    // */
    // private void scheduleAlertStateRestore(Command command) {
    // cancelScheduledFuture();
    // int delay = getAlertDuration(command);
    //
    // if (delay > 0) {
    // scheduledFuture = scheduler.schedule(new Runnable() {
    //
    // @Override
    // public void run() {
    // updateState(CHANNEL_ALERT, new StringType(LightStateConverter.ALERT_MODE_NONE));
    // }
    // }, delay, TimeUnit.MILLISECONDS);
    // }
    // }
    //
    // /**
    // * This method will cancel previously scheduled alert item state
    // * restoration.
    // */
    // private void cancelScheduledFuture() {
    // if (scheduledFuture != null) {
    // scheduledFuture.cancel(true);
    // }
    // }
    //
    // /**
    // * This method returns the time in <strong>milliseconds</strong> after
    // * which, the state of the alert item has to be restored to {@link LightStateConverter#ALERT_MODE_NONE}.
    // *
    // * @param command
    // * The initial command sent to the alert item.
    // * @return Based on the initial command will return:
    // * <ul>
    // * <li><strong>2000</strong> for {@link LightStateConverter#ALERT_MODE_SELECT}.
    // * <li><strong>15000</strong> for {@link LightStateConverter#ALERT_MODE_LONG_SELECT}.
    // * <li><strong>-1</strong> for any command different from the previous two.
    // * </ul>
    // */
    // private int getAlertDuration(Command command) {
    // int delay;
    // switch (command.toString()) {
    // case LightStateConverter.ALERT_MODE_LONG_SELECT:
    // delay = 15000;
    // break;
    // case LightStateConverter.ALERT_MODE_SELECT:
    // delay = 2000;
    // break;
    // default:
    // delay = -1;
    // break;
    // }
    //
    // return delay;
    // }
}
