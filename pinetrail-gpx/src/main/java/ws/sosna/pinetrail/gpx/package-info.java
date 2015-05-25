/**
 * Provides thread-safe readers that extract information from GPX data files.
 *
 * <p>
 * {@code Gpx11ReaderProvider} acts as a service provider, as defined in Java
 * SPI, and will be automatically registered with the simple service-provider
 * loading facility offered by the {@code Readers} class.
 *
 * @author Xavier Sosnovsky
 *
 * @see ws.sosna.pinetrail.api.io.Readers
 * @see java.util.ServiceLoader
 */
package ws.sosna.pinetrail.gpx;
