package trj.com.xponsedsample;

import android.hardware.Sensor;
import android.util.SparseArray;

import java.lang.reflect.Field;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findClass;

/**
 * @author TRJ
 * @date 2018/3/21
 * Description:
 */
public class WxSportsHook implements IXposedHookLoadPackage {
    private static int stepCount = 1;
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        // filter
        if (!lpparam.packageName.equals("com.tencent.mm")) {
            return;
        }

        final Class<?> sensorEL = findClass("android.hardware.SystemSensorManager$SensorEventQueue", lpparam.classLoader);

        XposedBridge.hookAllMethods(sensorEL, "dispatchSensorEvent", new XC_MethodHook() {

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                ((float[]) param.args[1])[0] = ((float[]) param.args[1])[0] + 1168 * stepCount;
                stepCount++;

                Field field = param.thisObject.getClass().getEnclosingClass().getDeclaredField("sHandleToSensor");
                field.setAccessible(true);

                int handle = (Integer) param.args[0];
                Sensor sensor = ((SparseArray<Sensor>) field.get(0)).get(handle);
                XposedBridge.log("sensor = " + sensor);
            }
        });
    }
}
