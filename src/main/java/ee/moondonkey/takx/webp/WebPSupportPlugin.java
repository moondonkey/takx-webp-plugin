package ee.moondonkey.takx.webp;

import gov.takx.api.plugin.annotations.AppPlugin;
import gov.takx.api.plugin.annotations.LibraryDependency;
import gov.takx.api.plugin.annotations.PluginDescriptor;
import gov.takx.api.plugin.annotations.PluginSpecification;
import gov.takx.api.plugin.app.AAppPlugin;
import gov.takx.api.plugin.app.IAppPluginManager.AutoStartMode;
import gov.takx.api.plugin.support.SupportedPlatformVersion;

/**
 * Makes WebP tiles (e.g. WebP MBTiles) readable in TAKX.
 *
 * TAKX decodes map tiles through javax.imageio, which has no WebP reader.
 * This plugin registers the TwelveMonkeys WebP reader in the global ImageIO registry.
 * The registry is shared by the whole process, so class loader isolation does not matter.
 */
@AppPlugin(
        spec = @PluginSpecification(
                family = "ee.moondonkey.webp",
                type = "WebP Imagery Support",
                version = "1.0.0",
                iconPath = "icons/webp.png",
                minimumSupportedVersion = SupportedPlatformVersion.V5_7),
        descriptor = @PluginDescriptor(
                name = "WebP Imagery Support",
                description = "Adds support for WebP imagery tiles (e.g. WebP MBTiles)",
                longDescription = "Registers the TwelveMonkeys WebP ImageIO reader so that map tiles "
                        + "stored as WebP (MBTiles, GeoPackage) are displayed."),
        autoStartMode = AutoStartMode.background)
@LibraryDependency("org.slf4j.slf4j-api")
public class WebPSupportPlugin extends AAppPlugin
{
    static
    {
        // As early as possible: on class initialization, before start().
        WebPRegistration.register();
    }

    @Override
    public void start()
    {
        WebPRegistration.register();
    }

    @Override
    public void stop()
    {
        // Keep the reader registered: already loaded imagery layers still need it.
    }

    @Override
    public void shutdown()
    {
        WebPRegistration.unregister();
    }
}
