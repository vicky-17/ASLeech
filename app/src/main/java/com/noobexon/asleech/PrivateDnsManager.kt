package com.noobexon.asleech

import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.util.Log

object PrivateDnsManager {
    private const val TAG = "PrivateDnsManager"

    // Ordered fallback list — tried in sequence until one succeeds.
    // Primary: family filter (porn + SafeSearch enforcement)
    // Fallbacks: alternate providers in case the primary host is unreachable
    // or rejects the DoT handshake (PRIVATE_DNS_SET_ERROR_HOST_NOT_SERVING).
    val DNS_FALLBACK_CHAIN = listOf(
        "family-filter-dns.cleanbrowsing.org",
        "adult-filter-dns.cleanbrowsing.org",
        "family.dns.adguard.com"
    )

    sealed class DnsResult {
        data class Success(val host: String) : DnsResult()
        object NotDeviceOwner : DnsResult()
        object AllHostsFailed : DnsResult()
    }

    fun isDeviceOwner(context: Context): Boolean {
        val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        return dpm.isDeviceOwnerApp(context.packageName)
    }

    /**
     * Tries each host in DNS_FALLBACK_CHAIN until one is accepted.
     * Must be called off the main thread — the underlying call is blocking.
     * Safe to call even if not Device Owner — it will just report that and do nothing.
     */
    fun applyDnsWithFallback(context: Context): DnsResult {
        if (!isDeviceOwner(context)) {
            Log.w(TAG, "Not device owner — cannot set global private DNS")
            return DnsResult.NotDeviceOwner
        }

        val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val admin = ComponentName(context, ASLeechDeviceAdminReceiver::class.java)

        for (host in DNS_FALLBACK_CHAIN) {
            val result = try {
                dpm.setGlobalPrivateDnsModeSpecifiedHost(admin, host)
            } catch (e: SecurityException) {
                Log.e(TAG, "SecurityException setting DNS to $host", e)
                continue
            }

            when (result) {
                DevicePolicyManager.PRIVATE_DNS_SET_NO_ERROR -> {
                    Log.i(TAG, "Private DNS set successfully: $host")
                    return DnsResult.Success(host)
                }
                DevicePolicyManager.PRIVATE_DNS_SET_ERROR_HOST_NOT_SERVING -> {
                    Log.w(TAG, "$host did not respond to DoT check, trying next fallback")
                }
                DevicePolicyManager.PRIVATE_DNS_SET_ERROR_FAILURE_SETTING -> {
                    Log.w(TAG, "Generic failure setting $host, trying next fallback")
                }
            }
        }

        Log.e(TAG, "All DNS hosts in fallback chain failed")
        return DnsResult.AllHostsFailed
    }

    /** Checks whether the currently active DNS matches what we expect. */
    fun verifyCurrentDns(context: Context): Boolean {
        if (!isDeviceOwner(context)) return false
        val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager
        val admin = ComponentName(context, ASLeechDeviceAdminReceiver::class.java)

        return try {
            val mode = dpm.getGlobalPrivateDnsMode(admin)
            val currentHost = dpm.getGlobalPrivateDnsHost(admin)
            mode == DevicePolicyManager.PRIVATE_DNS_MODE_PROVIDER_HOSTNAME &&
                    currentHost in DNS_FALLBACK_CHAIN
        } catch (e: SecurityException) {
            false
        }
    }
}