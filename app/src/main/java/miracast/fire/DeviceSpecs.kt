package miracast.fire

import android.os.Build
import android.os.Build.VERSION
import android.os.Build.VERSION.SDK


class DeviceSpecs {
    fun getModel(): String? {
        return Build.MODEL
    }

    fun getProductName(): String? {
        return Build.PRODUCT
    }

    fun getOSVersion(): String? {
        return VERSION.RELEASE
    }

    fun getSDKVersion(): String? {
        return SDK
    }

    fun getVersionCodename(): String? {
        return VERSION.CODENAME
    }

    fun getVersionSDKINT(): Int {
        return VERSION.SDK_INT
    }
}