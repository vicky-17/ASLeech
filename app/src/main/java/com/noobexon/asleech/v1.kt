//package com.noobexon.asleech
//
//import android.accessibilityservice.AccessibilityService
//import android.view.accessibility.AccessibilityEvent
//
//class v1 : AccessibilityService() {
//
//    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
//
//        // Stop if event is null
//        event ?: return
//
//        // Get current screen root
//        val root = rootInActiveWindow ?: return
//
//        // Blocks Control centre
//        if (root.findAccessibilityNodeInfosByText("Control centre").isNotEmpty()) {
//            performGlobalAction(GLOBAL_ACTION_BACK)
//        }
//
//        // Blocks Power odd and restart
//        if (event?.packageName == "miui.systemui.plugin") {
//            performGlobalAction(GLOBAL_ACTION_BACK)
//        }
//    }
//
//    override fun onInterrupt() {}
//}