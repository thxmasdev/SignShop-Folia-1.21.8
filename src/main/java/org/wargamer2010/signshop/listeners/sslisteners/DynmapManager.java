
package org.wargamer2010.signshop.listeners.sslisteners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.wargamer2010.signshop.Seller;
import org.wargamer2010.signshop.SignShop;
import org.wargamer2010.signshop.configuration.Storage;
import org.wargamer2010.signshop.events.SSCreatedEvent;
import org.wargamer2010.signshop.events.SSDestroyedEvent;
import org.wargamer2010.signshop.events.SSDestroyedEventType;
import org.wargamer2010.signshop.util.signshopUtil;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.logging.Level;

public class DynmapManager implements Listener {
    private Object dynmapAPI = null;
    private Object markerAPI = null;
    private Object ms = null;
    private Object mi = null;

    private final static String MarkerSetName = "SignShopMarkers";
    private final static String MarkerSetLabel = "SignShop Marker Set";
    private final static String Filename = "signshopsign.png";
    private final static String MarkerName = "signshop_icon_555";
    private final static String MarkerLabel = "SignShop";

    public DynmapManager() {
        init();
    }

    private boolean safelyCheckInit() {
        try {
            if (dynmapAPI == null) return false;
            Method m = dynmapAPI.getClass().getMethod("markerAPIInitialized");
            Object res = m.invoke(dynmapAPI);
            return res instanceof Boolean && (Boolean) res;
        } catch (Throwable ex) {
            return false;
        }
    }

    private void init() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("dynmap");
        if(plugin == null)
            return;

        dynmapAPI = plugin;
        if (!SignShop.getInstance().getSignShopConfig().getEnableDynmapSupport()) {
            if (safelyCheckInit()) {
                try {
                    Method getMarkerAPI = dynmapAPI.getClass().getMethod("getMarkerAPI");
                    Object api = getMarkerAPI.invoke(dynmapAPI);
                    Method getMarkerSet = api.getClass().getMethod("getMarkerSet", String.class);
                    Object temp = getMarkerSet.invoke(api, MarkerSetName);
                    if (temp != null) {
                        Method deleteMarkerSet = temp.getClass().getMethod("deleteMarkerSet");
                        deleteMarkerSet.invoke(temp);
                    }
                } catch (Throwable ignored) {
                }
            }
            return;
        }

        if(!safelyCheckInit()) {
            SignShop.log("MarkerAPI for Dynmap has not been initialised, please check dynmap's configuration.", Level.WARNING);
            return;
        }

        try {
            Method getMarkerAPI = dynmapAPI.getClass().getMethod("getMarkerAPI");
            markerAPI = getMarkerAPI.invoke(dynmapAPI);
            Method getMarkerSet = markerAPI.getClass().getMethod("getMarkerSet", String.class);
            ms = getMarkerSet.invoke(markerAPI, MarkerSetName);
            if (ms == null) {
                Method createMarkerSet = markerAPI.getClass().getMethod("createMarkerSet", String.class, String.class, Object.class, boolean.class);
                ms = createMarkerSet.invoke(markerAPI, MarkerSetName, MarkerSetLabel, null, false);
            }
        } catch (Throwable ignored) {
        }
        if(ms == null) {
            SignShop.log("Could not create MarkerSet for Dynmap.", Level.WARNING);
            return;
        }

        try {
            Method getMarkerIcon = markerAPI.getClass().getMethod("getMarkerIcon", String.class);
            Object existing = getMarkerIcon.invoke(markerAPI, MarkerName);
            if (existing == null) {
                InputStream in = getClass().getResourceAsStream("/" + Filename);
                if(in != null && in.available() > 0) {
                    Method createMarkerIcon = markerAPI.getClass().getMethod("createMarkerIcon", String.class, String.class, InputStream.class);
                    mi = createMarkerIcon.invoke(markerAPI, MarkerName, MarkerLabel, in);
                }
            } else {
                mi = existing;
            }
        } catch (IOException ignored) {
        } catch (Throwable ignored) {
        }

        if(mi == null) {
            try {
                Method getMarkerIcon = markerAPI.getClass().getMethod("getMarkerIcon", String.class);
                mi = getMarkerIcon.invoke(markerAPI, "sign");
            } catch (Throwable ignored) {
            }
        }

        for(Seller seller : Storage.get().getSellers()) {
            ManageMarkerForSeller(seller, false);
        }
    }

    private void ManageMarkerForSeller(Seller seller, boolean remove) {
        ManageMarkerForSeller(seller.getSignLocation(), seller.getOwner().getName(), seller.getWorld(), remove);
    }

    private void ManageMarkerForSeller(Location loc, String owner, String world, boolean remove) {
        if(ms == null)
            return;

        String id = ("SignShop_" + signshopUtil.convertLocationToString(loc).replace(".", ""));
        String label = (owner + "'s SignShop");

        try {
            Method findMarker = ms.getClass().getMethod("findMarker", String.class);
            Object m = findMarker.invoke(ms, id);
            if(remove) {
                if(m != null) {
                    Method deleteMarker = m.getClass().getMethod("deleteMarker");
                    deleteMarker.invoke(m);
                }
                return;
            }

            if(m == null) {
                Method createMarker = ms.getClass().getMethod("createMarker", String.class, String.class, String.class, double.class, double.class, double.class, mi.getClass(), boolean.class);
                createMarker.invoke(ms, id, label, world, loc.getX(), loc.getY(), loc.getZ(), mi, false);
            } else {
                Method setLocation = m.getClass().getMethod("setLocation", String.class, double.class, double.class, double.class);
                setLocation.invoke(m, world, loc.getX(), loc.getY(), loc.getZ());
                Method setLabel = m.getClass().getMethod("setLabel", String.class);
                setLabel.invoke(m, label);
                Method setMarkerIcon = m.getClass().getMethod("setMarkerIcon", mi.getClass());
                setMarkerIcon.invoke(m, mi);
            }
        } catch (Throwable ignored) {
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSSDestroyCleanup(SSDestroyedEvent event) {
        if(event.isCancelled() || event.getReason() != SSDestroyedEventType.sign)
            return;

        ManageMarkerForSeller(event.getShop(), true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onSSBuildEvent(SSCreatedEvent event) {
        if(event.isCancelled())
            return;

        ManageMarkerForSeller(event.getSign().getLocation(), event.getPlayer().getName(), event.getPlayer().getWorld().getName(), false);
    }
}
