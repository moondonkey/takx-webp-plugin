package ee.moondonkey.takx.webp;

import com.twelvemonkeys.imageio.plugins.webp.WebPImageReaderSpi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;

final class WebPRegistration
{
    private static final Logger LOG = LoggerFactory.getLogger(WebPRegistration.class);

    private static ImageReaderSpi spi;

    private WebPRegistration()
    {
    }

    static synchronized void register()
    {
        if (spi != null)
        {
            return;
        }
        if (ImageIO.getImageReadersByMIMEType("image/webp").hasNext())
        {
            LOG.info("A WebP ImageIO reader is already present, skipping registration");
            return;
        }
        spi = new WebPImageReaderSpi();
        IIORegistry.getDefaultInstance().registerServiceProvider(spi, ImageReaderSpi.class);
        LOG.info("WebP ImageIO reader registered ({})", spi.getVendorName() + " " + spi.getVersion());
    }

    static synchronized void unregister()
    {
        if (spi == null)
        {
            return;
        }
        IIORegistry.getDefaultInstance().deregisterServiceProvider(spi, ImageReaderSpi.class);
        spi = null;
    }
}
