package app.xtreamvod.playback

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.net.toUri

object VlcLauncher {
    const val VLC_PACKAGE = "org.videolan.vlc"

    fun isVlcInstalled(context: Context): Boolean {
        return try {
            val pm = context.packageManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pm.getPackageInfo(VLC_PACKAGE, PackageManager.PackageInfoFlags.of(0))
            } else {
                @Suppress("DEPRECATION")
                pm.getPackageInfo(VLC_PACKAGE, 0)
            }
            true
        } catch (_: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun play(context: Context, url: String): Boolean {
        if (!isVlcInstalled(context)) {
            return false
        }
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(url.toUri(), "video/*")
            setPackage(VLC_PACKAGE)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
        return true
    }
}
