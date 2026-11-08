package ee.moondonkey.takx.webp;

import gov.takx.api.plugin.annotations.AppPlugin;
import gov.takx.api.plugin.annotations.LibraryDependency;
import gov.takx.api.plugin.annotations.PluginDescriptor;
import gov.takx.api.plugin.annotations.PluginSpecification;
import gov.takx.api.plugin.app.AAppPlugin;
import gov.takx.api.plugin.app.IAppPluginManager.AutoStartMode;
import gov.takx.api.plugin.support.SupportedPlatformVersion;

/**
 * Teeb WebP-plaadid (nt WebP MBTiles) TAKX-is loetavaks.
 *
 * TAKX dekodeerib kaardiplaadid javax.imageio kaudu, millel WebP lugejat pole.
 * Plugin registreerib TwelveMonkeys WebP lugeja ImageIO globaalsesse registrisse.
 * Register on kogu protsessi peale ühine, seega klassilaadurite eraldus ei sega.
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
                description = "Lisab WebP-plaatide (nt WebP MBTiles) toe",
                longDescription = "Registreerib TwelveMonkeys WebP ImageIO lugeja, et WebP-vormingus "
                        + "kaardiplaadid (MBTiles, GeoPackage) kuvataks."),
        autoStartMode = AutoStartMode.background)
@LibraryDependency("org.slf4j.slf4j-api")
public class WebPSupportPlugin extends AAppPlugin
{
    static
    {
        // Võimalikult vara: klassi initsialiseerimisel, enne start()-i.
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
        // Lugeja jääb registreerituks: juba laaditud kaardikihid vajavad seda edasi.
    }

    @Override
    public void shutdown()
    {
        WebPRegistration.unregister();
    }
}
