package com.vmartins.cordova.netinterfaces;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import java.net.InetAddress;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.NetworkInterface;
import java.net.SocketException;

import java.util.Enumeration;
import java.util.logging.*;

public class NetInterfaces extends CordovaPlugin {
    public static final String GET_INFO = "getInfo";
    private static final String TAG = "cordova-plugin-netinterfaces";

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        try {
            if (GET_INFO.equals(action)) {
                return getInfo(callbackContext);
            }

            callbackContext.error("Error no such method '" + action + "'");
            return false;
        } catch (Exception e) {
            callbackContext.error("Error while calling ''" + action + "' '" + e.getMessage());
            return false;
        }
    }

    private boolean getInfo(CallbackContext callbackContext) {
        JSONArray interfaces = new JSONArray();

        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = (NetworkInterface) en.nextElement();

                // convert mac to string
                byte[] hardwareAddress = intf.getHardwareAddress();
                StringBuilder mac = new StringBuilder(18);
                if (hardwareAddress != null) {
                    for (byte b: hardwareAddress) {
                        if (mac.length() > 0)
                            mac.append(':');
                        mac.append(String.format("%02x", b));
                    }
                }

                // addresses list
                JSONArray addresses = new JSONArray();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();

                    JSONObject address = new JSONObject();
                    address.put("canonical_host_name", inetAddress.getCanonicalHostName());
                    address.put("host_address", inetAddress.getHostAddress());
                    address.put("host_name", inetAddress.getHostName());
                    address.put("is_ipv4", inetAddress instanceof Inet4Address);
                    address.put("is_ipv6", inetAddress instanceof Inet6Address);
                    address.put("is_any_local_address", inetAddress.isAnyLocalAddress());
                    address.put("is_link_local_address", inetAddress.isLinkLocalAddress());
                    address.put("is_loopback_address", inetAddress.isLoopbackAddress());
                    address.put("is_multicast_global", inetAddress.isMCGlobal());
                    address.put("is_multicast_link_local", inetAddress.isMCLinkLocal());
                    address.put("is_multicast_node_local", inetAddress.isMCNodeLocal());
                    address.put("is_multicast_org_local", inetAddress.isMCOrgLocal());
                    address.put("is_multicast_site_local", inetAddress.isMCSiteLocal());
                    address.put("is_multicast_address", inetAddress.isMulticastAddress());
                    address.put("is_site_local_address", inetAddress.isSiteLocalAddress());
                    address.put("is_site_local_address", inetAddress.isSiteLocalAddress());

                    addresses.put(address);
                }

                JSONObject iface = new JSONObject();
                iface.put("name", intf.getName());
                iface.put("display_name", intf.getDisplayName());
                iface.put("mtu", intf.getMTU());
                iface.put("is_loopback", intf.isLoopback());
                iface.put("is_point_to_point", intf.isPointToPoint());
                iface.put("is_up", intf.isUp());
                iface.put("is_virtual", intf.isVirtual());
                iface.put("supports_multicast", intf.supportsMulticast());
                iface.put("mac", mac.toString());
                iface.put("inet_addresses", addresses);

                interfaces.put(iface);
            }
        } catch (SocketException e) {
            Log.e(TAG, "SocketException in Get Info: " + e.toString());
            return false;
        } catch (JSONException e) {
            Log.e(TAG, "JSONException in Get Info: " + e.toString());
            return false;
        }

        callbackContext.sendPluginResult(new PluginResult(PluginResult.Status.OK, interfaces));
        return true;
    }

}