package org.eclipse.smarthome.binding.hue.internal;

import com.google.common.base.Objects;
import com.philips.lighting.model.PHLight.PHLightAlertMode;
import com.philips.lighting.model.PHLight.PHLightColorMode;
import com.philips.lighting.model.PHLight.PHLightEffectMode;
import com.philips.lighting.model.PHLightState;

public class LightState {

    private PHLightState phLightState;

    public LightState() {
        this(new PHLightState());
    }

    public LightState(PHLightState phLightState) {
        super();
        this.phLightState = phLightState;
    }

    public PHLightState getPHLightState() {
        return phLightState;
    }

    public void setAlertMode(PHLightAlertMode alertMode) {
        phLightState.setAlertMode(alertMode);
    }

    public PHLightAlertMode getAlertMode() {
        return Objects.firstNonNull(phLightState.getAlertMode(), PHLightAlertMode.ALERT_NONE);
    }

    public void setBrightness(Integer brightness) {
        phLightState.setBrightness(brightness);
    }

    public int getBrightness() {
        return Objects.firstNonNull(phLightState.getBrightness(), 0);
    }

    public void setIncrementBrightness(Integer incrementBrightness) {
        phLightState.setIncrementBri(incrementBrightness);
    }

    public int getIncrementBrightness() {
        return Objects.firstNonNull(phLightState.getIncrementBri(), 0);
    }

    public PHLightColorMode getColorMode() {
        return Objects.firstNonNull(phLightState.getColorMode(), PHLightColorMode.COLORMODE_NONE);
    }

    public void setCt(Integer ct) {
        phLightState.setCt(ct);
    }

    public int getCt() {
        return Objects.firstNonNull(phLightState.getCt(), 0);
    }

    public void setIncrementCt(Integer incrementCt) {
        phLightState.setIncrementCt(incrementCt);
    }

    public int getIncrementCt() {
        return Objects.firstNonNull(phLightState.getIncrementCt(), 0);
    }

    public void setEffectMode(PHLightEffectMode effectMode) {
        phLightState.setEffectMode(effectMode);
    }

    public PHLightEffectMode getEffectMode() {
        return Objects.firstNonNull(phLightState.getEffectMode(), PHLightEffectMode.EFFECT_NONE);
    }

    public void setHue(Integer hue) {
        phLightState.setHue(hue);
    }

    public int getHue() {
        return Objects.firstNonNull(phLightState.getHue(), 0);
    }

    public void setSaturation(Integer saturation) {
        phLightState.setSaturation(saturation);
    }

    public int getSaturation() {
        return Objects.firstNonNull(phLightState.getSaturation(), 0);
    }

    public boolean isReachable() {
        return Objects.firstNonNull(phLightState.isReachable(), false);
    }

    public void setOn(Boolean on) {
        phLightState.setOn(on);
    }

    public boolean isOn() {
        return Objects.firstNonNull(phLightState.isOn(), false);
    }

    public void setTransitionTime(Integer transitionTime) {
        phLightState.setTransitionTime(transitionTime);
    }
}
